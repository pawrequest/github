package github

import com.google.gson.Gson
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class GitHubRelease(
    var tag_name: String,
    var assets: List<GitHubAsset>,
    var url: URL, var assets_url: URL, var html_url: URL, var upload_url: URL,
    var id: Int,
    var node_id: String,
    var created_at: Date, var published_at: Date
) {
    companion object {
        @Throws(IOException::class)
        fun fromURL(release_url: URL): GitHubRelease {
            println("Fetching Release from: $release_url")
            val connection = release_url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept", "application/json")

            if (connection.responseCode != 200) {
                val retry = connection.getHeaderField("retry-after")
                if (retry != null) {
                    println("Rate Limited. Retry after: $retry")
                }
                val x = "Failed to fetch latest release from " + release_url + " msg =" + connection.responseMessage
                println(x)
                throw IOException(x)
            }

            BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                val response = StringBuilder()
                var line: String?
                while ((reader.readLine().also { line = it }) != null) {
                    response.append(line)
                }
                return Gson().fromJson(response.toString(), GitHubRelease::class.java)
            }
        }
    }
}
