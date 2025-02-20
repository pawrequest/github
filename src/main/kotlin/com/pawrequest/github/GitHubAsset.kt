package com.pawrequest.github

import java.net.URL

class GitHubAsset(
    var url: String,
    var id: Long,
    var nodeId: String,
    var name: String,
    var label: String,
    var contentType: String,
    var state: String,
    var size: Long,
    var downloadCount: Int,
    var createdAt: String,
    var updatedAt: String,
    var browserDownloadUrl: URL
)
