package ly.img.cesdk.core.library.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import ly.img.cesdk.core.library.state.AssetSourceGroupType

internal object AssetLibraryUiConfig {

    fun contentRowHeight(assetSourceGroupType: AssetSourceGroupType) = when (assetSourceGroupType) {
        AssetSourceGroupType.Image, AssetSourceGroupType.Video, AssetSourceGroupType.Gallery, AssetSourceGroupType.Filter,
        AssetSourceGroupType.Effect, AssetSourceGroupType.Blur-> 96.dp
        AssetSourceGroupType.Audio, AssetSourceGroupType.Text -> 216.dp
        else -> 80.dp
    }

    fun contentRowWidth(assetSourceGroupType: AssetSourceGroupType) = when (assetSourceGroupType) {
        AssetSourceGroupType.Image, AssetSourceGroupType.Video, AssetSourceGroupType.Gallery, AssetSourceGroupType.Filter,
        AssetSourceGroupType.Effect, AssetSourceGroupType.Blur -> 96.dp
        AssetSourceGroupType.Audio, AssetSourceGroupType.Text -> 216.dp
        else -> 80.dp
    }

    fun shouldTintImages(assetSourceGroupType: AssetSourceGroupType) = assetSourceGroupType == AssetSourceGroupType.Shape

    fun contentPadding(assetSourceGroupType: AssetSourceGroupType) = when (assetSourceGroupType) {
        AssetSourceGroupType.Image, AssetSourceGroupType.Video, AssetSourceGroupType.Gallery, AssetSourceGroupType.Filter,
        AssetSourceGroupType.Effect, AssetSourceGroupType.Blur -> 0.dp
        else -> 4.dp
    }

    fun contentScale(assetSourceGroupType: AssetSourceGroupType) = when (assetSourceGroupType) {
        AssetSourceGroupType.Image, AssetSourceGroupType.Video,
        AssetSourceGroupType.Gallery, AssetSourceGroupType.Filter,
        AssetSourceGroupType.Effect, AssetSourceGroupType.Blur -> ContentScale.Crop
        else -> ContentScale.Fit
    }

    fun assetGridColumns(assetSourceGroupType: AssetSourceGroupType) = when (assetSourceGroupType) {
        AssetSourceGroupType.Image, AssetSourceGroupType.Video, AssetSourceGroupType.Gallery, AssetSourceGroupType.Filter,
        AssetSourceGroupType.Effect, AssetSourceGroupType.Blur -> 3
        AssetSourceGroupType.Audio, AssetSourceGroupType.Text -> 1
        AssetSourceGroupType.Shape, AssetSourceGroupType.Sticker -> 4
    }

    fun assetGridVerticalArrangement(assetSourceGroupType: AssetSourceGroupType) = when (assetSourceGroupType) {
        AssetSourceGroupType.Audio, AssetSourceGroupType.Text -> Arrangement.Top
        else -> Arrangement.spacedBy(4.dp)
    }

    fun assetGridHorizontalArrangement(assetSourceGroupType: AssetSourceGroupType) = when (assetSourceGroupType) {
        AssetSourceGroupType.Audio, AssetSourceGroupType.Text -> Arrangement.Start
        else -> Arrangement.spacedBy(4.dp)
    }
}