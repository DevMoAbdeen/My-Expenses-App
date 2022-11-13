package com.msa.myexpenses.broadcast_receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import java.util.*

private const val HOUR_TO_SHOW_PUSH = 8
lateinit var alarmManager: AlarmManager
lateinit var alarmPendingIntent: PendingIntent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val sharedSetting = context.getSharedPreferences("SettingsApp", AppCompatActivity.MODE_PRIVATE)
        val notification = sharedSetting.getBoolean("notification", false)
        if(notification){
            alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
            val alarmPendingIntent2 by lazy {
                val intent = Intent(context, AlarmReceiver::class.java)
                PendingIntent.getBroadcast(context, 0, intent, 0)
            }
            alarmPendingIntent = alarmPendingIntent2
            schedulePushNotifications()
        }
    }

    fun schedulePushNotifications() {
        val calendar = GregorianCalendar.getInstance().apply {
            if (get(Calendar.HOUR_OF_DAY) >= HOUR_TO_SHOW_PUSH) {
                add(Calendar.DAY_OF_MONTH, 1)
            }

            set(Calendar.HOUR_OF_DAY, HOUR_TO_SHOW_PUSH)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            alarmPendingIntent
        )
    }

}