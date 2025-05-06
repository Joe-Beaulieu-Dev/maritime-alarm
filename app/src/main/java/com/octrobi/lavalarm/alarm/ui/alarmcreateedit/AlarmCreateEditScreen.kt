package com.octrobi.lavalarm.alarm.ui.alarmcreateedit

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.octrobi.lavalarm.R
import com.octrobi.lavalarm.alarm.data.model.Alarm
import com.octrobi.lavalarm.alarm.data.model.WeeklyRepeater
import com.octrobi.lavalarm.alarm.data.preview.calendarAlarm
import com.octrobi.lavalarm.alarm.data.preview.consistentFutureAlarm
import com.octrobi.lavalarm.alarm.data.preview.repeatingAlarm
import com.octrobi.lavalarm.alarm.data.preview.sampleRingtoneData
import com.octrobi.lavalarm.alarm.ui.alarmcreateedit.component.AlarmDays
import com.octrobi.lavalarm.alarm.ui.alarmcreateedit.component.DateSelectionDialog
import com.octrobi.lavalarm.alarm.ui.alarmcreateedit.component.TimeSelectionDialog
import com.octrobi.lavalarm.alarm.validation.AlarmValidator
import com.octrobi.lavalarm.alarm.validation.ValidationError
import com.octrobi.lavalarm.alarm.validation.ValidationResult
import com.octrobi.lavalarm.core.extension.get12HourTime
import com.octrobi.lavalarm.core.extension.get24HourTime
import com.octrobi.lavalarm.core.extension.getAmPm
import com.octrobi.lavalarm.core.ui.shared.CustomTopAppBar
import com.octrobi.lavalarm.core.ui.shared.RowSelectionItem
import com.octrobi.lavalarm.core.ui.shared.UnsavedChangesDialog
import com.octrobi.lavalarm.core.ui.theme.BoatHull
import com.octrobi.lavalarm.core.ui.theme.BoatSails
import com.octrobi.lavalarm.core.ui.theme.DarkVolcanicRock
import com.octrobi.lavalarm.core.ui.theme.DarkerBoatSails
import com.octrobi.lavalarm.core.ui.theme.LavalarmTheme
import com.octrobi.lavalarm.core.ui.theme.LightVolcanicRock
import com.octrobi.lavalarm.core.ui.theme.MediumVolcanicRock
import com.octrobi.lavalarm.core.ui.theme.VolcanicRock
import com.octrobi.lavalarm.core.ui.theme.WayDarkerBoatSails
import com.octrobi.lavalarm.core.ui.transform.CharLimitVisualTransformation
import com.octrobi.lavalarm.core.util.StatusBarUtil
import com.octrobi.lavalarm.settings.data.model.TimeDisplay
import com.octrobi.lavalarm.settings.ui.alarmdefaults.component.SnoozeDurationDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun AlarmCreateEditScreen(
    navHostController: NavHostController,
    navigateToRingtonePickerScreen: (String) -> Unit,
    @StringRes titleRes: Int,
    alarm: Alarm,
    alarmRingtoneName: String,
    timeDisplay: TimeDisplay,
    saveAndScheduleAlarm: (Context, NavHostController) -> Unit,
    updateName: (String) -> Unit,
    updateDate: (LocalDate) -> Unit,
    updateTime: (Int, Int) -> Unit,
    addDay: (WeeklyRepeater.Day) -> Unit,
    removeDay: (WeeklyRepeater.Day) -> Unit,
    toggleVibration: () -> Unit,
    updateSnoozeDuration: (Int) -> Unit,
    nameCharacterLimit: Int,
    isNameLengthValid: ValidationResult<AlarmValidator.NameError>,
    isNameContentValid: ValidationResult<AlarmValidator.NameError>,
    snackbarFlow: Flow<ValidationResult.Error<ValidationError>>,
    tryNavigateUp: () -> Unit,
    tryNavigateBack: () -> Unit,
    showUnsavedChangesDialog: Boolean,
    unsavedChangesLeave: () -> Unit,
    unsavedChangesStay: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Configure Status Bar
    StatusBarUtil.setDarkStatusBar()

    // State
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val upNavigationFocusRequester = remember { FocusRequester() }
    val saveFocusRequester = remember { FocusRequester() }

    // Show Snackbar
    LaunchedEffect(key1 = context, key2 = lifecycleOwner.lifecycle, key3 = snackbarFlow) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                snackbarFlow.collect {
                    snackbarHostState.showSnackbar(message = it.error.toSnackbarString(context))
                }
            }
        }
    }

    // Intercept system back navigation
    BackHandler {
        tryNavigateBack()
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                titleRes = titleRes,
                navigationButton = {
                    IconButton(
                        onClick = {
                            upNavigationFocusRequester.requestFocus()
                            tryNavigateUp()
                        },
                        modifier = Modifier
                            .focusRequester(upNavigationFocusRequester)
                            .focusable()
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
                    }
                },
                actionButton = {
                    IconButton(
                        onClick = {
                            saveFocusRequester.requestFocus()
                            saveAndScheduleAlarm(context, navHostController)
                        },
                        modifier = Modifier
                            .focusRequester(saveFocusRequester)
                            .focusable()
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null)
                    }
                },
                modifier = Modifier.background(color = MediumVolcanicRock)
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                Snackbar(
                    snackbarData = snackbarData,
                    containerColor = VolcanicRock,
                    contentColor = BoatSails
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    0.07f to MediumVolcanicRock,
                    0.08f to DarkVolcanicRock
                )
            )
            .windowInsetsPadding(WindowInsets.systemBars)
            .clickable(interactionSource = null, indication = null) { focusManager.clearFocus() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Alarm Name, Date/Time Settings, Alert Settings, Snooze Settings
            Column(
                modifier = Modifier
                    .padding(start = 20.dp, top = 20.dp, end = 20.dp)
                    .fillMaxWidth()
            ) {
                // Alarm Name
                AlarmName(
                    alarm = alarm,
                    updateName = updateName,
                    nameCharacterLimit = nameCharacterLimit,
                    isNameLengthValid = isNameLengthValid,
                    isNameContentValid = isNameContentValid
                )

                // Date/Time Settings
                DateTimeSettings(
                    alarm = alarm,
                    timeDisplay = timeDisplay,
                    updateDate = updateDate,
                    updateTime = updateTime,
                    addDay = addDay,
                    removeDay = removeDay
                )
                HorizontalDivider(
                    color = VolcanicRock,
                    modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
                )
            }

            // Alert Settings
            AlertSettings(
                navigateToRingtonePickerScreen = { navigateToRingtonePickerScreen(alarm.ringtoneUri) },
                selectedRingtone = alarmRingtoneName,
                isVibrationEnabled = alarm.isVibrationEnabled,
                toggleVibration = toggleVibration,
                modifier = Modifier.fillMaxWidth()
            )

            // Snooze Settings
            SnoozeSettings(
                snoozeDuration = alarm.snoozeDuration,
                updateSnoozeDuration = updateSnoozeDuration,
                modifier = Modifier.padding(top = 20.dp)
            )
        }
    }

    // Unsaved Changes Dialog
    if (showUnsavedChangesDialog) {
        UnsavedChangesDialog(
            onLeave = unsavedChangesLeave,
            onStay = unsavedChangesStay
        )
    }
}

