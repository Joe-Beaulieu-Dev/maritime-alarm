package com.joebsource.lavalarm.core.ui.core.component

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.runtime.remember
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
import com.joebsource.lavalarm.R
import com.joebsource.lavalarm.alarm.data.preview.consistentFutureAlarm
import com.joebsource.lavalarm.alarm.data.preview.snoozedAlarm
import com.joebsource.lavalarm.core.extension.LocalDateTimeUtil
import com.joebsource.lavalarm.core.extension.toCountdownString
import com.joebsource.lavalarm.core.navigation.Destination
import com.joebsource.lavalarm.core.ui.permission.Permission
import com.joebsource.lavalarm.core.ui.permission.SimplePermissionGate
import com.joebsource.lavalarm.core.ui.theme.DarkGrey
import com.joebsource.lavalarm.core.ui.theme.LavalarmTheme

@Composable
fun NextAlarmCloud(
    currentCoreDestination: Destination,
    previousCoreDestination: Destination,
    modifier: Modifier = Modifier,
    nextAlarmCloudViewModel: NextAlarmCloudViewModel = viewModel(factory = NextAlarmCloudViewModel.Factory)
) {
    // State
    val alarmCountdownState by nextAlarmCloudViewModel.alarmCountdownState.collectAsState()
    val onScreenWithAlarmCloudText = currentCoreDestination is Destination.AlarmListScreen
    val comingFromScreenWithAlarmCloudText = previousCoreDestination is Destination.AlarmListScreen
    val visibleState = remember(key1 = currentCoreDestination, key2 = previousCoreDestination) {
        // True == text displayed
        // False == text not displayed
        val initialState = when {
            onScreenWithAlarmCloudText && comingFromScreenWithAlarmCloudText ->
                true
            onScreenWithAlarmCloudText && !comingFromScreenWithAlarmCloudText ->
                false
            !onScreenWithAlarmCloudText && comingFromScreenWithAlarmCloudText ->
                true
            else ->
                // !onScreenWithAlarmCloudText && !comingFromScreenWithAlarmCloudText
                false
        }

        MutableTransitionState(initialState = initialState).apply { targetState = onScreenWithAlarmCloudText }
    }

    NextAlarmCloudContent(
        currentCoreDestination = currentCoreDestination,
        alarmCountdownState = alarmCountdownState,
        visibleState = visibleState,
        timeChangeReceiver = nextAlarmCloudViewModel.timeChangeReceiver,
        modifier = modifier
    )
}

@Composable
fun NextAlarmCloudContent(
    currentCoreDestination: Destination,
    alarmCountdownState: AlarmCountdownState,
    visibleState: MutableTransitionState<Boolean>,
    timeChangeReceiver: BroadcastReceiver,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Manage the BroadcastReceiver for keeping the Alarm Countdown Text up to date
    DisposableEffect(key1 = context, key2 = currentCoreDestination) {
        // Register BroadcastReceiver
        if (currentCoreDestination is Destination.AlarmListScreen) {
            val intentFilter = IntentFilter().apply {
                addAction(Intent.ACTION_TIME_TICK)
                addAction(Intent.ACTION_TIME_CHANGED)
                addAction(Intent.ACTION_DATE_CHANGED)
                addAction(Intent.ACTION_TIMEZONE_CHANGED)
            }
            ContextCompat.registerReceiver(
                context,
                timeChangeReceiver,
                intentFilter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }

        // Unregister BroadcastReceiver
        onDispose {
            try {
                context.unregisterReceiver(timeChangeReceiver)
            } catch (e: Exception) {
                // Receiver was never registered in the first place. Nothing to do here. Just don't crash.
            }
        }
    }

    val notificationGatedIconAndText: @Composable () -> Unit = {
        AlarmIconAndText(
            alarmCountdownState = alarmCountdownState,
            visibleState = visibleState
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .width(120.dp)
            .heightIn(min = 50.dp)
            .clip(shape = CircleShape)
            .background(color = Color.White)
    ) {
        // POST_NOTIFICATIONS permission was introduced in API 33 (TIRAMISU).
        // SCHEDULE_EXACT_ALARM permission is only required for APIs < 33 because
        // alarm apps can instead use USE_EXACT_ALARM on API 33+ which cannot be revoked.
        // Therefore, we only need to ask for one or the other, never both.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            SimplePermissionGate(
                permission = Permission.PostNotifications,
                gatedComposable = notificationGatedIconAndText
            )
        } else {
            SimplePermissionGate(
                permission = Permission.ScheduleExactAlarm,
                gatedComposable = notificationGatedIconAndText
            )
        }
    }
}

