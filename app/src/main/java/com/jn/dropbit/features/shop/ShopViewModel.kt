package com.jn.dropbit.features.shop

import androidx.lifecycle.viewModelScope
import com.jn.dropbit.R
import com.jn.dropbit.billing.BillingManager
import com.jn.dropbit.domain.usecase.GetCoinsUseCase
import com.jn.dropbit.domain.usecase.SaveCoinsUseCase
import com.jn.dropbit.presentation.base.BaseViewModel
import com.jn.dropbit.utils.SoundManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ShopViewModel(
    private val getCoinsUseCase: GetCoinsUseCase,
    private val saveCoinsUseCase: SaveCoinsUseCase,
    private val billingManager: BillingManager,
    private val soundManager: SoundManager,
) : BaseViewModel<ShopState, ShopIntent, ShopEffect>(ShopState()) {

    init {
        observeData()
    }

    private fun observeData() {
        getCoinsUseCase().onEach { coins -> updateState { copy(coins = coins) } }
            .launchIn(viewModelScope)

        billingManager.billingState.onEach { state -> updateState { copy(billingState = state) } }
            .launchIn(viewModelScope)
        billingManager.products.onEach { products ->
            updateState { copy(products = products) }
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            billingManager.purchaseSuccess.collectLatest { amount ->
                addCoins(amount)
            }
        }
    }

    override fun onIntent(intent: ShopIntent) {
        when (intent) {
            is ShopIntent.PurchaseProduct -> billingManager.launchPurchaseFlow(
                intent.activity,
                intent.productId,
            )

            ShopIntent.RetryBilling -> billingManager.retryConnection()
        }
    }

    private fun addCoins(amount: Int) {
        soundManager.play(R.raw.success)
        viewModelScope.launch {
            saveCoinsUseCase(currentState.coins + amount)
        }
    }
}
