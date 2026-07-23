package com.jn.dropbit.domain.repository

import com.jn.dropbit.domain.model.GameMode
import com.jn.dropbit.domain.model.HistoryRecord
import com.jn.dropbit.domain.model.ScoreRecord
import com.jn.dropbit.domain.model.SettingsState
import kotlinx.coroutines.flow.Flow

interface IGameRepository {
    fun getHighScore(mode: GameMode): Flow<ScoreRecord>
    suspend fun saveHighScore(score: ScoreRecord)
    fun getSelectedSkin(): Flow<String>
    suspend fun saveSelectedSkin(skin: String)
    fun getCoins(): Flow<Int>
    suspend fun saveCoins(coins: Int)
    fun getLastAdTime(): Flow<Long>
    suspend fun saveLastAdTime(time: Long)
    fun getHistory(): Flow<List<HistoryRecord>>
    suspend fun saveHistory(record: HistoryRecord)
    fun getSettings(): Flow<SettingsState>
    suspend fun saveSettings(settings: SettingsState)
    fun getUnlockedSkins(): Flow<List<String>>
    suspend fun unlockSkin(skin: String)
}
