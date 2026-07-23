package com.jn.dropbit.features.game.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jn.dropbit.domain.model.GameMode
import com.jn.dropbit.features.common.ui.DropbitScreen
import com.jn.dropbit.features.common.ui.NeonButton
import com.jn.dropbit.features.common.ui.NeonText
import com.jn.dropbit.features.game.GameIntent
import com.jn.dropbit.features.game.GameUIState
import com.jn.dropbit.features.game.GameViewModel
import com.jn.dropbit.ui.theme.DropbitTheme
import com.jn.dropbit.ui.theme.NeonBlue
import com.jn.dropbit.ui.theme.NeonGreen
import com.jn.dropbit.ui.theme.NeonOrange
import com.jn.dropbit.ui.theme.NeonPink
import com.jn.dropbit.ui.theme.NeonPurple
import com.jn.dropbit.ui.theme.ensureContrast

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    mode: GameMode,
    onBackToMenu: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(mode) {
        viewModel.onIntent(GameIntent.StartGame(mode))
    }

    GameScreenContent(
        uiState = uiState,
        onMovePlayer = { deltaX -> viewModel.onIntent(GameIntent.MovePlayer(deltaX)) },
        onRestart = { viewModel.onIntent(GameIntent.RestartGame) },
        onBackToMenu = onBackToMenu
    )
}

@Composable
fun GameScreenContent(
    uiState: GameUIState,
    onMovePlayer: (Float) -> Unit,
    onRestart: () -> Unit,
    onBackToMenu: () -> Unit
) {
    val gameState = uiState.gameState
    val mode = uiState.gameMode

    DropbitScreen(
        useSystemBarsPadding = false,
        modifier = Modifier.pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
                change.consume()
                onMovePlayer(dragAmount.x)
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Draw player
                val playerColor = when (uiState.playerSkin) {
                    "Red" -> NeonPink
                    "Green" -> NeonGreen
                    "Purple" -> NeonPurple
                    "Orange" -> NeonOrange
                    else -> NeonBlue
                }
                drawRect(
                    color = playerColor,
                    topLeft = Offset(gameState.playerX, 1800f),
                    size = androidx.compose.ui.geometry.Size(100f, 100f)
                )

                // Draw obstacles
                gameState.obstacles.forEach { obstacle ->
                    drawCircle(
                        color = Color.White,
                        center = obstacle.position,
                        radius = 30f
                    )
                }
            }

            // HUD
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    NeonText(
                        "SCORE: ${gameState.score}",
                        color = NeonBlue,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    if (mode == GameMode.TIME_ATTACK) {
                        NeonText(
                            "TIME: ${gameState.timeLeft / 1000}s",
                            color = NeonPink,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (gameState.isGameOver) {
                GameOverOverlay(
                    score = gameState.score,
                    onRestart = onRestart,
                    onMenu = onBackToMenu
                )
            }
        }
    }
}

@Composable
fun GameOverOverlay(score: Int, onRestart: () -> Unit, onMenu: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.navigationBarsPadding()
        ) {
            NeonText(
                "GAME OVER",
                color = NeonPink,
                style = MaterialTheme.typography.displaySmall,
                glowRadius = 8.dp,
                fontWeight = FontWeight.Black
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "FINAL SCORE: $score",
                color = Color.White.copy(alpha = 0.9f),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(48.dp))
            NeonButton(
                onClick = onRestart,
                color = NeonGreen,
                modifier = Modifier
                    .height(56.dp)
                    .width(160.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null,
                        tint = NeonGreen.ensureContrast()
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "RETRY",
                        color = NeonGreen.ensureContrast(),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            TextButton(onClick = onMenu) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(Modifier.width(8.dp))
                Text("BACK TO MENU", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    DropbitTheme {
        GameScreenContent(
            uiState = GameUIState(),
            onMovePlayer = {},
            onRestart = {}
        ) {
            // onBackToMenu
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameOverOverlayPreview() {
    DropbitTheme {
        GameOverOverlay(score = 500, onRestart = {}, onMenu = {})
    }
}
