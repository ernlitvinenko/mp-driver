package com.example.mpdriver.recievers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlin.random.Random


class TimeTickReciever: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_TIME_TICK) {
            handlers.forEach {
                it.value()
            }
        }
    }

    companion object {
        var handlers = mutableMapOf<Int, () -> Unit>()
        fun registerHandler(cb: () -> Unit): Int {
            handlers[cb.hashCode()] = cb
            return cb.hashCode()
        }

        fun unregisterHandler(hashCode: Int) {
            handlers.remove(hashCode)
        }
    }

}