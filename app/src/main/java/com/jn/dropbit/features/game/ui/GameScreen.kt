package com.jn.dropbit.features.game.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
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
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jn.dropbit.domain.model.ALL_SKINS
import com.jn.dropbit.domain.model.DAILY_CHALLENGE_REWARD
import com.jn.dropbit.domain.model.FallingObjectType
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
        onUpdateMetrics = { padding, height ->
            viewModel.onIntent(
                GameIntent.UpdateMetrics(
                    padding,
                    height
                )
            )
        },
        onBuyHeart = { viewModel.onIntent(GameIntent.BuyHeart) },
        onRestart = { viewModel.onIntent(GameIntent.RestartGame) },
        onBackToMenu = onBackToMenu
    )
}

@Composable
fun GameScreenContent(
    uiState: GameUIState,
    onMovePlayer: (Float) -> Unit,
    onUpdateMetrics: (Float, Float) -> Unit,
    onBuyHeart: () -> Unit,
    onRestart: () -> Unit,
    onBackToMenu: () -> Unit
) {
    val gameState = uiState.gameState
    val mode = uiState.gameMode
    val density = LocalDensity.current
    val heartPainter = rememberVectorPainter(Icons.Default.Favorite)

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val screenWidth = constraints.maxWidth.toFloat()
        val screenHeight = constraints.maxHeight.toFloat()
        val scaleX = screenWidth / 1000f
        val scaleY = screenHeight / 2500f

        val paddingUnits = with(density) { 20.dp.toPx() } / scaleX
        val playerHeightUnits = 100f * (scaleX / scaleY)

        LaunchedEffect(paddingUnits, playerHeightUnits) {
            onUpdateMetrics(paddingUnits, playerHeightUnits)
        }

        DropbitScreen(
            useSystemBarsPadding = false,
            modifier = Modifier.pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onMovePlayer(dragAmount.x / scaleX)
                }
            }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    withTransform({
                        scale(scaleX, scaleY, pivot = Offset.Zero)
                    }) {
                        // Draw Area Backgrounds
                        val lightGreen = NeonGreen.copy(alpha = 0.3f)
                        val lightPink = NeonPink.copy(alpha = 0.3f)

                        // Left padding background
                        drawRect(
                            color = lightGreen,
                            topLeft = Offset(0f, 0f),
                            size = androidx.compose.ui.geometry.Size(paddingUnits, 2500f)
                        )
                        // Right padding background
                        drawRect(
                            color = lightGreen,
                            topLeft = Offset(1000f - paddingUnits, 0f),
                            size = androidx.compose.ui.geometry.Size(paddingUnits, 2500f)
                        )
                        // Bottom scoring area background
                        drawRect(
                            color = lightPink,
                            topLeft = Offset(0f, 2000f),
                            size = androidx.compose.ui.geometry.Size(1000f, 500f)
                        )

                        // Draw Grid Layout Background
                        val gridStepX = 100f
                        val gridStepY = gridStepX * (scaleX / scaleY)
                        val gridColor = Color.Gray.copy(alpha = 0.5f)

                        // Vertical grid lines
                        for (x in 0..10) {
                            val lineX = x * gridStepX
                            drawLine(
                                color = gridColor,
                                start = Offset(lineX, 0f),
                                end = Offset(lineX, 2500f),
                                strokeWidth = 1f / scaleX
                            )
                        }

                        // Horizontal grid lines
                        val numYLines = (2500f / gridStepY).toInt()
                        for (y in 0..numYLines) {
                            val lineY = y * gridStepY
                            drawLine(
                                color = gridColor,
                                start = Offset(0f, lineY),
                                end = Offset(1000f, lineY),
                                strokeWidth = 1f / scaleY
                            )
                        }

                        // Draw boundary lines (Green for horizontal movement)
                        drawLine(
                            color = NeonGreen.copy(alpha = 0.4f),
                            start = Offset(paddingUnits, 0f),
                            end = Offset(paddingUnits, 2500f),
                            strokeWidth = 4f / scaleX
                        )
                        drawLine(
                            color = NeonGreen.copy(alpha = 0.4f),
                            start = Offset(1000f - paddingUnits, 0f),
                            end = Offset(1000f - paddingUnits, 2500f),
                            strokeWidth = 4f / scaleX
                        )

                        // Draw scoring line (Red under player)
                        drawLine(
                            color = NeonPink,
                            start = Offset(0f, 2000f),
                            end = Offset(1000f, 2000f),
                            strokeWidth = 4f / scaleY
                        )

                        // Draw player
                        val playerColor =
                            ALL_SKINS.find { it.name == uiState.playerSkin }?.color ?: NeonBlue
                        drawRect(
                            color = playerColor,
                            topLeft = Offset(gameState.playerX, 1800f),
                            size = androidx.compose.ui.geometry.Size(100f, playerHeightUnits)
                        )

                        // Draw obstacles
                        gameState.obstacles.forEach { obstacle ->
                            if (obstacle.type == FallingObjectType.HEART) {
                                withTransform({
                                    translate(
                                        obstacle.position.x - 30f,
                                        obstacle.position.y - 30f
                                    )
                                }) {
                                    with(heartPainter) {
                                        draw(
                                            size = androidx.compose.ui.geometry.Size(60f, 60f),
                                            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Red)
                                        )
                                    }
                                }
                            } else {
                                drawCircle(
                                    color = Color.White,
                                    center = obstacle.position,
                                    radius = 30f
                                )
                            }
                        }
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
                        onBackClick = onBackToMenu
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            NeonText(
                                "SCORE: ${gameState.score}",
                                color = NeonBlue,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = NeonPink,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                NeonText(
                                    "x${gameState.hearts}",
                                    color = NeonPink,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            val timerText = when {
                                mode == GameMode.ENDLESS -> "TIME: ${gameState.timePlayed / 1000}s"
                                else -> "TIME: ${gameState.timeLeft / 1000}s"
                            }
                            val timerColor = if (mode == GameMode.ENDLESS) NeonGreen else NeonPink

                            NeonText(
                                timerText,
                                color = timerColor,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            if (!gameState.isGameOver && uiState.coins >= 70) {
                                TextButton(
                                    onClick = onBuyHeart,
                                    modifier = Modifier.height(32.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = null,
                                        tint = NeonYellow,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        "BUY HEART (70)",
                                        color = NeonYellow,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
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
                        isVictory = gameState.isVictory,
                        onRestart = onRestart,
                        onMenu = onBackToMenu
                    )
                }
            }
        }
    }
}

