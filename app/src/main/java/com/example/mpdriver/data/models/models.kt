package com.example.mpdriver.data.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
enum class TaskStatus {
    // ID_VLST = 20303
    // MPDRIVER: Статус задачи
    @SerializedName("Cancelled")
    CANCELLED,

    @SerializedName("InProgress")
    IN_PROGRESS,

    @SerializedName("Completed")
    COMPLETED,

    @SerializedName("NotDefined")
    NOT_DEFINED
}

@Keep
enum class TaskType {
//    ID_VLST = 20301
//    MPDRIVER: Типы событий

    @SerializedName("MovMarsh")
    MOV_MARSH,

    @SerializedName("Mst_In")
    MST_IN,

    @SerializedName("Mst_Out")
    MST_OUT,

    @SerializedName("SetUnLoading")
    SET_UNLOADING,

    @SerializedName("SetLoading")
    SET_LOADING
}

@Keep
enum class MarshTemperatureProperty {
    @SerializedName("1")
    HOT,

    @SerializedName("2")
    COLD,

    @SerializedName("0")
    NOT_DEFINED
}

@Keep
data class GetPhoneCodeRequest(
    @SerializedName("phone") val phoneNumber: String
)
@Keep
data class GetPhoneCodeResponse(
    val code: Int?,
    val status: Int,
    val error: String?
)
@Keep
data class GetTokenResponse(
    @SerializedName("result")
    val accessToken: List<String>,
)

@Keep
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
@Keep
data class GetMPD_APP_TASK_RESPONSE(
    @SerializedName("app_tasks") val appTasks: List<AppTask>?,
    @SerializedName("global_events") val events: List<AppEventResponse>?,
    @SerializedName("global_notes") val notes: List<AppNote>?
)

@Keep
data class AppMstResponse(
    val id: Long,
    val name: String,
    val location: AppLocationResponse,
)
@Keep
data class AppLocationResponse(
    val lat: Float,
    val lon: Float
)
@Keep
data class AppEventResponse(
    val id: Long,
    val type: String,
    val text: String?,
    val eventDatetime: String,
    val eventData: List<Map<String, String>>
)
@Keep
data class AppNote(
    val id: Long,
    val idAppTask: AppTask?,
    val status: Int,
    val type: Int,
    val text: String,
    val dtCreate: String,
    val dtChange: String
)
@Keep
data class AppMarshResponse(
    val id: Long,
    val temperatureProperty: MarshTemperatureProperty,
    val name: String,
    val trailer: AppTRSResponse?,
    val truck: AppTRSResponse?
)
@Keep
data class AppTRSResponse(
    val id: Long,
    val gost: String?
)


enum class AppEventKinds {
    @SerializedName("8678")
    ChangeTask,

    @SerializedName("8795")
    CreateNote,

    @SerializedName("8797")
    ChangeNote,

    @SerializedName("8798")
    CreateUserEvent
}


sealed class EventParameters(val parameterIndex: String) {
    data object IdMarshTrs: EventParameters("8750")
    data object NewTaskStatus: EventParameters("8794") {
        data object NotDefined  : EventParameters("8687")
        data object Cancelled   : EventParameters("8680")
        data object InProgress  : EventParameters("8681")
        data object Completed   : EventParameters("8682")
    }
    data object IdTrs: EventParameters("8667")
    data object IdMst:  EventParameters("8668")
    data object IdMarsh:EventParameters("8666")
    data object DTS:    EventParameters("8669")
    data object DTPo:   EventParameters("8670")
}



data class MpdSetAppEventsRequest(
    @SerializedName("APP_EVENT_ID_REC") val recordId: String? = null,
    @SerializedName("APP_EVENT_VID") val kind: AppEventKinds,
    @SerializedName("APP_EVENT_TIP") val type: String? = null,
    @SerializedName("APP_EVENT_DATA") val eventData: List<Map<String, String>>,
    @SerializedName("APP_EVENT_DT") val dateTime: String,
    @SerializedName("APP_EVENT_TEXT") val text: String? = null
)

data class MpdSetAppEventsResponse(
    val status: Int?,
    val error: String?
)