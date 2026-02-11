package com.example.intervaltimer.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.intervaltimer.R
import kotlin.math.ceil

@Composable
fun TimerScreen(
    remainingTimeMillis: Long,
    isRunning: Boolean,
    onStart: (minutes: Int, seconds: Int, intervals: Int) -> Unit,
    onStop: () -> Unit
) {
    val prefs = LocalContext.current.getSharedPreferences("timer_settings", Context.MODE_PRIVATE)
    var minutes by remember { mutableIntStateOf(prefs.getInt("minutes", 1)) }
    var seconds by remember { mutableIntStateOf(prefs.getInt("seconds", 0)) }
    var intervals by remember { mutableIntStateOf(prefs.getInt("intervals", 2)) }

    if (isRunning) {
        // Timer running screen
        RunningTimerScreen(
            remainingTimeMillis = remainingTimeMillis,
            onStop = onStop
        )
    } else {
        // Setup screen
        SetupScreen(
            minutes = minutes,
            onMinutesChange = { minutes = it; prefs.edit().putInt("minutes", it).apply() },
            seconds = seconds,
            onSecondsChange = { seconds = it; prefs.edit().putInt("seconds", it).apply() },
            intervals = intervals,
            onIntervalsChange = { intervals = it; prefs.edit().putInt("intervals", it).apply() },
            onStart = { onStart(minutes, seconds, intervals) }
        )
    }
}

@Composable
private fun SetupScreen(
    minutes: Int,
    onMinutesChange: (Int) -> Unit,
    seconds: Int,
    onSecondsChange: (Int) -> Unit,
    intervals: Int,
    onIntervalsChange: (Int) -> Unit,
    onStart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.set_timer_title),
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Time pickers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NumberPicker(
                value = minutes,
                onValueChange = onMinutesChange,
                range = 0..59,
                label = stringResource(R.string.minutes_label),
                modifier = Modifier.weight(1f)
            )

            Text(
                text = ":",
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            NumberPicker(
                value = seconds,
                onValueChange = onSecondsChange,
                range = 0..59,
                label = stringResource(R.string.seconds_label),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Intervals picker
        NumberPicker(
            value = intervals,
            onValueChange = onIntervalsChange,
            range = 1..10,
            label = stringResource(R.string.intervals_label),
            modifier = Modifier.fillMaxWidth(),
            horizontal = true,
            formatter = { it.toString() }
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            enabled = minutes > 0 || seconds > 0
        ) {
            Text(stringResource(R.string.start_button), fontSize = 28.sp)
        }
    }
}

@Composable
private fun RunningTimerScreen(
    remainingTimeMillis: Long,
    onStop: () -> Unit
) {
    val displaySeconds = ceil(remainingTimeMillis / 1000.0).toLong()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Large timer display
        Text(
            text = formatTime(displaySeconds * 1000),
            fontSize = 96.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(64.dp))

        // Stop button
        Button(
            onClick = onStop,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text(stringResource(R.string.stop_button), fontSize = 18.sp)
        }
    }
}

private fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
