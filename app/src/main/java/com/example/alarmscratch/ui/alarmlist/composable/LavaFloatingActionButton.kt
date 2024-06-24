package com.example.alarmscratch.ui.alarmlist.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmAdd
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alarmscratch.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.ui.theme.AncientLavaOrange
import com.example.alarmscratch.ui.theme.MaxBrightLavaOrange

@Composable
fun LavaFloatingActionButton(
    onFabClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.TopCenter,
        // this elevation is not from the top of the volcano, fix this
        modifier = modifier
    ) {
        // Center Blob
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(30.dp)
                .offset(x = 0.dp, y = 27.dp)
                .clip(shape = CircleShape)
                .background(color = AncientLavaOrange)
        )

        // Left Drip
        Box(
            modifier = Modifier
                .width(10.dp)
                .height(20.dp)
                .offset(x = (-10).dp, y = 45.dp)
                .clip(shape = CircleShape)
                .background(color = AncientLavaOrange)
        )

        // Right Drip
        Box(
            modifier = Modifier
                .width(10.dp)
                .height(25.dp)
                .offset(x = (8).dp, y = 45.dp)
                .clip(shape = CircleShape)
                .background(color = AncientLavaOrange)
        )

        // Floating Action Button
        FloatingActionButton(
            shape = CircleShape,
            onClick = { onFabClicked() },
            containerColor = AncientLavaOrange,
            contentColor = MaxBrightLavaOrange,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 0.dp
            )
        ) {
            Icon(
                imageVector = Icons.Default.AlarmAdd,
                contentDescription = null
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0066CC
)
@Composable
private fun LavaFloatingActionButtonPreview() {
    AlarmScratchTheme {
        LavaFloatingActionButton(
            onFabClicked = {},
            modifier = Modifier.padding(12.dp)
        )
    }
}
