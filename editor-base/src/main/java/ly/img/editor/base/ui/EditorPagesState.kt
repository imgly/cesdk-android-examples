package ly.img.editor.base.ui

import android.graphics.Bitmap
import androidx.annotation.StringRes
import ly.img.editor.base.R
import ly.img.editor.base.components.VectorIcon
import ly.img.editor.base.engine.isDeleteAllowed
import ly.img.editor.base.engine.isDuplicateAllowed
import ly.img.editor.core.ui.ColorSchemeKeyToken
import ly.img.editor.core.ui.engine.getSortedPages
import ly.img.editor.core.ui.iconpack.Addpage
import ly.img.editor.core.ui.iconpack.Delete
import ly.img.editor.core.ui.iconpack.Duplicate
import ly.img.editor.core.ui.iconpack.Edit
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.iconpack.Movedown
import ly.img.editor.core.ui.iconpack.Moveup
import ly.img.engine.DesignBlock
import ly.img.engine.Engine

data class EditorPagesState(
    val sessionId: Int,
    val selectedPage: Page,
    val pages: List<Page>,
    val dockOptions: List<DockOption>,
    val pageAspectRatio: Float,
) {
    val selectedPageIndex: Int by lazy {
        pages.indexOf(selectedPage)
    }

    data class Page(
        val block: DesignBlock,
        val mark: Boolean,
    ) {
        var thumbnail: Bitmap? = null
    }

    data class DockOption(
        @StringRes val titleRes: Int,
        val icon: VectorIcon,
        val enabled: Boolean,
        val actions: List<Event>,
    )
}

fun createEditorPagesState(
    sessionId: Int,
    engine: Engine,
    selectedPageIndex: Int,
): EditorPagesState {
    val pages =
        engine.getSortedPages().map { pageBlock ->
            EditorPagesState.Page(
                block = pageBlock,
                mark = true,
            )
        }
    return createEditorPagesState(sessionId, engine, pages, selectedPageIndex)
}

fun EditorPagesState.copy(
    engine: Engine,
    markThumbnails: Boolean,
    selectedPage: EditorPagesState.Page? = null,
): EditorPagesState {
    val newPages =
        engine.getSortedPages().map { pageBlock ->
            val existingPage = this.pages.firstOrNull { oldPage -> oldPage.block == pageBlock }
            EditorPagesState.Page(
                block = pageBlock,
                mark = markThumbnails,
            ).also {
                it.thumbnail = existingPage?.thumbnail
            }
        }
    // 1. Pick any new page first
    // 2. If no new page, pick same page as before
    // 3. If that page is gone, try to pick the next page
    // 4. If next page is not available, pick the previous page (guaranteed to exist)
    val newSelectedPage =
        selectedPage ?: run {
            newPages.firstOrNull { newPage ->
                this.pages.none { it.block == newPage.block }
            }
        } ?: run {
            newPages.firstOrNull { it.block == this.selectedPage.block }
        } ?: run {
            this.pages
                .getOrNull(this.selectedPageIndex + 1)
                ?.let { nextPage ->
                    newPages.firstOrNull { it.block == nextPage.block }
                }
        } ?: run {
            this.pages[this.selectedPageIndex - 1]
                .let { previousPage ->
                    newPages.first { it.block == previousPage.block }
                }
        }
    val newSelectedPageIndex = newPages.indexOfFirst { it.block == newSelectedPage.block }
    return createEditorPagesState(sessionId, engine, newPages, newSelectedPageIndex)
}

fun EditorPagesState.copy(updatedPage: EditorPagesState.Page): EditorPagesState {
    var newSelectedPage = updatedPage
    val newPages =
        pages.map {
            val newPage = if (it.block == updatedPage.block) updatedPage else it
            if (it == selectedPage) {
                newSelectedPage = newPage
            }
            newPage
        }
    return copy(selectedPage = newSelectedPage, pages = newPages)
}

private fun createEditorPagesState(
    sessionId: Int,
    engine: Engine,
    pages: List<EditorPagesState.Page>,
    selectedPageIndex: Int,
): EditorPagesState {
    val selectedPage = pages[selectedPageIndex]
    val selectedPageBlock = selectedPage.block
    val dockOptions =
        buildList {
            EditorPagesState.DockOption(
                titleRes = R.string.ly_img_editor_edit,
                icon = VectorIcon(IconPack.Edit),
                enabled = true,
                actions =
                    listOf(
                        Event.OnPage(page = selectedPageIndex),
                        Event.OnTogglePagesMode,
                    ),
            ).let(::add)
            EditorPagesState.DockOption(
                titleRes = R.string.ly_img_editor_move_up,
                icon = VectorIcon(IconPack.Moveup),
                enabled = selectedPageIndex != 0,
                actions = listOf(BlockEvent.OnBackwardNonSelected(selectedPageBlock)),
            ).let(::add)
            EditorPagesState.DockOption(
                titleRes = R.string.ly_img_editor_move_down,
                icon = VectorIcon(IconPack.Movedown),
                enabled = selectedPageIndex != pages.lastIndex,
                actions = listOf(BlockEvent.OnForwardNonSelected(selectedPageBlock)),
            ).let(::add)
            EditorPagesState.DockOption(
                titleRes = R.string.ly_img_editor_add_page,
                icon = VectorIcon(IconPack.Addpage),
                enabled = true,
                actions = listOf(Event.OnAddPage(index = selectedPageIndex + 1)),
            ).let(::add)
            if (engine.isDuplicateAllowed(selectedPageBlock)) {
                EditorPagesState.DockOption(
                    titleRes = R.string.ly_img_editor_duplicate,
                    icon = VectorIcon(IconPack.Duplicate),
                    enabled = true,
                    actions = listOf(BlockEvent.OnDuplicateNonSelected(selectedPageBlock)),
                ).let(::add)
            }
            if (pages.size > 1 && engine.isDeleteAllowed(selectedPageBlock)) {
                EditorPagesState.DockOption(
                    titleRes = R.string.ly_img_editor_delete,
                    icon =
                        VectorIcon(
                            imageVector = IconPack.Delete,
                            tint = ColorSchemeKeyToken.Error,
                        ),
                    enabled = true,
                    actions = listOf(BlockEvent.OnDeleteNonSelected(selectedPageBlock)),
                ).let(::add)
            }
        }
    val pageAspectRatio =
        pages.firstOrNull()?.let {
            engine.block.getWidth(it.block) / engine.block.getHeight(it.block)
        } ?: 1F
    return EditorPagesState(
        sessionId = sessionId,
        selectedPage = selectedPage,
        pages = pages,
        dockOptions = dockOptions,
        pageAspectRatio = pageAspectRatio,
    )
}
