package com.jn.dropbit.features.home

import androidx.lifecycle.viewModelScope
import com.jn.dropbit.domain.usecase.GetCoinsUseCase
import com.jn.dropbit.domain.usecase.GetPlayerSkinUseCase
import com.jn.dropbit.presentation.base.BaseViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class HomeViewModel(
    getCoinsUseCase: GetCoinsUseCase,
    getPlayerSkinUseCase: GetPlayerSkinUseCase,
) : BaseViewModel<HomeState, HomeIntent, HomeEffect>(HomeState()) {

    init {
        observeData(getCoinsUseCase, getPlayerSkinUseCase)
    }

    private fun observeData(
        getCoinsUseCase: GetCoinsUseCase,
        getPlayerSkinUseCase: GetPlayerSkinUseCase,
    ) {
        getCoinsUseCase().onEach { coins ->
            updateState { copy(coins = coins) }
        }.launchIn(viewModelScope)

        getPlayerSkinUseCase().onEach { skin ->
            updateState { copy(selectedSkin = skin) }
        }.launchIn(viewModelScope)
    }

    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.LoadData -> { /* Already observing */
            }

            HomeIntent.PlayGame -> sendEffect(HomeEffect.NavigateToModeSelection)
        }
    }
}
