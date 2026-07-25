package com.jn.dropbit.domain.model

import androidx.compose.ui.graphics.Color
import com.jn.dropbit.ui.theme.NeonBlue
import com.jn.dropbit.ui.theme.NeonGreen
import com.jn.dropbit.ui.theme.NeonOrange
import com.jn.dropbit.ui.theme.NeonPink
import com.jn.dropbit.ui.theme.NeonPurple

const val DEFAULT_SKIN = "Blue"

data class SkinData(val name: String, val color: Color)

val ALL_SKINS = listOf(
    SkinData(DEFAULT_SKIN, NeonBlue),
    SkinData("Red", NeonPink),
    SkinData("Green", NeonGreen),
    SkinData("Purple", NeonPurple),
    SkinData("Orange", NeonOrange)
)
