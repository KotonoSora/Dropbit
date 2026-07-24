package com.jn.dropbit.features.shop.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jn.dropbit.billing.BillingManager
import com.jn.dropbit.billing.BillingState
import com.jn.dropbit.domain.model.INITIAL_COINS
import com.jn.dropbit.domain.model.ShopProduct
import com.jn.dropbit.features.common.ui.DropbitScreen
import com.jn.dropbit.features.common.ui.HeaderBar
import com.jn.dropbit.features.common.ui.NeonCard
import com.jn.dropbit.features.common.ui.NeonText
import com.jn.dropbit.features.shop.ShopIntent
import com.jn.dropbit.features.shop.ShopState
import com.jn.dropbit.features.shop.ShopViewModel
import com.jn.dropbit.findActivity
import com.jn.dropbit.ui.theme.DropbitTheme
import com.jn.dropbit.ui.theme.NeonBlue
import com.jn.dropbit.ui.theme.NeonGreen
import com.jn.dropbit.ui.theme.NeonOrange
import com.jn.dropbit.ui.theme.NeonPink
import com.jn.dropbit.ui.theme.NeonPurple
import com.jn.dropbit.ui.theme.ensureContrast

@Composable
fun ShopCoinScreen(viewModel: ShopViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }

    ShopCoinScreenContent(
        state = state,
        onPurchase = { productId ->
            activity?.let {
                viewModel.onIntent(
                    ShopIntent.PurchaseProduct(
                        it,
                        productId
                    )
                )
            }
        },
        onRetryBilling = { viewModel.onIntent(ShopIntent.RetryBilling) },
        onBack = onBack
    )
}

@Composable
fun ShopCoinScreenContent(
    state: ShopState,
    onPurchase: (String) -> Unit,
    onRetryBilling: () -> Unit,
    onBack: () -> Unit,
) {
    DropbitScreen {
        HeaderBar(
            coins = state.coins,
            title = "COIN SHOP",
            onBackClick = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            Box(modifier = Modifier.weight(1f)) {
                when (state.billingState) {
                    BillingState.CONNECTING -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = NeonBlue)
                        }
                    }

                    BillingState.FAILURE -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = NeonPink,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(Modifier.height(16.dp))
                            Text("Store currently unavailable", color = Color.White)
                            Button(onClick = onRetryBilling) { Text("RETRY") }
                        }
                    }

                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.products.sortedBy { it.amount }) { product ->
                                CoinPackageCard(product) { onPurchase(product.id) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CoinPackageCard(product: ShopProduct, onClick: () -> Unit) {
    val amount = product.amount
    val color = when {
        amount <= 500 -> NeonBlue
        amount <= 1500 -> NeonGreen
        amount <= 2500 -> NeonPurple
        amount <= 3500 -> NeonOrange
        else -> NeonPink
    }
    val price = product.price

    NeonCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = color,
        borderWidth = 1.dp,
        glowRadius = 4.dp,
        cornerRadius = 16.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.AttachMoney,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                NeonText(
                    product.title,
                    color = color,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black
                )
            }
            Spacer(Modifier.width(16.dp))
            Text(
                price,
                color = color.ensureContrast(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShopCoinScreenPreview() {
    DropbitTheme {
        ShopCoinScreenContent(
            state = ShopState(
                coins = INITIAL_COINS,
                billingState = BillingState.IDLE,
                products = BillingManager.getMockProducts()
            ),
            onPurchase = {},
            onRetryBilling = {},
        ) {
            // onBack
        }
    }
}
