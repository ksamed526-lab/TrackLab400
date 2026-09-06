package com.tracklab400.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TrackLabSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    min: Float,
    max: Float,
    steps: Int,
    label: @Composable (Float) -> String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = min..max,
            steps = steps,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = label(value),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
        )
    }
}