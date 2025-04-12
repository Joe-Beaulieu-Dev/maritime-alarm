package com.joebsource.lavalarm.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.joebsource.lavalarm.R
import com.joebsource.lavalarm.core.ui.theme.LavalarmTheme
import com.joebsource.lavalarm.core.ui.theme.MediumVolcanicRock

@Composable
fun SettingsScreen(
    navigateToGeneralSettingsScreen: () -> Unit,
    navigateToAlarmDefaultsScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier
    ) {
        LazyColumn {
            // General Settings
            item {
                SettingsComponent(
                    icon = Icons.Default.Settings,
                    nameRes = R.string.settings_general,
                    onClick = navigateToGeneralSettingsScreen
                )
            }
            item {
                HorizontalDivider(
                    thickness = 2.dp,
                    color = MediumVolcanicRock,
                    modifier = Modifier.padding(horizontal = 15.dp)
                )
            }

            // Alarm Defaults
            item {
                SettingsComponent(
                    icon = Icons.Default.Alarm,
                    nameRes = R.string.settings_alarm_defaults,
                    onClick = navigateToAlarmDefaultsScreen
                )
            }
        }
    }
}

@Composable
fun SettingsComponent(
    icon: ImageVector,
    @StringRes nameRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        // Icon and Label
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 15.dp, top = 35.dp, bottom = 35.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null)
            Text(
                text = stringResource(id = nameRes),
                fontSize = 24.sp,
                modifier = Modifier.padding(start = 15.dp)
            )
        }

        // Chevron
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            modifier = Modifier.padding(end = 15.dp)
        )
    }
}

/*
 * Previews
 */

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0066CC
)
@Composable
private fun SettingsScreenPreview() {
    LavalarmTheme {
        SettingsScreen(
            navigateToGeneralSettingsScreen = {},
            navigateToAlarmDefaultsScreen = {},
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF373736
)
@Composable
private fun SettingsComponentPreview() {
    LavalarmTheme {
        SettingsComponent(icon = Icons.Default.Alarm, nameRes = R.string.settings_alarm_defaults, onClick = {})
    }
}
