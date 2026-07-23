package com.jn.dropbit.domain.usecase

import com.jn.dropbit.domain.repository.IGameRepository

class SaveLastAdTimeUseCase(private val repository: IGameRepository) {
    suspend operator fun invoke(time: Long) = repository.saveLastAdTime(time)
}
