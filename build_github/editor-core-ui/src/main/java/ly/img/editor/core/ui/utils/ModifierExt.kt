package ly.img.editor.core.ui.utils

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.InspectorValueInfo
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset

inline fun Modifier.ifTrue(
    predicate: Boolean,
    builder: Modifier.() -> Modifier,
) = if (predicate) this.builder() else this

fun Modifier.animateItemPlacement(
    animationSpec: FiniteAnimationSpec<IntOffset> =
        spring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntOffset.VisibilityThreshold,
        ),
) = this.then(
    AnimateItemPlacementModifier(
        animationSpec,
        debugInspectorInfo {
            name = "animateItemPlacement"
            value = animationSpec
        },
    ),
)

private class AnimateItemPlacementModifier(
    val animationSpec: FiniteAnimationSpec<IntOffset>,
    inspectorInfo: InspectorInfo.() -> Unit,
) : ParentDataModifier, InspectorValueInfo(inspectorInfo) {
    override fun Density.modifyParentData(parentData: Any?): Any = animationSpec

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AnimateItemPlacementModifier) return false
        return animationSpec != other.animationSpec
    }

    override fun hashCode(): Int {
        return animationSpec.hashCode()
    }
}
