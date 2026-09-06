package com.tracklab400.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.tracklab400.app.R
import com.tracklab400.app.ui.components.TrackLabEmpty
import com.tracklab400.app.ui.theme.TrackLabSpacing

@Composable
fun PlaceholderScreen(
    title: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(TrackLabSpacing.md),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            TrackLabEmpty(
                title = stringResource(R.string.placeholder_coming_soon),
                description = stringResource(R.string.placeholder_coming_soon_desc),
                icon = Icons.Default.Build,
            )
        }
    }
}
