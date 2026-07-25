package com.jn.dropbit.domain.engine

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.jn.dropbit.domain.model.DAILY_CHALLENGE_REWARD
import com.jn.dropbit.domain.model.GameMode
import com.jn.dropbit.domain.model.GameState
import com.jn.dropbit.domain.model.NORMAL_GAME_REWARD
import com.jn.dropbit.domain.model.Obstacle
import kotlin.random.Random

class GameEngine {
    private val playerSize = Size(100f, 100f)
    private val obstacleRadius = 30f

    fun updateGame(currentState: GameState, deltaTime: Long, sessionDuration: Long): GameState {
        if (currentState.isGameOver) return currentState

        // Update Time Attack mode or Challenge
        val isTimerMode = currentState.gameMode == GameMode.TIME_ATTACK || currentState.isChallenge
        val newTimeLeft = if (isTimerMode) {
            (currentState.timeLeft - deltaTime).coerceAtLeast(0L)
        } else currentState.timeLeft

        // Update obstacles first so collision is checked against their NEW positions
        val newObstacles = currentState.obstacles
            .asSequence()
            .map { it.copy(position = it.position.copy(y = it.position.y + it.speed)) }
            .filter { it.position.y < 2500f } // Screen height buffer
            .toList()

        // Collision detection: Circle-Rect intersection
        val playerRect = Rect(Offset(currentState.playerX, 1800f), playerSize)
        val hasCollision = newObstacles.any { obstacle ->
            // Find the closest point to the circle within the rectangle
            val closestX = obstacle.position.x.coerceIn(playerRect.left, playerRect.right)
            val closestY = obstacle.position.y.coerceIn(playerRect.top, playerRect.bottom)

            // Calculate the distance between the circle's center and this closest point
            val distanceX = obstacle.position.x - closestX
            val distanceY = obstacle.position.y - closestY

            // Collision if distance is less than radius squared (to avoid sqrt)
            (distanceX * distanceX + distanceY * distanceY) < (obstacleRadius * obstacleRadius)
        }

        val isGameOverByTime = isTimerMode && (newTimeLeft <= 0L)
        val gameIsOver = hasCollision || isGameOverByTime

        if (gameIsOver) {
            val coinsEarned = when {
                currentState.isChallenge && isGameOverByTime -> DAILY_CHALLENGE_REWARD
                currentState.isChallenge -> 0
                else -> NORMAL_GAME_REWARD
            }
            return currentState.copy(
                obstacles = newObstacles,
                timeLeft = newTimeLeft,
                isGameOver = true,
                coinsEarned = coinsEarned
            )
        }

        // Score logic: Count obstacles that passed the player (bottom line at 1900f)
        val passedObstaclesCount = newObstacles.count { newObs ->
            val oldObs = currentState.obstacles.find { it.id == newObs.id }
            oldObs != null && oldObs.position.y < 1900f && newObs.position.y >= 1900f
        }

        // Difficulty scaling: increases speed and spawn rate over time
        val difficultyFactor = 1.0f + (sessionDuration / 10000f)
        val spawnRate = (5 + (difficultyFactor * 2).toInt()).coerceAtMost(20)
        val baseSpeed = 10f * difficultyFactor

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

        return currentState.copy(
            obstacles = finalObstacles,
            score = currentState.score + passedObstaclesCount,
            timeLeft = newTimeLeft,
            isGameOver = false,
            obstacleCounter = nextCounter
        )
    }
}
