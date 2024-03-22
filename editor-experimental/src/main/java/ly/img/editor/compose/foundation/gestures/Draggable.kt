/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ly.img.editor.compose.foundation.gestures

import android.annotation.SuppressLint
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.positionChangeIgnoreConsumed
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import ly.img.editor.compose.foundation.gestures.DragEvent.DragCancelled
import ly.img.editor.compose.foundation.gestures.DragEvent.DragDelta
import ly.img.editor.compose.foundation.gestures.DragEvent.DragStarted
import ly.img.editor.compose.foundation.gestures.DragEvent.DragStopped
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.sign

/**
 * State of [draggable]. Allows for a granular control of how deltas are consumed by the user as
 * well as to write custom drag methods using [drag] suspend function.
 */
interface DraggableState {
    /**
     * Call this function to take control of drag logic.
     *
     * All actions that change the logical drag position must be performed within a [drag]
     * block (even if they don't call any other methods on this object) in order to guarantee
     * that mutual exclusion is enforced.
     *
     * If [drag] is called from elsewhere with the [dragPriority] higher or equal to ongoing
     * drag, ongoing drag will be canceled.
     *
     * @param dragPriority of the drag operation
     * @param block to perform drag in
     */
    suspend fun drag(
        dragPriority: MutatePriority = MutatePriority.Default,
        block: suspend DragScope.() -> Unit,
    )

    /**
     * Dispatch drag delta in pixels avoiding all drag related priority mechanisms.
     *
     * **NOTE:** unlike [drag], dispatching any delta with this method will bypass scrolling of
     * any priority. This method will also ignore `reverseDirection` and other parameters set in
     * [draggable].
     *
     * This method is used internally for low level operations, allowing implementers of
     * [DraggableState] influence the consumption as suits them, e.g. introduce nested scrolling.
     * Manually dispatching delta via this method will likely result in a bad user experience,
     * you must prefer [drag] method over this one.
     *
     * @param delta amount of scroll dispatched in the nested drag process
     */
    fun dispatchRawDelta(delta: Float)
}

/**
 * Scope used for suspending drag blocks
 */
interface DragScope {
    /**
     * Attempts to drag by [pixels] px.
     */
    fun dragBy(pixels: Float)
}

/**
 * Default implementation of [DraggableState] interface that allows to pass a simple action that
 * will be invoked when the drag occurs.
 *
 * This is the simplest way to set up a [draggable] modifier. When constructing this
 * [DraggableState], you must provide a [onDelta] lambda, which will be invoked whenever
 * drag happens (by gesture input or a custom [DraggableState.drag] call) with the delta in
 * pixels.
 *
 * If you are creating [DraggableState] in composition, consider using [rememberDraggableState].
 *
 * @param onDelta callback invoked when drag occurs. The callback receives the delta in pixels.
 */
fun DraggableState(onDelta: (Float) -> Unit): DraggableState = DefaultDraggableState(onDelta)

/**
 * Create and remember default implementation of [DraggableState] interface that allows to pass a
 * simple action that will be invoked when the drag occurs.
 *
 * This is the simplest way to set up a [draggable] modifier. When constructing this
 * [DraggableState], you must provide a [onDelta] lambda, which will be invoked whenever
 * drag happens (by gesture input or a custom [DraggableState.drag] call) with the delta in
 * pixels.
 *
 * @param onDelta callback invoked when drag occurs. The callback receives the delta in pixels.
 */
@Composable
fun rememberDraggableState(onDelta: (Float) -> Unit): DraggableState {
    val onDeltaState = rememberUpdatedState(onDelta)
    return remember { DraggableState { onDeltaState.value.invoke(it) } }
}

/**
 * Configure touch dragging for the UI element in a single [Orientation]. The drag distance
 * reported to [DraggableState], allowing users to react on the drag delta and update their state.
 *
 * The common usecase for this component is when you need to be able to drag something
 * inside the component on the screen and represent this state via one float value
 *
 * If you need to control the whole dragging flow, consider using [pointerInput] instead with the
 * helper functions like [detectDragGestures].
 *
 * If you are implementing scroll/fling behavior, consider using [scrollable].
 *
 * @sample androidx.compose.foundation.samples.DraggableSample
 *
 * @param state [DraggableState] state of the draggable. Defines how drag events will be
 * interpreted by the user land logic.
 * @param orientation orientation of the drag
 * @param enabled whether or not drag is enabled
 * @param interactionSource [MutableInteractionSource] that will be used to emit
 * [DragInteraction.Start] when this draggable is being dragged.
 * @param startDragImmediately when set to true, draggable will start dragging immediately and
 * prevent other gesture detectors from reacting to "down" events (in order to block composed
 * press-based gestures).  This is intended to allow end users to "catch" an animating widget by
 * pressing on it. It's useful to set it when value you're dragging is settling / animating.
 * @param onDragStarted callback that will be invoked when drag is about to start at the starting
 * position, allowing user to suspend and perform preparation for drag, if desired. This suspend
 * function is invoked with the draggable scope, allowing for async processing, if desired
 * @param onDragStopped callback that will be invoked when drag is finished, allowing the
 * user to react on velocity and process it. This suspend function is invoked with the draggable
 * scope, allowing for async processing, if desired
 * @param reverseDirection reverse the direction of the scroll, so top to bottom scroll will
 * behave like bottom to top and left to right will behave like right to left.
 */
