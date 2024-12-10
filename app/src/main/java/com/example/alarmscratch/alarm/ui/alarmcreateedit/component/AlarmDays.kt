package com.example.alarmscratch.alarm.ui.alarmcreateedit.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.data.model.Alarm
import com.example.alarmscratch.alarm.data.model.WeeklyRepeater
import com.example.alarmscratch.alarm.data.preview.calendarAlarm
import com.example.alarmscratch.alarm.data.preview.repeatingAlarm
import com.example.alarmscratch.alarm.data.preview.todayAlarm
import com.example.alarmscratch.alarm.data.preview.tomorrowAlarm
import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import com.example.alarmscratch.core.extension.isRepeating
import com.example.alarmscratch.core.extension.toAlarmDateString
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import java.time.LocalDateTime

@Composable
fun AlarmDays(
    alarm: Alarm,
    modifier: Modifier = Modifier
) {
    if (alarm.isRepeating()) {
        RepeatingAlarmDays(repeatingDays = alarm.weeklyRepeater, modifier = modifier)
    } else {
        NonRepeatingAlarmDays(dateTime = alarm.dateTime, modifier = modifier)
    }
}

@Composable
private fun RepeatingAlarmDays(
    repeatingDays: WeeklyRepeater,
    modifier: Modifier = Modifier
) {
    Text(
        text = "${stringResource(id = R.string.repeating_alarm_date_label)} ${repeatingDays.toAlarmCreationDateString()}",
        modifier = modifier
    )
}

@Composable
private fun NonRepeatingAlarmDays(
    dateTime: LocalDateTime,
    modifier: Modifier = Modifier
) {
    Text(text = dateTime.toAlarmDateString(context = LocalContext.current), modifier = modifier)
}

/*
 * Previews
 */

@Preview(
    showBackground = true,
    backgroundColor = 0xFF373736
)
@Composable
private fun AlarmDaysRepeatingPreview() {
    AlarmScratchTheme {
        AlarmDays(
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
private fun AlarmDaysTodayPreview() {
    AlarmScratchTheme {
        AlarmDays(
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
private fun AlarmDaysTomorrowPreview() {
    AlarmScratchTheme {
        AlarmDays(
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
private fun AlarmDaysBeyondTomorrowPreview() {
    AlarmScratchTheme {
        AlarmDays(
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
private fun AlarmDaysBeforeTodayPreview() {
    AlarmScratchTheme {
        AlarmDays(
            alarm = todayAlarm.copy(
                dateTime = LocalDateTimeUtil.nowTruncated().minusDays(1)
            ),
            modifier = Modifier.padding(20.dp)
        )
    }
}
