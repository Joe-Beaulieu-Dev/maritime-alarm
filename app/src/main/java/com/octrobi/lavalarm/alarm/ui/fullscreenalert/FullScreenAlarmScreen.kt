package com.octrobi.lavalarm.alarm.ui.fullscreenalert

import android.content.Context
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.octrobi.lavalarm.R
import com.octrobi.lavalarm.alarm.data.preview.consistentFutureAlarm
import com.octrobi.lavalarm.alarm.ui.fullscreenalert.component.BeachBackdrop
import com.octrobi.lavalarm.core.extension.get12HourTime
import com.octrobi.lavalarm.core.extension.get24HourTime
import com.octrobi.lavalarm.core.extension.getAmPm
import com.octrobi.lavalarm.core.extension.getDayFull
import com.octrobi.lavalarm.core.ui.shared.LongPressButton
import com.octrobi.lavalarm.core.ui.theme.BoatHull
import com.octrobi.lavalarm.core.ui.theme.DarkGrey
import com.octrobi.lavalarm.core.ui.theme.DrySand
import com.octrobi.lavalarm.core.ui.theme.Grey
import com.octrobi.lavalarm.core.ui.theme.LavalarmTheme
import com.octrobi.lavalarm.core.ui.theme.MediumGrey
import com.octrobi.lavalarm.core.ui.theme.SkyBlue
import com.octrobi.lavalarm.core.ui.theme.TransparentBlack
import com.octrobi.lavalarm.core.ui.theme.TransparentWetSand
import com.octrobi.lavalarm.core.util.StatusBarUtil
import java.time.LocalDateTime

@Composable
fun FullScreenAlarmScreen(fullScreenAlarmViewModel: FullScreenAlarmViewModel) {
    // Configure Status Bar
    StatusBarUtil.setLightStatusBar()

    FullScreenAlarmScreenContent(
        alarmName = fullScreenAlarmViewModel.alarmExecutionData.name,
        alarmExecutionDateTime = fullScreenAlarmViewModel.alarmExecutionData.executionDateTime,
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
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    0.07f to SkyBlue,
                    0.08f to DrySand
                )
            )
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        // Screen background
        BeachBackdrop()

        // Alarm Data and Snooze and Dismiss Buttons with Hold Indicator
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Alarm Data
            AlarmData(
                alarmName = alarmName,
                alarmExecutionDateTime = alarmExecutionDateTime,
                is24Hour = is24Hour,
                modifier = Modifier.weight(0.25f)
            )

            // Snooze and Dismiss Buttons with Hold Indicator
            SnoozeAndDismissButtons(
                snoozeAlarm = snoozeAlarm,
                dismissAlarm = dismissAlarm,
                modifier = Modifier.weight(0.75f)
            )
        }
    }
}

@Composable
fun AlarmData(
    alarmName: String,
    alarmExecutionDateTime: LocalDateTime,
    is24Hour: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // Alarm Name, Day, and Time
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
                color = DarkGrey,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            // Day
            Text(
                text = alarmExecutionDateTime.getDayFull(),
                color = DarkGrey,
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold
            )

            // Time
            Row {
                // Hour and Minute
                Text(
                    text = if (is24Hour) {
                        alarmExecutionDateTime.get24HourTime()
                    } else {
                        alarmExecutionDateTime.get12HourTime()
                    },
                    color = DarkGrey,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.alignByBaseline()
                )

                // AM/PM
                if (!is24Hour) {
                    Text(
                        text = alarmExecutionDateTime.getAmPm(LocalContext.current),
                        color = DarkGrey,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.alignByBaseline()
                    )
                }
            }
        }
    }
}

