package com.example.alarmscratch.ui.settings

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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.alarmscratch.R
import com.example.alarmscratch.ui.navigation.Destination
import com.example.alarmscratch.ui.navigation.navigateSingleTop
import com.example.alarmscratch.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.ui.theme.MediumVolcanicRock

@Composable
fun SettingsScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = modifier
    ) {
        LazyColumn {
            item {
                SettingsComponent(icon = Icons.Default.Settings, nameRes = R.string.settings_general, onClick = {})
            }
            item {
                HorizontalDivider(
                    thickness = 2.dp,
                    color = MediumVolcanicRock,
                    modifier = Modifier.padding(horizontal = 15.dp)
                )
            }
            item {
                SettingsComponent(
                    icon = Icons.Default.Alarm,
                    nameRes = R.string.settings_alarm_defaults,
                    onClick = { navHostController.navigateSingleTop(route = Destination.AlarmDefaultSettings.route) }
                )
            }
        }
    }
}

@Composable
fun SettingsComponent(
    icon: ImageVector,
    @StringRes
    nameRes: Int,
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
    AlarmScratchTheme {
        SettingsScreen(
            navHostController = rememberNavController(),
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
    AlarmScratchTheme {
        SettingsComponent(icon = Icons.Default.Alarm, nameRes = R.string.settings_alarm_defaults, onClick = {})
    }
}
