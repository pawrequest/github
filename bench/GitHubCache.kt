//package com.pawrequest.github
//
//import java.io.File
//import java.net.URL
//import java.nio.file.Files
//import java.nio.file.Path
//import java.nio.file.Paths
//import java.nio.file.StandardCopyOption
//
//fun cachedOrNull(cacheDir: Path, assetName: String): File? {
//    val cachedAsset = Paths.get(cacheDir.toString(), assetName).toFile()
//    return if (cachedAsset.exists()) cachedAsset else null
//}
//
//fun cachedOrDownload(cacheDir: Path, assetName: String, downloadUrl: URL): File {
//    val cachedAsset = cachedOrNull(cacheDir, assetName)
//    return if (cachedAsset != null) {
//        println("Cached Asset Exists: $cachedAsset")
//        cachedAsset
//    } else {
//
//        println("Cached Asset Does Not Exist: $cachedAsset")
//        var fetcher = GitHubReleaseFetcher()
//        val binaryFile = fetcher.fetchBinary(downloadUrl)
//        println("Caching Asset to: $cachedAsset")
//        Files.createDirectories(cacheDir)
//        Files.copy(binaryFile.toPath(), cachedAsset.toPath(), StandardCopyOption.REPLACE_EXISTING)
//
//        return cachedAsset
//    }
//
//}