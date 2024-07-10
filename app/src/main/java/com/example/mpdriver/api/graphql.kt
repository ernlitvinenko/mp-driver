package com.example.mpdriver.api
import androidx.compose.ui.platform.LocalContext
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.http.DefaultHttpEngine
import com.example.mpdriver.FetchApplicationDataQuery
import com.example.mpdriver.storage.Database
import com.tencent.mmkv.MMKV


val apolloClient = ApolloClient.Builder().serverUrl("http://192.168.0.101:8000/graphql").httpEngine(DefaultHttpEngine(timeoutMillis = 60000)).build()


suspend fun ApolloClient.fetchAppDataToDB(): Unit {
    val kv = Database.kv

    val req =  this.query(FetchApplicationDataQuery("1125904232173609")).execute().data

    req?.let {
        for (task in req.tasks) {
            kv.encode("task:${task.id}", task.toJson())
        }
        for (subtask in req.subtasks) {
            kv.encode("subtask:${subtask.id}", subtask.toJson())
        }
        for (note in req.notes) {
            kv.encode("note:${note.id}", note.toJson())
        }
    }

}