// ExperimentalMaterial3Api OptIn for LocalRippleConfiguration and RippleConfiguration
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnoozeAndDismissButtons(
    snoozeAlarm: (Context) -> Unit,
    dismissAlarm: (Context) -> Unit,
    modifier: Modifier = Modifier
) {
    // Snooze and Dismiss button state
    val context = LocalContext.current
    var enabledButton by remember { mutableStateOf(FullScreenAlarmButton.BOTH) }
    val isButtonEnabled: (FullScreenAlarmButton) -> Boolean = { button ->
        button == enabledButton || enabledButton == FullScreenAlarmButton.BOTH
    }

    // Hold Indicator text state
    var showHoldIndicator by remember { mutableStateOf(false) }
    var holdIndicatorTextRes by remember { mutableIntStateOf(FullScreenAlarmButton.BOTH.fullScreenStringRes) }

    // Hold Indicator progress state
    val longPressTimeout = 1500
    var targetProgress by remember { mutableFloatStateOf(0f) }
    var animationSpec: TweenSpec<Float> by remember {
        mutableStateOf(tween(durationMillis = longPressTimeout, easing = LinearEasing))
    }
    val currentProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = animationSpec,
        label = "hold_indicator_progress",
        finishedListener = { endProgress ->
            if (endProgress < 1f) {
                if (enabledButton == FullScreenAlarmButton.BOTH) {
                    // Progress reached 0f naturally after a short press. Hide the hold indicator.
                    showHoldIndicator = false
                } else {
                    // Progress was hard reset by hardResetHoldIndicatorProgress(), which is only called inside
                    // of onPressStart(). Therefore, the User is currently holding a button. Start animating up towards 1f.
                    animationSpec = tween(durationMillis = longPressTimeout, easing = LinearEasing)
                    targetProgress = 1f
                }
            }
        }
    )
    val hardResetHoldIndicatorProgress: () -> Unit = {
        // Set initial progress to 0.01f instantly with a 0 durationMillis animationSpec.
        // Progress should always reset on a new button press since the longPressTimeout is always
        // the same, and the Hold Indicator should always reach the end at the same time that onLongPress() is invoked.
        //
        // Resetting the progress like this will cause currentProgress's animator to invoke its finishedListener,
        // which will immediately start animating the currentProgress up to 1f with a durationMillis of longPressTimeout.
        //
        // targetProgress MUST be "reset" here to 0.01f instead of 0f. This is because targetProgress's INITIAL value
        // (and also END value after a short press) is 0f, so if we "reset" it here to 0f, then currentProgress will
        // NEVER animate for ANY button presses. This is because no animation is necessary to go from 0f to 0f, and upward
        // animation towards 1f ONLY occurs as a result of the currentProgress animator's finishedListener being
        // invoked by modifying the targetProgress here in hardResetHoldIndicatorProgress().
        //
        // Setting it here to 0.01f will invoke a NEW animation of currentProgress, which will result in its finishedListener
        // being invoke at the end. If the finishedListener was invoked as a result of hardResetHoldIndicatorProgress() then
        // it will begin animating up towards 1f, as desired.
        //
        // The only reason why any of this logic exists is to ensure that the Hold Indicator is reset at the beginning of every
        // button press, so that it always finishes filling up at the same time that onLongPress() is invoked.
        animationSpec = tween(durationMillis = 0, easing = LinearEasing)
        targetProgress = 0.01f
    }

    // Snooze and Dismiss button functions
    val onPressStart: (FullScreenAlarmButton) -> Unit = { button ->
        // Set button as enabled
        enabledButton = button
        // Configure and show Hold Indicator
        holdIndicatorTextRes = button.fullScreenStringRes
        hardResetHoldIndicatorProgress()
        showHoldIndicator = true
    }

    val onShortPress: () -> Unit = {
        // Reset buttons and animate progress down towards 0f
        enabledButton = FullScreenAlarmButton.BOTH
        targetProgress = 0f
    }

    val onLongPress: (FullScreenAlarmButton) -> Unit = { button ->
        // Perform long press action
        when (button) {
            FullScreenAlarmButton.SNOOZE ->
                snoozeAlarm(context)
            FullScreenAlarmButton.DISMISS ->
                dismissAlarm(context)
            FullScreenAlarmButton.BOTH ->
                Unit
        }
    }

    val onLongPressRelease: () -> Unit = {
        // Reset buttons
        enabledButton = FullScreenAlarmButton.BOTH
    }

    val onPressCancelled: () -> Unit = {
        // Reset buttons and animate progress down towards 0f
        enabledButton = FullScreenAlarmButton.BOTH
        targetProgress = 0f
    }

    // Snooze and Dismiss Buttons with Hold Indicator
    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // Hold Indicator
        if (showHoldIndicator) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .width(IntrinsicSize.Min)
                    .height(IntrinsicSize.Min)
                    .padding(bottom = 14.dp)
            ) {
                // Hold Text
                Text(
                    text = stringResource(id = holdIndicatorTextRes),
                    color = MediumGrey,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )

                // Progress Indicator
                LinearProgressIndicator(
                    progress = { currentProgress },
                    color = TransparentBlack,
                    trackColor = Color.Transparent,
                    drawStopIndicator = {},
                    modifier = Modifier
                        .fillMaxWidth(fraction = 0.65f)
                        .fillMaxHeight()
                )
            }
        }

        // Snooze and Dismiss buttons
        // Replace default Ripple
        CompositionLocalProvider(value = LocalRippleConfiguration provides RippleConfiguration(color = Grey)) {
            // Snooze Button
            LongPressButton(
                longPressTimeout = longPressTimeout.toLong(),
                onPressStart = { onPressStart(FullScreenAlarmButton.SNOOZE) },
                onShortPress = onShortPress,
                onLongPress = { onLongPress(FullScreenAlarmButton.SNOOZE) },
                onLongPressRelease = onLongPressRelease,
                onPressCancelled = onPressCancelled,
                enabled = isButtonEnabled(FullScreenAlarmButton.SNOOZE),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TransparentWetSand,
                    contentColor = Grey,
                    disabledContainerColor = TransparentWetSand,
                    disabledContentColor = Grey
                ),
                contentPadding = PaddingValues(start = 28.dp, top = 10.dp, end = 28.dp, bottom = 10.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.snooze_alarm),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(32.dp))

            // Dismiss Button
            LongPressButton(
                longPressTimeout = longPressTimeout.toLong(),
                onPressStart = { onPressStart(FullScreenAlarmButton.DISMISS) },
                onShortPress = onShortPress,
                onLongPress = { onLongPress(FullScreenAlarmButton.DISMISS) },
                onLongPressRelease = onLongPressRelease,
                onPressCancelled = onPressCancelled,
                enabled = isButtonEnabled(FullScreenAlarmButton.DISMISS),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TransparentWetSand,
                    contentColor = BoatHull,
                    disabledContainerColor = TransparentWetSand,
                    disabledContentColor = BoatHull
                ),
                contentPadding = PaddingValues(start = 28.dp, top = 10.dp, end = 28.dp, bottom = 10.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.dismiss_alarm),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

/*
 * Previews
 */

@Preview
@Composable
private fun FullScreenAlarmScreen12HourPreview() {
    LavalarmTheme {
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
    LavalarmTheme {
        FullScreenAlarmScreenContent(
            alarmName = consistentFutureAlarm.name,
            alarmExecutionDateTime = consistentFutureAlarm.dateTime,
            is24Hour = true,
            snoozeAlarm = {},
            dismissAlarm = {}
        )
    }
}

@Preview
@Composable
private fun FullScreenAlarmScreenLongNamePreview() {
    LavalarmTheme {
        FullScreenAlarmScreenContent(
            alarmName = "1234567890123456789012345678901234567890",
            alarmExecutionDateTime = consistentFutureAlarm.dateTime,
            is24Hour = false,
            snoozeAlarm = {},
            dismissAlarm = {}
        )
    }
}
