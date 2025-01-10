package ly.img.editor.core.component

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import ly.img.editor.core.EditorScope
import ly.img.editor.core.LocalEditorScope
import ly.img.editor.core.R
import ly.img.editor.core.component.EditorComponent.Companion.noneEnterTransition
import ly.img.editor.core.component.EditorComponent.Companion.noneExitTransition
import ly.img.editor.core.component.InspectorBar.Button
import ly.img.editor.core.component.InspectorBar.ButtonScope
import ly.img.editor.core.component.data.ConicalGradientFill
import ly.img.editor.core.component.data.EditorIcon
import ly.img.editor.core.component.data.Fill
import ly.img.editor.core.component.data.LinearGradientFill
import ly.img.editor.core.component.data.Nothing
import ly.img.editor.core.component.data.RadialGradientFill
import ly.img.editor.core.component.data.Selection
import ly.img.editor.core.component.data.SolidFill
import ly.img.editor.core.component.data.nothing
import ly.img.editor.core.component.data.unsafeLazy
import ly.img.editor.core.compose.rememberLastValue
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.iconpack.Adjustments
import ly.img.editor.core.iconpack.AsClip
import ly.img.editor.core.iconpack.AsOverlay
import ly.img.editor.core.iconpack.Blur
import ly.img.editor.core.iconpack.CropRotate
import ly.img.editor.core.iconpack.Delete
import ly.img.editor.core.iconpack.Duplicate
import ly.img.editor.core.iconpack.Effect
import ly.img.editor.core.iconpack.Filter
import ly.img.editor.core.iconpack.GroupEnter
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.Keyboard
import ly.img.editor.core.iconpack.LayersOutline
import ly.img.editor.core.iconpack.ReorderHorizontally
import ly.img.editor.core.iconpack.Replace
import ly.img.editor.core.iconpack.SelectGroup
import ly.img.editor.core.iconpack.ShapeIcon
import ly.img.editor.core.iconpack.Split
import ly.img.editor.core.iconpack.Typeface
import ly.img.editor.core.iconpack.VolumeHigh
import ly.img.editor.core.sheet.SheetType
import ly.img.editor.core.ui.EditorIcon
import ly.img.engine.BlockApi
import ly.img.engine.ColorSpace
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.RGBAColor
import ly.img.engine.SceneMode
import ly.img.engine.ShapeType

private const val KIND_STICKER = "sticker"

/**
 * An extension function for checking whether the [designBlock] is a background track.
 *
 * @return true if the [designBlock] is a background track.
 */
private fun Engine.isBackgroundTrack(designBlock: DesignBlock): Boolean {
    return DesignBlockType.get(block.getType(designBlock)) == DesignBlockType.Track &&
        block.isAlwaysOnBottom(designBlock)
}

/**
 * An extension function for checking whether the [selection] can be moved up/down.
 *
 * @param selection the selection that is queried.
 * @return true if the [selection] can be moved, false otherwise.
 */
private fun Engine.isMoveAllowed(selection: Selection): Boolean {
    return block.isAllowedByScope(selection.designBlock, "editor/add") &&
        !isGrouped(selection) && selection.parentDesignBlock?.let { !isBackgroundTrack(it) } ?: true
}

/**
 * An extension function for checking whether the [selection] can be duplicated.
 *
 * @param selection the selection that is queried.
 * @return true if the [selection] can be duplicated, false otherwise.
 */
private fun Engine.isDuplicateAllowed(selection: Selection): Boolean {
    return block.isAllowedByScope(selection.designBlock, "lifecycle/duplicate") && !isGrouped(selection)
}

/**
 * An extension function for checking whether the [selection] can be deleted.
 *
 * @param selection the selection that is queried.
 * @return true if the [selection] can be deleted, false otherwise.
 */
private fun Engine.isDeleteAllowed(selection: Selection): Boolean {
    return block.isAllowedByScope(selection.designBlock, "lifecycle/destroy") && !isGrouped(selection)
}

/**
 * An extension function for checking whether the [selection] is part of a group.
 *
 * @param selection the selection that is queried.
 * @return true if the [selection] is part of a group, false otherwise.
 */
private fun Engine.isGrouped(selection: Selection): Boolean {
    val parentDesignBlock = selection.parentDesignBlock ?: return false
    return DesignBlockType.get(block.getType(parentDesignBlock)) == DesignBlockType.Group
}

/**
 * The id of the inspector bar button returned by [InspectorBar.Button.Companion.rememberReorder].
 */
val Button.Id.Companion.reorder by unsafeLazy {
    EditorComponentId("ly.img.component.inspectorBar.button.reorder")
}

/**
 * A composable helper function that creates and remembers an [InspectorBar.Button] that
 * opens reorder sheet via [EditorEvent.Sheet.Open].
 *
 * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
 * Default value is always [IconPack.ReorderHorizontally].
 * @param text the text content of the button as a string. If null then text is not rendered.
 * Default value is always [R.string.ly_img_editor_reorder].
 * @param tint the tint color of the content. If null then no tint is applied.
 * Default value is null.
 * @param enabled whether the button is enabled.
 * Default value is always true.
 * @param onClick the callback that is invoked when the button is clicked.
 * By default [EditorEvent.Sheet.Open] is invoked with sheet type [SheetType.Reorder].
 * @param scope the scope of this component. Every new value will trigger recomposition of all functions with
 * signature @Composable Scope.() -> {}.
 * If you need to access [EditorScope] to construct the scope, use [LocalEditorScope].
 * By default it is updated both when the parent component scope ([InspectorBar.scope], accessed via [LocalEditorScope]) is updated
 * and whenever the number of children in the background track becomes >= or < than 2.
 * @param visible whether the button should be visible.
 * By default the value is true when the editor has a scene with a background track where the number of tracks is >= 2, false otherwise.
 * @param enterTransition transition of the button when it enters the parent composable.
 * Default value is always no enter transition.
 * @param exitTransition transition of the button when it exits the parent composable.
 * Default value is always no exit transition.
 * @param decoration decoration of the button. Useful when you want to add custom background, foreground, shadow, paddings etc.
 * Default value is always no decoration.
 * @return a button that will be displayed in the inspector bar.
 */
@Composable
fun Button.Companion.rememberReorder(
    vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.ReorderHorizontally },
    text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_reorder) },
    tint: (@Composable ButtonScope.() -> Color)? = null,
    enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
    onClick: ButtonScope.() -> Unit = {
        editorContext.eventHandler.send(EditorEvent.Sheet.Open(SheetType.Reorder()))
    },
    scope: ButtonScope =
        (LocalEditorScope.current as InspectorBar.Scope).run {
            rememberLastValue(this) {
                if (editorContext.safeSelection == null) lastValue else ButtonScope(parentScope = this@run)
            }
        },
    visible: @Composable ButtonScope.() -> Boolean = {
        remember(this) {
            editorContext.selection.parentDesignBlock
                ?.let { parent ->
                    editorContext.engine.isBackgroundTrack(parent) &&
                        editorContext.engine.block.getChildren(parent).size >= 2
                } ?: false
        }
    },
    enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
    exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
    decoration: @Composable ButtonScope.(@Composable () -> Unit) -> Unit = { it() },
    `_`: Nothing = nothing,
): Button =
    remember(
        id = Button.Id.reorder,
        vectorIcon = vectorIcon,
        text = text,
        tint = tint,
        enabled = enabled,
        onClick = onClick,
        scope = scope,
        visible = visible,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        decoration = decoration,
        `_` = `_`,
    )

/**
 * The id of the inspector bar button returned by [InspectorBar.Button.Companion.rememberAdjustments].
 */
val Button.Id.Companion.adjustments by unsafeLazy {
    EditorComponentId("ly.img.component.inspectorBar.button.adjustments")
}

/**
 * A helper function that returns an [InspectorBar.Button] that opens adjustments sheet via [EditorEvent.Sheet.Open].
 *
 * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
 * Default value is always [IconPack.Adjustments].
 * @param text the text content of the button as a string. If null then text is not rendered.
 * Default value is always [R.string.ly_img_editor_adjustments].
 * @param tint the tint color of the content. If null then no tint is applied.
 * Default value is null.
 * @param enabled whether the button is enabled.
 * Default value is always true.
 * @param onClick the callback that is invoked when the button is clicked.
 * By default [EditorEvent.Sheet.Open] is invoked with sheet type [SheetType.Adjustments].
 * @param scope the scope of this component. Every new value will trigger recomposition of all functions with
 * signature @Composable Scope.() -> {}.
 * If you need to access [EditorScope] to construct the scope, use [LocalEditorScope].
 * By default it is updated only when the parent component scope ([InspectorBar.scope], accessed via [LocalEditorScope]) is updated.
 * @param visible whether the button should be visible.
 * By default the value is true when the selected design block has a fill type [FillType.Image] (where kind != "sticker") or [FillType.Video]
 * and an enabled engine scope "appearance/adjustments".
 * @param enterTransition transition of the button when it enters the parent composable.
 * Default value is always no enter transition.
 * @param exitTransition transition of the button when it exits the parent composable.
 * Default value is always no exit transition.
 * @param decoration decoration of the button. Useful when you want to add custom background, foreground, shadow, paddings etc.
 * Default value is always no decoration.
 * @return a button that will be displayed in the inspector bar.
 */
