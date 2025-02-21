package com.example.mpdriver.viewmodels

import android.content.Context
import android.util.Log
import com.example.mpdriver.data.api.UpdateChangeLogResponse
import com.example.mpdriver.data.database.Tables

class UpdateViewModel: BaseViewModel() {

    suspend fun checkForUpdates(context: Context): UpdateChangeLogResponse? {
        val data = updateApi.getUpdates(context.packageName)

        if (data.versionCode > VERSION_CODE) {
            Log.d("updates",  "Version: ${data.id} ${data.versionCode} ${data.link} ${data.description}")
            val dbVal = Tables.UpdatesAPIBaseUrl.getValue()
            val linkEP: String

            if (dbVal != null) {
                linkEP = "https://${dbVal}"
            }
            else {
                linkEP = "https://mp-srv.jde.ru"
            }

            data.link = "${linkEP}${data.link}"
            Log.d("updates", data.link)
            return data
        }
        return null
    }

    suspend fun installYanavi(): UpdateChangeLogResponse {
        return updateApi.getUpdates("ru.yandex.yandexnavi")
    }

}