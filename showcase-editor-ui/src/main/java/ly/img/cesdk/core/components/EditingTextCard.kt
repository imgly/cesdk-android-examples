package ly.img.cesdk.core.components

import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ly.img.cesdk.core.UiDefaults
import ly.img.cesdk.core.iconpack.Check
import ly.img.cesdk.core.iconpack.IconPack
import ly.img.cesdk.editorui.R

@Composable
fun EditingTextCard(
    modifier: Modifier,
    onClose: () -> Unit
) {
    Surface(
        modifier = modifier.imePadding(),
        shape = UiDefaults.CornerLargeTop
    ) {
        SheetHeader(
            title = stringResource(R.string.cesdk_editing_text),
            icon = IconPack.Check,
            onClose = onClose
        )
    }
}
