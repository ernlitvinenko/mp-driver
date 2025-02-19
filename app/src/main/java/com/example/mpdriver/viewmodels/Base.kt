package com.example.mpdriver.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.preference.PreferenceManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mpdriver.data.api.RetrofitClient
import com.example.mpdriver.data.api.RetrofitUpdateApi
import com.example.mpdriver.data.database.DatabaseHelper
import com.example.mpdriver.data.database.MMKVDb
import com.example.mpdriver.data.database.Tables
import com.example.mpdriver.variables.VC
import com.tencent.mmkv.MMKV
import java.sql.SQLException

open class BaseViewModel : ViewModel() {
    internal val VERSION_CODE = VC

    internal val api = RetrofitClient.api
    internal val updateApi = RetrofitUpdateApi.api
    internal var db: SQLiteDatabase? = null


    fun dropAccessToken() {
        accessToken.value = null
        try {
            Tables.AccessToken.deleteValue()
            Tables.Tasks.listValues().forEach { Tables.Tasks.deleteValue(it.id) }
        } catch (e: Exception) {
            Log.e("viewmodel_dropAccessToken", "dropAccessToken: ${e.message}")
        }
    }

    fun setAccessToken(token: String) {
        accessToken.value = token
        try {
            Tables.AccessToken.setValue(token)
        } catch (e: Exception) {
            Log.e("viewModel_setAccessToken", "setAccessToken: ${e.message}")
        }

    }

    fun initAccessToken() {

        try {
            val token = Tables.AccessToken.getValue()

            token?.let {
                setAccessToken(it)
            }
        } catch (e: Exception) {
            Log.e("viewmodel_initAccessToken", "initAccessToken: ${e.message}")
        }

    }

    fun generateSessionHeader(): String {
        return "dssession=${accessToken.value}"
    }

    fun initializeDatabase(context: Context) {
        MMKVDb.instance.initializeStorage(context)
        db = DatabaseHelper.newInstance(context).writableDatabase
    }

    companion object {
        val accessToken = MutableLiveData<String?>(null)
    }
}