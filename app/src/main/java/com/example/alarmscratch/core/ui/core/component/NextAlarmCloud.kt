package com.example.alarmscratch.core.ui.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.data.preview.consistentFutureAlarm
import com.example.alarmscratch.alarm.data.preview.snoozedAlarm
import com.example.alarmscratch.alarm.data.repository.AlarmState
import com.example.alarmscratch.core.extension.isSnoozed
import com.example.alarmscratch.core.extension.toCountdownString
import com.example.alarmscratch.core.navigation.Destination
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.InCloudBlack
import java.time.LocalDateTime

@Composable
fun NextAlarmCloud(
    selectedNavComponentDest: Destination,
    modifier: Modifier = Modifier,
    skylineHeaderViewModel: SkylineHeaderViewModel = viewModel(factory = SkylineHeaderViewModel.Factory)
) {
    // State
    val nextAlarmState by skylineHeaderViewModel.nextAlarm.collectAsState()

    NextAlarmCloudContent(
        selectedNavComponentDest = selectedNavComponentDest,
        nextAlarmState = nextAlarmState,
        modifier = modifier
    )
}

@Composable
fun NextAlarmCloudContent(
    selectedNavComponentDest: Destination,
    nextAlarmState: AlarmState,
    modifier: Modifier = Modifier
) {
    var fontSize: TextUnit
    var lineHeight: TextUnit

    Box(
        modifier = modifier
            .clip(shape = CircleShape)
            .background(color = Color.White)
    ) {
        // Next Alarm Icon and Text
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.Center)
                .width(120.dp)
                .heightIn(min = 50.dp)
                .padding(horizontal = 8.dp)
        ) {
            if (selectedNavComponentDest == Destination.AlarmListScreen) {
                // Next Alarm Data
                val alarmIcon =
                    if (nextAlarmState is AlarmState.Success) {
                        if (nextAlarmState.alarm.isSnoozed()) {
                            Icons.Default.Snooze
                        } else {
                            Icons.Default.Alarm
                        }
                    } else {
                        Icons.Default.AlarmOff
                    }
                val countdownText =
                    if (nextAlarmState is AlarmState.Success) {
                        nextAlarmState.alarm.toCountdownString(LocalContext.current)
                    } else {
                        stringResource(id = R.string.no_active_alarms)
                    }

                // You cannot simultaneously apply both fontSize and lineHeight to the same AnnotatedString.
                // Therefore a normal String is used, and these values will be applied directly to the Text Composable.
                if (countdownText.length > 7) { // Small
                    fontSize = 14.sp
                    lineHeight = 16.sp
                } else if (countdownText.length in 4..7) { // Medium
                    // Default Text properties
                    fontSize = TextUnit.Unspecified
                    lineHeight = TextUnit.Unspecified
                } else { // Large: length < 4
                    fontSize = 20.sp
                    lineHeight = 22.sp
                }

                // Alarm Icon
                Icon(
                    imageVector = alarmIcon,
                    contentDescription = null,
                    tint = InCloudBlack,
                    modifier = Modifier.padding(end = 4.dp, bottom = 2.dp)
                )
                // Countdown Text
                Text(
                    text = countdownText,
                    color = InCloudBlack,
                    fontSize = fontSize,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = lineHeight
                )
            }
        }
    }
}

/*
 * Previews
 */

@Preview(
    showBackground = true,
    backgroundColor = 0xFFc2e0ff
)
@Composable
private fun NextAlarmCloudNoAlarmsSmallText1Preview() {
    AlarmScratchTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            NextAlarmCloudContent(
                selectedNavComponentDest = Destination.AlarmListScreen,
                nextAlarmState = AlarmState.Error(Throwable())
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFc2e0ff
)
@Composable
private fun NextAlarmCloudSmallText2Preview() {
    AlarmScratchTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            NextAlarmCloudContent(
                selectedNavComponentDest = Destination.AlarmListScreen,
                nextAlarmState = AlarmState.Success(
                    alarm = consistentFutureAlarm.copy(
                        dateTime = LocalDateTime.now().plusDays(12).plusHours(10).plusMinutes(45)
                    )
                )
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFc2e0ff
)
@Composable
private fun NextAlarmCloudMediumText1Preview() {
    AlarmScratchTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            NextAlarmCloudContent(
                selectedNavComponentDest = Destination.AlarmListScreen,
                nextAlarmState = AlarmState.Success(
                    alarm = consistentFutureAlarm.copy(
                        dateTime = LocalDateTime.now().plusHours(20).plusMinutes(45)
                    )
                )
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFc2e0ff
)
@Composable
private fun NextAlarmCloudMediumText2Preview() {
    AlarmScratchTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            NextAlarmCloudContent(
                selectedNavComponentDest = Destination.AlarmListScreen,
                nextAlarmState = AlarmState.Success(alarm = consistentFutureAlarm)
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFc2e0ff
)
@Composable
private fun NextAlarmCloudSnoozedAlarmLargeTextPreview() {
    AlarmScratchTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            NextAlarmCloudContent(
                selectedNavComponentDest = Destination.AlarmListScreen,
                nextAlarmState = AlarmState.Success(alarm = snoozedAlarm)
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFc2e0ff
)
@Composable
private fun NextAlarmCloudSettingsScreenPreview() {
    AlarmScratchTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            NextAlarmCloudContent(
                selectedNavComponentDest = Destination.SettingsScreen,
                nextAlarmState = AlarmState.Success(alarm = consistentFutureAlarm)
            )
        }
    }
}
