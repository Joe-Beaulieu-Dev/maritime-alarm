package com.example.alarmscratch.core.ui.shared

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alarmscratch.R
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme

// TODO: Use M3 TopAppBar once it's no longer experimental
@Composable
fun CustomTopAppBar(
    @StringRes titleRes: Int,
    modifier: Modifier = Modifier,
    navigationButton: @Composable () -> Unit = {},
    actionButton: @Composable () -> Unit = {}
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        // Navigation Button and Title
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Navigation Button
            navigationButton()
            // Title
            Text(
                text = stringResource(id = titleRes),
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
        // Action Button
        actionButton()
    }
}

/*
 * Previews
 */

@Preview
@Composable
private fun CustomTopAppBarPreview() {
    AlarmScratchTheme {
        CustomTopAppBar(
            titleRes = R.string.alarm_creation_screen_title,
            navigationButton = {
                IconButton(onClick = {}) {
                    Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
                }
            },
            actionButton = {
                IconButton(onClick = {}) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null)
                }
            }
        )
    }
}
