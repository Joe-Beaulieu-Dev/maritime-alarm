package com.example.alarmscratch.core.ui.core.component

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmAdd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.RippleConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.alarmscratch.R
import com.example.alarmscratch.core.navigation.Destination
import com.example.alarmscratch.core.ui.notificationcheck.AppNotificationChannel
import com.example.alarmscratch.core.ui.notificationcheck.SimpleNotificationGate
import com.example.alarmscratch.core.ui.permission.Permission
import com.example.alarmscratch.core.ui.permission.SimplePermissionGate
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.AncientLavaOrange
import com.example.alarmscratch.core.ui.theme.MaxBrightLavaOrange

@Composable
fun LavaFloatingActionButton(
    currentCoreDestination: Destination,
    previousCoreDestination: Destination,
    onFabClicked: () -> Unit,
    volcanoSpacerHeight: Dp,
    modifier: Modifier = Modifier
) {
    // LavaFloatingActionButton specs
    val fabHeight = 70.dp
    val fabAnimationHeight = with(LocalDensity.current) { (fabHeight + volcanoSpacerHeight).toPx().toInt() }

    // Visibility state
    val onScreenWithFab = currentCoreDestination is Destination.AlarmListScreen
    val comingFromScreenWithFab = previousCoreDestination is Destination.AlarmListScreen
    val visibleState = remember(key1 = currentCoreDestination, key2 = previousCoreDestination) {
        // True == FAB up
        // False == FAB down
        val initialState = when {
            onScreenWithFab && comingFromScreenWithFab ->
                true
            onScreenWithFab && !comingFromScreenWithFab ->
                false
            !onScreenWithFab && comingFromScreenWithFab ->
                true
            else ->
                // !onScreenWithFab && !comingFromScreenWithFab
                false
        }

        MutableTransitionState(initialState = initialState).apply { targetState = onScreenWithFab }
    }

    val floatingActionButtonContent: @Composable () -> Unit = {
        AnimatedVisibility(
            visibleState = visibleState,
            enter = slideInVertically(
                animationSpec = tween(durationMillis = 150, easing = LinearOutSlowInEasing),
                initialOffsetY = { fabAnimationHeight }
            ),
            exit = slideOutVertically(
                animationSpec = tween(durationMillis = 250, easing = FastOutLinearInEasing),
                targetOffsetY = { fabAnimationHeight }
            )
        ) {
            LavaFloatingActionButtonContent(
                enabled = onScreenWithFab,
                onFabClicked = onFabClicked,
                modifier = modifier
            )
        }
    }

    val notificationGatedFab: @Composable () -> Unit = {
        SimpleNotificationGate(
            appNotificationChannel = AppNotificationChannel.Alarm,
            gatedComposable = floatingActionButtonContent
        )
    }

    // POST_NOTIFICATIONS permission requires API 33 (TIRAMISU)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        SimplePermissionGate(
            permission = Permission.PostNotifications,
            gatedComposable = notificationGatedFab
        )
    } else {
        notificationGatedFab()
    }
}

// ExperimentalMaterial3Api OptIn for LocalRippleConfiguration and RippleConfiguration
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LavaFloatingActionButtonContent(
    enabled: Boolean,
    onFabClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Copy of Android's internal FabPrimaryTokens.ContainerHeight
    val fabDefaultHeight = 56.dp
    val largeDripHang = 14.dp
    val rippleConfiguration: RippleConfiguration? = if (enabled) RippleConfiguration() else null
    val elevation = if (enabled) {
        FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
    } else {
        FloatingActionButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp
        )
    }

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier.height(fabDefaultHeight + largeDripHang)
    ) {
        // Center Blob
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .width(40.dp)
                .height(30.dp)
                .offset(x = 0.dp, y = (-13).dp)
                .clip(shape = CircleShape)
                .background(color = AncientLavaOrange)
        )

        // Left Drip
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .width(10.dp)
                .height(20.dp)
                .offset(x = (-10).dp, y = (-5).dp)
                .clip(shape = CircleShape)
                .background(color = AncientLavaOrange)
        )

        // Right Drip
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .width(10.dp)
                .height(25.dp)
                .offset(x = 8.dp, y = 0.dp)
                .clip(shape = CircleShape)
                .background(color = AncientLavaOrange)
        )

        // Floating Action Button
        CompositionLocalProvider(value = LocalRippleConfiguration provides rippleConfiguration) {
            FloatingActionButton(
                shape = CircleShape,
                onClick = { if (enabled) onFabClicked() },
                containerColor = AncientLavaOrange,
                contentColor = MaxBrightLavaOrange,
                elevation = elevation
            ) {
                Icon(
                    imageVector = Icons.Default.AlarmAdd,
                    contentDescription = stringResource(id = R.string.lava_fab_create_alarm_cd)
                )
            }
        }
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
private fun LavaFloatingActionButtonPreview() {
    AlarmScratchTheme {
        LavaFloatingActionButtonContent(
            enabled = true,
            onFabClicked = {},
            modifier = Modifier.padding(20.dp)
        )
    }
}
