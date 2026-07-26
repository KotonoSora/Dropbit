package com.jn.dropbit.features.help

import androidx.lifecycle.viewModelScope
import com.jn.dropbit.domain.usecase.GetCoinsUseCase
import com.jn.dropbit.presentation.base.BaseViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class HelpViewModel(
    getCoinsUseCase: GetCoinsUseCase
) : BaseViewModel<HelpState, HelpIntent, HelpEffect>(HelpState()) {

    init {
        getCoinsUseCase().onEach { coins ->
            updateState { copy(coins = coins) }
        }.launchIn(viewModelScope)
    }

    override fun onIntent(intent: HelpIntent) {
        // No intents for help screen
    }
}
