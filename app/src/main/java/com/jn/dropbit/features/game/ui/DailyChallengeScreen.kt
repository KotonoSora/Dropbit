package com.jn.dropbit.features.game.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jn.dropbit.domain.model.DAILY_CHALLENGE_DURATION_MS
import com.jn.dropbit.domain.model.DAILY_CHALLENGE_REWARD
import com.jn.dropbit.domain.model.INITIAL_COINS
import com.jn.dropbit.features.common.ui.DropbitScreen
import com.jn.dropbit.features.common.ui.HeaderBar
import com.jn.dropbit.features.common.ui.MenuButton
import com.jn.dropbit.features.common.ui.NeonCard
import com.jn.dropbit.features.common.ui.NeonText
import com.jn.dropbit.ui.theme.DropbitTheme
import com.jn.dropbit.ui.theme.NeonGreen
import com.jn.dropbit.ui.theme.NeonPurple
import com.jn.dropbit.ui.theme.NeonYellow
import com.jn.dropbit.ui.theme.ensureContrast

@Composable
fun DailyChallengeScreen(
    coins: Int = INITIAL_COINS,
    onPlayChallenge: () -> Unit,
    onBack: () -> Unit
) {
    DropbitScreen {
        HeaderBar(
            coins = coins,
            title = "DAILY CHALLENGE",
            onBackClick = onBack
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
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
                        "Survive for ${DAILY_CHALLENGE_DURATION_MS / 60000} minutes in ENDLESS mode with higher speed obstacles.",
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "REWARD:",
                        color = NeonYellow.ensureContrast(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AttachMoney,
                            contentDescription = null,
                            tint = NeonYellow,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        NeonText(
                            "$DAILY_CHALLENGE_REWARD coins",
                            color = NeonYellow,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(Modifier.height(48.dp))
            MenuButton("PLAY CHALLENGE", Icons.Default.PlayArrow, NeonGreen) {
                onPlayChallenge()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DailyChallengeScreenPreview() {
    DropbitTheme {
        DailyChallengeScreen(
            coins = INITIAL_COINS,
            onPlayChallenge = {}
        ) {
            // onBack
        }
    }
}
