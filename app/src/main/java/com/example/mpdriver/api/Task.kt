package com.example.mpdriver.api

import android.content.Context
import android.provider.ContactsContract.Data
import android.util.Log
import com.example.mpdriver.storage.CreateUpdateTaskData
import com.example.mpdriver.storage.Database
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import ru.gildor.coroutines.okhttp.await
import java.io.IOException
import kotlin.math.log


data class SetTaskStatusRequest(
    val task_id: Long
)

data class TaskResponse(

    @SerializedName("id") var id: Long? = null,
    @SerializedName("startPln") var startPln: String? = null,
    @SerializedName("endPln") var endPln: String? = null,
    @SerializedName("startFact") var startFact: String? = null,
    @SerializedName("endFact") var endFact: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("taskType") var taskType: String? = null,
    @SerializedName("text") var text: String? = null,
    @SerializedName("events") var events: ArrayList<Events> = arrayListOf(),
    @SerializedName("subtasks") var subtasks: ArrayList<Subtasks> = arrayListOf(),
    @SerializedName("route") var route: Route? = Route()

)

data class Events(

    @SerializedName("id") var id: Long? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("text") var text: String? = null,
    @SerializedName("eventDatetime") var eventDatetime: String? = null

)

data class Location(

    @SerializedName("lat") var lat: Double? = null,
    @SerializedName("lon") var lon: Double? = null

)

data class Station(

    @SerializedName("id") var id: Long? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("location") var location: Location? = Location()

)

data class Subtasks(

    @SerializedName("id") var id: Long? = null,
    @SerializedName("parentId") var parentId: Long? = null,
    @SerializedName("startPln") var startPln: String? = null,
    @SerializedName("endPln") var endPln: String? = null,
    @SerializedName("startFact") var startFact: String? = null,
    @SerializedName("endFact") var endFact: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("taskType") var taskType: String? = null,
    @SerializedName("text") var text: String? = null,
    @SerializedName("station") var station: Station? = Station()

)

data class TRS(

    @SerializedName("id") var id: Long? = null,
    @SerializedName("gost") var gost: String? = null

)

data class Route(

    @SerializedName("id") var id: Long? = null,
    @SerializedName("temperatureProperty") var temperatureProperty: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("trailer") var trailer: TRS? = null,
    @SerializedName("truck") var truck: TRS? = null

)


class TaskApi(ctx: Context) : Api(ctx) {

    data class SendTaskStatusReq(
        val data: List<CreateUpdateTaskData>
    )

    fun send_task_status_chains(onResponse: (Response) -> Unit = {}, onFailure: () -> Unit = {}) {
        val chainBody = SendTaskStatusReq(Database.updateTasks).toJson().toRequestBody()
        val req = Request.Builder().url("$BASE_URL/tasks").post(chainBody).build()
        client.newCall(req).enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("taskAPI", "${e}")
                    onFailure()
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.d("taskAPI", response.body.toString())
                    if (response.isSuccessful) {
                        return onResponse(response)
                    }
                    return onFailure()

                }

            }
        )
    }

    suspend fun getPlannedTasksApiCall(
        errorHandler: (Exception) -> Unit = {},
        handler: (List<TaskResponse>) -> Unit
    ) {
        val req = Request.Builder()
            .url("$BASE_URL/tasks/planned")
            .build()

        performRequest(req, errorHandler) {
            handler(it.parseList<TaskResponse>())
        }
    }

//    fun getPlannedTasks(errorHandler: (Exception)-> Unit = {}, handler: (List<TaskResponse>) -> Unit) {
//        runBlocking {
//            launch(Dispatchers.IO) {
//                getPlannedTasksApiCall {
//                    println(Database.planned_tasks)
//                    println(it)
//                    Database.planned_tasks = it
//                    println(Database.planned_tasks)
//                }
//            }
//            handler(Database.planned_tasks)
//        }
//    }

    suspend fun getActiveTaskApiCall(
        errorHandler: (Exception) -> Unit = {},
        handler: (TaskResponse) -> Unit
    ) {
        val req = Request.Builder()
            .url("$BASE_URL/tasks/active")
            .build()

        performRequest(req, errorHandler) {
            handler(it.parse<TaskResponse>())
        }
    }
//    fun getActiveTask(errorHandler: (Exception)-> Unit = {}, handler: (TaskResponse) -> Unit) {
//        runBlocking {
//            launch(Dispatchers.IO) {
//                getAllTasksForUser()
//            }
//
//            val data = Database.tasks.filter { it.status == "InProgress" }
//            if (data.count() >= 1) {
//                return@runBlocking handler(data[0])
//            }
//            handler(TaskResponse())
//        }
//    }

    fun getCompletedTask(
        errorHandler: (Exception) -> Unit = {},
        handler: (List<TaskResponse>) -> Unit
    ) {
        val req = Request.Builder()
            .url("$BASE_URL/tasks/completed")
            .build()

        performRequest(req, errorHandler) {
            handler(it.parseList<TaskResponse>())
        }
    }

    fun getSubtasks(
        taskId: Long,
        errorHandler: (Exception) -> Unit = {},
        handler: (List<Subtasks>) -> Unit
    ) {
        val req = Request.Builder()
            .url("$BASE_URL/tasks/$taskId/subtasks")
            .build()

        performRequest(req, errorHandler) {
            handler(it.parseList<Subtasks>())
        }
    }

    fun getEvents(
        taskId: Long,
        errorHandler: (Exception) -> Unit = {},
        handler: (List<Events>) -> Unit
    ) {
        val req = Request.Builder()
            .url("$BASE_URL/tasks/$taskId/events")
            .build()

        performRequest(req, errorHandler) {
            handler(it.parseList<Events>())
        }
    }

//    fun getAllTasksForUser() {
//        val req = Request.Builder().url("$BASE_URL/tasks").build()
//        performRequest(req) {res ->
//            val data = res.parseList<TaskResponse>()
//            Database.tasks = data
//        }
//    }

//    fun setTaskStatusInProgress(taskId: Long, errorHandler: (Exception)-> Unit = {}, handler: (TaskResponse) -> Unit) {
//
//        val body = SetTaskStatusRequest(taskId).toJson().toRequestBody("application/json".toMediaType())
//
//        val req = Request.Builder()
//            .url("$BASE_URL/tasks/active")
//            .post(body)
//
//        performRequest(req.build(), errorHandler) {
//            handler(it.parse())
//        }
//    }
}