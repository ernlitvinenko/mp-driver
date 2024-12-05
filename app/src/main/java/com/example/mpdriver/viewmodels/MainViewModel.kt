package com.example.mpdriver.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mpdriver.data.models.AppTaskResponse

class MainViewModel: ViewModel() {
    val accessToken =  MutableLiveData("")


    val tasks: MutableLiveData<List<AppTaskResponse>> by lazy {
        MutableLiveData<List<AppTaskResponse>>()
    }

    fun updateTaskList(data: List<AppTaskResponse>) {
        tasks.value = data
    }

//    TODO Write services for receiving data and push it in viewModel attrs.

}