package ly.img.editor.base.dock

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import ly.img.editor.core.ui.IconTextButton

@Composable
fun DockMenuOption(
    data: OptionItemData,
    onClick: (OptionType) -> Unit,
) {
    IconTextButton(
        onClick = { onClick(data.optionType) },
        text = stringResource(data.labelStringRes),
        editorIcon = data.icon,
        textColor = data.textColor?.invoke() ?: Color.Unspecified,
    )
}
