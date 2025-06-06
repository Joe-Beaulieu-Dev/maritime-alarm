package com.octrobi.lavalarm.alarm.ui.alarmcreateedit.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.octrobi.lavalarm.R
import com.octrobi.lavalarm.alarm.data.model.Alarm
import com.octrobi.lavalarm.alarm.data.model.WeeklyRepeater
import com.octrobi.lavalarm.alarm.data.preview.calendarAlarm
import com.octrobi.lavalarm.alarm.data.preview.everyDay
import com.octrobi.lavalarm.alarm.data.preview.repeatingAlarm
import com.octrobi.lavalarm.alarm.data.preview.todayAlarm
import com.octrobi.lavalarm.alarm.data.preview.tomorrowAlarm
import com.octrobi.lavalarm.core.extension.LocalDateTimeUtil
import com.octrobi.lavalarm.core.extension.isRepeating
import com.octrobi.lavalarm.core.extension.toAlarmDateString
import com.octrobi.lavalarm.core.ui.theme.LavalarmTheme
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
    val dateText = if (repeatingDays.isRepeatingEveryDay()) {
        stringResource(id = R.string.repeating_alarm_every_day)
    } else {
        "${stringResource(id = R.string.repeating_alarm_date_label)} ${repeatingDays.toAlarmCreationDateString()}"
    }

    Text(text = dateText, modifier = modifier)
}

@Composable
private fun NonRepeatingAlarmDays(
    dateTime: LocalDateTime,
    modifier: Modifier = Modifier
) {
    Text(
        text = dateTime.toAlarmDateString(context = LocalContext.current),
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
private fun AlarmDaysRepeatingPreview() {
    LavalarmTheme {
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
private fun AlarmDaysRepeatingEveryDayPreview() {
    LavalarmTheme {
        AlarmDays(
            alarm = repeatingAlarm.copy(weeklyRepeater = WeeklyRepeater(everyDay)),
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
    LavalarmTheme {
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
    LavalarmTheme {
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
    LavalarmTheme {
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
    LavalarmTheme {
        AlarmDays(
            alarm = todayAlarm.copy(dateTime = LocalDateTimeUtil.nowTruncated().minusDays(1)),
            modifier = Modifier.padding(20.dp)
        )
    }
}
