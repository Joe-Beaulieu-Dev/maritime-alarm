package com.example.alarmscratch.core.ui.core.component

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.runtime.DisposableEffect
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.data.preview.consistentFutureAlarm
import com.example.alarmscratch.alarm.data.preview.snoozedAlarm
import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import com.example.alarmscratch.core.extension.toCountdownString
import com.example.alarmscratch.core.navigation.Destination
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.InCloudBlack

@Composable
fun NextAlarmCloud(
    selectedNavComponentDest: Destination,
    modifier: Modifier = Modifier,
    nextAlarmCloudViewModel: NextAlarmCloudViewModel = viewModel(factory = NextAlarmCloudViewModel.Factory)
) {
    // State
    val nextAlarmDisplayState by nextAlarmCloudViewModel.nextAlarmDisplay.collectAsState()
    val timeChangeReceiver = nextAlarmCloudViewModel.timeChangeReceiver

    NextAlarmCloudContent(
        selectedNavComponentDest = selectedNavComponentDest,
        nextAlarmDisplayState = nextAlarmDisplayState,
        timeChangeReceiver = timeChangeReceiver,
        modifier = modifier
    )
}

@Composable
fun NextAlarmCloudContent(
    selectedNavComponentDest: Destination,
    nextAlarmDisplayState: AlarmCloudState,
    timeChangeReceiver: BroadcastReceiver,
    modifier: Modifier = Modifier
) {
    val localContext = LocalContext.current

    // Manage the BroadcastReceiver for keeping the Alarm Countdown Text up to date
    DisposableEffect(key1 = localContext, key2 = selectedNavComponentDest) {
        // Register BroadcastReceiver
        if (selectedNavComponentDest is Destination.AlarmListScreen) {
            ContextCompat.registerReceiver(
                localContext,
                timeChangeReceiver,
                IntentFilter(Intent.ACTION_TIME_TICK),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }

        // Unregister BroadcastReceiver
        onDispose {
            try {
                localContext.unregisterReceiver(timeChangeReceiver)
            } catch (e: Exception) {
                // Receiver was never registered in the first place. Nothing to do here. Just don't crash.
            }
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .width(120.dp)
            .heightIn(min = 50.dp)
            .clip(shape = CircleShape)
            .background(color = Color.White)
    ) {
        // Alarm Icon and Countdown Text
        // This Animation is to match the NavHost's
        AnimatedVisibility(
            visible = selectedNavComponentDest == Destination.AlarmListScreen && nextAlarmDisplayState is AlarmCloudState.Success,
            enter = fadeIn(animationSpec = tween(durationMillis = 700)),
            exit = fadeOut(animationSpec = tween(durationMillis = 700))
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                // Alarm Icon
                Icon(
                    imageVector = (nextAlarmDisplayState as AlarmCloudState.Success).icon,
                    contentDescription = null,
                    tint = InCloudBlack,
                    modifier = Modifier.padding(end = 4.dp, bottom = 2.dp)
                )
                // Countdown Text
                Text(
                    text = nextAlarmDisplayState.countdownText,
                    color = InCloudBlack,
                    fontSize = getCountdownTextFontSize(nextAlarmDisplayState.countdownText),
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = getCountdownTextLineHeight(nextAlarmDisplayState.countdownText)
                )
            }
        }
    }
}

private fun getCountdownTextFontSize(countdownText: String): TextUnit =
    when {
        countdownText.length > 7 -> // Small
            14.sp
        countdownText.length in 5..7 -> // Medium: Default Text properties
            TextUnit.Unspecified
        else -> // Large: length < 5
            20.sp
    }

private fun getCountdownTextLineHeight(countdownText: String): TextUnit =
    when {
        countdownText.length > 7 -> // Small
            16.sp
        countdownText.length in 5..7 -> // Medium: Default Text properties
            TextUnit.Unspecified
        else -> // Large: length < 5
            22.sp
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
                nextAlarmDisplayState = AlarmCloudState.Success(
                    icon = Icons.Default.AlarmOff,
                    countdownText = stringResource(id = R.string.no_active_alarms)
                ),
                timeChangeReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {}
                }
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
    val alarm = consistentFutureAlarm.copy(
        dateTime = LocalDateTimeUtil.nowTruncated().plusDays(12).plusHours(10).plusMinutes(45)
    )

    AlarmScratchTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            NextAlarmCloudContent(
                selectedNavComponentDest = Destination.AlarmListScreen,
                nextAlarmDisplayState = AlarmCloudState.Success(
                    icon = Icons.Default.Alarm,
                    countdownText = alarm.toCountdownString(LocalContext.current)
                ),
                timeChangeReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {}
                }
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
    val alarm = consistentFutureAlarm.copy(
        dateTime = LocalDateTimeUtil.nowTruncated().plusHours(20).plusMinutes(45)
    )

    AlarmScratchTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            NextAlarmCloudContent(
                selectedNavComponentDest = Destination.AlarmListScreen,
                nextAlarmDisplayState = AlarmCloudState.Success(
                    icon = Icons.Default.Alarm,
                    countdownText = alarm.toCountdownString(LocalContext.current)
                ),
                timeChangeReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {}
                }
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
                nextAlarmDisplayState = AlarmCloudState.Success(
                    icon = Icons.Default.Alarm,
                    countdownText = consistentFutureAlarm.toCountdownString(LocalContext.current)
                ),
                timeChangeReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {}
                }
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFc2e0ff
)
@Composable
private fun NextAlarmCloudLargeText1Preview() {
    val alarm = consistentFutureAlarm.copy(
        dateTime = LocalDateTimeUtil.nowTruncated().plusMinutes(1)
    )

    AlarmScratchTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            NextAlarmCloudContent(
                selectedNavComponentDest = Destination.AlarmListScreen,
                nextAlarmDisplayState = AlarmCloudState.Success(
                    icon = Icons.Default.Alarm,
                    countdownText = alarm.toCountdownString(LocalContext.current)
                ),
                timeChangeReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {}
                }
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFc2e0ff
)

@Composable
private fun NextAlarmCloudSnoozedAlarmLargeText2Preview() {
    AlarmScratchTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            NextAlarmCloudContent(
                selectedNavComponentDest = Destination.AlarmListScreen,
                nextAlarmDisplayState = AlarmCloudState.Success(
                    icon = Icons.Default.Snooze,
                    countdownText = snoozedAlarm.toCountdownString(LocalContext.current)
                ),
                timeChangeReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {}
                }
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
                nextAlarmDisplayState = AlarmCloudState.Success(
                    icon = Icons.Default.Alarm,
                    countdownText = consistentFutureAlarm.toCountdownString(LocalContext.current)
                ),
                timeChangeReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {}
                }
            )
        }
    }
}
