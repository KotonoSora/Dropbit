package com.jn.dropbit.features.history.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jn.dropbit.domain.model.GameMode
import com.jn.dropbit.domain.model.HistoryRecord
import com.jn.dropbit.domain.model.INITIAL_COINS
import com.jn.dropbit.features.common.ui.DropbitScreen
import com.jn.dropbit.features.common.ui.HeaderBar
import com.jn.dropbit.features.common.ui.NeonCard
import com.jn.dropbit.features.common.ui.NeonText
import com.jn.dropbit.features.history.HistoryUIState
import com.jn.dropbit.features.history.HistoryViewModel
import com.jn.dropbit.ui.theme.DropbitTheme
import com.jn.dropbit.ui.theme.NeonGreen
import com.jn.dropbit.ui.theme.NeonOrange
import com.jn.dropbit.ui.theme.NeonPink
import com.jn.dropbit.ui.theme.NeonPurple
import com.jn.dropbit.ui.theme.ensureContrast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ScoresScreen(viewModel: HistoryViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsState()
    ScoresScreenContent(state = state, onBack = onBack)
}

@Composable
fun ScoresScreenContent(state: HistoryUIState, onBack: () -> Unit) {
    DropbitScreen {
        HeaderBar(
            coins = state.coins,
            title = "HIGH SCORES",
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

            ScoreRow("CLASSIC", state.highScores[GameMode.CLASSIC] ?: 0, NeonGreen)
            ScoreRow("TIME ATTACK", state.highScores[GameMode.TIME_ATTACK] ?: 0, NeonPurple)
            ScoreRow("ENDLESS", state.highScores[GameMode.ENDLESS] ?: 0, NeonPink)
        }
    }
}

@Composable
fun ScoreRow(mode: String, score: Int, color: Color) {
    NeonCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        color = color,
        glowRadius = 4.dp,
        containerColor = color.copy(alpha = 0.05f)
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                mode,
                color = color.ensureContrast(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            NeonText(
                score.toString(),
                color = color,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun HistoryScreen(viewModel: HistoryViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsState()
    HistoryScreenContent(state = state, onBack = onBack)
}

@Composable
fun HistoryScreenContent(state: HistoryUIState, onBack: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()) }

    DropbitScreen {
        HeaderBar(
            coins = state.coins,
            title = "MY HISTORY",
            onBackClick = onBack
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "DATE",
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.weight(1.5f)
                )
                Text(
                    "MODE",
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "SCORE",
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
                Text(
                    "COINS",
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.history) { record ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            dateFormat.format(Date(record.date)),
                            color = Color.White.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1.5f),
                        )
                        Text(
                            record.mode.name.take(5),
                            color = NeonPurple.ensureContrast(),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            record.score.toString(),
                            color = Color.White.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                        Text(
                            "+${record.coinsEarned}",
                            color = NeonOrange.ensureContrast(),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                    }
                    HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScoresScreenPreview() {
    DropbitTheme {
        ScoresScreenContent(
            state = HistoryUIState(
                coins = INITIAL_COINS,
                highScores = mapOf(GameMode.CLASSIC to 150, GameMode.ENDLESS to 300)
            )
        ) {
            // onBack
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    DropbitTheme {
        HistoryScreenContent(
            state = HistoryUIState(
                coins = INITIAL_COINS,
                history = listOf(
                    HistoryRecord(
                        date = System.currentTimeMillis(),
                        mode = GameMode.CLASSIC,
                        score = 100,
                        coinsEarned = 10
                    ),
                    HistoryRecord(
                        date = System.currentTimeMillis() - 86400000,
                        mode = GameMode.ENDLESS,
                        score = 250,
                        coinsEarned = 25
                    )
                )
            ),
            onBack = {}
        )
    }
}
