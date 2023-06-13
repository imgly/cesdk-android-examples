package ly.img.cesdk.editorui

import ly.img.cesdk.Secrets
import ly.img.cesdk.engine.BASE_PATH
import ly.img.cesdk.engine.DOUBLE_CLICK_SELECTION_MODE_DIRECT
import ly.img.cesdk.engine.ROLE
import ly.img.cesdk.engine.ROLE_ADOPTER
import ly.img.cesdk.engine.TOUCH_ACTION_NONE
import ly.img.cesdk.engine.TOUCH_ACTION_ZOOM
import ly.img.engine.Engine
import ly.img.engine.GlobalScope

internal fun setSettingsForEditorUi(engine: Engine) {
    engine.editor.setSettingBoolean("touch/singlePointPanning", true)
    engine.editor.setSettingBoolean("touch/dragStartCanSelect", false)
    engine.editor.setSettingEnum("touch/pinchAction", TOUCH_ACTION_ZOOM)
    engine.editor.setSettingEnum("touch/rotateAction", TOUCH_ACTION_NONE)
    engine.editor.setSettingBoolean("doubleClickToCropEnabled", true)
    engine.editor.setSettingString("basePath", BASE_PATH)
    engine.editor.setSettingEnum(ROLE, ROLE_ADOPTER)
    engine.editor.setSettingEnum("doubleClickSelectionMode", DOUBLE_CLICK_SELECTION_MODE_DIRECT)
    engine.editor.setSettingString("license", Secrets.license)
    arrayOf(
        "design/style",
        "design/arrange",
        "design/arrange/move",
        "design/arrange/resize",
        "design/arrange/rotate",
        "design/arrange/flip",
        "content/replace",
        "lifecycle/destroy",
        "lifecycle/duplicate",
        "editor/select"
    ).forEach {
        engine.editor.setGlobalScope(it, GlobalScope.DEFER)
    }
}