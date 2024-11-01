package com.example.alarmscratch.alarm.ui.fullscreenalert

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.data.preview.consistentFutureAlarm
import com.example.alarmscratch.alarm.ui.fullscreenalert.component.BeachBackdrop
import com.example.alarmscratch.core.extension.get12HourTime
import com.example.alarmscratch.core.extension.get24HourTime
import com.example.alarmscratch.core.extension.getAmPm
import com.example.alarmscratch.core.extension.getDayFull
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.InCloudBlack
import com.example.alarmscratch.core.ui.theme.SkyBlue
import com.example.alarmscratch.core.ui.theme.TransparentBlack
import com.example.alarmscratch.core.util.StatusBarUtil
import java.time.LocalDateTime

@Composable
fun FullScreenAlarmScreen(fullScreenAlarmViewModel: FullScreenAlarmViewModel) {
    // TODO: The Status Bar has a grey tint to it on the Lock Screen. Fix this.
    // Configure Status Bar
    StatusBarUtil.setLightStatusBar()

    FullScreenAlarmScreenContent(
        alarmName = fullScreenAlarmViewModel.alarmName,
        alarmExecutionDateTime = fullScreenAlarmViewModel.alarmExecutionDateTime,
        is24Hour = fullScreenAlarmViewModel.is24Hour,
        snoozeAlarm = fullScreenAlarmViewModel::snoozeAlarm,
        dismissAlarm = fullScreenAlarmViewModel::dismissAlarm
    )
}

@Composable
fun FullScreenAlarmScreenContent(
    alarmName: String,
    alarmExecutionDateTime: LocalDateTime,
    is24Hour: Boolean,
    snoozeAlarm: (Context) -> Unit,
    dismissAlarm: (Context) -> Unit
) {
    // Alarm data
    val context = LocalContext.current
    val day = alarmExecutionDateTime.getDayFull()
    val time = if (is24Hour) alarmExecutionDateTime.get24HourTime() else alarmExecutionDateTime.get12HourTime()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(color = SkyBlue)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        // Screen background
        BeachBackdrop()

        // Alarm Data and Buttons
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Alarm Data
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(0.25f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(color = TransparentBlack)
                        .padding(12.dp)
                ) {
                    // Name
                    Text(
                        text = alarmName,
                        color = InCloudBlack,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Day
                    Text(
                        text = day,
                        color = InCloudBlack,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    // Time
                    Row {
                        // Hour and Minute
                        Text(
                            text = time,
                            color = InCloudBlack,
                            fontSize = 64.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.alignByBaseline()
                        )

                        // AM/PM
                        if (!is24Hour) {
                            Text(
                                text = alarmExecutionDateTime.getAmPm(context),
                                color = InCloudBlack,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.alignByBaseline()
                            )
                        }
                    }
                }
            }

            // Snooze and Dismiss Buttons
            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(0.75f)
            ) {
                // Snooze Button
                Button(onClick = { snoozeAlarm(context) }) {
                    Text(text = stringResource(id = R.string.snooze_alarm), fontSize = 42.sp)
                }
                Spacer(modifier = Modifier.height(48.dp))

                // Dismiss Button
                Button(onClick = { dismissAlarm(context) }) {
                    Text(text = stringResource(id = R.string.dismiss_alarm), fontSize = 42.sp)
                }
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

/*
 * Previews
 */

@Preview
@Composable
private fun FullScreenAlarmScreen12HourPreview() {
    AlarmScratchTheme {
        FullScreenAlarmScreenContent(
            alarmName = consistentFutureAlarm.name,
            alarmExecutionDateTime = consistentFutureAlarm.dateTime,
            is24Hour = false,
            snoozeAlarm = {},
            dismissAlarm = {}
        )
    }
}

@Preview
@Composable
private fun FullScreenAlarmScreen24HourPreview() {
    AlarmScratchTheme {
        FullScreenAlarmScreenContent(
            alarmName = consistentFutureAlarm.name,
            alarmExecutionDateTime = consistentFutureAlarm.dateTime,
            is24Hour = true,
            snoozeAlarm = {},
            dismissAlarm = {}
        )
    }
}
