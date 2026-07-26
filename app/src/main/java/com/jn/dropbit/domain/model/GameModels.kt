package com.jn.dropbit.domain.model

import androidx.compose.ui.geometry.Offset

enum class GameMode { CLASSIC, TIME_ATTACK, ENDLESS }

enum class FallingObjectType { OBSTACLE, HEART }

const val INITIAL_COINS = 300
const val DAILY_CHALLENGE_REWARD = 50
const val DAILY_CHALLENGE_DURATION_MS = 120_000L
const val NORMAL_GAME_REWARD = 30
const val HEART_COST = 70
const val CLASSIC_DURATION_MS = 180_000L
const val TIME_ATTACK_DURATION_MS = 300_000L

data class GameState(
    val playerX: Float = 450f,
    val obstacles: List<Obstacle> = emptyList(),
    val score: Int = 0,
    val timeLeft: Long = 60_000L,
    val timePlayed: Long = 0L,
    val hearts: Int = 5,
    val isGameOver: Boolean = false,
    val isVictory: Boolean = false,
    val isChallenge: Boolean = false,
    val coinsEarned: Int = 0,
    val hasJustCollided: Boolean = false,
    val hasCollectedHeart: Boolean = false,
    val gameMode: GameMode = GameMode.CLASSIC,
    val obstacleCounter: Int = 0,
)

data class Obstacle(
    val id: Int,
    val position: Offset,
    val speed: Float,
    val type: FallingObjectType = FallingObjectType.OBSTACLE
)

data class ScoreRecord(val mode: GameMode, val score: Int)

data class HistoryRecord(
    val date: Long,
    val mode: GameMode,
    val score: Int,
    val coinsEarned: Int
)

data class SettingsState(
    val soundEnabled: Boolean = true
)
