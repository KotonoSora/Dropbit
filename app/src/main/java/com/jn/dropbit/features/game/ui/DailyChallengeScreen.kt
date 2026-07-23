package com.jn.dropbit.features.game.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jn.dropbit.features.common.ui.DropbitScreen
import com.jn.dropbit.features.common.ui.HeaderBar
import com.jn.dropbit.features.common.ui.MenuButton
import com.jn.dropbit.features.common.ui.NeonCard
import com.jn.dropbit.features.common.ui.NeonText
import com.jn.dropbit.ui.theme.DropbitTheme
import com.jn.dropbit.ui.theme.NeonBlue
import com.jn.dropbit.ui.theme.NeonGreen
import com.jn.dropbit.ui.theme.NeonOrange
import com.jn.dropbit.ui.theme.NeonPurple
import com.jn.dropbit.ui.theme.ensureContrast

@Composable
fun DailyChallengeScreen(coins: Int = 0, onPlayChallenge: () -> Unit, onBack: () -> Unit) {
    DropbitScreen {
        HeaderBar(coins = coins)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = NeonOrange,
                modifier = Modifier.size(64.dp)
            )
            Spacer(Modifier.height(16.dp))
            NeonText(
                "DAILY CHALLENGE",
                style = MaterialTheme.typography.headlineMedium,
                color = NeonBlue
            )
            Spacer(Modifier.height(32.dp))

            NeonCard(
                modifier = Modifier.fillMaxWidth(),
                color = NeonPurple,
                glowRadius = 4.dp,
                containerColor = NeonPurple.copy(alpha = 0.05f)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "RULE OF THE DAY:",
                        color = NeonPurple.ensureContrast(),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Survive for 2 minutes in ENDLESS mode with higher speed obstacles.",
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "REWARD:",
                        color = NeonOrange.ensureContrast(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    NeonText(
                        "500 Coins",
                        color = NeonOrange,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(Modifier.height(48.dp))
            MenuButton("PLAY CHALLENGE", Icons.Default.PlayArrow, NeonGreen) {
                onPlayChallenge()
            }

            IconButton(onClick = onBack, modifier = Modifier.padding(top = 24.dp)) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = NeonBlue
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DailyChallengeScreenPreview() {
    DropbitTheme {
        DailyChallengeScreen(onPlayChallenge = {}) {
            // onBack
        }
    }
}
