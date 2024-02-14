package ly.img.editor.base.ui

import ly.img.editor.base.components.VectorIcon
import ly.img.editor.base.components.VectorIconComposable
import ly.img.editor.base.dock.FillStrokeIcon
import ly.img.editor.base.dock.FillStrokeIconComposable
import ly.img.editor.core.ui.tab_item.TabIconMappings

open class EditorUiTabIconMappings : TabIconMappings() {
    init {
        registerTabIconComposable<VectorIcon>(VectorIconComposable)
        registerTabIconComposable<FillStrokeIcon>(FillStrokeIconComposable)
    }
}
