package com.jn.dropbit.domain.usecase

import com.jn.dropbit.domain.model.GameMode
import com.jn.dropbit.domain.model.ScoreRecord
import com.jn.dropbit.domain.repository.IGameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SaveHighScoreUseCaseTest {

    private val fakeRepository = object : IGameRepository {
        val scores = mutableMapOf<GameMode, Int>()
        override fun getHighScore(mode: GameMode): Flow<ScoreRecord> = flow {
            emit(ScoreRecord(mode, scores[mode] ?: 0))
        }
        override suspend fun saveHighScore(score: ScoreRecord) {
            scores[score.mode] = score.score
        }
        override fun getSelectedSkin(): Flow<String> = flow { emit("Blue") }
        override suspend fun saveSelectedSkin(skin: String) {}
        override fun getCoins(): Flow<Int> = flow { emit(0) }
        override suspend fun saveCoins(coins: Int) {}
        override fun getHistory(): Flow<List<com.jn.dropbit.domain.model.HistoryRecord>> = flow { emit(emptyList()) }
        override suspend fun saveHistory(record: com.jn.dropbit.domain.model.HistoryRecord) {}
        override fun getSettings(): Flow<com.jn.dropbit.domain.model.SettingsState> = flow { emit(com.jn.dropbit.domain.model.SettingsState()) }
        override suspend fun saveSettings(settings: com.jn.dropbit.domain.model.SettingsState) {}
        override fun getUnlockedSkins(): Flow<List<String>> = flow { emit(listOf("Blue")) }
        override suspend fun unlockSkin(skin: String) {}
    }

    private val saveHighScoreUseCase = SaveHighScoreUseCase(fakeRepository)

    @Test
    fun `saveHighScore should return true and save if score is higher than current`() = runBlocking {
        // Given
        val mode = GameMode.CLASSIC
        fakeRepository.scores[mode] = 100

        // When
        val result = saveHighScoreUseCase(mode, 150)
        
        // Then
        assertTrue(result)
        assertEquals(150, fakeRepository.scores[mode])
    }

    @Test
    fun `saveHighScore should return false and not save if score is not higher than current`() = runBlocking {
        // Given
        val mode = GameMode.CLASSIC
        fakeRepository.scores[mode] = 100

        // When
        val result = saveHighScoreUseCase(mode, 50)
        
        // Then
        assertFalse(result)
        assertEquals(100, fakeRepository.scores[mode])
    }
}