@Composable
fun Button.Companion.rememberAdjustments(
    vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.Adjustments },
    text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_adjustments) },
    tint: (@Composable ButtonScope.() -> Color)? = null,
    enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
    onClick: ButtonScope.() -> Unit = {
        editorContext.eventHandler.send(EditorEvent.Sheet.Open(SheetType.Adjustments()))
    },
    scope: ButtonScope =
        (LocalEditorScope.current as InspectorBar.Scope).run {
            rememberLastValue(this) {
                if (editorContext.safeSelection == null) lastValue else ButtonScope(parentScope = this@run)
            }
        },
    visible: @Composable ButtonScope.() -> Boolean = {
        remember(this) {
            (
                (editorContext.selection.fillType == FillType.Image && editorContext.selection.kind != KIND_STICKER) ||
                    editorContext.selection.fillType == FillType.Video
            ) &&
                editorContext.engine.block.isAllowedByScope(
                    editorContext.selection.designBlock,
                    "appearance/adjustments",
                )
        }
    },
    enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
    exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
    decoration: @Composable ButtonScope.(@Composable () -> Unit) -> Unit = { it() },
    `_`: Nothing = nothing,
): Button =
    remember(
        id = Button.Id.adjustments,
        vectorIcon = vectorIcon,
        text = text,
        tint = tint,
        enabled = enabled,
        onClick = onClick,
        scope = scope,
        visible = visible,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        decoration = decoration,
        `_` = `_`,
    )

/**
 * The id of the inspector bar button returned by [InspectorBar.Button.Companion.rememberFilter].
 */
val Button.Id.Companion.filter by unsafeLazy {
    EditorComponentId("ly.img.component.inspectorBar.button.filter")
}

/**
 * A helper function that returns an [InspectorBar.Button] that opens filter sheet via [EditorEvent.Sheet.Open].
 *
 * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
 * Default value is always [IconPack.Filter].
 * @param text the text content of the button as a string. If null then text is not rendered.
 * Default value is always [R.string.ly_img_editor_filter].
 * @param tint the tint color of the content. If null then no tint is applied.
 * Default value is null.
 * @param enabled whether the button is enabled.
 * Default value is always true.
 * @param onClick the callback that is invoked when the button is clicked.
 * By default [EditorEvent.Sheet.Open] is invoked with sheet type [SheetType.Filter].
 * @param scope the scope of this component. Every new value will trigger recomposition of all functions with
 * signature @Composable Scope.() -> {}.
 * If you need to access [EditorScope] to construct the scope, use [LocalEditorScope].
 * By default it is updated only when the parent component scope ([InspectorBar.scope], accessed via [LocalEditorScope]) is updated.
 * @param visible whether the button should be visible.
 * By default the value is true when the selected design block has a fill type [FillType.Image] (where kind != "sticker") or [FillType.Video]
 * and an enabled engine scope "appearance/filter".
 * @param enterTransition transition of the button when it enters the parent composable.
 * Default value is always no enter transition.
 * @param exitTransition transition of the button when it exits the parent composable.
 * Default value is always no exit transition.
 * @param decoration decoration of the button. Useful when you want to add custom background, foreground, shadow, paddings etc.
 * Default value is always no decoration.
 * @return a button that will be displayed in the inspector bar.
 */
@Composable
fun Button.Companion.rememberFilter(
    vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.Filter },
    text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_filter) },
    tint: (@Composable ButtonScope.() -> Color)? = null,
    enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
    onClick: ButtonScope.() -> Unit = {
        editorContext.eventHandler.send(EditorEvent.Sheet.Open(SheetType.Filter()))
    },
    scope: ButtonScope =
        (LocalEditorScope.current as InspectorBar.Scope).run {
            rememberLastValue(this) {
                if (editorContext.safeSelection == null) lastValue else ButtonScope(parentScope = this@run)
            }
        },
    visible: @Composable ButtonScope.() -> Boolean = {
        remember(this) {
            (
                (editorContext.selection.fillType == FillType.Image && editorContext.selection.kind != KIND_STICKER) ||
                    editorContext.selection.fillType == FillType.Video
            ) &&
                editorContext.engine.block.isAllowedByScope(
                    editorContext.selection.designBlock,
                    "appearance/filter",
                )
        }
    },
    enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
    exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
    decoration: @Composable ButtonScope.(@Composable () -> Unit) -> Unit = { it() },
    `_`: Nothing = nothing,
): Button =
    remember(
        id = Button.Id.filter,
        vectorIcon = vectorIcon,
        text = text,
        tint = tint,
        enabled = enabled,
        onClick = onClick,
        scope = scope,
        visible = visible,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        decoration = decoration,
        `_` = `_`,
    )

/**
 * The id of the inspector bar button returned by [InspectorBar.Button.Companion.rememberEffect].
 */
val Button.Id.Companion.effect by unsafeLazy {
    EditorComponentId("ly.img.component.inspectorBar.button.effect")
}

/**
 * A helper function that returns an [InspectorBar.Button] that opens effect sheet via [EditorEvent.Sheet.Open].
 *
 * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
 * Default value is always [IconPack.Effect].
 * @param text the text content of the button as a string. If null then text is not rendered.
 * Default value is always [R.string.ly_img_editor_effect].
 * @param tint the tint color of the content. If null then no tint is applied.
 * Default value is null.
 * @param enabled whether the button is enabled.
 * Default value is always true.
 * @param onClick the callback that is invoked when the button is clicked.
 * By default [EditorEvent.Sheet.Open] is invoked with sheet type [SheetType.Effect].
 * @param scope the scope of this component. Every new value will trigger recomposition of all functions with
 * signature @Composable Scope.() -> {}.
 * If you need to access [EditorScope] to construct the scope, use [LocalEditorScope].
 * By default it is updated only when the parent component scope ([InspectorBar.scope], accessed via [LocalEditorScope]) is updated.
 * @param visible whether the button should be visible.
 * * By default the value is true when the selected design block has a fill type [FillType.Image] (where kind != "sticker") or [FillType.Video]
 * and an enabled engine scope "appearance/effect".
 * @param enterTransition transition of the button when it enters the parent composable.
 * Default value is always no enter transition.
 * @param exitTransition transition of the button when it exits the parent composable.
 * Default value is always no exit transition.
 * @param decoration decoration of the button. Useful when you want to add custom background, foreground, shadow, paddings etc.
 * Default value is always no decoration.
 * @return a button that will be displayed in the inspector bar.
 */
@Composable
fun Button.Companion.rememberEffect(
    vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.Effect },
    text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_effect) },
    tint: (@Composable ButtonScope.() -> Color)? = null,
    enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
    onClick: ButtonScope.() -> Unit = {
        editorContext.eventHandler.send(EditorEvent.Sheet.Open(SheetType.Effect()))
    },
    scope: ButtonScope =
        (LocalEditorScope.current as InspectorBar.Scope).run {
            rememberLastValue(this) {
                if (editorContext.safeSelection == null) lastValue else ButtonScope(parentScope = this@run)
            }
        },
    visible: @Composable ButtonScope.() -> Boolean = {
        remember(this) {
            (
                (editorContext.selection.fillType == FillType.Image && editorContext.selection.kind != KIND_STICKER) ||
                    editorContext.selection.fillType == FillType.Video
            ) &&
                editorContext.engine.block.isAllowedByScope(
                    editorContext.selection.designBlock,
                    "appearance/effect",
                )
        }
    },
    enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
    exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
    decoration: @Composable ButtonScope.(@Composable () -> Unit) -> Unit = { it() },
    `_`: Nothing = nothing,
): Button =
    remember(
        id = Button.Id.effect,
        vectorIcon = vectorIcon,
        text = text,
        tint = tint,
        enabled = enabled,
        onClick = onClick,
        scope = scope,
        visible = visible,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        decoration = decoration,
        `_` = `_`,
    )

/**
 * The id of the inspector bar button returned by [InspectorBar.Button.Companion.rememberBlur].
 */
val Button.Id.Companion.blur by unsafeLazy {
    EditorComponentId("ly.img.component.inspectorBar.button.blur")
}

/**
 * A helper function that returns an [InspectorBar.Button] that opens blur sheet via [EditorEvent.Sheet.Open].
 *
 * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
 * Default value is always [IconPack.Blur].
 * @param text the text content of the button as a string. If null then text is not rendered.
 * Default value is always [R.string.ly_img_editor_blur].
 * @param tint the tint color of the content. If null then no tint is applied.
 * Default value is null.
 * @param enabled whether the button is enabled.
 * Default value is always true.
 * @param onClick the callback that is invoked when the button is clicked.
 * By default [EditorEvent.Sheet.Open] is invoked with sheet type [SheetType.Blur].
 * @param scope the scope of this component. Every new value will trigger recomposition of all functions with
 * signature @Composable Scope.() -> {}.
 * If you need to access [EditorScope] to construct the scope, use [LocalEditorScope].
 * By default it is updated only when the parent component scope ([InspectorBar.scope], accessed via [LocalEditorScope]) is updated.
 * @param visible whether the button should be visible.
 * By default the value is true when the selected design block has a fill type [FillType.Image] (where kind != "sticker") or [FillType.Video]
 * and an enabled engine scope "appearance/blur".
 * @param enterTransition transition of the button when it enters the parent composable.
 * Default value is always no enter transition.
 * @param exitTransition transition of the button when it exits the parent composable.
 * Default value is always no exit transition.
 * @param decoration decoration of the button. Useful when you want to add custom background, foreground, shadow, paddings etc.
 * Default value is always no decoration.
 * @return a button that will be displayed in the inspector bar.
 */
