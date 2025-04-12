package com.joebsource.lavalarm.alarm.ui.alarmlist.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.joebsource.lavalarm.R
import com.joebsource.lavalarm.alarm.data.model.Alarm
import com.joebsource.lavalarm.alarm.data.model.WeeklyRepeater
import com.joebsource.lavalarm.alarm.data.preview.calendarAlarm
import com.joebsource.lavalarm.alarm.data.preview.repeatingAlarm
import com.joebsource.lavalarm.alarm.data.preview.todayAlarm
import com.joebsource.lavalarm.alarm.data.preview.tomorrowAlarm
import com.joebsource.lavalarm.core.extension.isRepeating
import com.joebsource.lavalarm.core.extension.toAlarmDateString
import com.joebsource.lavalarm.core.ui.theme.LavalarmTheme
import java.time.LocalDateTime

@Composable
fun AlarmDate(
    alarm: Alarm,
    modifier: Modifier = Modifier
) {
    if (alarm.isRepeating()) {
        RepeatingAlarmDate(
            repeatingDays = alarm.weeklyRepeater,
            enabled = alarm.enabled,
            modifier = modifier
        )
    } else {
        NonRepeatingAlarmDate(
            dateTime = alarm.dateTime,
            enabled = alarm.enabled,
            modifier = modifier
        )
    }
}

@Composable
private fun RepeatingAlarmDate(
    repeatingDays: WeeklyRepeater,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Text(
        text = repeatingDays.toAlarmCardDateAnnotatedString(enabled = enabled),
        fontSize = 12.sp,
        fontWeight = if (enabled) FontWeight.Medium else null,
        modifier = modifier
    )
}

@Composable
private fun NonRepeatingAlarmDate(
    dateTime: LocalDateTime,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val dateText = if (enabled) {
        dateTime.toAlarmDateString(context = LocalContext.current)
    } else {
        stringResource(id = R.string.not_scheduled)
    }

    Text(
        text = dateText,
        fontSize = 12.sp,
        fontWeight = if (enabled) FontWeight.Medium else null,
        modifier = modifier
    )
}

/*
 * Previews
 */

@Preview(
    showBackground = true,
    backgroundColor = 0xFF373736
)
@Composable
private fun AlarmDateRepeatingEnabledPreview() {
    LavalarmTheme {
        AlarmDate(
            alarm = repeatingAlarm,
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF373736
)
@Composable
private fun AlarmDateRepeatingDisabledPreview() {
    LavalarmTheme {
        AlarmDate(
            alarm = repeatingAlarm.copy(enabled = false),
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF373736
)
@Composable
private fun AlarmDateTodayPreview() {
    LavalarmTheme {
        AlarmDate(
            alarm = todayAlarm,
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF373736
)
@Composable
private fun AlarmDateTomorrowPreview() {
    LavalarmTheme {
        AlarmDate(
            alarm = tomorrowAlarm.copy(enabled = true),
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF373736
)
@Composable
private fun AlarmDateBeyondTomorrowPreview() {
    LavalarmTheme {
        AlarmDate(
            alarm = calendarAlarm,
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF373736
)
@Composable
private fun AlarmDateNotScheduledPreview() {
    LavalarmTheme {
        AlarmDate(
            alarm = tomorrowAlarm,
            modifier = Modifier.padding(20.dp)
        )
    }
}
