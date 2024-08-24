package com.example.alarmscratch.core.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
data class CoreNavComponent2(
    @StringRes val navNameRes: Int,
    @DrawableRes val navIconRes: Int
)

class CoreNavComponent(
    @StringRes val navNameRes: Int,
    val navIcon: ImageVector
)
