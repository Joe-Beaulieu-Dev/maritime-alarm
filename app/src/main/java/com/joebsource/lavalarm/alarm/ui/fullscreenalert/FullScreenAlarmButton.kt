package com.joebsource.lavalarm.alarm.ui.fullscreenalert

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.ui.graphics.vector.ImageVector
import com.joebsource.lavalarm.R

enum class FullScreenAlarmButton(
    @StringRes val fullScreenStringRes: Int,
    @StringRes val confirmationStringRes: Int,
    val confirmationIcon: ImageVector
) {
    SNOOZE(
        fullScreenStringRes = R.string.hold_to_snooze,
        confirmationStringRes = R.string.post_alarm_confirmation_snooze,
        confirmationIcon = Icons.Default.Snooze
    ),
    DISMISS(
        fullScreenStringRes = R.string.hold_to_dismiss,
        confirmationStringRes = R.string.post_alarm_confirmation_dismiss,
        confirmationIcon = Icons.Default.AlarmOff
    ),
    BOTH(
        fullScreenStringRes = R.string.hold_to_dismiss,
        confirmationStringRes = R.string.post_alarm_confirmation_dismiss,
        confirmationIcon = Icons.Default.AlarmOff
    )
}
