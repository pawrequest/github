package com.pawrequest.github

import java.net.URI
@Suppress("unused")
open class GitHubRepo(
    open var name: String,
    open var user: GitHubUser,
) {
    var repoUri = URI.create("https://github.com/${user.name}/${name}")
    var apiUri = URI.create("https://api.github.com/repos/${user.name}/${name}")
    var latestReleaseApiUri = apiUri.resolve("releases/latest")
    var releasesUri: URI = repoUri.resolve("releases/")

    fun assetDownloadUri(version: String, assetName: String): URI {
        return repoUri.resolve("download/$version/$assetName")
    }

//    fun latestRelease(): GitHubRelease {
//        return GitHubRelease.fromURL(latestReleaseApiUri.toURL())
//    }


    companion object {
        fun fromUserNameAndRepoName(userName: String, repoName: String): GitHubRepo {
            return GitHubRepo(repoName, GitHubUser(userName))
        }
    }
}
