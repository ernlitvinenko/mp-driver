package com.example.mpdriver.data.models

import com.google.gson.annotations.SerializedName


data class GetPhoneCodeRequest(
    @SerializedName("phone") val phoneNumber: String
)

data class GetPhoneCodeResponse(
    val code: Int?,
    val status: Int
)

data class GetTokenResponse(
    @SerializedName("access_token")
    val accessToken: String?,

    @SerializedName("token_type")
    val tokenType: String?
)


data class AppTaskResponse(
    val id: Long,
    val profileId: Long,
    val startPln: String,
    val endPln: String,
    val startFact: String,
    val endFact: String,
    val status: String,
    val taskType: String,
    val text: String,
    val events: List<AppEventResponse>,
    val subtasks: List<AppTaskResponse>,
    val route: AppMarshResponse
)

data class SubTaskResponse(
    val id: Long,
    val startPln: String,
    val endPln: String,
    val startFact: String,
    val endFact: String,
    val status: String,
    val taskType: String,
    val text: String,

    val station: AppMstResponse?
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
//    val eventData: String?,
    val eventDatetime: String
)

data class AppMarshResponse(
    val id: Long,
    val name: String,
    val trailer: AppTRSResponse?,
    val truck: AppTRSResponse?
)

data class AppTRSResponse(
    val id: Long,
    val gost: String?
)