@Composable
fun Button.Companion.rememberBlur(
    vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.Blur },
    text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_blur) },
    tint: (@Composable ButtonScope.() -> Color)? = null,
    enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
    onClick: ButtonScope.() -> Unit = {
        editorContext.eventHandler.send(EditorEvent.Sheet.Open(SheetType.Blur()))
    },
    scope: ButtonScope =
        (LocalEditorScope.current as InspectorBar.Scope).run {
            rememberLastValue(this) {
                if (editorContext.safeSelection == null) lastValue else ButtonScope(parentScope = this@run)
            }
        },
    visible: @Composable ButtonScope.() -> Boolean = {
        remember(this) {
            (
                (editorContext.selection.fillType == FillType.Image && editorContext.selection.kind != KIND_STICKER) ||
                    editorContext.selection.fillType == FillType.Video
            ) &&
                editorContext.engine.block.isAllowedByScope(
                    editorContext.selection.designBlock,
                    "appearance/blur",
                )
        }
    },
    enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
    exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
    decoration: @Composable ButtonScope.(@Composable () -> Unit) -> Unit = { it() },
    `_`: Nothing = nothing,
): Button =
    remember(
        id = Button.Id.blur,
        vectorIcon = vectorIcon,
        text = text,
        tint = tint,
        enabled = enabled,
        onClick = onClick,
        scope = scope,
        visible = visible,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        decoration = decoration,
        `_` = `_`,
    )

/**
 * The id of the inspector bar button returned by [InspectorBar.Button.Companion.rememberVolume].
 */
val Button.Id.Companion.volume by unsafeLazy {
    EditorComponentId("ly.img.component.inspectorBar.button.volume")
}

/**
 * A helper function that returns an [InspectorBar.Button] that opens volume sheet via [EditorEvent.Sheet.Open].
 *
 * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
 * Default value is always [IconPack.VolumeHigh].
 * @param text the text content of the button as a string. If null then text is not rendered.
 * Default value is always [R.string.ly_img_editor_volume].
 * @param tint the tint color of the content. If null then no tint is applied.
 * Default value is null.
 * @param enabled whether the button is enabled.
 * Default value is always true.
 * @param onClick the callback that is invoked when the button is clicked.
 * By default [EditorEvent.Sheet.Open] is invoked with sheet type [SheetType.Volume].
 * @param scope the scope of this component. Every new value will trigger recomposition of all functions with
 * signature @Composable Scope.() -> {}.
 * If you need to access [EditorScope] to construct the scope, use [LocalEditorScope].
 * By default it is updated only when the parent component scope ([InspectorBar.scope], accessed via [LocalEditorScope]) is updated.
 * @param visible whether the button should be visible.
 * By default the value is true when the selected design block is [DesignBlockType.Audio] or has a fill type [FillType.Video] and
 * an enabled engine scope "fill/change".
 * @param enterTransition transition of the button when it enters the parent composable.
 * Default value is always no enter transition.
 * @param exitTransition transition of the button when it exits the parent composable.
 * Default value is always no exit transition.
 * @param decoration decoration of the button. Useful when you want to add custom background, foreground, shadow, paddings etc.
 * Default value is always no decoration.
 * @return a button that will be displayed in the inspector bar.
 */
@Composable
fun Button.Companion.rememberVolume(
    vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.VolumeHigh },
    text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_volume) },
    tint: (@Composable ButtonScope.() -> Color)? = null,
    enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
    onClick: ButtonScope.() -> Unit = {
        editorContext.eventHandler.send(EditorEvent.Sheet.Open(SheetType.Volume()))
    },
    scope: ButtonScope =
        (LocalEditorScope.current as InspectorBar.Scope).run {
            rememberLastValue(this) {
                if (editorContext.safeSelection == null) lastValue else ButtonScope(parentScope = this@run)
            }
        },
    visible: @Composable ButtonScope.() -> Boolean = {
        remember(this) {
            (editorContext.selection.type == DesignBlockType.Audio || editorContext.selection.fillType == FillType.Video) &&
                editorContext.engine.block.isAllowedByScope(editorContext.selection.designBlock, "fill/change")
        }
    },
    enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
    exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
    decoration: @Composable ButtonScope.(@Composable () -> Unit) -> Unit = { it() },
    `_`: Nothing = nothing,
): Button =
    remember(
        id = Button.Id.volume,
        vectorIcon = vectorIcon,
        text = text,
        tint = tint,
        enabled = enabled,
        onClick = onClick,
        scope = scope,
        visible = visible,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        decoration = decoration,
        `_` = `_`,
    )

/**
 * The id of the inspector bar button returned by [InspectorBar.Button.Companion.rememberCrop].
 */
val Button.Id.Companion.crop by unsafeLazy {
    EditorComponentId("ly.img.component.inspectorBar.button.crop")
}

/**
 * A helper function that returns an [InspectorBar.Button] that opens crop sheet via [EditorEvent.Sheet.Open].
 *
 * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
 * Default value is always [IconPack.CropRotate].
 * @param text the text content of the button as a string. If null then text is not rendered.
 * Default value is always [R.string.ly_img_editor_crop].
 * @param tint the tint color of the content. If null then no tint is applied.
 * Default value is null.
 * @param enabled whether the button is enabled.
 * Default value is always true.
 * @param onClick the callback that is invoked when the button is clicked.
 * By default [EditorEvent.Sheet.Open] is invoked with sheet type [SheetType.Crop].
 * @param scope the scope of this component. Every new value will trigger recomposition of all functions with
 * signature @Composable Scope.() -> {}.
 * If you need to access [EditorScope] to construct the scope, use [LocalEditorScope].
 * By default it is updated only when the parent component scope ([InspectorBar.scope], accessed via [LocalEditorScope]) is updated.
 * @param visible whether the button should be visible.
 * By default the value is true when the selected design block has a fill type [FillType.Image] (where kind != "sticker") or [FillType.Video],
 * [ly.img.engine.BlockApi.supportsCrop] is true and an enabled engine scope "fill/change".
 * @param enterTransition transition of the button when it enters the parent composable.
 * Default value is always no enter transition.
 * @param exitTransition transition of the button when it exits the parent composable.
 * Default value is always no exit transition.
 * @param decoration decoration of the button. Useful when you want to add custom background, foreground, shadow, paddings etc.
 * Default value is always no decoration.
 * @return a button that will be displayed in the inspector bar.
 */
@Composable
fun Button.Companion.rememberCrop(
    vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.CropRotate },
    text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_crop) },
    tint: (@Composable ButtonScope.() -> Color)? = null,
    enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
    onClick: ButtonScope.() -> Unit = {
        editorContext.eventHandler.send(EditorEvent.Sheet.Open(SheetType.Crop()))
    },
    scope: ButtonScope =
        (LocalEditorScope.current as InspectorBar.Scope).run {
            rememberLastValue(this) {
                if (editorContext.safeSelection == null) lastValue else ButtonScope(parentScope = this@run)
            }
        },
    visible: @Composable ButtonScope.() -> Boolean = {
        remember(this) {
            val designBlock = editorContext.selection.designBlock
            (
                (editorContext.selection.fillType == FillType.Image && editorContext.selection.kind != KIND_STICKER) ||
                    editorContext.selection.fillType == FillType.Video
            ) &&
                editorContext.engine.block.supportsCrop(designBlock) &&
                editorContext.engine.block.isAllowedByScope(designBlock, "layer/crop")
        }
    },
    enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
    exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
    decoration: @Composable ButtonScope.(@Composable () -> Unit) -> Unit = { it() },
    `_`: Nothing = nothing,
): Button =
    remember(
        id = Button.Id.crop,
        vectorIcon = vectorIcon,
        text = text,
        tint = tint,
        enabled = enabled,
        onClick = onClick,
        scope = scope,
        visible = visible,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        decoration = decoration,
        `_` = `_`,
    )

/**
 * The id of the inspector bar button returned by [InspectorBar.Button.Companion.rememberDuplicate].
 */
val Button.Id.Companion.duplicate by unsafeLazy {
    EditorComponentId("ly.img.component.inspectorBar.button.duplicate")
}

/**
 * A helper function that returns an [InspectorBar.Button] that duplicates currently selected
 * design block via [EditorEvent.Selection.Duplicate].
 *
 * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
 * Default value is always [IconPack.Duplicate].
 * @param text the text content of the button as a string. If null then text is not rendered.
 * Default value is always [R.string.ly_img_editor_duplicate].
 * @param tint the tint color of the content. If null then no tint is applied.
 * Default value is null.
 * @param enabled whether the button is enabled.
 * Default value is always true.
 * @param onClick the callback that is invoked when the button is clicked.
 * By default [EditorEvent.Selection.Duplicate] is invoked.
 * @param scope the scope of this component. Every new value will trigger recomposition of all functions with
 * signature @Composable Scope.() -> {}.
 * If you need to access [EditorScope] to construct the scope, use [LocalEditorScope].
 * By default it is updated only when the parent component scope ([InspectorBar.scope], accessed via [LocalEditorScope]) is updated.
 * @param visible whether the button should be visible.
 * By default the value is true when the selected design block type is not [DesignBlockType.Page]
 * and when [isDuplicateAllowed] returns true.
 * @param enterTransition transition of the button when it enters the parent composable.
 * Default value is always no enter transition.
 * @param exitTransition transition of the button when it exits the parent composable.
 * Default value is always no exit transition.
 * @param decoration decoration of the button. Useful when you want to add custom background, foreground, shadow, paddings etc.
 * Default value is always no decoration.
 * @return a button that will be displayed in the inspector bar.
 */
