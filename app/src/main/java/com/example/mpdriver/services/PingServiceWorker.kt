package com.example.mpdriver.services

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mpdriver.data.api.RetrofitClient
import com.example.mpdriver.data.database.MMKVDb
import com.example.mpdriver.data.models.MpdSetAppEventsRequest
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime
import java.net.HttpURLConnection
import java.net.URL

class PingServiceWorker( context: Context, params: WorkerParameters): CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        return withContext(Dispatchers.IO) {
            try {
                val api = RetrofitClient.api
                val dssession =  "dssession=${com.example.mpdriver.data.database.Tables.AccessToken.getValue()}"

                Log.d("PingServiceWorker", "access token is: ${com.example.mpdriver.data.database.Tables.AccessToken.getValue()}")

                // Get updates URL from MMKV
                val updates = com.example.mpdriver.data.database.Tables.Updates.getValue()
                
                updates?.let { update ->
                    try {
                        val updatesList = Gson().fromJson(update, Array<MpdSetAppEventsRequest>::class.java).toList()
                        Log.d("PingServiceWorker", "Updates: ${updatesList}")
                        api.createEvent(dssession, updatesList)
                        com.example.mpdriver.data.database.Tables.Updates.deleteValue()
                    } catch (e: Exception) {
                        Log.e("PingServiceWorker", "Error pinging updates URL: ${e.message}")
                    }
                }

                val tasksData = api.getTasks(
                    dssession,
                    kotlinx.datetime.Clock.System.now().toLocalDateTime(
                        kotlinx.datetime.TimeZone.currentSystemDefault()
                    ).format(com.example.mpdriver.variables.datetimeFormatFrom)
                )

                tasksData.appTasks?.let { list ->
                    // Clear existing tasks
                    com.example.mpdriver.data.database.Tables.Tasks.listValues().forEach {
                        com.example.mpdriver.data.database.Tables.Tasks.deleteValue(it.id)
                    }

                    // Store new tasks
                    list.forEach {
                        com.example.mpdriver.data.database.Tables.Tasks.setValue(it.id, it)
                    }
                }

                Log.d("PingServiceWorker", "Successfully fetched and stored ${tasksData.appTasks?.size} tasks")
                Result.success()
            }
            catch (e: Exception) {
                Log.e("PingServiceWorker", "Error fetching tasks: ${e.message}")
                Result.retry()
            }
        }
    }

}