@Composable
fun AlarmName(
    alarm: Alarm,
    updateName: (String) -> Unit,
    nameCharacterLimit: Int,
    isNameLengthValid: ValidationResult<AlarmValidator.NameError>,
    isNameContentValid: ValidationResult<AlarmValidator.NameError>,
    modifier: Modifier = Modifier
) {
    // State
    var stylizedAlarmName by remember { mutableStateOf(TextFieldValue(alarm.name)) }
    val isError = isNameLengthValid is ValidationResult.Error || isNameContentValid is ValidationResult.Error

    OutlinedTextField(
        value = stylizedAlarmName,
        onValueChange = {
            stylizedAlarmName = it
            // Since we're passing a TextFieldValue to the OutlinedTextField, onValueChange will be invoked when any
            // property of the TextFieldValue is changed, including the cursor position (TextFieldValue.selection).
            // Prevent unnecessary code execution by only updating the ViewModel when the text of the TextFieldValue changes.
            if (alarm.name != stylizedAlarmName.text) {
                updateName(stylizedAlarmName.text)
            }
        },
        placeholder = { Text(text = stringResource(id = R.string.alarm_name_placeholder), color = LightVolcanicRock) },
        trailingIcon = if (isError) {
            { Icon(imageVector = Icons.Default.Error, contentDescription = null) }
        } else {
            null
        },
        supportingText = {
            // Error Text and Character Counter
            Row {
                // Error Text
                Text(
                    text = if (isNameContentValid is ValidationResult.Error) {
                        isNameContentValid.error.toInlineString(LocalContext.current)
                    } else {
                        "" // Empty String prevents Error text from shifting UI
                    },
                    // Weight modifier not only determines width, but also ensures the Error Text
                    // gets measured after the Character Counter. Without it the Character Counter
                    // would get bumped off the screen if the Error Text grew too large, due to
                    // the Error Text getting measured first.
                    modifier = Modifier.weight(1f)
                )

                // Character Counter
                Text(
                    text = if (isNameLengthValid is ValidationResult.Error) {
                        "${nameCharacterLimit - alarm.name.length}"
                    } else {
                        ""
                    }
                )
            }
        },
        isError = isError,
        visualTransformation = CharLimitVisualTransformation(nameCharacterLimit, BoatHull),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DarkerBoatSails),
        modifier = modifier
    )
}

