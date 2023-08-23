package ly.img.cesdk.editorui

import ly.img.cesdk.components.VectorIcon
import ly.img.cesdk.components.VectorIconComposable
import ly.img.cesdk.core.ui.tab_item.TabIconMappings
import ly.img.cesdk.dock.FillStrokeIcon
import ly.img.cesdk.dock.FillStrokeIconComposable

open class EditorUiTabIconMappings : TabIconMappings() {

    init {
        registerTabIconComposable<VectorIcon>(VectorIconComposable)
        registerTabIconComposable<FillStrokeIcon>(FillStrokeIconComposable)
    }
}