@Composable
private fun AlarmIconAndText(
    alarmCountdownState: AlarmCountdownState,
    visibleState: MutableTransitionState<Boolean>
) {
    // Alarm Icon and Countdown Text
    // This Animation is to match the NavHost's
    if (alarmCountdownState is AlarmCountdownState.Success) {
        AnimatedVisibility(
            visibleState = visibleState,
            enter = fadeIn(animationSpec = tween(durationMillis = 400)),
            exit = fadeOut(animationSpec = tween(durationMillis = 400))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                // Alarm Icon
                Icon(
                    imageVector = alarmCountdownState.icon,
                    contentDescription = null,
                    tint = DarkGrey,
                    modifier = Modifier.padding(end = 4.dp, bottom = 2.dp)
                )

                // Countdown Text
                Text(
                    text = alarmCountdownState.countdownText,
                    color = DarkGrey,
                    fontSize = getCountdownTextFontSize(alarmCountdownState.countdownText),
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = getCountdownTextLineHeight(alarmCountdownState.countdownText)
                )
            }
        }
    }
}

private fun getCountdownTextFontSize(countdownText: String): TextUnit =
    when {
        countdownText.length > 7 -> // Small
            14.sp
        countdownText.length in 5..7 -> // Medium: Default Font Size
            TextUnit.Unspecified
        else -> // Large: length < 5
            20.sp
    }

private fun getCountdownTextLineHeight(countdownText: String): TextUnit =
    when {
        countdownText.length > 7 -> // Small
            16.sp
        countdownText.length in 5..7 -> // Medium: Default Line Height
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
    LavalarmTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            NextAlarmCloudContent(
                currentCoreDestination = Destination.AlarmListScreen,
                alarmCountdownState = AlarmCountdownState.Success(
                    icon = Icons.Default.AlarmOff,
                    countdownText = stringResource(id = R.string.no_active_alarms)
                ),
                visibleState = MutableTransitionState(true),
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

    LavalarmTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            NextAlarmCloudContent(
                currentCoreDestination = Destination.AlarmListScreen,
                alarmCountdownState = AlarmCountdownState.Success(
                    icon = Icons.Default.Alarm,
                    countdownText = alarm.toCountdownString(LocalContext.current)
                ),
                visibleState = MutableTransitionState(true),
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

    LavalarmTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            NextAlarmCloudContent(
                currentCoreDestination = Destination.AlarmListScreen,
                alarmCountdownState = AlarmCountdownState.Success(
                    icon = Icons.Default.Alarm,
                    countdownText = alarm.toCountdownString(LocalContext.current)
                ),
                visibleState = MutableTransitionState(true),
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
    LavalarmTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            NextAlarmCloudContent(
                currentCoreDestination = Destination.AlarmListScreen,
                alarmCountdownState = AlarmCountdownState.Success(
                    icon = Icons.Default.Alarm,
                    countdownText = consistentFutureAlarm.toCountdownString(LocalContext.current)
                ),
                visibleState = MutableTransitionState(true),
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

    LavalarmTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            NextAlarmCloudContent(
                currentCoreDestination = Destination.AlarmListScreen,
                alarmCountdownState = AlarmCountdownState.Success(
                    icon = Icons.Default.Alarm,
                    countdownText = alarm.toCountdownString(LocalContext.current)
                ),
                visibleState = MutableTransitionState(true),
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
    LavalarmTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            NextAlarmCloudContent(
                currentCoreDestination = Destination.AlarmListScreen,
                alarmCountdownState = AlarmCountdownState.Success(
                    icon = Icons.Default.Snooze,
                    countdownText = snoozedAlarm.toCountdownString(LocalContext.current)
                ),
                visibleState = MutableTransitionState(true),
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
    LavalarmTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            NextAlarmCloudContent(
                currentCoreDestination = Destination.SettingsScreen,
                alarmCountdownState = AlarmCountdownState.Success(
                    icon = Icons.Default.Alarm,
                    countdownText = consistentFutureAlarm.toCountdownString(LocalContext.current)
                ),
                visibleState = MutableTransitionState(false),
                timeChangeReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {}
                }
            )
        }
    }
}
