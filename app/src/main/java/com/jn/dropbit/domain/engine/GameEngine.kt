package com.jn.dropbit.domain.engine

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.jn.dropbit.domain.model.FallingObjectType
import com.jn.dropbit.domain.model.GameMode
import com.jn.dropbit.domain.model.GameState
import com.jn.dropbit.domain.model.Obstacle
import kotlin.random.Random

class GameEngine {
    private val obstacleRadius = 30f

    fun updateGame(
        currentState: GameState,
        deltaTime: Long,
        sessionDuration: Long,
        playerSize: Size = Size(100f, 100f)
    ): GameState {
        if (currentState.isGameOver) return currentState

        // Update timers
        val isTimerMode = currentState.gameMode == GameMode.CLASSIC ||
                currentState.gameMode == GameMode.TIME_ATTACK ||
                currentState.isChallenge

        val newTimeLeft = if (isTimerMode) {
            (currentState.timeLeft - deltaTime).coerceAtLeast(0L)
        } else {
            currentState.timeLeft
        }

        val newTimePlayed = currentState.timePlayed + deltaTime

        // Update obstacles
        val movedObstacles = currentState.obstacles
            .asSequence()
            .map { it.copy(position = it.position.copy(y = it.position.y + it.speed)) }
            .filter { it.position.y < 2500f }
            .toList()

        // Collision detection
        val playerRect = Rect(Offset(currentState.playerX, 1800f), playerSize)
        val collidingObstacle = movedObstacles.find { obstacle ->
            val closestX = obstacle.position.x.coerceIn(playerRect.left, playerRect.right)
            val closestY = obstacle.position.y.coerceIn(playerRect.top, playerRect.bottom)
            val distanceX = obstacle.position.x - closestX
            val distanceY = obstacle.position.y - closestY
            (distanceX * distanceX + distanceY * distanceY) < (obstacleRadius * obstacleRadius)
        }

        var currentHearts = currentState.hearts
        var currentObstacles = movedObstacles
        var isGameOver = false
        var isVictory = false
        var hasJustCollided = false
        var hasCollectedHeart = false

        if (collidingObstacle != null) {
            if (collidingObstacle.type == FallingObjectType.HEART) {
                currentHearts++
                hasCollectedHeart = true
                currentObstacles = movedObstacles.filter { it.id != collidingObstacle.id }
            } else {
                currentHearts--
                hasJustCollided = true
                if (currentHearts <= 0) {
                    isGameOver = true
                    isVictory = false
                } else {
                    // Remove the obstacle that hit the player so it doesn't collide again
                    currentObstacles = movedObstacles.filter { it.id != collidingObstacle.id }
                }
            }
        }

        val isTimeOver = isTimerMode && newTimeLeft <= 0L
        if (!isGameOver && isTimeOver) {
            isGameOver = true
            isVictory = true
        }

        // Score logic - hearts don't count for score
        val passedObstaclesCount = currentObstacles.count { newObs ->
            val oldObs = currentState.obstacles.find { it.id == newObs.id }
            oldObs != null &&
                    oldObs.type == FallingObjectType.OBSTACLE &&
                    oldObs.position.y < 2000f && newObs.position.y >= 2000f
        }
        val finalScore = currentState.score + passedObstaclesCount

        if (isGameOver) {
            val coinsEarned = if (isVictory) {
                if (currentState.isChallenge) (finalScore / 10) + 50
                else finalScore / 10
            } else {
                finalScore / 20
            }
            return currentState.copy(
                obstacles = currentObstacles,
                score = finalScore,
                timeLeft = newTimeLeft,
                timePlayed = newTimePlayed,
                hearts = currentHearts.coerceAtLeast(0),
                isGameOver = true,
                isVictory = isVictory,
                hasJustCollided = hasJustCollided,
                hasCollectedHeart = hasCollectedHeart,
                coinsEarned = coinsEarned
            )
        }

        val difficultyFactor = (1.0f + (sessionDuration / 30000f)).coerceAtMost(3.0f)
        val spawnRate = (2 + (difficultyFactor * 0.8f).toInt()).coerceAtMost(5)
        val baseSpeed = 7f * difficultyFactor

        var nextCounter = currentState.obstacleCounter
        val finalObstacles = if (Random.nextInt(100) < spawnRate) {
            val type = if (Random.nextFloat() < 0.015f) {
                FallingObjectType.HEART
            } else {
                FallingObjectType.OBSTACLE
            }

            val speed = if (type == FallingObjectType.HEART) {
                4f // Hearts drop very slowly
            } else {
                baseSpeed * (0.8f + Random.nextFloat() * 0.7f) // Variable speeds
            }

            currentObstacles + Obstacle(
                id = nextCounter++,
                position = Offset(Random.nextFloat() * 1000f, -50f),
                speed = speed,
                type = type
            )
        } else {
            currentObstacles
        }

        return currentState.copy(
            obstacles = finalObstacles,
            score = finalScore,
            timeLeft = newTimeLeft,
            timePlayed = newTimePlayed,
            hearts = currentHearts,
            isGameOver = false,
            hasJustCollided = hasJustCollided,
            hasCollectedHeart = hasCollectedHeart,
            obstacleCounter = nextCounter
        )
    }
}
