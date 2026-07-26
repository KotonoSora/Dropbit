package com.jn.dropbit.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jn.dropbit.di.DataComponent
import com.jn.dropbit.di.DomainComponent
import com.jn.dropbit.features.game.GameViewModel
import com.jn.dropbit.features.help.HelpViewModel
import com.jn.dropbit.features.history.HistoryViewModel
import com.jn.dropbit.features.home.HomeViewModel
import com.jn.dropbit.features.settings.SettingsViewModel
import com.jn.dropbit.features.shop.ShopViewModel

class ViewModelModule(
    private val dataComponent: DataComponent,
    private val domainComponent: DomainComponent
) {
    val viewModelFactory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                    HomeViewModel(
                        domainComponent.getCoinsUseCase,
                        domainComponent.getPlayerSkinUseCase
                    ) as T
                }

                modelClass.isAssignableFrom(ShopViewModel::class.java) -> {
                    ShopViewModel(
                        domainComponent.getCoinsUseCase,
                        domainComponent.saveCoinsUseCase,
                        dataComponent.billingManager,
                        dataComponent.soundManager
                    ) as T
                }

                modelClass.isAssignableFrom(GameViewModel::class.java) -> {
                    GameViewModel(
                        domainComponent.processGameOverUseCase,
                        domainComponent.saveCoinsUseCase,
                        domainComponent.getPlayerSkinUseCase,
                        domainComponent.getCoinsUseCase,
                        domainComponent.gameEngine,
                        dataComponent.soundManager
                    ) as T
                }

                modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                    SettingsViewModel(
                        domainComponent.getSettingsUseCase,
                        domainComponent.getCoinsUseCase,
                        domainComponent.getPlayerSkinUseCase,
                        domainComponent.savePlayerSkinUseCase,
                        domainComponent.saveSettingsUseCase,
                        dataComponent.soundManager
                    ) as T
                }

                modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                    HistoryViewModel(
                        domainComponent.getHistoryUseCase,
                        domainComponent.getCoinsUseCase,
                    ) as T
                }

                modelClass.isAssignableFrom(HelpViewModel::class.java) -> {
                    HelpViewModel(
                        domainComponent.getCoinsUseCase
                    ) as T
                }

                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
