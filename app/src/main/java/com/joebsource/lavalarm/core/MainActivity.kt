package com.joebsource.lavalarm.core

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.joebsource.lavalarm.core.navigation.TopLevelNavHost
import com.joebsource.lavalarm.core.ui.theme.AndroidDefaultDarkScrim
import com.joebsource.lavalarm.core.ui.theme.LavalarmTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Create and display Splash Screen and auto switch from
        // Splash Screen theme to general app theme afterwards
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // Enable edge to edge for dynamic Status Bar coloring
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidDefaultDarkScrim.toArgb())
        )

        setContent {
            LavalarmTheme {
                TopLevelNavHost()
            }
        }
    }
}
