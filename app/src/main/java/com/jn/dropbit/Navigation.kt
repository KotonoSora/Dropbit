package com.jn.dropbit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jn.dropbit.ui.theme.DarkBackground
import com.jn.dropbit.ui.theme.NeonBlue
import com.jn.dropbit.ui.theme.NeonGreen
import com.jn.dropbit.ui.theme.NeonOrange
import com.jn.dropbit.ui.theme.NeonPink
import com.jn.dropbit.ui.theme.NeonPurple
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun MainMenuScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "DODGE\nSURVIVAL",
                style = MaterialTheme.typography.headlineLarge,
                color = NeonBlue,
                lineHeight = 50.sp,
                letterSpacing = 4.sp,
                fontWeight = FontWeight.Black
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MenuButton(
                    text = "START GAME",
                    icon = Icons.Default.PlayArrow,
                    color = NeonGreen,
                    onClick = { navController.navigate("mode_selection") }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SmallMenuButton(
                        text = "SKINS",
                        icon = Icons.Default.Star,
                        color = NeonPurple,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate("skins") }
                    )
                    SmallMenuButton(
                        text = "SCORES",
                        icon = Icons.Default.Settings,
                        color = NeonOrange,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate("scores") }
                    )
                }
            }
        }
    }
}

@Composable
fun ModeSelectionScreen(navController: NavController) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(DarkBackground)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("SELECT MODE", style = MaterialTheme.typography.headlineMedium, color = NeonBlue)
            Spacer(Modifier.height(48.dp))

            GameMode.values().forEach { mode ->
                MenuButton(
                    text = mode.name.replace("_", " "),
                    icon = Icons.Default.PlayArrow,
                    color = when (mode) {
                        GameMode.CLASSIC -> NeonGreen
                        GameMode.TIME_ATTACK -> NeonPurple
                        GameMode.ENDLESS -> NeonPink
                    },
                    onClick = { navController.navigate("game/${mode.name}") }
                )
                Spacer(Modifier.height(16.dp))
            }

            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(top = 32.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = NeonBlue,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun ScoresScreen(navController: NavController) {
    val context = LocalContext.current
    var classicHigh by remember { mutableStateOf(0) }
    var timeAttackHigh by remember { mutableStateOf(0) }
    var endlessHigh by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        classicHigh =
            GamePreferences.getHighScore(context, GamePreferences.HIGH_SCORE_CLASSIC).first()
        timeAttackHigh =
            GamePreferences.getHighScore(context, GamePreferences.HIGH_SCORE_TIME_ATTACK).first()
        endlessHigh =
            GamePreferences.getHighScore(context, GamePreferences.HIGH_SCORE_ENDLESS).first()
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(DarkBackground)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("HIGH SCORES", style = MaterialTheme.typography.headlineMedium, color = NeonBlue)
            Spacer(Modifier.height(48.dp))

            ScoreRow("CLASSIC", classicHigh, NeonGreen)
            ScoreRow("TIME ATTACK", timeAttackHigh, NeonPurple)
            ScoreRow("ENDLESS", endlessHigh, NeonPink)

            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(top = 32.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = NeonBlue,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun ScoreRow(mode: String, score: Int, color: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(mode, color = color, fontWeight = FontWeight.Bold)
            Text(
                score.toString(),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun MenuButton(
    text: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            color.copy(alpha = 0.2f),
                            color.copy(alpha = 0.5f)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
                Spacer(Modifier.width(16.dp))
                Text(text, color = color, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SmallMenuButton(
    text: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            color.copy(alpha = 0.2f),
                            color.copy(alpha = 0.5f)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(8.dp))
            Text(text, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CharacterSelectionScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val skins = listOf("Blue" to NeonBlue, "Red" to NeonPink, "Green" to NeonGreen)

    Box(modifier = Modifier
        .fillMaxSize()
        .background(DarkBackground)) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("CHOOSE SKIN", style = MaterialTheme.typography.headlineMedium, color = NeonBlue)
            Spacer(Modifier.height(48.dp))

            skins.forEach { (name, color) ->
                Button(
                    onClick = {
                        scope.launch {
                            GamePreferences.saveSelectedSkin(context, name)
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = color.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(15.dp),
                    border = androidx.compose.foundation.BorderStroke(2.dp, color)
                ) {
                    Text(name.uppercase(), color = color, fontWeight = FontWeight.Bold)
                }
            }

            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(top = 32.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = NeonBlue,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun DropbitNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "menu") {
        composable("menu") { MainMenuScreen(navController) }
        composable("mode_selection") { ModeSelectionScreen(navController) }
        composable("skins") { CharacterSelectionScreen(navController) }
        composable("scores") { ScoresScreen(navController) }
        composable("game/{mode}") { backStackEntry ->
            val modeString = backStackEntry.arguments?.getString("mode") ?: "CLASSIC"
            val mode = GameMode.valueOf(modeString)
            GameScreen(
                mode = mode,
                onBackToMenu = {
                    navController.popBackStack("menu", inclusive = false)
                }
            )
        }
    }
}
