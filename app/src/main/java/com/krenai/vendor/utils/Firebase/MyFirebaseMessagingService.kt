package com.krenai.vendor.utils.Firebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.krenai.vendor.Activity.MainActivity
import com.krenai.vendor.R
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class MyFirebaseMessagingService : FirebaseMessagingService() {
    private var sharedPreferencesStatus: SharedPreferences? = null
    private var userid:String? = ""
    private var token:String? = ""
    override fun onNewToken(s: String) {
        sharedPreferencesStatus = getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferencesStatus!!.edit()
        editor.putString("FCM",s)
        editor.apply()
        super.onNewToken(s)
        Log.i("NEW_TOKEN", s)
        //sendToken(s)
    }
    override fun onMessageReceived(remoteMessage: RemoteMessage)
    {
        super.onMessageReceived(remoteMessage)
        Log.d("msg", "onMessageReceived: " + remoteMessage.data["body"])
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val intent2 = Intent(this,NotificationCancelReceiver::class.java)
        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
     val fullScreenIntent = Intent(this, MainActivity::class.java)
     val fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
        fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val  dismissIntent:PendingIntent = PendingIntent.getActivity(this, 0, intent2, PendingIntent.FLAG_CANCEL_CURRENT)
        val declineAction =
            NotificationCompat.Action(
                android.R.drawable.ic_menu_call,
                "DECLINE",
                dismissIntent
            )
        val acceptAction =
            NotificationCompat.Action(
                android.R.drawable.ic_menu_call,
                "ACCEPT",
                pendingIntent
            )
        val channelId = "Default"
        val builder =
            NotificationCompat.Builder(this, channelId)
                .setContentTitle(remoteMessage.data["title"])
                .setContentText(remoteMessage.data["body"]).setAutoCancel(true)
                     .setDefaults(Notification.DEFAULT_SOUND)
                   .setFullScreenIntent(fullScreenPendingIntent, true)
                     .addAction(acceptAction)
                     .addAction(declineAction)
                .setAutoCancel(true)

        val manager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
            builder.setColor(ContextCompat.getColor(this, R.color.colorAccent))
            builder.setSmallIcon(R.drawable.logo_notification)
        }
        else
        {
            builder.setSmallIcon(R.drawable.logo_wb)
            NotificationManager.IMPORTANCE_HIGH
        }
       // val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        //val mp: MediaPlayer = MediaPlayer.create(applicationContext, alarmSound)
        //mp.start()
        manager.notify(0, builder.build())
        }



        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

    /*private fun scheduleJob() {
        // [START dispatch_job]
        val work = OneTimeWorkRequest.Builder(MainActivity::class.java).build()
        WorkManager.getInstance().beginWith(work).enqueue()
        // [END dispatch_job]
    }*/

    /**
     * Handle time allotted to BroadcastReceivers.
     */

}