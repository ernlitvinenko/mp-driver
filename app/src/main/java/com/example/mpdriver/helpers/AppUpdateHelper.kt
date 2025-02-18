package com.example.mpdriver.helpers

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import kotlin.coroutines.coroutineContext


class AppUpdateHelper(private val context: Context) {

    fun downloadAndInstallApk(apkUrl: String): Long {
        // Create a directory to store the downloaded APK

//        var isDownloading = true

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
//                    isDownloading = false
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
        return downloadId
    }


    @SuppressLint("Range")
    suspend fun getPercentageOfDownloading(downloadId: Long, cb: (Float) -> Unit) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        coroutineScope {
            var isDownloading = true
            while (isDownloading) {
                val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadId))
                cursor.moveToFirst()
                val cIdxBytesDownloaded = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                val cIdxBytesTotal = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                if (cIdxBytesDownloaded < 0 || cIdxBytesTotal < 0) {
                    throw Exception("Cannot get right column index")
                }
                val bytesDownloaded = cursor.getFloat(cIdxBytesDownloaded)
                val bytesTotal = cursor.getFloat(cIdxBytesTotal)

                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    isDownloading = false;
                }
                val dl_progress = (bytesDownloaded / bytesTotal)
                delay(1000)
                withContext(Dispatchers.Main) {
                    cb(dl_progress)
                }
            }
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