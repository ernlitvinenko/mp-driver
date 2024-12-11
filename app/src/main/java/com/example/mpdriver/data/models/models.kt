package com.example.mpdriver.data.models

import com.google.gson.annotations.SerializedName

enum class TaskStatus {
    // ID_VLST = 20303
    // MPDRIVER: Статус задачи
    @SerializedName("Cancelled") CANCELLED,
    @SerializedName("InProgress") IN_PROGRESS,
    @SerializedName("Completed") COMPLETED,
    @SerializedName("NotDefined") NOT_DEFINED
}

enum class TaskType {
//    ID_VLST = 20301
//    MPDRIVER: Типы событий

    @SerializedName("MovMarsh") MOV_MARSH,
    @SerializedName("Mst_In") MST_IN,
    @SerializedName("Mst_Out") MST_OUT,
    @SerializedName("SetUnLoading") SET_UNLOADING,
    @SerializedName("SetLoading") SET_LOADING
}

enum class MarshTemperatureProperty {
    @SerializedName("1") HOT,
    @SerializedName("2") COLD,
    @SerializedName("0") NOT_DEFINED
}


data class GetPhoneCodeRequest(
    @SerializedName("phone") val phoneNumber: String
)

data class GetPhoneCodeResponse(
    val code: Int?,
    val status: Int
)

data class GetTokenResponse(
    @SerializedName("result")
    val accessToken: List<String>,
)


data class AppTask(
    val id: Long,
    val startPln: String,
    val endPln: String,
    val startFact: String?,
    val endFact: String?,

    val status: TaskStatus,
    val taskType: TaskType,

    val text: String,

    val events: List<AppEventResponse>?,
    val subtasks: List<AppTask>?,
    val route: AppMarshResponse?,

    val station: AppMstResponse?
)

data class GetMPD_APP_TASK_RESPONSE (
    @SerializedName("app_tasks") val appTasks: List<AppTask>?,
    @SerializedName("events") val events: List<AppEventResponse>?
)


data class AppMstResponse (
    val id: Long,
    val name: String,
    val location: AppLocationResponse,
)

data class AppLocationResponse (
    val lat: Float,
    val lon: Float
)

data class AppEventResponse(
    val id: Long,
    val type: String,
    val text: String?,
    val eventDatetime: String
)

data class AppMarshResponse(
    val id: Long,
    val temperatureProperty: MarshTemperatureProperty,
    val name: String,
    val trailer: AppTRSResponse?,
    val truck: AppTRSResponse?
)

data class AppTRSResponse(
    val id: Long,
    val gost: String?
)