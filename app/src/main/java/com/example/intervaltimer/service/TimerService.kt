package com.example.intervaltimer.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import com.example.intervaltimer.notification.NotificationHelper
import kotlin.math.ceil

private const val TAG = "TimerService"

class TimerService : Service() {

    companion object {
        const val ACTION_TICK = "com.example.intervaltimer.TICK"
        const val ACTION_FINISH = "com.example.intervaltimer.FINISH"
        const val EXTRA_REMAINING_TIME = "remaining_time"
        const val EXTRA_TOTAL_TIME = "total_time"
        const val EXTRA_INTERVALS = "intervals"
    }

    private val binder = TimerBinder()
    private var countDownTimer: CountDownTimer? = null
    private lateinit var notificationHelper: NotificationHelper

    private var totalTimeMillis: Long = 0
    private var numberOfIntervals: Int = 1
    private var intervalDurationSeconds: Long = 0
    private var notificationThresholds = mutableListOf<Long>()  // remaining seconds when to notify
    private var previousDisplaySeconds: Long = -1
    private var isRunning = false

    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onCreate() {
        super.onCreate()
        notificationHelper = NotificationHelper(this)
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            totalTimeMillis = it.getLongExtra(EXTRA_TOTAL_TIME, 0)
            numberOfIntervals = it.getIntExtra(EXTRA_INTERVALS, 1)

            if (totalTimeMillis > 0 && numberOfIntervals > 0) {
                startTimer()
            }
        }
        return START_NOT_STICKY
    }

    private fun startTimer() {
        val totalSeconds = totalTimeMillis / 1000
        intervalDurationSeconds = totalSeconds / numberOfIntervals
        previousDisplaySeconds = -1
        isRunning = true

        // Build notification thresholds: for 10sec/2intervals -> notify at 5sec and 0sec
        notificationThresholds.clear()
        for (i in 1..numberOfIntervals) {
            val threshold = totalSeconds - (i * intervalDurationSeconds)
            notificationThresholds.add(threshold)
        }
        Log.d(TAG, "Starting timer: total=${totalSeconds}s, intervals=$numberOfIntervals, thresholds=$notificationThresholds")

        val initialNotification = notificationHelper.createForegroundNotification(formatTime(totalTimeMillis))
        startForeground(NotificationHelper.FOREGROUND_NOTIFICATION_ID, initialNotification)

        countDownTimer = object : CountDownTimer(totalTimeMillis, 50) {
            override fun onTick(millisUntilFinished: Long) {
                // Use ceiling so 4999ms shows as 5sec, 4001ms shows as 5sec, 4000ms shows as 4sec
                val displaySeconds = ceil(millisUntilFinished / 1000.0).toLong()

                notificationHelper.updateForegroundNotification(formatTime(displaySeconds * 1000))

                // Check if we crossed a threshold
                if (displaySeconds != previousDisplaySeconds && notificationThresholds.isNotEmpty()) {
                    if (displaySeconds == notificationThresholds.first()) {
                        val intervalNum = numberOfIntervals - notificationThresholds.size + 1
                        Log.d(TAG, "Interval $intervalNum notification at ${displaySeconds}s remaining")
                        notificationHelper.showIntervalNotification(intervalNum, numberOfIntervals)
                        notificationThresholds.removeAt(0)
                    }
                }
                previousDisplaySeconds = displaySeconds

                // Broadcast tick for UI update (use display seconds for consistency)
                val tickIntent = Intent(ACTION_TICK).apply {
                    putExtra(EXTRA_REMAINING_TIME, displaySeconds * 1000)
                    setPackage(packageName)
                }
                sendBroadcast(tickIntent)
            }

            override fun onFinish() {
                Log.d(TAG, "Timer finished!")
                isRunning = false
                notificationHelper.showFinishNotification()
                notificationHelper.cancelForegroundNotification()

                val finishIntent = Intent(ACTION_FINISH).apply {
                    setPackage(packageName)
                }
                sendBroadcast(finishIntent)

                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }.start()
    }

    fun stopTimer() {
        countDownTimer?.cancel()
        countDownTimer = null
        isRunning = false
        notificationHelper.cancelForegroundNotification()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    fun isTimerRunning(): Boolean = isRunning

    private fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        countDownTimer?.cancel()
        super.onDestroy()
    }
}
