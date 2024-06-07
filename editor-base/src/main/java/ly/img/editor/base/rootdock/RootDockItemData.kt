package ly.img.editor.base.rootdock

import androidx.annotation.StringRes
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.ui.tab_item.TabIcon

data class RootDockItemData(
    val type: RootDockItemActionType,
    @StringRes val labelStringRes: Int,
    val icon: TabIcon,
)

sealed interface RootDockItemActionType {
    data object OpenGallery : RootDockItemActionType

    data object OpenCamera : RootDockItemActionType

    data class OpenLibraryCategory(val libraryCategory: LibraryCategory) : RootDockItemActionType
}
