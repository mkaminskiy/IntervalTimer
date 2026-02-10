package com.example.intervaltimer.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    modifier: Modifier = Modifier,
    label: String = ""
) {
    val items = range.toList()
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = value - range.first)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val index = listState.firstVisibleItemIndex
            val offset = listState.firstVisibleItemScrollOffset
            val selectedIndex = if (offset > 25) index + 1 else index
            val newValue = (range.first + selectedIndex).coerceIn(range)
            if (newValue != value) {
                onValueChange(newValue)
            }
        }
    }

    LaunchedEffect(value) {
        val targetIndex = value - range.first
        if (listState.firstVisibleItemIndex != targetIndex) {
            listState.scrollToItem(targetIndex)
        }
    }

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

        Box(
            modifier = Modifier.height(150.dp),
            contentAlignment = Alignment.Center
        ) {
            // Selection indicator
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-25).dp),
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = 25.dp),
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )

            LazyColumn(
                state = listState,
                flingBehavior = flingBehavior,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 50.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(items.size) { index ->
                    val itemValue = items[index]
                    val isSelected = itemValue == value
                    Text(
                        text = String.format("%02d", itemValue),
                        fontSize = if (isSelected) 32.sp else 24.sp,
                        textAlign = TextAlign.Center,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .height(50.dp)
                            .wrapContentHeight(Alignment.CenterVertically)
                            .alpha(if (isSelected) 1f else 0.5f)
                    )
                }
            }
        }
    }
}
