package com.jn.dropbit.domain.usecase

import com.jn.dropbit.domain.repository.IGameRepository
import kotlinx.coroutines.flow.Flow

class GetCoinsUseCase(private val repository: IGameRepository) {
    operator fun invoke(): Flow<Int> = repository.getCoins()
}
