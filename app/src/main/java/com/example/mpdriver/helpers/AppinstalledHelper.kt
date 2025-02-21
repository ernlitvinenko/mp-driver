package com.example.mpdriver.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log

@SuppressLint("QueryPermissionsNeeded")
fun isAppInstalled(context: Context, packageName: String): Boolean {
    return try {
        // Получаем информацию о пакете
        val packages = context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        for (packageInfo in packages) {
            Log.d("installedApp", "Package name:" + packageInfo.packageName)
        }

        context.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        true // Если пакет найден, возвращаем true
    } catch (e: PackageManager.NameNotFoundException) {
        false // Если пакет не найден, возвращаем false
    }
}