@Composable
fun Button.Companion.rememberDuplicate(
    vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.Duplicate },
    text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_duplicate) },
    tint: (@Composable ButtonScope.() -> Color)? = null,
    enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
    onClick: ButtonScope.() -> Unit = {
        editorContext.eventHandler.send(EditorEvent.Selection.Duplicate())
    },
    scope: ButtonScope =
        (LocalEditorScope.current as InspectorBar.Scope).run {
            rememberLastValue(this) {
                if (editorContext.safeSelection == null) lastValue else ButtonScope(parentScope = this@run)
            }
        },
    visible: @Composable ButtonScope.() -> Boolean = {
        remember(this) {
            editorContext.selection.type != DesignBlockType.Page &&
                editorContext.engine.isDuplicateAllowed(editorContext.selection)
        }
    },
    enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
    exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
    decoration: @Composable ButtonScope.(@Composable () -> Unit) -> Unit = { it() },
    `_`: Nothing = nothing,
): Button =
    remember(
        id = Button.Id.duplicate,
        vectorIcon = vectorIcon,
        text = text,
        tint = tint,
        enabled = enabled,
        onClick = onClick,
        scope = scope,
        visible = visible,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        decoration = decoration,
        `_` = `_`,
    )

/**
 * The id of the inspector bar button returned by [InspectorBar.Button.Companion.rememberLayer].
 */
val Button.Id.Companion.layer by unsafeLazy {
    EditorComponentId("ly.img.component.inspectorBar.button.layer")
}

/**
 * A helper function that returns an [InspectorBar.Button] that opens layer sheet via [EditorEvent.Sheet.Open].
 *
 * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
 * Default value is always [IconPack.LayersOutline].
 * @param text the text content of the button as a string. If null then text is not rendered.
 * Default value is always [R.string.ly_img_editor_layer].
 * @param tint the tint color of the content. If null then no tint is applied.
 * Default value is null.
 * @param enabled whether the button is enabled.
 * Default value is always true.
 * @param onClick the callback that is invoked when the button is clicked.
 * By default [EditorEvent.Sheet.Open] is invoked with sheet type [SheetType.Layer].
 * @param scope the scope of this component. Every new value will trigger recomposition of all functions with
 * signature @Composable Scope.() -> {}.
 * If you need to access [EditorScope] to construct the scope, use [LocalEditorScope].
 * By default it is updated only when the parent component scope ([InspectorBar.scope], accessed via [LocalEditorScope]) is updated.
 * @param visible whether the button should be visible.
 * By default the value is true when the selected design block type is not [DesignBlockType.Page] or [DesignBlockType.Audio],
 * has enabled engine scopes "layer/blendMode" and "layer/opacity" and finally, at least one of
 * [isMoveAllowed], [isDeleteAllowed], [isDuplicateAllowed] functions returns true.
 * @param enterTransition transition of the button when it enters the parent composable.
 * Default value is always no enter transition.
 * @param exitTransition transition of the button when it exits the parent composable.
 * Default value is always no exit transition.
 * @param decoration decoration of the button. Useful when you want to add custom background, foreground, shadow, paddings etc.
 * Default value is always no decoration.
 * @return a button that will be displayed in the inspector bar.
 */
@Composable
fun Button.Companion.rememberLayer(
    vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.LayersOutline },
    text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_layer) },
    tint: (@Composable ButtonScope.() -> Color)? = null,
    enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
    onClick: ButtonScope.() -> Unit = {
        editorContext.eventHandler.send(EditorEvent.Sheet.Open(SheetType.Layer()))
    },
    scope: ButtonScope =
        (LocalEditorScope.current as InspectorBar.Scope).run {
            rememberLastValue(this) {
                if (editorContext.safeSelection == null) lastValue else ButtonScope(parentScope = this@run)
            }
        },
    visible: @Composable ButtonScope.() -> Boolean = {
        remember(this) {
            val selection = editorContext.selection
            selection.type != DesignBlockType.Page &&
                selection.type != DesignBlockType.Audio && (
                    editorContext.engine.block.isAllowedByScope(selection.designBlock, "layer/blendMode") ||
                        editorContext.engine.block.isAllowedByScope(selection.designBlock, "layer/opacity") ||
                        editorContext.engine.isMoveAllowed(selection) ||
                        editorContext.engine.isDeleteAllowed(selection) ||
                        editorContext.engine.isDuplicateAllowed(selection)
                )
        }
    },
    enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
    exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
    decoration: @Composable ButtonScope.(@Composable () -> Unit) -> Unit = { it() },
    `_`: Nothing = nothing,
): Button =
    remember(
        id = Button.Id.layer,
        vectorIcon = vectorIcon,
        text = text,
        tint = tint,
        enabled = enabled,
        onClick = onClick,
        scope = scope,
        visible = visible,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        decoration = decoration,
        `_` = `_`,
    )

/**
 * The id of the inspector bar button returned by [InspectorBar.Button.Companion.rememberSplit].
 */
val Button.Id.Companion.split by unsafeLazy {
    EditorComponentId("ly.img.component.inspectorBar.button.split")
}

/**
 * A helper function that returns an [InspectorBar.Button] that splits currently selected
 * design block via [EditorEvent.Selection.Split] in a video scene.
 *
 * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
 * Default value is always [IconPack.Split].
 * @param text the text content of the button as a string. If null then text is not rendered.
 * Default value is always [R.string.ly_img_editor_split].
 * @param tint the tint color of the content. If null then no tint is applied.
 * Default value is null.
 * @param enabled whether the button is enabled.
 * Default value is always true.
 * @param onClick the callback that is invoked when the button is clicked.
 * By default [EditorEvent.Selection.Split] is invoked.
 * @param scope the scope of this component. Every new value will trigger recomposition of all functions with
 * signature @Composable Scope.() -> {}.
 * If you need to access [EditorScope] to construct the scope, use [LocalEditorScope].
 * By default it is updated only when the parent component scope ([InspectorBar.scope], accessed via [LocalEditorScope]) is updated.
 * @param visible whether the button should be visible.
 * By default the value is true when the selected design block has an enabled engine scope "lifecycle/duplicate"
 * and the [SceneMode] is video.
 * @param enterTransition transition of the button when it enters the parent composable.
 * Default value is always no enter transition.
 * @param exitTransition transition of the button when it exits the parent composable.
 * Default value is always no exit transition.
 * @param decoration decoration of the button. Useful when you want to add custom background, foreground, shadow, paddings etc.
 * Default value is always no decoration.
 * @return a button that will be displayed in the inspector bar.
 */
@Composable
fun Button.Companion.rememberSplit(
    vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.Split },
    text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_split) },
    tint: (@Composable ButtonScope.() -> Color)? = null,
    enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
    onClick: ButtonScope.() -> Unit = {
        editorContext.eventHandler.send(EditorEvent.Selection.Split())
    },
    scope: ButtonScope =
        (LocalEditorScope.current as InspectorBar.Scope).run {
            rememberLastValue(this) {
                if (editorContext.safeSelection == null) lastValue else ButtonScope(parentScope = this@run)
            }
        },
    visible: @Composable ButtonScope.() -> Boolean = {
        remember(this) {
            editorContext.engine.block.isAllowedByScope(
                editorContext.selection.designBlock,
                "lifecycle/duplicate",
            ) && editorContext.engine.scene.getMode() == SceneMode.VIDEO
        }
    },
    enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
    exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
    decoration: @Composable ButtonScope.(@Composable () -> Unit) -> Unit = { it() },
    `_`: Nothing = nothing,
): Button =
    remember(
        id = Button.Id.split,
        vectorIcon = vectorIcon,
        text = text,
        tint = tint,
        enabled = enabled,
        onClick = onClick,
        scope = scope,
        visible = visible,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        decoration = decoration,
        `_` = `_`,
    )

/**
 * An extension function that constructs [EditorIcon.FillStroke] icon from [designBlock] based on
 * the engine state.
 *
 * @param designBlock the design block to construct the icon from.
 */
