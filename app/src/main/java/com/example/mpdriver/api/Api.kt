package com.example.mpdriver.api

import android.annotation.SuppressLint
import android.content.Context
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mpdriver.storage.Database
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.EMPTY_REQUEST
import java.io.IOException
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit


data class Langs(
    val ru: String?
)

fun <T> T.toJson(): String {
    return Gson().toJson(this)
}

inline fun <reified T> Response.parse(): T {
    val data = this.body!!.string()
    return Gson().fromJson(data, T::class.java)
}

inline fun <reified T> Response.parseList(): List<T> {
    val data = this.body!!.string()
    val type: Type = object : TypeToken<List<T>>() {}.type
    return Gson().fromJson(data, type)
}


open class Api(val ctx: Context) {

    val clientCheckAuth = OkHttpClient.Builder().build()

    val client = OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).writeTimeout(30 ,TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val req = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${Database.access_token}")
                .build()
            chain.proceed(req)
        }
        .build()


    val BASE_URL = "http://192.168.0.101:8000/api/v1"

    fun performRequest(clientReq: Request, errorHandler: (Exception) -> Unit = {}, handler: (Response) -> Unit) {
        client.newCall(clientReq).enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                errorHandler(e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.code == 401) {
                    Database.access_token = null
                    hostController?.let { hc ->
                        MainScope().launch {
                            hc.navigate("auth")
                        }
                    }
                }
                handler(response)
            }
        })
    }

    companion object {
        var hostController: NavHostController? = null

        fun setNavHostController(hc: NavHostController) {
            hostController = hc
        }
    }
}

