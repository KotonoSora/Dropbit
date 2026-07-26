package com.jn.dropbit.features.history

import androidx.lifecycle.viewModelScope
import com.jn.dropbit.domain.usecase.GetCoinsUseCase
import com.jn.dropbit.domain.usecase.GetHistoryUseCase
import com.jn.dropbit.presentation.base.BaseViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.combine

class HistoryViewModel(
    getHistoryUseCase: GetHistoryUseCase,
    getCoinsUseCase: GetCoinsUseCase,
) : BaseViewModel<HistoryUIState, HistoryIntent, HistoryEffect>(HistoryUIState()) {

    init {
        combine(
            getHistoryUseCase(),
            getCoinsUseCase(),
        ) { history, coins ->
            HistoryUIState(
                coins = coins,
                history = history.sortedByDescending { it.score },
            )
        }.onEach { state ->
            updateState { state }
        }.launchIn(viewModelScope)
    }

    override fun onIntent(intent: HistoryIntent) {
        // Automatically loaded via init
    }
}