@Composable
fun DateTimeSettings(
    alarm: Alarm,
    timeDisplay: TimeDisplay,
    updateDate: (LocalDate) -> Unit,
    updateTime: (Int, Int) -> Unit,
    addDay: (WeeklyRepeater.Day) -> Unit,
    removeDay: (WeeklyRepeater.Day) -> Unit
) {
    // State
    var showDateSelectionDialog by rememberSaveable { mutableStateOf(false) }
    val toggleDateSelectionDialog: () -> Unit = { showDateSelectionDialog = !showDateSelectionDialog }
    val focusRequester = remember { FocusRequester() }

    Column {
        // Time
        AlarmTime(
            dateTime = alarm.dateTime,
            timeDisplay = timeDisplay,
            updateTime = updateTime
        )

        // Calendar Button and Alarm execution config
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Calendar Button
            IconButton(
                onClick = {
                    focusRequester.requestFocus()
                    toggleDateSelectionDialog()
                },
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .focusable()
            ) {
                Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null)
            }

            // Alarm execution config
            AlarmDays(alarm = alarm, modifier = Modifier.padding(start = 4.dp))
        }

        // Day of Week
        DayOfWeekPicker(
            weeklyRepeater = alarm.weeklyRepeater,
            addDay = addDay,
            removeDay = removeDay
        )
    }

    // Date Selection Dialog
    if (showDateSelectionDialog) {
        DateSelectionDialog(
            alarmDateTime = alarm.dateTime,
            onCancel = toggleDateSelectionDialog,
            onConfirm = { date ->
                updateDate(date)
                toggleDateSelectionDialog()
            }
        )
    }
}

@Composable
fun AlarmTime(
    dateTime: LocalDateTime,
    timeDisplay: TimeDisplay,
    updateTime: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // State
    var showTimePickerDialog by rememberSaveable { mutableStateOf(false) }
    val toggleTimePickerDialog: () -> Unit = { showTimePickerDialog = !showTimePickerDialog }
    val time = when (timeDisplay) {
        TimeDisplay.TwelveHour ->
            dateTime.get12HourTime()
        TimeDisplay.TwentyFourHour ->
            dateTime.get24HourTime()
    }
    val focusRequester = remember { FocusRequester() }

    // Time and AM/PM
    Row(
        modifier = modifier
            .offset(x = (-10).dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color = DarkVolcanicRock)
            .clickable(
                onClick = {
                    focusRequester.requestFocus()
                    toggleTimePickerDialog()
                }
            )
            .focusRequester(focusRequester)
            .focusable()
    ) {
        // Time
        Text(
            text = time,
            color = DarkerBoatSails,
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 10.dp)
                .alignByBaseline()
        )

        // AM/PM
        if (timeDisplay == TimeDisplay.TwelveHour) {
            Text(
                text = dateTime.getAmPm(LocalContext.current),
                color = DarkerBoatSails,
                fontSize = 38.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.alignByBaseline()
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
    }

    // Time Selection Dialog
    if (showTimePickerDialog) {
        TimeSelectionDialog(
            initialHour = dateTime.hour,
            initialMinute = dateTime.minute,
            timeDisplay = timeDisplay,
            onCancel = toggleTimePickerDialog,
            onConfirm = { hour, minute ->
                updateTime(hour, minute)
                toggleTimePickerDialog()
            }
        )
    }
}

@Composable
fun DayOfWeekPicker(
    weeklyRepeater: WeeklyRepeater,
    addDay: (WeeklyRepeater.Day) -> Unit,
    removeDay: (WeeklyRepeater.Day) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        WeeklyRepeater.Day.entries.forEach { day ->
            DayOfWeekButton(
                dayText = day.oneLetterShorthand,
                selected = weeklyRepeater.isRepeatingOn(day),
                addDay = { addDay(day) },
                removeDay = { removeDay(day) }
            )
        }
    }
}

