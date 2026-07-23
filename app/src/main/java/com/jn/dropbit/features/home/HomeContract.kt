package com.jn.dropbit.features.home

data class HomeState(
    val coins: Int = 0,
    val selectedSkin: String = "Blue",
)

sealed class HomeIntent {
    object LoadData : HomeIntent()
    object PlayGame : HomeIntent()
}

sealed class HomeEffect {
    object NavigateToModeSelection : HomeEffect()
}
