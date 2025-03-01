package com.pawrequest.github

import java.net.URI
@Suppress("unused")
open class GitHubRepo(
    open val name: String,
    open val user: GitHubUser,
) {
    val repoUri = URI.create("https://github.com/${user.name}/${name}/")
    val releasesUri: URI = repoUri.resolve("releases/")
    val apiUri = URI.create("https://api.github.com/repos/${user.name}/${name}/")
    val latestReleaseApiUri = apiUri.resolve("releases/latest/")

    fun assetDownloadUri(version: String, assetName: String): URI {
        println("USER: ${user.name}")
        println("REPO: $name")
        println("RepoURI: $repoUri")
        println("ReleaseURI: $releasesUri")
        println("Asset Download URI: $version $assetName")
        return repoUri.resolve("releases/download/$version/$assetName")
    }

    fun latestRelease(): GitHubRelease {
        return GitHubRelease.fromURL(latestReleaseApiUri.toURL())
    }


    companion object {
        fun fromUserNameAndRepoName(userName: String, repoName: String): GitHubRepo {
            return GitHubRepo(repoName, GitHubUser(userName))
        }
    }
}
