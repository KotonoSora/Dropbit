package com.jn.dropbit.domain.engine

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.jn.dropbit.domain.model.GameMode
import com.jn.dropbit.domain.model.GameState
import com.jn.dropbit.domain.model.Obstacle
import kotlin.random.Random

class GameEngine {
    private val playerSize = Size(100f, 100f)
    private val obstacleRadius = 30f

    fun updateGame(currentState: GameState, deltaTime: Long, sessionDuration: Long): GameState {
        if (currentState.isGameOver) return currentState

        // Update Time Attack mode
        val newTimeLeft = if (currentState.gameMode == GameMode.TIME_ATTACK) {
            (currentState.timeLeft - deltaTime).coerceAtLeast(0L)
        } else currentState.timeLeft

        val isGameOverByTime =
            (currentState.gameMode == GameMode.TIME_ATTACK) && (newTimeLeft <= 0L)

        // Difficulty scaling: increases speed and spawn rate over time
        val difficultyFactor = 1.0f + (sessionDuration / 10000f)
        val spawnRate = (5 + (difficultyFactor * 2).toInt()).coerceAtMost(20)
        val baseSpeed = 10f * difficultyFactor

        // Update obstacles
        val newObstacles = currentState.obstacles
            .asSequence()
            .map { it.copy(position = it.position.copy(y = it.position.y + it.speed)) }
            .filter { it.position.y < 2500f } // Screen height buffer
            .toList()

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
        var nextCounter = currentState.obstacleCounter
        val finalObstacles = if (Random.nextInt(100) < spawnRate) {
            newObstacles + Obstacle(
                id = nextCounter++,
                position = Offset(Random.nextFloat() * 1000f, -50f),
                speed = baseSpeed + Random.nextFloat() * 5f
            )
        } else {
            newObstacles
        }

        val gameIsOver =
            (hasCollision && currentState.gameMode != GameMode.ENDLESS) || isGameOverByTime

        return currentState.copy(
            obstacles = finalObstacles,
            score = currentState.score + 1,
            timeLeft = newTimeLeft,
            isGameOver = gameIsOver,
            obstacleCounter = nextCounter
        )
    }
}
