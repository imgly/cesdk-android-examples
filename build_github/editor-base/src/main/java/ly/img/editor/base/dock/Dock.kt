package ly.img.editor.base.dock

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import ly.img.editor.base.ui.Block
import ly.img.editor.compose.animation.AnimatedVisibility
import ly.img.editor.compose.animation_core.ExitTransition
import ly.img.editor.compose.animation_core.slideInHorizontally
import ly.img.editor.compose.animation_core.slideInVertically
import ly.img.editor.compose.animation_core.slideOutVertically
import ly.img.editor.compose.animation_core.tween
import ly.img.editor.core.R
import ly.img.editor.core.ui.iconpack.Close
import ly.img.editor.core.ui.iconpack.IconPack

val enterEasing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
val exitEasing = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)

@Composable
fun Dock(
    selectedBlock: Block?,
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
    onClick: (OptionType) -> Unit,
) {
    // We cache the block so we can continue to show its options during the animation
    // Otherwise the options disappear as soon as the selectedBlock becomes null
    var cachedBlock: Block? by remember {
        mutableStateOf(selectedBlock)
    }

    LaunchedEffect(selectedBlock) {
        if (selectedBlock != null) {
            cachedBlock = selectedBlock
        }
    }

    AnimatedVisibility(
        visible = selectedBlock != null,
        modifier = modifier,
        enter = slideInVertically(animationSpec = tween(400, easing = enterEasing), initialOffsetY = { it }),
        exit = slideOutVertically(animationSpec = tween(150, easing = exitEasing), targetOffsetY = { it }),
    ) {
        Surface {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 8.dp, bottom = 12.dp),
            ) {
                val gradientHeight = 64.dp
                val gradientWidth = 16.dp
                val closeButtonWidth = 48.dp

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CloseButton(
                        modifier =
                            Modifier
                                .size(closeButtonWidth, gradientHeight)
                                .padding(4.dp),
                        onClick = onClose,
                    )

                    Row(
                        modifier =
                            Modifier
                                .animateEnterExit(
                                    enter =
                                        slideInHorizontally(
                                            animationSpec = tween(400, easing = enterEasing),
                                            initialOffsetX = { it / 3 },
                                        ),
                                    exit = ExitTransition.None,
                                )
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                    ) {
                        cachedBlock?.options?.forEach {
                            DockMenuOption(
                                data = it,
                                onClick = onClick,
                            )
                        }
                    }
                }

                val gradientColor = MaterialTheme.colorScheme.surface
                val gradient =
                    remember(gradientColor) {
                        Brush.horizontalGradient(
                            listOf(
                                gradientColor,
                                gradientColor.copy(alpha = 0f),
                            ),
                        )
                    }
                Box(
                    modifier =
                        Modifier
                            .offset(x = closeButtonWidth)
                            .size(gradientWidth, gradientHeight)
                            .background(gradient),
                )
            }
        }
    }
}

// This is essentially a FilledTonalIconButton but supports elevation
@Composable
private fun CloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = IconButtonDefaults.filledShape,
) = Surface(
    onClick = onClick,
    modifier = modifier.semantics { role = Role.Button },
    shape = shape,
    color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    shadowElevation = 3.dp,
) {
    Box(
        modifier = Modifier.size(40.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(IconPack.Close, contentDescription = stringResource(id = R.string.ly_img_editor_close))
    }
}