@Composable
fun DayOfWeekButton(
    dayText: String,
    selected: Boolean,
    addDay: () -> Unit,
    removeDay: () -> Unit,
    modifier: Modifier = Modifier
) {
    // State
    val textColor = if (selected) BoatSails else LightVolcanicRock
    val borderColor = if (selected) BoatSails else LightVolcanicRock
    val border = BorderStroke(width = 1.dp, color = borderColor)
    val focusRequester = remember { FocusRequester() }

    OutlinedButton(
        onClick = {
            focusRequester.requestFocus()
            if (selected) removeDay() else addDay()
        },
        colors = ButtonDefaults.outlinedButtonColors(contentColor = textColor),
        border = border,
        contentPadding = PaddingValues(0.dp),
        modifier = modifier
            .defaultMinSize(minWidth = 40.dp, minHeight = 40.dp)
            .focusRequester(focusRequester)
            .focusable()
    ) {
        Text(text = dayText)
    }
}

@Composable
fun AlertSettings(
    navigateToRingtonePickerScreen: () -> Unit,
    selectedRingtone: String,
    isVibrationEnabled: Boolean,
    toggleVibration: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val ringtoneFocusRequester = remember { FocusRequester() }
    val vibrationRowFocusRequester = remember { FocusRequester() }

    Column(modifier = modifier) {
        // Alert Icon and Text
        Row(modifier = Modifier.padding(start = 20.dp)) {
            Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = null,
                tint = DarkerBoatSails
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.section_alert),
                color = DarkerBoatSails,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Sound/Ringtone selection
        RowSelectionItem(
            rowOnClick = {
                ringtoneFocusRequester.requestFocus()
                navigateToRingtonePickerScreen()
            },
            rowLabelResId = R.string.alarm_create_edit_alarm_sound_label,
            choiceComponent = { Text(text = selectedRingtone) },
            modifier = Modifier
                .focusRequester(ringtoneFocusRequester)
                .focusable()
        )

        // Vibration toggle
        RowSelectionItem(
            rowOnClick = {
                vibrationRowFocusRequester.requestFocus()
                toggleVibration()
            },
            rowLabelResId = R.string.alarm_create_edit_alarm_vibration_label,
            choiceComponent = {
                Switch(
                    checked = isVibrationEnabled,
                    onCheckedChange = {
                        focusManager.clearFocus()
                        toggleVibration()
                    },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = WayDarkerBoatSails,
                        uncheckedTrackColor = DarkVolcanicRock
                    )
                )
            },
            modifier = Modifier
                .focusRequester(vibrationRowFocusRequester)
                .focusable()
        )
    }
}

