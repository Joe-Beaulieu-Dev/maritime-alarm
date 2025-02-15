package com.example.alarmscratch.core.gesture

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
 * Detect and react to single tap gestures. Gestures include: [onPressStart], [onShortPress], and [onLongPress].
 * [onPressStart] is immediately invoked after the first down event, and either [onShortPress] or [onLongPress]
 * are subsequently invoked depending on if the [longPressTimeout] was exceeded before the first up event was received.
 *
 * Although [onLongPress] is invoked immediately after [longPressTimeout] has been exceeded, all press events
 * continue to be consumed until the first up event is received.
 *
 * Ex: [longPressTimeout] is set to 1.5 seconds. The User holds the Button down for 2 seconds. [onLongPress] will be
 * invoked at the 1.5 second mark, but we will still continue to consume the last 0.5 seconds of the "hold" since the
 * entire 2 seconds of the User's press in considered to be a single intention.
 *
 * This function does not perform any special behavior specific to double taps. A double tap will simply result in
 * 2 calls to [onPressStart] and [onShortPress].
 *
 * @param longPressTimeout how long to wait after a down event for the press to be considered a long press
 * @param onPressStart invoked immediately after the first down event
 * @param onShortPress invoked after the first up event if the duration of the press did not exceed [longPressTimeout]
 * @param onLongPress invoked immediately once the duration of the press exceeds [longPressTimeout]
 * @param onLongPressRelease invoked immediately after the User actually releases a long press,
 *   which will be an arbitrary amount of time after [onLongPress] is invoked
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
        // given MutableInteractionSource. This is typically used to initiate Ripple effects.
        val downPressInteraction = PressInteraction.Press(downPress.position)
        launch {
            interactionSource.emit(downPressInteraction)
        }

        // Wait for the next up event and consume it. If it takes longer than longPressTimeout
        // then a PointerEventTimeoutCancellationException is thrown and caught, onLongPress() is immediately
        // invoked, and all subsequent press events are consumed until the first up event is received.
        var upOrCancel: PointerInputChange? = null
        try {
            // Wait for up event or cancellation. If the pointer is held down longer than
            // longPressTimeout then throw a PointerEventTimeoutCancellationException.
            upOrCancel = withTimeout(longPressTimeout) {
                waitForUpOrCancellation()
            }

            // The up event was not cancelled, so consume it
            upOrCancel?.consume()
        } catch (e: PointerEventTimeoutCancellationException) {
            // Invoke onLongPress() immediately. However, since the User could still be holding
            // the pointer down, we must consume all press events until the pointer is lifted,
            // even though we've already invoked onLongPress(). This is because the entirety of
            // this window of press events is to be interpreted as a single User intention.
            onLongPress?.invoke()
            consumeUntilUp()

            // The User has released their press. Invoke onLongPressRelease().
            onLongPressRelease?.invoke()
        }

        // The up event was successful and not cancelled, and the
        // longPressTimeout was not exceeded. Invoke onShortPress().
        if (upOrCancel != null) {
            onShortPress?.invoke()
        }

        // Emit a Release or Cancel PressInteraction via the given
        // MutableInteractionSource. This is typically used to end Ripple effects.
        launch {
            releaseOrCancelPressInteraction(interactionSource, downPressInteraction, upOrCancel)
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

/**
 * Emit either PressInteraction.Cancel or PressInteraction.Release from the given [interactionSource]
 * based on whether [upOrCancel] is null or non-null, respectively.
 *
 * @param interactionSource MutableInteractionSource to emit the PressInteraction from
 * @param downPress the source Press interaction that is being either released or cancelled
 * @param upOrCancel PointerInputChange being used to determine if the Press is being released or cancelled
 */
private suspend fun releaseOrCancelPressInteraction(
    interactionSource: MutableInteractionSource,
    downPress: PressInteraction.Press,
    upOrCancel: PointerInputChange?
) {
    when (upOrCancel) {
        null ->
            interactionSource.emit(PressInteraction.Cancel(downPress))
        else ->
            interactionSource.emit(PressInteraction.Release(downPress))
    }
}
