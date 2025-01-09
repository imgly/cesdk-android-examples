package ly.img.editor

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import ly.img.editor.base.components.color_picker.fillAndStrokeColors
import ly.img.editor.core.EditorScope
import ly.img.editor.core.UnstableEditorApi
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.InspectorBar
import ly.img.editor.core.component.data.Nothing
import ly.img.editor.core.component.data.nothing
import ly.img.editor.core.component.rememberForDesign
import ly.img.editor.core.component.rememberForPhoto
import ly.img.editor.core.component.rememberForVideo
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.iconpack.ArrowBack
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.library.AssetLibrary

/**
 * A composable helper function that creates and remembers an [EngineConfiguration] instance when launching [DesignEditor].
 * It uses [EditorUiState] as its state.
 * In combination with the default [ly.img.editor.EngineConfiguration.Companion.rememberForDesign] it does the following:
 * 1. Displays loading during the editor load and the export
 * 2. Shows confirmation dialog in case there are unsaved changes when trying to close the editor
 * 3. Shows error dialog in case the editor encounters errors.
 *
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
 * By default [EditorDefaults.onEvent] is used to handle the callback.
 * @param overlay the composable that draws over the editor. It is useful if you want to display a popup dialog or anything in the
 * overlay. For example, you can send a ui event in one of the callbacks of the [EngineConfiguration], capture the event in
 * [onEvent], update your internal state and render in the overlay based on the state change. Check the implementation in
 * {solution_name}.get methods below for the sample usage of the composable callback.
 * Note that the overlay is edge-to-edge, therefore it is your responsibility to draw over system bars too.
 * If null, then the overlay will not be rendered.
 * By default [EditorDefaults.Overlay] is used to handle the composable callback.
 * @param dock the configuration object of the dock that is displayed as horizontal list of items at the bottom of the editor.
 * If null, then the dock will not be rendered.
 * By default [ly.img.editor.core.component.Dock.Companion.rememberForDesign] is used to handle the composable callback.
 * @param inspectorBar the configuration object of the inspector bar that is displayed as horizontal list of items at the
 * bottom of the editor when a design block is selected.
 * If null, then the inspector bar will not be rendered.
 * By default [InspectorBar.remember] is returned with default values.
 * @return an [EditorConfiguration] that should be used to launch a [DesignEditor].
 */
@UnstableEditorApi
@Composable
fun EditorConfiguration.Companion.rememberForDesign(
    uiMode: EditorUiMode = EditorUiMode.SYSTEM,
    navigationIcon: ImageVector = IconPack.ArrowBack,
    assetLibrary: AssetLibrary = AssetLibrary.getDefault(),
    colorPalette: List<Color> = fillAndStrokeColors,
    onEvent: EditorScope.(EditorUiState, EditorEvent) -> EditorUiState = { state, event ->
        EditorDefaults.onEvent(editorContext.activity, state, event)
    },
    overlay: (@Composable (EditorScope.(EditorUiState) -> Unit))? = { state ->
        EditorDefaults.Overlay(state = state, eventHandler = editorContext.eventHandler)
    },
    dock: (@Composable EditorScope.() -> Dock)? = { Dock.rememberForDesign() },
    inspectorBar: (@Composable EditorScope.() -> InspectorBar)? = { InspectorBar.remember() },
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
        inspectorBar = inspectorBar,
        `_` = `_`,
    )

/**
 * A composable helper function that creates and remembers an [EngineConfiguration] instance when launching [PhotoEditor].
 * It uses [EditorUiState] as its state.
 * In combination with the default [ly.img.editor.EngineConfiguration.Companion.rememberForPhoto] it does the following:
 * 1. Displays loading during the editor load and the export
 * 2. Shows confirmation dialog in case there are unsaved changes when trying to close the editor
 * 3. Shows error dialog in case the editor encounters errors.
 *
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
 * By default [EditorDefaults.onEvent] is used to handle the callback.
 * @param overlay the composable that draws over the editor. It is useful if you want to display a popup dialog or anything in the
 * overlay. For example, you can send a ui event in one of the callbacks of the [EngineConfiguration], capture the event in
 * [onEvent], update your internal state and render in the overlay based on the state change. Check the implementation in
 * {solution_name}.get methods below for the sample usage of the composable callback.
 * Note that the overlay is edge-to-edge, therefore it is your responsibility to draw over system bars too.
 * If null, then the overlay will not be rendered.
 * By default [EditorDefaults.Overlay] is used to handle the composable callback.
 * @param dock the configuration object of the dock that is displayed as horizontal list of items at the bottom of the editor.
 * If null, then dock will not be rendered.
 * By default [ly.img.editor.core.component.Dock.Companion.rememberForPhoto] is used to handle the composable callback.
 * @param inspectorBar the configuration object of the inspector bar that is displayed as horizontal list of items at the
 * bottom of the editor when a design block is selected.
 * If null, then the inspector bar will not be rendered.
 * By default [InspectorBar.remember] is returned with default values.
 * @return an [EditorConfiguration] that should be used to launch a [PhotoEditor].
 */
