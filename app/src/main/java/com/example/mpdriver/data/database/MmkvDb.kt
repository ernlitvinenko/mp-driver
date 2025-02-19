package com.example.mpdriver.data.database

import android.content.Context
import com.example.mpdriver.data.models.AppTask
import com.google.gson.Gson
import com.tencent.mmkv.MMKV


interface Methods {
    fun deleteValue()
    fun getValue(): String?
    fun setValue(value: String)
}

open class Table(val pkey: String) : Methods {
    internal val kv = MMKVDb.instance.kv
    override fun deleteValue() {
        kv.removeValueForKey(pkey)
    }

    override fun getValue(): String? {
        return kv.decodeString(pkey)
    }

    override fun setValue(value: String) {
        kv.encode(pkey, value)
    }

}

sealed class Tables {
    data object AccessToken : Table("access_token")
    data object ServerAPIBaseURL : Table("server_api_base_url")
    data object UpdatesAPIBaseUrl : Table("updates_api_base_url")
    data object Tasks : Table("tasks") {
        fun listValues():List<AppTask> {
            val regex = """${pkey}\/\d+""".toRegex()
            val keys = kv.allKeys()?.filter { regex.matches(it) }
            keys?.let { list ->
                return list.map { key ->
                    Gson().fromJson(kv.decodeString(key), AppTask::class.java)
                }
            }
            return emptyList()
        }

        fun getValue(id: Long): AppTask? {
            val regex = """${pkey}\/$id""".toRegex()
            val keys = kv.allKeys()?.filter { regex.matches(it) }
            keys?.let { l ->
                if (l.size == 0) {
                    return null
                }
                val key = l.first()
                val strData = kv.decodeString(key)
                return Gson().fromJson(strData, AppTask::class.java)
            }
            return null
        }

        fun setValue(id: Long, value: AppTask) {
            kv.encode("$pkey/$id", Gson().toJson(value))
        }

        fun deleteValue(id: Long) {
            kv.removeValueForKey("$pkey/$id")
        }
    }
    data object Updates: Table("updates")
}


class MMKVDb {
    private lateinit var rootDir: String
    val kv: MMKV
        get() {
            if (!isInitialized) {
                throw Exception("Database is not initialized")
            }
            return MMKV.defaultMMKV()
        }

    private var isInitialized = false

    fun initializeStorage(context: Context) {
        // Run this in main activity
        rootDir = MMKV.initialize(context)
        isInitialized = true
    }

    companion object {
        val instance: MMKVDb by lazy {
            MMKVDb()
        }
    }

}