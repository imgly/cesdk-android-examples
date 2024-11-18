package ly.img.editor.video

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ly.img.editor.base.engine.TOUCH_ACTION_SCALE
import ly.img.editor.base.engine.showPage
import ly.img.editor.base.ui.Block
import ly.img.editor.base.ui.EditorUiViewModel
import ly.img.editor.core.EditorScope
import ly.img.editor.core.ui.engine.BlockType
import ly.img.editor.core.ui.engine.getCurrentPage
import ly.img.editor.core.ui.library.LibraryViewModel

class VideoUiViewModel(
    editorScope: EditorScope,
    onCreate: suspend EditorScope.() -> Unit,
    onExport: suspend EditorScope.() -> Unit,
    onClose: suspend EditorScope.(Boolean) -> Unit,
    onError: suspend EditorScope.(Throwable) -> Unit,
    libraryViewModel: LibraryViewModel,
) : EditorUiViewModel(
        editorScope = editorScope,
        onCreate = onCreate,
        onExport = onExport,
        onClose = onClose,
        onError = onError,
        libraryViewModel = libraryViewModel,
    ) {
    val uiState =
        baseUiState.map {
            VideoUiViewState(
                editorUiViewState = baseUiState.value,
                canExport = timelineState?.totalDuration?.isPositive() == true,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = VideoUiViewState(baseUiState.value),
        )

    override val verticalPageInset = 1F

    override fun getBlockForEvents(): Block {
        return super.getBlockForEvents() ?: Block(
            designBlock = engine.getCurrentPage(),
            type = BlockType.Page,
        )
    }

    override fun onPreCreate() {
        super.onPreCreate()
        with(engine.editor) {
            setSettingEnum("touch/pinchAction", TOUCH_ACTION_SCALE)
            setSettingBoolean("controlGizmo/showRotateHandles", false)
            setSettingBoolean("controlGizmo/showScaleHandles", false)
            setSettingBoolean("controlGizmo/showMoveHandles", false)
            setSettingBoolean("touch/singlePointPanning", false)
            setSettingColor("page/innerBorderColor", ly.img.engine.Color.fromRGBA(0.67f, 0.67f, 0.67f, 0.5f))
        }
    }

    override fun enterEditMode() {
        engine.showPage(pageIndex.value)
    }

    override fun enterPreviewMode() = throw UnsupportedOperationException()
}
