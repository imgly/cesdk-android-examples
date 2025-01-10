package ly.img.editor

import android.app.Activity
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import ly.img.editor.base.components.color_picker.fillAndStrokeColors
import ly.img.editor.core.EditorScope
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.InspectorBar
import ly.img.editor.core.component.data.Nothing
import ly.img.editor.core.component.data.nothing
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.event.EditorEventHandler
import ly.img.editor.core.iconpack.ArrowBack
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.library.AssetLibrary

/**
 * Configuration class of the UI of the editor. All the properties are optional.
 * Use remember composable functions in the companion object to create an instance of this class.
 *
 * @param initialState the initial state of the editor. It is used to render the [overlay].
 * Initial state can then later be altered in [onEvent].
 * @param uiMode the UI mode of the editor for theming purpose.
 * @param navigationIcon the navigation icon in the toolbar that is used to exit the editor. A back arrow is used by default.
 * @param assetLibrary the asset library configuration object. Check the documentation of [AssetLibrary] for more details.
 * @param colorPalette the default color palette used in the UI elements that contain color modifiers such as "Fill color",
 * "Stroke Color" etc.
 * @param onEvent the callback that is invoked every time an event is sent via [ly.img.editor.core.event.EditorEventHandler] from
 * [EngineConfiguration].
 * Note that [ly.img.editor.core.event.EditorEventHandler.send] can be called in any of the callbacks of [EngineConfiguration]
 * and from composable functions in [EditorConfiguration].
 * @param overlay the composable that draws over the editor. It is useful if you want to display a popup dialog or anything in the
 * overlay. For example, you can send a ui event in one of the callbacks of the [EngineConfiguration], capture the event in
 * [onEvent], update your internal [STATE] and render in the overlay based on the state change. Check the implementation in
 * {solution_name}.get methods below for the sample usage of the composable callback.
 * Note that the overlay is edge-to-edge, therefore it is your responsibility to draw over system bars too.
 * @param dock the configuration object of the dock that is displayed as horizontal list of items at the bottom of the editor.
 * @param inspectorBar the configuration object of the inspector bar that is displayed as horizontal list of items at the
 * bottom of the editor when a design block is selected.
 */
