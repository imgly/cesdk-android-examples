package ly.img.cesdk.core.library.components

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import ly.img.cesdk.core.R
import ly.img.cesdk.core.iconpack.IconPack
import ly.img.cesdk.core.iconpack.Image
import ly.img.cesdk.core.iconpack.Imageoutline
import ly.img.cesdk.core.iconpack.Libraryelements
import ly.img.cesdk.core.iconpack.Libraryelementsoutline
import ly.img.cesdk.core.iconpack.Music
import ly.img.cesdk.core.iconpack.Photocamera
import ly.img.cesdk.core.iconpack.Photocameraoutline
import ly.img.cesdk.core.iconpack.Playbox
import ly.img.cesdk.core.iconpack.Playboxoutline
import ly.img.cesdk.core.iconpack.Shapes
import ly.img.cesdk.core.iconpack.Shapesoutline
import ly.img.cesdk.core.iconpack.Stickeremoji
import ly.img.cesdk.core.iconpack.Stickeremojioutline
import ly.img.cesdk.core.iconpack.Textfields
import ly.img.cesdk.core.library.state.LibraryCategory

internal sealed class LibraryNavBarItem(
    val libraryCategory: LibraryCategory,
    @StringRes val titleRes: Int,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
) {
    class Elements(libraryCategory: LibraryCategory) : LibraryNavBarItem(
        libraryCategory,
        R.string.cesdk_elements,
        IconPack.Libraryelementsoutline,
        IconPack.Libraryelements
    )

    object Video : LibraryNavBarItem(LibraryCategory.Video, R.string.cesdk_videos, IconPack.Playboxoutline, IconPack.Playbox)
    object Audio : LibraryNavBarItem(LibraryCategory.Audio, R.string.cesdk_audio, IconPack.Music, IconPack.Music)
    object Text : LibraryNavBarItem(LibraryCategory.Text, R.string.cesdk_text, IconPack.Textfields, IconPack.Textfields)
    object Images : LibraryNavBarItem(LibraryCategory.Images, R.string.cesdk_images, IconPack.Imageoutline, IconPack.Image)
    object Shapes : LibraryNavBarItem(LibraryCategory.Shapes, R.string.cesdk_shapes, IconPack.Shapesoutline, IconPack.Shapes)

    object Stickers :
        LibraryNavBarItem(LibraryCategory.Stickers, R.string.cesdk_stickers, IconPack.Stickeremojioutline, IconPack.Stickeremoji)

    class Gallery(libraryCategory: LibraryCategory) :
        LibraryNavBarItem(libraryCategory, R.string.cesdk_gallery, IconPack.Photocameraoutline, IconPack.Photocamera)

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
        }
    }
}