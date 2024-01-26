package ly.img.editor.core.ui.library.util

import androidx.annotation.StringRes
import ly.img.editor.core.R
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.data.AssetSourceType

@StringRes
internal fun getTitleRes(libraryCategory: LibraryCategory): Int {
    return when (libraryCategory) {
        is LibraryCategory.Elements -> R.string.cesdk_elements
        is LibraryCategory.Gallery -> R.string.cesdk_gallery
        LibraryCategory.Audio -> R.string.cesdk_audio
        LibraryCategory.Images -> R.string.cesdk_images
        LibraryCategory.Shapes -> R.string.cesdk_shapes
        LibraryCategory.Stickers -> R.string.cesdk_stickers
        LibraryCategory.Text -> R.string.cesdk_text
        LibraryCategory.Filters -> R.string.cesdk_filters
        LibraryCategory.Video -> R.string.cesdk_videos
        LibraryCategory.FxEffects -> R.string.cesdk_effects
        LibraryCategory.Blur -> R.string.cesdk_blur
    }
}

@StringRes
internal fun getTitleRes(id: String): Int {
    return when (id) {
        AssetSourceType.Shapes.sourceId -> R.string.cesdk_shapes
        AssetSourceType.Stickers.sourceId -> R.string.cesdk_stickers
        AssetSourceType.Images.sourceId -> R.string.cesdk_images
        AssetSourceType.ImageUploads.sourceId -> R.string.cesdk_image_uploads
        AssetSourceType.Unsplash.sourceId -> R.string.cesdk_unsplash
        AssetSourceType.Videos.sourceId -> R.string.cesdk_videos
        AssetSourceType.VideoUploads.sourceId -> R.string.cesdk_video_uploads
        AssetSourceType.Audio.sourceId -> R.string.cesdk_audio
        AssetSourceType.AudioUploads.sourceId -> R.string.cesdk_audio_uploads
        AssetSourceType.Text.sourceId -> R.string.cesdk_text
        "//ly.img.cesdk.stickers.doodle/category/doodle" -> R.string.cesdk_stickers_doodle
        "//ly.img.cesdk.stickers.emoji/category/emoji" -> R.string.cesdk_stickers_emoji
        "//ly.img.cesdk.stickers.emoticons/category/emoticons" -> R.string.cesdk_stickers_emoticons
        "//ly.img.cesdk.stickers.hand/category/hand" -> R.string.cesdk_stickers_hands
        "//ly.img.cesdk.vectorpaths/category/vectorpaths" -> R.string.cesdk_shapes_basic
        "//ly.img.cesdk.vectorpaths.abstract/category/abstract" -> R.string.cesdk_shapes_abstract
        else -> R.string.cesdk_elements
    }
}
