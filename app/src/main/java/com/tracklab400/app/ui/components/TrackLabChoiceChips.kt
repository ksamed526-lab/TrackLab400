package com.tracklab400.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tracklab400.app.ui.theme.TrackLabSpacing

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> TrackLabChoiceChips(
    options: List<T>,
    optionLabel: @Composable (T) -> String,
    isSelected: (T) -> Boolean,
    onToggle: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(TrackLabSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(TrackLabSpacing.xs),
    ) {
        options.forEach { option ->
            FilterChip(
                selected = isSelected(option),
                onClick = { onToggle(option) },
                label = { Text(optionLabel(option)) },
            )
        }
    }
}