private fun Engine.getFillStrokeButtonIcon(designBlock: DesignBlock): EditorIcon.FillStroke {
    fun RGBAColor.toComposeColor(): Color {
        return Color(
            red = this.r,
            green = this.g,
            blue = this.b,
            alpha = this.a,
        )
    }

    fun ly.img.engine.Color.toRGBColor(engine: Engine): RGBAColor {
        return this as? RGBAColor
            ?: engine.editor.convertColorToColorSpace(this, ColorSpace.SRGB) as RGBAColor
    }

    fun BlockApi.getFillType(designBlock: DesignBlock): FillType? =
        if (!this.supportsFill(designBlock)) {
            null
        } else {
            FillType.get(this.getType(this.getFill(designBlock)))
        }

    fun BlockApi.hasColorOrGradientFill(designBlock: DesignBlock): Boolean {
        val fillType = getFillType(designBlock)
        return fillType == FillType.Color ||
            fillType == FillType.LinearGradient ||
            fillType == FillType.RadialGradient ||
            fillType == FillType.ConicalGradient
    }

    fun Engine.getFill(designBlock: DesignBlock): Fill? {
        return if (!block.supportsFill(designBlock)) {
            null
        } else {
            when (block.getFillType(designBlock)) {
                FillType.Color -> {
                    val rgbaColor =
                        if (DesignBlockType.getOrNull(block.getType(designBlock)) == DesignBlockType.Text) {
                            block.getTextColors(designBlock).first().toRGBColor(this)
                        } else {
                            block.getColor(designBlock, "fill/solid/color") as RGBAColor
                        }
                    SolidFill(rgbaColor.toComposeColor())
                }

                FillType.LinearGradient -> {
                    val fill = block.getFill(designBlock)
                    LinearGradientFill(
                        startPointX = block.getFloat(fill, "fill/gradient/linear/startPointX"),
                        startPointY = block.getFloat(fill, "fill/gradient/linear/startPointY"),
                        endPointX = block.getFloat(fill, "fill/gradient/linear/endPointX"),
                        endPointY = block.getFloat(fill, "fill/gradient/linear/endPointY"),
                        colorStops = block.getGradientColorStops(fill, "fill/gradient/colors"),
                    )
                }

                FillType.RadialGradient -> {
                    val fill = block.getFill(designBlock)
                    RadialGradientFill(
                        centerX = block.getFloat(fill, "fill/gradient/radial/centerPointX"),
                        centerY = block.getFloat(fill, "fill/gradient/radial/centerPointY"),
                        radius = block.getFloat(fill, "fill/gradient/radial/radius"),
                        colorStops = block.getGradientColorStops(fill, "fill/gradient/colors"),
                    )
                }

                FillType.ConicalGradient -> {
                    val fill = block.getFill(designBlock)
                    ConicalGradientFill(
                        centerX = block.getFloat(fill, "fill/gradient/conical/centerPointX"),
                        centerY = block.getFloat(fill, "fill/gradient/conical/centerPointY"),
                        colorStops = block.getGradientColorStops(fill, "fill/gradient/colors"),
                    )
                }

                // Image fill and Video fill are not supported yet
                else -> null
            }
        }
    }

    fun Engine.getStrokeColor(designBlock: DesignBlock): Color? {
        if (!block.supportsStroke(designBlock)) return null
        return block.getColor(designBlock, "stroke/color")
            .toRGBColor(this)
            .toComposeColor()
    }
    val showFill =
        block.supportsFill(designBlock) &&
            block.hasColorOrGradientFill(designBlock) &&
            block.isAllowedByScope(designBlock, "fill/change")
    val showStroke = block.supportsStroke(designBlock) && block.isAllowedByScope(designBlock, "stroke/change")
    return EditorIcon.FillStroke(
        showFill = showFill,
        showStroke = showStroke,
        fill =
            if (showFill && block.isFillEnabled(designBlock)) {
                getFill(designBlock)
            } else {
                null
            },
        stroke =
            if (showStroke && block.isStrokeEnabled(designBlock)) {
                getStrokeColor(designBlock)
            } else {
                null
            },
    )
}

/**
 * The id of the inspector bar button returned by [InspectorBar.Button.Companion.rememberFillStroke].
 */
val Button.Id.Companion.fillStroke by unsafeLazy {
    EditorComponentId("ly.img.component.inspectorBar.button.fillStroke")
}

/**
 * A helper function that returns an [InspectorBar.Button] that opens fill stroke sheet via [EditorEvent.Sheet.Open].
 * [InspectorBar.FillStrokeButtonScope] is constructed using [getFillStrokeButtonIcon] function.
 *
 * @param icon the icon content of the button. If null then icon is not rendered.
 * Default value is an [EditorIcon], which is built based on [InspectorBar.FillStrokeButtonScope].
 * @param text the text content of the button as a string. If null then text is not rendered.
 * Default value is a text, that is built based on [InspectorBar.FillStrokeButtonScope].
 * @param tint the tint color of the content. If null then no tint is applied.
 * Default value is null.
 * @param enabled whether the button is enabled.
 * Default value is always true.
 * @param onClick the callback that is invoked when the button is clicked.
 * By default [EditorEvent.Sheet.Open] is invoked with sheet type [SheetType.FillStroke].
 * @param scope the scope of this component. Every new value will trigger recomposition of all functions with
 * signature @Composable Scope.() -> {}.
 * If you need to access [EditorScope] to construct the scope, use [LocalEditorScope].
 * By default it is updated only when the parent component scope ([InspectorBar.scope], accessed via [LocalEditorScope]) is updated.
 * @param visible whether the button should be visible.
 * By default the value is true when the selected design block does not have a fill type [FillType.Image], or has but the kind
 * is not "sticker" and [InspectorBar.FillStrokeButtonScope.fillStrokeIcon] has showFill == true or showStroke == true.
 * @param enterTransition transition of the button when it enters the parent composable.
 * Default value is always no enter transition.
 * @param exitTransition transition of the button when it exits the parent composable.
 * Default value is always no exit transition.
 * @param decoration decoration of the button. Useful when you want to add custom background, foreground, shadow, paddings etc.
 * Default value is always no decoration.
 * @return a button that will be displayed in the inspector bar.
 */
@Composable
fun Button.Companion.rememberFillStroke(
    icon: (@Composable ButtonScope.() -> Unit)? = {
        val fillStrokeIcon = (this as InspectorBar.FillStrokeButtonScope).editorContext.fillStrokeIcon
        EditorIcon(fillStrokeIcon)
    },
    text: (@Composable ButtonScope.() -> String)? = {
        val textResId =
            remember(this) {
                val fillStrokeIcon = (this as InspectorBar.FillStrokeButtonScope).editorContext.fillStrokeIcon
                if (fillStrokeIcon.showFill && fillStrokeIcon.showStroke) {
                    R.string.ly_img_editor_fill_and_stroke
                } else if (fillStrokeIcon.showFill) {
                    R.string.ly_img_editor_fill
                } else {
                    R.string.ly_img_editor_stroke
                }
            }
        stringResource(textResId)
    },
    tint: (@Composable ButtonScope.() -> Color)? = null,
    enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
    onClick: ButtonScope.() -> Unit = {
        editorContext.eventHandler.send(EditorEvent.Sheet.Open(SheetType.FillStroke()))
    },
    scope: ButtonScope =
        (LocalEditorScope.current as InspectorBar.Scope).run {
            val parentScope =
                rememberLastValue(this) {
                    if (editorContext.safeSelection == null) lastValue else this@run
                }
            val initial =
                remember(parentScope) {
                    editorContext.engine.getFillStrokeButtonIcon(editorContext.selection.designBlock)
                }
            val fillStrokeIcon by remember(parentScope) {
                val selection = parentScope.editorContext.selection
                editorContext.engine.event.subscribe(listOf(selection.designBlock))
                    .filter {
                        // When the design block is unselected/deleted, this lambda is entered before parent scope is updated.
                        // We need to make sure that current component does not update if engine selection has changed.
                        selection.designBlock == editorContext.engine.block.findAllSelected().firstOrNull()
                    }
                    .map { editorContext.engine.getFillStrokeButtonIcon(selection.designBlock) }
                    .onStart { emit(initial) }
            }.collectAsState(initial = initial)
            remember(parentScope, fillStrokeIcon) {
                InspectorBar.FillStrokeButtonScope(parentScope = this, fillStrokeIcon = fillStrokeIcon)
            }
        },
    visible: @Composable ButtonScope.() -> Boolean = {
        remember(this) {
            val fillStrokeIcon = (this as InspectorBar.FillStrokeButtonScope).editorContext.fillStrokeIcon
            (editorContext.selection.fillType != FillType.Image || editorContext.selection.kind != KIND_STICKER) &&
                (fillStrokeIcon.showFill || fillStrokeIcon.showStroke)
        }
    },
    enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
    exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
    decoration: @Composable ButtonScope.(@Composable () -> Unit) -> Unit = { it() },
    `_`: Nothing = nothing,
): Button =
    remember(
        id = Button.Id.fillStroke,
        icon = icon,
        text =
            text?.let {
                {
                    Text(
                        text = text(this),
                        style = MaterialTheme.typography.labelSmall,
                        color = tint?.invoke(this) ?: Color.Unspecified,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            },
        enabled = enabled,
        onClick = onClick,
        scope = scope,
        visible = visible,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        decoration = decoration,
        `_` = `_`,
    )

/**
 * The id of the inspector bar button returned by [InspectorBar.Button.Companion.rememberMoveAsClip].
 */
val Button.Id.Companion.moveAsClip by unsafeLazy {
    EditorComponentId("ly.img.component.inspectorBar.button.moveAsClip")
}

/**
 * A helper function that returns an [InspectorBar.Button] that moves currently selected design block into
 * the background track as clip via [EditorEvent.Selection.MoveAsClip].
 *
 * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
 * Default value is always [IconPack.AsClip].
 * @param text the text content of the button as a string. If null then text is not rendered.
 * Default value is always [R.string.ly_img_editor_attach_background].
 * @param tint the tint color of the content. If null then no tint is applied.
 * Default value is null.
 * @param enabled whether the button is enabled.
 * Default value is always true.
 * @param onClick the callback that is invoked when the button is clicked.
 * By default [EditorEvent.Selection.MoveAsClip] is invoked.
 * @param scope the scope of this component. Every new value will trigger recomposition of all functions with
 * signature @Composable Scope.() -> {}.
 * If you need to access [EditorScope] to construct the scope, use [LocalEditorScope].
 * By default it is updated only when the parent component scope ([InspectorBar.scope], accessed via [LocalEditorScope]) is updated.
 * @param visible whether the button should be visible.
 * By default the value is true when the selected design block does not have a type [DesignBlockType.Audio],
 * the [SceneMode] is video and the parent is not a background track.
 * @param enterTransition transition of the button when it enters the parent composable.
 * Default value is always no enter transition.
 * @param exitTransition transition of the button when it exits the parent composable.
 * Default value is always no exit transition.
 * @param decoration decoration of the button. Useful when you want to add custom background, foreground, shadow, paddings etc.
 * Default value is always no decoration.
 * @return a button that will be displayed in the inspector bar.
 */
@Composable
fun Button.Companion.rememberMoveAsClip(
    vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.AsClip },
    text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_attach_background) },
    tint: (@Composable ButtonScope.() -> Color)? = null,
    enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
    onClick: ButtonScope.() -> Unit = {
        editorContext.eventHandler.send(EditorEvent.Selection.MoveAsClip())
    },
    scope: ButtonScope =
        (LocalEditorScope.current as InspectorBar.Scope).run {
            rememberLastValue(this) {
                if (editorContext.safeSelection == null) lastValue else ButtonScope(parentScope = this@run)
            }
        },
    visible: @Composable ButtonScope.() -> Boolean = {
        remember(this) {
            editorContext.selection.type != DesignBlockType.Audio &&
                editorContext.engine.scene.getMode() == SceneMode.VIDEO &&
                editorContext.selection.parentDesignBlock.let {
                    it != null && editorContext.engine.isBackgroundTrack(it).not()
                }
        }
    },
    enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
    exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
    decoration: @Composable ButtonScope.(@Composable () -> Unit) -> Unit = { it() },
    `_`: Nothing = nothing,
): Button =
    remember(
        id = Button.Id.moveAsClip,
        vectorIcon = vectorIcon,
        text = text,
        tint = tint,
        enabled = enabled,
        onClick = onClick,
        scope = scope,
        visible = visible,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        decoration = decoration,
        `_` = `_`,
    )

