package com.jn.dropbit.domain.model

import androidx.compose.ui.geometry.Offset

enum class GameMode { CLASSIC, TIME_ATTACK, ENDLESS }

const val INITIAL_COINS = 300
const val DAILY_CHALLENGE_REWARD = 50
const val DAILY_CHALLENGE_DURATION_MS = 120_000L
const val NORMAL_GAME_REWARD = 30

data class GameState(
    val playerX: Float = 0f,
    val obstacles: List<Obstacle> = emptyList(),
    val score: Int = 0,
    val timeLeft: Long = 60_000L,
    val isGameOver: Boolean = false,
    val isChallenge: Boolean = false,
    val coinsEarned: Int = 0,
    val gameMode: GameMode = GameMode.CLASSIC,
    val obstacleCounter: Int = 0,
)

data class Obstacle(val id: Int, val position: Offset, val speed: Float)

data class ScoreRecord(val mode: GameMode, val score: Int)

data class HistoryRecord(
    val date: Long,
    val mode: GameMode,
    val score: Int,
    val coinsEarned: Int
)

data class SettingsState(
    val soundEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true
)
