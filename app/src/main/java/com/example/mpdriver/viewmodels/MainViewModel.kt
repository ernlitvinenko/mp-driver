package com.example.mpdriver.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.mpdriver.data.models.AppEventKinds
import com.example.mpdriver.data.models.AppEventResponse
import com.example.mpdriver.data.models.AppNote
import com.example.mpdriver.data.models.AppTask
import com.example.mpdriver.data.models.EventParameters
import com.example.mpdriver.data.models.MpdSetAppEventsRequest
import com.example.mpdriver.data.models.TaskStatus
import com.example.mpdriver.variables.datetimeFormatFrom
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime
import retrofit2.HttpException
import java.net.SocketTimeoutException


class MainViewModel : BaseViewModel() {

    val tasks: MutableLiveData<List<AppTask>> by lazy {
        MutableLiveData<List<AppTask>>()
    }

    val events: MutableLiveData<List<AppEventResponse>?> by lazy {
        MutableLiveData()
    }

    val notes: MutableLiveData<List<AppNote>?> by lazy {
        MutableLiveData()
    }

    val plannedTasksLiveData = tasks.map {
        it.filter { task ->
            task.status == TaskStatus.NOT_DEFINED
        }
    }

    val completedTaskLiveData = tasks.map {
        it.filter { task ->
            task.status == TaskStatus.COMPLETED
        }
    }

    val activeTaskLiveData = tasks.map {
        it.find { task ->
            task.status == TaskStatus.IN_PROGRESS
        }
    }


    suspend fun isAuthorized(): Boolean {
        fetchTaskData()
        return accessToken.value != null && accessToken.value != ""
    }

    suspend fun fetchTaskData() {
        try {
            val tasksData = api.getTasks(
                generateSessionHeader(), Clock.System.now().toLocalDateTime(
                    TimeZone.currentSystemDefault()
                ).format(datetimeFormatFrom)
            )
            Log.d("fetchTaskData", "fetchTaskData: ${tasksData.appTasks}")
            tasksData.appTasks?.let {
                tasks.value = it
            }
            tasksData.events?.let {
                events.value = it
            }
            tasksData.notes?.let {
                notes.value = it
            }
        } catch (e: HttpException) {
            Log.e("fetchTaskData", "Error on fetching tasks: ${e.message}")
            Log.e("fetchTaskData", "Status code: ${e.code()}")

            when (e.code()) {
                401 -> dropAccessToken()
            }
        }
        catch (e: SocketTimeoutException) {
            Log.e("fetchTaskData", "fetchTaskData: error on fetching tasks: ${e.message}", )
        }
    }

    suspend fun addEvent(
        task: AppTask? = null,
        datetime: LocalDateTime? = null,
        eventData: Map<String, String>
    ) {
        try {
            api.createEvent(
                generateSessionHeader(),
                listOf(
                    MpdSetAppEventsRequest(
                        task?.id?.toString(),
                        kind = AppEventKinds.CreateUserEvent,
                        eventData = listOf( eventData),
                        dateTime = datetime?.format(datetimeFormatFrom)
                            ?: Clock.System.now().toLocalDateTime(
                                TimeZone.currentSystemDefault()).format(datetimeFormatFrom)
                    )
                )
            )
        } catch (e: HttpException) {
            Log.w("AddEventException", "addEvent: ${e.message()}\nStatus Code: ${e.code()}}", )
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


    private fun grabNextSubtask(parent: AppTask, current: AppTask): AppTask {
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
                        grabNextSubtask(parent, current).id,
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
                        grabNextSubtask(parent, current).id,
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
            null -> buildRequestData(findParentTask(task.id)!!, task, status, datetime, errorText)
            else -> buildRequestData(task, task, status, datetime, errorText)

        }

        api.createEvent(generateSessionHeader(), requestData)
    }

    suspend fun changeTask(
        taskId: Long,
        status: TaskStatus,
        datetime: LocalDateTime,
        errorText: String? = null
    ) {
        val task = tasks.value?.find { it.id == taskId } ?: tasks.value?.map { t ->
            t.subtasks
        }?.reduce { acc, appTasks ->
            acc!! + appTasks!!
        }?.find { sbt -> sbt.id == taskId }
        changeTask(task!!, status, datetime, errorText)
    }

}