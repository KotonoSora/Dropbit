package com.jn.dropbit.domain.usecase

import com.jn.dropbit.domain.model.SettingsState
import com.jn.dropbit.domain.repository.IGameRepository
import kotlinx.coroutines.flow.Flow

class GetSettingsUseCase(private val repository: IGameRepository) {
    operator fun invoke(): Flow<SettingsState> = repository.getSettings()
}

class SaveSettingsUseCase(private val repository: IGameRepository) {
    suspend operator fun invoke(settings: SettingsState) = repository.saveSettings(settings)
}
