package ly.img.editor.core.library

import ly.img.editor.core.R
import ly.img.editor.core.library.data.AssetSourceType

sealed class LibraryCategory private constructor(
    val assetSourceGroups: List<AssetSourceGroup>,
    val hiddenInAddLibrary: Boolean = false
) {
    class Elements(groups: List<AssetSourceGroup>) : LibraryCategory(groups)
    class Gallery(groups: List<AssetSourceGroup>) : LibraryCategory(groups)

    data object Video : LibraryCategory(
        listOf(
            AssetSourceGroup(
                R.string.cesdk_videos,
                listOf(AssetSourceType.VideoUploads, AssetSourceType.Videos),
                AssetSourceGroupType.Video
            ),
        )
    )

    data object Audio : LibraryCategory(
        listOf(
            AssetSourceGroup(
                R.string.cesdk_audio,
                listOf(AssetSourceType.AudioUploads, AssetSourceType.Audio),
                AssetSourceGroupType.Audio
            ),
        )
    )

    data object Images : LibraryCategory(
        listOf(
            AssetSourceGroup(
                R.string.cesdk_images,
                listOf(AssetSourceType.ImageUploads, AssetSourceType.Images, AssetSourceType.Unsplash),
                AssetSourceGroupType.Image
            )
        )
    )

    object Text : LibraryCategory(
        listOf(
            AssetSourceGroup(
                R.string.cesdk_text,
                listOf(AssetSourceType.Text),
                AssetSourceGroupType.Text
            )
        )
    )

    data object Shapes : LibraryCategory(
        listOf(
            AssetSourceGroup(
                R.string.cesdk_shapes,
                listOf(AssetSourceType.Shapes),
                AssetSourceGroupType.Shape
            )
        )
    )

    data object Stickers : LibraryCategory(
        listOf(
            AssetSourceGroup(
                R.string.cesdk_stickers,
                listOf(AssetSourceType.Stickers),
                AssetSourceGroupType.Sticker
            )
        )
    )

    object Filters : LibraryCategory(
        listOf(
            AssetSourceGroup(
                R.string.cesdk_filters,
                listOf(AssetSourceType.DuoToneFilter, AssetSourceType.LutFilter),
                AssetSourceGroupType.Filter
            )
        ), hiddenInAddLibrary = true
    )

    object FxEffects : LibraryCategory(
        listOf(
            AssetSourceGroup(
                R.string.cesdk_effects,
                listOf(AssetSourceType.FxEffect),
                AssetSourceGroupType.Effect
            )
        ), hiddenInAddLibrary = true
    )
    object Blur : LibraryCategory(
        listOf(
            AssetSourceGroup(
                R.string.cesdk_blur,
                listOf(AssetSourceType.Blur),
                AssetSourceGroupType.Blur
            )
        ), hiddenInAddLibrary = true
    )
}
