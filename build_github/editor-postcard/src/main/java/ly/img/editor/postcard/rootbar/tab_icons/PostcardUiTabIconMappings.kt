package ly.img.editor.postcard.rootbar.tab_icons

import ly.img.editor.base.ui.EditorUiTabIconMappings

class PostcardUiTabIconMappings : EditorUiTabIconMappings() {
    init {
        registerTabIconComposable<MessageColorIcon>(MessageColorIconComposable)
        registerTabIconComposable<TemplateColorsIcon>(TemplateColorsIconComposable)
    }
}
