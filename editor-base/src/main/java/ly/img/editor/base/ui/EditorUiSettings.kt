package ly.img.editor.base.ui

import ly.img.editor.base.engine.DOUBLE_CLICK_SELECTION_MODE_DIRECT
import ly.img.editor.base.engine.TOUCH_ACTION_NONE
import ly.img.editor.base.engine.TOUCH_ACTION_ZOOM
import ly.img.editor.core.ui.engine.BASE_PATH
import ly.img.editor.core.ui.engine.ROLE
import ly.img.editor.core.ui.engine.ROLE_ADOPTER
import ly.img.editor.core.ui.engine.Scope
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