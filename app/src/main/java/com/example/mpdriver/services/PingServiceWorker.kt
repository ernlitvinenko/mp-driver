package com.example.mpdriver.services

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class PingServiceWorker( context: Context, params: WorkerParameters): CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {

//                TODO("Implement it")
//                Log.d("pingServiceWork", "doWork: status ok")
                Result.success()
//                val url = URL("https://google.com")
//                val connection = url.openConnection() as HttpURLConnection
//                connection.requestMethod = "GET"
//                connection.connectTimeout = 5000
//                connection.readTimeout = 5000
//
//                val responseCode = connection.responseCode
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    // Ping successful
//                    Log.d("pingServiceWork", "doWork: status ok")
//                    Result.success()
//                } else {
//                    // Ping failed
//                    Log.d("pingServiceWork", "doWork: status bad")
//                    Result.retry()
//               }
            }
            catch (e: Exception) {
//                e.printStackTrace()
//                Log.d("pingServiceWork", "doWork: status bad exception")
                Result.retry()
            }
        }
    }

}