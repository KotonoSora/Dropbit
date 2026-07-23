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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jn.dropbit.billing.BillingState
import com.jn.dropbit.features.common.ui.DropbitScreen
import com.jn.dropbit.features.common.ui.HeaderBar
import com.jn.dropbit.features.common.ui.NeonButton
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
import kotlinx.coroutines.delay

@Composable
fun ShopCoinScreen(viewModel: ShopViewModel, onNavigateToSkins: () -> Unit, onBack: () -> Unit) {
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
        onWatchAd = { viewModel.onIntent(ShopIntent.WatchAd) },
        onNavigateToSkins = onNavigateToSkins,
        onBack = onBack
    )
}

@Composable
fun ShopCoinScreenContent(
    state: ShopState,
    onPurchase: (String) -> Unit,
    onRetryBilling: () -> Unit,
    onWatchAd: () -> Unit,
    onNavigateToSkins: () -> Unit,
    onBack: () -> Unit,
) {
    var timeLeftMillis by remember { mutableStateOf(0L) }

    val coinPackages = listOf(
        CoinPackage("Starter Pack", 100, "$0.29", "coins_100", NeonBlue),
        CoinPackage("Mini Pack", 500, "$0.49", "coins_500", NeonGreen),
        CoinPackage("Midi Pack", 1000, "$0.69", "coins_1000", NeonPurple),
        CoinPackage("Common Pack", 1500, "$0.99", "coins_1500", NeonBlue),
        CoinPackage("Pro Pack", 2000, "$1.99", "coins_2000", NeonGreen),
        CoinPackage("Ultra Pack", 2500, "$3.99", "coins_2500", NeonPurple),
        CoinPackage("Legend Pack", 3000, "$4.99", "coins_3000", NeonOrange),
        CoinPackage("God Pack", 3500, "$7.99", "coins_3500", NeonPink),
        CoinPackage("Supreme Pack", 4000, "$9.99", "coins_4000", NeonOrange),
    )

    LaunchedEffect(state.lastAdTime) {
        while (true) {
            val now = System.currentTimeMillis()
            val elapsed = now - state.lastAdTime
            val cooldown = 5 * 60 * 1000L
            timeLeftMillis = if (elapsed < cooldown) cooldown - elapsed else 0L
            delay(1000)
        }
    }

    DropbitScreen {
        HeaderBar(coins = state.coins)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NeonText(
                "COIN SHOP",
                style = MaterialTheme.typography.headlineMedium,
                color = NeonBlue,
                fontWeight = FontWeight.Black
            )
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
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(coinPackages) { pkg ->
                                CoinPackageCard(pkg) { onPurchase(pkg.productId) }
                            }
                        }
                    }
                }
            }

            AdRewardCard(timeLeftMillis) { if (timeLeftMillis <= 0) onWatchAd() }
            Spacer(Modifier.height(16.dp))
            NeonButton(
                onClick = onNavigateToSkins,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                color = NeonPink,
                glowRadius = 4.dp
            ) {
                Text("GO TO SKIN SHOP", color = NeonPink, fontWeight = FontWeight.Bold)
            }
            IconButton(
                onClick = onBack,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = NeonBlue
                )
            }
        }
    }
}

data class CoinPackage(
    val name: String,
    val amount: Int,
    val price: String,
    val productId: String,
    val color: Color
)

@Composable
fun CoinPackageCard(pkg: CoinPackage, onClick: () -> Unit) {
    NeonCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = pkg.color,
        borderWidth = 1.dp,
        glowRadius = 4.dp,
        cornerRadius = 16.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = pkg.color,
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                pkg.name,
                color = Color.White.copy(alpha = 0.9f),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            NeonText(
                "${pkg.amount} Coins",
                color = pkg.color,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black
            )
            Spacer(Modifier.height(8.dp))
            Text(
                pkg.price,
                color = pkg.color.ensureContrast(),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun AdRewardCard(timeLeftMillis: Long, onClick: () -> Unit) {
    val isAvailable = timeLeftMillis <= 0
    val minutes = (timeLeftMillis / 1000) / 60
    val seconds = (timeLeftMillis / 1000) % 60

    NeonCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isAvailable) { onClick() },
        color = if (isAvailable) NeonPurple else Color.DarkGray,
        glowRadius = if (isAvailable) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = if (isAvailable) NeonPurple else Color.Gray,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        "WATCH AD",
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Reward: 50 Coins",
                        color = (if (isAvailable) NeonPurple else Color.Gray).ensureContrast(),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            if (!isAvailable) {
                Text(
                    "${minutes.toString().padStart(2, '0')}:${
                        seconds.toString().padStart(2, '0')
                    }", color = NeonPink, fontWeight = FontWeight.Bold
                )
            } else {
                Text("CLAIM", color = NeonGreen, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShopCoinScreenPreview() {
    DropbitTheme {
        ShopCoinScreenContent(
            state = ShopState(coins = 500, billingState = BillingState.IDLE),
            onPurchase = {},
            onRetryBilling = {},
            onWatchAd = {},
            onNavigateToSkins = {},
        ) {
            // onBack
        }
    }
}
