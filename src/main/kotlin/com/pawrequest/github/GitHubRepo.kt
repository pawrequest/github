package com.pawrequest.github

import java.net.URI
@Suppress("unused")
open class GitHubRepo(
    open val name: String,
    open val user: GitHubUser,
) {
    val repoUri = URI.create("https://github.com/${user.name}/${name}")
    val apiUri = URI.create("https://api.github.com/repos/${user.name}/${name}")
    val latestReleaseApiUri = apiUri.resolve("releases/latest")
    val releasesUri: URI = repoUri.resolve("releases/")

    fun assetDownloadUri(version: String, assetName: String): URI {
        return repoUri.resolve("download/$version/$assetName")
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
