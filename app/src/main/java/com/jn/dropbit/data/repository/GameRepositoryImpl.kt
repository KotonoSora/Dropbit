package com.jn.dropbit.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.jn.dropbit.data.mapper.toDomain
import com.jn.dropbit.data.mapper.toDto
import com.jn.dropbit.data.mapper.toScoreRecord
import com.jn.dropbit.data.model.HistoryRecordDto
import com.jn.dropbit.domain.model.GameMode
import com.jn.dropbit.domain.model.HistoryRecord
import com.jn.dropbit.domain.model.ScoreRecord
import com.jn.dropbit.domain.model.SettingsState
import com.jn.dropbit.domain.repository.IGameRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GameRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : IGameRepository {

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val historyAdapter = moshi.adapter<List<HistoryRecordDto>>(
        Types.newParameterizedType(List::class.java, HistoryRecordDto::class.java)
    )

    override fun getHighScore(mode: GameMode): Flow<ScoreRecord> {
        val prefKey = intPreferencesKey("high_score_${mode.name.lowercase()}")
        return dataStore.data.map { (it[prefKey] ?: 0).toScoreRecord(mode) }
    }

    override suspend fun saveHighScore(score: ScoreRecord) {
        val prefKey = intPreferencesKey("high_score_${score.mode.name.lowercase()}")
        dataStore.edit { it[prefKey] = score.score }
    }

    override fun getSelectedSkin(): Flow<String> {
        return dataStore.data.map { it[SELECTED_SKIN] ?: "Blue" }
    }

    override suspend fun saveSelectedSkin(skin: String) {
        dataStore.edit { it[SELECTED_SKIN] = skin }
    }

    override fun getCoins(): Flow<Int> {
        return dataStore.data.map { it[COINS] ?: 0 }
    }

    override suspend fun saveCoins(coins: Int) {
        dataStore.edit { it[COINS] = coins }
    }

    override fun getLastAdTime(): Flow<Long> {
        return dataStore.data.map { it[LAST_AD_TIME] ?: 0L }
    }

    override suspend fun saveLastAdTime(time: Long) {
        dataStore.edit { it[LAST_AD_TIME] = time }
    }

    override fun getHistory(): Flow<List<HistoryRecord>> {
        return dataStore.data.map { preferences ->
            val json = preferences[HISTORY] ?: ""
            if (json.isEmpty()) emptyList()
            else historyAdapter.fromJson(json)?.map { it.toDomain() } ?: emptyList()
        }
    }

    override suspend fun saveHistory(record: HistoryRecord) {
        dataStore.edit { preferences ->
            val currentJson = preferences[HISTORY] ?: ""
            val currentList = if (currentJson.isEmpty()) emptyList()
            else historyAdapter.fromJson(currentJson) ?: emptyList()

            val newList = (listOf(record.toDto()) + currentList).take(50) // Keep last 50
            preferences[HISTORY] = historyAdapter.toJson(newList)
        }
    }

    override fun getSettings(): Flow<SettingsState> {
        return dataStore.data.map { preferences ->
            SettingsState(
                soundEnabled = preferences[SOUND_ENABLED] ?: true,
                notificationsEnabled = preferences[NOTIFICATIONS_ENABLED] ?: true
            )
        }
    }

    override suspend fun saveSettings(settings: SettingsState) {
        dataStore.edit { preferences ->
            preferences[SOUND_ENABLED] = settings.soundEnabled
            preferences[NOTIFICATIONS_ENABLED] = settings.notificationsEnabled
        }
    }

    override fun getUnlockedSkins(): Flow<List<String>> {
        return dataStore.data.map { preferences ->
            val unlocked = preferences[UNLOCKED_SKINS] ?: "Blue"
            unlocked.split(",")
        }
    }

    override suspend fun unlockSkin(skin: String) {
        dataStore.edit { preferences ->
            val current = preferences[UNLOCKED_SKINS] ?: "Blue"
            if (!current.split(",").contains(skin)) {
                preferences[UNLOCKED_SKINS] = "$current,$skin"
            }
        }
    }

    companion object {
        private val SELECTED_SKIN = stringPreferencesKey("selected_skin")
        private val COINS = intPreferencesKey("coins")
        private val LAST_AD_TIME = longPreferencesKey("last_ad_time")
        private val HISTORY = stringPreferencesKey("history")
        private val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val UNLOCKED_SKINS = stringPreferencesKey("unlocked_skins")
    }
}
