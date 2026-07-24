package com.jn.dropbit.presentation.viewmodel

import com.jn.dropbit.features.game.GameIntent
import com.jn.dropbit.features.game.GameViewModel
import com.jn.dropbit.domain.engine.GameEngine
import com.jn.dropbit.domain.model.GameMode
import com.jn.dropbit.domain.model.ScoreRecord
import com.jn.dropbit.domain.repository.IGameRepository
import com.jn.dropbit.domain.usecase.*
import com.jn.dropbit.utils.SoundManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakeRepository = object : IGameRepository {
        override fun getHighScore(mode: GameMode): Flow<ScoreRecord> = flowOf(ScoreRecord(mode, 0))
        override suspend fun saveHighScore(score: ScoreRecord) {}
        override fun getSelectedSkin(): Flow<String> = flowOf("Blue")
        override suspend fun saveSelectedSkin(skin: String) {}
        override fun getCoins(): Flow<Int> = flowOf(0)
        override suspend fun saveCoins(coins: Int) {}
        override fun getHistory(): Flow<List<com.jn.dropbit.domain.model.HistoryRecord>> = flowOf(emptyList())
        override suspend fun saveHistory(record: com.jn.dropbit.domain.model.HistoryRecord) {}
        override fun getSettings(): Flow<com.jn.dropbit.domain.model.SettingsState> = flowOf(com.jn.dropbit.domain.model.SettingsState())
        override suspend fun saveSettings(settings: com.jn.dropbit.domain.model.SettingsState) {}
        override fun getUnlockedSkins(): Flow<List<String>> = flowOf(listOf("Blue"))
        override suspend fun unlockSkin(skin: String) {}
    }

    private val saveHighScoreUseCase = SaveHighScoreUseCase(fakeRepository)
    private val getPlayerSkinUseCase = GetPlayerSkinUseCase(fakeRepository)
    private val getCoinsUseCase = GetCoinsUseCase(fakeRepository)
    private val saveCoinsUseCase = SaveCoinsUseCase(fakeRepository)
    private val saveHistoryUseCase = SaveHistoryUseCase(fakeRepository)
    private val processGameOverUseCase = ProcessGameOverUseCase(
        saveHighScoreUseCase,
        getCoinsUseCase,
        saveCoinsUseCase,
        saveHistoryUseCase
    )
    private val gameEngine = GameEngine()
    
    private lateinit var fakeSoundManager: SoundManager
    private lateinit var viewModel: GameViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        fakeSoundManager = object : SoundManager(null, fakeRepository) {
            override fun play(resId: Int) {}
            override fun release() {}
        }

        viewModel = GameViewModel(
            processGameOverUseCase,
            getPlayerSkinUseCase,
            getCoinsUseCase,
            gameEngine,
            fakeSoundManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `StartGame intent should initialize state with correct mode`() = runTest {
        // When
        viewModel.onIntent(GameIntent.StartGame(GameMode.TIME_ATTACK))
        
        // Then
        assertEquals(GameMode.TIME_ATTACK, viewModel.uiState.value.gameMode)
        assertEquals(60_000L, viewModel.uiState.value.gameState.timeLeft)
    }

    @Test
    fun `MovePlayer intent should update playerX within bounds`() = runTest {
        // Given
        viewModel.onIntent(GameIntent.StartGame(GameMode.CLASSIC))
        
        // When
        viewModel.onIntent(GameIntent.MovePlayer(100f))
        
        // Then
        assertEquals(100f, viewModel.uiState.value.gameState.playerX)

        // When (Moving out of bounds)
        viewModel.onIntent(GameIntent.MovePlayer(-500f))
        
        // Then
        assertEquals(0f, viewModel.uiState.value.gameState.playerX)
    }
}
