package com.example.alarmscratch.ui.alarmlist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.alarmscratch.ui.alarmlist.composable.AlarmListScreen
import com.example.alarmscratch.ui.theme.AlarmScratchTheme

class AlarmListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlarmScratchTheme {
                AlarmListScreen()
            }
        }
    }
}
