package com.octrobi.lavalarm.core.ui.shared

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.octrobi.lavalarm.R
import com.octrobi.lavalarm.core.ui.theme.DarkVolcanicRock
import com.octrobi.lavalarm.core.ui.theme.LavalarmTheme
import com.octrobi.lavalarm.core.ui.theme.WayDarkerBoatSails

@Composable
fun RowSelectionItem(
    rowOnClick: () -> Unit,
    @StringRes rowLabelResId: Int,
    choiceComponent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable { rowOnClick() }
            .minimumInteractiveComponentSize()
            .padding(start = 32.dp, end = 20.dp)
    ) {
        // Item label
        Text(text = stringResource(id = rowLabelResId))

        // Choice component
        choiceComponent()
    }
}

/*
 * Previews
 */

@Preview
@Composable
private fun RowSelectionItemPreview() {
    LavalarmTheme {
        RowSelectionItem(
            rowOnClick = {},
            rowLabelResId = R.string.alarm_create_edit_alarm_vibration_label,
            choiceComponent = {
                Switch(
                    checked = true,
                    onCheckedChange = {},
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = WayDarkerBoatSails,
                        uncheckedTrackColor = DarkVolcanicRock
                    )
                )
            }
        )
    }
}
