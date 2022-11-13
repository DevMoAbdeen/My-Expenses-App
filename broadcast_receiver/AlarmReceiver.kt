package com.msa.myexpenses.broadcast_receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.msa.myexpenses.R
import com.msa.myexpenses.activities.HomeActivity

class AlarmReceiver : BroadcastReceiver() {
    lateinit var notificationManager: NotificationManager
    val channelID = "111"
    var id = 1

    override fun onReceive(context: Context, intent: Intent) {
        showPushNotification(context)
    }

    private fun showPushNotification(context: Context) {
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createChannel(context)

        val intent = Intent(context, HomeActivity::class.java)
        val pIntent = PendingIntent.getActivity(context, 100, intent, 0)

        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.ic_splash)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_on))
            .setShowWhen(true)
            .setContentTitle("Good morning")
            .setContentText("Remember to write your expenses today")
            .setContentIntent(pIntent)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
        notificationManager.notify(id, notification)
    }

    private fun createChannel(context: Context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelID, "Daily remider", NotificationManager.IMPORTANCE_HIGH)
            channel.description = "You receive this notification every day at 7 oclock to remind you to write your expenses"
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
        }else{
            notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

    }
}