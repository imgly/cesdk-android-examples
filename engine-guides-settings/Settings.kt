import android.util.Log
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withTimeout
import ly.img.engine.Color
import ly.img.engine.Engine
import ly.img.engine.GlobalScope
import ly.img.engine.PropertyType

private const val TAG = "SettingsGuide"

suspend fun settings(
    engine: Engine,
    scope: CoroutineScope,
    afterSettingChange: suspend () -> Unit = {},
) {
    val previousCropEnabled = engine.editor.getSettingBoolean("doubleClickToCropEnabled")
    val previousMaxPreviewResolution = engine.editor.getSettingInt("maxPreviewResolution")
    val previousSnappingThreshold = engine.editor.getSettingFloat("positionSnappingThreshold")
    val previousTitleSeparator = engine.editor.getSettingString("page/title/separator")
    val previousHighlightColor = engine.editor.getSettingColor("highlightColor")
    val previousSelectionMode = engine.editor.getSettingEnum("doubleClickSelectionMode")
    val previousRole = engine.editor.getRole()
    val previousMoveScope = engine.editor.getGlobalScope(key = "layer/move")

    // highlight-android-discover-settings
    val allSettings = engine.editor.findAllSettings()
    val selectionModeType = engine.editor.getSettingType("doubleClickSelectionMode")
    val selectionModeOptions = engine.editor.getSettingEnumOptions("doubleClickSelectionMode")
    // highlight-android-discover-settings

    check("doubleClickSelectionMode" in allSettings)
    check(selectionModeType == PropertyType.ENUM)
    check("Direct" in selectionModeOptions)
    check("Hierarchical" in selectionModeOptions)

    // highlight-android-read-write-settings
    engine.editor.setSettingBoolean("doubleClickToCropEnabled", value = false)
    val cropEnabled = engine.editor.getSettingBoolean("doubleClickToCropEnabled")

    engine.editor.setSettingInt("maxPreviewResolution", value = 2048)
    val maxPreviewResolution = engine.editor.getSettingInt("maxPreviewResolution")

    engine.editor.setSettingFloat("positionSnappingThreshold", value = 2F)
    val snappingThreshold = engine.editor.getSettingFloat("positionSnappingThreshold")

    engine.editor.setSettingString("page/title/separator", value = " | ")
    val titleSeparator = engine.editor.getSettingString("page/title/separator")

    val highlightColor = Color.fromRGBA(r = 1F, g = 0F, b = 1F, a = 1F)
    engine.editor.setSettingColor("highlightColor", value = highlightColor)
    val currentHighlightColor = engine.editor.getSettingColor("highlightColor")

    engine.editor.setSettingEnum("doubleClickSelectionMode", value = "Direct")
    val selectionMode = engine.editor.getSettingEnum("doubleClickSelectionMode")
    // highlight-android-read-write-settings

    check(!cropEnabled)
    check(maxPreviewResolution == 2048)
    check(snappingThreshold == 2F)
    check(titleSeparator == " | ")
    check(currentHighlightColor == highlightColor)
    check(selectionMode == "Direct")

    // highlight-android-observe-settings
    val settingsChanged = CompletableDeferred<Unit>()
    val settingsJob = engine.editor.onSettingsChanged()
        .onEach {
            Log.i(TAG, "Editor settings have changed")
            settingsChanged.complete(Unit)
        }
        .launchIn(scope)

    engine.editor.setSettingBoolean("doubleClickToCropEnabled", value = true)
    val observedCropEnabled = engine.editor.getSettingBoolean("doubleClickToCropEnabled")
    // highlight-android-observe-settings

    afterSettingChange()

    // highlight-android-observe-settings-cleanup
    withTimeout(1_000) { settingsChanged.await() }
    settingsJob.cancel()
    // highlight-android-observe-settings-cleanup

    check(observedCropEnabled)

    // highlight-android-role-management
    val currentRole = engine.editor.getRole()
    val roleJob = engine.editor.onRoleChanged()
        .onEach { role -> Log.i(TAG, "Role changed to $role") }
        .launchIn(scope)

    engine.editor.setRole("Adopter")
    val appliedRole = engine.editor.getRole()

    roleJob.cancel()
    // highlight-android-role-management

    check(currentRole == previousRole)
    check(appliedRole == "Adopter")

    // highlight-android-global-scopes
    val allScopes = engine.editor.findAllScopes()
    engine.editor.setGlobalScope(key = "layer/move", globalScope = GlobalScope.DEFER)
    val moveScope = engine.editor.getGlobalScope(key = "layer/move")
    // highlight-android-global-scopes

    check("layer/move" in allScopes)
    check(moveScope == GlobalScope.DEFER)

    engine.editor.setRole(previousRole)
    engine.editor.setGlobalScope(key = "layer/move", globalScope = previousMoveScope)
    engine.editor.setSettingBoolean("doubleClickToCropEnabled", previousCropEnabled)
    engine.editor.setSettingInt("maxPreviewResolution", previousMaxPreviewResolution)
    engine.editor.setSettingFloat("positionSnappingThreshold", previousSnappingThreshold)
    engine.editor.setSettingString("page/title/separator", previousTitleSeparator)
    engine.editor.setSettingColor("highlightColor", previousHighlightColor)
    engine.editor.setSettingEnum("doubleClickSelectionMode", previousSelectionMode)
}
