package com.example.mpdriver.data.api

import androidx.annotation.Keep
import com.example.mpdriver.data.database.Tables
import com.example.mpdriver.data.models.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Аутентификация
    @POST("PhoneAuthorize/")
    suspend fun getPhoneCode(@Body req: GetPhoneCodeRequest): GetPhoneCodeResponse

    @GET("GetPhoneAuthorize")
    suspend fun getToken(@Header("Authorization") authorization: String): GetTokenResponse

    // Задачи и подзадачи

    @GET("GetMPD_APP_TASKS")
    suspend fun getTasks(
        @Header("Pragma") dssession: String,
        @Query("DT") datetime: String
    ): GetMPD_APP_TASK_RESPONSE

    //    Создание событий
    @POST("MPD_SET_APP_EVENTS")
    suspend fun createEvent(
        @Header("Pragma") dssession: String,
        @Body eventData: List<MpdSetAppEventsRequest>
    ): MpdSetAppEventsResponse

    @GET("GetMPDSotrInf")
    suspend fun getUsername(@Header("Pragma") dssession: String): String

    @GET("GetMPDServerTime")
    suspend fun getServerTime(@Header("Pragma") dssession: String): String

}

object RetrofitClient {
    private val BASE_URL: String
        get() {
            val EP = Tables.ServerAPIBaseURL.getValue()

            EP?.let {
                return "http://${it}/datasnapJDE/rest/TsmAPIvJ/"
            }

            Tables.ServerAPIBaseURL.setValue("10.2.100.110:30033")

            return "http://10.2.100.110:30033/datasnapJDE/rest/TsmAPIvJ/"
        }
    val api: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }
}


@Keep
data class UpdateChangeLogResponse(
    val id: String,
    val versionCode: Int,
    val description: String,
    val link: String
)

interface UpdateService {
    @GET("updates/{appId}/update-changelog.json")
    suspend fun getUpdates(@Path("appId") applicationId: String): UpdateChangeLogResponse
}

object RetrofitUpdateApi {
    private val BASE_URL: String
        get() {
            val EP = Tables.UpdatesAPIBaseUrl.getValue()

            EP?.let {
                return "http://$it/"
            }

            Tables.UpdatesAPIBaseUrl.setValue("10.2.101.91:8005")

            return "http://10.2.101.91:8005/"
        }

    val api: UpdateService by lazy {
        val retrofit =
            Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
                .build()
        retrofit.create(UpdateService::class.java)
    }
}