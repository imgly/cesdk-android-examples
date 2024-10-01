package ly.img.editor.video

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ly.img.editor.base.components.VectorIcon
import ly.img.editor.base.engine.showPage
import ly.img.editor.base.rootdock.RootDockItemActionType
import ly.img.editor.base.rootdock.RootDockItemData
import ly.img.editor.base.ui.Block
import ly.img.editor.base.ui.EditorUiViewModel
import ly.img.editor.base.ui.Event
import ly.img.editor.core.R
import ly.img.editor.core.event.EditorEventHandler
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.ui.engine.BlockType
import ly.img.editor.core.ui.engine.getCurrentPage
import ly.img.editor.core.ui.iconpack.Addaudio
import ly.img.editor.core.ui.iconpack.Addcamerabackground
import ly.img.editor.core.ui.iconpack.Addgallerybackground
import ly.img.editor.core.ui.iconpack.Addoverlay
import ly.img.editor.core.ui.iconpack.Addsticker
import ly.img.editor.core.ui.iconpack.Addtext
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.iconpack.Reorderhorizontally
import ly.img.editor.core.ui.library.engine.getBackgroundTrack
import ly.img.engine.Engine
import ly.img.engine.SceneMode

class VideoUiViewModel(
    baseUri: Uri,
    onCreate: suspend (Engine, EditorEventHandler) -> Unit,
    onExport: suspend (Engine, EditorEventHandler) -> Unit,
    onClose: suspend (Engine, Boolean, EditorEventHandler) -> Unit,
    onError: suspend (Throwable, Engine, EditorEventHandler) -> Unit,
    colorPalette: List<Color>,
) : EditorUiViewModel(
        baseUri = baseUri,
        onCreate = onCreate,
        onExport = onExport,
        onClose = onClose,
        onError = onError,
        colorPalette = colorPalette,
    ) {
    val uiState =
        _uiState.map {
            VideoUiViewState(
                editorUiViewState = _uiState.value,
                canExport = timelineState?.totalDuration?.isPositive() == true,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = VideoUiViewState(_uiState.value),
        )

    override val verticalPageInset = 1F

    override fun getRootDockItems(assetLibrary: AssetLibrary): List<RootDockItemData> {
        fun getType(libraryCategory: LibraryCategory): RootDockItemActionType {
            return RootDockItemActionType.OnEvent(
                Event.OnAddLibraryCategoryClick(
                    libraryCategory = libraryCategory,
                    addToBackgroundTrack = false,
                ),
            )
        }
        val items =
            mutableListOf(
                RootDockItemData(
                    type = RootDockItemActionType.OpenGallery,
                    labelStringRes = R.string.ly_img_editor_gallery,
                    icon = VectorIcon(IconPack.Addgallerybackground),
                ),
                RootDockItemData(
                    type = RootDockItemActionType.OpenCamera,
                    labelStringRes = R.string.ly_img_editor_camera,
                    icon = VectorIcon(IconPack.Addcamerabackground),
                ),
                RootDockItemData(
                    type = getType(assetLibrary.overlays),
                    labelStringRes = R.string.ly_img_editor_overlay,
                    icon = VectorIcon(IconPack.Addoverlay),
                ),
                RootDockItemData(
                    type = getType(assetLibrary.text(SceneMode.VIDEO)),
                    labelStringRes = R.string.ly_img_editor_text,
                    icon = VectorIcon(IconPack.Addtext),
                ),
                RootDockItemData(
                    type = getType(assetLibrary.stickers(SceneMode.VIDEO)),
                    labelStringRes = R.string.ly_img_editor_sticker,
                    icon = VectorIcon(IconPack.Addsticker),
                ),
                RootDockItemData(
                    type = getType(assetLibrary.audios(SceneMode.VIDEO)),
                    labelStringRes = R.string.ly_img_editor_audio,
                    icon = VectorIcon(IconPack.Addaudio),
                ),
            )

        if (!engine.isEngineRunning()) {
            return items
        }

        // Background track may or may not be there at this point
        engine.block.getBackgroundTrack()?.let { backgroundTrack ->
            // only show reorder if there are at-least 2 items to reorder
            if (engine.block.getChildren(backgroundTrack).size >= 2) {
                items.add(
                    RootDockItemData(
                        type = RootDockItemActionType.OnEvent(Event.OnReorder),
                        labelStringRes = ly.img.editor.base.R.string.ly_img_editor_reorder,
                        icon = VectorIcon(IconPack.Reorderhorizontally),
                    ),
                )
            }
        }

        return items
    }

    override fun getBlockForEvents(): Block {
        return super.getBlockForEvents() ?: Block(
            designBlock = engine.getCurrentPage(),
            type = BlockType.Page,
        )
    }

    override fun setSettings() {
        super.setSettings()
        with(engine.editor) {
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

    override suspend fun onPreExport() = Unit

    override suspend fun onPostExport() = Unit
}
