package ly.img.editor.core.ui.library.util

import android.net.Uri
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.LibraryContent
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.editor.core.ui.BaseEvent
import ly.img.editor.core.ui.library.state.WrappedAsset
import ly.img.engine.DesignBlock

sealed interface LibraryEvent : BaseEvent {
    data class OnEnterSearchMode(val enter: Boolean, val libraryCategory: LibraryCategory) : LibraryEvent

    data class OnSearchTextChange(val value: String, val libraryCategory: LibraryCategory, val debounce: Boolean = false) : LibraryEvent

    data object OnDispose : LibraryEvent

    data class OnPopStack(val libraryCategory: LibraryCategory) : LibraryEvent

    data class OnDrillDown(val libraryCategory: LibraryCategory, val expandContent: LibraryContent) : LibraryEvent

    data class OnFetch(val libraryCategory: LibraryCategory) : LibraryEvent

    data class OnAddAsset(val wrappedAsset: WrappedAsset) : LibraryEvent

    data class OnReplaceAsset(val wrappedAsset: WrappedAsset, val designBlock: DesignBlock) : LibraryEvent

    data class OnReplaceUri(val assetSource: UploadAssetSourceType, val uri: Uri, val designBlock: DesignBlock) : LibraryEvent

    data class OnAddUri(val assetSource: UploadAssetSourceType, val uri: Uri) : LibraryEvent

    data class OnAssetLongClick(val wrappedAsset: WrappedAsset) : LibraryEvent
}
