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
        .build()

    private val _products = MutableStateFlow<List<ProductDetails>>(emptyList())
    val products: StateFlow<List<ProductDetails>> = _products

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

    fun retryConnection() {
        if (_billingState.value != BillingState.CONNECTING) {
            startConnection()
        }
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
                    handleConnectionFailure()
                }
            },
        )
    }

    private fun handleConnectionFailure() {
        if (isDebug) {
            // In debug mode, we still want to show products even if billing connection fails
            _billingState.value = BillingState.SUCCESS
            // products will be populated by queryProducts equivalent or mock data
            // Since we failed connection, we can't query real products, so we mock them here.
            // But we prefer queryProducts to try first.
            queryProducts()
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
                if (productDetailsList.isEmpty() && isDebug) {
                    _billingState.value = BillingState.SUCCESS
                } else {
                    _products.value = productDetailsList
                    _billingState.value = BillingState.SUCCESS
                }
            } else {
                if (isDebug) {
                    _billingState.value = BillingState.SUCCESS
                } else {
                    _billingState.value = BillingState.FAILURE
                }
            }
        }
    }

    fun launchPurchaseFlow(activity: Activity, productId: String) {
        if (isDebug && _products.value.isEmpty()) {
            // Simulated purchase for debug mode when no real products are available
            val amount = getAmountFromId(productId)
            scope.launch {
                _purchaseSuccess.emit(amount)
            }
            return
        }

        val productDetails = _products.value.find { it.productId == productId } ?: return
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
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

    private fun getAmountFromId(productId: String): Int {
        return when (productId) {
            "coins_100" -> 100
            "coins_500" -> 500
            "coins_1000" -> 1000
            "coins_1500" -> 1500
            "coins_2000" -> 2000
            "coins_2500" -> 2500
            "coins_3000" -> 3000
            "coins_3500" -> 3500
            "coins_4000" -> 4000
            else -> 0
        }
    }
}
