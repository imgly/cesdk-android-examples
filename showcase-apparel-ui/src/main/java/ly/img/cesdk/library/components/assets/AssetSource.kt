package ly.img.cesdk.library.components.assets

import androidx.annotation.StringRes
import ly.img.cesdk.apparelui.R

sealed class AssetSource(val sourceId: String) {
    object Shapes : AssetSource("ly.img.vectorpath.showcase")
    object Stickers : AssetSource("ly.img.sticker.showcase")
}

sealed class ImageSource(sourceId: String) : AssetSource(sourceId) {
    object Local : ImageSource("ly.img.image.showcase")
    object Uploads : ImageSource("ly.img.image.upload")
    object Unsplash : ImageSource("ly.img.asset.source.unsplash")
}

@StringRes
fun AssetSource.getSearchTextRes(): Int {
    return when (this) {
        is ImageSource -> R.string.cesdk_search_images
        AssetSource.Shapes -> R.string.cesdk_search_shapes
        AssetSource.Stickers -> R.string.cesdk_search_stickers
    }
}