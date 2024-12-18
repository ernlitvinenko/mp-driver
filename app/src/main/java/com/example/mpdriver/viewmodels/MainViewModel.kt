package com.example.mpdriver.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mpdriver.data.models.AppEventKinds
import com.example.mpdriver.data.models.AppTask
import com.example.mpdriver.data.models.EventParameters
import com.example.mpdriver.data.models.MpdSetAppEventsRequest
import com.example.mpdriver.data.models.TaskStatus
import com.example.mpdriver.variables.datetimeFormatFrom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import retrofit2.HttpException

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

    suspend fun isAuthorized(): Boolean {
        fetchTaskData()
        return accessToken.value != null && accessToken.value != ""
    }

    suspend fun fetchTaskData() {
        try {
            val tasksData = api.getTasks(generateSessionHeader())
            tasks.value = tasksData.appTasks
        } catch (e: HttpException) {
            Log.e("fetchTaskData", "Error on fetching tasks: ${e.message}")
            Log.e("fetchTaskData", "Status code: ${e.code()}")

            when (e.code()) {
                401 -> dropAccessToken()
            }
        }
    }

    private fun findParentTask(subtaskId: Long): AppTask? {
        return tasks.value?.find { task ->
            task.subtasks?.find { subtask ->
                subtask.id == subtaskId
            } != null
        }
    }

    private fun buildRequestSchemeItem(
        recId: Long,
        status: EventParameters,
        datetime: LocalDateTime,
        errorText: String? = null
    ): MpdSetAppEventsRequest {
        return if (errorText == null) MpdSetAppEventsRequest(
            recId.toString(),
            AppEventKinds.ChangeTask,
            eventData = listOf(
                mutableMapOf(
                    EventParameters.NewTaskStatus.parameterIndex to status.parameterIndex
                )
            ),
            dateTime = datetime.format(datetimeFormatFrom)
        ) else MpdSetAppEventsRequest(
            recId.toString(),
            AppEventKinds.ChangeTask,
            eventData = listOf(
                mutableMapOf(
                    EventParameters.NewTaskStatus.parameterIndex to status.parameterIndex,
                )
            ),
            text = errorText,
            dateTime = datetime.format(datetimeFormatFrom)
        )
    }


    private fun grabNextSubtask(parent: AppTask, current: AppTask): AppTask? {
        val currentIndex = parent.subtasks?.indexOfFirst { current.id == it.id }!!
        return parent.subtasks[currentIndex + 1]
    }


    private fun buildRequestDataForStartingTask(
        task: AppTask,
        datetime: LocalDateTime
    ): List<MpdSetAppEventsRequest> {
        return mutableListOf(
            buildRequestSchemeItem(task.id, EventParameters.NewTaskStatus.InProgress, datetime),
            buildRequestSchemeItem(
                task.subtasks!!.first().id,
                EventParameters.NewTaskStatus.InProgress,
                datetime
            )
        )
    }


    private fun buildRequestDataWhenCompleted(
        parent: AppTask,
        current: AppTask,
        datetime: LocalDateTime
    ): List<MpdSetAppEventsRequest> {
        val subtaskCount = parent.subtasks?.count()!!
        val index = parent.subtasks.indexOfFirst { current.id == it.id }

        return when (index) {
            subtaskCount - 1 -> {
                listOf(
                    buildRequestSchemeItem(
                        current.id,
                        EventParameters.NewTaskStatus.Completed,
                        datetime
                    ),
                    buildRequestSchemeItem(
                        parent.id,
                        EventParameters.NewTaskStatus.Completed,
                        datetime
                    ),
                )
            }

            else -> {
                listOf(
                    buildRequestSchemeItem(
                        current.id,
                        EventParameters.NewTaskStatus.Completed,
                        datetime
                    ),
                    buildRequestSchemeItem(
                        grabNextSubtask(parent, current)!!.id,
                        EventParameters.NewTaskStatus.InProgress,
                        datetime
                    ),
                )
            }
        }
    }

    private fun buildRequestDataWhenCancelled(
        parent: AppTask,
        current: AppTask,
        datetime: LocalDateTime,
        errorText: String
    ): List<MpdSetAppEventsRequest> {
        val subtaskCount = parent.subtasks?.count()!!
        val index = parent.subtasks.indexOfFirst { current.id == it.id }

        return when (index) {
            subtaskCount - 1 -> {
                listOf(
                    buildRequestSchemeItem(
                        current.id,
                        EventParameters.NewTaskStatus.Cancelled,
                        datetime,
                        errorText
                    ),
                    buildRequestSchemeItem(
                        parent.id,
                        EventParameters.NewTaskStatus.Completed,
                        datetime
                    )
                )
            }

            else -> {
                listOf(
                    buildRequestSchemeItem(
                        current.id,
                        EventParameters.NewTaskStatus.Cancelled,
                        datetime,
                        errorText
                    ),
                    buildRequestSchemeItem(
                        grabNextSubtask(parent, current)!!.id,
                        EventParameters.NewTaskStatus.InProgress,
                        datetime
                    )
                )
            }
        }
    }

    private fun buildRequestData(
        parent: AppTask,
        current: AppTask,
        newStatus: TaskStatus,
        datetime: LocalDateTime,
        errorText: String? = null
    ): List<MpdSetAppEventsRequest> {
        if (parent.id == current.id) {
            return buildRequestDataForStartingTask(current, datetime)
        }

        return when (newStatus) {
            TaskStatus.COMPLETED -> {
                buildRequestDataWhenCompleted(parent, current, datetime)
            }

            else -> {
                buildRequestDataWhenCancelled(parent, current, datetime, errorText!!)
            }
        }
    }


    suspend fun changeTask(
        task: AppTask,
        status: TaskStatus,
        datetime: LocalDateTime,
        errorText: String? = null
    ) {

        val requestData = when (task.subtasks) {
            null -> buildRequestData(task, task, status, datetime, errorText)
            else -> buildRequestData(findParentTask(task.id)!!, task, status, datetime, errorText)

        }

        api.createEvent(generateSessionHeader(), requestData)
    }
    suspend fun changeTask(taskId: Long, status: TaskStatus, datetime: LocalDateTime, errorText: String? = null) {
        val task = tasks.value?.find { it.id == taskId} ?: tasks.value?.map { t ->
            t.subtasks
        }?.reduce {acc, appTasks ->
            acc!! + appTasks!!
        }?.find { sbt -> sbt.id == taskId }
        changeTask(task!!, status, datetime, errorText)
    }

}