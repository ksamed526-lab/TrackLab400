package com.tracklab400.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

object TrackLabSpacing {
    val xxs = 2.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 48.dp
}

object TrackLabRadius {
    val small = 10.dp
    val medium = 14.dp
    val large = 20.dp
    val extraLarge = 28.dp
}

val TrackLabShapes = Shapes(
    extraSmall = RoundedCornerShape(TrackLabRadius.small),
    small = RoundedCornerShape(TrackLabRadius.medium),
    medium = RoundedCornerShape(TrackLabRadius.large),
    large = RoundedCornerShape(TrackLabRadius.extraLarge),
    extraLarge = RoundedCornerShape(32.dp),
)
