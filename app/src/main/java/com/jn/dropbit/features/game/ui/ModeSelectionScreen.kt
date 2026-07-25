package com.jn.dropbit.features.game.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jn.dropbit.domain.model.GameMode
import com.jn.dropbit.domain.model.INITIAL_COINS
import com.jn.dropbit.features.common.ui.DropbitScreen
import com.jn.dropbit.features.common.ui.HeaderBar
import com.jn.dropbit.features.common.ui.MenuButton
import com.jn.dropbit.ui.theme.DropbitTheme
import com.jn.dropbit.ui.theme.NeonGreen
import com.jn.dropbit.ui.theme.NeonPink
import com.jn.dropbit.ui.theme.NeonPurple

@Composable
fun ModeSelectionScreen(
    coins: Int = INITIAL_COINS,
    onModeSelected: (GameMode) -> Unit,
    onBack: () -> Unit,
) {
    DropbitScreen {
        HeaderBar(
            coins = coins,
            title = "SELECT MODE",
            onBackClick = onBack
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(48.dp))

            GameMode.entries.forEach { mode ->
                MenuButton(
                    text = mode.name.replace("_", " "),
                    icon = Icons.Default.PlayArrow,
                    color = when (mode) {
                        GameMode.CLASSIC -> NeonGreen
                        GameMode.TIME_ATTACK -> NeonPurple
                        GameMode.ENDLESS -> NeonPink
                    },
                ) {
                    onModeSelected(mode)
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ModeSelectionScreenPreview() {
    DropbitTheme {
        ModeSelectionScreen(
            coins = INITIAL_COINS,
            onModeSelected = {},
            onBack = {}
        )
    }
}
