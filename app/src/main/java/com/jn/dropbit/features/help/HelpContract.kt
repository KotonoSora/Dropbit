package com.jn.dropbit.features.help

import com.jn.dropbit.domain.model.INITIAL_COINS

data class HelpState(
    val coins: Int = INITIAL_COINS,
    val instructions: List<String> = listOf(
        "1. Drag your square left and right to avoid falling circles.",
        "2. Surviving longer increases your score.",
        "3. Collect coins to unlock new skins in the shop.",
        "4. In Time Attack, survive until the clock runs out.",
    ),
)

sealed class HelpIntent

sealed class HelpEffect
