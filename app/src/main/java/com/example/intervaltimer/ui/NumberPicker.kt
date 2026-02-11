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
    label: String = "",
    horizontal: Boolean = false,
    formatter: ((Int) -> String)? = null
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
            modifier = if (horizontal) Modifier.height(80.dp) else Modifier.height(180.dp),
            factory = { context ->
                ShawnNumberPicker(context).apply {
                    minValue = range.first
                    maxValue = range.last
                    this.value = value
                    wrapSelectorWheel = false
                    setSelectedTextColor(primaryColor)
                    textColor = onSurfaceColor
                    selectedTextSize = 120f
                    textSize = 70f
                    wheelItemCount = 3
                    if (horizontal) {
                        orientation = ShawnNumberPicker.HORIZONTAL
                    }
                    setFormatter { v ->
                        formatter?.invoke(v) ?: String.format("%02d", v)
                    }
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
