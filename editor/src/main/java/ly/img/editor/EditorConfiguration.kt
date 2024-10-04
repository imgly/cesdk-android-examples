package ly.img.editor

import android.app.Activity
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import ly.img.editor.EditorConfiguration.Companion.getDefault
import ly.img.editor.base.components.color_picker.fillAndStrokeColors
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.event.EditorEventHandler
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.core.ui.iconpack.Arrowback
import ly.img.editor.core.ui.iconpack.IconPack

/**
 * Configuration class of the UI of the editor. All the properties are optional.
 *
 * @param initialState the initial state of the editor. It is used to render the [overlay].
 * Initial state can then later be altered in [onEvent].
 * @param uiMode the UI mode of the editor for theming purpose. [EditorUiMode.SYSTEM] is
 * used by default.
 * @param navigationIcon the navigation icon in the toolbar that is used to exit the editor. A back arrow is used by default.
 * @param assetLibrary the asset library configuration object. Check the documentation of [AssetLibrary] for more details.
 * @param colorPalette the default color palette used in the UI elements that contain color modifiers such as "Fill color",
 * "Stroke Color" etc.
 * @param onEvent the callback that is invoked every time an event is sent via [ly.img.editor.core.event.EditorEventHandler] from
 * [EngineConfiguration]. Check the implementation in [getDefault] for the sample usage of the callback. Note that
 * [ly.img.editor.core.event.EditorEventHandler.send] can be called in any of the callbacks of [EngineConfiguration].
 * @param overlay the composable that draws over the editor. It is useful if you want to display a popup dialog or anything in the
 * overlay. For example, you can send a ui event in one of the callbacks of the [EngineConfiguration], capture the event in
 * [onEvent], update your internal [STATE] and render in the overlay based on the state change. Check the implementation in
 * [getDefault] for the sample usage of the composable callback.
 * Note that the overlay is edge-to-edge, therefore it is your responsibility to draw over system bars too.
 */
class EditorConfiguration<STATE : Parcelable>(
    val initialState: STATE,
    val uiMode: EditorUiMode = EditorUiMode.SYSTEM,
    val navigationIcon: ImageVector = IconPack.Arrowback,
    val assetLibrary: AssetLibrary = AssetLibrary.getDefault(),
    val colorPalette: List<Color> = fillAndStrokeColors,
    val onEvent: (Activity, STATE, EditorEvent) -> STATE = { _, state, _ -> state },
    val overlay: @Composable ((STATE, EditorEventHandler) -> Unit) = { _, _ -> },
) {
    companion object {
        /**
         * The default implementation of the [EditorConfiguration] that uses [EditorUiState] as its [STATE].
         * In combination with the default [EngineConfiguration] it does the following:
         * 1. Displays loading during the editor load and the export
         * 2. Shows confirmation dialog in case there are unsaved changes when trying to close the editor
         * 3. Shows error dialog in case the editor encounters errors.
         * Check [EditorDefaults.onEvent] and [EditorDefaults.Overlay] for the default implementations.
         */
        fun getDefault(
            uiMode: EditorUiMode = EditorUiMode.SYSTEM,
            navigationIcon: ImageVector = IconPack.Arrowback,
            assetLibrary: AssetLibrary = AssetLibrary.getDefault(),
            colorPalette: List<Color> = fillAndStrokeColors,
        ) = EditorConfiguration(
            initialState = EditorUiState(),
            uiMode = uiMode,
            navigationIcon = navigationIcon,
            assetLibrary = assetLibrary,
            colorPalette = colorPalette,
            onEvent = EditorDefaults::onEvent,
            overlay = { state, eventHandler ->
                EditorDefaults.Overlay(state = state, eventHandler = eventHandler)
            },
        )
    }
}
