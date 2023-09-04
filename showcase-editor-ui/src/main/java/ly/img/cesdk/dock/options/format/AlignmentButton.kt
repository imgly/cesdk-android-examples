package ly.img.cesdk.dock.options.format

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ly.img.cesdk.components.ToggleIconButton
import ly.img.cesdk.core.iconpack.Formataligncenter
import ly.img.cesdk.core.iconpack.Formatalignleft
import ly.img.cesdk.core.iconpack.Formatalignright
import ly.img.cesdk.core.iconpack.IconPack
import ly.img.cesdk.core.iconpack.Verticalalignbottom
import ly.img.cesdk.core.iconpack.Verticalaligncenter
import ly.img.cesdk.core.iconpack.Verticalaligntop
import ly.img.cesdk.editorui.R

@Composable
fun <T : Alignment> AlignmentButton(
    alignment: Alignment,
    currentAlignment: T,
    changeAlignment: (T) -> Unit
) {
    ToggleIconButton(
        checked = currentAlignment == alignment,
        onCheckedChange = {
            @Suppress("UNCHECKED_CAST")
            changeAlignment(alignment as T)
        },
    ) {
        Icon(
            imageVector = when (alignment) {
                HorizontalAlignment.Left -> IconPack.Formatalignleft
                HorizontalAlignment.Center -> IconPack.Formataligncenter
                HorizontalAlignment.Right -> IconPack.Formatalignright
                VerticalAlignment.Bottom -> IconPack.Verticalalignbottom
                VerticalAlignment.Center -> IconPack.Verticalaligncenter
                VerticalAlignment.Top -> IconPack.Verticalaligntop
            },
            contentDescription = when (alignment) {
                HorizontalAlignment.Left -> stringResource(R.string.cesdk_align_left)
                HorizontalAlignment.Center, VerticalAlignment.Center -> stringResource(R.string.cesdk_align_center)
                HorizontalAlignment.Right -> stringResource(R.string.cesdk_align_right)
                VerticalAlignment.Top -> stringResource(R.string.cesdk_align_top)
                VerticalAlignment.Bottom -> stringResource(R.string.cesdk_align_bottom)
            }
        )
    }
}