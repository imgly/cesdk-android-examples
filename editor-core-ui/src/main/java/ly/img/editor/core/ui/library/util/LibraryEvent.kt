package ly.img.editor.core.ui.library.util

import android.net.Uri
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.editor.core.ui.BaseEvent
import ly.img.editor.core.ui.library.state.LibraryStackData
import ly.img.engine.Asset
import ly.img.engine.DesignBlock

internal sealed interface LibraryEvent : BaseEvent {
    data class OnEnterSearchMode(val enter: Boolean, val libraryCategory: LibraryCategory) : LibraryEvent
    data class OnSearchTextChange(val value: String, val libraryCategory: LibraryCategory, val debounce: Boolean = false) : LibraryEvent
    data object OnDispose : LibraryEvent

    data class OnPopStack(val libraryCategory: LibraryCategory) : LibraryEvent

    data class OnFetch(val libraryCategory: LibraryCategory, val flatten: Boolean = false) : LibraryEvent
    data class OnDrillDown(val libraryCategory: LibraryCategory, val libraryStackData: LibraryStackData) : LibraryEvent

    data class OnReplaceAsset(val assetSourceType: AssetSourceType, val asset: Asset, val designBlock: DesignBlock) : LibraryEvent
    data class OnReplaceUri(val assetSource: UploadAssetSourceType, val uri: Uri, val designBlock: DesignBlock) : LibraryEvent
    data class OnAddAsset(val assetSourceType: AssetSourceType, val asset: Asset) : LibraryEvent
    data class OnAddUri(val assetSource: UploadAssetSourceType, val uri: Uri) : LibraryEvent

    data class OnAssetLongClick(val assetSourceType: AssetSourceType, val asset: Asset) : LibraryEvent
}
