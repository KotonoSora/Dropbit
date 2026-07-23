package com.jn.dropbit.domain.usecase

import com.jn.dropbit.domain.model.GameMode
import com.jn.dropbit.domain.model.HistoryRecord
import com.jn.dropbit.domain.model.ScoreRecord
import com.jn.dropbit.domain.repository.IGameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class GetHighScoreUseCase(private val repository: IGameRepository) {
    operator fun invoke(mode: GameMode): Flow<Int> =
        repository.getHighScore(mode).map { it.score }
}

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
        score: Int
    ): Pair<Boolean, Int> {
        val isHighScore = saveHighScoreUseCase(mode, score)
        val coinsEarned = score / 10

        if (coinsEarned > 0) {
            val currentCoins = getCoinsUseCase().first()
            saveCoinsUseCase(currentCoins + coinsEarned)
        }

        saveHistoryUseCase(
            HistoryRecord(
                date = System.currentTimeMillis(),
                mode = mode,
                score = score,
                coinsEarned = coinsEarned
            )
        )

        return isHighScore to coinsEarned
    }
}
