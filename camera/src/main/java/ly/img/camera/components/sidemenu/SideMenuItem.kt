package ly.img.camera.components.sidemenu

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import ly.img.camera.components.Shadowed
import ly.img.editor.core.theme.LocalExtendedColorScheme
import ly.img.editor.core.ui.utils.Easing

@Composable
internal fun SideMenuItem(
    imageVector: ImageVector,
    @StringRes contentDescription: Int,
    @StringRes label: Int,
    checked: Boolean,
    expanded: Boolean,
    onClick: () -> Unit,
) {
    // The usual Button has a min width which cannot be overriden so we need this custom button
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .minimumInteractiveComponentSize()
                .clip(CircleShape)
                .background(Color.Transparent)
                .clickable(
                    onClick = onClick,
                    role = Role.Button,
                    interactionSource = remember { MutableInteractionSource() },
                    indication =
                        rememberRipple(
                            bounded = false,
                        ),
                ),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
        ) {
            CheckedIcon(
                imageVector = imageVector,
                contentDescription = contentDescription,
                checked = checked,
            )

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(tween(durationMillis = 500, easing = Easing.EmphasizedDecelerate)),
                exit = fadeOut(tween(durationMillis = 500, easing = Easing.EmphasizedDecelerate)),
            ) {
                Shadowed {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = stringResource(id = label),
                        style = MaterialTheme.typography.labelLarge,
                        color = LocalExtendedColorScheme.current.white,
                    )
                }
            }
        }
    }
}

@Composable
private fun CheckedIcon(
    imageVector: ImageVector,
    @StringRes contentDescription: Int,
    checked: Boolean,
) {
    Shadowed(enabled = !checked) {
        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (checked) LocalExtendedColorScheme.current.white else Color.Transparent),
        ) {
            Icon(
                modifier = Modifier.align(Alignment.Center),
                imageVector = imageVector,
                contentDescription = stringResource(contentDescription),
                tint = if (checked) LocalExtendedColorScheme.current.black else LocalExtendedColorScheme.current.white,
            )
        }
    }
}
