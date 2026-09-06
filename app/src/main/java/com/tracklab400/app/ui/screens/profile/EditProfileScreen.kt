package com.tracklab400.app.ui.screens.profile

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.tracklab400.app.R
import com.tracklab400.app.ui.components.TrackLabTag
import com.tracklab400.app.ui.components.TrackLabTextButton
import com.tracklab400.app.ui.theme.TrackLabSpacing
import com.tracklab400.app.ui.theme.trackLabColors
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    formState: ProfileFormState,
    events: Flow<ProfileEvent>,
    callbacks: ProfileFormCallbacks,
    isDemo: Boolean,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onDeleted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                ProfileEvent.ProfileSaved -> onBack()
                ProfileEvent.ProfileDeleted -> onDeleted()
                ProfileEvent.DemoApplied -> Unit
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(R.string.form_title_edit))
                        if (isDemo) {
                            Spacer(Modifier.width(TrackLabSpacing.sm))
                            TrackLabTag(
                                text = stringResource(R.string.demo_badge),
                                containerColor = MaterialTheme.trackLabColors.warning,
                                contentColor = MaterialTheme.colorScheme.onTertiary,
                            )
                        }
                    }
                },
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
        ProfileFormContent(
            formState = formState,
            callbacks = callbacks,
            modifier = Modifier.padding(innerPadding),
            footer = {
                TrackLabTextButton(
                    text = stringResource(R.string.action_delete_profile),
                    onClick = { showDeleteDialog = true },
                )
            },
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_profile_title)) },
            text = { Text(stringResource(R.string.delete_profile_desc)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    },
                ) {
                    Text(stringResource(R.string.action_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }
}
