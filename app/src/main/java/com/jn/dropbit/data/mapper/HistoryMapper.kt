package com.jn.dropbit.data.mapper

import com.jn.dropbit.data.model.HistoryRecordDto
import com.jn.dropbit.domain.model.GameMode
import com.jn.dropbit.domain.model.HistoryRecord

fun HistoryRecord.toDto(): HistoryRecordDto = HistoryRecordDto(
    date = date,
    mode = mode.name,
    score = score,
    coinsEarned = coinsEarned
)

fun HistoryRecordDto.toDomain(): HistoryRecord = HistoryRecord(
    date = date,
    mode = GameMode.valueOf(mode),
    score = score,
    coinsEarned = coinsEarned
)
