package com.tracklab400.app.ui.screens.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.tracklab400.app.ui.components.TrackLabLoading
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Composable
fun StartRoute(
    onboardingCompleted: Flow<Boolean?>,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var completed by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(Unit) {
        completed = onboardingCompleted.first() ?: false
    }

    LaunchedEffect(completed) {
        when (completed) {
            true -> onNavigateToHome()
            false -> onNavigateToOnboarding()
            null -> Unit
        }
    }

    TrackLabLoading(modifier = modifier)
}
