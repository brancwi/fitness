package com.muscu.app.data.remote

import com.google.gson.annotations.SerializedName

/**
 * Réponse de l'API GitHub Releases pour le endpoint /repos/{owner}/{repo}/releases/latest
 */
data class GitHubRelease(
    @SerializedName("tag_name") val tagName: String,
    val name: String,
    val body: String?,
    val assets: List<Asset>
) {
    data class Asset(
        @SerializedName("browser_download_url") val downloadUrl: String,
        val name: String
    )

    /**
     * URL de téléchargement du premier asset APK trouvé, ou null.
     */
    fun apkDownloadUrl(): String? {
        return assets.firstOrNull { it.name.endsWith(".apk", ignoreCase = true) }?.downloadUrl
    }
}
