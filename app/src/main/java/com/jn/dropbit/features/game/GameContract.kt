package com.jn.dropbit.features.game

import com.jn.dropbit.domain.model.GameMode
import com.jn.dropbit.domain.model.GameState

data class GameUIState(
    val gameState: GameState = GameState(),
    val playerSkin: String = "Blue",
    val gameMode: GameMode = GameMode.CLASSIC,
)

sealed class GameIntent {
    data class StartGame(val mode: GameMode) : GameIntent()
    data class MovePlayer(val deltaX: Float) : GameIntent()
    object RestartGame : GameIntent()
}

sealed class GameEffect
