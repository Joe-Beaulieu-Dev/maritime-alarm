package com.example.alarmscratch.alarm.ui.alarmcreateedit.component

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.data.preview.consistentFutureAlarm
import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import com.example.alarmscratch.core.extension.LocalDateUtil
import com.example.alarmscratch.core.extension.toUtcMillis
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.BoatSails
import com.example.alarmscratch.core.ui.theme.DarkVolcanicRock
import com.example.alarmscratch.core.ui.theme.LightVolcanicRock
import com.example.alarmscratch.core.ui.theme.VolcanicRock
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelectionDialog(
    alarmDateTime: LocalDateTime,
    isDateSelectable: (Long, LocalDateTime, LocalDateTime) -> Boolean,
    onCancel: () -> Unit,
    onConfirm: (LocalDate) -> Unit
) {
    // State
    val currentDateTime = LocalDateTimeUtil.nowTruncated()
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = alarmDateTime.toLocalDate().toUtcMillis(),
        yearRange = IntRange(currentDateTime.year, 2100),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                isDateSelectable(utcTimeMillis, alarmDateTime, currentDateTime)
        }
    )

    DatePickerDialog(
        onDismissRequest = onCancel,
        confirmButton = {
            TextButton(
                onClick = {
                    // selectedDateMillis represents a date with 00:00 time in UTC. The fact that it's in UTC doesn't matter
                    // except when converting it to any sort of DateTime. It doesn't matter what time zone you're in
                    // because it's not pulled from any sort of clock, but rather calculated based on the Day/Month
                    // the User selects on the calendar. It's more of an encode/decode thing than a "select your time
                    // zone for proper local conversion" thing. Therefore, you don't have to care about the fact that
                    // it's UTC when doing anything with the Alarm's Date/Time.
                    //
                    // It's just to say "The start of the day on Date X is Y milliseconds since Epoch in UTC".
                    // Same thing goes for other "millis" dates in DatePicker.
                    datePickerState.selectedDateMillis?.let { onConfirm(LocalDateUtil.fromUtcMillis(it)) }
                },
                enabled = datePickerState.selectedDateMillis?.let {
                    isDateSelectable(it, alarmDateTime, LocalDateTimeUtil.nowTruncated())
                } ?: false,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = BoatSails,
                    disabledContentColor = LightVolcanicRock
                )
            ) {
                Text(text = stringResource(id = R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel, colors = ButtonDefaults.textButtonColors(contentColor = BoatSails)) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        colors = DatePickerDefaults.colors(containerColor = DarkVolcanicRock)
    ) {
        DateSelector(datePickerState = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateSelector(datePickerState: DatePickerState) {
    DatePicker(
        state = datePickerState,
        colors = DatePickerDefaults.colors(
            containerColor = DarkVolcanicRock,
            currentYearContentColor = BoatSails,
            disabledDayContentColor = LightVolcanicRock,
            todayContentColor = BoatSails,
            dividerColor = VolcanicRock,
            dateTextFieldColors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = BoatSails,
                focusedBorderColor = BoatSails
            )
        )
    )
}

/*
 * Previews
 */

@Preview
@Composable
private fun DateSelectionDialogPreview() {
    AlarmScratchTheme {
        DateSelectionDialog(
            alarmDateTime = consistentFutureAlarm.dateTime,
            onCancel = {},
            onConfirm = {},
            isDateSelectable = { _, _, _ -> true }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun DateSelectorPickerModePreview() {
    AlarmScratchTheme {
        DateSelector(
            datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = consistentFutureAlarm.dateTime.toLocalDate().toUtcMillis()
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun DateSelectorInputModePreview() {
    AlarmScratchTheme {
        DateSelector(
            datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = consistentFutureAlarm.dateTime.toLocalDate().toUtcMillis(),
                initialDisplayMode = DisplayMode.Input
            )
        )
    }
}
