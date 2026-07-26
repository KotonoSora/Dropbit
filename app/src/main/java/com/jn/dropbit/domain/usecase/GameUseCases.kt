package com.jn.dropbit.domain.usecase

import com.jn.dropbit.domain.model.GameMode
import com.jn.dropbit.domain.model.HistoryRecord
import com.jn.dropbit.domain.model.ScoreRecord
import com.jn.dropbit.domain.repository.IGameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class SaveHighScoreUseCase(private val repository: IGameRepository) {
    suspend operator fun invoke(mode: GameMode, score: Int): Boolean {
        val currentHigh = repository.getHighScore(mode).first().score
        return if (score > currentHigh) {
            repository.saveHighScore(ScoreRecord(mode, score))
            true
        } else {
            false
        }
    }
}

class GetPlayerSkinUseCase(private val repository: IGameRepository) {
    operator fun invoke(): Flow<String> = repository.getSelectedSkin()
}

class SavePlayerSkinUseCase(private val repository: IGameRepository) {
    suspend operator fun invoke(skin: String) = repository.saveSelectedSkin(skin)
}

class ProcessGameOverUseCase(
    private val saveHighScoreUseCase: SaveHighScoreUseCase,
    private val getCoinsUseCase: GetCoinsUseCase,
    private val saveCoinsUseCase: SaveCoinsUseCase,
    private val saveHistoryUseCase: SaveHistoryUseCase,
) {
    suspend operator fun invoke(
        mode: GameMode,
        score: Int,
        coinsEarned: Int,
        timePlayed: Long = 0L
    ): Boolean {
        // For Endless mode, we might want to use timePlayed (in seconds) as an alternative or primary score
        val finalScore = if (mode == GameMode.ENDLESS && score == 0) {
            (timePlayed / 1000).toInt()
        } else score

        val isHighScore = saveHighScoreUseCase(mode, finalScore)

        if (coinsEarned > 0) {
            val currentCoins = getCoinsUseCase().first()
            saveCoinsUseCase(currentCoins + coinsEarned)
        }

        saveHistoryUseCase(
            HistoryRecord(
                date = System.currentTimeMillis(),
                mode = mode,
                score = finalScore,
                coinsEarned = coinsEarned
            )
        )

        return isHighScore
    }
}
