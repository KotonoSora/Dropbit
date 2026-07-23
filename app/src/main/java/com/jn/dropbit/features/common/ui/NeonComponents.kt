package com.jn.dropbit.features.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.jn.dropbit.ui.theme.DropbitTheme
import com.jn.dropbit.ui.theme.NeonBlue
import com.jn.dropbit.ui.theme.NeonPurple
import com.jn.dropbit.ui.theme.SurfaceDark
import com.jn.dropbit.ui.theme.ensureContrast
import com.jn.dropbit.ui.theme.neonBorder

@Composable
fun NeonCard(
    modifier: Modifier = Modifier,
    color: Color,
    borderWidth: Dp = 2.dp,
    glowRadius: Dp = 4.dp,
    cornerRadius: Dp = 16.dp,
    containerColor: Color = SurfaceDark.copy(alpha = 0.5f),
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .neonBorder(
                color = color,
                width = borderWidth,
                glowRadius = glowRadius,
                cornerRadius = cornerRadius
            )
            .background(containerColor, RoundedCornerShape(cornerRadius)),
        content = content
    )
}

@Composable
fun NeonText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    textColor: Color = color.ensureContrast(),
    style: TextStyle = LocalTextStyle.current,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    glowRadius: Dp = 8.dp,
    glowAlpha: Float = 0.6f
) {
    Text(
        text = text,
        modifier = modifier,
        color = textColor,
        style = style.copy(
            shadow = Shadow(
                color = color.copy(alpha = glowAlpha),
                blurRadius = glowRadius.value * 2.5f,
                offset = Offset.Zero
            )
        ),
        fontWeight = fontWeight,
        textAlign = textAlign,
        lineHeight = lineHeight,
        letterSpacing = letterSpacing
    )
}

@Composable
fun NeonButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color,
    enabled: Boolean = true,
    cornerRadius: Dp = 16.dp,
    glowRadius: Dp = 6.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val alpha = if (enabled) 1f else 0.3f
    val effectiveColor = color.copy(alpha = alpha)

    Box(
        modifier = modifier
            .neonBorder(
                color = effectiveColor,
                glowRadius = if (enabled) glowRadius else 0.dp,
                cornerRadius = cornerRadius
            )
            .background(
                color = effectiveColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(cornerRadius)
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
        content = content
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0D1117)
@Composable
fun NeonCardPreview() {
    DropbitTheme {
        Box(modifier = Modifier.padding(20.dp)) {
            NeonCard(
                modifier = Modifier.size(200.dp, 100.dp),
                color = NeonBlue
            ) {
                Text(
                    "Neon Card",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D1117)
@Composable
fun NeonTextPreview() {
    DropbitTheme {
        Box(modifier = Modifier.padding(20.dp)) {
            NeonText(
                text = "NEON GLOW",
                color = NeonPurple,
                style = MaterialTheme.typography.headlineLarge
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D1117)
@Composable
fun NeonButtonPreview() {
    DropbitTheme {
        Box(modifier = Modifier.padding(20.dp)) {
            NeonButton(
                onClick = {},
                color = NeonBlue,
                modifier = Modifier.size(200.dp, 60.dp)
            ) {
                Text("NEON BUTTON", color = NeonBlue, fontWeight = FontWeight.Bold)
            }
        }
    }
}
