package com.octrobi.lavalarm.core.ui.core.component

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
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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
import com.octrobi.lavalarm.R
import com.octrobi.lavalarm.alarm.data.preview.consistentFutureAlarm
import com.octrobi.lavalarm.alarm.data.preview.snoozedAlarm
import com.octrobi.lavalarm.core.extension.LocalDateTimeUtil
import com.octrobi.lavalarm.core.extension.toCountdownString
import com.octrobi.lavalarm.core.navigation.Destination
import com.octrobi.lavalarm.core.ui.notificationcheck.AppNotificationChannel
import com.octrobi.lavalarm.core.ui.notificationcheck.SimpleNotificationGate
import com.octrobi.lavalarm.core.ui.permission.Permission
import com.octrobi.lavalarm.core.ui.permission.SimplePermissionGate
import com.octrobi.lavalarm.core.ui.theme.DarkGrey
import com.octrobi.lavalarm.core.ui.theme.LavalarmTheme

@Composable
fun NextAlarmCloud(
    alarmCountdownState: State<AlarmCountdownState>,
    currentCoreDestination: Destination,
    previousCoreDestination: Destination,
    timeChangeReceiver: BroadcastReceiver,
    modifier: Modifier = Modifier
) {
    // State
    val context = LocalContext.current
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
            } catch (_: Exception) {
                // Receiver was never registered in the first place. Nothing to do here. Just don't crash.
            }
        }
    }

    val notificationGatedIconAndText: @Composable () -> Unit = {
        SimpleNotificationGate(
            appNotificationChannel = AppNotificationChannel.Alarm,
            gatedComposable = {
                AlarmIconAndText(
                    alarmCountdownState = alarmCountdownState,
                    visibleState = visibleState
                )
            }
        )
    }

    // Cloud with gated Icon and Text
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
    alarmCountdownState: State<AlarmCountdownState>,
    visibleState: MutableTransitionState<Boolean>
) {
    // Alarm Icon and Countdown Text
    // This Animation is to match the NavHost's
    (alarmCountdownState.value as? AlarmCountdownState.Success)?.let { state ->
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
                    imageVector = state.icon,
                    contentDescription = null,
                    tint = DarkGrey,
                    modifier = Modifier.padding(end = 4.dp, bottom = 2.dp)
                )

                // Countdown Text
                Text(
                    text = state.countdownText,
                    color = DarkGrey,
                    fontSize = getCountdownTextFontSize(state.countdownText),
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = getCountdownTextLineHeight(state.countdownText)
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
    val countdownText = stringResource(id = R.string.no_active_alarms)
    val alarmCountdownState: State<AlarmCountdownState> = remember {
        mutableStateOf(
            AlarmCountdownState.Success(
                icon = Icons.Default.AlarmOff,
                countdownText = countdownText
            )
        )
    }

    LavalarmTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            NextAlarmCloud(
                alarmCountdownState = alarmCountdownState,
                currentCoreDestination = Destination.AlarmListScreen,
                previousCoreDestination = Destination.AlarmListScreen,
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
    val countdownText = alarm.toCountdownString(LocalContext.current)
    val alarmCountdownState: State<AlarmCountdownState> = remember {
        mutableStateOf(
            AlarmCountdownState.Success(
                icon = Icons.Default.Alarm,
                countdownText = countdownText
            )
        )
    }

    LavalarmTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            NextAlarmCloud(
                alarmCountdownState = alarmCountdownState,
                currentCoreDestination = Destination.AlarmListScreen,
                previousCoreDestination = Destination.AlarmListScreen,
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
    val countdownText = alarm.toCountdownString(LocalContext.current)
    val alarmCountdownState: State<AlarmCountdownState> = remember {
        mutableStateOf(
            AlarmCountdownState.Success(
                icon = Icons.Default.Alarm,
                countdownText = countdownText
            )
        )
    }

    LavalarmTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            NextAlarmCloud(
                alarmCountdownState = alarmCountdownState,
                currentCoreDestination = Destination.AlarmListScreen,
                previousCoreDestination = Destination.AlarmListScreen,
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
    val countdownText = consistentFutureAlarm.toCountdownString(LocalContext.current)
    val alarmCountdownState: State<AlarmCountdownState> = remember {
        mutableStateOf(
            AlarmCountdownState.Success(
                icon = Icons.Default.Alarm,
                countdownText = countdownText
            )
        )
    }

    LavalarmTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            NextAlarmCloud(
                alarmCountdownState = alarmCountdownState,
                currentCoreDestination = Destination.AlarmListScreen,
                previousCoreDestination = Destination.AlarmListScreen,
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
    val countdownText = alarm.toCountdownString(LocalContext.current)
    val alarmCountdownState: State<AlarmCountdownState> = remember {
        mutableStateOf(
            AlarmCountdownState.Success(
                icon = Icons.Default.Alarm,
                countdownText = countdownText
            )
        )
    }

    LavalarmTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            NextAlarmCloud(
                alarmCountdownState = alarmCountdownState,
                currentCoreDestination = Destination.AlarmListScreen,
                previousCoreDestination = Destination.AlarmListScreen,
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
    val countdownText = snoozedAlarm.toCountdownString(LocalContext.current)
    val alarmCountdownState: State<AlarmCountdownState> = remember {
        mutableStateOf(
            AlarmCountdownState.Success(
                icon = Icons.Default.Snooze,
                countdownText = countdownText
            )
        )
    }

    LavalarmTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            NextAlarmCloud(
                alarmCountdownState = alarmCountdownState,
                currentCoreDestination = Destination.AlarmListScreen,
                previousCoreDestination = Destination.AlarmListScreen,
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
    val countdownText = consistentFutureAlarm.toCountdownString(LocalContext.current)
    val alarmCountdownState: State<AlarmCountdownState> = remember {
        mutableStateOf(
            AlarmCountdownState.Success(
                icon = Icons.Default.Alarm,
                countdownText = countdownText
            )
        )
    }

    LavalarmTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            NextAlarmCloud(
                alarmCountdownState = alarmCountdownState,
                currentCoreDestination = Destination.SettingsScreen,
                previousCoreDestination = Destination.AlarmListScreen,
                timeChangeReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {}
                }
            )
        }
    }
}
