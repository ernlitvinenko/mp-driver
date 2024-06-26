package com.example.mpdriver.storage

import com.example.mpdriver.api.TaskResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tencent.mmkv.MMKV
import java.lang.reflect.Type

object Database {
    val kv = MMKV.defaultMMKV()

    var tasks: List<TaskResponse>
        get() {
            val data = kv.decodeString("tasks")
            data?.let { json ->
                val type: Type = object : TypeToken<List<TaskResponse>>() {}.type
                return Gson().fromJson(json, type)
            }
            return emptyList()
        }
        set(value) {
            val data = Gson().toJson(value)
            kv.encode("tasks", data)
        }

    var planned_tasks: List<TaskResponse>
        get() {
            val data = kv.decodeString("planned_tasks")
            data?.let { json ->
                val type: Type = object : TypeToken<List<TaskResponse>>() {}.type
                return Gson().fromJson(json, type)
            }
            return emptyList()
        }
        set(value) {
            val data = Gson().toJson(value)
            println(value)
            println(data)
            kv.encode("planned_tasks", data)
        }

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
}