/**
 * The id of the inspector bar button returned by [InspectorBar.Button.Companion.rememberMoveAsOverlay].
 */
val Button.Id.Companion.moveAsOverlay by unsafeLazy {
    EditorComponentId("ly.img.component.inspectorBar.button.moveAsOverlay")
}

/**
 * A helper function that returns an [InspectorBar.Button] that moves currently selected design block from
 * the background track to an overlay via [EditorEvent.Selection.MoveAsOverlay]
 *
 * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
 * Default value is always [IconPack.AsOverlay].
 * @param text the text content of the button as a string. If null then text is not rendered.
 * Default value is always [R.string.ly_img_editor_detach_background].
 * @param tint the tint color of the content. If null then no tint is applied.
 * Default value is null.
 * @param enabled whether the button is enabled.
 * Default value is always true.
 * @param onClick the callback that is invoked when the button is clicked.
 * By default [EditorEvent.Selection.MoveAsOverlay] is invoked.
 * @param scope the scope of this component. Every new value will trigger recomposition of all functions with
 * signature @Composable Scope.() -> {}.
 * If you need to access [EditorScope] to construct the scope, use [LocalEditorScope].
 * By default it is updated only when the parent component scope ([InspectorBar.scope], accessed via [LocalEditorScope]) is updated.
 * @param visible whether the button should be visible.
 * By default the value is true when the selected design block does not have a type [DesignBlockType.Audio],
 * the [SceneMode] is video and the parent is a background track.
 * @param enterTransition transition of the button when it enters the parent composable.
 * Default value is always no enter transition.
 * @param exitTransition transition of the button when it exits the parent composable.
 * Default value is always no exit transition.
 * @param decoration decoration of the button. Useful when you want to add custom background, foreground, shadow, paddings etc.
 * Default value is always no decoration.
 * @return a button that will be displayed in the inspector bar.
 */
@Composable
fun Button.Companion.rememberMoveAsOverlay(
    vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.AsOverlay },
    text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_detach_background) },
    tint: (@Composable ButtonScope.() -> Color)? = null,
    enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
    onClick: ButtonScope.() -> Unit = {
        editorContext.eventHandler.send(EditorEvent.Selection.MoveAsOverlay())
    },
    scope: ButtonScope =
        (LocalEditorScope.current as InspectorBar.Scope).run {
            rememberLastValue(this) {
                if (editorContext.safeSelection == null) lastValue else ButtonScope(parentScope = this@run)
            }
        },
    visible: @Composable ButtonScope.() -> Boolean = {
        remember(this) {
            editorContext.selection.type != DesignBlockType.Audio &&
                editorContext.engine.scene.getMode() == SceneMode.VIDEO &&
                editorContext.selection.parentDesignBlock.let {
                    it != null && editorContext.engine.isBackgroundTrack(it)
                }
        }
    },
    enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
    exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
    decoration: @Composable ButtonScope.(@Composable () -> Unit) -> Unit = { it() },
    `_`: Nothing = nothing,
): Button =
    remember(
        id = Button.Id.moveAsOverlay,
        vectorIcon = vectorIcon,
        text = text,
        tint = tint,
        enabled = enabled,
        onClick = onClick,
        scope = scope,
        visible = visible,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        decoration = decoration,
        `_` = `_`,
    )

/**
 * The id of the inspector bar button returned by [InspectorBar.Button.Companion.rememberReplace].
 */
val Button.Id.Companion.replace by unsafeLazy {
    EditorComponentId("ly.img.component.inspectorBar.button.replace")
}

/**
 * A helper function that returns an [InspectorBar.Button] that opens a library sheet via [EditorEvent.Sheet.Open].
 * Selected asset will replace the content of the currently selected design block.
 * By default [DesignBlockType], [FillType] and kind of the selected design block are used to find the library in
 * [ly.img.editor.core.library.AssetLibrary].
 *
 * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
 * Default value is always [IconPack.Replace].
 * @param text the text content of the button as a string. If null then text is not rendered.
 * Default value is always [R.string.ly_img_editor_replace].
 * @param tint the tint color of the content. If null then no tint is applied.
 * Default value is null.
 * @param enabled whether the button is enabled.
 * Default value is always true.
 * @param onClick the callback that is invoked when the button is clicked.
 * By default [EditorEvent.Sheet.Open] is invoked with sheet type [SheetType.LibraryReplace] where
 * the libraryCategory is picked from the [ly.img.editor.core.library.AssetLibrary] based on the [DesignBlockType], [FillType] and kind
 * of the selected block.
 * @param scope the scope of this component. Every new value will trigger recomposition of all functions with
 * signature @Composable Scope.() -> {}.
 * If you need to access [EditorScope] to construct the scope, use [LocalEditorScope].
 * By default it is updated only when the parent component scope ([InspectorBar.scope], accessed via [LocalEditorScope]) is updated.
 * @param visible whether the button should be visible.
 * By default the value is true when the selected design block type is [DesignBlockType.Audio] or
 * [DesignBlockType.Graphic] with [FillType.Image] or [FillType.Video] fill and has an enabled engine scope "fill/change".
 * @param enterTransition transition of the button when it enters the parent composable.
 * Default value is always no enter transition.
 * @param exitTransition transition of the button when it exits the parent composable.
 * Default value is always no exit transition.
 * @param decoration decoration of the button. Useful when you want to add custom background, foreground, shadow, paddings etc.
 * Default value is always no decoration.
 * @return a button that will be displayed in the inspector bar.
 */
@Composable
fun Button.Companion.rememberReplace(
    vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.Replace },
    text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_replace) },
    tint: (@Composable ButtonScope.() -> Color)? = null,
    enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
    onClick: ButtonScope.() -> Unit = {
        val libraryCategory =
            when (editorContext.selection.type) {
                DesignBlockType.Audio -> editorContext.assetLibrary.audios
                DesignBlockType.Graphic ->
                    when (editorContext.selection.fillType) {
                        FillType.Video -> editorContext.assetLibrary.videos
                        FillType.Image -> {
                            when (editorContext.selection.kind) {
                                KIND_STICKER -> editorContext.assetLibrary.stickers
                                else -> editorContext.assetLibrary.images
                            }
                        }
                        else -> {
                            error(
                                "Unsupported fillType ${editorContext.selection.fillType} for replace inspector bar button.",
                            )
                        }
                    }
                else -> error("Unsupported type ${editorContext.selection.type} for replace inspector bar button.")
            }(editorContext.engine.scene.getMode())
        editorContext.eventHandler.send(EditorEvent.Sheet.Open(SheetType.LibraryReplace(libraryCategory = libraryCategory)))
    },
    scope: ButtonScope =
        (LocalEditorScope.current as InspectorBar.Scope).run {
            rememberLastValue(this) {
                if (editorContext.safeSelection == null) lastValue else ButtonScope(parentScope = this@run)
            }
        },
    visible: @Composable ButtonScope.() -> Boolean = {
        remember(this) {
            (
                editorContext.selection.type == DesignBlockType.Audio ||
                    (
                        editorContext.selection.type == DesignBlockType.Graphic &&
                            editorContext.selection.fillType == FillType.Image || editorContext.selection.fillType == FillType.Video
                    )
            ) && editorContext.engine.block.isAllowedByScope(editorContext.selection.designBlock, "fill/change")
        }
    },
    enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
    exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
    decoration: @Composable ButtonScope.(@Composable () -> Unit) -> Unit = { it() },
    `_`: Nothing = nothing,
): Button =
    remember(
        id = Button.Id.replace,
        vectorIcon = vectorIcon,
        text = text,
        tint = tint,
        enabled = enabled,
        onClick = onClick,
        scope = scope,
        visible = visible,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        decoration = decoration,
        `_` = `_`,
    )

