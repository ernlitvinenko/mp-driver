package com.example.mpdriver.data.api

import com.example.mpdriver.data.models.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    // Аутентификация
    @POST("PhoneAuthorize/")
    suspend fun getPhoneCode(@Body req: GetPhoneCodeRequest): GetPhoneCodeResponse

    @GET("GetPhoneAuthorize")
    suspend fun getToken(@Header("Authorization") authorization: String): GetTokenResponse

    // Задачи и подзадачи

    @GET("GetMPD_APP_TASKS")
    suspend fun getTasks(@Header("Pragma") dssession: String, @Query("DT") datetime: String): GetMPD_APP_TASK_RESPONSE

//    Создание событий
    @POST("MPD_SET_APP_EVENTS")
    suspend fun createEvent(@Header("Pragma") dssession: String, @Body eventData: List<MpdSetAppEventsRequest>): MpdSetAppEventsResponse
}

object RetrofitClient {
    private const val BASE_URL = "http://10.2.101.188:30008/datasnapJDE/rest/TsmAPIvJ/"
    val api: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }
}
