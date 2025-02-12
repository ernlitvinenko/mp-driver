package com.example.mpdriver.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import com.example.mpdriver.data.api.UpdateChangeLogResponse

class UpdateViewModel: BaseViewModel() {

    suspend fun checkForUpdates(context: Context): UpdateChangeLogResponse? {
        val data = updateApi.getUpdates(context.packageName)

        if (data.versionCode > VERSION_CODE) {
            Log.d("updates",  "Version: ${data.id} ${data.versionCode} ${data.link} ${data.description}")
            return data
        }
        return null
    }

    suspend fun installYanavi(): UpdateChangeLogResponse {
        return updateApi.getUpdates("ru.yandex.yandexnavi")
    }

}