package com.jn.dropbit.data.mapper

import com.jn.dropbit.domain.model.GameMode
import com.jn.dropbit.domain.model.ScoreRecord

fun Int.toScoreRecord(mode: GameMode): ScoreRecord = ScoreRecord(mode, this)
