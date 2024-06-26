package com.example.mpdriver.api

import android.content.Context
import android.widget.Toast
import com.example.mpdriver.storage.Database
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

data class GetPhoneCodeRequest(
    val phoneNumber: String
)

enum class ErrorCodes {
    @SerializedName("SotrNotFounded")
    SOTR_NOT_FOUNDED,
    @SerializedName("IncorrectPhone")
    INCORRECT_PHONE
}


data class GetPhoneCodeResponse(
    val code: Int?,
    val error: ErrorCodes?,
    val status: Int?,
    val detail: String?,
    val langs: Langs?
)

data class GetTokenResponse(
    @SerializedName("access_token") val accessToken: String?,
    @SerializedName("token_type") val tokenType: String?
)


class Auth(ctx: Context) : Api(ctx) {

    fun getPhoneCode(phone: String, errorHandler: (IOException)-> Unit = {}, handler: (GetPhoneCodeResponse) -> Unit) {
        Database.phoneNumber = phone
        val body = GetPhoneCodeRequest(phone).toJson().toRequestBody("application/json".toMediaType())
        val req = Request.Builder()
            .url("${BASE_URL}/auth/phone")
            .post(body)
            .build()
        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println(e)
               errorHandler(e)
            }

            override fun onResponse(call: Call, response: Response) {
                handler(response.parse())
            }
        })
    }

    fun getToken(phone: String, code: String, errorHandler: (IOException)-> Unit = {}, handler: (GetTokenResponse) -> Unit) {
        val body = MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("username", phone).addFormDataPart("password", code).build()
        val req = Request.Builder()
            .url("${BASE_URL}/auth/phone/code")
            .post(body)
            .build()
        client.newCall(req).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                errorHandler(e)
            }

            override fun onResponse(call: Call, response: Response) {
                val parsedResponse = response.parse<GetTokenResponse>()
                Database.access_token = parsedResponse.accessToken
                handler(parsedResponse)
            }

        })
    }

}