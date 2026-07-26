package com.jn.dropbit.domain.usecase

import com.jn.dropbit.domain.repository.IGameRepository

class SaveCoinsUseCase(private val repository: IGameRepository) {
    suspend operator fun invoke(coins: Int) = repository.saveCoins(coins)
}
