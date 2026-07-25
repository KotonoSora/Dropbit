package com.jn.dropbit.features.settings

import com.jn.dropbit.domain.model.DEFAULT_SKIN
import com.jn.dropbit.domain.model.INITIAL_COINS
import com.jn.dropbit.domain.model.SettingsState

data class SettingsUIState(
    val coins: Int = INITIAL_COINS,
    val settings: SettingsState = SettingsState(),
    val selectedSkin: String = DEFAULT_SKIN,
)

sealed class SettingsIntent {
    object ToggleSound : SettingsIntent()
    data class SelectSkin(val name: String) : SettingsIntent()
}

sealed class SettingsEffect {
    // No effects for now
}
