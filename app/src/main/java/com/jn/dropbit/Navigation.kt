package com.jn.dropbit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jn.dropbit.domain.model.GameMode
import com.jn.dropbit.features.game.ui.DailyChallengeScreen
import com.jn.dropbit.features.game.ui.GameScreen
import com.jn.dropbit.features.game.ui.ModeSelectionScreen
import com.jn.dropbit.features.help.ui.HelpScreen
import com.jn.dropbit.features.history.ui.HistoryScreen
import com.jn.dropbit.features.history.ui.ScoresScreen
import com.jn.dropbit.features.home.HomeViewModel
import com.jn.dropbit.features.home.ui.HomeScreen
import com.jn.dropbit.features.settings.ui.SettingsScreen
import com.jn.dropbit.features.shop.ui.CharacterSelectionScreen
import com.jn.dropbit.features.shop.ui.ShopCoinScreen

@Composable
fun DropbitNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val app = context.applicationContext as DropbitApp
    val factory = app.container.viewModelFactory
    val soundManager = app.container.soundManager

    val homeViewModel: HomeViewModel = viewModel(factory = factory)
    val homeState by homeViewModel.uiState.collectAsState()
    val currentCoins = homeState.coins

    val navigateWithSound: (String) -> Unit = { route ->
        soundManager.play(R.raw.click)
        navController.navigate(route)
    }

    val popBackWithSound: () -> Unit = {
        soundManager.play(R.raw.click)
        navController.popBackStack()
    }

    NavHost(navController = navController, startDestination = "menu") {
        composable("menu") {
            HomeScreen(
                viewModel = viewModel(factory = factory),
                onNavigate = navigateWithSound
            )
        }
        composable("mode_selection") {
            ModeSelectionScreen(
                coins = currentCoins,
                onModeSelected = { mode ->
                    soundManager.play(R.raw.click)
                    navController.navigate("game/${mode.name}")
                },
                onBack = popBackWithSound
            )
        }
        composable("skins") {
            CharacterSelectionScreen(
                viewModel = viewModel(factory = factory),
                onBack = popBackWithSound
            )
        }
        composable("scores") {
            ScoresScreen(
                viewModel = viewModel(factory = factory),
                onBack = popBackWithSound
            )
        }
        composable("shop") {
            ShopCoinScreen(
                viewModel = viewModel(factory = factory),
                onNavigateToSkins = { navigateWithSound("skins") },
                onBack = popBackWithSound
            )
        }
        composable("daily_challenge") {
            DailyChallengeScreen(
                coins = currentCoins,
                onPlayChallenge = { navigateWithSound("game/ENDLESS") },
                onBack = popBackWithSound
            )
        }
        composable("leaderboard") {
            HistoryScreen(
                viewModel = viewModel(factory = factory),
                onBack = popBackWithSound
            )
        }
        composable("help") {
            HelpScreen(
                viewModel = viewModel(factory = factory),
                onBack = popBackWithSound
            )
        }
        composable("settings") {
            SettingsScreen(
                viewModel = viewModel(factory = factory),
                onBack = popBackWithSound,
            )
        }
        composable("game/{mode}") { backStackEntry ->
            val modeString = backStackEntry.arguments?.getString("mode") ?: "CLASSIC"
            val mode = GameMode.valueOf(modeString)
            GameScreen(
                viewModel = viewModel(factory = factory),
                mode = mode
            ) {
                soundManager.play(R.raw.click)
                navController.popBackStack("menu", inclusive = false)
            }
        }
    }
}
