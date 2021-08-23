package com.krenai.vendor.utils.Firebase

import android.app.Activity
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import androidx.core.content.ContextCompat.getSystemService


class NotificationCancelReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        //Cancel your ongoing Notification
        val notificationManager =context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(0)
    }

}