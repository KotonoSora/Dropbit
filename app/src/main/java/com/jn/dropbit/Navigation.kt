package com.jn.dropbit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
                    navController.navigate("game/${mode.name}?challenge=false")
                },
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
                onBack = popBackWithSound
            )
        }
        composable("daily_challenge") {
            DailyChallengeScreen(
                coins = currentCoins,
                onPlayChallenge = { navigateWithSound("game/ENDLESS?challenge=true") },
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
        composable(
            route = "game/{mode}?challenge={challenge}",
            arguments = listOf(
                navArgument("mode") { type = NavType.StringType },
                navArgument("challenge") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val modeString = backStackEntry.arguments?.getString("mode") ?: "CLASSIC"
            val mode = GameMode.valueOf(modeString)
            val isChallenge = backStackEntry.arguments?.getBoolean("challenge") ?: false
            GameScreen(
                viewModel = viewModel(factory = factory),
                mode = mode,
                isChallenge = isChallenge
            ) {
                soundManager.play(R.raw.click)
                navController.popBackStack("menu", inclusive = false)
            }
        }
    }
}
