package com.example.newbegdinnings

import android.app.Service
import android.content.Intent
import android.os.IBinder

class ReminderService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // your reminder code
        return START_NOT_STICKY
    }
}