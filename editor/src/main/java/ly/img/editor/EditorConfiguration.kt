package ly.img.editor

import android.app.Activity
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import ly.img.editor.base.components.color_picker.fillAndStrokeColors
import ly.img.editor.core.EditorScope
import ly.img.editor.core.UnstableEditorApi
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.data.Nothing
import ly.img.editor.core.component.data.nothing
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.event.EditorEventHandler
import ly.img.editor.core.iconpack.Arrowback
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.core.iconpack.IconPack as CoreIconPack

/**
 * Configuration class of the UI of the editor. All the properties are optional.
 * Use remember composable functions in the companion object to create an instance of this class.
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
 * [EngineConfiguration]. Check the implementation in {solution_name}.get methods below for the sample usage of the callback. Note that
 * [ly.img.editor.core.event.EditorEventHandler.send] can be called in any of the callbacks of [EngineConfiguration].
 * @param overlay the composable that draws over the editor. It is useful if you want to display a popup dialog or anything in the
 * overlay. For example, you can send a ui event in one of the callbacks of the [EngineConfiguration], capture the event in
 * [onEvent], update your internal [STATE] and render in the overlay based on the state change. Check the implementation in
 * {solution_name}.get methods below for the sample usage of the composable callback.
 * Note that the overlay is edge-to-edge, therefore it is your responsibility to draw over system bars too.
 * @param dock the configuration object of the dock that is displayed as horizontal list of buttons at the bottom of the editor.
 */
