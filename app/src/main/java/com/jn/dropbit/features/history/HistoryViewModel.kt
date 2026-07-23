package com.jn.dropbit.features.history

import androidx.lifecycle.viewModelScope
import com.jn.dropbit.domain.model.GameMode
import com.jn.dropbit.domain.usecase.GetCoinsUseCase
import com.jn.dropbit.domain.usecase.GetHighScoreUseCase
import com.jn.dropbit.domain.usecase.GetHistoryUseCase
import com.jn.dropbit.presentation.base.BaseViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class HistoryViewModel(
    getHistoryUseCase: GetHistoryUseCase,
    getCoinsUseCase: GetCoinsUseCase,
    private val getHighScoreUseCase: GetHighScoreUseCase,
) : BaseViewModel<HistoryUIState, HistoryIntent, HistoryEffect>(HistoryUIState()) {

    init {
        val classicFlow = getHighScoreUseCase(GameMode.CLASSIC)
        val timeAttackFlow = getHighScoreUseCase(GameMode.TIME_ATTACK)
        val endlessFlow = getHighScoreUseCase(GameMode.ENDLESS)

        combine(
            getHistoryUseCase(),
            getCoinsUseCase(),
            classicFlow,
            timeAttackFlow,
            endlessFlow
        ) { history, coins, classic, timeAttack, endless ->
            HistoryUIState(
                coins = coins,
                history = history,
                highScores = mapOf(
                    GameMode.CLASSIC to classic,
                    GameMode.TIME_ATTACK to timeAttack,
                    GameMode.ENDLESS to endless
                )
            )
        }.onEach { state ->
            updateState { state }
        }.launchIn(viewModelScope)
    }

    override fun onIntent(intent: HistoryIntent) {
        // Automatically loaded via init
    }
}
