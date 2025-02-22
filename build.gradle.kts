import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import java.net.URI

plugins {
    `maven-publish`
    id("java") // Java support
    alias(libs.plugins.kotlin) // IntelliJ Platform Gradle Plugin
    alias(libs.plugins.intelliJPlatform) // IntelliJ Platform Gradle Plugin
    alias(libs.plugins.changelog) // Gradle Changelog Plugin
    alias(libs.plugins.qodana) // Gradle Qodana Plugin
    alias(libs.plugins.kover) // Gradle Kover Plugin
}


group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()
val vendorName = providers.gradleProperty("pluginGroup").get().substringAfter("com.")
//val thisRepo = GithubRepoUrl(providers.gradleProperty("artifactID").get())
val thisPackageUri = GithubPackageUri(providers.gradleProperty("artifactID").get())
val customDependenciesProps = providers.gradleProperty("customDependencies").get().split(",")

//val pawtestPackageUri = URI.create("https://maven.pkg.github.com/${providers.gradleProperty("vendorName").get()}/${providers.gradleProperty("artifactID").get()}")

//val pawtestPackageUri = URI.create("https://maven.pkg.github.com/")
//val pawtestPackageUri = URI.create("https://maven.pkg.github.com/pawrequest/pawtest")
//val pawtestPackageUri = GithubPackageUri("pawtest", vendorName)

fun MavenGitHubPackage(repositoryHandler: RepositoryHandler, uri: URI) {
    repositoryHandler.maven {
        url = uri
        name = "GitHubPackages"

        credentials {
            username = System.getenv("GITHUB_USERNAME")
            password = System.getenv("PUBLISH_TOKEN")
        }
    }
}

fun MakeMavenRepo(publicationContainer: PublicationContainer) {
    publicationContainer.create<MavenPublication>("mavenJava") {
        from(components["java"])
        groupId = providers.gradleProperty("pluginGroup").get()
        artifactId = providers.gradleProperty("artifactID").get()
        version = providers.gradleProperty("pluginVersion").get()
    }
}


fun GithubRepoUrl(artifactID: String, vendor: String = vendorName): URI {
    return URI.create("https://github.com/$vendor/$artifactID")
}

fun GithubPackageUri(artifactID: String, vendor: String = vendorName): URI {
    return URI.create("https://maven.pkg.github.com/$vendor/$artifactID")
}


fun getCustomDependencies(): List<String> {
    val imps = mutableListOf<String>()
    for (dep in customDependenciesProps) {
        val depVals = dep.split(" ")
        val imp = "${depVals[2]}:${depVals[1]}:${depVals[3]}"
        imps.add(imp)
    }
    return imps
}

//fun configureCustomRepos() {
//    for (dep in customDependenciesProps) {
//        val depVals = dep.split(" ")
//        val repo = GithubPackageUri(depVals[1], depVals[0])
//        MavenGitHubPackage(this, repo)
//    }
//}

fun getCustomRepos(): List<URI> {
    val repos = mutableListOf<URI>()
    for (dep in customDependenciesProps) {
        val depVals = dep.split(" ")
        val repo = GithubPackageUri(depVals[1], depVals[0])
        repos.add(repo)
    }
    return repos
}


// Set the JVM language level used to build the project.
kotlin {
    jvmToolchain(21)
}

// Configure project's dependencies
repositories {
    mavenCentral()
    getCustomRepos().forEach { repo ->
        MavenGitHubPackage(this, repo)
    }
    intellijPlatform {
        defaultRepositories()
    }
}


//// Dependencies are managed with Gradle version catalog - read more: https://docs.gradle.org/current/userguide/platforms.html#sub:version-catalog
dependencies {
    testImplementation(libs.junit)

//    implementation("com.pawrequest:pawtest:0.0.1")

    // Add custom dependencies dynamically
    getCustomDependencies().forEach { dep ->
        implementation(dep)
    }


    // IntelliJ Platform Gradle Plugin Dependencies Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html
    intellijPlatform {
        create(providers.gradleProperty("platformType"), providers.gradleProperty("platformVersion"))

        // Plugin Dependencies. Uses `platformBundledPlugins` property from the gradle.properties file for bundled IntelliJ Platform plugins.
        bundledPlugins(providers.gradleProperty("platformBundledPlugins").map { it.split(',') })

        // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file for plugin from JetBrains Marketplace.
        plugins(providers.gradleProperty("platformPlugins").map { it.split(',') })

//        instrumentationTools()
        pluginVerifier()
        zipSigner()
        testFramework(TestFrameworkType.Platform)
    }
}




publishing {
    repositories {
        MavenGitHubPackage(this, thisPackageUri)
    }
    publications {
        MakeMavenRepo(this)
    }
}


// Configure IntelliJ Platform Gradle Plugin - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-extension.html
intellijPlatform {
    pluginConfiguration {

        id = providers.gradleProperty("pluginID").get()
        name = providers.gradleProperty("pluginName")
        version = providers.gradleProperty("pluginVersion")

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        description = providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"

            with(it.lines()) {
                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
            }
        }


        ideaVersion {
            sinceBuild = providers.gradleProperty("pluginSinceBuild")
            untilBuild = providers.gradleProperty("pluginUntilBuild")
        }
    }

    pluginVerification {
        ides {
            recommended()
        }
    }
}


