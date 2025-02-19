package github

import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption

open class GitHubReleaseFetcher protected constructor(val release_uri: URI) {
    @Throws(IOException::class, URISyntaxException::class)
    open fun fetch_binary(download_url: URL): File {
        println("Downloading Binary from: $download_url")
        val tempFile = getTemporaryFile(download_url)
        val connection = getHttpURLConnection(download_url)
        copy_filestream(connection, tempFile)
        set_exe(tempFile)
        return tempFile
    }

    companion object {
        @Throws(IOException::class)
        private fun set_exe(tempFile: File) {
            if (!tempFile.setExecutable(true)) {
                throw IOException("Failed to set executable permissions on the downloaded binary.")
            }
        }

        @Throws(IOException::class)
        private fun copy_filestream(connection: HttpURLConnection, tempFile: File) {
            connection.inputStream.use { inputStream ->
                Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
        }

        @Throws(IOException::class)
        fun getTemporaryFile(download_url: URL): File {
            val url_tail = download_url.path.substring(download_url.path.lastIndexOf('/') + 1)
            val tempFile = File.createTempFile(url_tail, "")
            tempFile.deleteOnExit()
            return tempFile
        }

        @Throws(IOException::class)
        private fun getHttpURLConnection(connection_url: URL): HttpURLConnection {
            val connection = connection_url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            if (connection.responseCode != 200) {
                throw IOException("Failed to connect... " + connection.responseMessage)
            }
            return connection
        }
    }
}
