package com.jn.dropbit.features.settings

import androidx.lifecycle.viewModelScope
import com.jn.dropbit.R
import com.jn.dropbit.domain.usecase.GetCoinsUseCase
import com.jn.dropbit.domain.usecase.GetSettingsUseCase
import com.jn.dropbit.domain.usecase.SaveSettingsUseCase
import com.jn.dropbit.presentation.base.BaseViewModel
import com.jn.dropbit.utils.SoundManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SettingsViewModel(
    getSettingsUseCase: GetSettingsUseCase,
    getCoinsUseCase: GetCoinsUseCase,
    private val saveSettingsUseCase: SaveSettingsUseCase,
    private val soundManager: SoundManager,
) : BaseViewModel<SettingsUIState, SettingsIntent, SettingsEffect>(SettingsUIState()) {

    init {
        getSettingsUseCase().onEach { settings ->
            updateState { copy(settings = settings) }
        }.launchIn(viewModelScope)

        getCoinsUseCase().onEach { coins ->
            updateState { copy(coins = coins) }
        }.launchIn(viewModelScope)
    }

    override fun onIntent(intent: SettingsIntent) {
        when (intent) {
            SettingsIntent.ToggleSound -> toggleSound()
            SettingsIntent.ToggleNotifications -> toggleNotifications()
        }
    }

    private fun toggleSound() {
        soundManager.play(R.raw.click)
        viewModelScope.launch {
            val current = currentState.settings
            saveSettingsUseCase(current.copy(soundEnabled = !current.soundEnabled))
        }
    }

    private fun toggleNotifications() {
        soundManager.play(R.raw.click)
        viewModelScope.launch {
            val current = currentState.settings
            saveSettingsUseCase(current.copy(notificationsEnabled = !current.notificationsEnabled))
        }
    }
}