class EditorConfiguration<STATE : Parcelable> private constructor(
    val initialState: STATE,
    val uiMode: EditorUiMode,
    val navigationIcon: ImageVector,
    val assetLibrary: AssetLibrary,
    val colorPalette: List<Color>,
    val onEvent: EditorScope.(STATE, EditorEvent) -> STATE,
    val overlay: (@Composable (EditorScope.(STATE) -> Unit))?,
    val dock: (@Composable EditorScope.() -> Dock)?,
    val inspectorBar: (@Composable EditorScope.() -> InspectorBar)?,
    private val `_`: Nothing,
) {
    override fun toString(): String {
        return "$`_`EditorConfiguration(" +
            "uiMode = $uiMode, " +
            ", navigationIcon = $navigationIcon" +
            ", assetLibrary = $assetLibrary" +
            ", colorPalette = $colorPalette" +
            ", onEvent = $onEvent" +
            ", overlay = $overlay" +
            ", dock = $dock" +
            ")"
    }

    companion object {
        /**
         * A composable function that creates and remembers an [EditorConfiguration] instance.
         *
         * @param initialState the initial state of the editor. It is used to render the [overlay].
         * Initial state can then later be altered in [onEvent].
         * @param uiMode the UI mode of the editor for theming purpose.
         * Default value is [EditorUiMode.SYSTEM].
         * @param navigationIcon the navigation icon in the toolbar that is used to exit the editor.
         * Default value is [IconPack.ArrowBack].
         * @param assetLibrary the asset library configuration object. Check the documentation of [AssetLibrary] for more details.
         * Default value is [AssetLibrary.getDefault].
         * @param colorPalette the default color palette used in the UI elements that contain color modifiers such as "Fill color",
         * "Stroke Color" etc.
         * Default value is [fillAndStrokeColors].
         * @param onEvent the callback that is invoked every time an event is sent via [ly.img.editor.core.event.EditorEventHandler] from
         * [EngineConfiguration].
         * Note that [ly.img.editor.core.event.EditorEventHandler.send] can be called in any of the callbacks of [EngineConfiguration]
         * and from composable functions in [EditorConfiguration].
         * By default, no events are handled.
         * @param overlay the composable that draws over the editor. It is useful if you want to display a popup dialog or anything in the
         * overlay. For example, you can send a ui event in one of the callbacks of the [EngineConfiguration], capture the event in
         * [onEvent], update your internal [STATE] and render in the overlay based on the state change. Check the implementation in
         * {solution_name}.get methods below for the sample usage of the composable callback.
         * Note that the overlay is edge-to-edge, therefore it is your responsibility to draw over system bars too.
         * If null, then the overlay will not be rendered.
         * By default, the overlay is null.
         * @param dock the configuration object of the dock that is displayed as horizontal list of items at the bottom of the editor.
         * If null, then the dock will not be rendered.
         * By default, the dock is null.
         * @param inspectorBar the configuration object of the inspector bar that is displayed as horizontal list of items at the
         * bottom of the editor when a design block is selected.
         * If null, then the inspector bar will not be rendered.
         * By default [InspectorBar.remember] is returned with default values.
         * @return an [EditorConfiguration] that should be used to launch an editor.
         */
        @Composable
        fun <STATE : Parcelable> remember(
            initialState: STATE,
            uiMode: EditorUiMode = EditorUiMode.SYSTEM,
            navigationIcon: ImageVector = IconPack.ArrowBack,
            assetLibrary: AssetLibrary = AssetLibrary.getDefault(),
            colorPalette: List<Color> = fillAndStrokeColors,
            onEvent: EditorScope.(STATE, EditorEvent) -> STATE = { state, _ -> state },
            overlay: (@Composable (EditorScope.(STATE) -> Unit))? = null,
            dock: (@Composable EditorScope.() -> Dock)? = null,
            inspectorBar: (@Composable EditorScope.() -> InspectorBar)? = { InspectorBar.remember() },
            `_`: Nothing = nothing,
        ): EditorConfiguration<STATE> =
            // todo consider adding all parameters as keys. If we add now it crashes.
            androidx.compose.runtime.remember {
                EditorConfiguration(
                    initialState = initialState,
                    uiMode = uiMode,
                    navigationIcon = navigationIcon,
                    assetLibrary = assetLibrary,
                    colorPalette = colorPalette,
                    onEvent = onEvent,
                    overlay = overlay,
                    dock = dock,
                    inspectorBar = inspectorBar,
                    `_` = `_`,
                )
            }

        @Deprecated(
            message = "Use rememberFor{solution_name} instead, i.e. EditorConfiguration.rememberForDesign",
        )
        fun getDefault(
            uiMode: EditorUiMode = EditorUiMode.SYSTEM,
            navigationIcon: ImageVector = IconPack.ArrowBack,
            assetLibrary: AssetLibrary = AssetLibrary.getDefault(),
            colorPalette: List<Color> = fillAndStrokeColors,
            `_`: Nothing = nothing,
        ) = EditorConfiguration(
            initialState = EditorUiState(),
            uiMode = uiMode,
            navigationIcon = navigationIcon,
            assetLibrary = assetLibrary,
            colorPalette = colorPalette,
            onEvent = { state, event ->
                EditorDefaults.onEvent(editorContext.activity, state, event)
            },
            overlay = { state ->
                EditorDefaults.Overlay(state = state, eventHandler = editorContext.eventHandler)
            },
            dock = null,
            inspectorBar = { InspectorBar.remember() },
            `_` = `_`,
        )
    }

    @Deprecated("Use EditorConfiguration.Companion.remember function instead. This constructor will be removed soon.")
    constructor(
        initialState: STATE,
        uiMode: EditorUiMode = EditorUiMode.SYSTEM,
        navigationIcon: ImageVector = IconPack.ArrowBack,
        assetLibrary: AssetLibrary = AssetLibrary.getDefault(),
        colorPalette: List<Color> = fillAndStrokeColors,
        onEvent: (Activity, STATE, EditorEvent) -> STATE = { _, state, _ -> state },
        overlay: @Composable ((STATE, EditorEventHandler) -> Unit) = { _, _ -> },
        `_`: Nothing = nothing,
    ) : this(
        initialState = initialState,
        uiMode = uiMode,
        navigationIcon = navigationIcon,
        assetLibrary = assetLibrary,
        colorPalette = colorPalette,
        onEvent = { state, event -> onEvent(editorContext.activity, state, event) },
        overlay = { state -> overlay(state, editorContext.eventHandler) },
        dock = null,
        inspectorBar = { InspectorBar.remember() },
        `_` = `_`,
    )
}
