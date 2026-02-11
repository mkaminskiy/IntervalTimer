package com.example.intervaltimer.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.intervaltimer.MainActivity
import com.example.intervaltimer.R

class NotificationHelper(private val context: Context) {

    companion object {
        const val TIMER_CHANNEL_ID = "timer_channel"
        const val INTERVAL_CHANNEL_ID = "interval_channel"
        const val FOREGROUND_NOTIFICATION_ID = 1
        private var intervalNotificationId = 100
    }

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val timerChannel = NotificationChannel(
            TIMER_CHANNEL_ID,
            context.getString(R.string.timer_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.timer_channel_description)
            setShowBadge(true)
        }

        val intervalChannel = NotificationChannel(
            INTERVAL_CHANNEL_ID,
            context.getString(R.string.interval_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.interval_channel_description)
            enableVibration(true)
        }

        notificationManager.createNotificationChannel(timerChannel)
        notificationManager.createNotificationChannel(intervalChannel)
    }

    fun createForegroundNotification(remainingTime: String): android.app.Notification {
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, TIMER_CHANNEL_ID)
            .setContentTitle(context.getString(R.string.foreground_title))
            .setContentText(context.getString(R.string.foreground_remaining, remainingTime))
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setSilent(true)
            .build()
    }

    fun updateForegroundNotification(remainingTime: String) {
        val notification = createForegroundNotification(remainingTime)
        notificationManager.notify(FOREGROUND_NOTIFICATION_ID, notification)
    }

    fun showIntervalNotification(intervalNumber: Int, totalIntervals: Int) {
        val notification = NotificationCompat.Builder(context, INTERVAL_CHANNEL_ID)
            .setContentTitle(context.getString(R.string.interval_title, intervalNumber, totalIntervals))
            .setContentText(context.getString(R.string.interval_text))
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .setDefaults(NotificationCompat.DEFAULT_SOUND)
            .setLocalOnly(false)  // Allow bridging to wearables
            .build()

        notificationManager.notify(intervalNotificationId++, notification)
    }

    fun showFinishNotification() {
        val notification = NotificationCompat.Builder(context, INTERVAL_CHANNEL_ID)
            .setContentTitle(context.getString(R.string.finish_title))
            .setContentText(context.getString(R.string.finish_text))
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setVibrate(longArrayOf(0, 1000, 200, 1000))
            .setDefaults(NotificationCompat.DEFAULT_SOUND)
            .setLocalOnly(false)  // Allow bridging to wearables
            .build()

        notificationManager.notify(intervalNotificationId++, notification)
    }

    fun cancelForegroundNotification() {
        notificationManager.cancel(FOREGROUND_NOTIFICATION_ID)
    }
}
