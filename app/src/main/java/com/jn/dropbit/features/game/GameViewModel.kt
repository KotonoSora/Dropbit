package com.jn.dropbit.features.game

import androidx.compose.ui.geometry.Size
import androidx.lifecycle.viewModelScope
import com.jn.dropbit.R
import com.jn.dropbit.domain.engine.GameEngine
import com.jn.dropbit.domain.model.CLASSIC_DURATION_MS
import com.jn.dropbit.domain.model.DAILY_CHALLENGE_DURATION_MS
import com.jn.dropbit.domain.model.GameMode
import com.jn.dropbit.domain.model.GameState
import com.jn.dropbit.domain.model.HEART_COST
import com.jn.dropbit.domain.model.TIME_ATTACK_DURATION_MS
import com.jn.dropbit.domain.usecase.GetCoinsUseCase
import com.jn.dropbit.domain.usecase.GetPlayerSkinUseCase
import com.jn.dropbit.domain.usecase.ProcessGameOverUseCase
import com.jn.dropbit.domain.usecase.SaveCoinsUseCase
import com.jn.dropbit.presentation.base.BaseViewModel
import com.jn.dropbit.utils.SoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class GameViewModel(
    private val processGameOverUseCase: ProcessGameOverUseCase,
    private val saveCoinsUseCase: SaveCoinsUseCase,
    getPlayerSkinUseCase: GetPlayerSkinUseCase,
    getCoinsUseCase: GetCoinsUseCase,
    private val gameEngine: GameEngine,
    private val soundManager: SoundManager,
) : BaseViewModel<GameUIState, GameIntent, GameEffect>(GameUIState()) {

    private var sessionDuration = 0L
    private var lastMilestoneScore = 0
    private var paddingUnits = 0f
    private var playerHeightUnits = 100f

    init {
        getPlayerSkinUseCase().onEach { skin ->
            updateState { copy(playerSkin = skin) }
        }.launchIn(viewModelScope)

        getCoinsUseCase().onEach { coins ->
            updateState { copy(coins = coins) }
        }.launchIn(viewModelScope)
    }

    override fun onIntent(intent: GameIntent) {
        when (intent) {
            is GameIntent.StartGame -> startGame(intent.mode, intent.isChallenge)
            is GameIntent.MovePlayer -> movePlayer(intent.deltaX)
            is GameIntent.UpdateMetrics -> {
                paddingUnits = intent.paddingUnits
                playerHeightUnits = intent.playerHeightUnits
            }

            GameIntent.BuyHeart -> buyHeart()
            GameIntent.RestartGame -> startGame(
                currentState.gameMode,
                currentState.gameState.isChallenge
            )
        }
    }

    private fun buyHeart() {
        if (currentState.coins >= HEART_COST && !currentState.gameState.isGameOver) {
            val newCoins = currentState.coins - HEART_COST
            val newHearts = currentState.gameState.hearts + 1
            updateState {
                copy(
                    coins = newCoins,
                    gameState = gameState.copy(hearts = newHearts)
                )
            }
            soundManager.play(R.raw.click)
            viewModelScope.launch {
                saveCoinsUseCase(newCoins)
            }
        }
    }

    private fun startGame(mode: GameMode, isChallenge: Boolean) {
        soundManager.play(R.raw.click)
        val initialTimeLeft = when {
            isChallenge -> DAILY_CHALLENGE_DURATION_MS
            mode == GameMode.CLASSIC -> CLASSIC_DURATION_MS
            mode == GameMode.TIME_ATTACK -> TIME_ATTACK_DURATION_MS
            else -> 0L
        }
        updateState {
            copy(
                gameState = GameState(
                    gameMode = mode,
                    isChallenge = isChallenge,
                    timeLeft = initialTimeLeft,
                    hearts = 5
                ),
                gameMode = mode
            )
        }
        sessionDuration = 0L
        lastMilestoneScore = 0
        startGameLoop()
    }

    private fun startGameLoop() {
        viewModelScope.launch {
            while (!currentState.gameState.isGameOver) {
                val newState = gameEngine.updateGame(
                    currentState.gameState,
                    16,
                    sessionDuration,
                    Size(100f, playerHeightUnits)
                )
                updateState { copy(gameState = newState) }

                // Play collision sound (Do this before checking gameOver so it plays on final hit)
                if (newState.hasJustCollided) {
                    soundManager.play(R.raw.collide)
                }

                // Play heart collection sound
                if (newState.hasCollectedHeart) {
                    soundManager.play(R.raw.success)
                }

                if (newState.isGameOver) break

                // Play milestone sound every 100 points
                if (newState.score / 100 > lastMilestoneScore / 100) {
                    soundManager.play(R.raw.milestone)
                    lastMilestoneScore = newState.score
                }

                delay(16.milliseconds)
                sessionDuration += 16
            }
            handleGameOver()
        }
    }

    private fun movePlayer(deltaX: Float) {
        val currentGameState = currentState.gameState
        val minX = paddingUnits
        val maxX = 1000f - 100f - paddingUnits
        val newPlayerX = (currentGameState.playerX + deltaX).coerceIn(minX, maxX)
        updateState { copy(gameState = currentGameState.copy(playerX = newPlayerX)) }
    }

    private fun handleGameOver() {
        val mode = currentState.gameMode
        val score = currentState.gameState.score
        val earned = currentState.gameState.coinsEarned
        val isVictory = currentState.gameState.isVictory
        val timePlayed = currentState.gameState.timePlayed

        viewModelScope.launch {
            val isHighScore = processGameOverUseCase(
                mode = mode,
                score = score,
                coinsEarned = earned,
                timePlayed = timePlayed
            )
            updateState { copy(coinsEarned = earned) }

            if (isVictory || (isHighScore && score > 0)) {
                soundManager.play(R.raw.win)
            } else {
                soundManager.play(R.raw.lose)
            }
        }
    }
}
