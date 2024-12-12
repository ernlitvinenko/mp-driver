package com.example.mpdriver.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.preference.PreferenceManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mpdriver.data.api.RetrofitClient
import com.example.mpdriver.data.database.DatabaseHelper
import java.sql.SQLException

open class BaseViewModel: ViewModel() {
    internal val api = RetrofitClient.api
    internal var db: SQLiteDatabase? = null

    fun setAccessToken(token: String) {
        accessToken.value = token

        if (checkDBInstance()) {
            try {
                db!!.execSQL("""insert into preferences (id, p_key, p_val) values (1, 'access_token', '${accessToken.value}') on conflict do update set p_val=excluded.p_val""")
                Log.i("viewModel_setAccessToken", "access_token has been inserted into preferences table")
                return
            }
            catch (e: SQLException) {
                Log.e("viewModel_setAccessToken", "setAccessToken: ${e.message}", )
            }

        }
        Log.e("viewModel_setAccessToken", "access_token has not been inserted into preferences table, database instance is null.")
    }

    fun initAccessToken() {
        if (!checkDBInstance()) {
            Log.w("viewmodel_initAccessToken", "initAccessToken: Can not init access token. No Database instance")
            return
        }
        var accessTokenData: String? = null

        val cursor = db?.rawQuery("select p_val from preferences where id = 1", null)
        if (cursor?.moveToFirst() == true) {
            do {
                accessTokenData = cursor.getString(0)
            } while (cursor.moveToNext())
        }
        cursor?.close()

        if (accessTokenData != null) {
            setAccessToken(accessTokenData)
        }
    }



    fun checkDBInstance(): Boolean {
        return db != null
    }

    fun generateSessionHeader(): String {
        return "dssession=${accessToken.value}"
    }

    fun initializeDatabase(context: Context) {
        db = DatabaseHelper.newInstance(context).writableDatabase
    }

    companion object {
        val accessToken =  MutableLiveData<String?>(null)
    }
}