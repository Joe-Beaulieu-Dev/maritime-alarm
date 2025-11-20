package com.octrobi.lavalarm.core.ui.core.component

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.octrobi.lavalarm.R
import com.octrobi.lavalarm.alarm.data.preview.consistentFutureAlarm
import com.octrobi.lavalarm.alarm.data.preview.snoozedAlarm
import com.octrobi.lavalarm.core.extension.LocalDateTimeUtil
import com.octrobi.lavalarm.core.extension.toCountdownString
import com.octrobi.lavalarm.core.navigation.Destination
import com.octrobi.lavalarm.core.ui.shared.SailBoat
import com.octrobi.lavalarm.core.ui.theme.BoatHull
import com.octrobi.lavalarm.core.ui.theme.BoatSails
import com.octrobi.lavalarm.core.ui.theme.LavalarmTheme
import com.octrobi.lavalarm.core.ui.theme.SkyBlue

@Composable
fun SkylineHeader(
    alarmCountdownState: State<AlarmCountdownState>,
    currentCoreDestination: Destination,
    previousCoreDestination: Destination,
    timeChangeReceiver: BroadcastReceiver,
    modifier: Modifier = Modifier
) {
    SkylineHeaderContent(
        nextAlarmIndicator = {
            NextAlarmCloud(
                alarmCountdownState = alarmCountdownState,
                currentCoreDestination = currentCoreDestination,
                previousCoreDestination = previousCoreDestination,
                timeChangeReceiver = timeChangeReceiver
            )
        },
        modifier = modifier
    )
}

@Composable
fun SkylineHeaderContent(
    nextAlarmIndicator: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Sky with Clouds and Boat
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(color = SkyBlue)
        ) {
            // Leftmost Cloud
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .width(50.dp)
                    .height(30.dp)
                    .offset(x = 10.dp, y = 35.dp)
                    .clip(shape = CircleShape)
                    .background(color = Color.White)
            )

            // Second from Left Small Cloud
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .width(30.dp)
                    .height(20.dp)
                    .offset(x = (-110).dp, y = 5.dp)
                    .clip(shape = CircleShape)
                    .background(color = Color.White)
            )

            // Next Alarm Indicator
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(x = 0.dp, y = 5.dp)
            ) {
                // Small part of Cloud
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .width(75.dp)
                        .height(30.dp)
                        .offset(x = (-22).dp, y = 5.dp)
                        .clip(shape = CircleShape)
                        .background(color = Color.White)
                )

                // Large part of Cloud
                nextAlarmIndicator()
            }

            // Sun
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .width(50.dp)
                    .height(50.dp)
                    .offset(x = (-20).dp, y = 0.dp)
                    .clip(shape = CircleShape)
                    .background(color = Color.Yellow)
            )

            // Sun Cloud
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .width(90.dp)
                    .height(30.dp)
                    .offset(x = (-10).dp, y = 20.dp)
                    .clip(shape = CircleShape)
                    .background(color = Color.White)
            )

            // Boat
            SailBoat(
                boatSize = 30.dp,
                hullColor = BoatHull,
                sailColor = BoatSails,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 65.dp, y = 1.dp)
            )
        }
    }
}

/*
 * Preview
 */

@Preview
@Composable
private fun SkylineHeaderOneLineAlarmPreview() {
    val countdownText = consistentFutureAlarm.toCountdownString(LocalContext.current)
    val alarmCountdownState = remember {
        mutableStateOf(
            AlarmCountdownState.Success(
                icon = Icons.Default.Alarm,
                countdownText = countdownText
            )
        )
    }

    LavalarmTheme {
        SkylineHeaderContent(
            nextAlarmIndicator = {
                NextAlarmCloud(
                    alarmCountdownState = alarmCountdownState,
                    currentCoreDestination = Destination.AlarmListScreen,
                    previousCoreDestination = Destination.AlarmListScreen,
                    timeChangeReceiver = object : BroadcastReceiver() {
                        override fun onReceive(context: Context?, intent: Intent?) {}
                    }
                )
            }
        )
    }
}

@Preview
@Composable
private fun SkylineHeaderTwoLineAlarmPreview() {
    val alarm = consistentFutureAlarm.copy(
        dateTime = LocalDateTimeUtil.nowTruncated().plusDays(12).plusHours(10).plusMinutes(45)
    )
    val countdownText = alarm.toCountdownString(LocalContext.current)
    val alarmCountdownState = remember {
        mutableStateOf(
            AlarmCountdownState.Success(
                icon = Icons.Default.Alarm,
                countdownText = countdownText
            )
        )
    }

    LavalarmTheme {
        SkylineHeaderContent(
            nextAlarmIndicator = {
                NextAlarmCloud(
                    alarmCountdownState = alarmCountdownState,
                    currentCoreDestination = Destination.AlarmListScreen,
                    previousCoreDestination = Destination.AlarmListScreen,
                    timeChangeReceiver = object : BroadcastReceiver() {
                        override fun onReceive(context: Context?, intent: Intent?) {}
                    }
                )
            }
        )
    }
}

@Preview
@Composable
private fun SkylineHeaderSnoozedAlarmPreview() {
    val countdownText = snoozedAlarm.toCountdownString(LocalContext.current)
    val alarmCountdownState = remember {
        mutableStateOf(
            AlarmCountdownState.Success(
                icon = Icons.Default.Snooze,
                countdownText = countdownText
            )
        )
    }

    LavalarmTheme {
        SkylineHeaderContent(
            nextAlarmIndicator = {
                NextAlarmCloud(
                    alarmCountdownState = alarmCountdownState,
                    currentCoreDestination = Destination.AlarmListScreen,
                    previousCoreDestination = Destination.AlarmListScreen,
                    timeChangeReceiver = object : BroadcastReceiver() {
                        override fun onReceive(context: Context?, intent: Intent?) {}
                    }
                )
            }
        )
    }
}

@Preview
@Composable
private fun SkylineHeaderNoAlarmsPreview() {
    val countdownText = stringResource(id = R.string.no_active_alarms)
    val alarmCountdownState = remember {
        mutableStateOf(
            AlarmCountdownState.Success(
                icon = Icons.Default.AlarmOff,
                countdownText = countdownText
            )
        )
    }

    LavalarmTheme {
        SkylineHeaderContent(
            nextAlarmIndicator = {
                NextAlarmCloud(
                    alarmCountdownState = alarmCountdownState,
                    currentCoreDestination = Destination.AlarmListScreen,
                    previousCoreDestination = Destination.AlarmListScreen,
                    timeChangeReceiver = object : BroadcastReceiver() {
                        override fun onReceive(context: Context?, intent: Intent?) {}
                    }
                )
            }
        )
    }
}

@Preview
@Composable
private fun SkylineHeaderSettingsScreenPreview() {
    val countdownText = consistentFutureAlarm.toCountdownString(LocalContext.current)
    val alarmCountdownState = remember {
        mutableStateOf(
            AlarmCountdownState.Success(
                icon = Icons.Default.Alarm,
                countdownText = countdownText
            )
        )
    }

    LavalarmTheme {
        SkylineHeaderContent(
            nextAlarmIndicator = {
                NextAlarmCloud(
                    alarmCountdownState = alarmCountdownState,
                    currentCoreDestination = Destination.SettingsScreen,
                    previousCoreDestination = Destination.AlarmListScreen,
                    timeChangeReceiver = object : BroadcastReceiver() {
                        override fun onReceive(context: Context?, intent: Intent?) {}
                    }
                )
            }
        )
    }
}