@Composable
fun SnoozeSettings(
    snoozeDuration: Int,
    updateSnoozeDuration: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // State
    var showSnoozeDurationDialog by rememberSaveable { mutableStateOf(false) }
    val toggleSnoozeDurationDialog: () -> Unit = { showSnoozeDurationDialog = !showSnoozeDurationDialog }
    val focusRequester = remember { FocusRequester() }

    Column(modifier = modifier) {
        // Snooze Icon and Text
        Row(modifier = Modifier.padding(start = 20.dp)) {
            Icon(
                imageVector = Icons.Default.Snooze,
                contentDescription = null,
                tint = DarkerBoatSails
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.section_snooze),
                color = DarkerBoatSails,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Snooze Duration
        RowSelectionItem(
            rowOnClick = {
                focusRequester.requestFocus()
                toggleSnoozeDurationDialog()
            },
            rowLabelResId = R.string.alarm_create_edit_alarm_snooze_duration,
            choiceComponent = { Text(text = "$snoozeDuration ${stringResource(id = R.string.snooze_minutes)}") },
            modifier = Modifier
                .focusRequester(focusRequester)
                .focusable()
        )
    }

    // Snooze Duration Dialog
    if (showSnoozeDurationDialog) {
        SnoozeDurationDialog(
            initialSnoozeDuration = snoozeDuration,
            onCancel = toggleSnoozeDurationDialog,
            onConfirm = { newSnoozeDuration ->
                updateSnoozeDuration(newSnoozeDuration)
                toggleSnoozeDurationDialog()
            }
        )
    }
}

/*
 * Previews
 */

@Preview
@Composable
private fun AlarmCreateEditScreen12HourPreview() {
    val snackbarChannel = Channel<ValidationResult.Error<ValidationError>>()
    val snackbarFlow = snackbarChannel.receiveAsFlow()

    LavalarmTheme {
        AlarmCreateEditScreen(
            navHostController = rememberNavController(),
            navigateToRingtonePickerScreen = {},
            titleRes = R.string.alarm_creation_screen_title,
            alarm = repeatingAlarm,
            alarmRingtoneName = sampleRingtoneData.name,
            timeDisplay = TimeDisplay.TwelveHour,
            saveAndScheduleAlarm = { _, _ -> },
            updateName = {},
            updateDate = {},
            updateTime = { _, _ -> },
            addDay = {},
            removeDay = {},
            toggleVibration = {},
            updateSnoozeDuration = {},
            nameCharacterLimit = AlarmValidator.NAME_CHARACTER_LIMIT,
            isNameLengthValid = ValidationResult.Success(),
            isNameContentValid = ValidationResult.Success(),
            snackbarFlow = snackbarFlow,
            tryNavigateUp = {},
            tryNavigateBack = {},
            showUnsavedChangesDialog = false,
            unsavedChangesLeave = {},
            unsavedChangesStay = {}
        )
    }
}

@Preview
@Composable
private fun AlarmCreateEditScreen24HourPreview() {
    val snackbarChannel = Channel<ValidationResult.Error<ValidationError>>()
    val snackbarFlow = snackbarChannel.receiveAsFlow()

    LavalarmTheme {
        AlarmCreateEditScreen(
            navHostController = rememberNavController(),
            navigateToRingtonePickerScreen = {},
            titleRes = R.string.alarm_creation_screen_title,
            alarm = calendarAlarm,
            alarmRingtoneName = sampleRingtoneData.name,
            timeDisplay = TimeDisplay.TwentyFourHour,
            saveAndScheduleAlarm = { _, _ -> },
            updateName = {},
            updateDate = {},
            updateTime = { _, _ -> },
            addDay = {},
            removeDay = {},
            toggleVibration = {},
            updateSnoozeDuration = {},
            nameCharacterLimit = AlarmValidator.NAME_CHARACTER_LIMIT,
            isNameLengthValid = ValidationResult.Success(),
            isNameContentValid = ValidationResult.Success(),
            snackbarFlow = snackbarFlow,
            tryNavigateUp = {},
            tryNavigateBack = {},
            showUnsavedChangesDialog = false,
            unsavedChangesLeave = {},
            unsavedChangesStay = {}
        )
    }
}

@Preview
@Composable
private fun AlarmCreateEditScreenErrorIllegalCharacterPreview() {
    val snackbarChannel = Channel<ValidationResult.Error<ValidationError>>()
    val snackbarFlow = snackbarChannel.receiveAsFlow()

    LavalarmTheme {
        AlarmCreateEditScreen(
            navHostController = rememberNavController(),
            navigateToRingtonePickerScreen = {},
            titleRes = R.string.alarm_creation_screen_title,
            alarm = repeatingAlarm.copy(name = "Illegal.String"),
            alarmRingtoneName = sampleRingtoneData.name,
            timeDisplay = TimeDisplay.TwelveHour,
            saveAndScheduleAlarm = { _, _ -> },
            updateName = {},
            updateDate = {},
            updateTime = { _, _ -> },
            addDay = {},
            removeDay = {},
            toggleVibration = {},
            updateSnoozeDuration = {},
            nameCharacterLimit = AlarmValidator.NAME_CHARACTER_LIMIT,
            isNameLengthValid = ValidationResult.Success(),
            isNameContentValid = ValidationResult.Error(AlarmValidator.NameError.ILLEGAL_CHARACTER),
            snackbarFlow = snackbarFlow,
            tryNavigateUp = {},
            tryNavigateBack = {},
            showUnsavedChangesDialog = false,
            unsavedChangesLeave = {},
            unsavedChangesStay = {}
        )
    }
}

@Preview
@Composable
private fun AlarmCreateEditScreenErrorOnlyWhitespacePreview() {
    val snackbarChannel = Channel<ValidationResult.Error<ValidationError>>()
    val snackbarFlow = snackbarChannel.receiveAsFlow()

    LavalarmTheme {
        AlarmCreateEditScreen(
            navHostController = rememberNavController(),
            navigateToRingtonePickerScreen = {},
            titleRes = R.string.alarm_creation_screen_title,
            alarm = repeatingAlarm.copy(name = " "),
            alarmRingtoneName = sampleRingtoneData.name,
            timeDisplay = TimeDisplay.TwelveHour,
            saveAndScheduleAlarm = { _, _ -> },
            updateName = {},
            updateDate = {},
            updateTime = { _, _ -> },
            addDay = {},
            removeDay = {},
            toggleVibration = {},
            updateSnoozeDuration = {},
            nameCharacterLimit = AlarmValidator.NAME_CHARACTER_LIMIT,
            isNameLengthValid = ValidationResult.Success(),
            isNameContentValid = ValidationResult.Error(AlarmValidator.NameError.ONLY_WHITESPACE),
            snackbarFlow = snackbarFlow,
            tryNavigateUp = {},
            tryNavigateBack = {},
            showUnsavedChangesDialog = false,
            unsavedChangesLeave = {},
            unsavedChangesStay = {}
        )
    }
}

@Preview
@Composable
private fun AlarmCreateEditScreenErrorLengthPreview() {
    val snackbarChannel = Channel<ValidationResult.Error<ValidationError>>()
    val snackbarFlow = snackbarChannel.receiveAsFlow()

    LavalarmTheme {
        AlarmCreateEditScreen(
            navHostController = rememberNavController(),
            navigateToRingtonePickerScreen = {},
            titleRes = R.string.alarm_creation_screen_title,
            alarm = repeatingAlarm.copy(name = "Very long alarm name wow so big"),
            alarmRingtoneName = sampleRingtoneData.name,
            timeDisplay = TimeDisplay.TwelveHour,
            saveAndScheduleAlarm = { _, _ -> },
            updateName = {},
            updateDate = {},
            updateTime = { _, _ -> },
            addDay = {},
            removeDay = {},
            toggleVibration = {},
            updateSnoozeDuration = {},
            nameCharacterLimit = AlarmValidator.NAME_CHARACTER_LIMIT,
            isNameLengthValid = ValidationResult.Error(AlarmValidator.NameError.CHARACTER_LIMIT),
            isNameContentValid = ValidationResult.Success(),
            snackbarFlow = snackbarFlow,
            tryNavigateUp = {},
            tryNavigateBack = {},
            showUnsavedChangesDialog = false,
            unsavedChangesLeave = {},
            unsavedChangesStay = {}
        )
    }
}

@Preview
@Composable
private fun AlarmCreateEditScreenErrorLengthAndIllegalCharacterPreview() {
    val snackbarChannel = Channel<ValidationResult.Error<ValidationError>>()
    val snackbarFlow = snackbarChannel.receiveAsFlow()

    LavalarmTheme {
        AlarmCreateEditScreen(
            navHostController = rememberNavController(),
            navigateToRingtonePickerScreen = {},
            titleRes = R.string.alarm_creation_screen_title,
            alarm = repeatingAlarm.copy(name = "***Very long alarm name wow so big"),
            alarmRingtoneName = sampleRingtoneData.name,
            timeDisplay = TimeDisplay.TwelveHour,
            saveAndScheduleAlarm = { _, _ -> },
            updateName = {},
            updateDate = {},
            updateTime = { _, _ -> },
            addDay = {},
            removeDay = {},
            toggleVibration = {},
            updateSnoozeDuration = {},
            nameCharacterLimit = AlarmValidator.NAME_CHARACTER_LIMIT,
            isNameLengthValid = ValidationResult.Error(AlarmValidator.NameError.CHARACTER_LIMIT),
            isNameContentValid = ValidationResult.Error(AlarmValidator.NameError.ILLEGAL_CHARACTER),
            snackbarFlow = snackbarFlow,
            tryNavigateUp = {},
            tryNavigateBack = {},
            showUnsavedChangesDialog = false,
            unsavedChangesLeave = {},
            unsavedChangesStay = {}
        )
    }
}

@Preview
@Composable
private fun DateTimeSettingsPreview() {
    LavalarmTheme {
        DateTimeSettings(
            alarm = consistentFutureAlarm,
            timeDisplay = TimeDisplay.TwelveHour,
            updateDate = {},
            updateTime = { _, _ -> },
            addDay = {},
            removeDay = {}
        )
    }
}

@Preview
@Composable
private fun AlarmTimePreview() {
    LavalarmTheme {
        AlarmTime(
            dateTime = consistentFutureAlarm.dateTime,
            timeDisplay = TimeDisplay.TwelveHour,
            updateTime = { _, _ -> }
        )
    }
}
