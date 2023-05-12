package ly.img.cesdk.rootbar.tab_icons

import ly.img.cesdk.editorui.EditorUiTabIconMappings

class PostcardUiTabIconMappings : EditorUiTabIconMappings() {

    init {
        registerTabIconComposable<MessageColorIcon>(MessageColorIconComposable)
        registerTabIconComposable<TemplateColorsIcon>(TemplateColorsIconComposable)
    }
}