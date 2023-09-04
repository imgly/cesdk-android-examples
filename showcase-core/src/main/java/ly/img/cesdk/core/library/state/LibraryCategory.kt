package ly.img.cesdk.core.library.state

import ly.img.cesdk.core.R
import ly.img.cesdk.core.data.AssetSource

internal sealed class LibraryCategory(val assetSourceGroups: List<AssetSourceGroup>) {
    class Elements(groups: List<AssetSourceGroup>) : LibraryCategory(groups)
    class Gallery(groups: List<AssetSourceGroup>) : LibraryCategory(groups)

    object Video : LibraryCategory(
        listOf(
            AssetSourceGroup(
                R.string.cesdk_videos,
                listOf(AssetSource.VideoUploads, AssetSource.Videos),
                AssetSourceGroupType.Video
            ),
        )
    )

    object Audio : LibraryCategory(
        listOf(
            AssetSourceGroup(
                R.string.cesdk_audio,
                listOf(AssetSource.AudioUploads, AssetSource.Audio),
                AssetSourceGroupType.Audio
            ),
        )
    )

    object Images : LibraryCategory(
        listOf(
            AssetSourceGroup(
                R.string.cesdk_images,
                listOf(AssetSource.ImageUploads, AssetSource.Images, AssetSource.Unsplash),
                AssetSourceGroupType.Image
            )
        )
    )

    object Text : LibraryCategory(
        listOf(
            AssetSourceGroup(
                R.string.cesdk_text,
                listOf(AssetSource.Text),
                AssetSourceGroupType.Text
            )
        )
    )

    object Shapes : LibraryCategory(
        listOf(
            AssetSourceGroup(
                R.string.cesdk_shapes,
                listOf(AssetSource.Shapes),
                AssetSourceGroupType.Shape
            )
        )
    )

    object Stickers : LibraryCategory(
        listOf(
            AssetSourceGroup(
                R.string.cesdk_stickers,
                listOf(AssetSource.Stickers),
                AssetSourceGroupType.Sticker
            )
        )
    )
}