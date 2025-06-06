package com.octrobi.lavalarm.alarm.ui.fullscreenalert

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.octrobi.lavalarm.R
import com.octrobi.lavalarm.core.ui.theme.DarkVolcanicRock
import com.octrobi.lavalarm.core.ui.theme.LavalarmTheme
import com.octrobi.lavalarm.core.util.StatusBarUtil
import kotlinx.coroutines.delay

@Composable
fun PostAlarmConfirmationScreen(
    fullScreenAlarmButton: FullScreenAlarmButton,
    snoozeDuration: Int,
    modifier: Modifier = Modifier,
    postAlarmConfirmationViewModel: PostAlarmConfirmationViewModel = viewModel(factory = PostAlarmConfirmationViewModel.Factory)
) {
    // Configure Status Bar
    StatusBarUtil.setDarkStatusBar()

    // Countdown timer to finish the Alarm execution flow
    val context = LocalContext.current
    BasicCountdown(timeSeconds = 2) {
        postAlarmConfirmationViewModel.finishFullScreenAlarmFlow(context)
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .background(color = DarkVolcanicRock)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Confirmation Text and optional Snooze Time
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(0.60f)
            ) {
                // Confirmation Text
                Text(
                    text = stringResource(id = fullScreenAlarmButton.confirmationStringRes),
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 44.sp
                )

                // Snooze Duration
                if (fullScreenAlarmButton == FullScreenAlarmButton.SNOOZE) {
                    Text(
                        text = "${snoozeDuration}${stringResource(id = R.string.post_alarm_confirmation_min)}",
                        fontSize = 54.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Confirmation Icon
            Box(modifier = Modifier.weight(0.40f)) {
                Icon(
                    imageVector = fullScreenAlarmButton.confirmationIcon,
                    contentDescription = null,
                    modifier = Modifier.size(90.dp)
                )
            }
        }
    }
}

@Composable
fun BasicCountdown(
    timeSeconds: Int,
    onCountdownFinished: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var timeLeft by rememberSaveable { mutableIntStateOf(timeSeconds) }

    LaunchedEffect(key1 = lifecycleOwner.lifecycle, key2 = timeLeft) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            if (timeLeft > 0) {
                delay(1000L)
                timeLeft--
            } else {
                onCountdownFinished()
            }
        }
    }
}

/*
 * Preview
 */

@Preview
@Composable
private fun PostAlarmConfirmationScreenSnoozePreview() {
    LavalarmTheme {
        PostAlarmConfirmationScreen(
            fullScreenAlarmButton = FullScreenAlarmButton.SNOOZE,
            snoozeDuration = 10
        )
    }
}

@Preview
@Composable
private fun PostAlarmConfirmationScreenDismissPreview() {
    LavalarmTheme {
        PostAlarmConfirmationScreen(
            fullScreenAlarmButton = FullScreenAlarmButton.DISMISS,
            snoozeDuration = 10
        )
    }
}
