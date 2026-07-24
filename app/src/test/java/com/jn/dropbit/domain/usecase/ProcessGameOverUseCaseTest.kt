package com.jn.dropbit.domain.usecase

import com.jn.dropbit.domain.model.GameMode
import com.jn.dropbit.domain.model.HistoryRecord
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ProcessGameOverUseCaseTest {

    private val saveHighScoreUseCase: SaveHighScoreUseCase = mockk()
    private val getCoinsUseCase: GetCoinsUseCase = mockk()
    private val saveCoinsUseCase: SaveCoinsUseCase = mockk()
    private val saveHistoryUseCase: SaveHistoryUseCase = mockk()

    private lateinit var useCase: ProcessGameOverUseCase

    @Before
    fun setup() {
        useCase = ProcessGameOverUseCase(
            saveHighScoreUseCase,
            getCoinsUseCase,
            saveCoinsUseCase,
            saveHistoryUseCase
        )
    }

    @Test
    fun `when game over in normal mode, should receive 30 coins`() = runTest {
        // Given
        val mode = GameMode.CLASSIC
        val score = 100
        coEvery { saveHighScoreUseCase(mode, score) } returns true
        every { getCoinsUseCase() } returns flowOf(50)
        coEvery { saveCoinsUseCase(any()) } returns Unit
        coEvery { saveHistoryUseCase(any()) } returns Unit

        // When
        val (isHighScore, coinsEarned) = useCase(mode, score, isChallenge = false, isWin = false)

        // Then
        assertEquals(true, isHighScore)
        assertEquals(30, coinsEarned)
        coVerify { saveCoinsUseCase(80) }
    }

    @Test
    fun `when daily challenge won, should receive 50 coins`() = runTest {
        // Given
        val mode = GameMode.ENDLESS
        val score = 500
        coEvery { saveHighScoreUseCase(mode, score) } returns false
        every { getCoinsUseCase() } returns flowOf(100)
        coEvery { saveCoinsUseCase(any()) } returns Unit
        coEvery { saveHistoryUseCase(any()) } returns Unit

        // When
        val (isHighScore, coinsEarned) = useCase(mode, score, isChallenge = true, isWin = true)

        // Then
        assertEquals(50, coinsEarned)
        coVerify { saveCoinsUseCase(150) }
    }
}
