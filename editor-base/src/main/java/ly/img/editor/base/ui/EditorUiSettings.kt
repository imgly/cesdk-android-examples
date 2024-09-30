package ly.img.editor.base.ui

import android.net.Uri
import ly.img.editor.base.engine.CAMERA_OVERSHOOT_MODE_CENTER
import ly.img.editor.base.engine.DOUBLE_CLICK_SELECTION_MODE_DIRECT
import ly.img.editor.base.engine.TOUCH_ACTION_ROTATE
import ly.img.editor.base.engine.TOUCH_ACTION_ZOOM
import ly.img.editor.core.ui.engine.ROLE_ADOPTER
import ly.img.editor.core.ui.engine.Scope
import ly.img.engine.Engine
import ly.img.engine.GlobalScope

internal fun setSettingsForEditorUi(
    engine: Engine,
    baseUri: Uri,
) {
    val scopes =
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
            Scope.EditorSelect,
        )

    with(engine.editor) {
        // Set role first as it affects other settings
        setRole(ROLE_ADOPTER)
        setSettingBoolean("touch/singlePointPanning", true)
        setSettingBoolean("touch/dragStartCanSelect", false)
        setSettingEnum("touch/rotateAction", TOUCH_ACTION_ROTATE)
        setSettingEnum("touch/pinchAction", TOUCH_ACTION_ZOOM)
        setSettingBoolean("doubleClickToCropEnabled", true)
        setSettingString("basePath", baseUri.toString())
        setSettingEnum("doubleClickSelectionMode", DOUBLE_CLICK_SELECTION_MODE_DIRECT)
        setSettingEnum("camera/clamping/overshootMode", CAMERA_OVERSHOOT_MODE_CENTER)
        setSettingColor("placeholderHighlightColor", getSettingColor("highlightColor"))

        scopes.forEach {
            setGlobalScope(it, GlobalScope.DEFER)
        }
    }
}
