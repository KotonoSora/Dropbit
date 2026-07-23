package com.jn.dropbit.features.shop

import androidx.lifecycle.viewModelScope
import com.jn.dropbit.R
import com.jn.dropbit.billing.BillingManager
import com.jn.dropbit.domain.usecase.BuySkinUseCase
import com.jn.dropbit.domain.usecase.GetCoinsUseCase
import com.jn.dropbit.domain.usecase.GetLastAdTimeUseCase
import com.jn.dropbit.domain.usecase.GetPlayerSkinUseCase
import com.jn.dropbit.domain.usecase.GetUnlockedSkinsUseCase
import com.jn.dropbit.domain.usecase.SaveCoinsUseCase
import com.jn.dropbit.domain.usecase.SaveLastAdTimeUseCase
import com.jn.dropbit.domain.usecase.SavePlayerSkinUseCase
import com.jn.dropbit.presentation.base.BaseViewModel
import com.jn.dropbit.utils.SoundManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ShopViewModel(
    private val getCoinsUseCase: GetCoinsUseCase,
    private val saveCoinsUseCase: SaveCoinsUseCase,
    private val getUnlockedSkinsUseCase: GetUnlockedSkinsUseCase,
    private val buySkinUseCase: BuySkinUseCase,
    private val getPlayerSkinUseCase: GetPlayerSkinUseCase,
    private val savePlayerSkinUseCase: SavePlayerSkinUseCase,
    private val getLastAdTimeUseCase: GetLastAdTimeUseCase,
    private val saveLastAdTimeUseCase: SaveLastAdTimeUseCase,
    private val billingManager: BillingManager,
    private val soundManager: SoundManager,
) : BaseViewModel<ShopState, ShopIntent, ShopEffect>(ShopState()) {

    init {
        observeData()
    }

    private fun observeData() {
        getCoinsUseCase().onEach { coins -> updateState { copy(coins = coins) } }
            .launchIn(viewModelScope)
        getUnlockedSkinsUseCase().onEach { skins -> updateState { copy(unlockedSkins = skins) } }
            .launchIn(viewModelScope)
        getPlayerSkinUseCase().onEach { skin -> updateState { copy(selectedSkin = skin) } }
            .launchIn(viewModelScope)
        getLastAdTimeUseCase().onEach { time -> updateState { copy(lastAdTime = time) } }
            .launchIn(viewModelScope)

        billingManager.billingState.onEach { state -> updateState { copy(billingState = state) } }
            .launchIn(viewModelScope)
        billingManager.products.onEach { products -> updateState { copy(products = products) } }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            billingManager.purchaseSuccess.collectLatest { amount ->
                addCoins(amount)
            }
        }
    }

    override fun onIntent(intent: ShopIntent) {
        when (intent) {
            is ShopIntent.BuySkin -> buySkin(intent.name, intent.price)
            is ShopIntent.SelectSkin -> selectSkin(intent.name)
            is ShopIntent.PurchaseProduct -> billingManager.launchPurchaseFlow(
                intent.activity,
                intent.productId,
            )

            ShopIntent.WatchAd -> watchAd()
            ShopIntent.RetryBilling -> billingManager.retryConnection()
        }
    }

    private fun buySkin(name: String, price: Int) {
        viewModelScope.launch {
            val success = buySkinUseCase(name, price)
            if (success) {
                soundManager.play(R.raw.success)
            } else {
                soundManager.play(R.raw.error)
            }
        }
    }

    private fun selectSkin(name: String) {
        soundManager.play(R.raw.click)
        viewModelScope.launch {
            savePlayerSkinUseCase(name)
        }
    }

    private fun addCoins(amount: Int) {
        soundManager.play(R.raw.success)
        viewModelScope.launch {
            saveCoinsUseCase(currentState.coins + amount)
        }
    }

    private fun watchAd() {
        viewModelScope.launch {
            saveLastAdTimeUseCase(System.currentTimeMillis())
            addCoins(50)
        }
    }
}
