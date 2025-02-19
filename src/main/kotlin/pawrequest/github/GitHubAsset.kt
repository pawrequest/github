package github

import java.net.URL

class GitHubAsset(
    var url: String,
    var id: Long,
    var node_id: String,
    var name: String,
    var label: String,
    var content_type: String,
    var state: String,
    var size: Long,
    var download_count: Int,
    var created_at: String,
    var updated_at: String,
    var browser_download_url: URL
)
