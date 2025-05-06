package com.octrobi.lavalarm.core.ui.shared

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.octrobi.lavalarm.R
import com.octrobi.lavalarm.core.ui.theme.LavalarmTheme

@Composable
fun UnsavedChangesDialog(
    onLeave: () -> Unit,
    onStay: () -> Unit
) {
    BasicDialog(
        icon = Icons.Default.Warning,
        titleRes = R.string.unsaved_changes_dialog_title,
        cancelTextRes = R.string.unsaved_changes_dialog_leave,
        confirmTextRes = R.string.unsaved_changes_dialog_stay,
        onCancel = onLeave,
        onConfirm = onStay
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
    LavalarmTheme {
        UnsavedChangesDialog(
            onLeave = {},
            onStay = {}
        )
    }
}
