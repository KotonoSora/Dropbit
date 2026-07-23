package com.jn.dropbit.features.settings

import com.jn.dropbit.domain.model.SettingsState

data class SettingsUIState(
    val coins: Int = 0,
    val settings: SettingsState = SettingsState(),
)

sealed class SettingsIntent {
    object ToggleSound : SettingsIntent()
    object ToggleNotifications : SettingsIntent()
}

sealed class SettingsEffect {
    // No effects for now
}
