package com.pawrequest.github

import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption


fun downloadOrCache(cacheDir: Path, downloadUrl: URL): File {
    val cachedAsset = cachedOrNull(cacheDir, urlTail(downloadUrl))
    return if (cachedAsset != null) {
        println("Cached Asset Exists: $cachedAsset")
        cachedAsset
    } else {
        val cacheTarget = Paths.get(cacheDir.toString(), urlTail(downloadUrl))

        println("Cache miss, fetching from $downloadUrl to $cacheTarget")
        val binaryFile = downloadBinaryNoCache(downloadUrl)
        Files.createDirectories(cacheDir)
        Files.copy(binaryFile.toPath(), cacheTarget, StandardCopyOption.REPLACE_EXISTING)
        return binaryFile
    }
}

fun downloadBinaryNoCache(downloadUrl: URL): File {
    println("Downloading Binary from: $downloadUrl")
    val tempFile = getTemporaryFile(downloadUrl)
    val connection = getHttpURLConnection(downloadUrl)
    copyFilestream(connection, tempFile)
    setExe(tempFile)
    return tempFile
}


@Throws(IOException::class)
private fun setExe(tempFile: File) {
    if (!tempFile.setExecutable(true)) {
        throw IOException("Failed to set executable permissions on the downloaded binary.")
    }
}

@Throws(IOException::class)
private fun copyFilestream(connection: HttpURLConnection, tempFile: File) {
    connection.inputStream.use { inputStream ->
        Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
    }
}

@Throws(IOException::class)
fun getTemporaryFile(downloadUrl: URL): File {
    val urlTail = urlTail(downloadUrl)
    val tempFile = File.createTempFile(urlTail, "")
    tempFile.deleteOnExit()
    return tempFile
}

private fun urlTail(downloadUrl: URL): String {
    val urlTail = downloadUrl.path.substring(downloadUrl.path.lastIndexOf('/') + 1)
    return urlTail
}

@Throws(IOException::class)
private fun getHttpURLConnection(connectionUrl: URL): HttpURLConnection {
    val connection = connectionUrl.openConnection() as HttpURLConnection
    connection.requestMethod = "GET"

    if (connection.responseCode != 200) {
        throw IOException("Failed to connect... " + connection.responseMessage)
    }
    return connection
}

fun cachedOrNull(cacheDir: Path, fileName: String): File? {
    val cachedAsset = Paths.get(cacheDir.toString(), fileName).toFile()
    return if (cachedAsset.exists()) cachedAsset else null
}