@UnstableEditorApi
@Composable
fun EditorConfiguration.Companion.rememberForPhoto(
    uiMode: EditorUiMode = EditorUiMode.SYSTEM,
    navigationIcon: ImageVector = IconPack.ArrowBack,
    assetLibrary: AssetLibrary = AssetLibrary.getDefault(),
    colorPalette: List<Color> = fillAndStrokeColors,
    onEvent: EditorScope.(EditorUiState, EditorEvent) -> EditorUiState = { state, event ->
        EditorDefaults.onEvent(editorContext.activity, state, event)
    },
    overlay: (@Composable (EditorScope.(EditorUiState) -> Unit))? = { state ->
        EditorDefaults.Overlay(state = state, eventHandler = editorContext.eventHandler)
    },
    dock: (@Composable EditorScope.() -> Dock)? = { Dock.rememberForPhoto() },
    inspectorBar: (@Composable EditorScope.() -> InspectorBar)? = { InspectorBar.remember() },
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
        inspectorBar = inspectorBar,
        `_` = `_`,
    )

/**
 * A composable helper function that creates and remembers an [EngineConfiguration] instance when launching [ApparelEditor].
 * It uses [EditorUiState] as its state.
 * In combination with the default [ly.img.editor.EngineConfiguration.Companion.rememberForApparel] it does the following:
 * 1. Displays loading during the editor load and the export
 * 2. Shows confirmation dialog in case there are unsaved changes when trying to close the editor
 * 3. Shows error dialog in case the editor encounters errors.
 *
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
 * By default [EditorDefaults.onEvent] is used to handle the callback.
 * @param overlay the composable that draws over the editor. It is useful if you want to display a popup dialog or anything in the
 * overlay. For example, you can send a ui event in one of the callbacks of the [EngineConfiguration], capture the event in
 * [onEvent], update your internal state and render in the overlay based on the state change. Check the implementation in
 * {solution_name}.get methods below for the sample usage of the composable callback.
 * Note that the overlay is edge-to-edge, therefore it is your responsibility to draw over system bars too.
 * If null, then the overlay will not be rendered.
 * By default [EditorDefaults.Overlay] is used to handle the composable callback.
 * @param inspectorBar the configuration object of the inspector bar that is displayed as horizontal list of items at the
 * bottom of the editor when a design block is selected.
 * If null, then the inspector bar will not be rendered.
 * By default [InspectorBar.remember] is returned with default values.
 * @return an [EditorConfiguration] that should be used to launch a [ApparelEditor].
 */
@UnstableEditorApi
@Composable
fun EditorConfiguration.Companion.rememberForApparel(
    uiMode: EditorUiMode = EditorUiMode.SYSTEM,
    navigationIcon: ImageVector = IconPack.ArrowBack,
    assetLibrary: AssetLibrary = AssetLibrary.getDefault(),
    colorPalette: List<Color> = fillAndStrokeColors,
    onEvent: EditorScope.(EditorUiState, EditorEvent) -> EditorUiState = { state, event ->
        EditorDefaults.onEvent(editorContext.activity, state, event)
    },
    overlay: (@Composable (EditorScope.(EditorUiState) -> Unit))? = { state ->
        EditorDefaults.Overlay(state = state, eventHandler = editorContext.eventHandler)
    },
    inspectorBar: (@Composable EditorScope.() -> InspectorBar)? = { InspectorBar.remember() },
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
        inspectorBar = inspectorBar,
        `_` = `_`,
    )

