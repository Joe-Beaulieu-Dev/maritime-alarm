package com.example.alarmscratch.extension

import android.content.Context
import com.example.alarmscratch.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale

// TODO do exception handling for java code.
//  Already looked, might give this a pass. It's just for currentDateTime val.
fun LocalDateTime.toAlarmDateString(context: Context) : String {
    val currentDateTime = LocalDateTime.now().withNano(0)
    return if (this.isAfter(currentDateTime)) {
        val alarmDate = this.toLocalDate()
        val currentDate = currentDateTime.toLocalDate()

        // Alarm is for today
        if (alarmDate.isEqual(currentDate)) {
            context.getString(R.string.date_today)
        } else if (alarmDate.dayOfYear - currentDate.dayOfYear == 1) { // Alarm is for tomorrow
            context.getString(R.string.date_tomorrow)
        } else { // Alarm is for a day beyond tomorrow
            formatDate(alarmDate)
        }
    } else {
        context.getString(R.string.error)
    }
}

// TODO do something different with Locale
private fun formatDate(date: LocalDate): String =
    "${date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)}, " +
            "${date.month.getDisplayName(TextStyle.SHORT, Locale.US)} " +
            "${date.dayOfMonth.toOrdinal()} " +
            "${date.year}"
