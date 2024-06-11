package com.example.alarmscratch.ui.alarmlist.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alarmscratch.R
import com.example.alarmscratch.data.model.Alarm
import com.example.alarmscratch.data.model.WeeklyRepeater
import com.example.alarmscratch.extension.isRepeating
import com.example.alarmscratch.extension.toOrdinal
import com.example.alarmscratch.ui.alarmlist.preview.alarmSampleData
import com.example.alarmscratch.ui.alarmlist.preview.tueWedThu
import com.example.alarmscratch.ui.theme.AlarmScratchTheme
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale

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
fun RepeatingDateBox(
    repeatingDays: WeeklyRepeater,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    AnnotatedDateBox(
        dateText = repeatingDays.toAnnotatedDateString(enabled = enabled),
        enabled = enabled,
        modifier = modifier
    )
}

// TODO do exception handling for java code
//  already looked, might give this a pass. It's just for currentDateTime val
@Composable
fun NonRepeatingDateBox(
    dateTime: LocalDateTime,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val currentDateTime = LocalDateTime.now().withNano(0)
    val dateText = if (dateTime.isAfter(currentDateTime)) {
        val alarmDate = dateTime.toLocalDate()
        val currentDate = currentDateTime.toLocalDate()

        // alarm is for today
        if (alarmDate.isEqual(currentDate)) {
            stringResource(id = R.string.date_today)
        } else if (alarmDate.dayOfYear - currentDate.dayOfYear == 1) { // alarm is for tomorrow
            stringResource(id = R.string.date_tomorrow)
        } else { // alarm is for a day beyond tomorrow
            formatDate(alarmDate)
        }
    } else {
        stringResource(id = R.string.error)
    }

    DateBox(
        dateText = dateText,
        enabled = enabled,
        modifier = modifier
    )
}

@Composable
fun DateBox(
    dateText: String,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Text(
        text = dateText,
        fontSize = 12.sp,
        fontWeight = if (enabled) FontWeight.Medium else null,
        modifier = modifier
    )
}

@Composable
fun AnnotatedDateBox(
    dateText: AnnotatedString,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Text(
        text = dateText,
        fontSize = 12.sp,
        fontWeight = if (enabled) FontWeight.Medium else null,
        modifier = modifier
    )
}

// TODO do something different with Locale
private fun formatDate(date: LocalDate): String =
    "${date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)}, " +
            "${date.month.getDisplayName(TextStyle.SHORT, Locale.US)} " +
            "${date.dayOfMonth.toOrdinal()} " +
            "${date.year}"

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
            alarm = alarmSampleData[0],
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF373736
)
@Composable
private fun AlarmDateNonRepeatingPreview() {
    AlarmScratchTheme {
        AlarmDate(
            alarm = alarmSampleData[1],
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF373736
)
@Composable
fun RepeatingDateBoxPreview() {
    AlarmScratchTheme {
        RepeatingDateBox(
            repeatingDays = WeeklyRepeater(encodedRepeatingDays = tueWedThu),
            enabled = true,
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF373736
)
@Composable
private fun NonRepeatingDateBoxTodayPreview() {
    AlarmScratchTheme {
        NonRepeatingDateBox(
            dateTime = alarmSampleData[1].dateTime,
            enabled = true,
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF373736
)
@Composable
private fun NonRepeatingDateBoxTomorrowPreview() {
    AlarmScratchTheme {
        NonRepeatingDateBox(
            dateTime = alarmSampleData[2].dateTime,
            enabled = true,
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF373736
)
@Composable
private fun NonRepeatingDateBoxBeyondTomorrowPreview() {
    AlarmScratchTheme {
        NonRepeatingDateBox(
            dateTime = alarmSampleData[3].dateTime,
            enabled = true,
            modifier = Modifier.padding(20.dp)
        )
    }
}


@Preview(
    showBackground = true,
    backgroundColor = 0xFF373736
)
@Composable
private fun DateBoxEnabledPreview() {
    AlarmScratchTheme {
        DateBox(
            dateText = "Enabled Bold Date",
            enabled = true,
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF373736
)
@Composable
private fun DateBoxDisabledPreview() {
    AlarmScratchTheme {
        DateBox(
            dateText = "Disabled Non-Bold Date",
            enabled = false,
            modifier = Modifier.padding(20.dp)
        )
    }
}
