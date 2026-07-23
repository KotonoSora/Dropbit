package com.jn.dropbit.domain.usecase

import com.jn.dropbit.domain.repository.IGameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class GetUnlockedSkinsUseCase(private val repository: IGameRepository) {
    operator fun invoke(): Flow<List<String>> = repository.getUnlockedSkins()
}

class UnlockSkinUseCase(private val repository: IGameRepository) {
    suspend operator fun invoke(skin: String) = repository.unlockSkin(skin)
}

class BuySkinUseCase(
    private val getCoinsUseCase: GetCoinsUseCase,
    private val saveCoinsUseCase: SaveCoinsUseCase,
    private val unlockSkinUseCase: UnlockSkinUseCase,
) {
    suspend operator fun invoke(name: String, price: Int): Boolean {
        val currentCoins = getCoinsUseCase().first()
        return if (currentCoins >= price) {
            saveCoinsUseCase(currentCoins - price)
            unlockSkinUseCase(name)
            true
        } else {
            false
        }
    }
}
