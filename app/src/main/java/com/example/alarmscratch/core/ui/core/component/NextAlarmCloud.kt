package com.example.alarmscratch.core.ui.core.component

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
import com.example.alarmscratch.alarm.data.repository.AlarmState
import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import com.example.alarmscratch.core.extension.isSnoozed
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
    val nextAlarmState by nextAlarmCloudViewModel.nextAlarm.collectAsState()

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
    // State
    val localContext = LocalContext.current
    var countdownText by rememberSaveable { mutableStateOf("") }
    var fontSize: TextUnit
    var lineHeight: TextUnit

    // Manage the BroadcastReceiver for keeping the Alarm Countdown Text up to date
    DisposableEffect(key1 = localContext, key2 = selectedNavComponentDest) {
        val timeChangeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (context != null && intent?.action == Intent.ACTION_TIME_TICK) {
                    countdownText = getCountdownText(context, nextAlarmState)
                }
            }
        }

        if (selectedNavComponentDest is Destination.AlarmListScreen) {
            ContextCompat.registerReceiver(
                localContext,
                timeChangeReceiver,
                IntentFilter(Intent.ACTION_TIME_TICK),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }

        onDispose {
            try {
                localContext.unregisterReceiver(timeChangeReceiver)
            } catch (e: Exception) {
                // Receiver was never registered in the first place. Nothing to do here. Just don't crash.
            }
        }
    }

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
                .width(120.dp)
                .heightIn(min = 50.dp)
                .padding(horizontal = 8.dp)
        ) {
            if (selectedNavComponentDest == Destination.AlarmListScreen) {
                // You cannot simultaneously apply both fontSize and lineHeight to the same AnnotatedString.
                // Therefore a normal String is used, and these values will be applied directly to the Text Composable.
                countdownText = getCountdownText(localContext, nextAlarmState)
                if (countdownText.length > 7) { // Small
                    fontSize = 14.sp
                    lineHeight = 16.sp
                } else if (countdownText.length in 5..7) { // Medium
                    // Default Text properties
                    fontSize = TextUnit.Unspecified
                    lineHeight = TextUnit.Unspecified
                } else { // Large: length < 5
                    fontSize = 20.sp
                    lineHeight = 22.sp
                }

                // Alarm Icon
                Icon(
                    imageVector = getAlarmIcon(nextAlarmState),
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

private fun getAlarmIcon(alarmState: AlarmState): ImageVector =
    if (alarmState is AlarmState.Success) {
        if (alarmState.alarm.isSnoozed()) {
            Icons.Default.Snooze
        } else {
            Icons.Default.Alarm
        }
    } else {
        Icons.Default.AlarmOff
    }

private fun getCountdownText(context: Context, alarmState: AlarmState): String =
    if (alarmState is AlarmState.Success) {
        alarmState.alarm.toCountdownString(context)
    } else {
        context.getString(R.string.no_active_alarms)
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
                        dateTime = LocalDateTimeUtil.nowTruncated().plusDays(12).plusHours(10).plusMinutes(45)
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
                        dateTime = LocalDateTimeUtil.nowTruncated().plusHours(20).plusMinutes(45)
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
private fun NextAlarmCloudLargeText1Preview() {
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
                        dateTime = LocalDateTimeUtil.nowTruncated().plusMinutes(1)
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