@Composable
fun GameOverOverlay(
    score: Int,
    coinsEarned: Int = 0,
    isChallenge: Boolean = false,
    isVictory: Boolean = false,
    onRestart: () -> Unit,
    onMenu: () -> Unit
) {
    val title = when {
        isVictory && isChallenge -> "CHALLENGE COMPLETE"
        isVictory -> "VICTORY"
        isChallenge -> "CHALLENGE FAILED"
        else -> "GAME OVER"
    }
    val titleColor = if (isVictory) NeonGreen else NeonPink

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
            onUpdateMetrics = { _, _ -> },
            onBuyHeart = {},
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
            onUpdateMetrics = { _, _ -> },
            onBuyHeart = {},
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
                    isVictory = true,
                    coinsEarned = 30,
                    score = 850,
                    timeLeft = 0
                )
            ),
            onMovePlayer = {},
            onUpdateMetrics = { _, _ -> },
            onBuyHeart = {},
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
                    isVictory = true,
                    coinsEarned = 50,
                    score = 1200,
                    timeLeft = 0
                )
            ),
            onMovePlayer = {},
            onUpdateMetrics = { _, _ -> },
            onBuyHeart = {},
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
                    isVictory = false,
                    coinsEarned = 0,
                    score = 300
                )
            ),
            onMovePlayer = {},
            onUpdateMetrics = { _, _ -> },
            onBuyHeart = {},
            onRestart = {}
        ) { /* onBackToMenu */ }
    }
}
