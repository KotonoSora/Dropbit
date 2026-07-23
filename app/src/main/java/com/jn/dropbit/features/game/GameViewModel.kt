package com.jn.dropbit.features.game

import androidx.lifecycle.viewModelScope
import com.jn.dropbit.R
import com.jn.dropbit.domain.engine.GameEngine
import com.jn.dropbit.domain.model.GameMode
import com.jn.dropbit.domain.model.GameState
import com.jn.dropbit.domain.usecase.GetPlayerSkinUseCase
import com.jn.dropbit.domain.usecase.ProcessGameOverUseCase
import com.jn.dropbit.presentation.base.BaseViewModel
import com.jn.dropbit.utils.SoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class GameViewModel(
    private val processGameOverUseCase: ProcessGameOverUseCase,
    getPlayerSkinUseCase: GetPlayerSkinUseCase,
    private val gameEngine: GameEngine,
    private val soundManager: SoundManager,
) : BaseViewModel<GameUIState, GameIntent, GameEffect>(GameUIState()) {

    private var sessionDuration = 0L
    private var lastMilestoneScore = 0

    init {
        getPlayerSkinUseCase().onEach { skin ->
            updateState { copy(playerSkin = skin) }
        }.launchIn(viewModelScope)
    }

    override fun onIntent(intent: GameIntent) {
        when (intent) {
            is GameIntent.StartGame -> startGame(intent.mode)
            is GameIntent.MovePlayer -> movePlayer(intent.deltaX)
            GameIntent.RestartGame -> startGame(currentState.gameMode)
        }
    }

    private fun startGame(mode: GameMode) {
        soundManager.play(R.raw.click)
        updateState { copy(gameState = GameState(gameMode = mode), gameMode = mode) }
        sessionDuration = 0L
        lastMilestoneScore = 0
        startGameLoop()
    }

    private fun startGameLoop() {
        viewModelScope.launch {
            while (!currentState.gameState.isGameOver) {
                val newState = gameEngine.updateGame(currentState.gameState, 16, sessionDuration)

                // Play milestone sound every 100 points
                if (newState.score / 100 > lastMilestoneScore / 100) {
                    soundManager.play(R.raw.milestone)
                    lastMilestoneScore = newState.score
                }

                updateState { copy(gameState = newState) }
                delay(16.milliseconds)
                sessionDuration += 16
            }
            handleGameOver()
        }
    }

    private fun movePlayer(deltaX: Float) {
        val currentGameState = currentState.gameState
        val newPlayerX = (currentGameState.playerX + deltaX).coerceIn(0f, 900f)
        updateState { copy(gameState = currentGameState.copy(playerX = newPlayerX)) }
    }

    private fun handleGameOver() {
        val mode = currentState.gameMode
        val score = currentState.gameState.score

        viewModelScope.launch {
            val (isHighScore, _) = processGameOverUseCase(mode, score)

            if (isHighScore && score > 0) {
                soundManager.play(R.raw.win)
            } else {
                soundManager.play(R.raw.lose)
            }
        }
    }
}
