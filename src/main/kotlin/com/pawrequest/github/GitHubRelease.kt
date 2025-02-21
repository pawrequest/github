//package com.pawrequest.github
//
//import com.google.gson.Gson
//import java.io.BufferedReader
//import java.io.IOException
//import java.io.InputStreamReader
//import java.net.HttpURLConnection
//import java.net.URI
//import java.net.URL
//import java.util.*
//
//@Suppress("unused")
//open class GitHubRelease(
//    var tagName: String,
//    var assets: List<GitHubAsset>,
//    var url: URL, var assetsUrl: URL, var htmlUrl: URL, var uploadUrl: URL,
//    var id: Int,
//    var nodeId: String,
//    var createdAt: Date, var publishedAt: Date
//) {
//    companion object {
//
//        fun fromUserNameAndRepoName(userName: String, repoName: String): GitHubRelease {
//            val uri = URI.create("https://api.github.com/repos/${userName}/${repoName}/releases/latest")
//            return fromURL(uri.toURL())
//
//        }
//        @Throws(IOException::class)
//        fun fromURL(releaseUrl: URL): GitHubRelease {
//            println("Fetching Release from: $releaseUrl")
//            val connection = releaseUrl.openConnection() as HttpURLConnection
//            connection.requestMethod = "GET"
//            connection.setRequestProperty("Accept", "application/json")
//
//            if (connection.responseCode != 200) {
//                val retry = connection.getHeaderField("retry-after")
//                if (retry != null) {
//                    println("Rate Limited. Retry after: $retry")
//                }
//                val x = "Failed to fetch latest release from " + releaseUrl + " msg =" + connection.responseMessage
//                println(x)
//                throw IOException(x)
//            }
//
//            BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
//                val response = StringBuilder()
//                var line: String?
//                while ((reader.readLine().also { line = it }) != null) {
//                    response.append(line)
//                }
//                return Gson().fromJson(response.toString(), GitHubRelease::class.java)
//            }
//        }
//    }
//
//}
