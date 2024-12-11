package com.example.mpdriver.viewmodels

import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.mpdriver.data.api.ApiService
import com.example.mpdriver.data.api.RetrofitClient
import com.example.mpdriver.data.models.GetPhoneCodeRequest
import com.example.mpdriver.data.models.GetPhoneCodeResponse
import okhttp3.Credentials

class AuthViewModel : BaseViewModel() {

    private var _phoneNumber = MutableLiveData("")
    private var _phoneCode = MutableLiveData("")

    var phoneNumber: LiveData<String> = _phoneNumber
    var phoneCode: LiveData<String> = _phoneCode

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
            val data = api.getPhoneCode(GetPhoneCodeRequest(phoneNumber = it))
            return data
        }
        return null
    }

    suspend fun authenticate(setAccessTokenHandler: (String) -> Unit = {}) {
        if (phoneNumber.value.isNullOrEmpty() || phoneCode.value.isNullOrEmpty()) {
            return
        }
        val cred = Credentials.basic(phoneNumber.value!!, phoneCode.value!!)

        val data = api.getToken(cred)
        data.accessToken.forEach {
            setAccessTokenHandler(it)
        }


    }


}