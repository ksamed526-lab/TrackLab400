package com.tracklab400.app.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tracklab400.app.R
import com.tracklab400.app.data.model.ThemeMode
import com.tracklab400.app.ui.components.TrackLabCard
import com.tracklab400.app.ui.components.TrackLabSectionHeader
import com.tracklab400.app.ui.theme.TrackLabSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceScreen(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_appearance)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(TrackLabSpacing.md),
        ) {
            TrackLabSectionHeader(title = stringResource(R.string.appearance_theme_section))
            Spacer(Modifier.height(TrackLabSpacing.sm))
            ThemeOptionRow(
                icon = Icons.Outlined.PhoneAndroid,
                title = stringResource(R.string.appearance_system),
                description = stringResource(R.string.appearance_system_desc),
                selected = themeMode == ThemeMode.SYSTEM,
                onClick = { onThemeModeChange(ThemeMode.SYSTEM) },
            )
            Spacer(Modifier.height(TrackLabSpacing.sm))
            ThemeOptionRow(
                icon = Icons.Outlined.LightMode,
                title = stringResource(R.string.appearance_light),
                description = stringResource(R.string.appearance_light_desc),
                selected = themeMode == ThemeMode.LIGHT,
                onClick = { onThemeModeChange(ThemeMode.LIGHT) },
            )
            Spacer(Modifier.height(TrackLabSpacing.sm))
            ThemeOptionRow(
                icon = Icons.Default.DarkMode,
                title = stringResource(R.string.appearance_dark),
                description = stringResource(R.string.appearance_dark_desc),
                selected = themeMode == ThemeMode.DARK,
                onClick = { onThemeModeChange(ThemeMode.DARK) },
            )
            Spacer(Modifier.height(TrackLabSpacing.sm))
            ThemeOptionRow(
                icon = Icons.Default.Favorite,
                title = stringResource(R.string.appearance_pink),
                description = stringResource(R.string.appearance_pink_desc),
                selected = themeMode == ThemeMode.PINK,
                onClick = { onThemeModeChange(ThemeMode.PINK) },
            )
        }
    }
}

@Composable
private fun ThemeOptionRow(
    icon: ImageVector,
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    TrackLabCard(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(20.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(Modifier.width(TrackLabSpacing.md))
            Column(Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}