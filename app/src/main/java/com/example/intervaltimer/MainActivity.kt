package com.example.intervaltimer

import android.Manifest
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.intervaltimer.service.TimerService
import com.example.intervaltimer.ui.TimerScreen
import com.example.intervaltimer.ui.theme.IntervalTimerTheme

class MainActivity : ComponentActivity() {

    private var timerService: TimerService? = null
    private var isBound = false

    private var remainingTime by mutableLongStateOf(0L)
    private var isTimerRunning by mutableStateOf(false)

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as TimerService.TimerBinder
            timerService = binder.getService()
            isBound = true
            isTimerRunning = timerService?.isTimerRunning() == true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            timerService = null
            isBound = false
        }
    }

    private val tickReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                TimerService.ACTION_TICK -> {
                    remainingTime = intent.getLongExtra(TimerService.EXTRA_REMAINING_TIME, 0L)
                }
                TimerService.ACTION_FINISH -> {
                    remainingTime = 0L
                    isTimerRunning = false
                }
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestNotificationPermission()

        setContent {
            IntervalTimerTheme {
                TimerScreen(
                    remainingTimeMillis = remainingTime,
                    isRunning = isTimerRunning,
                    onStart = { minutes, seconds, intervals ->
                        startTimer(minutes, seconds, intervals)
                    },
                    onStop = {
                        stopTimer()
                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, TimerService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        val filter = IntentFilter().apply {
            addAction(TimerService.ACTION_TICK)
            addAction(TimerService.ACTION_FINISH)
        }
        ContextCompat.registerReceiver(
            this,
            tickReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
        unregisterReceiver(tickReceiver)
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED -> {
                    // Permission granted, check if notifications are enabled
                    if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                        openNotificationSettings()
                    }
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun openNotificationSettings() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        }
        startActivity(intent)
    }

    private fun startTimer(minutes: Int, seconds: Int, intervals: Int) {
        val totalTimeMillis = ((minutes * 60L) + seconds) * 1000L
        remainingTime = totalTimeMillis
        isTimerRunning = true

        val intent = Intent(this, TimerService::class.java).apply {
            putExtra(TimerService.EXTRA_TOTAL_TIME, totalTimeMillis)
            putExtra(TimerService.EXTRA_INTERVALS, intervals)
        }
        startForegroundService(intent)
    }

    private fun stopTimer() {
        timerService?.stopTimer()
        stopService(Intent(this, TimerService::class.java))
        remainingTime = 0L
        isTimerRunning = false
    }
}
