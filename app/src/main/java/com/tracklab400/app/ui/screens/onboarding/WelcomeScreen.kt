package com.tracklab400.app.ui.screens.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.tracklab400.app.R
import com.tracklab400.app.ui.components.TrackLabBrandLockup
import com.tracklab400.app.ui.components.TrackLabButton
import com.tracklab400.app.ui.components.TrackLabOutlinedButton
import com.tracklab400.app.ui.theme.TrackLabSpacing
import com.tracklab400.app.ui.theme.TrackLabTheme
import com.tracklab400.app.ui.theme.trackLabColors
import com.tracklab400.app.ui.screens.profile.ProfileEvent
import kotlinx.coroutines.flow.Flow

@Composable
fun WelcomeScreen(
    events: Flow<ProfileEvent>,
    onCreateProfile: () -> Unit,
    onTryDemo: () -> Unit,
    onDemoApplied: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) {
        events.collect { event ->
            if (event == ProfileEvent.DemoApplied) onDemoApplied()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(TrackLabSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(TrackLabSpacing.xxl))
        TrackLabBrandLockup()
        Spacer(Modifier.height(TrackLabSpacing.lg))
        Text(
            text = stringResource(R.string.welcome_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(TrackLabSpacing.sm))
        Text(
            text = stringResource(R.string.welcome_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(TrackLabSpacing.lg))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.trackLabColors.info,
            )
            Spacer(Modifier.height(TrackLabSpacing.xs))
            Text(
                text = stringResource(R.string.welcome_truth_note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(Modifier.weight(1f))
        TrackLabButton(
            text = stringResource(R.string.welcome_create_profile),
            onClick = onCreateProfile,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(TrackLabSpacing.sm))
        TrackLabOutlinedButton(
            text = stringResource(R.string.welcome_demo),
            onClick = onTryDemo,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(TrackLabSpacing.md))
    }
}

@Preview(showBackground = true)
@Composable
private fun WelcomeScreenPreview() {
    TrackLabTheme {
        WelcomeScreen(
            events = kotlinx.coroutines.flow.emptyFlow(),
            onCreateProfile = {},
            onTryDemo = {},
            onDemoApplied = {},
        )
    }
}
