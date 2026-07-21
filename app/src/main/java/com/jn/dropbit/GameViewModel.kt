package com.jn.dropbit

import android.app.Application
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class GameMode { CLASSIC, TIME_ATTACK, ENDLESS }

data class GameState(
    val playerX: Float = 0f,
    val obstacles: List<Obstacle> = emptyList(),
    val score: Int = 0,
    val timeLeft: Long = 60_000L, // For Time Attack
    val isGameOver: Boolean = false,
    val gameMode: GameMode = GameMode.CLASSIC
)

data class Obstacle(val id: Int, val position: Offset, val speed: Float)

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val _gameState = MutableStateFlow(GameState())
    val gameState = _gameState.asStateFlow()

    private var obstacleCounter = 0
    private var sessionDuration = 0L
    private val playerSize = Size(100f, 100f)
    private val obstacleRadius = 30f

    fun startGame(mode: GameMode) {
        _gameState.value = GameState(gameMode = mode)
        sessionDuration = 0L
        startGameLoop()
    }

    private fun startGameLoop() {
        viewModelScope.launch {
            while (!_gameState.value.isGameOver) {
                updateGame(16)
                delay(16) // ~60 FPS
                sessionDuration += 16
            }
        }
    }

    private fun updateGame(deltaTime: Long) {
        val currentState = _gameState.value
        if (currentState.isGameOver) return

        // Update Time Attack mode
        val newTimeLeft = if (currentState.gameMode == GameMode.TIME_ATTACK) {
            (currentState.timeLeft - deltaTime).coerceAtLeast(0L)
        } else currentState.timeLeft

        val isGameOverByTime = currentState.gameMode == GameMode.TIME_ATTACK && newTimeLeft <= 0L

        // Difficulty scaling: increases speed and spawn rate over time
        val difficultyFactor = 1.0f + (sessionDuration / 10000f)
        val spawnRate = (5 + (difficultyFactor * 2).toInt()).coerceAtMost(20)
        val baseSpeed = 10f * difficultyFactor

        // Update obstacles
        val newObstacles = currentState.obstacles
            .map { it.copy(position = it.position.copy(y = it.position.y + it.speed)) }
            .filter { it.position.y < 2500f } // Screen height buffer

        // Collision detection
        val playerRect = Rect(Offset(currentState.playerX, 1800f), playerSize)
        val hasCollision = newObstacles.any { obstacle ->
            val obstacleRect = Rect(
                obstacle.position.x - obstacleRadius,
                obstacle.position.y - obstacleRadius,
                obstacle.position.x + obstacleRadius,
                obstacle.position.y + obstacleRadius
            )
            playerRect.overlaps(obstacleRect)
        }

        // Randomly add new obstacles
        val finalObstacles = if (Random.nextInt(100) < spawnRate) {
            newObstacles + Obstacle(
                id = obstacleCounter++,
                position = Offset(Random.nextFloat() * 1000f, -50f),
                speed = baseSpeed + Random.nextFloat() * 5f
            )
        } else {
            newObstacles
        }

        val gameIsOver =
            (hasCollision && currentState.gameMode != GameMode.ENDLESS) || isGameOverByTime

        _gameState.value = currentState.copy(
            obstacles = finalObstacles,
            score = currentState.score + 1,
            timeLeft = newTimeLeft,
            isGameOver = gameIsOver
        )

        if (gameIsOver) {
            saveHighScore()
        }
    }

    private fun saveHighScore() {
        viewModelScope.launch {
            val mode = _gameState.value.gameMode
            val score = _gameState.value.score
            val key = when (mode) {
                GameMode.CLASSIC -> GamePreferences.HIGH_SCORE_CLASSIC
                GameMode.TIME_ATTACK -> GamePreferences.HIGH_SCORE_TIME_ATTACK
                GameMode.ENDLESS -> GamePreferences.HIGH_SCORE_ENDLESS
            }
            val currentHigh = GamePreferences.getHighScore(getApplication(), key).first()
            if (score > currentHigh) {
                GamePreferences.saveHighScore(getApplication(), key, score)
            }
        }
    }

    fun movePlayer(deltaX: Float) {
        _gameState.value = _gameState.value.copy(
            playerX = (_gameState.value.playerX + deltaX).coerceIn(0f, 900f)
        )
    }
}
