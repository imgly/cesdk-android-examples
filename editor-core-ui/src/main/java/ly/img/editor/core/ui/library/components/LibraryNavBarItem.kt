package ly.img.editor.core.ui.library.components

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import ly.img.editor.core.R
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.iconpack.Image
import ly.img.editor.core.ui.iconpack.Imageoutline
import ly.img.editor.core.ui.iconpack.Libraryelements
import ly.img.editor.core.ui.iconpack.Libraryelementsoutline
import ly.img.editor.core.ui.iconpack.Music
import ly.img.editor.core.ui.iconpack.Photocamera
import ly.img.editor.core.ui.iconpack.Photocameraoutline
import ly.img.editor.core.ui.iconpack.Playbox
import ly.img.editor.core.ui.iconpack.Playboxoutline
import ly.img.editor.core.ui.iconpack.Shapes
import ly.img.editor.core.ui.iconpack.Shapesoutline
import ly.img.editor.core.ui.iconpack.Stickeremoji
import ly.img.editor.core.ui.iconpack.Stickeremojioutline
import ly.img.editor.core.ui.iconpack.Textfields

internal sealed class LibraryNavBarItem(
    val libraryCategory: LibraryCategory,
    @StringRes val titleRes: Int,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
) {
    class Elements(libraryCategory: LibraryCategory) : LibraryNavBarItem(
        libraryCategory = libraryCategory,
        titleRes = R.string.cesdk_elements,
        unselectedIcon = IconPack.Libraryelementsoutline,
        selectedIcon = IconPack.Libraryelements
    )

    data object Video : LibraryNavBarItem(
        libraryCategory = LibraryCategory.Video,
        titleRes = R.string.cesdk_videos,
        unselectedIcon = IconPack.Playboxoutline,
        selectedIcon = IconPack.Playbox
    )
    data object Audio : LibraryNavBarItem(
        libraryCategory = LibraryCategory.Audio,
        titleRes = R.string.cesdk_audio,
        unselectedIcon = IconPack.Music,
        selectedIcon = IconPack.Music
    )
    data object Text : LibraryNavBarItem(
        libraryCategory = LibraryCategory.Text,
        titleRes = R.string.cesdk_text,
        unselectedIcon = IconPack.Textfields,
        selectedIcon = IconPack.Textfields
    )
    data object Images : LibraryNavBarItem(
        libraryCategory = LibraryCategory.Images,
        titleRes = R.string.cesdk_images,
        unselectedIcon = IconPack.Imageoutline,
        selectedIcon = IconPack.Image
    )
    data object Shapes : LibraryNavBarItem(
        libraryCategory = LibraryCategory.Shapes,
        titleRes = R.string.cesdk_shapes,
        unselectedIcon = IconPack.Shapesoutline,
        selectedIcon = IconPack.Shapes
    )

    data object Stickers : LibraryNavBarItem(
        libraryCategory = LibraryCategory.Stickers,
        titleRes = R.string.cesdk_stickers,
        unselectedIcon = IconPack.Stickeremojioutline,
        selectedIcon = IconPack.Stickeremoji
    )

    class Gallery(libraryCategory: LibraryCategory) : LibraryNavBarItem(
        libraryCategory = libraryCategory,
        titleRes = R.string.cesdk_gallery,
        unselectedIcon = IconPack.Photocameraoutline,
        selectedIcon = IconPack.Photocamera
    )

    companion object {
        fun from(libraryCategory: LibraryCategory) = when (libraryCategory) {
            LibraryCategory.Audio -> Audio
            is LibraryCategory.Elements -> Elements(libraryCategory)
            LibraryCategory.Images -> Images
            LibraryCategory.Shapes -> Shapes
            LibraryCategory.Stickers -> Stickers
            LibraryCategory.Text -> Text
            is LibraryCategory.Gallery -> Gallery(libraryCategory)
            LibraryCategory.Video -> Video

            LibraryCategory.Filters, LibraryCategory.FxEffects, LibraryCategory.Blur ->
                throw IllegalArgumentException("Unsupported LibraryNavBarItem category: $libraryCategory")
        }
    }
}
