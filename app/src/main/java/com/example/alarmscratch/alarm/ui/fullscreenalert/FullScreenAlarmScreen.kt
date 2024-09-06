package com.example.alarmscratch.alarm.ui.fullscreenalert

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.data.preview.consistentFutureAlarm
import com.example.alarmscratch.core.extension.get12HrTime
import com.example.alarmscratch.core.extension.getAmPm
import com.example.alarmscratch.core.extension.getDay
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.InCloudBlack
import com.example.alarmscratch.core.ui.theme.SkyBlue
import com.example.alarmscratch.core.util.StatusBarUtil
import java.time.LocalDateTime

@Composable
fun FullScreenAlarmScreen(
    alarmName: String,
    alarmDateTime: LocalDateTime?
) {
    // TODO: Test this on the Lock Screen
    // Configure Status Bar
    StatusBarUtil.setLightStatusBar()

    val context = LocalContext.current
    val alarmDate = alarmDateTime?.getDay() ?: context.getString(R.string.default_alarm_date)
    val alarm12HourTime = alarmDateTime?.get12HrTime() ?: context.getString(R.string.default_alarm_time)
    val alarm12HourTimePeriod = alarmDateTime?.getAmPm(context) ?: ""

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(color = SkyBlue)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        // Screen background
        BeachBackdrop()

        // Screen content
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Alarm Name, Date, and Time
            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(0.25f)
            ) {
                // Name
                Text(
                    text = alarmName,
                    color = InCloudBlack,
                    fontSize = 36.sp
                )

                // Date
                Text(
                    text = alarmDate,
                    color = InCloudBlack,
                    fontSize = 32.sp
                )

                // Time
                Row {
                    Text(
                        text = alarm12HourTime,
                        color = InCloudBlack,
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.alignByBaseline()
                    )
                    Text(
                        text = alarm12HourTimePeriod,
                        color = InCloudBlack,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.alignByBaseline()
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Snooze and Dismiss Buttons
            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(0.75f)
            ) {
                // Snooze Button
                Button(onClick = {}) {
                    Text(text = stringResource(id = R.string.snooze_alarm), fontSize = 42.sp)
                }
                Spacer(modifier = Modifier.height(48.dp))

                // Dismiss Button
                Button(onClick = {}) {
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
private fun FullScreenAlarmScreenPreview() {
    AlarmScratchTheme {
        FullScreenAlarmScreen(
            alarmName = consistentFutureAlarm.name,
            alarmDateTime = consistentFutureAlarm.dateTime
        )
    }
}