fun Modifier.draggable(
    state: DraggableState,
    orientation: Orientation,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    startDragImmediately: Boolean = false,
    onDragStarted: suspend CoroutineScope.(startedPosition: Offset) -> Unit = {},
    onDragStopped: suspend CoroutineScope.(velocity: Float) -> Unit = {},
    reverseDirection: Boolean = false,
): Modifier =
    draggable(
        state = state,
        orientation = orientation,
        enabled = enabled,
        interactionSource = interactionSource,
        startDragImmediately = { startDragImmediately },
        onDragStarted = onDragStarted,
        onDragStopped = { velocity -> onDragStopped(velocity.toFloat(orientation)) },
        reverseDirection = reverseDirection,
        canDrag = { true },
    )

@SuppressLint("ModifierFactoryUnreferencedReceiver")
internal fun Modifier.draggable(
    state: DraggableState,
    canDrag: (PointerInputChange) -> Boolean,
    orientation: Orientation,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    startDragImmediately: () -> Boolean,
    onDragStarted: suspend CoroutineScope.(startedPosition: Offset) -> Unit = {},
    onDragStopped: suspend CoroutineScope.(velocity: Velocity) -> Unit = {},
    reverseDirection: Boolean = false,
): Modifier =
    composed(
        inspectorInfo =
            debugInspectorInfo {
                name = "draggable"
                properties["canDrag"] = canDrag
                properties["orientation"] = orientation
                properties["enabled"] = enabled
                properties["reverseDirection"] = reverseDirection
                properties["interactionSource"] = interactionSource
                properties["startDragImmediately"] = startDragImmediately
                properties["onDragStarted"] = onDragStarted
                properties["onDragStopped"] = onDragStopped
                properties["state"] = state
            },
    ) {
        val draggedInteraction = remember { mutableStateOf<DragInteraction.Start?>(null) }
        DisposableEffect(interactionSource) {
            onDispose {
                draggedInteraction.value?.let { interaction ->
                    interactionSource?.tryEmit(DragInteraction.Cancel(interaction))
                    draggedInteraction.value = null
                }
            }
        }
        val channel = remember { Channel<DragEvent>(capacity = Channel.UNLIMITED) }
        val startImmediatelyState = rememberUpdatedState(startDragImmediately)
        val canDragState = rememberUpdatedState(canDrag)
        val dragLogic by rememberUpdatedState(
            DragLogic(onDragStarted, onDragStopped, draggedInteraction, interactionSource),
        )
        LaunchedEffect(state) {
            while (isActive) {
                var event = channel.receive()
                if (event !is DragStarted) continue
                with(dragLogic) { processDragStart(event as DragStarted) }
                try {
                    state.drag(MutatePriority.UserInput) {
                        while (event !is DragStopped && event !is DragCancelled) {
                            (event as? DragDelta)?.let { dragBy(it.delta.toFloat(orientation)) }
                            event = channel.receive()
                        }
                    }
                    with(dragLogic) {
                        if (event is DragStopped) {
                            processDragStop(event as DragStopped)
                        } else if (event is DragCancelled) {
                            processDragCancel()
                        }
                    }
                } catch (c: CancellationException) {
                    with(dragLogic) { processDragCancel() }
                }
            }
        }
        Modifier.pointerInput(orientation, enabled, reverseDirection) {
            if (!enabled) return@pointerInput
            coroutineScope {
                try {
                    awaitPointerEventScope {
                        while (isActive) {
                            val velocityTracker = VelocityTracker()
                            awaitDownAndSlop(
                                canDragState,
                                startImmediatelyState,
                                velocityTracker,
                                orientation,
                            )?.let {
                                var isDragSuccessful = false
                                try {
                                    isDragSuccessful =
                                        awaitDrag(
                                            it.first,
                                            it.second,
                                            velocityTracker,
                                            channel,
                                            reverseDirection,
                                            orientation,
                                        )
                                } catch (cancellation: CancellationException) {
                                    isDragSuccessful = false
                                    if (!isActive) throw cancellation
                                } finally {
                                    val event =
                                        if (isDragSuccessful) {
                                            val velocity =
                                                velocityTracker.calculateVelocity()
                                            DragStopped(velocity * if (reverseDirection) -1f else 1f)
                                        } else {
                                            DragCancelled
                                        }
                                    channel.trySend(event)
                                }
                            }
                        }
                    }
                } catch (exception: CancellationException) {
                    if (!isActive) {
                        throw exception
                    }
                }
            }
        }
    }

