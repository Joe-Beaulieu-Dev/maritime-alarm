package com.example.alarmscratch.alarm.ui.fullscreenalert

import android.content.Context
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.alarmscratch.core.ui.shared.LongPressButton
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.BoatHull
import com.example.alarmscratch.core.ui.theme.DarkGrey
import com.example.alarmscratch.core.ui.theme.Grey
import com.example.alarmscratch.core.ui.theme.MediumGrey
import com.example.alarmscratch.core.ui.theme.SkyBlue
import com.example.alarmscratch.core.ui.theme.TransparentBlack
import com.example.alarmscratch.core.ui.theme.TransparentWetSand
import com.example.alarmscratch.core.util.StatusBarUtil
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

// ExperimentalMaterial3Api OptIn for LocalRippleConfiguration and RippleConfiguration
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenAlarmScreenContent(
    alarmName: String,
    alarmExecutionDateTime: LocalDateTime,
    is24Hour: Boolean,
    snoozeAlarm: (Context) -> Unit,
    dismissAlarm: (Context) -> Unit
) {
    // Hold text state
    val context = LocalContext.current
    var holdText by remember { mutableStateOf("") }
    var showHoldIndicator by remember { mutableStateOf(false) }
    val holdToSnoozeText = stringResource(id = R.string.hold_to_snooze)
    val holdToDismissText = stringResource(id = R.string.hold_to_dismiss)

    // Progress state
    val longPressThreshold = 1500
    var start by remember { mutableStateOf(false) }
    var currentProgress by remember { mutableFloatStateOf(0f) }
    val currentPercentage by animateFloatAsState(
        targetValue = currentProgress,
        animationSpec = tween(durationMillis = longPressThreshold, easing = LinearEasing),
        label = "progress",
        finishedListener = { endProgress ->
            if (endProgress == 1f) {
                start = false
            } else {
                showHoldIndicator = false
            }
        }
    )

    // Progress functions
    val onPressStart: () -> Unit = {
        // Start progress animation and show hold text
        start = true
        showHoldIndicator = true
    }
    val onShortPress: () -> Unit = {
        start = false
        // Reset progress animation
        currentProgress = 0f
    }

    // Start progress animation
    if (start) {
        LaunchedEffect(key1 = Unit) {
            currentProgress = 1f
        }
    }

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
                        color = DarkGrey,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold
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
                                text = alarmExecutionDateTime.getAmPm(context),
                                color = DarkGrey,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.alignByBaseline()
                            )
                        }
                    }
                }
            }

            // Snooze and Dismiss Buttons, and Hold Indicator
            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(0.75f)
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
                        Text(
                            text = holdText,
                            color = MediumGrey,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                        )

                        LinearProgressIndicator(
                            progress = { currentPercentage },
                            color = TransparentBlack,
                            trackColor = Color.Transparent,
                            drawStopIndicator = {},
                            modifier = Modifier
                                .fillMaxWidth(fraction = 0.65f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                }

                // Replace default Ripple
                CompositionLocalProvider(value = LocalRippleConfiguration provides RippleConfiguration(color = Grey)) {
                    // Snooze Button
                    LongPressButton(
                        longPressThreshold = longPressThreshold,
                        onPressStart = {
                            holdText = holdToSnoozeText
                            onPressStart()
                        },
                        onLongPress = { snoozeAlarm(context) },
                        onShortPress = onShortPress,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TransparentWetSand,
                            contentColor = Grey
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
                        longPressThreshold = longPressThreshold,
                        onPressStart = {
                            holdText = holdToDismissText
                            onPressStart()
                        },
                        onLongPress = { dismissAlarm(context) },
                        onShortPress = onShortPress,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TransparentWetSand,
                            contentColor = BoatHull
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
