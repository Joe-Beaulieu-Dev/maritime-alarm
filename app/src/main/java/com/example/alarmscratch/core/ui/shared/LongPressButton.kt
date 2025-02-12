package com.example.alarmscratch.core.ui.shared

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import kotlinx.coroutines.coroutineScope

@Composable
fun LongPressButton(
    longPressThreshold: Int,
    onPressStart: () -> Unit,
    onShortPress: () -> Unit,
    onLongPress: () -> Unit,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val containerColor = if (enabled) colors.containerColor else colors.disabledContainerColor
    val contentColor = if (enabled) colors.contentColor else colors.disabledContentColor

    Surface(
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        modifier = modifier
            .semantics { role = Role.Button }
            .pointerInput(Unit) {
                detectPressGestures(
                    longPressThreshold = longPressThreshold.toLong(),
                    onPressStart = onPressStart,
                    onLongPress = onLongPress,
                    onShortPress = onShortPress
                )
            }
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .defaultMinSize(
                    minWidth = ButtonDefaults.MinWidth,
                    minHeight = ButtonDefaults.MinHeight
                )
                .padding(contentPadding),
            content = content
        )
    }
}

suspend fun PointerInputScope.detectPressGestures(
    longPressThreshold: Long,
    onPressStart: (() -> Unit)? = null,
    onShortPress: (() -> Unit)? = null,
    onLongPress: (() -> Unit)? = null
) = coroutineScope {
    awaitEachGesture {
        // Consume the initial press event
        val down = awaitFirstDown()
        down.consume()

        // Record time of initial press, and invoke onPressStart()
        val downTime = System.currentTimeMillis()
        onPressStart?.invoke()

        var longPressThresholdPassed = false

        // Grab initial PointerEvent and loop until the User releases
        do {
            val event: PointerEvent = awaitPointerEvent()
            val currentTime = System.currentTimeMillis()

            // First time long press threshold is passed. Invoke onLongPress().
            if (!longPressThresholdPassed && currentTime - downTime >= longPressThreshold) {
                onLongPress?.invoke()
                longPressThresholdPassed = true
            }

            // Consume press events since they're being handled here
            event.changes.forEach { it.consume() }
        } while (event.changes.any { it.pressed })

        // Invoke onShortPress() if long press threshold was not reached
        if (!longPressThresholdPassed) {
            onShortPress?.invoke()
        }
    }
}

/*
 * Previews
 */

@Preview(
    showBackground = true,
    backgroundColor = 0xFF373736
)
@Composable
private fun LongPressButtonPreview() {
    AlarmScratchTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(200.dp)
                .height(200.dp)
        ) {
            LongPressButton(
                longPressThreshold = 3000,
                onPressStart = {},
                onShortPress = {},
                onLongPress = {}
            ) {
                Text(text = "Confirm")
            }
        }
    }
}
