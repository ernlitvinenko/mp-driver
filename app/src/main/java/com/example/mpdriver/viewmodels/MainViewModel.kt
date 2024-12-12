package com.example.mpdriver.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mpdriver.data.models.AppTask
import com.example.mpdriver.data.models.TaskStatus

class MainViewModel : BaseViewModel() {

    val tasks: MutableLiveData<List<AppTask>> by lazy {
        MutableLiveData<List<AppTask>>()
    }

    val plannedTasks: List<AppTask>
        get() = if (tasks.value != null) tasks.value!!.filter { it.status == TaskStatus.NOT_DEFINED } else emptyList()

    val completedTasks: List<AppTask>
        get() = if (tasks.value != null) tasks.value!!.filter { it.status == TaskStatus.COMPLETED } else emptyList()

    val activeTask: AppTask?
        get() {
            if (tasks.value != null) {
                return try {
                    tasks.value!!.filter { it.status == TaskStatus.IN_PROGRESS }.first()
                } catch (_: NoSuchElementException) {
                    null
                }
            }
            return null
        }

    fun isAuthorized(): Boolean {
        return accessToken.value != null && accessToken.value != ""
    }

    suspend fun fetchTaskData() {
        try {
            val tasksData = api.getTasks(generateSessionHeader())
            tasks.value = tasksData.appTasks
        } catch (e: Exception) {
            Log.d("fetchTaskData", "Error on fetching tasks: ${e.message}")
        }

    }

//    TODO Write services for receiving data and push it in viewModel attrs.

}