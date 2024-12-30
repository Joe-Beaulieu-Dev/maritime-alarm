package com.example.alarmscratch.core.ui.shared

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alarmscratch.R
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme

@Composable
fun UnsavedChangesDialog(
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    BasicDialog(
        titleRes = R.string.unsaved_changes_dialog_title,
        cancelTextRes = R.string.unsaved_changes_dialog_leave,
        confirmTextRes = R.string.unsaved_changes_dialog_stay,
        onCancel = onCancel,
        onConfirm = onConfirm
    ) {
        Row(modifier = Modifier.padding(start = 24.dp, top = 12.dp, end = 24.dp, bottom = 2.dp)) {
            Text(text = stringResource(id = R.string.unsaved_changes_dialog_body))
        }
    }
}

/*
 * Previews
 */

@Preview
@Composable
private fun UnsavedChangesDialogPreview() {
    AlarmScratchTheme {
        UnsavedChangesDialog(
            onCancel = {},
            onConfirm = {}
        )
    }
}
