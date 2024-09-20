package ly.img.editor.base.ui

import android.net.Uri
import ly.img.editor.base.engine.TOUCH_ACTION_NONE
import ly.img.editor.base.engine.TOUCH_ACTION_ZOOM
import ly.img.editor.core.ui.engine.ROLE_ADOPTER
import ly.img.editor.core.ui.engine.Scope
import ly.img.engine.Engine
import ly.img.engine.GlobalScope

internal fun setSettingsForEditorUi(
    engine: Engine,
    baseUri: Uri,
) {
    engine.editor.run {
        setSettingBoolean("touch/singlePointPanning", true)
        setSettingBoolean("touch/dragStartCanSelect", false)
        setSettingEnum("touch/pinchAction", TOUCH_ACTION_ZOOM)
        setSettingEnum("touch/rotateAction", TOUCH_ACTION_NONE)
        setSettingBoolean("doubleClickToCropEnabled", true)
        setSettingString("basePath", baseUri.toString())
        setRole(ROLE_ADOPTER)
        setSettingEnum("camera/clamping/overshootMode", "Center")
        val color = engine.editor.getSettingColor("highlightColor")
        engine.editor.setSettingColor("placeholderHighlightColor", color)
    }
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
    ).forEach {
        engine.editor.setGlobalScope(it, GlobalScope.DEFER)
    }
}
