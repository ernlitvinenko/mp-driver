package com.example.mpdriver.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mpdriver.data.api.RetrofitClient

open class BaseViewModel: ViewModel() {
    internal val api = RetrofitClient.api

    fun setAccessToken(token: String) {
        accessToken.value = token
    }

    fun generateSessionHeader(): String {
        return "dssession=${accessToken.value}"
    }

    companion object {
        val accessToken =  MutableLiveData("")
    }
}