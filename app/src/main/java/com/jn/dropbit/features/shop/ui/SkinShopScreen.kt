package com.jn.dropbit.features.shop.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jn.dropbit.features.common.ui.DropbitScreen
import com.jn.dropbit.features.common.ui.HeaderBar
import com.jn.dropbit.features.common.ui.NeonButton
import com.jn.dropbit.features.common.ui.NeonCard
import com.jn.dropbit.features.common.ui.NeonText
import com.jn.dropbit.features.shop.ShopIntent
import com.jn.dropbit.features.shop.ShopState
import com.jn.dropbit.features.shop.ShopViewModel
import com.jn.dropbit.ui.theme.DropbitTheme
import com.jn.dropbit.ui.theme.NeonBlue
import com.jn.dropbit.ui.theme.NeonGreen
import com.jn.dropbit.ui.theme.NeonOrange
import com.jn.dropbit.ui.theme.NeonPink
import com.jn.dropbit.ui.theme.NeonPurple
import com.jn.dropbit.ui.theme.ensureContrast

@Composable
fun CharacterSelectionScreen(viewModel: ShopViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsState()
    CharacterSelectionScreenContent(
        state = state,
        onSelectSkin = { name -> viewModel.onIntent(ShopIntent.SelectSkin(name)) },
        onBuySkin = { name, price -> viewModel.onIntent(ShopIntent.BuySkin(name, price)) },
        onBack = onBack
    )
}

@Composable
fun CharacterSelectionScreenContent(
    state: ShopState,
    onSelectSkin: (String) -> Unit,
    onBuySkin: (String, Int) -> Unit,
    onBack: () -> Unit,
) {
    val skins = listOf(
        SkinData("Blue", NeonBlue, 0),
        SkinData("Red", NeonPink, 100),
        SkinData("Green", NeonGreen, 200),
        SkinData("Purple", NeonPurple, 500),
        SkinData("Orange", NeonOrange, 1000)
    )

    DropbitScreen {
        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderBar(coins = state.coins)
            NeonText(
                "SKIN SHOP",
                style = MaterialTheme.typography.headlineMedium,
                color = NeonBlue,
                fontWeight = FontWeight.Black
            )
            Spacer(Modifier.height(32.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(skins) { skin ->
                    val isUnlocked = state.unlockedSkins.contains(skin.name)
                    val isSelected = state.selectedSkin == skin.name
                    SkinItem(
                        skin = skin,
                        isUnlocked = isUnlocked,
                        isSelected = isSelected,
                        canAfford = state.coins >= skin.price,
                        onAction = {
                            if (isUnlocked) onSelectSkin(skin.name)
                            else onBuySkin(skin.name, skin.price)
                        }
                    )
                }
            }

            IconButton(onClick = onBack, modifier = Modifier.padding(top = 16.dp)) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = NeonBlue
                )
            }
        }
    }
}

data class SkinData(val name: String, val color: Color, val price: Int)

@Composable
fun SkinItem(
    skin: SkinData,
    isUnlocked: Boolean,
    isSelected: Boolean,
    canAfford: Boolean,
    onAction: () -> Unit
) {
    NeonCard(
        modifier = Modifier.fillMaxWidth(),
        color = if (isSelected) skin.color else skin.color.copy(alpha = 0.3f),
        glowRadius = if (isSelected) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(skin.color, RoundedCornerShape(8.dp))
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        skin.name.uppercase(),
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    if (!isUnlocked) Text(
                        "${skin.price} Coins",
                        color = NeonOrange.ensureContrast(),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            val buttonColor = when {
                isSelected -> NeonBlue; isUnlocked -> NeonGreen; canAfford -> skin.color; else -> Color.Gray
            }
            NeonButton(
                onClick = onAction,
                color = buttonColor,
                cornerRadius = 12.dp,
                glowRadius = 2.dp,
                enabled = isUnlocked || canAfford
            ) {
                Text(
                    text = when {
                        isSelected -> "SELECTED"; isUnlocked -> "SELECT"; else -> "BUY"
                    },
                    color = buttonColor.ensureContrast(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CharacterSelectionScreenPreview() {
    DropbitTheme {
        CharacterSelectionScreenContent(
            state = ShopState(coins = 1000, unlockedSkins = listOf("Blue", "Red")),
            onSelectSkin = {},
            onBuySkin = { _, _ -> }
        ) {
            // onBack
        }
    }
}
