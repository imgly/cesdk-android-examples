package ly.img.editor.core.ui.library.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import ly.img.editor.core.library.AssetType

internal object AssetLibraryUiConfig {
    fun contentRowHeight(assetType: AssetType) =
        when (assetType) {
            AssetType.Image, AssetType.Video, AssetType.Gallery, AssetType.Filter,
            AssetType.Effect, AssetType.Blur,
            -> 96.dp
            AssetType.Audio, AssetType.Text -> 216.dp
            else -> 80.dp
        }

    fun contentRowWidth(assetType: AssetType) =
        when (assetType) {
            AssetType.Image, AssetType.Video, AssetType.Gallery, AssetType.Filter,
            AssetType.Effect, AssetType.Blur,
            -> 96.dp
            AssetType.Audio, AssetType.Text -> 216.dp
            else -> 80.dp
        }

    fun shouldTintImages(assetType: AssetType) = assetType == AssetType.Shape

    fun contentPadding(assetType: AssetType) =
        when (assetType) {
            AssetType.Image, AssetType.Video, AssetType.Gallery, AssetType.Filter,
            AssetType.Effect, AssetType.Blur,
            -> 0.dp
            else -> 4.dp
        }

    fun contentScale(assetType: AssetType) =
        when (assetType) {
            AssetType.Image, AssetType.Video,
            AssetType.Gallery, AssetType.Filter,
            AssetType.Effect, AssetType.Blur,
            -> ContentScale.Crop
            else -> ContentScale.Fit
        }

    fun assetGridColumns(assetType: AssetType) =
        when (assetType) {
            AssetType.Image, AssetType.Video, AssetType.Gallery, AssetType.Filter,
            AssetType.Effect, AssetType.Blur,
            -> 3
            AssetType.Audio, AssetType.Text, AssetType.Typeface -> 1
            AssetType.Shape, AssetType.Sticker -> 4
        }

    fun assetGridVerticalArrangement(assetType: AssetType) =
        when (assetType) {
            AssetType.Audio, AssetType.Text -> Arrangement.Top
            else -> Arrangement.spacedBy(4.dp)
        }

    fun assetGridHorizontalArrangement(assetType: AssetType) =
        when (assetType) {
            AssetType.Audio, AssetType.Text -> Arrangement.Start
            else -> Arrangement.spacedBy(4.dp)
        }
}
