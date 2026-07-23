package com.jn.dropbit.features.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jn.dropbit.features.common.ui.DropbitScreen
import com.jn.dropbit.features.common.ui.HeaderBar
import com.jn.dropbit.features.settings.SettingsIntent
import com.jn.dropbit.features.settings.SettingsUIState
import com.jn.dropbit.features.settings.SettingsViewModel
import com.jn.dropbit.ui.theme.DropbitTheme
import com.jn.dropbit.ui.theme.NeonBlue
import com.jn.dropbit.ui.theme.ensureContrast

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsState()
    SettingsScreenContent(
        state = state,
        onToggleSound = { viewModel.onIntent(SettingsIntent.ToggleSound) },
        onToggleNotifications = { viewModel.onIntent(SettingsIntent.ToggleNotifications) },
        onBack = onBack
    )
}

@Composable
fun SettingsScreenContent(
    state: SettingsUIState,
    onToggleSound: () -> Unit,
    onToggleNotifications: () -> Unit,
    onBack: () -> Unit
) {
    DropbitScreen {
        HeaderBar(coins = state.coins)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Text(
                "SETTINGS",
                style = MaterialTheme.typography.headlineMedium,
                color = NeonBlue.ensureContrast()
            )
            Spacer(Modifier.height(32.dp))

            SettingsToggle("Sound Effects", state.settings.soundEnabled, onToggleSound)
            SettingsToggle(
                "Notifications",
                state.settings.notificationsEnabled,
                onToggleNotifications
            )

            Spacer(Modifier.weight(1f))
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = NeonBlue
                )
            }
        }
    }
}

@Composable
fun SettingsToggle(label: String, enabled: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            color = Color.White.copy(alpha = 0.9f),
            style = MaterialTheme.typography.bodyLarge
        )
        Switch(
            checked = enabled,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = NeonBlue,
                checkedTrackColor = NeonBlue.copy(alpha = 0.5f)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    DropbitTheme {
        SettingsScreenContent(
            state = SettingsUIState(),
            onToggleSound = {},
            onToggleNotifications = {}
        ) {
            // onBack
        }
    }
}
