package com.example.mpdriver.data.database

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

private sealed class Version(val upgrade: String, val downgrade: String?) {
    data object INITIAL : Version(
        """
                    create table if not exists preferences (
                        id integer primary key autoincrement not null,
                        p_key text not null,
                        p_val text not null
                    );
    """.trimIndent(), null
    )

    data object VERSION_2 : Version(
        upgrade = """
create table if not exists app_task
(
    id        integer primary key,
    parentId  integer,
    startPln  TEXT    not null,
    endPln    text    not null,
    startFact text,
    endFact   text,
    status    integer not null,
    taskType  integer not null,
    text      text,

    route     integer,
    station integer
);

create table if not exists app_event(
    id integer primary key,
    id_rec integer,
    vid integer,
    tip integer,
    dt text,
    text text,
    data text
);

create table if not exists app_mst (
    id integer primary key,
    name text,
    lat real not null,
    lon real not null
);

create table if not exists app_route
(
    id                  integer primary key,
    temperatureProperty integer not null,
    name                text    not null,
    trailer             integer,
    truck               integer
);

create table if not exists app_trs
(
    id   integer primary key,
    gost text
);       """.trimIndent(), downgrade = """
    drop table if exists app_mst;
    drop table if exists app_route;
    drop table if exists app_task;
    drop table if exists app_trs;
    drop table if exists app_event;
""".trimIndent()
    )
}

private enum class MutationVariants {
    UPGRADE,
    DOWNGRADE
}

class DatabaseHelper(private val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(Version.INITIAL.upgrade)
        for (sql in Version.VERSION_2.upgrade.split(";")) {
            var formattedSql = sql.trimIndent()
            if (formattedSql.length == 0 ) {
                return
            }
            formattedSql += ";"
            db?.execSQL(formattedSql)
        }
//        executeVersion(db, version = Version.INITIAL, variant = MutationVariants.UPGRADE)
//        executeVersion(db, version = Version.VERSION_2, variant = MutationVariants.UPGRADE)
    }

    private fun executeMultipleStatements(db: SQLiteDatabase?, stmt: String) {
        db?.beginTransaction()
        try {
            stmt.split(";").forEach { sql ->
                var formattedSql = sql.trimIndent()
                if (formattedSql.length == 0 ) {
                    return
                }
                formattedSql += ";"
                Log.d("DatabaseHelper", "executeMultipleStatements: $formattedSql")
                db?.execSQL(formattedSql)
            }
            db?.setTransactionSuccessful()
            Log.i("DatabaseHelper", "Transaction completed successfully")
        }
        catch (e : Exception) {
            Log.d("DatabaseHelper", "executeMultipleStatements Exception: ${e.message}")
        }
        finally {
            db?.endTransaction()
        }
    }

    private fun executeVersion(db: SQLiteDatabase?, version: Version, variant: MutationVariants) {
        when (variant) {
            MutationVariants.UPGRADE -> executeMultipleStatements(db, version.upgrade)
            MutationVariants.DOWNGRADE -> {
                version.downgrade?.let {
                    executeMultipleStatements(db, version.downgrade)
                }
            }
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val versions = listOf(Version.INITIAL, Version.VERSION_2)

        if (newVersion < oldVersion) {
            (newVersion ..<  oldVersion).toList().asReversed().forEach { verNum ->
                executeVersion(db, versions[verNum - 1], MutationVariants.DOWNGRADE)
            }

            return
        }
        (oldVersion + 1 .. newVersion).forEach {verNum ->
            executeVersion(db, versions[verNum - 1], MutationVariants.UPGRADE)
        }
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }


    companion object {
        private const val DATABASE_NAME: String = "mp_driver_v1.db"
        private const val DATABASE_VERSION: Int = 2
        private var instance: DatabaseHelper? = null

        fun newInstance(context: Context): DatabaseHelper {
            if (instance == null) {
                instance = DatabaseHelper(context)
            }
            return instance as DatabaseHelper
        }

    }
}