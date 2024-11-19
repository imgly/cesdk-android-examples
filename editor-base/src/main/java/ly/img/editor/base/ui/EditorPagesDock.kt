package ly.img.editor.base.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.editor.compose.animation.AnimatedVisibility
import ly.img.editor.compose.animation_core.slideInVertically
import ly.img.editor.compose.animation_core.slideOutVertically
import ly.img.editor.compose.animation_core.tween
import ly.img.editor.core.theme.surface2
import ly.img.editor.core.ui.IconTextButton
import ly.img.editor.core.ui.utils.Easing

@Composable
fun EditorPagesDock(
    modifier: Modifier,
    state: EditorPagesState?,
    onEvent: (Event) -> Unit,
) {
    var cachedState: EditorPagesState? by remember {
        mutableStateOf(state)
    }
    LaunchedEffect(state) {
        if (state != null) {
            cachedState = state
        }
    }

    AnimatedVisibility(
        modifier = modifier,
        visible = state != null,
        enter =
            slideInVertically(
                animationSpec =
                    tween(
                        durationMillis = 400,
                        easing = Easing.EmphasizedDecelerate,
                    ),
                initialOffsetY = { it },
            ),
        exit =
            slideOutVertically(
                animationSpec =
                    tween(
                        durationMillis = 150,
                        easing = Easing.EmphasizedAccelerate,
                    ),
                targetOffsetY = { it },
            ),
    ) {
        cachedState?.let { cachedState ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface2,
            ) {
                Row(
                    modifier =
                        Modifier
                            .padding(vertical = 10.dp)
                            .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    cachedState.dockOptions.forEach {
                        IconTextButton(
                            editorIcon = it.icon,
                            text = stringResource(it.titleRes),
                            textColor = it.icon.tint?.invoke() ?: Color.Unspecified,
                            enabled = it.enabled,
                            onClick = {
                                it.actions.forEach { action -> onEvent(action) }
                            },
                        )
                    }
                }
            }
        }
    }
}