/**
 * A composable helper function that creates and remembers an [EngineConfiguration] instance when launching [PostcardEditor].
 * It uses [EditorUiState] as its state.
 * In combination with the default [ly.img.editor.EngineConfiguration.Companion.rememberForPostcard] it does the following:
 * 1. Displays loading during the editor load and the export
 * 2. Shows confirmation dialog in case there are unsaved changes when trying to close the editor
 * 3. Shows error dialog in case the editor encounters errors.
 *
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
 * By default [EditorDefaults.onEvent] is used to handle the callback.
 * @param overlay the composable that draws over the editor. It is useful if you want to display a popup dialog or anything in the
 * overlay. For example, you can send a ui event in one of the callbacks of the [EngineConfiguration], capture the event in
 * [onEvent], update your internal state and render in the overlay based on the state change. Check the implementation in
 * {solution_name}.get methods below for the sample usage of the composable callback.
 * Note that the overlay is edge-to-edge, therefore it is your responsibility to draw over system bars too.
 * If null, then the overlay will not be rendered.
 * By default [EditorDefaults.Overlay] is used to handle the composable callback.
 * @param inspectorBar the configuration object of the inspector bar that is displayed as horizontal list of items at the
 * bottom of the editor when a design block is selected.
 * If null, then the inspector bar will not be rendered.
 * By default [InspectorBar.remember] is returned with default values.
 * @return an [EditorConfiguration] that should be used to launch a [PostcardEditor].
 */
@UnstableEditorApi
@Composable
fun EditorConfiguration.Companion.rememberForPostcard(
    uiMode: EditorUiMode = EditorUiMode.SYSTEM,
    navigationIcon: ImageVector = IconPack.ArrowBack,
    assetLibrary: AssetLibrary = AssetLibrary.getDefault(),
    colorPalette: List<Color> = fillAndStrokeColors,
    onEvent: EditorScope.(EditorUiState, EditorEvent) -> EditorUiState = { state, event ->
        EditorDefaults.onEvent(editorContext.activity, state, event)
    },
    overlay: (@Composable (EditorScope.(EditorUiState) -> Unit))? = { state ->
        EditorDefaults.Overlay(state = state, eventHandler = editorContext.eventHandler)
    },
    inspectorBar: (@Composable EditorScope.() -> InspectorBar)? = { InspectorBar.remember() },
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
        inspectorBar = inspectorBar,
        `_` = `_`,
    )

/**
 * A composable helper function that creates and remembers an [EngineConfiguration] instance when launching [VideoEditor].
 * It uses [EditorUiState] as its state.
 * In combination with the default [ly.img.editor.EngineConfiguration.Companion.rememberForVideo] it does the following:
 * 1. Displays loading during the editor load and the export
 * 2. Shows confirmation dialog in case there are unsaved changes when trying to close the editor
 * 3. Shows error dialog in case the editor encounters errors.
 *
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
 * By default [EditorDefaults.onEvent] is used to handle the callback.
 * @param overlay the composable that draws over the editor. It is useful if you want to display a popup dialog or anything in the
 * overlay. For example, you can send a ui event in one of the callbacks of the [EngineConfiguration], capture the event in
 * [onEvent], update your internal state and render in the overlay based on the state change. Check the implementation in
 * {solution_name}.get methods below for the sample usage of the composable callback.
 * Note that the overlay is edge-to-edge, therefore it is your responsibility to draw over system bars too.
 * By default [EditorDefaults.Overlay] is used to handle the composable callback.
 * @param dock the configuration object of the dock that is displayed as horizontal list of items at the bottom of the editor.
 * By default [ly.img.editor.core.component.Dock.Companion.rememberForVideo] is used to handle the composable callback.
 * If null, then the dock will not be rendered.
 * @param inspectorBar the configuration object of the inspector bar that is displayed as horizontal list of items at the
 * bottom of the editor when a design block is selected.
 * By default [InspectorBar.remember] is returned with default values.
 * If null, then the inspector bar will not be rendered.
 * @return an [EditorConfiguration] that should be used to launch a [VideoEditor].
 */
@UnstableEditorApi
@Composable
fun EditorConfiguration.Companion.rememberForVideo(
    uiMode: EditorUiMode = EditorUiMode.SYSTEM,
    navigationIcon: ImageVector = IconPack.ArrowBack,
    assetLibrary: AssetLibrary = AssetLibrary.getDefault(),
    colorPalette: List<Color> = fillAndStrokeColors,
    onEvent: EditorScope.(EditorUiState, EditorEvent) -> EditorUiState = { state, event ->
        EditorDefaults.onEvent(editorContext.activity, state, event)
    },
    overlay: (@Composable (EditorScope.(EditorUiState) -> Unit))? = { state ->
        EditorDefaults.Overlay(state = state, eventHandler = editorContext.eventHandler)
    },
    dock: (@Composable EditorScope.() -> Dock)? = { Dock.rememberForVideo() },
    inspectorBar: (@Composable EditorScope.() -> InspectorBar)? = { InspectorBar.remember() },
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
        inspectorBar = inspectorBar,
        `_` = `_`,
    )
