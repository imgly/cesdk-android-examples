package ly.img.editor.base.components

import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ly.img.editor.base.R
import ly.img.editor.core.ui.SheetHeader
import ly.img.editor.core.ui.UiDefaults
import ly.img.editor.core.ui.iconpack.Check
import ly.img.editor.core.ui.iconpack.IconPack

@Composable
fun EditingTextCard(
    modifier: Modifier,
    onClose: () -> Unit,
) {
    Surface(
        modifier = modifier.imePadding(),
        shape = UiDefaults.CornerLargeTop,
    ) {
        SheetHeader(
            title = stringResource(R.string.ly_img_editor_editing_text),
            icon = IconPack.Check,
            onClose = onClose,
        )
    }
}
