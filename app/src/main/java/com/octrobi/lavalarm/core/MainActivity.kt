package com.octrobi.lavalarm.core

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.octrobi.lavalarm.core.navigation.TopLevelNavHost
import com.octrobi.lavalarm.core.recovery.ForceStopRecoveryState
import com.octrobi.lavalarm.core.ui.theme.AndroidDefaultDarkScrim
import com.octrobi.lavalarm.core.ui.theme.LavalarmTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainActivityViewModel> {
        MainActivityViewModel.provideFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Create and display Splash Screen and auto switch from
        // Splash Screen theme to general app theme afterwards
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // Handle any potential Force Stop Recovery on APIs < 35.
        // APIs >= 35 have different behavior when recovering from a Forced Stop state
        // and are handled in AlarmRefreshReceiver, reacting to Intent.ACTION_LOCKED_BOOT_COMPLETED.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            handleForceStopRecoveryPreApi35()
        }

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

    fun handleForceStopRecoveryPreApi35() {
        // Listen for and react to MainActivityViewModel's determination
        // as to whether or not Force Stop Recovery is needed.
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.shouldPerformForceStopRecovery.collect { forceStopRecoveryState ->
                    if (forceStopRecoveryState is ForceStopRecoveryState.ShouldPerformRecovery) {
                        viewModel.performForceStopRecoveryPreApi35(this@MainActivity)
                    }
                }
            }
        }

        // Determine whether Force Stop Recovery is needed
        viewModel.checkForceStopPreApi35(this)
    }
}
