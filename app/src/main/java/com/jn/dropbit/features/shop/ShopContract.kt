package com.jn.dropbit.features.shop

import com.android.billingclient.api.ProductDetails
import com.jn.dropbit.billing.BillingState

data class ShopState(
    val coins: Int = 0,
    val unlockedSkins: List<String> = listOf("Blue"),
    val selectedSkin: String = "Blue",
    val billingState: BillingState = BillingState.IDLE,
    val products: List<ProductDetails> = emptyList(),
    val lastAdTime: Long = 0L,
)

sealed class ShopIntent {
    data class BuySkin(val name: String, val price: Int) : ShopIntent()
    data class SelectSkin(val name: String) : ShopIntent()
    data class PurchaseProduct(val activity: android.app.Activity, val productId: String) :
        ShopIntent()

    object WatchAd : ShopIntent()
    object RetryBilling : ShopIntent()
}

sealed class ShopEffect
