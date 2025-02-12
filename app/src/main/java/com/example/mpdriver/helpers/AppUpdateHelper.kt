package com.example.mpdriver.helpers
import android.app.DownloadManager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import android.os.Build
import java.util.UUID

class AppUpdateHelper(private val context: Context) {

    fun downloadAndInstallApk(apkUrl: String) {
        // Create a directory to store the downloaded APK
        val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "AppUpdates")
        if (!directory.exists()) {
            directory.mkdirs()
        }

        // Create a file to save the APK
        val myUuid = UUID.randomUUID()
        val myUuidAsString = myUuid.toString()

        val file = File(directory, "${myUuidAsString}.apk")

        // Set up the download request
        val request = DownloadManager.Request(Uri.parse(apkUrl))
            .setTitle("MP Водитель")
            .setDescription("Скачиваем последнюю версию приложения")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setMimeType("application/vnd.android.package-archive")
            .setDestinationUri(Uri.fromFile(file))
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        // Get the download service and enqueue the download
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(request)

        // Monitor the download progress
        val downloadCompleteReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadId) {
                    installApk(file)
                }
            }
        }

        // Register the receiver with the appropriate flag
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(
                downloadCompleteReceiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    Context.RECEIVER_NOT_EXPORTED // Use NOT_EXPORTED for better security
                } else {
                    Context.RECEIVER_EXPORTED
                }
            )
        } else {
            context.registerReceiver(
                downloadCompleteReceiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            )
        }
    }

    private fun installApk(apkFile: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        val apkUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            apkFile
        )

        intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        context.startActivity(intent)
    }
}