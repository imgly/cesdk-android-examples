package ly.img.cesdk.core.library.util

import android.net.Uri
import ly.img.cesdk.core.data.AssetSource
import ly.img.cesdk.core.data.UploadAssetSource
import ly.img.cesdk.core.library.state.LibraryCategory
import ly.img.cesdk.core.library.state.LibraryStackData
import ly.img.engine.Asset
import ly.img.engine.DesignBlock

internal sealed interface LibraryEvent {
    data class OnEnterSearchMode(val enter: Boolean, val libraryCategory: LibraryCategory) : LibraryEvent
    data class OnSearchTextChange(val value: String, val libraryCategory: LibraryCategory, val debounce: Boolean = false) : LibraryEvent
    data class OnDispose(val libraryCategory: LibraryCategory) : LibraryEvent

    data class OnPopStack(val libraryCategory: LibraryCategory) : LibraryEvent

    data class OnFetch(val libraryCategory: LibraryCategory) : LibraryEvent
    data class OnDrillDown(val libraryCategory: LibraryCategory, val libraryStackData: LibraryStackData) : LibraryEvent

    data class OnReplaceAsset(val assetSource: AssetSource, val asset: Asset, val designBlock: DesignBlock) : LibraryEvent
    data class OnReplaceUri(val assetSource: UploadAssetSource, val uri: Uri, val designBlock: DesignBlock) : LibraryEvent
    data class OnAddAsset(val assetSource: AssetSource, val asset: Asset) : LibraryEvent
    data class OnAddUri(val assetSource: UploadAssetSource, val uri: Uri) : LibraryEvent

    data class OnAssetLongClick(val assetSource: AssetSource, val asset: Asset) : LibraryEvent
}