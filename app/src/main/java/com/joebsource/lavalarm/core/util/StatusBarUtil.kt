package com.joebsource.lavalarm.core.util

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@SuppressLint("ComposableNaming")
object StatusBarUtil {

    @Composable
    fun setLightStatusBar() {
        setStatusBarTextAndIconColor(useLightColors = true)
    }

    @Composable
    fun setDarkStatusBar() {
        setStatusBarTextAndIconColor(useLightColors = false)
    }

    @Composable
    private fun setStatusBarTextAndIconColor(useLightColors: Boolean) {
        val localView = LocalView.current
        if (!localView.isInEditMode && localView.context is Activity) {
            SideEffect {
                val window = (localView.context as Activity).window
                WindowCompat.getInsetsController(window, localView).isAppearanceLightStatusBars = useLightColors
            }
        }
    }
}
