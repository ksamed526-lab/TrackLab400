package com.tracklab400.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.tracklab400.app.ui.theme.TrackLabSpacing

@Composable
fun TrackLabCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    contentPadding: PaddingValues = PaddingValues(TrackLabSpacing.md),
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = MaterialTheme.shapes.large
    val colors = CardDefaults.cardColors(
        containerColor = containerColor,
        contentColor = contentColor,
    )
    val innerContent: @Composable ColumnScope.() -> Unit = {
        Column(Modifier.padding(contentPadding)) {
            content()
        }
    }
    val clickHandler = onClick
    if (clickHandler != null) {
        Card(
            onClick = clickHandler,
            modifier = modifier,
            shape = shape,
            colors = colors,
            content = innerContent,
        )
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            colors = colors,
            content = innerContent,
        )
    }
}