private suspend fun AwaitPointerEventScope.awaitDownAndSlop(
    canDrag: State<(PointerInputChange) -> Boolean>,
    startDragImmediately: State<() -> Boolean>,
    velocityTracker: VelocityTracker,
    orientation: Orientation,
): Pair<PointerInputChange, Offset>? {
    val initialDown =
        awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
    return if (!canDrag.value.invoke(initialDown)) {
        null
    } else if (startDragImmediately.value.invoke()) {
        initialDown.consume()
        velocityTracker.addPointerInputChange(initialDown)
        // since we start immediately we don't wait for slop and the initial delta is 0
        initialDown to Offset.Zero
    } else {
        val down = awaitFirstDown(requireUnconsumed = false)
        velocityTracker.addPointerInputChange(down)
        var initialDelta = Offset.Zero
        val postPointerSlop = { event: PointerInputChange, offset: Offset ->
            velocityTracker.addPointerInputChange(event)
            event.consume()
            initialDelta = offset
        }

        val afterSlopResult =
            awaitPointerSlopOrCancellation(
                down.id,
                down.type,
                pointerDirectionConfig = orientation.toPointerDirectionConfig(),
                onPointerSlopReached = postPointerSlop,
            )

        if (afterSlopResult != null) afterSlopResult to initialDelta else null
    }
}

private suspend fun AwaitPointerEventScope.awaitDrag(
    startEvent: PointerInputChange,
    initialDelta: Offset,
    velocityTracker: VelocityTracker,
    channel: SendChannel<DragEvent>,
    reverseDirection: Boolean,
    orientation: Orientation,
): Boolean {
    val overSlopOffset = initialDelta
    val xSign = sign(startEvent.position.x)
    val ySign = sign(startEvent.position.y)
    val adjustedStart =
        startEvent.position -
            Offset(overSlopOffset.x * xSign, overSlopOffset.y * ySign)
    channel.trySend(DragStarted(adjustedStart))

    channel.trySend(DragDelta(if (reverseDirection) initialDelta * -1f else initialDelta))

    return onDragOrUp(orientation, startEvent.id) { event ->
        // Velocity tracker takes all events, even UP
        velocityTracker.addPointerInputChange(event)

        // Dispatch only MOVE events
        if (!event.changedToUpIgnoreConsumed()) {
            val delta = event.positionChange()
            event.consume()
            channel.trySend(DragDelta(if (reverseDirection) delta * -1f else delta))
        }
    }
}

private suspend fun AwaitPointerEventScope.onDragOrUp(
    orientation: Orientation,
    pointerId: PointerId,
    onDrag: (PointerInputChange) -> Unit,
): Boolean {
    val motionFromChange: (PointerInputChange) -> Float =
        if (orientation == Orientation.Vertical) {
            { it.positionChangeIgnoreConsumed().y }
        } else {
            { it.positionChangeIgnoreConsumed().x }
        }

    return drag(
        pointerId = pointerId,
        onDrag = onDrag,
        motionFromChange = motionFromChange,
        motionConsumed = { it.isConsumed },
    )?.let(onDrag) != null
}

private class DragLogic(
    val onDragStarted: suspend CoroutineScope.(startedPosition: Offset) -> Unit,
    val onDragStopped: suspend CoroutineScope.(velocity: Velocity) -> Unit,
    val dragStartInteraction: MutableState<DragInteraction.Start?>,
    val interactionSource: MutableInteractionSource?,
) {
    suspend fun CoroutineScope.processDragStart(event: DragStarted) {
        dragStartInteraction.value?.let { oldInteraction ->
            interactionSource?.emit(DragInteraction.Cancel(oldInteraction))
        }
        val interaction = DragInteraction.Start()
        interactionSource?.emit(interaction)
        dragStartInteraction.value = interaction
        onDragStarted.invoke(this, event.startPoint)
    }

    suspend fun CoroutineScope.processDragStop(event: DragStopped) {
        dragStartInteraction.value?.let { interaction ->
            interactionSource?.emit(DragInteraction.Stop(interaction))
            dragStartInteraction.value = null
        }
        onDragStopped.invoke(this, event.velocity)
    }

    suspend fun CoroutineScope.processDragCancel() {
        dragStartInteraction.value?.let { interaction ->
            interactionSource?.emit(DragInteraction.Cancel(interaction))
            dragStartInteraction.value = null
        }
        onDragStopped.invoke(this, Velocity.Zero)
    }
}

private class DefaultDraggableState(val onDelta: (Float) -> Unit) : DraggableState {
    private val dragScope: DragScope =
        object : DragScope {
            override fun dragBy(pixels: Float): Unit = onDelta(pixels)
        }

    private val scrollMutex = MutatorMutex()

    override suspend fun drag(
        dragPriority: MutatePriority,
        block: suspend DragScope.() -> Unit,
    ): Unit =
        coroutineScope {
            scrollMutex.mutateWith(dragScope, dragPriority, block)
        }

    override fun dispatchRawDelta(delta: Float) {
        return onDelta(delta)
    }
}

private sealed class DragEvent {
    class DragStarted(val startPoint: Offset) : DragEvent()

    class DragStopped(val velocity: Velocity) : DragEvent()

    object DragCancelled : DragEvent()

    class DragDelta(val delta: Offset) : DragEvent()
}

private fun Offset.toFloat(orientation: Orientation) = if (orientation == Orientation.Vertical) this.y else this.x

private fun Velocity.toFloat(orientation: Orientation) = if (orientation == Orientation.Vertical) this.y else this.x