/**
 * The id of the inspector bar button returned by [InspectorBar.Button.Companion.rememberEnterGroup].
 */
val Button.Id.Companion.enterGroup by unsafeLazy {
    EditorComponentId("ly.img.component.inspectorBar.button.enterGroup")
}

/**
 * A helper function that that changes selection from the selected group design block to a design block
 * within that group via [EditorEvent.Selection.EnterGroup].
 *
 * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
 * Default value is always [IconPack.GroupEnter].
 * @param text the text content of the button as a string. If null then text is not rendered.
 * Default value is always [R.string.ly_img_editor_enter_group].
 * @param tint the tint color of the content. If null then no tint is applied.
 * Default value is null.
 * @param enabled whether the button is enabled.
 * Default value is always true.
 * @param onClick the callback that is invoked when the button is clicked.
 * By default [EditorEvent.Selection.EnterGroup] is invoked.
 * @param scope the scope of this component. Every new value will trigger recomposition of all functions with
 * signature @Composable Scope.() -> {}.
 * If you need to access [EditorScope] to construct the scope, use [LocalEditorScope].
 * By default it is updated only when the parent component scope ([InspectorBar.scope], accessed via [LocalEditorScope]) is updated.
 * @param visible whether the button should be visible.
 * By default the value is true when the selected design block type is [DesignBlockType.Group].
 * @param enterTransition transition of the button when it enters the parent composable.
 * Default value is always no enter transition.
 * @param exitTransition transition of the button when it exits the parent composable.
 * Default value is always no exit transition.
 * @param decoration decoration of the button. Useful when you want to add custom background, foreground, shadow, paddings etc.
 * Default value is always no decoration.
 * @return a button that will be displayed in the inspector bar.
 */
@Composable
fun Button.Companion.rememberEnterGroup(
    vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.GroupEnter },
    text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_enter_group) },
    tint: (@Composable ButtonScope.() -> Color)? = null,
    enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
    onClick: ButtonScope.() -> Unit = {
        editorContext.eventHandler.send(EditorEvent.Selection.EnterGroup())
    },
    scope: ButtonScope =
        (LocalEditorScope.current as InspectorBar.Scope).run {
            rememberLastValue(this) {
                if (editorContext.safeSelection == null) lastValue else ButtonScope(parentScope = this@run)
            }
        },
    visible: @Composable ButtonScope.() -> Boolean = {
        remember(this) {
            editorContext.selection.type == DesignBlockType.Group
        }
    },
    enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
    exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
    decoration: @Composable ButtonScope.(@Composable () -> Unit) -> Unit = { it() },
    `_`: Nothing = nothing,
): Button =
    remember(
        id = Button.Id.enterGroup,
        vectorIcon = vectorIcon,
        text = text,
        tint = tint,
        enabled = enabled,
        onClick = onClick,
        scope = scope,
        visible = visible,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        decoration = decoration,
        `_` = `_`,
    )

/**
 * The id of the inspector bar button returned by [InspectorBar.Button.Companion.rememberSelectGroup].
 */
val Button.Id.Companion.selectGroup by unsafeLazy {
    EditorComponentId("ly.img.component.inspectorBar.button.selectGroup")
}

/**
 * A helper function that returns an [InspectorBar.Button] that selects the group design block that
 * contains the currently selected design block via [EditorEvent.Selection.SelectGroup].
 *
 * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
 * Default value is always [IconPack.SelectGroup].
 * @param text the text content of the button as a string. If null then text is not rendered.
 * Default value is always [R.string.ly_img_editor_select_group].
 * @param tint the tint color of the content. If null then no tint is applied.
 * Default value is null.
 * @param enabled whether the button is enabled.
 * Default value is always true.
 * @param onClick the callback that is invoked when the button is clicked.
 * By default [EditorEvent.Selection.EnterGroup] is invoked.
 * @param scope the scope of this component. Every new value will trigger recomposition of all functions with
 * signature @Composable Scope.() -> {}.
 * If you need to access [EditorScope] to construct the scope, use [LocalEditorScope].
 * By default it is updated only when the parent component scope ([InspectorBar.scope], accessed via [LocalEditorScope]) is updated.
 * @param visible whether the button should be visible.
 * By default the value is true when [isGrouped] returns true.
 * @param enterTransition transition of the button when it enters the parent composable.
 * Default value is always no enter transition.
 * @param exitTransition transition of the button when it exits the parent composable.
 * Default value is always no exit transition.
 * @param decoration decoration of the button. Useful when you want to add custom background, foreground, shadow, paddings etc.
 * Default value is always no decoration.
 * @return a button that will be displayed in the inspector bar.
 */
@Composable
fun Button.Companion.rememberSelectGroup(
    vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.SelectGroup },
    text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_select_group) },
    tint: (@Composable ButtonScope.() -> Color)? = null,
    enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
    onClick: ButtonScope.() -> Unit = {
        editorContext.eventHandler.send(EditorEvent.Selection.SelectGroup())
    },
    scope: ButtonScope =
        (LocalEditorScope.current as InspectorBar.Scope).run {
            rememberLastValue(this) {
                if (editorContext.safeSelection == null) lastValue else ButtonScope(parentScope = this@run)
            }
        },
    visible: @Composable ButtonScope.() -> Boolean = {
        remember(this) {
            editorContext.engine.isGrouped(editorContext.selection)
        }
    },
    enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
    exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
    decoration: @Composable ButtonScope.(@Composable () -> Unit) -> Unit = { it() },
    `_`: Nothing = nothing,
): Button =
    remember(
        id = Button.Id.selectGroup,
        vectorIcon = vectorIcon,
        text = text,
        tint = tint,
        enabled = enabled,
        onClick = onClick,
        scope = scope,
        visible = visible,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        decoration = decoration,
        `_` = `_`,
    )

/**
 * The id of the inspector bar button returned by [InspectorBar.Button.Companion.rememberDelete].
 */
val Button.Id.Companion.delete by unsafeLazy {
    EditorComponentId("ly.img.component.inspectorBar.button.delete")
}

/**
 * A helper function that returns an [InspectorBar.Button] that that deletes currently selected
 * design block via [EditorEvent.Selection.Delete].
 *
 * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
 * Default value is always [IconPack.Delete].
 * @param text the text content of the button as a string. If null then text is not rendered.
 * Default value is always [R.string.ly_img_editor_delete].
 * @param tint the tint color of the content. If null then no tint is applied.
 * Default value is [androidx.compose.material3.ColorScheme.error].
 * @param enabled whether the button is enabled.
 * Default value is always true.
 * @param onClick the callback that is invoked when the button is clicked.
 * By default [EditorEvent.Selection.Delete] is invoked.
 * @param scope the scope of this component. Every new value will trigger recomposition of all functions with
 * signature @Composable Scope.() -> {}.
 * If you need to access [EditorScope] to construct the scope, use [LocalEditorScope].
 * By default it is updated only when the parent component scope ([InspectorBar.scope], accessed via [LocalEditorScope]) is updated.
 * @param visible whether the button should be visible.
 * By default the value is true when the selected design block type is not [DesignBlockType.Page]
 * and [isDeleteAllowed] returns true.
 * @param enterTransition transition of the button when it enters the parent composable.
 * Default value is always no enter transition.
 * @param exitTransition transition of the button when it exits the parent composable.
 * Default value is always no exit transition.
 * @param decoration decoration of the button. Useful when you want to add custom background, foreground, shadow, paddings etc.
 * Default value is always no decoration.
 * @return a button that will be displayed in the inspector bar.
 */
@Composable
fun Button.Companion.rememberDelete(
    vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.Delete },
    text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_delete) },
    tint: (@Composable ButtonScope.() -> Color)? = { MaterialTheme.colorScheme.error },
    enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
    onClick: ButtonScope.() -> Unit = {
        editorContext.eventHandler.send(EditorEvent.Selection.Delete())
    },
    scope: ButtonScope =
        (LocalEditorScope.current as InspectorBar.Scope).run {
            rememberLastValue(this) {
                if (editorContext.safeSelection == null) lastValue else ButtonScope(parentScope = this@run)
            }
        },
    visible: @Composable ButtonScope.() -> Boolean = {
        remember(this) {
            editorContext.selection.type != DesignBlockType.Page &&
                editorContext.engine.isDeleteAllowed(editorContext.selection)
        }
    },
    enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
    exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
    decoration: @Composable ButtonScope.(@Composable () -> Unit) -> Unit = { it() },
    `_`: Nothing = nothing,
): Button =
    remember(
        id = Button.Id.delete,
        vectorIcon = vectorIcon,
        text = text,
        tint = tint,
        enabled = enabled,
        onClick = onClick,
        scope = scope,
        visible = visible,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        decoration = decoration,
        `_` = `_`,
    )

/**
 * The id of the inspector bar button returned by [InspectorBar.Button.Companion.rememberEditText].
 */
val Button.Id.Companion.editText by unsafeLazy {
    EditorComponentId("ly.img.component.inspectorBar.button.editText")
}

