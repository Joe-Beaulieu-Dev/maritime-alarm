package com.octrobi.lavalarm.core.gesture

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Detect and react to single tap gestures. Gestures include: [onPressStart], [onShortPress], [onLongPress],
 * and [onLongPressRelease]. [onPressStart] is invoked immediately after the first down event, and then
 * either [onShortPress] or [onLongPress] are subsequently invoked depending on if the [longPressTimeout]
 * was exceeded before the first up event was received. In the event of a long press, [onLongPress] is
 * invoked immediately after [longPressTimeout] has been exceeded, and all press events will continue to be
 * consumed until the first up event is received, at which point [onLongPressRelease] is invoked.
 *
 * Ex: [longPressTimeout] is set to 1.5 seconds. The User holds the Button down for 2 seconds. [onLongPress] will be
 * invoked at the 1.5 second mark, but this function will still continue to consume the last 0.5 seconds of the "hold"
 * since the entire 2 seconds of the User's press is considered to be a single intention.
 *
 * This function does not perform any special behavior specific to double taps. A double tap will simply result in
 * 2 calls to [onPressStart] and [onShortPress].
 *
 * @param longPressTimeout how long to wait after a down event for the press to be considered a long press
 * @param onPressStart invoked after the first down event
 * @param onShortPress invoked after the first up event if the duration of the press did not exceed [longPressTimeout]
 * @param onLongPress invoked once the duration of the press exceeds [longPressTimeout]
 * @param onLongPressRelease invoked after the User actually releases a long press, which will be an arbitrary
 *   amount of time after [onLongPress] is invoked
 * @param interactionSource an optional MutableInteractionSource used to emit press Interactions related to this function.
 *   This is typically used for things like adding a Ripple effect to a composable. This is not required for any of
 *   the other callbacks passed to this function to work. It is separate and optional.
 */
suspend fun PointerInputScope.detectSingleTapGestures(
    longPressTimeout: Long,
    onPressStart: (() -> Unit)? = null,
    onShortPress: (() -> Unit)? = null,
    onLongPress: (() -> Unit)? = null,
    onLongPressRelease: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource
) = coroutineScope {
    awaitEachGesture {
        // Consume the initial down event and invoke onPressStart()
        val downPress = awaitFirstDown()
        downPress.consume()
        onPressStart?.invoke()

        // Create a PressInteraction for the down press and emit it via the
        // given MutableInteractionSource. This is typically used to initiate a Ripple effect.
        val downPressInteraction = PressInteraction.Press(downPress.position)
        launch {
            interactionSource.emit(downPressInteraction)
        }

        // Wait for the next up event and consume it. If it takes longer than longPressTimeout
        // then a PointerEventTimeoutCancellationException is thrown and caught, onLongPress() is immediately
        // invoked, and all subsequent press events are consumed until the first up event is received,
        // at which time onLongPressReleased() is invoked.
        var shortPressUpOrCancel: PointerInputChange? = null
        var longPressTimeoutExceeded = false
        try {
            // Wait for up event or cancellation. If the pointer is held down longer than
            // longPressTimeout then throw a PointerEventTimeoutCancellationException.
            shortPressUpOrCancel = withTimeout(longPressTimeout) {
                waitForUpOrCancellation()
            }

            // The up event was not cancelled, so consume it
            shortPressUpOrCancel?.consume()
        } catch (e: PointerEventTimeoutCancellationException) {
            // Invoke onLongPress() immediately. However, since the User could still be holding
            // the pointer down, we must consume all press events until the pointer is lifted,
            // even though we've already invoked onLongPress(). This is because the entirety of
            // this window of press events is to be interpreted as a single User intention.
            longPressTimeoutExceeded = true
            onLongPress?.invoke()
            consumeUntilUp()

            // The User has released their press. Invoke onLongPressRelease().
            onLongPressRelease?.invoke()
        }

        // The up event was successful and not cancelled, and the
        // longPressTimeout was not exceeded. Invoke onShortPress().
        if (shortPressUpOrCancel != null) {
            onShortPress?.invoke()
        }

        val finalPressInteraction =
            if (shortPressUpOrCancel != null || longPressTimeoutExceeded) {
                // There was either a non-cancelled short press, or the longPressTimeout was exceeded.
                // Whether or not the long press was cancelled AFTER the longPressTimeout was exceeded is irrelevant.
                PressInteraction.Release(downPressInteraction)
            } else {
                // There was no non-cancelled short press, and the longPressTimeout was not exceeded
                PressInteraction.Cancel(downPressInteraction)
            }

        // Emit a Release or Cancel PressInteraction via the given
        // MutableInteractionSource. This is typically used to end a Ripple effect.
        launch {
            interactionSource.emit(finalPressInteraction)
        }
    }
}

/**
 * Consume all pointer events until nothing is pressed,
 * assuming that something is currently pressed.
 */
private suspend fun AwaitPointerEventScope.consumeUntilUp() {
    do {
        val event = awaitPointerEvent()
        event.changes.fastForEach { it.consume() }
    } while (event.changes.fastAny { it.pressed })
}
