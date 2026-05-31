package com.muscu.app.data.updater

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.muscu.app.data.remote.GitHubApiService
import com.muscu.app.data.remote.GitHubRelease
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

object AppUpdater {

    private const val TAG = "AppUpdater"

    private val api = GitHubApiService.create()
    private val client = OkHttpClient()

    /**
     * Vérifie si une release plus récente existe sur GitHub.
     * @param currentVersion version actuelle de l'app (ex: "1.0")
     * @return la [GitHubRelease] si une mise à jour est disponible, null sinon
     */
    suspend fun checkForUpdate(currentVersion: String): GitHubRelease? {
        return try {
            val response = api.getLatestRelease()
            if (!response.isSuccessful) {
                Log.w(TAG, "GitHub API error: ${response.code()}")
                return null
            }
            val release = response.body() ?: return null
            val latestVersion = release.tagName.removePrefix("v")
            Log.i(TAG, "Latest version on GitHub: $latestVersion (current: $currentVersion)")
            if (isNewerVersion(latestVersion, currentVersion)) {
                Log.i(TAG, "Update available: $latestVersion")
                release
            } else {
                Log.i(TAG, "No update available")
                null
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to check for update", e)
            null
        }
    }

    /**
     * Télécharge l'APK et lance l'installateur.
     */
    suspend fun downloadAndInstall(context: Context, release: GitHubRelease) {
        val url = release.apkDownloadUrl()
        if (url == null) {
            Toast.makeText(context, "Aucun APK trouvé dans la release", Toast.LENGTH_SHORT).show()
            return
        }

        if (!canInstallPackages(context)) {
            Toast.makeText(
                context,
                "Autorise l'installation de sources inconnues pour mettre à jour",
                Toast.LENGTH_LONG
            ).show()
            openInstallSettings(context)
            return
        }

        val file = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            "muscu_update.apk"
        )

        val success = withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(url).build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext false
                    response.body?.byteStream()?.use { input ->
                        file.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    true
                }
            } catch (_: Exception) {
                false
            }
        }

        if (success) {
            installApk(context, file)
        } else {
            Toast.makeText(context, "Échec du téléchargement", Toast.LENGTH_SHORT).show()
        }
    }

    private fun installApk(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(context, "Impossible d'ouvrir l'installateur", Toast.LENGTH_SHORT).show()
        }
    }

    private fun canInstallPackages(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.packageManager.canRequestPackageInstalls()
        } else {
            true
        }
    }

    private fun openInstallSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(
                Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                "package:${context.packageName}".toUri()
            )
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    /**
     * Compare deux versions sémantiques (ex: "1.2.3" vs "1.0").
     * @return true si [latest] est plus récente que [current]
     */
    private fun isNewerVersion(latest: String, current: String): Boolean {
        val latestParts = latest.split(".").map { it.toIntOrNull() ?: 0 }
        val currentParts = current.split(".").map { it.toIntOrNull() ?: 0 }
        val maxLength = maxOf(latestParts.size, currentParts.size)
        for (i in 0 until maxLength) {
            val l = latestParts.getOrElse(i) { 0 }
            val c = currentParts.getOrElse(i) { 0 }
            if (l > c) return true
            if (l < c) return false
        }
        return false
    }
}
