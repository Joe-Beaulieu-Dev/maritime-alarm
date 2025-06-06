package com.octrobi.lavalarm.alarm.ui.alarmcreateedit.component

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import com.octrobi.lavalarm.R
import com.octrobi.lavalarm.alarm.data.preview.todayAlarm
import com.octrobi.lavalarm.alarm.data.preview.tomorrowAlarm
import com.octrobi.lavalarm.core.extension.LocalDateTimeUtil
import com.octrobi.lavalarm.core.extension.LocalDateUtil
import com.octrobi.lavalarm.core.extension.toUtcMillis
import com.octrobi.lavalarm.core.ui.theme.AndroidDisabledAlpha
import com.octrobi.lavalarm.core.ui.theme.BoatHull
import com.octrobi.lavalarm.core.ui.theme.BoatSails
import com.octrobi.lavalarm.core.ui.theme.DarkVolcanicRock
import com.octrobi.lavalarm.core.ui.theme.LavalarmTheme
import com.octrobi.lavalarm.core.ui.theme.LightVolcanicRock
import com.octrobi.lavalarm.core.ui.theme.VolcanicRock
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelectionDialog(
    alarmDateTime: LocalDateTime,
    onCancel: () -> Unit,
    onConfirm: (LocalDate) -> Unit
) {
    // State
    val datePickerState = rememberDatePickerStateWrapper(
        alarmDateTime = alarmDateTime,
        currentDateTime = LocalDateTimeUtil.nowTruncated()
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
                    isCalendarDateSelectable(it, alarmDateTime, LocalDateTimeUtil.nowTruncated())
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
        colors = DatePickerDefaults.colors(containerColor = DarkVolcanicRock),
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = true
        )
    ) {
        DateSelector(
            datePickerState = datePickerState,
            alarmDateTime = alarmDateTime,
            modifier = Modifier.verticalScroll(rememberScrollState())
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateSelector(
    datePickerState: DatePickerState,
    alarmDateTime: LocalDateTime,
    modifier: Modifier = Modifier
) {
    val todayUtcMillis = LocalDateTimeUtil.nowTruncated().toLocalDate().toUtcMillis()
    val isTodaySelectable = isCalendarDateSelectable(todayUtcMillis, alarmDateTime, LocalDateTimeUtil.nowTruncated())

    DatePicker(
        state = datePickerState,
        colors = DatePickerDefaults.colors(
            containerColor = DarkVolcanicRock,
            currentYearContentColor = BoatSails,
            disabledDayContentColor = LightVolcanicRock,
            todayContentColor = if (isTodaySelectable) BoatSails else LightVolcanicRock,
            // This alpha value is copied from Android's internal ColorScheme.DisabledAlpha.
            // This internal constant is applied here to mimic the "disabled shading" of
            // disabledSelectedDayContainerColor. Without applying this alpha, today's outline would
            // be bright red when it's not selectable, rather than being the darker disabled color.
            todayDateBorderColor = if (isTodaySelectable) BoatHull else BoatHull.copy(alpha = AndroidDisabledAlpha),
            dividerColor = VolcanicRock,
            dateTextFieldColors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = BoatSails,
                focusedBorderColor = BoatSails
            )
        ),
        modifier = modifier
    )
}

// ExperimentalMaterial3Api OptIn for DatePickerState, rememberDatePickerState, and SelectableDates
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberDatePickerStateWrapper(
    alarmDateTime: LocalDateTime,
    currentDateTime: LocalDateTime,
    initialDisplayMode: DisplayMode = DisplayMode.Picker
): DatePickerState =
    rememberDatePickerState(
        initialSelectedDateMillis = alarmDateTime.toLocalDate().toUtcMillis(),
        yearRange = IntRange(currentDateTime.year, 2100),
        initialDisplayMode = initialDisplayMode,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                isCalendarDateSelectable(utcTimeMillis, alarmDateTime, currentDateTime)
        }
    )

private fun isCalendarDateSelectable(
    calendarDateUtcMillis: Long,
    alarmDateTime: LocalDateTime,
    currentDateTime: LocalDateTime
): Boolean {
    val calendarDate = LocalDateUtil.fromUtcMillis(calendarDateUtcMillis)
    val potentialNewAlarm = LocalDateTime.of(calendarDate, alarmDateTime.toLocalTime())

    return potentialNewAlarm.isAfter(currentDateTime)
}

/*
 * Previews
 */

@Preview
@Composable
private fun DateSelectionDialogTomorrowLatePreview() {
    LavalarmTheme {
        DateSelectionDialog(
            alarmDateTime = tomorrowAlarm.dateTime.withHour(23).withMinute(59),
            onCancel = {},
            onConfirm = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun DateSelectorPickerTodayLatePreview() {
    val alarmDateTime = todayAlarm.dateTime

    LavalarmTheme {
        DateSelector(
            datePickerState = rememberDatePickerStateWrapper(
                alarmDateTime = alarmDateTime,
                currentDateTime = LocalDateTimeUtil.nowTruncated()
            ),
            alarmDateTime = alarmDateTime
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun DateSelectorPickerTodayEarlyPreview() {
    val alarmDateTime = todayAlarm.dateTime.withHour(0).withMinute(5)

    LavalarmTheme {
        DateSelector(
            datePickerState = rememberDatePickerStateWrapper(
                alarmDateTime = alarmDateTime,
                currentDateTime = LocalDateTimeUtil.nowTruncated()
            ),
            alarmDateTime = alarmDateTime
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun DateSelectorPickerTomorrowEarlyPreview() {
    val alarmDateTime = tomorrowAlarm.dateTime.withHour(0).withMinute(5)

    LavalarmTheme {
        DateSelector(
            datePickerState = rememberDatePickerStateWrapper(
                alarmDateTime = alarmDateTime,
                currentDateTime = LocalDateTimeUtil.nowTruncated()
            ),
            alarmDateTime = alarmDateTime
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun DateSelectorPickerYesterdayLatePreview() {
    val alarmDateTime = todayAlarm.dateTime.minusDays(1)

    LavalarmTheme {
        DateSelector(
            datePickerState = rememberDatePickerStateWrapper(
                alarmDateTime = alarmDateTime,
                currentDateTime = LocalDateTimeUtil.nowTruncated()
            ),
            alarmDateTime = alarmDateTime
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun DateSelectorInputModePreview() {
    val alarmDateTime = todayAlarm.dateTime

    LavalarmTheme {
        DateSelector(
            datePickerState = rememberDatePickerStateWrapper(
                alarmDateTime = alarmDateTime,
                currentDateTime = LocalDateTimeUtil.nowTruncated(),
                initialDisplayMode = DisplayMode.Input
            ),
            alarmDateTime = alarmDateTime
        )
    }
}
