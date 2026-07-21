package com.kotonosora.dropbit

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

object GamePreferences {
    val HIGH_SCORE_CLASSIC = intPreferencesKey("high_score_classic")
    val HIGH_SCORE_TIME_ATTACK = intPreferencesKey("high_score_time_attack")
    val HIGH_SCORE_ENDLESS = intPreferencesKey("high_score_endless")
    val SELECTED_SKIN = stringPreferencesKey("selected_skin")

    fun getHighScore(context: Context, key: androidx.datastore.preferences.core.Preferences.Key<Int>): Flow<Int> = 
        context.dataStore.data.map { it[key] ?: 0 }

    suspend fun saveHighScore(context: Context, key: androidx.datastore.preferences.core.Preferences.Key<Int>, score: Int) {
        context.dataStore.edit { it[key] = score }
    }

    fun getSelectedSkin(context: Context): Flow<String> = 
        context.dataStore.data.map { it[SELECTED_SKIN] ?: "Blue" }

    suspend fun saveSelectedSkin(context: Context, skin: String) {
        context.dataStore.edit { it[SELECTED_SKIN] = skin }
    }
}
