package ly.img.cesdk.core.library.util

import androidx.annotation.StringRes
import ly.img.cesdk.core.R
import ly.img.cesdk.core.data.AssetSource
import ly.img.cesdk.core.library.state.LibraryCategory

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
        AssetSource.Shapes.sourceId -> R.string.cesdk_shapes
        AssetSource.Stickers.sourceId -> R.string.cesdk_stickers
        AssetSource.Images.sourceId -> R.string.cesdk_images
        AssetSource.ImageUploads.sourceId -> R.string.cesdk_image_uploads
        AssetSource.Unsplash.sourceId -> R.string.cesdk_unsplash
        AssetSource.Videos.sourceId -> R.string.cesdk_videos
        AssetSource.VideoUploads.sourceId -> R.string.cesdk_video_uploads
        AssetSource.Audio.sourceId -> R.string.cesdk_audio
        AssetSource.AudioUploads.sourceId -> R.string.cesdk_audio_uploads
        AssetSource.Text.sourceId -> R.string.cesdk_text
        "//ly.img.cesdk.stickers.doodle/category/doodle" -> R.string.cesdk_stickers_doodle
        "//ly.img.cesdk.stickers.emoji/category/emoji" -> R.string.cesdk_stickers_emoji
        "//ly.img.cesdk.stickers.emoticons/category/emoticons" -> R.string.cesdk_stickers_emoticons
        "//ly.img.cesdk.stickers.hand/category/hand" -> R.string.cesdk_stickers_hands
        "//ly.img.cesdk.vectorpaths/category/vectorpaths" -> R.string.cesdk_shapes_basic
        "//ly.img.cesdk.vectorpaths.abstract/category/abstract" -> R.string.cesdk_shapes_abstract
        else -> R.string.cesdk_elements
    }
}