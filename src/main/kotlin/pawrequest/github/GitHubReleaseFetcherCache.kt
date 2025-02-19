package github

import java.io.File
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class GitHubReleaseFetcherCache(val cache_dir: Path, releaseURI: URI) : GitHubReleaseFetcher(releaseURI) {
    fun cachedBinary(assetName: String): File {
        return Paths.get(cache_dir.toString(), assetName).toFile()
    }

    @Throws(IOException::class, URISyntaxException::class)
    override fun fetch_binary(download_url: URL): File {
        println("Fetching Binary: $download_url")

        val assetNames = listOf<String>(*download_url.path.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
        val assetName = assetNames[assetNames.size - 1]
        println("Asset Name: $assetName")

        val cachedBinary = cachedBinary(assetName)
        if (cachedBinary.exists()) {
            println("Cached Binary Exists: $cachedBinary")
            return cachedBinary
        }
        println("Cached Binary Does Not Exist: $cachedBinary")
        val binaryFile = super.fetch_binary(download_url)
        println("Caching Binary to: $cachedBinary")
        Files.createDirectories(cache_dir)
        Files.copy(binaryFile.toPath(), cachedBinary.toPath(), StandardCopyOption.REPLACE_EXISTING)

        return cachedBinary
    }
}