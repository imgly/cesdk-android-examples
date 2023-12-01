package ly.img.cesdk.core.library.state

import ly.img.cesdk.core.R
import ly.img.cesdk.core.data.AssetSource

sealed class LibraryCategory private constructor(internal val assetSourceGroups: List<AssetSourceGroup>, internal val hiddenInAddLibrary: Boolean = false) {
    internal class Elements(groups: List<AssetSourceGroup>) : LibraryCategory(groups)
    internal class Gallery(groups: List<AssetSourceGroup>) : LibraryCategory(groups)

    internal object Video : LibraryCategory(
        listOf(
            AssetSourceGroup(
                R.string.cesdk_videos,
                listOf(AssetSource.VideoUploads, AssetSource.Videos),
                AssetSourceGroupType.Video
            ),
        )
    )

    internal object Audio : LibraryCategory(
        listOf(
            AssetSourceGroup(
                R.string.cesdk_audio,
                listOf(AssetSource.AudioUploads, AssetSource.Audio),
                AssetSourceGroupType.Audio
            ),
        )
    )

    internal object Images : LibraryCategory(
        listOf(
            AssetSourceGroup(
                R.string.cesdk_images,
                listOf(AssetSource.ImageUploads, AssetSource.Images, AssetSource.Unsplash),
                AssetSourceGroupType.Image
            )
        )
    )

    internal object Text : LibraryCategory(
        listOf(
            AssetSourceGroup(
                R.string.cesdk_text,
                listOf(AssetSource.Text),
                AssetSourceGroupType.Text
            )
        )
    )

    internal object Shapes : LibraryCategory(
        listOf(
            AssetSourceGroup(
                R.string.cesdk_shapes,
                listOf(AssetSource.Shapes),
                AssetSourceGroupType.Shape
            )
        )
    )

    internal object Stickers : LibraryCategory(
        listOf(
            AssetSourceGroup(
                R.string.cesdk_stickers,
                listOf(AssetSource.Stickers),
                AssetSourceGroupType.Sticker
            )
        )
    )

    object Filters : LibraryCategory(
        listOf(
            AssetSourceGroup(
                R.string.cesdk_filters,
                listOf(AssetSource.DuotoneFilter, AssetSource.LutFilter),
                AssetSourceGroupType.Filter
            )
        ), hiddenInAddLibrary = true
    )

    object FxEffects : LibraryCategory(
        listOf(
            AssetSourceGroup(
                R.string.cesdk_effects,
                listOf(AssetSource.FxEffect),
                AssetSourceGroupType.Effect
            )
        ), hiddenInAddLibrary = true
    )
    object Blur : LibraryCategory(
        listOf(
            AssetSourceGroup(
                R.string.cesdk_blur,
                listOf(AssetSource.Blur),
                AssetSourceGroupType.Blur
            )
        ), hiddenInAddLibrary = true
    )
}