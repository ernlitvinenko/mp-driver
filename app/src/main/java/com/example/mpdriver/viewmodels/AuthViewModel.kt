package com.example.mpdriver.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mpdriver.data.models.GetPhoneCodeRequest
import com.example.mpdriver.data.models.GetPhoneCodeResponse
import okhttp3.Credentials

class AuthViewModel : BaseViewModel() {

    private var _phoneNumber = MutableLiveData("")
    private var _phoneCode = MutableLiveData("")

    var phoneNumber: LiveData<String> = _phoneNumber
    var phoneCode: LiveData<String> = _phoneCode

    fun clearAllData() {
        _phoneNumber.value = ""
        _phoneCode.value = ""
    }

    fun onChangePhoneNumber(value: String) {
        _phoneNumber.value = value
    }

    fun onChangePhoneCode(value: String) {
        if (value.length > 4) {
            _phoneCode.value = value.slice(0..3)
            return
        }
        _phoneCode.value = value
    }


    suspend fun getCode(): GetPhoneCodeResponse? {
        phoneNumber.value?.let {
            try {
                val data = api.getPhoneCode(GetPhoneCodeRequest(phoneNumber = it))
                Log.d("GetSMSCode", "getCode bla bla bla: ${data}")
                return data
            }
            catch (e: Exception) {
                Log.e("GetSMSCodeError", "error is: ${e.message}")
                return GetPhoneCodeResponse(code=null, status = -100, error = null)
            }

        }
        Log.d("SMSCODEERROR", "phone_number: ${phoneNumber.value}")
        return null
    }

    suspend fun authenticate(setAccessTokenHandler: (String) -> Unit = {}) {
        if (phoneNumber.value.isNullOrEmpty() || phoneCode.value.isNullOrEmpty()) {
            return
        }
        val cred = Credentials.basic(phoneNumber.value!!, phoneCode.value!!)
        try {
            val data = api.getToken(cred)
            data.accessToken.forEach {
                setAccessTokenHandler(it)
            }
        }
        catch (e: Exception) {
            Log.e("Enter SMS Code", "${e.message}")
            return
        }



    }


}