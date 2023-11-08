package ly.img.cesdk.editorui

import ly.img.cesdk.core.Secrets
import ly.img.cesdk.core.engine.BASE_PATH
import ly.img.cesdk.core.engine.ROLE
import ly.img.cesdk.core.engine.ROLE_ADOPTER
import ly.img.cesdk.engine.DOUBLE_CLICK_SELECTION_MODE_DIRECT
import ly.img.cesdk.engine.Scope
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
    engine.editor.setSettingBoolean("features/unifiedBlocksEnabled", true)
    arrayOf(
        Scope.TextCharacter,
        Scope.StrokeChange,
        Scope.LayerOpacity,
        Scope.LayerBlendMode,
        Scope.LayerVisibility,
        Scope.LayerClipping,
        Scope.LayerMove,
        Scope.LayerResize,
        Scope.LayerRotate,
        Scope.LayerFlip,
        Scope.TextEdit,
        Scope.FillChange,
        Scope.LifecycleDestroy,
        Scope.LifecycleDuplicate,
        Scope.EditorSelect
    ).forEach {
        engine.editor.setGlobalScope(it, GlobalScope.DEFER)
    }
}