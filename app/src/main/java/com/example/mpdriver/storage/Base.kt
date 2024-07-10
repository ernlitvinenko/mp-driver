package com.example.mpdriver.storage

import android.util.Log
import com.benasher44.uuid.uuid4
import com.example.mpdriver.FetchApplicationDataQuery
import com.example.mpdriver.api.toJson
import com.example.mpdriver.type.DateTime
import com.example.mpdriver.type.StatusEnumQl
import com.google.gson.Gson
import com.tencent.mmkv.MMKV


data class CreateUpdateTaskData(
    val task_id: Long,
    val dt: Long,
    val status: String,
    val error_text: String? = null
)

object Database {

    val kv = MMKV.defaultMMKV()

    val allKeys: Array<out String>?
        get() = kv.allKeys()

    var access_token: String?
        get() {
            return kv.decodeString("access_token")
        }
        set(value) {
            kv.encode("access_token", value)
        }

    var phoneNumber: String?
        get() = kv.decodeString("phone_number")
        set(value) {
            kv.encode("phone_number", value)
        }


    private inline fun <reified D> parseData(prefix: String): MutableList<D> {
        val returnedList = mutableListOf<D>()

        for (key in allKeys!!) {
            if (key.startsWith(prefix)) {
                val data = kv.decodeString(key)
                data.let {
                    returnedList.add(Gson().fromJson(it, D::class.java))
                }
            }
        }
        return returnedList
    }

    val tasks: MutableList<FetchApplicationDataQuery.Task>
        get() = parseData("task:")

    val notes: MutableList<FetchApplicationDataQuery.Note>
        get() = parseData("note:")

    val subtasks: MutableList<FetchApplicationDataQuery.Subtask1>
        get() = parseData("subtask:")


    val updateTasks: MutableList<CreateUpdateTaskData>
        get() = parseData("update:task:")

    fun createUpdateTaskDataLocally(d: CreateUpdateTaskData) {

        kv.encode("update:task:${uuid4()}", d.toJson())
        val task = tasks.find { it.id == d.task_id.toString() }
        val sbt = subtasks.find { it.id == d.task_id.toString() }

        task?.let {

            val newTask = FetchApplicationDataQuery.Task(
                id = task.id,
                startPln = task.startPln,
                startFact = task.startFact,
                endPln = task.endPln,
                endFact = task.endFact,
                status = when (d.status) {
                    "InProgress" -> StatusEnumQl.IN_PROGRESS
                    "Completed" -> StatusEnumQl.COMPLETED
                    "Cancelled" -> StatusEnumQl.CANCELLED
                    else -> task.status
                },
                events = task.events,
                subtasks = task.subtasks,
                taskType = task.taskType,
                route = task.route,
                text = task.text
            )
            kv.encode("task:${newTask.id}", newTask.toJson())
//            Log.d("MMKV: task:${newTask.id}", kv.decodeString("task:${newTask.id}")!!)
            Log.d("MMKV.KEYS", allKeys!!.toMutableList().joinToString(", "))
        }

        sbt?.let { s ->
            val newSubtask = FetchApplicationDataQuery.Subtask1(
                id = s.id,
                startPln = s.startPln,
                startFact = s.startFact,
                endPln = s.endPln,
                endFact = s.endFact,
                station = s.station,
                status = when (d.status) {
                    "InProgress" -> StatusEnumQl.IN_PROGRESS
                    "Completed" -> StatusEnumQl.COMPLETED
                    "Cancelled" -> StatusEnumQl.CANCELLED
                    else -> s.status
                },
                taskType = s.taskType,
                text = s.text
            )
            kv.encode("subtask:${newSubtask.id}", newSubtask.toJson())
            Log.d("MMKV.KEYS", allKeys!!.toMutableList().joinToString(", "))
        }

    }


    fun dropUpdates() {
        for (key in allKeys!!){
            if (key.startsWith("update:task:")) {
                kv.remove(key)
            }
        }
    }

}
