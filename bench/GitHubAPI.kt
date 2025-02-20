//package com.pawrequest.github
//
//import com.jetbrains.rd.util.URI
//
//@Suppress("unused")
//fun getRelease(releaseURI: URI): GitHubRelease {
//    return GitHubRelease.fromURL(releaseURI.toURL())
//}
//
//@Suppress("unused")
//fun getRepoLatestRelease(repoURI: URI): GitHubRelease {
//    return GitHubRelease.fromURL(repoURI.resolve("releases/latest").toURL())
//}
//
//fun getRepoReleaseTag(repoApiUri: URI, tagName: String): GitHubRelease {
//    return GitHubRelease.fromURL(repoApiUri.resolve("releases/tag/$tagName").toURL())
//}