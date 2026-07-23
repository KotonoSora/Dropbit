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
    fun `when game over, should save high score and coins`() = runTest {
        // Given
        val mode = GameMode.CLASSIC
        val score = 100
        coEvery { saveHighScoreUseCase(mode, score) } returns true
        every { getCoinsUseCase() } returns flowOf(50)
        coEvery { saveCoinsUseCase(any()) } returns Unit
        coEvery { saveHistoryUseCase(any()) } returns Unit

        // When
        val (isHighScore, coinsEarned) = useCase(mode, score)

        // Then
        assertEquals(true, isHighScore)
        assertEquals(10, coinsEarned)
        coVerify { saveCoinsUseCase(60) }
        coVerify { 
            saveHistoryUseCase(match { 
                it.score == 100 && it.coinsEarned == 10 && it.mode == GameMode.CLASSIC 
            }) 
        }
    }
}
