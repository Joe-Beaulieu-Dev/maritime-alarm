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
import androidx.compose.ui.unit.dp
import com.example.alarmscratch.R
import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.BoatSails
import com.example.alarmscratch.core.ui.theme.LightVolcanicRock
import com.example.alarmscratch.core.ui.theme.VolcanicRock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelectionDialog(
    alarmTime: LocalTime,
    onCancel: () -> Unit,
    onConfirm: (LocalDate) -> Unit
) {
    // State
    val currentDateTime = LocalDateTimeUtil.nowTruncated()
    val datePickerState = rememberDatePickerState(
        yearRange = IntRange(currentDateTime.year, 2100),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val calDate = Instant.ofEpochMilli(utcTimeMillis)
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDate()

                val potentialNewAlarm = LocalDateTime.of(calDate, alarmTime)

                return potentialNewAlarm.isAfter(currentDateTime)
            }
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
                    datePickerState.selectedDateMillis?.let { utcDateMillis ->
                        val date = Instant.ofEpochMilli(utcDateMillis)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate()

                        onConfirm(date)
                    }
                },
                enabled = datePickerState.selectedDateMillis != null,
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
        tonalElevation = 0.dp
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
            alarmTime = LocalDateTimeUtil.nowTruncated().toLocalTime(),
            onCancel = {},
            onConfirm = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun DateSelectorPickerModePreview() {
    AlarmScratchTheme {
        DateSelector(datePickerState = rememberDatePickerState())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun DateSelectorInputModePreview() {
    val datePickerState = rememberDatePickerState().apply { displayMode = DisplayMode.Input }
    AlarmScratchTheme {
        DateSelector(datePickerState = datePickerState)
    }
}
