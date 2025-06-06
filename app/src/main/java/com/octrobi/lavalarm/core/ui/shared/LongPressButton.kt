package com.octrobi.lavalarm.core.ui.shared

import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
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
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.octrobi.lavalarm.core.gesture.detectSingleTapGestures
import com.octrobi.lavalarm.core.ui.theme.LavalarmTheme

@Composable
fun LongPressButton(
    longPressTimeout: Long,
    onPressStart: (() -> Unit)? = null,
    onShortPress: (() -> Unit)? = null,
    onLongPress: () -> Unit,
    onLongPressRelease: (() -> Unit)? = null,
    onPressCancelled: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    // State
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val containerColor = if (enabled) colors.containerColor else colors.disabledContainerColor
    val contentColor = if (enabled) colors.contentColor else colors.disabledContentColor

    // Listen for press cancellations
    LaunchedEffect(key1 = interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Cancel) {
                onPressCancelled?.invoke()
            }
        }
    }

    Surface(
        color = containerColor,
        contentColor = contentColor,
        modifier = modifier
            .semantics { role = Role.Button }
            .clip(shape)
            .pointerInput(enabled) {
                if (enabled) {
                    detectSingleTapGestures(
                        longPressTimeout = longPressTimeout,
                        onPressStart = onPressStart,
                        onShortPress = onShortPress,
                        onLongPress = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            onLongPress()
                        },
                        onLongPressRelease = onLongPressRelease,
                        interactionSource = interactionSource
                    )
                }
            }
            .indication(interactionSource, ripple())
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

/*
 * Previews
 */

@Preview(
    showBackground = true,
    backgroundColor = 0xFF373736
)
@Composable
private fun LongPressButtonPreview() {
    LavalarmTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(200.dp)
                .height(200.dp)
        ) {
            LongPressButton(
                longPressTimeout = 3000L,
                onPressStart = {},
                onShortPress = {},
                onLongPress = {}
            ) {
                Text(text = "Confirm")
            }
        }
    }
}
