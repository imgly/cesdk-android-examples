/*
 * Copyright 2021 The Android Open Source Project
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

import android.view.KeyEvent.KEYCODE_DPAD_CENTER
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.KeyEvent.KEYCODE_NUMPAD_ENTER
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType.Companion.KeyDown
import androidx.compose.ui.input.key.KeyEventType.Companion.KeyUp
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalView
import ly.img.editor.compose.foundation.gestures.ModifierLocalScrollableContainer
import ly.img.editor.compose.internal.OriginallyExpect

/**
 * Returns a lambda that calculates whether the root Compose layout node is hosted in a scrollable
 * container outside of Compose. On Android this will be whether the root View is in a scrollable
 * ViewGroup, as even if nothing in the Compose part of the hierarchy is scrollable, if the View
 * itself is in a scrollable container, we still want to delay presses in case presses in Compose
 * convert to a scroll outside of Compose.
 *
 * Combine this with [ModifierLocalScrollableContainer], which returns whether a [Modifier] is
 * within a scrollable Compose layout, to calculate whether this modifier is within some form of
 * scrollable container, and hence should delay presses.
 */
@Composable
@OriginallyExpect
internal fun isComposeRootInScrollableContainer(): () -> Boolean {
    val view = LocalView.current
    return {
        view.isInScrollableViewGroup()
    }
}

// Copied from View#isInScrollingContainer() which is @hide
private fun View.isInScrollableViewGroup(): Boolean {
    var p = parent
    while (p != null && p is ViewGroup) {
        if (p.shouldDelayChildPressedState()) {
            return true
        }
        p = p.parent
    }
    return false
}

/**
 * How long to wait before appearing 'pressed' (emitting [PressInteraction.Press]) - if a touch
 * down will quickly become a drag / scroll, this timeout means that we don't show a press effect.
 */
@OriginallyExpect
internal val TapIndicationDelay: Long = ViewConfiguration.getTapTimeout().toLong()

/**
 * Whether the specified [KeyEvent] should trigger a press for a clickable component, i.e. whether
 * it is associated with a press of an enter key or dpad centre.
 */
@OriginallyExpect
internal val KeyEvent.isPress: Boolean
    get() = type == KeyDown && isEnter

/**
 * Whether the specified [KeyEvent] should trigger a click for a clickable component.
 */
@OriginallyExpect
internal val KeyEvent.isClick: Boolean
    get() = type == KeyUp && isEnter

private val KeyEvent.isEnter: Boolean
    get() =
        when (key.nativeKeyCode) {
            KEYCODE_DPAD_CENTER, KEYCODE_ENTER, KEYCODE_NUMPAD_ENTER -> true
            else -> false
        }
