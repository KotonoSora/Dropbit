package com.jn.dropbit.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HistoryRecordDto(
    val date: Long,
    val mode: String,
    val score: Int,
    val coinsEarned: Int
)
