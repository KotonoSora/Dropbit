package com.jn.dropbit.features.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jn.dropbit.domain.model.ALL_SKINS
import com.jn.dropbit.features.common.ui.DropbitScreen
import com.jn.dropbit.features.common.ui.HeaderBar
import com.jn.dropbit.features.settings.SettingsIntent
import com.jn.dropbit.features.settings.SettingsUIState
import com.jn.dropbit.features.settings.SettingsViewModel
import com.jn.dropbit.ui.theme.DropbitTheme
import com.jn.dropbit.ui.theme.NeonBlue
import com.jn.dropbit.ui.theme.NeonRed
import com.jn.dropbit.ui.theme.neonBorder

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsState()
    SettingsScreenContent(
        state = state,
        onToggleSound = { viewModel.onIntent(SettingsIntent.ToggleSound) },
        onSelectSkin = { name -> viewModel.onIntent(SettingsIntent.SelectSkin(name)) },
        onBack = onBack
    )
}

@Composable
fun SettingsScreenContent(
    state: SettingsUIState,
    onToggleSound: () -> Unit,
    onSelectSkin: (String) -> Unit,
    onBack: () -> Unit
) {
    DropbitScreen {
        HeaderBar(
            coins = state.coins,
            title = "SETTINGS",
            onBackClick = onBack
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Spacer(Modifier.height(32.dp))

            SettingsToggle("Sound Effects", state.settings.soundEnabled, onToggleSound)

            Spacer(Modifier.height(24.dp))

            Text(
                "Select Skin",
                color = Color.White.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ALL_SKINS.forEach { skin ->
                    val isSelected = state.selectedSkin == skin.name
                    SkinOption(
                        color = skin.color,
                        isSelected = isSelected,
                        onClick = { onSelectSkin(skin.name) }
                    )
                }
            }

            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
fun SkinOption(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .then(
                if (isSelected) {
                    Modifier.neonBorder(
                        color = NeonRed,
                        width = 2.dp,
                        glowRadius = 4.dp,
                        cornerRadius = 24.dp
                    )
                } else Modifier
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(color, CircleShape)
                .then(
                    if (isSelected) {
                        Modifier.border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                    } else Modifier
                )
        )
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
            onSelectSkin = {}
        ) {
            // onBack
        }
    }
}
