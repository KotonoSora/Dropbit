package com.jn.dropbit

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jn.dropbit.ui.theme.DarkBackground
import com.jn.dropbit.ui.theme.NeonBlue
import com.jn.dropbit.ui.theme.NeonGreen
import com.jn.dropbit.ui.theme.NeonOrange
import com.jn.dropbit.ui.theme.NeonPink
import com.jn.dropbit.ui.theme.SurfaceDark
import kotlinx.coroutines.flow.first

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = viewModel(),
    mode: GameMode,
    onBackToMenu: () -> Unit
) {
    val state by viewModel.gameState.collectAsState()
    val context = LocalContext.current
    var skinColor by remember { mutableStateOf(NeonBlue) }

    LaunchedEffect(Unit) {
        viewModel.startGame(mode)
        val selectedSkin = GamePreferences.getSelectedSkin(context).first()
        skinColor = when (selectedSkin) {
            "Red" -> NeonPink
            "Green" -> NeonGreen
            else -> NeonBlue
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Starfield background effect (simple)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val step = 100f
            for (x in 0..size.width.toInt() step step.toInt()) {
                drawLine(
                    color = NeonBlue.copy(alpha = 0.1f),
                    start = Offset(x.toFloat(), 0f),
                    end = Offset(x.toFloat(), size.height),
                    strokeWidth = 1f
                )
            }
            for (y in 0..size.height.toInt() step step.toInt()) {
                drawLine(
                    color = NeonBlue.copy(alpha = 0.1f),
                    start = Offset(0f, y.toFloat()),
                    end = Offset(size.width, y.toFloat()),
                    strokeWidth = 1f
                )
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        viewModel.movePlayer(dragAmount.x)
                    }
                }
        ) {
            // Draw Player with glow
            drawRect(
                color = skinColor.copy(alpha = 0.3f),
                topLeft = Offset(state.playerX - 10f, 1790f),
                size = Size(120f, 120f)
            )
            drawRect(
                color = skinColor,
                topLeft = Offset(state.playerX, 1800f),
                size = Size(100f, 100f),
                style = Stroke(width = 4f)
            )
            drawRect(
                color = skinColor.copy(alpha = 0.5f),
                topLeft = Offset(state.playerX + 25f, 1825f),
                size = Size(50f, 50f)
            )

            // Draw Obstacles (Meteors/Neon shapes)
            state.obstacles.forEach { obstacle ->
                drawCircle(
                    color = NeonPink.copy(alpha = 0.2f),
                    radius = 40f,
                    center = obstacle.position
                )
                drawCircle(
                    color = NeonPink,
                    radius = 30f,
                    center = obstacle.position,
                    style = Stroke(width = 3f)
                )
                drawCircle(
                    color = NeonPink.copy(alpha = 0.6f),
                    radius = 10f,
                    center = obstacle.position
                )
            }
        }

        // Overlay UI
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SCORE",
                color = NeonBlue.copy(alpha = 0.7f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${state.score}",
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Black
            )

            if (mode == GameMode.TIME_ATTACK) {
                Text(
                    text = "${state.timeLeft / 1000}s",
                    color = NeonOrange,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (state.isGameOver) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.padding(32.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(2.dp, NeonPink)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Text(
                            text = "GAME OVER",
                            style = MaterialTheme.typography.headlineMedium,
                            color = NeonPink
                        )
                        Text(
                            text = "FINAL SCORE: ${state.score}",
                            color = Color.White,
                            fontSize = 20.sp
                        )
                        Button(
                            onClick = { viewModel.startGame(mode) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("RETRY", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = onBackToMenu,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(2.dp, NeonBlue)
                        ) {
                            Text("MAIN MENU", color = NeonBlue)
                        }
                    }
                }
            }
        }
    }
}
