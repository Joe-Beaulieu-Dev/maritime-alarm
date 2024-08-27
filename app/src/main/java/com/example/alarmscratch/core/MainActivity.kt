package com.example.alarmscratch.core

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.alarmscratch.core.navigation.AlarmApp
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlarmScratchTheme {
                AlarmApp()
            }
        }
    }
}
