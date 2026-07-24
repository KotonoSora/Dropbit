package com.jn.dropbit.features.shop

import com.jn.dropbit.billing.BillingState
import com.jn.dropbit.domain.model.INITIAL_COINS
import com.jn.dropbit.domain.model.ShopProduct

data class ShopState(
    val coins: Int = INITIAL_COINS,
    val unlockedSkins: List<String> = listOf("Blue"),
    val selectedSkin: String = "Blue",
    val billingState: BillingState = BillingState.IDLE,
    val products: List<ShopProduct> = emptyList(),
)

sealed class ShopIntent {
    data class BuySkin(val name: String, val price: Int) : ShopIntent()
    data class SelectSkin(val name: String) : ShopIntent()
    data class PurchaseProduct(val activity: android.app.Activity, val productId: String) :
        ShopIntent()

    object RetryBilling : ShopIntent()
}

sealed class ShopEffect
