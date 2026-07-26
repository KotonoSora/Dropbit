package com.jn.dropbit.domain.usecase

import com.jn.dropbit.domain.model.HistoryRecord
import com.jn.dropbit.domain.repository.IGameRepository
import kotlinx.coroutines.flow.Flow

class GetHistoryUseCase(private val repository: IGameRepository) {
    operator fun invoke(): Flow<List<HistoryRecord>> = repository.getHistory()
}

class SaveHistoryUseCase(private val repository: IGameRepository) {
    suspend operator fun invoke(record: HistoryRecord) = repository.saveHistory(record)
}
