import java.net.URI

plugins {
    `maven-publish`
    id("java") // Java support
    alias(libs.plugins.kotlin) // IntelliJ Platform Gradle Plugin
//    alias(libs.plugins.intelliJPlatform) // IntelliJ Platform Gradle Plugin
//    alias(libs.plugins.changelog) // Gradle Changelog Plugin
//    alias(libs.plugins.qodana) // Gradle Qodana Plugin
    alias(libs.plugins.kover) // Gradle Kover Plugin
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21)) // Match the Java version of your main project
    }
}
kotlin {
    jvmToolchain(21)
}
group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

// pawrequest custom github repo/dependency adder ASSUMES HTTPS://REPO_URL/.../VENDOR/ASSET
val thisArtifactID = providers.gradleProperty("pluginRepositoryUrl").get().substringAfterLast("/")
val thisVendorName = providers.gradleProperty("pluginRepositoryUrl").get().substringBeforeLast("/").substringAfterLast("/")
val theseCustomDependencies = providers.gradleProperty("customDependencies")
    .orNull // Returns null if the property is missing
    ?.split(",") // Split only if the property is present
    ?.filter { it.isNotBlank() } // Filter out empty strings
    ?: emptyList() // Provide an empty list if the property is missing

fun githubPackageUri(vendor: String = thisVendorName, artifactID: String = thisArtifactID): URI {
    return URI.create("https://maven.pkg.github.com/$vendor/$artifactID")
}

fun addRepoUri(repositoryHandler: RepositoryHandler, uri: URI) {
    repositoryHandler.maven {
        url = uri
        name = "GitHubPackages"

        credentials {
            username = System.getenv("USERNAME_GITHUB")
            password = System.getenv("PUBLISH_TOKEN")
        }
    }
}


fun addCustomRepos(repositoryHandler: RepositoryHandler) {
    println("Custom Repos: $theseCustomDependencies")
    for (dep in theseCustomDependencies) {
        println("dep: $theseCustomDependencies")

        val depVals = dep.split(" ")
        val repoUri = githubPackageUri(depVals[0], depVals[1])
        addRepoUri(repositoryHandler, repoUri)
    }
}



publishing {
    repositories {
        addRepoUri(this, githubPackageUri())
    }
    publications {
        addPublication(this)
    }
}


fun addCustomDependencies(dependencyHandler: DependencyHandler) {
    println("Custom Dependencies: $theseCustomDependencies")

    for (dep in theseCustomDependencies) {
        println("dep: $dep")

        val depVals = dep.split(" ")
        val imp = "${depVals[2]}:${depVals[1]}:${depVals[3]}"
        dependencyHandler.implementation(imp)
    }
}


fun addPublication(publicationContainer: PublicationContainer) {
    publicationContainer.create<MavenPublication>("mavenJava") {
        groupId = providers.gradleProperty("pluginGroup").get()
        artifactId = "github"
        version = providers.gradleProperty("pluginVersion").get()

        // Explicitly include the JAR artifact
        artifact(tasks.jar.get().archiveFile) {
            classifier = ""
            extension = "jar"
        }
    }
}

// Configure project's dependencies
repositories {
    mavenCentral()
    addCustomRepos(this)
}

// Dependencies are managed with Gradle version catalog - read more: https://docs.gradle.org/current/userguide/platforms.html#sub:version-catalog
dependencies {
    implementation("com.google.code.gson:gson:2.10.1") // Add Gson dependency
    addCustomDependencies(this)
}


// Configure Gradle Changelog Plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
//changelog {
//    groups.empty()
//    repositoryUrl = providers.gradleProperty("pluginRepositoryUrl")
//}

// Configure Gradle Kover Plugin - read more: https://github.com/Kotlin/kotlinx-kover#configuration
kover {
    reports {
        total {
            xml {
                onCheck = true
            }
        }
    }
}
tasks {
    wrapper {
        gradleVersion = providers.gradleProperty("gradleVersion").get()
    }
}

