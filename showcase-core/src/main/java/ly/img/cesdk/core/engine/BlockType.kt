package ly.img.cesdk.core.engine

import androidx.annotation.StringRes
import ly.img.cesdk.core.R

enum class BlockType(@StringRes val titleRes: Int) {
    Image(R.string.cesdk_image),
    Sticker(R.string.cesdk_sticker),
    Text(R.string.cesdk_text),
    Shape(R.string.cesdk_shape),
    Group(R.string.cesdk_group),
    Page(R.string.cesdk_page)
}