package com.jn.dropbit.features.shop

import com.jn.dropbit.billing.BillingState
import com.jn.dropbit.domain.model.INITIAL_COINS
import com.jn.dropbit.domain.model.ShopProduct

data class ShopState(
    val coins: Int = INITIAL_COINS,
    val billingState: BillingState = BillingState.IDLE,
    val products: List<ShopProduct> = emptyList(),
)

sealed class ShopIntent {
    data class PurchaseProduct(val activity: android.app.Activity, val productId: String) :
        ShopIntent()

    object RetryBilling : ShopIntent()
}

sealed class ShopEffect
