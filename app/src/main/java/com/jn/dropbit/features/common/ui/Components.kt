package com.jn.dropbit.features.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jn.dropbit.ui.theme.DarkBackground
import com.jn.dropbit.ui.theme.DropbitTheme
import com.jn.dropbit.ui.theme.NeonBlue
import com.jn.dropbit.ui.theme.NeonGreen
import com.jn.dropbit.ui.theme.NeonOrange
import com.jn.dropbit.ui.theme.NeonPurple
import com.jn.dropbit.ui.theme.ensureContrast

@Composable
fun DropbitScreen(
    modifier: Modifier = Modifier,
    useSystemBarsPadding: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .then(if (useSystemBarsPadding) Modifier.systemBarsPadding() else Modifier)
        ) {
            content()
        }
    }
}

@Composable
fun HeaderBar(
    coins: Int,
    showShopIcon: Boolean = false,
    onShopClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NeonCard(
            color = NeonOrange,
            glowRadius = 2.dp,
            cornerRadius = 12.dp,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Coins",
                    tint = NeonOrange,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = coins.toString(),
                    color = NeonOrange.ensureContrast(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (showShopIcon) {
            NeonButton(
                onClick = onShopClick,
                color = NeonBlue,
                cornerRadius = 12.dp,
                glowRadius = 2.dp,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Shop",
                    tint = NeonBlue
                )
            }
        } else {
            // Spacer to maintain layout balance if needed, or just empty
            Spacer(modifier = Modifier.size(40.dp))
        }
    }
}

@Composable
fun MenuButton(
    text: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    NeonButton(
        onClick = onClick,
        color = color,
        cornerRadius = 16.dp,
        glowRadius = 4.dp,
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = text,
                color = color.ensureContrast(),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 16.sp,
                    letterSpacing = 1.sp
                ),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SmallMenuButton(
    text: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    NeonButton(
        onClick = onClick,
        color = color,
        cornerRadius = 20.dp,
        glowRadius = 6.dp,
        modifier = modifier.height(100.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(8.dp))
            Text(
                text = text,
                color = color.ensureContrast(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D1117)
@Composable
fun HeaderBarPreview() {
    DropbitTheme {
        HeaderBar(coins = 1234) {
            // onShopClick
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D1117)
@Composable
fun MenuButtonPreview() {
    DropbitTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            MenuButton(
                text = "PLAY GAME",
                icon = Icons.Default.PlayArrow,
                color = NeonGreen,
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D1117)
@Composable
fun SmallMenuButtonPreview() {
    DropbitTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            SmallMenuButton(
                text = "DAILY CHALLENGE",
                icon = Icons.Default.Star,
                color = NeonPurple,
                modifier = Modifier.width(160.dp),
                onClick = {}
            )
        }
    }
}
