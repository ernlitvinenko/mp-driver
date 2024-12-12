package com.example.mpdriver.data.database

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DatabaseHelper(private val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("""
            create table if not exists preferences (
                id integer primary key autoincrement not null,
                p_key text not null,
                p_val text not null
            );
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
//        TODO("Not yet implemented")
    }


    companion object {
        private const val DATABASE_NAME: String = "mp_driver_v1.db"
        private const val DATABASE_VERSION: Int = 1
        private var instance: DatabaseHelper? = null

        fun newInstance(context: Context): DatabaseHelper {
            if (instance == null) {
                instance = DatabaseHelper(context)
            }
            return instance as DatabaseHelper
        }

    }
}