package com.jn.dropbit.features.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jn.dropbit.domain.model.INITIAL_COINS
import com.jn.dropbit.features.common.ui.DropbitScreen
import com.jn.dropbit.features.common.ui.HeaderBar
import com.jn.dropbit.features.common.ui.MenuButton
import com.jn.dropbit.features.common.ui.NeonText
import com.jn.dropbit.features.home.HomeState
import com.jn.dropbit.features.home.HomeViewModel
import com.jn.dropbit.ui.theme.DropbitTheme
import com.jn.dropbit.ui.theme.NeonBlue
import com.jn.dropbit.ui.theme.NeonGreen
import com.jn.dropbit.ui.theme.NeonOrange
import com.jn.dropbit.ui.theme.NeonPink
import com.jn.dropbit.ui.theme.NeonPurple

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigate: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    HomeScreenContent(
        state = state,
        onNavigate = onNavigate
    )
}

@Composable
fun HomeScreenContent(
    state: HomeState,
    onNavigate: (String) -> Unit
) {
    DropbitScreen {
        HeaderBar(
            coins = state.coins,
            title = "",
            showShopIcon = true,
            onShopClick = { onNavigate("shop") }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            NeonText(
                text = "DROPBIT",
                style = MaterialTheme.typography.displayMedium,
                color = NeonBlue,
                glowRadius = 12.dp,
                letterSpacing = 4.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MenuButton(
                    text = "PLAY GAME",
                    icon = Icons.Default.PlayArrow,
                    color = NeonGreen,
                    onClick = { onNavigate("mode_selection") }
                )

                MenuButton(
                    text = "DAILY CHALLENGE",
                    icon = Icons.Default.DateRange,
                    color = NeonPurple,
                    onClick = { onNavigate("daily_challenge") }
                )

                MenuButton(
                    text = "LEADERBOARD",
                    icon = Icons.AutoMirrored.Filled.List,
                    color = NeonOrange,
                    onClick = { onNavigate("leaderboard") }
                )

                MenuButton(
                    text = "HELP",
                    icon = Icons.Default.Info,
                    color = NeonBlue,
                    onClick = { onNavigate("help") }
                )

                MenuButton(
                    text = "COIN SHOP",
                    icon = Icons.Default.ShoppingCart,
                    color = NeonPink,
                    onClick = { onNavigate("shop") }
                )

                MenuButton(
                    text = "SETTING",
                    icon = Icons.Default.Settings,
                    color = Color.LightGray,
                    onClick = { onNavigate("settings") }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    DropbitTheme {
        HomeScreenContent(
            state = HomeState(coins = INITIAL_COINS),
            onNavigate = {}
        )
    }
}
