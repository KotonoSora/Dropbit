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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jn.dropbit.domain.model.ALL_SKINS
import com.jn.dropbit.domain.model.DAILY_CHALLENGE_REWARD
import com.jn.dropbit.domain.model.GameMode
import com.jn.dropbit.features.common.ui.DropbitScreen
import com.jn.dropbit.features.common.ui.HeaderBar
import com.jn.dropbit.features.common.ui.NeonButton
import com.jn.dropbit.features.common.ui.NeonText
import com.jn.dropbit.features.game.GameIntent
import com.jn.dropbit.features.game.GameUIState
import com.jn.dropbit.features.game.GameViewModel
import com.jn.dropbit.ui.theme.DropbitTheme
import com.jn.dropbit.ui.theme.NeonBlue
import com.jn.dropbit.ui.theme.NeonGreen
import com.jn.dropbit.ui.theme.NeonPink
import com.jn.dropbit.ui.theme.NeonYellow
import com.jn.dropbit.ui.theme.ensureContrast

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    mode: GameMode,
    isChallenge: Boolean = false,
    onBackToMenu: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(mode, isChallenge) {
        viewModel.onIntent(GameIntent.StartGame(mode, isChallenge))
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
                val playerColor = ALL_SKINS.find { it.name == uiState.playerSkin }?.color ?: NeonBlue
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
            ) {
                HeaderBar(
                    coins = uiState.coins,
                    onBackClick = onBackToMenu // Allow quitting via back button in header
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    NeonText(
                        "SCORE: ${gameState.score}",
                        color = NeonBlue,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    if (mode == GameMode.TIME_ATTACK || gameState.isChallenge) {
                        NeonText(
                            "TIME: ${gameState.timeLeft / 1000}s",
                            color = NeonPink,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (gameState.isChallenge && !gameState.isGameOver) {
                    NeonText(
                        "REWARD: $ $DAILY_CHALLENGE_REWARD",
                        color = NeonYellow,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }

            if (gameState.isGameOver) {
                GameOverOverlay(
                    score = gameState.score,
                    coinsEarned = gameState.coinsEarned,
                    isChallenge = gameState.isChallenge,
                    timeLeft = gameState.timeLeft,
                    gameMode = mode,
                    onRestart = onRestart,
                    onMenu = onBackToMenu
                )
            }
        }
    }
}

@Composable
fun GameOverOverlay(
    score: Int,
    coinsEarned: Int = 0,
    isChallenge: Boolean = false,
    timeLeft: Long = 0L,
    gameMode: GameMode = GameMode.CLASSIC,
    onRestart: () -> Unit,
    onMenu: () -> Unit
) {
    val isWin = timeLeft <= 0 && (isChallenge || gameMode == GameMode.TIME_ATTACK)
    val title = when {
        isWin && isChallenge -> "CHALLENGE COMPLETE"
        isWin -> "VICTORY"
        isChallenge -> "CHALLENGE FAILED"
        else -> "GAME OVER"
    }
    val titleColor = if (isWin) NeonGreen else NeonPink

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
                title,
                color = titleColor,
                style = MaterialTheme.typography.displaySmall,
                glowRadius = 8.dp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(16.dp))
            if (coinsEarned > 0) {
                NeonText(
                    "REWARD EARNED: $ $coinsEarned",
                    color = NeonYellow,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            } else {
                Text(
                    "FINAL SCORE: $score",
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.titleLarge
                )
            }
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
fun GameActivePreview() {
    DropbitTheme {
        GameScreenContent(
            uiState = GameUIState(),
            onMovePlayer = {},
            onRestart = {}
        ) { /* onBackToMenu */ }
    }
}

@Preview(showBackground = true)
@Composable
fun ChallengeActivePreview() {
    DropbitTheme {
        GameScreenContent(
            uiState = GameUIState(
                gameMode = GameMode.ENDLESS,
                gameState = com.jn.dropbit.domain.model.GameState(
                    isChallenge = true,
                    timeLeft = 115000L,
                    score = 420
                )
            ),
            onMovePlayer = {},
            onRestart = {}
        ) { /* onBackToMenu */ }
    }
}

@Preview(showBackground = true)
@Composable
fun VictoryPreview() {
    DropbitTheme {
        GameScreenContent(
            uiState = GameUIState(
                gameMode = GameMode.TIME_ATTACK,
                gameState = com.jn.dropbit.domain.model.GameState(
                    isGameOver = true,
                    coinsEarned = 30,
                    score = 850,
                    timeLeft = 0
                )
            ),
            onMovePlayer = {},
            onRestart = {}
        ) { /* onBackToMenu */ }
    }
}

@Preview(showBackground = true)
@Composable
fun ChallengeSuccessPreview() {
    DropbitTheme {
        GameScreenContent(
            uiState = GameUIState(
                gameState = com.jn.dropbit.domain.model.GameState(
                    isGameOver = true,
                    isChallenge = true,
                    coinsEarned = 50,
                    score = 1200,
                    timeLeft = 0
                )
            ),
            onMovePlayer = {},
            onRestart = {}
        ) { /* onBackToMenu */ }
    }
}

@Preview(showBackground = true)
@Composable
fun GameOverPreview() {
    DropbitTheme {
        GameScreenContent(
            uiState = GameUIState(
                gameState = com.jn.dropbit.domain.model.GameState(
                    isGameOver = true,
                    coinsEarned = 0,
                    score = 300
                )
            ),
            onMovePlayer = {},
            onRestart = {}
        ) { /* onBackToMenu */ }
    }
}