class EditorConfiguration<STATE : Parcelable> private constructor(
    val initialState: STATE,
    val uiMode: EditorUiMode = EditorUiMode.SYSTEM,
    val navigationIcon: ImageVector = CoreIconPack.Arrowback,
    val assetLibrary: AssetLibrary = AssetLibrary.getDefault(),
    val colorPalette: List<Color> = fillAndStrokeColors,
    val onEvent: EditorScope.(STATE, EditorEvent) -> STATE = { state, _ -> state },
    val overlay: @Composable (EditorScope.(STATE) -> Unit) = { _ -> },
    val dock: @Composable EditorScope.() -> Dock = { Dock.remember(items = { emptyList() }) },
    private val `_`: Nothing = nothing,
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
         * @param uiMode the UI mode of the editor for theming purpose. [EditorUiMode.SYSTEM] is
         * used by default.
         * @param navigationIcon the navigation icon in the toolbar that is used to exit the editor. A back arrow is used by default.
         * @param assetLibrary the asset library configuration object. Check the documentation of [AssetLibrary] for more details.
         * @param colorPalette the default color palette used in the UI elements that contain color modifiers such as "Fill color",
         * "Stroke Color" etc.
         * @param onEvent the callback that is invoked every time an event is sent via [ly.img.editor.core.event.EditorEventHandler] from
         * [EngineConfiguration]. Check the implementation in {solution_name}.get methods below for the sample usage of the callback. Note that
         * [ly.img.editor.core.event.EditorEventHandler.send] can be called in any of the callbacks of [EngineConfiguration].
         * @param overlay the composable that draws over the editor. It is useful if you want to display a popup dialog or anything in the
         * overlay. For example, you can send a ui event in one of the callbacks of the [EngineConfiguration], capture the event in
         * [onEvent], update your internal [STATE] and render in the overlay based on the state change. Check the implementation in
         * {solution_name}.get methods below for the sample usage of the composable callback.
         * Note that the overlay is edge-to-edge, therefore it is your responsibility to draw over system bars too.
         * @param dock the configuration object of the dock that is displayed as horizontal list of buttons at the bottom of the editor.
         */
        @Composable
        fun <STATE : Parcelable> remember(
            initialState: STATE,
            uiMode: EditorUiMode = EditorUiMode.SYSTEM,
            navigationIcon: ImageVector = CoreIconPack.Arrowback,
            assetLibrary: AssetLibrary = AssetLibrary.getDefault(),
            colorPalette: List<Color> = fillAndStrokeColors,
            onEvent: EditorScope.(STATE, EditorEvent) -> STATE = { state, _ -> state },
            overlay: @Composable (EditorScope.(STATE) -> Unit) = { _ -> },
            dock: @Composable EditorScope.() -> Dock = { Dock.remember(items = { emptyList() }) },
            `_`: Nothing = nothing,
        ): EditorConfiguration<STATE> =
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
                    `_` = `_`,
                )
            }

        /**
         * A composable helper function that creates and remembers an [EngineConfiguration] instance when launching [DesignEditor].
         * It uses [EditorUiState] as its [STATE].
         * In combination with the default [EngineConfiguration.rememberForDesign] it does the following:
         * 1. Displays loading during the editor load and the export
         * 2. Shows confirmation dialog in case there are unsaved changes when trying to close the editor
         * 3. Shows error dialog in case the editor encounters errors.
         *
         * @param uiMode the UI mode of the editor for theming purpose. [EditorUiMode.SYSTEM] is
         * used by default.
         * @param navigationIcon the navigation icon in the toolbar that is used to exit the editor. A back arrow is used by default.
         * @param assetLibrary the asset library configuration object. Check the documentation of [AssetLibrary] for more details.
         * @param colorPalette the default color palette used in the UI elements that contain color modifiers such as "Fill color",
         * "Stroke Color" etc.
         * @param onEvent the callback that is invoked every time an event is sent via [ly.img.editor.core.event.EditorEventHandler] from
         * [EngineConfiguration]. Check the implementation in {solution_name}.get methods below for the sample usage of the callback. Note that
         * [ly.img.editor.core.event.EditorEventHandler.send] can be called in any of the callbacks of [EngineConfiguration].
         * @param overlay the composable that draws over the editor. It is useful if you want to display a popup dialog or anything in the
         * overlay. For example, you can send a ui event in one of the callbacks of the [EngineConfiguration], capture the event in
         * [onEvent], update your internal [STATE] and render in the overlay based on the state change. Check the implementation in
         * {solution_name}.get methods below for the sample usage of the composable callback.
         * Note that the overlay is edge-to-edge, therefore it is your responsibility to draw over system bars too.
         * @param dock the configuration object of the dock that is displayed as horizontal list of buttons at the bottom of the editor.
         * @return an [EditorConfiguration] that should be used to launch a [DesignEditor].
         */
        @UnstableEditorApi
        @Composable
        fun rememberForDesign(
            uiMode: EditorUiMode = EditorUiMode.SYSTEM,
            navigationIcon: ImageVector = CoreIconPack.Arrowback,
            assetLibrary: AssetLibrary = AssetLibrary.getDefault(),
            colorPalette: List<Color> = fillAndStrokeColors,
            onEvent: EditorScope.(EditorUiState, EditorEvent) -> EditorUiState = { state, event ->
                EditorDefaults.onEvent(editorContext.activity, state, event)
            },
            overlay: @Composable (EditorScope.(EditorUiState) -> Unit) = { state ->
                EditorDefaults.Overlay(state = state, eventHandler = editorContext.eventHandler)
            },
            dock: @Composable EditorScope.() -> Dock = { Dock.rememberForDesign() },
            `_`: Nothing = nothing,
        ): EditorConfiguration<EditorUiState> =
            remember(
                initialState = EditorUiState(),
                uiMode = uiMode,
                navigationIcon = navigationIcon,
                assetLibrary = assetLibrary,
                colorPalette = colorPalette,
                onEvent = onEvent,
                overlay = overlay,
                dock = dock,
                `_` = `_`,
            )

        /**
         * A composable helper function that creates and remembers an [EngineConfiguration] instance when launching [PhotoEditor].
         * It uses [EditorUiState] as its [STATE].
         * In combination with the default [EngineConfiguration.rememberForPhoto] it does the following:
         * 1. Displays loading during the editor load and the export
         * 2. Shows confirmation dialog in case there are unsaved changes when trying to close the editor
         * 3. Shows error dialog in case the editor encounters errors.
         *
         * @param uiMode the UI mode of the editor for theming purpose. [EditorUiMode.SYSTEM] is
         * used by default.
         * @param navigationIcon the navigation icon in the toolbar that is used to exit the editor. A back arrow is used by default.
         * @param assetLibrary the asset library configuration object. Check the documentation of [AssetLibrary] for more details.
         * @param colorPalette the default color palette used in the UI elements that contain color modifiers such as "Fill color",
         * "Stroke Color" etc.
         * @param onEvent the callback that is invoked every time an event is sent via [ly.img.editor.core.event.EditorEventHandler] from
         * [EngineConfiguration]. Check the implementation in {solution_name}.get methods below for the sample usage of the callback. Note that
         * [ly.img.editor.core.event.EditorEventHandler.send] can be called in any of the callbacks of [EngineConfiguration].
         * @param overlay the composable that draws over the editor. It is useful if you want to display a popup dialog or anything in the
         * overlay. For example, you can send a ui event in one of the callbacks of the [EngineConfiguration], capture the event in
         * [onEvent], update your internal [STATE] and render in the overlay based on the state change. Check the implementation in
         * {solution_name}.get methods below for the sample usage of the composable callback.
         * Note that the overlay is edge-to-edge, therefore it is your responsibility to draw over system bars too.
         * @param dock the configuration object of the dock that is displayed as horizontal list of buttons at the bottom of the editor.
         * @return an [EditorConfiguration] that should be used to launch a [PhotoEditor].
         */
        @UnstableEditorApi
        @Composable
        fun rememberForPhoto(
            uiMode: EditorUiMode = EditorUiMode.SYSTEM,
            navigationIcon: ImageVector = CoreIconPack.Arrowback,
            assetLibrary: AssetLibrary = AssetLibrary.getDefault(),
            colorPalette: List<Color> = fillAndStrokeColors,
            onEvent: EditorScope.(EditorUiState, EditorEvent) -> EditorUiState = { state, event ->
                EditorDefaults.onEvent(editorContext.activity, state, event)
            },
            overlay: @Composable (EditorScope.(EditorUiState) -> Unit) = { state ->
                EditorDefaults.Overlay(state = state, eventHandler = editorContext.eventHandler)
            },
            dock: @Composable EditorScope.() -> Dock = { Dock.rememberForPhoto() },
            `_`: Nothing = nothing,
        ): EditorConfiguration<EditorUiState> =
            remember(
                initialState = EditorUiState(),
                uiMode = uiMode,
                navigationIcon = navigationIcon,
                assetLibrary = assetLibrary,
                colorPalette = colorPalette,
                onEvent = onEvent,
                overlay = overlay,
                dock = dock,
                `_` = `_`,
            )

        /**
         * A composable helper function that creates and remembers an [EngineConfiguration] instance when launching [ApparelEditor].
         * It uses [EditorUiState] as its [STATE].
         * In combination with the default [EngineConfiguration.rememberForApparel] it does the following:
         * 1. Displays loading during the editor load and the export
         * 2. Shows confirmation dialog in case there are unsaved changes when trying to close the editor
         * 3. Shows error dialog in case the editor encounters errors.
         *
         * @param uiMode the UI mode of the editor for theming purpose. [EditorUiMode.SYSTEM] is
         * used by default.
         * @param navigationIcon the navigation icon in the toolbar that is used to exit the editor. A back arrow is used by default.
         * @param assetLibrary the asset library configuration object. Check the documentation of [AssetLibrary] for more details.
         * @param colorPalette the default color palette used in the UI elements that contain color modifiers such as "Fill color",
         * "Stroke Color" etc.
         * @param onEvent the callback that is invoked every time an event is sent via [ly.img.editor.core.event.EditorEventHandler] from
         * [EngineConfiguration]. Check the implementation in {solution_name}.get methods below for the sample usage of the callback. Note that
         * [ly.img.editor.core.event.EditorEventHandler.send] can be called in any of the callbacks of [EngineConfiguration].
         * @param overlay the composable that draws over the editor. It is useful if you want to display a popup dialog or anything in the
         * overlay. For example, you can send a ui event in one of the callbacks of the [EngineConfiguration], capture the event in
         * [onEvent], update your internal [STATE] and render in the overlay based on the state change. Check the implementation in
         * {solution_name}.get methods below for the sample usage of the composable callback.
         * Note that the overlay is edge-to-edge, therefore it is your responsibility to draw over system bars too.
         * @return an [EditorConfiguration] that should be used to launch an [ApparelEditor].
         */
        @UnstableEditorApi
        @Composable
        fun rememberForApparel(
            uiMode: EditorUiMode = EditorUiMode.SYSTEM,
            navigationIcon: ImageVector = CoreIconPack.Arrowback,
            assetLibrary: AssetLibrary = AssetLibrary.getDefault(),
            colorPalette: List<Color> = fillAndStrokeColors,
            onEvent: EditorScope.(EditorUiState, EditorEvent) -> EditorUiState = { state, event ->
                EditorDefaults.onEvent(editorContext.activity, state, event)
            },
            overlay: @Composable (EditorScope.(EditorUiState) -> Unit) = { state ->
                EditorDefaults.Overlay(state = state, eventHandler = editorContext.eventHandler)
            },
            `_`: Nothing = nothing,
        ): EditorConfiguration<EditorUiState> =
            remember(
                initialState = EditorUiState(),
                uiMode = uiMode,
                navigationIcon = navigationIcon,
                assetLibrary = assetLibrary,
                colorPalette = colorPalette,
                onEvent = onEvent,
                overlay = overlay,
                `_` = `_`,
            )

        /**
         * A composable helper function that creates and remembers an [EngineConfiguration] instance when launching [PostcardEditor].
         * It uses [EditorUiState] as its [STATE].
         * In combination with the default [EngineConfiguration.rememberForPostcard] it does the following:
         * 1. Displays loading during the editor load and the export
         * 2. Shows confirmation dialog in case there are unsaved changes when trying to close the editor
         * 3. Shows error dialog in case the editor encounters errors.
         *
         * @param uiMode the UI mode of the editor for theming purpose. [EditorUiMode.SYSTEM] is
         * used by default.
         * @param navigationIcon the navigation icon in the toolbar that is used to exit the editor. A back arrow is used by default.
         * @param assetLibrary the asset library configuration object. Check the documentation of [AssetLibrary] for more details.
         * @param colorPalette the default color palette used in the UI elements that contain color modifiers such as "Fill color",
         * "Stroke Color" etc.
         * @param onEvent the callback that is invoked every time an event is sent via [ly.img.editor.core.event.EditorEventHandler] from
         * [EngineConfiguration]. Check the implementation in {solution_name}.get methods below for the sample usage of the callback. Note that
         * [ly.img.editor.core.event.EditorEventHandler.send] can be called in any of the callbacks of [EngineConfiguration].
         * @param overlay the composable that draws over the editor. It is useful if you want to display a popup dialog or anything in the
         * overlay. For example, you can send a ui event in one of the callbacks of the [EngineConfiguration], capture the event in
         * [onEvent], update your internal [STATE] and render in the overlay based on the state change. Check the implementation in
         * {solution_name}.get methods below for the sample usage of the composable callback.
         * Note that the overlay is edge-to-edge, therefore it is your responsibility to draw over system bars too.
         * @return an [EditorConfiguration] that should be used to launch a [PostcardEditor].
         */
        @UnstableEditorApi
        @Composable
        fun rememberForPostcard(
            uiMode: EditorUiMode = EditorUiMode.SYSTEM,
            navigationIcon: ImageVector = CoreIconPack.Arrowback,
            assetLibrary: AssetLibrary = AssetLibrary.getDefault(),
            colorPalette: List<Color> = fillAndStrokeColors,
            onEvent: EditorScope.(EditorUiState, EditorEvent) -> EditorUiState = { state, event ->
                EditorDefaults.onEvent(editorContext.activity, state, event)
            },
            overlay: @Composable (EditorScope.(EditorUiState) -> Unit) = { state ->
                EditorDefaults.Overlay(state = state, eventHandler = editorContext.eventHandler)
            },
            `_`: Nothing = nothing,
        ): EditorConfiguration<EditorUiState> =
            remember(
                initialState = EditorUiState(),
                uiMode = uiMode,
                navigationIcon = navigationIcon,
                assetLibrary = assetLibrary,
                colorPalette = colorPalette,
                onEvent = onEvent,
                overlay = overlay,
                `_` = `_`,
            )

        /**
         * A composable helper function that creates and remembers an [EngineConfiguration] instance when launching [VideoEditor].
         * It uses [EditorUiState] as its [STATE].
         * In combination with the default [EngineConfiguration.rememberForVideo] it does the following:
         * 1. Displays loading during the editor load and the export
         * 2. Shows confirmation dialog in case there are unsaved changes when trying to close the editor
         * 3. Shows error dialog in case the editor encounters errors.
         *
         * @param uiMode the UI mode of the editor for theming purpose. [EditorUiMode.SYSTEM] is
         * used by default.
         * @param navigationIcon the navigation icon in the toolbar that is used to exit the editor. A back arrow is used by default.
         * @param assetLibrary the asset library configuration object. Check the documentation of [AssetLibrary] for more details.
         * @param colorPalette the default color palette used in the UI elements that contain color modifiers such as "Fill color",
         * "Stroke Color" etc.* @return an [EditorConfiguration] that should be used to launch a [VideoEditor].
         * @param onEvent the callback that is invoked every time an event is sent via [ly.img.editor.core.event.EditorEventHandler] from
         * [EngineConfiguration]. Check the implementation in {solution_name}.get methods below for the sample usage of the callback. Note that
         * [ly.img.editor.core.event.EditorEventHandler.send] can be called in any of the callbacks of [EngineConfiguration].
         * @param overlay the composable that draws over the editor. It is useful if you want to display a popup dialog or anything in the
         * overlay. For example, you can send a ui event in one of the callbacks of the [EngineConfiguration], capture the event in
         * [onEvent], update your internal [STATE] and render in the overlay based on the state change. Check the implementation in
         * {solution_name}.get methods below for the sample usage of the composable callback.
         * Note that the overlay is edge-to-edge, therefore it is your responsibility to draw over system bars too.
         * @param dock the configuration object of the dock that is displayed as horizontal list of buttons at the bottom of the editor.
         * @return an [EditorConfiguration] that should be used to launch a [VideoEditor].
         */
        @UnstableEditorApi
        @Composable
        fun rememberForVideo(
            uiMode: EditorUiMode = EditorUiMode.SYSTEM,
            navigationIcon: ImageVector = CoreIconPack.Arrowback,
            assetLibrary: AssetLibrary = AssetLibrary.getDefault(),
            colorPalette: List<Color> = fillAndStrokeColors,
            onEvent: EditorScope.(EditorUiState, EditorEvent) -> EditorUiState = { state, event ->
                EditorDefaults.onEvent(editorContext.activity, state, event)
            },
            overlay: @Composable (EditorScope.(EditorUiState) -> Unit) = { state ->
                EditorDefaults.Overlay(state = state, eventHandler = editorContext.eventHandler)
            },
            dock: @Composable EditorScope.() -> Dock = { Dock.rememberForVideo() },
            `_`: Nothing = nothing,
        ): EditorConfiguration<EditorUiState> =
            remember(
                initialState = EditorUiState(),
                uiMode = uiMode,
                navigationIcon = navigationIcon,
                assetLibrary = assetLibrary,
                colorPalette = colorPalette,
                onEvent = onEvent,
                overlay = overlay,
                dock = dock,
                `_` = `_`,
            )

        @Deprecated(
            message = "Use remember{solution_name} instead, i.e. EditorConfiguration.rememberForDesign",
        )
        fun getDefault(
            uiMode: EditorUiMode = EditorUiMode.SYSTEM,
            navigationIcon: ImageVector = CoreIconPack.Arrowback,
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
            `_` = `_`,
        )
    }

    @Deprecated("Use remember functions instead. This constructor will be removed soon.")
    @UnstableEditorApi
    constructor(
        initialState: STATE,
        uiMode: EditorUiMode = EditorUiMode.SYSTEM,
        navigationIcon: ImageVector = CoreIconPack.Arrowback,
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
        `_` = `_`,
    )
}
