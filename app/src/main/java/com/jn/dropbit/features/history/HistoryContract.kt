package com.jn.dropbit.features.history

import com.jn.dropbit.domain.model.GameMode
import com.jn.dropbit.domain.model.HistoryRecord

data class HistoryUIState(
    val coins: Int = 0,
    val history: List<HistoryRecord> = emptyList(),
    val highScores: Map<GameMode, Int> = emptyMap(),
)

sealed class HistoryIntent

sealed class HistoryEffect
