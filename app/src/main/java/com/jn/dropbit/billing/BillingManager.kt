package com.jn.dropbit.billing

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.jn.dropbit.domain.model.ShopProduct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class BillingState { IDLE, CONNECTING, SUCCESS, FAILURE }

class BillingManager(context: Context) : PurchasesUpdatedListener {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .enableAutoServiceReconnection()
        .build()

    private val _products = MutableStateFlow<List<ShopProduct>>(emptyList())
    val products: StateFlow<List<ShopProduct>> = _products

    private var realProductDetails: List<ProductDetails> = emptyList()

    private val _billingState = MutableStateFlow(BillingState.IDLE)
    val billingState: StateFlow<BillingState> = _billingState

    private val _purchaseSuccess = MutableSharedFlow<Int>()
    val purchaseSuccess: SharedFlow<Int> = _purchaseSuccess

    private val productIds = listOf(
        "coins_100",
        "coins_500",
        "coins_1000",
        "coins_1500",
        "coins_2000",
        "coins_2500",
        "coins_3000",
        "coins_3500",
        "coins_4000",
    )

    private val isDebug = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

    init {
        startConnection()
    }

    fun refreshProducts() {
        queryProducts()
    }

    private fun startConnection() {
        _billingState.value = BillingState.CONNECTING
        billingClient.startConnection(
            object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        queryProducts()
                    } else {
                        handleConnectionFailure()
                    }
                }

                override fun onBillingServiceDisconnected() {
                    // With enableAutoServiceReconnection(), we don't need to manually restart connection here.
                    // But we might want to update the state.
                    _billingState.value = BillingState.CONNECTING
                }
            },
        )
    }

    private fun handleConnectionFailure() {
        if (isDebug) {
            // In debug mode, show mock products even if connection fails
            _billingState.value = BillingState.SUCCESS
            _products.value = getMockProducts()
        } else {
            _billingState.value = BillingState.FAILURE
        }
    }

    private fun queryProducts() {
        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                productIds.map { id ->
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(id)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                },
            )
            .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, queryProductDetailsResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val productDetailsList = queryProductDetailsResult.productDetailsList
                realProductDetails = productDetailsList

                if (productDetailsList.isEmpty() && isDebug) {
                    _products.value = getMockProducts()
                    _billingState.value = BillingState.SUCCESS
                } else {
                    _products.value = productDetailsList.map { it.toShopProduct() }
                    _billingState.value = BillingState.SUCCESS
                }
            } else {
                if (isDebug) {
                    _products.value = getMockProducts()
                    _billingState.value = BillingState.SUCCESS
                } else {
                    _billingState.value = BillingState.FAILURE
                }
            }
        }
    }

    private fun ProductDetails.toShopProduct(): ShopProduct {
        return ShopProduct(
            id = productId,
            title = title.substringBefore(" ("),
            description = description,
            price = oneTimePurchaseOfferDetails?.formattedPrice ?: "N/A",
            amount = getAmountFromId(productId),
        )
    }

    fun launchPurchaseFlow(activity: Activity, productId: String) {
        val realDetails = realProductDetails.find { it.productId == productId }
        if (realDetails == null) {
            if (isDebug) {
                // Simulated purchase for debug mode when no real products are available
                val amount = getAmountFromId(productId)
                scope.launch {
                    _purchaseSuccess.emit(amount)
                }
            }
            return
        }

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(realDetails)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if ((billingResult.responseCode == BillingClient.BillingResponseCode.OK) && (purchases != null)) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            consumePurchase(purchase)
        }
    }

    private fun consumePurchase(purchase: Purchase) {
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient.consumeAsync(consumeParams) { billingResult, _ ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val amount = getAmountFromId(purchase.products.firstOrNull() ?: "")
                if (amount > 0) {
                    scope.launch {
                        _purchaseSuccess.emit(amount)
                    }
                }
            }
        }
    }

    companion object {
        fun getAmountFromId(productId: String): Int {
            return productId.substringAfter("coins_").toIntOrNull() ?: 0
        }

        fun getMockProducts(): List<ShopProduct> {
            val desc =
                "A pack of %d coins used to unlock powerful boosts like Extra Time, Hint, and Undo."
            return listOf(
                ShopProduct("coins_100", "100 Coins", desc.format(100), "$0.29", 100),
                ShopProduct("coins_500", "500 Coins", desc.format(500), "$0.49", 500),
                ShopProduct("coins_1000", "1000 Coins", desc.format(1000), "$0.69", 1000),
                ShopProduct("coins_1500", "1500 Coins", desc.format(1500), "$0.99", 1500),
                ShopProduct("coins_2000", "2000 Coins", desc.format(2000), "$1.99", 2000),
                ShopProduct("coins_2500", "2500 Coins", desc.format(2500), "$3.99", 2500),
                ShopProduct("coins_3000", "3000 Coins", desc.format(3000), "$4.99", 3000),
                ShopProduct("coins_3500", "3500 Coins", desc.format(3500), "$7.99", 3500),
                ShopProduct("coins_4000", "4000 Coins", desc.format(4000), "$9.99", 4000)
            )
        }
    }
}
