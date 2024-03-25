/*
 * Copyright 2022 The Android Open Source Project
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
package ly.img.editor.compose.foundation.relocation

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.OnPlacedModifier
import androidx.compose.ui.modifier.ModifierLocalConsumer
import androidx.compose.ui.modifier.ModifierLocalReadScope
import androidx.compose.ui.modifier.modifierLocalOf

/**
 * The Key for the ModifierLocal that can be used to access the [BringIntoViewParent].
 */
internal val ModifierLocalBringIntoViewParent = modifierLocalOf<BringIntoViewParent?> { null }

/**
 * A node that can respond to [bringChildIntoView] requests from its children by scrolling its
 * content.
 */
internal fun interface BringIntoViewParent {
    /**
     * Scrolls this node's content so that [boundsProvider] will be in visible bounds. Must ensure that the
     * request is propagated up to the parent node.
     *
     * This method will not return until this request has been satisfied or interrupted by a
     * newer request.
     *
     * @param childCoordinates The [LayoutCoordinates] of the child node making the request. This
     * parent can use these [LayoutCoordinates] to translate [boundsProvider] into its own
     * coordinates.
     * @param boundsProvider A function returning the rectangle to bring into view, relative to
     * [childCoordinates]. The function may return a different value over time, if the bounds of the
     * request change while the request is being processed. If the rectangle cannot be calculated,
     * e.g. because [childCoordinates] is not attached, return null.
     */
    suspend fun bringChildIntoView(
        childCoordinates: LayoutCoordinates,
        boundsProvider: () -> Rect?,
    )
}

/**
 * Common modifier logic shared between both requester and responder modifiers, namely recording
 * the [LayoutCoordinates] of the modifier and providing access to the appropriate
 * [BringIntoViewParent]: either one read from the [ModifierLocalBringIntoViewParent], or if no
 * modifier local is specified then the [defaultParent].
 *
 * @param defaultParent The [BringIntoViewParent] to use if there is no
 * [ModifierLocalBringIntoViewParent] available to read. This parent should always be obtained by
 * calling [rememberDefaultBringIntoViewParent] to support platform-specific integration.
 */
internal abstract class BringIntoViewChildModifier(
    private val defaultParent: BringIntoViewParent,
) : ModifierLocalConsumer,
    OnPlacedModifier {
    private var localParent: BringIntoViewParent? = null

    /** The [LayoutCoordinates] of this modifier, if attached. */
    protected var layoutCoordinates: LayoutCoordinates? = null
        get() = field?.takeIf { it.isAttached }
        private set

    protected val parent: BringIntoViewParent
        get() = localParent ?: defaultParent

    override fun onModifierLocalsUpdated(scope: ModifierLocalReadScope) {
        with(scope) {
            localParent = ModifierLocalBringIntoViewParent.current
        }
    }

    override fun onPlaced(coordinates: LayoutCoordinates) {
        layoutCoordinates = coordinates
    }
}
