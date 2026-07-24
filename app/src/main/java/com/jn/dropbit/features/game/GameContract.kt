package com.jn.dropbit.features.game

import com.jn.dropbit.domain.model.DEFAULT_SKIN
import com.jn.dropbit.domain.model.GameMode
import com.jn.dropbit.domain.model.GameState
import com.jn.dropbit.domain.model.INITIAL_COINS

data class GameUIState(
    val gameState: GameState = GameState(),
    val coins: Int = INITIAL_COINS,
    val coinsEarned: Int = 0,
    val playerSkin: String = DEFAULT_SKIN,
    val gameMode: GameMode = GameMode.CLASSIC,
)

sealed class GameIntent {
    data class StartGame(val mode: GameMode, val isChallenge: Boolean = false) : GameIntent()
    data class MovePlayer(val deltaX: Float) : GameIntent()
    object RestartGame : GameIntent()
}

sealed class GameEffect
