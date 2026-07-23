package com.jn.dropbit.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance

val DarkBackground = Color(0xFF0D1117)
val SurfaceDark = Color(0xFF161B22)
val NeonBlue = Color(0xFF00D2FF)
val NeonGreen = Color(0xFF39FF14)
val NeonPink = Color(0xFFFF007F)
val NeonPurple = Color(0xFFB026FF)
val NeonOrange = Color(0xFFFFAC1C)

fun Color.softNeon(): Color = lerp(this, Color.White, 0.7f)

/**
 * Ensures that this color has enough contrast against the provided background color.
 * If the contrast ratio is below the target, it will be lightened towards white.
 */
fun Color.ensureContrast(background: Color = DarkBackground, targetRatio: Float = 4.5f): Color {
    val bgLuminance = background.luminance()
    var currentText = this

    // Simplistic approach: if text is darker than background, we probably want to lighten it anyway 
    // for this specific neon theme.
    repeat(10) {
        val textLuminance = currentText.luminance()
        val contrast = if (textLuminance > bgLuminance) {
            (textLuminance + 0.05f) / (bgLuminance + 0.05f)
        } else {
            (bgLuminance + 0.05f) / (textLuminance + 0.05f)
        }

        if (contrast >= targetRatio) return currentText
        currentText = lerp(currentText, Color.White, 0.15f)
    }

    return currentText
}
