package com.joebsource.lavalarm.settings.ui.alarmdefaults.component

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.joebsource.lavalarm.R
import com.joebsource.lavalarm.core.ui.shared.BasicDialog
import com.joebsource.lavalarm.core.ui.theme.AlarmScratchTheme

@Composable
fun SnoozeDurationDialog(
    initialSnoozeDuration: Int,
    onCancel: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    // State
    var selectedSnoozeDuration by rememberSaveable { mutableIntStateOf(initialSnoozeDuration) }
    val updateSnoozeDuration: (Int) -> Unit = { selectedSnoozeDuration = it }

    BasicDialog(
        titleRes = R.string.snooze_duration,
        onCancel = onCancel,
        onConfirm = { onConfirm(selectedSnoozeDuration) }
    ) {
        // Snooze Duration Slider
        SnoozeDurationSlider(
            selectedSnoozeDuration = selectedSnoozeDuration,
            updateSnoozeDuration = updateSnoozeDuration,
            modifier = Modifier.padding(start = 8.dp, top = 20.dp, end = 8.dp, bottom = 18.dp)
        )
    }
}

/*
 * Previews
 */

@Preview
@Composable
private fun SnoozeDurationDialogPreview() {
    AlarmScratchTheme {
        SnoozeDurationDialog(
            initialSnoozeDuration = 15,
            onCancel = {},
            onConfirm = {}
        )
    }
}
