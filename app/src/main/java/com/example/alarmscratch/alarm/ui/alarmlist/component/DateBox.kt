package com.example.alarmscratch.alarm.ui.alarmlist.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alarmscratch.alarm.data.model.Alarm
import com.example.alarmscratch.alarm.data.model.WeeklyRepeater
import com.example.alarmscratch.alarm.data.preview.calendarAlarm
import com.example.alarmscratch.alarm.data.preview.repeatingAlarm
import com.example.alarmscratch.alarm.data.preview.todayAlarm
import com.example.alarmscratch.alarm.data.preview.tomorrowAlarm
import com.example.alarmscratch.core.extension.isRepeating
import com.example.alarmscratch.core.extension.toAlarmDateString
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import java.time.LocalDateTime

@Composable
fun AlarmDate(
    alarm: Alarm,
    modifier: Modifier = Modifier
) {
    if (alarm.isRepeating()) {
        RepeatingDateBox(
            repeatingDays = alarm.weeklyRepeater,
            enabled = alarm.enabled,
            modifier = modifier
        )
    } else {
        NonRepeatingDateBox(
            dateTime = alarm.dateTime,
            enabled = alarm.enabled,
            modifier = modifier
        )
    }
}

@Composable
private fun RepeatingDateBox(
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
private fun NonRepeatingDateBox(
    dateTime: LocalDateTime,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Text(
        text = dateTime.toAlarmDateString(context = LocalContext.current),
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
private fun AlarmDateRepeatingPreview() {
    AlarmScratchTheme {
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
    AlarmScratchTheme {
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
    AlarmScratchTheme {
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
private fun AlarmDateTomorrowDisabledPreview() {
    AlarmScratchTheme {
        AlarmDate(
            alarm = tomorrowAlarm,
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
    AlarmScratchTheme {
        AlarmDate(
            alarm = calendarAlarm,
            modifier = Modifier.padding(20.dp)
        )
    }
}
