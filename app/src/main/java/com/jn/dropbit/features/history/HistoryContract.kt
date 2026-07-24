package com.jn.dropbit.features.history

import com.jn.dropbit.domain.model.GameMode
import com.jn.dropbit.domain.model.HistoryRecord
import com.jn.dropbit.domain.model.INITIAL_COINS

data class HistoryUIState(
    val coins: Int = INITIAL_COINS,
    val history: List<HistoryRecord> = emptyList(),
    val highScores: Map<GameMode, Int> = emptyMap(),
)

sealed class HistoryIntent

sealed class HistoryEffect
