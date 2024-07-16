package com.example.alarmscratch.core.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme

class AlarmListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlarmScratchTheme {
                AlarmApp()
            }
        }
    }
}
