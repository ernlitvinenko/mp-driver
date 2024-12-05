package com.example.mpdriver.data.api

import android.app.ActivityManager.AppTask
import com.example.mpdriver.data.models.AppTaskResponse
import com.example.mpdriver.data.models.GetPhoneCodeRequest
import com.example.mpdriver.data.models.GetPhoneCodeResponse
import com.example.mpdriver.data.models.GetTokenResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    // Аутентификация
    @POST("PhoneAuthorize/")
    suspend fun getPhoneCode(@Body req: GetPhoneCodeRequest): GetPhoneCodeResponse

    @POST("GetPhoneAuthorize")
    suspend fun getToken(@Header("Authorization") authorization: String): GetTokenResponse

    // Задачи и подзадачи

    @GET("tasks")
    suspend fun getTasks(@Header("Authorization") token: String): List<AppTaskResponse>
}

object RetrofitClient {
    private const val BASE_URL = "http://10.2.100.110:30033/mpdriver/"
    val api: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }
}
