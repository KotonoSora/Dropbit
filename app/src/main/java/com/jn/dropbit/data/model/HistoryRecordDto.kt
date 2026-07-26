package com.jn.dropbit.data.model

import kotlinx.serialization.Serializable

@Serializable
data class HistoryRecordDto(
    val date: Long,
    val mode: String,
    val score: Int,
    val coinsEarned: Int
)
