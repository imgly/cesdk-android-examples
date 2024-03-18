/*
 * Copyright 2020 The Android Open Source Project
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

package ly.img.editor.compose.foundation

import android.annotation.SuppressLint
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.layout.LocalPinnableContainer
import androidx.compose.ui.layout.PinnableContainer
import androidx.compose.ui.platform.InspectableModifier
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.focused
import androidx.compose.ui.semantics.requestFocus
import androidx.compose.ui.semantics.semantics
import kotlinx.coroutines.launch
import ly.img.editor.compose.foundation.relocation.BringIntoViewRequester
import ly.img.editor.compose.foundation.relocation.bringIntoViewRequester

/**
 * Configure component to be focusable via focus system or accessibility "focus" event.
 *
 * Add this modifier to the element to make it focusable within its bounds.
 *
 * @sample androidx.compose.foundation.samples.FocusableSample
 *
 * @param enabled Controls the enabled state. When `false`, element won't participate in the focus
 * @param interactionSource [MutableInteractionSource] that will be used to emit
 * [FocusInteraction.Focus] when this element is being focused.
 */
fun Modifier.focusable(
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) = composed(
    inspectorInfo =
        debugInspectorInfo {
            name = "focusable"
            properties["enabled"] = enabled
            properties["interactionSource"] = interactionSource
        },
) {
    val scope = rememberCoroutineScope()
    val focusedInteraction = remember { mutableStateOf<FocusInteraction.Focus?>(null) }
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    // Focusables have a few different cases where they need to make sure they stay visible:
    //
    // 1. Focusable node newly receives focus – always bring entire node into view. That's what this
    //    BringIntoViewRequester does.
    // 2. Scrollable parent resizes and the currently-focused item is now hidden – bring entire node
    //    into view if it was also in view before the resize. This handles the case of
    //    `softInputMode=ADJUST_RESIZE`. See b/216842427.
    // 3. Entire window is panned due to `softInputMode=ADJUST_PAN` – report the correct focused
    //    rect to the view system, and the view system itself will keep the focused area in view.
    //    See aosp/1964580.
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    DisposableEffect(interactionSource) {
        onDispose {
            focusedInteraction.value?.let { oldValue ->
                val interaction = FocusInteraction.Unfocus(oldValue)
                interactionSource?.tryEmit(interaction)
                focusedInteraction.value = null
            }
        }
    }
    DisposableEffect(enabled) {
        if (!enabled) {
            scope.launch {
                focusedInteraction.value?.let { oldValue ->
                    val interaction = FocusInteraction.Unfocus(oldValue)
                    interactionSource?.emit(interaction)
                    focusedInteraction.value = null
                }
            }
        }
        onDispose { }
    }

    if (enabled) {
        val focusedChildModifier =
            if (isFocused) {
                remember { FocusedBoundsModifier() }
            } else {
                // Could use Modifier.onPlaced to ensure there's a LayoutCoordinates ready to go as soon
                // as we get focus, although waiting until the next recomposition seems to be good
                // enough for now.
                Modifier
            }

        val pinnableContainer = LocalPinnableContainer.current
        var pinHandle by remember { mutableStateOf<PinnableContainer.PinnedHandle?>(null) }
        DisposableEffect(pinnableContainer) {
            if (isFocused) {
                pinHandle = pinnableContainer?.pin()
            }
            onDispose {
                pinHandle?.release()
                pinHandle = null
            }
        }

        Modifier
            .semantics {
                this.focused = isFocused
                requestFocus {
                    focusRequester.requestFocus()
                    isFocused
                }
            }
            .bringIntoViewRequester(bringIntoViewRequester)
            .focusRequester(focusRequester)
            .then(focusedChildModifier)
            .onFocusChanged {
                isFocused = it.isFocused
                if (isFocused) {
                    pinHandle = pinnableContainer?.pin()
                    scope.launch {
                        focusedInteraction.value?.let { oldValue ->
                            val interaction = FocusInteraction.Unfocus(oldValue)
                            interactionSource?.emit(interaction)
                            focusedInteraction.value = null
                        }
                        val interaction = FocusInteraction.Focus()
                        interactionSource?.emit(interaction)
                        focusedInteraction.value = interaction

                        bringIntoViewRequester.bringIntoView()
                    }
                } else {
                    pinHandle?.release()
                    pinHandle = null
                    scope.launch {
                        focusedInteraction.value?.let { oldValue ->
                            val interaction = FocusInteraction.Unfocus(oldValue)
                            interactionSource?.emit(interaction)
                            focusedInteraction.value = null
                        }
                    }
                }
            }
            .focusTarget()
    } else {
        Modifier
    }
}

/**
 * Creates a focus group or marks this component as a focus group. This means that when we move
 * focus using the keyboard or programmatically using
 * [FocusManager.moveFocus()][androidx.compose.ui.focus.FocusManager.moveFocus], the items within
 * the focus group will be given a higher priority before focus moves to items outside the focus
 * group.
 *
 * In the sample below, each column is a focus group, so pressing the tab key will move focus
 * to all the buttons in column 1 before visiting column 2.
 *
 * @sample androidx.compose.foundation.samples.FocusGroupSample
 *
 * Note: The focusable children of a focusable parent automatically form a focus group. This
 * modifier is to be used when you want to create a focus group where the parent is not focusable.
 * If you encounter a component that uses a [focusGroup] internally, you can make it focusable by
 * using a [focusable] modifier. In the second sample here, the
 * [LazyRow][androidx.compose.foundation.lazy.LazyRow] is a focus group that is not itself
 * focusable. But you can make it focusable by adding a [focusable] modifier.
 *
 * @sample androidx.compose.foundation.samples.FocusableFocusGroupSample
 */
fun Modifier.focusGroup(): Modifier {
    return this.then(focusGroupInspectorInfo)
        .focusProperties { canFocus = false }
        .focusTarget()
}

// TODO: b/202856230 - consider either making this / a similar API public, or add a parameter to
//  focusable to configure this behavior.

/**
 * [focusable] but only when not in touch mode - when [LocalInputModeManager] is
 * not [InputMode.Touch]
 */
@SuppressLint("ModifierFactoryUnreferencedReceiver")
internal fun Modifier.focusableInNonTouchMode(
    enabled: Boolean,
    interactionSource: MutableInteractionSource?,
) = composed(
    inspectorInfo =
        debugInspectorInfo {
            name = "focusableInNonTouchMode"
            properties["enabled"] = enabled
            properties["interactionSource"] = interactionSource
        },
) {
    val inputModeManager = LocalInputModeManager.current
    Modifier
        .focusProperties { canFocus = inputModeManager.inputMode != InputMode.Touch }
        .focusable(enabled, interactionSource)
}

private val focusGroupInspectorInfo =
    InspectableModifier(
        debugInspectorInfo { name = "focusGroup" },
    )
