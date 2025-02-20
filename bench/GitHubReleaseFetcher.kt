package com.pawrequest.github
//
//import java.io.File
//import java.io.IOException
//import java.net.HttpURLConnection
//import java.net.URISyntaxException
//import java.net.URL
//import java.nio.file.Files
//import java.nio.file.StandardCopyOption
//
//open class GitHubReleaseFetcher {
//    @Throws(IOException::class, URISyntaxException::class)
//    open fun fetchBinary(downloadUrl: URL): File {
//        println("Downloading Binary from: $downloadUrl")
//        val tempFile = getTemporaryFile(downloadUrl)
//        val connection = getHttpURLConnection(downloadUrl)
//        copyFilestream(connection, tempFile)
//        setExe(tempFile)
//        return tempFile
//    }
//
//    companion object {
//        @Throws(IOException::class)
//        private fun setExe(tempFile: File) {
//            if (!tempFile.setExecutable(true)) {
//                throw IOException("Failed to set executable permissions on the downloaded binary.")
//            }
//        }
//
//        @Throws(IOException::class)
//        private fun copyFilestream(connection: HttpURLConnection, tempFile: File) {
//            connection.inputStream.use { inputStream ->
//                Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
//            }
//        }
//
//        @Throws(IOException::class)
//        fun getTemporaryFile(downloadUrl: URL): File {
//            val urlTail = downloadUrl.path.substring(downloadUrl.path.lastIndexOf('/') + 1)
//            val tempFile = File.createTempFile(urlTail, "")
//            tempFile.deleteOnExit()
//            return tempFile
//        }
//
//        @Throws(IOException::class)
//        private fun getHttpURLConnection(connectionUrl: URL): HttpURLConnection {
//            val connection = connectionUrl.openConnection() as HttpURLConnection
//            connection.requestMethod = "GET"
//
//            if (connection.responseCode != 200) {
//                throw IOException("Failed to connect... " + connection.responseMessage)
//            }
//            return connection
//        }
//    }
//}
