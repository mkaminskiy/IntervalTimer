package com.example.intervaltimer.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.shawnlin.numberpicker.NumberPicker as ShawnNumberPicker

@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    modifier: Modifier = Modifier,
    label: String = ""
) {
    val primaryColor = MaterialTheme.colorScheme.primary.toArgb()
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface.toArgb()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        AndroidView(
            modifier = Modifier.height(150.dp),
            factory = { context ->
                ShawnNumberPicker(context).apply {
                    minValue = range.first
                    maxValue = range.last
                    this.value = value
                    setSelectedTextColor(primaryColor)
                    textColor = onSurfaceColor
                    selectedTextSize = 80f
                    textSize = 50f
                    wheelItemCount = 3
                    setFormatter { String.format("%02d", it) }
                    setOnValueChangedListener { _, _, newVal ->
                        onValueChange(newVal)
                    }
                }
            },
            update = { picker ->
                if (picker.value != value) {
                    picker.value = value
                }
            }
        )
    }
}
