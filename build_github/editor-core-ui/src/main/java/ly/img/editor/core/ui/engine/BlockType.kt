package ly.img.editor.core.ui.engine

import androidx.annotation.StringRes
import ly.img.editor.core.R

enum class BlockType(
    @StringRes val titleRes: Int,
) {
    Image(R.string.ly_img_editor_image),
    Sticker(R.string.ly_img_editor_sticker),
    Text(R.string.ly_img_editor_text),
    Shape(R.string.ly_img_editor_shape),
    Group(R.string.ly_img_editor_group),
    Page(R.string.ly_img_editor_page),
    Video(R.string.ly_img_editor_video),
    Audio(R.string.ly_img_editor_audio),
}