/**
 * A helper function that returns an [InspectorBar.Button] that enters text editing mode for the
 * selected design block via [EditorEvent.Selection.EnterTextEditMode].
 *
 * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
 * Default value is always [IconPack.Keyboard].
 * @param text the text content of the button as a string. If null then text is not rendered.
 * Default value is always [R.string.ly_img_editor_edit].
 * @param tint the tint color of the content. If null then no tint is applied.
 * Default value is null.
 * @param enabled whether the button is enabled.
 * Default value is always true.
 * @param onClick the callback that is invoked when the button is clicked.
 * By default [EditorEvent.Selection.EnterTextEditMode] is invoked.
 * @param scope the scope of this component. Every new value will trigger recomposition of all functions with
 * signature @Composable Scope.() -> {}.
 * If you need to access [EditorScope] to construct the scope, use [LocalEditorScope].
 * By default it is updated only when the parent component scope ([InspectorBar.scope], accessed via [LocalEditorScope]) is updated.
 * @param visible whether the button should be visible.
 * By default the value is true when the selected design block type is [DesignBlockType.Text]
 * and has an enabled engine scope "text/edit".
 * @param enterTransition transition of the button when it enters the parent composable.
 * Default value is always no enter transition.
 * @param exitTransition transition of the button when it exits the parent composable.
 * Default value is always no exit transition.
 * @param decoration decoration of the button. Useful when you want to add custom background, foreground, shadow, paddings etc.
 * Default value is always no decoration.
 * @return a button that will be displayed in the inspector bar.
 */
@Composable
fun Button.Companion.rememberEditText(
    vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.Keyboard },
    text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_edit) },
    tint: (@Composable ButtonScope.() -> Color)? = null,
    enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
    onClick: ButtonScope.() -> Unit = {
        editorContext.eventHandler.send(EditorEvent.Selection.EnterTextEditMode())
    },
    scope: ButtonScope =
        (LocalEditorScope.current as InspectorBar.Scope).run {
            rememberLastValue(this) {
                if (editorContext.safeSelection == null) lastValue else ButtonScope(parentScope = this@run)
            }
        },
    visible: @Composable ButtonScope.() -> Boolean = {
        remember(this) {
            editorContext.selection.type == DesignBlockType.Text &&
                editorContext.engine.block.isAllowedByScope(editorContext.selection.designBlock, "text/edit")
        }
    },
    enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
    exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
    decoration: @Composable ButtonScope.(@Composable () -> Unit) -> Unit = { it() },
    `_`: Nothing = nothing,
): Button =
    remember(
        id = Button.Id.editText,
        vectorIcon = vectorIcon,
        text = text,
        tint = tint,
        enabled = enabled,
        onClick = onClick,
        scope = scope,
        visible = visible,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        decoration = decoration,
        `_` = `_`,
    )

/**
 * The id of the inspector bar button returned by [InspectorBar.Button.Companion.rememberFormatText].
 */
val Button.Id.Companion.formatText by unsafeLazy {
    EditorComponentId("ly.img.component.inspectorBar.button.formatText")
}

/**
 * A helper function that returns an [InspectorBar.Button] that opens text formatting sheet via [EditorEvent.Sheet.Open].
 *
 * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
 * Default value is always [IconPack.Typeface].
 * @param text the text content of the button as a string. If null then text is not rendered.
 * Default value is always [R.string.ly_img_editor_format].
 * @param tint the tint color of the content. If null then no tint is applied.
 * Default value is null.
 * @param enabled whether the button is enabled.
 * Default value is always true.
 * @param onClick the callback that is invoked when the button is clicked.
 * By default [EditorEvent.Sheet.Open] is invoked with sheet type [SheetType.FormatText].
 * @param scope the scope of this component. Every new value will trigger recomposition of all functions with
 * signature @Composable Scope.() -> {}.
 * If you need to access [EditorScope] to construct the scope, use [LocalEditorScope].
 * By default it is updated only when the parent component scope ([InspectorBar.scope], accessed via [LocalEditorScope]) is updated.
 * @param visible whether the button should be visible.
 * By default the value is true when the selected design block type is [DesignBlockType.Text]
 * and has an enabled engine scope "text/edit".
 * @param enterTransition transition of the button when it enters the parent composable.
 * Default value is always no enter transition.
 * @param exitTransition transition of the button when it exits the parent composable.
 * Default value is always no exit transition.
 * @param decoration decoration of the button. Useful when you want to add custom background, foreground, shadow, paddings etc.
 * Default value is always no decoration.
 * @return a button that will be displayed in the inspector bar.
 */
@Composable
fun Button.Companion.rememberFormatText(
    vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.Typeface },
    text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_format) },
    tint: (@Composable ButtonScope.() -> Color)? = null,
    enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
    onClick: ButtonScope.() -> Unit = {
        editorContext.eventHandler.send(EditorEvent.Sheet.Open(SheetType.FormatText()))
    },
    scope: ButtonScope =
        (LocalEditorScope.current as InspectorBar.Scope).run {
            rememberLastValue(this) {
                if (editorContext.safeSelection == null) lastValue else ButtonScope(parentScope = this@run)
            }
        },
    visible: @Composable ButtonScope.() -> Boolean = {
        remember(this) {
            editorContext.selection.type == DesignBlockType.Text &&
                editorContext.engine.block.isAllowedByScope(editorContext.selection.designBlock, "text/edit")
        }
    },
    enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
    exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
    decoration: @Composable ButtonScope.(@Composable () -> Unit) -> Unit = { it() },
    `_`: Nothing = nothing,
): Button =
    remember(
        id = Button.Id.formatText,
        vectorIcon = vectorIcon,
        text = text,
        tint = tint,
        enabled = enabled,
        onClick = onClick,
        scope = scope,
        visible = visible,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        decoration = decoration,
        `_` = `_`,
    )

/**
 * The id of the inspector bar button returned by [InspectorBar.Button.Companion.rememberShape].
 */
val Button.Id.Companion.shape by unsafeLazy {
    EditorComponentId("ly.img.component.inspectorBar.button.shape")
}

/**
 * A helper function that returns an [InspectorBar.Button] that opens shape options sheet via [EditorEvent.Sheet.Open].
 * The button is applicable for the following shape types:
 * [ShapeType.Star], [ShapeType.Polygon], [ShapeType.Line], [ShapeType.Rect].
 *
 * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
 * Default value is always [IconPack.ShapeIcon].
 * @param text the text content of the button as a string. If null then text is not rendered.
 * Default value is always [R.string.ly_img_editor_shape].
 * @param tint the tint color of the content. If null then no tint is applied.
 * Default value is null.
 * @param enabled whether the button is enabled.
 * Default value is always true.
 * @param onClick the callback that is invoked when the button is clicked.
 * By default [EditorEvent.Sheet.Open] is invoked with sheet type [SheetType.Shape].
 * @param scope the scope of this component. Every new value will trigger recomposition of all functions with
 * signature @Composable Scope.() -> {}.
 * If you need to access [EditorScope] to construct the scope, use [LocalEditorScope].
 * By default it is updated only when the parent component scope ([InspectorBar.scope], accessed via [LocalEditorScope]) is updated.
 * @param visible whether the button should be visible.
 * By default the value is true when the selected design block does not have a fill type [FillType.Image], or has but the kind
 * is not "sticker", has an enabled engine scope "shape/change", [BlockApi.supportsShape] is true and the [ShapeType] is
 * one of the following: [ShapeType.Star], [ShapeType.Polygon], [ShapeType.Line], [ShapeType.Rect].
 *
 * @param enterTransition transition of the button when it enters the parent composable.
 * Default value is always no enter transition.
 * @param exitTransition transition of the button when it exits the parent composable.
 * Default value is always no exit transition.
 * @param decoration decoration of the button. Useful when you want to add custom background, foreground, shadow, paddings etc.
 * Default value is always no decoration.
 * @return a button that will be displayed in the inspector bar.
 */
@Composable
fun Button.Companion.rememberShape(
    vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.ShapeIcon },
    text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_shape) },
    tint: (@Composable ButtonScope.() -> Color)? = null,
    enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
    onClick: ButtonScope.() -> Unit = {
        editorContext.eventHandler.send(EditorEvent.Sheet.Open(SheetType.Shape()))
    },
    scope: ButtonScope =
        (LocalEditorScope.current as InspectorBar.Scope).run {
            rememberLastValue(this) {
                if (editorContext.safeSelection == null) lastValue else ButtonScope(parentScope = this@run)
            }
        },
    visible: @Composable ButtonScope.() -> Boolean = {
        remember(this) {
            val designBlock = editorContext.selection.designBlock
            (editorContext.selection.fillType != FillType.Image || editorContext.selection.kind != KIND_STICKER) &&
                editorContext.engine.block.isAllowedByScope(designBlock, "shape/change") &&
                run {
                    val shapeType =
                        if (editorContext.engine.block.supportsShape(designBlock)) {
                            ShapeType.get(
                                editorContext.engine.block.getType(editorContext.engine.block.getShape(designBlock)),
                            )
                        } else {
                            null
                        }
                    shapeType in arrayOf(ShapeType.Star, ShapeType.Polygon, ShapeType.Line, ShapeType.Rect)
                }
        }
    },
    enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
    exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
    decoration: @Composable ButtonScope.(@Composable () -> Unit) -> Unit = { it() },
    `_`: Nothing = nothing,
): Button =
    remember(
        id = Button.Id.shape,
        vectorIcon = vectorIcon,
        text = text,
        tint = tint,
        enabled = enabled,
        onClick = onClick,
        scope = scope,
        visible = visible,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        decoration = decoration,
        `_` = `_`,
    )
