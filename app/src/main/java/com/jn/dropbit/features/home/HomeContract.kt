package com.jn.dropbit.features.home

import com.jn.dropbit.domain.model.DEFAULT_SKIN
import com.jn.dropbit.domain.model.INITIAL_COINS

data class HomeState(
    val coins: Int = INITIAL_COINS,
    val selectedSkin: String = DEFAULT_SKIN,
)

sealed class HomeIntent {
    object LoadData : HomeIntent()
    object PlayGame : HomeIntent()
}

sealed class HomeEffect {
    object NavigateToModeSelection : HomeEffect()
}
