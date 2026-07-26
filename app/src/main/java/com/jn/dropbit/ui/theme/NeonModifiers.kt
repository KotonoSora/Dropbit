package com.jn.dropbit.ui.theme

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.neonGlow(
    color: Color,
    radius: Dp = 8.dp,
    alpha: Float = 0.5f,
) = this.drawBehind {
    val blurRadius = radius.toPx()
    if (blurRadius <= 0f) return@drawBehind

    drawIntoCanvas { canvas ->
        val paint = Paint()
        paint.color = color.copy(alpha = alpha).toArgb()
        paint.maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.OUTER)

        canvas.nativeCanvas.drawRect(
            0f, 0f, size.width, size.height,
            paint
        )
    }
}

fun Modifier.neonBorder(
    color: Color,
    width: Dp = 2.dp,
    glowRadius: Dp = 4.dp,
    cornerRadius: Dp = 16.dp,
) = this.drawBehind {
    val strokeWidth = width.toPx()
    val glowWidth = glowRadius.toPx()
    val cornerRadiusPx = cornerRadius.toPx()

    drawIntoCanvas { canvas ->
        if (glowWidth > 0f) {
            val paint = Paint()
            paint.color = color.toArgb()
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = strokeWidth
            paint.maskFilter = BlurMaskFilter(glowWidth, BlurMaskFilter.Blur.OUTER)

            canvas.nativeCanvas.drawRoundRect(
                strokeWidth / 2,
                strokeWidth / 2,
                size.width - (strokeWidth / 2),
                size.height - (strokeWidth / 2),
                cornerRadiusPx,
                cornerRadiusPx,
                paint,
            )
        }
    }

    // Draw Sharp Border
    drawRoundRect(
        color = color,
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadiusPx),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
    )
}
