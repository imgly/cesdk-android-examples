package ly.img.editor.core.component

import android.content.Context
import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import ly.img.camera.core.CameraResult
import ly.img.camera.core.CaptureVideo
import ly.img.camera.core.EngineConfiguration
import ly.img.editor.core.EditorContext
import ly.img.editor.core.EditorScope
import ly.img.editor.core.R
import ly.img.editor.core.UnstableEditorApi
import ly.img.editor.core.component.Dock.Item
import ly.img.editor.core.component.data.Nothing
import ly.img.editor.core.component.data.nothing
import ly.img.editor.core.component.data.unsafeLazy
import ly.img.editor.core.engine.EngineScope
import ly.img.editor.core.engine.getBackgroundTrack
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.iconpack.Addaudio
import ly.img.editor.core.iconpack.Addcamerabackground
import ly.img.editor.core.iconpack.Addcameraforegound
import ly.img.editor.core.iconpack.Addgallerybackground
import ly.img.editor.core.iconpack.Addgalleryforeground
import ly.img.editor.core.iconpack.Addimageforeground
import ly.img.editor.core.iconpack.Addoverlay
import ly.img.editor.core.iconpack.Addshape
import ly.img.editor.core.iconpack.Addsticker
import ly.img.editor.core.iconpack.Addtext
import ly.img.editor.core.iconpack.Adjustments
import ly.img.editor.core.iconpack.Blur
import ly.img.editor.core.iconpack.Croprotate
import ly.img.editor.core.iconpack.Effect
import ly.img.editor.core.iconpack.Elements
import ly.img.editor.core.iconpack.Filter
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.Reorderhorizontally
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.theme.surface1
import ly.img.editor.core.ui.IconTextButton
import ly.img.engine.DesignBlock
import ly.img.engine.Engine
import ly.img.engine.SceneMode
import java.io.File

/**
 * A component for rendering the dock at the bottom of the editor.
 * Use remember composable functions in the companion object to create an instance of this class.
 *
 * @param items a callback that is invoked when the dock is about to be rendered. The returned list of components is displayed in
 * a horizontal list at the bottom of the editor.
 * Note that the height of components should be at most 64dp which is enforced in the [Item.Content].
 * @param horizontalArrangement the horizontal arrangement that should be used to render the items in the dock horizontally.
 * Default value is [Arrangement.SpaceEvenly].
 * @param scopeProvider a provider of the scope of the current component based on the parent scope.
 * @param updateTrigger a flow that is used to trigger recomposition of all functions with signature @Composable Scope.() -> {}
 * in this component and in all [items] if [EditorComponent.shouldReceiveUpdateFromParent] returns true for them.
 * @param visible whether the dock should be visible based on the [Engine]'s current state.
 * @param enterTransition transition of the dock when it enters the parent composable.
 * @param exitTransition transition of the dock when it exits the parent composable.
 */
@Stable
class Dock private constructor(
    val items: Scope.() -> List<Item<*>>,
    val horizontalArrangement: @Composable Scope.() -> Arrangement.Horizontal = { Arrangement.SpaceEvenly },
    override val scopeProvider: (EditorScope) -> Scope = { Scope(it) },
    override val updateTrigger: (Scope.() -> Flow<Any?>)? = null,
    override val visible: @Composable Scope.() -> Boolean = alwaysVisible,
    override val enterTransition: @Composable Scope.() -> EnterTransition = noneEnterTransition,
    override val exitTransition: @Composable Scope.() -> ExitTransition = noneExitTransition,
    private val `_`: Nothing = nothing,
) : EditorComponent<Dock.Scope>() {
    override val id: EditorComponentId = EditorComponentId("ly.img.component.dock")

    @Composable
    override fun Scope.Content(animatedVisibilityScope: AnimatedVisibilityScope?) {
        val items = remember { items() }
        val horizontalArrangement = horizontalArrangement()
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface1.copy(alpha = 0.95f),
        ) {
            Row(
                modifier =
                    Modifier
                        .padding(vertical = 10.dp)
                        .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = horizontalArrangement,
            ) {
                items.forEach { item ->
                    item.Content(this)
                }
            }
        }
    }

    /**
     * The scope of the [Dock] component.
     */
    @Stable
    open class Scope(
        parentScope: EditorScope,
    ) : EditorScope() {
        override val impl: EditorContext = parentScope.editorContext
    }

    override fun toString(): String {
        return "$`_`Dock"
    }

    /**
     * The scope of the [Item] component.
     */
    @Stable
    open class ItemScope(
        parentScope: EditorScope,
    ) : EditorScope() {
        override val impl: EditorContext = parentScope.editorContext
    }

    /**
     * A component that represents an item that can be rendered in the dock.
     * The only limitation is that the component must have a maximum height of 64.dp.
     */
    abstract class Item<Scope : ItemScope> : EditorComponent<Scope>() {
        /**
         * The content of the item in the dock.
         */
        @Composable
        protected abstract fun Scope.ItemContent()

        @Composable
        final override fun Scope.Content(animatedVisibilityScope: AnimatedVisibilityScope?) {
            Box(modifier = Modifier.sizeIn(maxHeight = 64.dp)) {
                ItemContent()
            }
        }
    }

    /**
     * A component that represents a custom content in the [Dock].
     *
     * @param id the id of the button. It is highly recommended that it is unique when representing unique buttons.
     * Check the object of [Dock.Button.Id] for available ids.
     * @param scopeProvider a provider of the scope of the current component based on the parent scope.
     * @param updateTrigger a flow that is used to trigger recomposition of all functions with signature @Composable Scope.() -> {}
     * in this component.
     * @param visible whether the button should be visible based on the [Engine]'s current state.
     * @param enterTransition transition of the button when it enters the parent composable.
     * @param exitTransition transition of the button when it exits the parent composable.
     * @param content the content of the component.
     */
    @Stable
    class Custom<Scope : ItemScope>(
        override val id: EditorComponentId,
        override val scopeProvider: EditorScope.() -> Scope,
        override val updateTrigger: (Scope.() -> Flow<Any?>)? = null,
        override val visible: @Composable Scope.() -> Boolean = alwaysVisible,
        override val enterTransition: @Composable Scope.() -> EnterTransition = noneEnterTransition,
        override val exitTransition: @Composable Scope.() -> ExitTransition = noneExitTransition,
        val content: @Composable Scope.() -> Unit,
    ) : Item<Scope>() {
        @Composable
        override fun Scope.ItemContent() {
            content()
        }
    }

    /**
     * The scope of the [Button] component.
     */
    @Stable
    open class ButtonScope(
        parentScope: EditorScope,
    ) : ItemScope(parentScope)

    /**
     * A component that represents a button in the [Dock].
     *
     * @param id the id of the button. It is highly recommended that it is unique when representing unique buttons.
     * Check the object of [Dock.Button.Id] for available ids.
     * @param onClick the callback that is invoked when the button is clicked.
     * @param icon the icon content of the button. If null, it will not be rendered.
     * @param text the text content of the button. If null, it will not be rendered.
     * @param enabled whether the button is enabled.
     * @param scopeProvider a provider of the scope of the current component based on the parent scope.
     * @param updateTrigger a flow that is used to trigger recomposition of all functions with signature @Composable Scope.() -> {}
     * in this component.
     * @param visible whether the button should be visible based on the [Engine]'s current state.
     * @param enterTransition transition of the button when it enters the parent composable.
     * @param exitTransition transition of the button when it exits the parent composable.
     */
    @Stable
    class Button(
        override val id: EditorComponentId,
        val onClick: ButtonScope.() -> Unit,
        val icon: (@Composable ButtonScope.() -> Unit)? = null,
        val text: (@Composable ButtonScope.() -> Unit)? = null,
        val enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
        override val scopeProvider: (EditorScope) -> ButtonScope = { ButtonScope(it) },
        override val updateTrigger: (ButtonScope.() -> Flow<Any?>)? = null,
        override val visible: @Composable ButtonScope.() -> Boolean = alwaysVisible,
        override val enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
        override val exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
        private val `_`: Nothing = nothing,
    ) : Item<ButtonScope>() {
        /**
         * A convenience constructor where [icon] composable is provided via [ImageVector] and [text] composable via [String].
         *
         * @param id the id of the button. It is highly recommended that it is unique when representing unique buttons.
         * Check the object of [Dock.Button.Id] for available ids.
         * @param onClick the callback that is invoked when the button is clicked.
         * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
         * @param text the text content of the button as a string. If null then text is not rendered.
         * @param tint the tint color of the content. If null then no tint is applied.
         * @param enabled whether the button is enabled.
         * @param scopeProvider a provider of the scope of the current component based on the parent scope.
         * @param updateTrigger a flow that is used to trigger recomposition of all functions with signature @Composable Scope.() -> {}
         * in this component.
         * @param visible whether the button should be visible based on the [Engine]'s current state.
         * @param enterTransition transition of the button when it enters the parent composable.
         * @param exitTransition transition of the button when it exits the parent composable.
         */
        constructor(
            id: EditorComponentId,
            onClick: ButtonScope.() -> Unit,
            vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = null,
            text: (@Composable ButtonScope.() -> String)? = null,
            tint: (@Composable ButtonScope.() -> Color)? = null,
            enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
            scopeProvider: (EditorScope) -> ButtonScope = { ButtonScope(it) },
            updateTrigger: (ButtonScope.() -> Flow<Any?>)? = null,
            visible: @Composable ButtonScope.() -> Boolean = alwaysVisible,
            enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
            exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
            `_`: Nothing = nothing,
        ) : this(
            id = id,
            onClick = onClick,
            icon =
                vectorIcon?.let {
                    {
                        Icon(
                            imageVector = vectorIcon(this),
                            contentDescription = null,
                            tint = tint?.let { it(this) } ?: LocalContentColor.current,
                        )
                    }
                },
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
            scopeProvider = scopeProvider,
            updateTrigger = updateTrigger,
            visible = visible,
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            `_` = `_`,
        )

        @Composable
        override fun ButtonScope.ItemContent() {
            IconTextButton(
                onClick = { onClick() },
                enabled = enabled(),
                icon = icon?.let { { it() } },
                text = text?.let { { it() } },
            )
        }

        override fun toString(): String {
            return "$`_`Dock.Button(id=${id.id})"
        }

        /**
         * The ids of the components declared in [Dock.Button].
         */
        object Id {
            /**
             * The id of the dock button returned by [Dock.Button.elementsLibrary].
             */
            val elementsLibrary by unsafeLazy {
                EditorComponentId("ly.img.component.dock.button.elementsLibrary")
            }

            /**
             * The id of the dock button returned by [Dock.Button.imagesLibrary].
             */
            val imagesLibrary by unsafeLazy {
                EditorComponentId("ly.img.component.dock.button.imagesLibrary")
            }

            /**
             * The id of the dock button returned by [Dock.Button.overlaysLibrary].
             */
            val overlaysLibrary by unsafeLazy {
                EditorComponentId("ly.img.component.dock.button.overlaysLibrary")
            }

            /**
             * The id of the dock button returned by [Dock.Button.stickersLibrary].
             */
            val stickersLibrary by unsafeLazy {
                EditorComponentId("ly.img.component.dock.button.stickersLibrary")
            }

            /**
             * The id of the dock button returned by [Dock.Button.audiosLibrary].
             */
            val audiosLibrary by unsafeLazy {
                EditorComponentId("ly.img.component.dock.button.audiosLibrary")
            }

            /**
             * The id of the dock button returned by [Dock.Button.shapesLibrary].
             */
            val shapesLibrary by unsafeLazy {
                EditorComponentId("ly.img.component.dock.button.shapesLibrary")
            }

            /**
             * The id of the dock button returned by [Dock.Button.textLibrary].
             */
            val textLibrary by unsafeLazy {
                EditorComponentId("ly.img.component.dock.button.textLibrary")
            }

            /**
             * The id of the dock button returned by [Dock.Button.systemGallery].
             */
            val systemGallery by unsafeLazy {
                EditorComponentId("ly.img.component.dock.button.systemGallery")
            }

            /**
             * The id of the dock button returned by [Dock.Button.systemCamera].
             */
            val systemCamera by unsafeLazy {
                EditorComponentId("ly.img.component.dock.button.systemCamera")
            }

            /**
             * The id of the dock button returned by [Dock.Button.imglyCamera].
             */
            val imglyCamera by unsafeLazy {
                EditorComponentId("ly.img.component.dock.button.imglyCamera")
            }

            /**
             * The id of the dock button returned by [Dock.Button.reorder].
             */
            val reorder by unsafeLazy {
                EditorComponentId("ly.img.component.dock.button.reorder")
            }

            /**
             * The id of the dock button returned by [Dock.Button.adjustments].
             */
            val adjustments by unsafeLazy {
                EditorComponentId("ly.img.component.dock.button.adjustments")
            }

            /**
             * The id of the dock button returned by [Dock.Button.filter].
             */
            val filter by unsafeLazy {
                EditorComponentId("ly.img.component.dock.button.filter")
            }

            /**
             * The id of the dock button returned by [Dock.Button.effect].
             */
            val effect by unsafeLazy {
                EditorComponentId("ly.img.component.dock.button.effect")
            }

            /**
             * The id of the dock button returned by [Dock.Button.blur].
             */
            val blur by unsafeLazy {
                EditorComponentId("ly.img.component.dock.button.blur")
            }

            /**
             * The id of the dock button returned by [Dock.Button.crop].
             */
            val crop by unsafeLazy {
                EditorComponentId("ly.img.component.dock.button.crop")
            }
        }

        companion object {
            /**
             * Predicate to be used when the [EditorComponent] is always enabled.
             */
            val alwaysEnabled: @Composable ButtonScope.() -> Boolean = { true }

            /**
             * A helper function that returns a [Dock.Button] that opens a library sheet with elements via [EditorEvent.OpenLibrarySheet].
             * By default [ly.img.editor.core.library.AssetLibrary.elements] will be displayed on the sheet.
             *
             * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
             * @param text the text content of the button as a string. If null then text is not rendered.
             * @param tint the tint color of the content. If null then no tint is applied.
             * @param enabled whether the button is enabled.
             * @param onClick the callback that is invoked when the button is clicked.
             * @param updateTrigger a flow that is used to trigger recomposition of all functions with signature @Composable Scope.() -> {}
             * in this component.
             * @param visible whether the button should be visible based on the [Engine]'s current state.
             * @param enterTransition transition of the button when it enters the parent composable.
             * @param exitTransition transition of the button when it exits the parent composable.
             */
            fun elementsLibrary(
                vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.Elements },
                text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_elements) },
                tint: (@Composable ButtonScope.() -> Color)? = null,
                enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
                onClick: ButtonScope.() -> Unit = {
                    editorContext.eventHandler.send(
                        EditorEvent.OpenLibrarySheet(
                            libraryCategory = editorContext.assetLibrary.elements(editorContext.engine.scene.getMode()),
                            isFloating = true,
                        ),
                    )
                },
                updateTrigger: (ButtonScope.() -> Flow<Any?>)? = null,
                visible: @Composable ButtonScope.() -> Boolean = alwaysVisible,
                enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
                exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
                `_`: Nothing = nothing,
            ) = Button(
                id = Id.elementsLibrary,
                vectorIcon = vectorIcon,
                text = text,
                tint = tint,
                enabled = enabled,
                onClick = onClick,
                updateTrigger = updateTrigger,
                visible = visible,
                enterTransition = enterTransition,
                exitTransition = exitTransition,
                `_` = `_`,
            )

            /**
             * A helper function that returns a [Dock.Button] that opens a library sheet with overlays via [EditorEvent.OpenLibrarySheet].
             * By default [ly.img.editor.core.library.AssetLibrary.overlays] will be displayed on the sheet.
             *
             * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
             * @param text the text content of the button as a string. If null then text is not rendered.
             * @param tint the tint color of the content. If null then no tint is applied.
             * @param enabled whether the button is enabled.
             * @param onClick the callback that is invoked when the button is clicked.
             * @param updateTrigger a flow that is used to trigger recomposition of all functions with signature @Composable Scope.() -> {}
             * in this component.
             * @param visible whether the button should be visible based on the [Engine]'s current state.
             * @param enterTransition transition of the button when it enters the parent composable.
             * @param exitTransition transition of the button when it exits the parent composable.
             */
            fun overlaysLibrary(
                vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.Addoverlay },
                text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_overlay) },
                tint: (@Composable ButtonScope.() -> Color)? = null,
                enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
                onClick: ButtonScope.() -> Unit = {
                    editorContext.eventHandler.send(
                        EditorEvent.OpenLibrarySheet(
                            libraryCategory = editorContext.assetLibrary.overlays,
                            isFloating = true,
                        ),
                    )
                },
                updateTrigger: (ButtonScope.() -> Flow<Any?>)? = null,
                visible: @Composable ButtonScope.() -> Boolean = alwaysVisible,
                enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
                exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
                `_`: Nothing = nothing,
            ) = Button(
                id = Id.overlaysLibrary,
                vectorIcon = vectorIcon,
                text = text,
                tint = tint,
                enabled = enabled,
                onClick = onClick,
                updateTrigger = updateTrigger,
                visible = visible,
                enterTransition = enterTransition,
                exitTransition = exitTransition,
                `_` = `_`,
            )

            /**
             * A helper function that returns a [Dock.Button] that opens a library sheet with images via [EditorEvent.OpenLibrarySheet].
             * By default [ly.img.editor.core.library.AssetLibrary.images] will be displayed on the sheet.
             *
             * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
             * @param text the text content of the button as a string. If null then text is not rendered.
             * @param tint the tint color of the content. If null then no tint is applied.
             * @param enabled whether the button is enabled.
             * @param onClick the callback that is invoked when the button is clicked.
             * @param updateTrigger a flow that is used to trigger recomposition of all functions with signature @Composable Scope.() -> {}
             * in this component.
             * @param visible whether the button should be visible based on the [Engine]'s current state.
             * @param enterTransition transition of the button when it enters the parent composable.
             * @param exitTransition transition of the button when it exits the parent composable.
             */
            fun imagesLibrary(
                vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.Addimageforeground },
                text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_image) },
                tint: (@Composable ButtonScope.() -> Color)? = null,
                enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
                onClick: ButtonScope.() -> Unit = {
                    editorContext.eventHandler.send(
                        EditorEvent.OpenLibrarySheet(
                            libraryCategory = editorContext.assetLibrary.images(editorContext.engine.scene.getMode()),
                            isFloating = true,
                        ),
                    )
                },
                updateTrigger: (ButtonScope.() -> Flow<Any?>)? = null,
                visible: @Composable ButtonScope.() -> Boolean = alwaysVisible,
                enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
                exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
                `_`: Nothing = nothing,
            ) = Button(
                id = Id.imagesLibrary,
                vectorIcon = vectorIcon,
                text = text,
                tint = tint,
                enabled = enabled,
                onClick = onClick,
                updateTrigger = updateTrigger,
                visible = visible,
                enterTransition = enterTransition,
                exitTransition = exitTransition,
                `_` = `_`,
            )

            /**
             * A helper function that returns a [Dock.Button] that opens a library sheet with text via [EditorEvent.OpenLibrarySheet].
             * By default [ly.img.editor.core.library.AssetLibrary.text] will be displayed on the sheet.
             *
             * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
             * @param text the text content of the button as a string. If null then text is not rendered.
             * @param tint the tint color of the content. If null then no tint is applied.
             * @param enabled whether the button is enabled.
             * @param onClick the callback that is invoked when the button is clicked.
             * @param updateTrigger a flow that is used to trigger recomposition of all functions with signature @Composable Scope.() -> {}
             * in this component.
             * @param visible whether the button should be visible based on the [Engine]'s current state.
             * @param enterTransition transition of the button when it enters the parent composable.
             * @param exitTransition transition of the button when it exits the parent composable.
             */
            fun textLibrary(
                vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.Addtext },
                text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_text) },
                tint: (@Composable ButtonScope.() -> Color)? = null,
                enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
                onClick: ButtonScope.() -> Unit = {
                    editorContext.eventHandler.send(
                        EditorEvent.OpenLibrarySheet(
                            libraryCategory = editorContext.assetLibrary.text(editorContext.engine.scene.getMode()),
                            isFloating = true,
                            isHalfExpandedInitially = true,
                        ),
                    )
                },
                updateTrigger: (ButtonScope.() -> Flow<Any?>)? = null,
                visible: @Composable ButtonScope.() -> Boolean = alwaysVisible,
                enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
                exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
                `_`: Nothing = nothing,
            ) = Button(
                id = Id.textLibrary,
                vectorIcon = vectorIcon,
                text = text,
                tint = tint,
                enabled = enabled,
                onClick = onClick,
                updateTrigger = updateTrigger,
                visible = visible,
                enterTransition = enterTransition,
                exitTransition = exitTransition,
                `_` = `_`,
            )

            /**
             * A helper function that returns a [Dock.Button] that opens a library sheet with shapes via [EditorEvent.OpenLibrarySheet].
             * By default [ly.img.editor.core.library.AssetLibrary.shapes] will be displayed on the sheet.
             *
             * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
             * @param text the text content of the button as a string. If null then text is not rendered.
             * @param tint the tint color of the content. If null then no tint is applied.
             * @param enabled whether the button is enabled.
             * @param onClick the callback that is invoked when the button is clicked.
             * @param updateTrigger a flow that is used to trigger recomposition of all functions with signature @Composable Scope.() -> {}
             * in this component.
             * @param visible whether the button should be visible based on the [Engine]'s current state.
             * @param enterTransition transition of the button when it enters the parent composable.
             * @param exitTransition transition of the button when it exits the parent composable.
             */
            fun shapesLibrary(
                vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.Addshape },
                text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_shape) },
                tint: (@Composable ButtonScope.() -> Color)? = null,
                enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
                onClick: ButtonScope.() -> Unit = {
                    editorContext.eventHandler.send(
                        EditorEvent.OpenLibrarySheet(
                            libraryCategory = editorContext.assetLibrary.shapes(editorContext.engine.scene.getMode()),
                            isFloating = true,
                        ),
                    )
                },
                updateTrigger: (ButtonScope.() -> Flow<Any?>)? = null,
                visible: @Composable ButtonScope.() -> Boolean = alwaysVisible,
                enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
                exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
                `_`: Nothing = nothing,
            ) = Button(
                id = Id.shapesLibrary,
                vectorIcon = vectorIcon,
                text = text,
                tint = tint,
                enabled = enabled,
                onClick = onClick,
                updateTrigger = updateTrigger,
                visible = visible,
                enterTransition = enterTransition,
                exitTransition = exitTransition,
                `_` = `_`,
            )

            /**
             * A helper function that returns a [Dock.Button] that opens a library sheet with stickers via [EditorEvent.OpenLibrarySheet].
             * By default [ly.img.editor.core.library.AssetLibrary.stickers] will be displayed on the sheet.
             *
             * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
             * @param text the text content of the button as a string. If null then text is not rendered.
             * @param tint the tint color of the content. If null then no tint is applied.
             * @param enabled whether the button is enabled.
             * @param onClick the callback that is invoked when the button is clicked.
             * @param updateTrigger a flow that is used to trigger recomposition of all functions with signature @Composable Scope.() -> {}
             * in this component.
             * @param visible whether the button should be visible based on the [Engine]'s current state.
             * @param enterTransition transition of the button when it enters the parent composable.
             * @param exitTransition transition of the button when it exits the parent composable.
             */
            fun stickersLibrary(
                vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.Addsticker },
                text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_sticker) },
                tint: (@Composable ButtonScope.() -> Color)? = null,
                enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
                onClick: ButtonScope.() -> Unit = {
                    editorContext.eventHandler.send(
                        EditorEvent.OpenLibrarySheet(
                            libraryCategory = editorContext.assetLibrary.stickers(editorContext.engine.scene.getMode()),
                            isFloating = true,
                        ),
                    )
                },
                updateTrigger: (ButtonScope.() -> Flow<Any?>)? = null,
                visible: @Composable ButtonScope.() -> Boolean = alwaysVisible,
                enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
                exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
                `_`: Nothing = nothing,
            ) = Button(
                id = Id.stickersLibrary,
                vectorIcon = vectorIcon,
                text = text,
                tint = tint,
                enabled = enabled,
                onClick = onClick,
                updateTrigger = updateTrigger,
                visible = visible,
                enterTransition = enterTransition,
                exitTransition = exitTransition,
                `_` = `_`,
            )

            /**
             * A helper function that returns a [Dock.Button] that opens a library sheet with audios via [EditorEvent.OpenLibrarySheet].
             * By default [ly.img.editor.core.library.AssetLibrary.audios] will be displayed on the sheet.
             *
             * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
             * @param text the text content of the button as a string. If null then text is not rendered.
             * @param tint the tint color of the content. If null then no tint is applied.
             * @param enabled whether the button is enabled.
             * @param onClick the callback that is invoked when the button is clicked.
             * @param updateTrigger a flow that is used to trigger recomposition of all functions with signature @Composable Scope.() -> {}
             * in this component.
             * @param visible whether the button should be visible based on the [Engine]'s current state.
             * @param enterTransition transition of the button when it enters the parent composable.
             * @param exitTransition transition of the button when it exits the parent composable.
             */
            fun audiosLibrary(
                vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.Addaudio },
                text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_audio) },
                tint: (@Composable ButtonScope.() -> Color)? = null,
                enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
                onClick: ButtonScope.() -> Unit = {
                    editorContext.eventHandler.send(
                        EditorEvent.OpenLibrarySheet(
                            libraryCategory = editorContext.assetLibrary.audios(editorContext.engine.scene.getMode()),
                            isFloating = true,
                        ),
                    )
                },
                updateTrigger: (ButtonScope.() -> Flow<Any?>)? = null,
                visible: @Composable ButtonScope.() -> Boolean = alwaysVisible,
                enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
                exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
                `_`: Nothing = nothing,
            ) = Button(
                id = Id.audiosLibrary,
                vectorIcon = vectorIcon,
                text = text,
                tint = tint,
                enabled = enabled,
                onClick = onClick,
                updateTrigger = updateTrigger,
                visible = visible,
                enterTransition = enterTransition,
                exitTransition = exitTransition,
                `_` = `_`,
            )

            /**
             * A helper function that returns a [Dock.Button] that opens the system gallery via [EditorEvent.LaunchContract].
             *
             * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
             * @param text the text content of the button as a string. If null then text is not rendered.
             * @param tint the tint color of the content. If null then no tint is applied.
             * @param enabled whether the button is enabled.
             * @param onClick the callback that is invoked when the button is clicked.
             * @param updateTrigger a flow that is used to trigger recomposition of all functions with signature @Composable Scope.() -> {}
             * in this component.
             * @param visible whether the button should be visible based on the [Engine]'s current state.
             * @param enterTransition transition of the button when it enters the parent composable.
             * @param exitTransition transition of the button when it exits the parent composable.
             */
            fun systemGallery(
                vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.Addgalleryforeground },
                text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_gallery) },
                tint: (@Composable ButtonScope.() -> Color)? = null,
                enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
                onClick: ButtonScope.() -> Unit = {
                    val mediaType =
                        if (editorContext.engine.scene.getMode() == SceneMode.VIDEO) {
                            ActivityResultContracts.PickVisualMedia.ImageAndVideo
                        } else {
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        }
                    val request = PickVisualMediaRequest(mediaType)
                    val event =
                        EditorEvent.LaunchContract(ActivityResultContracts.PickVisualMedia(), request) {
                            it?.let {
                                val uploadAssetSourceType =
                                    if (editorContext.activity.contentResolver.getType(it)?.startsWith("video") == true) {
                                        AssetSourceType.VideoUploads
                                    } else {
                                        AssetSourceType.ImageUploads
                                    }
                                editorContext.eventHandler.send(
                                    EditorEvent.AddUriToScene(uploadAssetSourceType, it, addToBackgroundTrack = true),
                                )
                            }
                        }
                    editorContext.eventHandler.send(event)
                },
                updateTrigger: (ButtonScope.() -> Flow<Any?>)? = null,
                visible: @Composable ButtonScope.() -> Boolean = alwaysVisible,
                enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
                exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
                `_`: Nothing = nothing,
            ) = Button(
                id = Id.systemGallery,
                vectorIcon = vectorIcon,
                text = text,
                tint = tint,
                enabled = enabled,
                onClick = onClick,
                updateTrigger = updateTrigger,
                visible = visible,
                enterTransition = enterTransition,
                exitTransition = exitTransition,
                `_` = `_`,
            )

            /**
             * A helper function that returns a [Dock.Button] that opens the system camera via [EditorEvent.LaunchContract].
             *
             * @param uri the uri that should be used to store the content of the taken picture by the camera app.
             * @param launchContract the contract that should be launched when clicked.
             * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
             * @param text the text content of the button as a string. If null then text is not rendered.
             * @param tint the tint color of the content. If null then no tint is applied.
             * @param enabled whether the button is enabled.
             * @param updateTrigger a flow that is used to trigger recomposition of all functions with signature @Composable Scope.() -> {}
             * in this component.
             * @param visible whether the button should be visible based on the [Engine]'s current state.
             * @param enterTransition transition of the button when it enters the parent composable.
             * @param exitTransition transition of the button when it exits the parent composable.
             */
            fun systemCamera(
                uri: (Context) -> Uri = { context ->
                    File.createTempFile("imgly_", null, context.filesDir).let {
                        FileProvider.getUriForFile(context, "${context.packageName}.ly.img.editor.fileprovider", it)
                    }
                },
                launchContract: ButtonScope.() -> ActivityResultContract<Uri, Boolean> = {
                    if (editorContext.engine.scene.getMode() == SceneMode.VIDEO) {
                        ActivityResultContracts.CaptureVideo()
                    } else {
                        ActivityResultContracts.TakePicture()
                    }
                },
                onUriReady: EditorScope.(Uri) -> Unit = {
                    val assetSourceType =
                        if (editorContext.engine.scene.getMode() == SceneMode.VIDEO) {
                            AssetSourceType.VideoUploads
                        } else {
                            AssetSourceType.ImageUploads
                        }
                    editorContext.eventHandler.send(
                        EditorEvent.AddUriToScene(assetSourceType, it, addToBackgroundTrack = true),
                    )
                },
                vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.Addcameraforegound },
                text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_camera) },
                tint: (@Composable ButtonScope.() -> Color)? = null,
                enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
                updateTrigger: (ButtonScope.() -> Flow<Any?>)? = null,
                visible: @Composable ButtonScope.() -> Boolean = alwaysVisible,
                enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
                exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
                `_`: Nothing = nothing,
            ): Button {
                val onClick: ButtonScope.() -> Unit = {
                    val actualUri = uri(editorContext.activity)
                    val event =
                        EditorEvent.LaunchContract(launchContract(), actualUri) {
                            if (it) {
                                onUriReady(actualUri)
                            }
                        }
                    editorContext.eventHandler.send(event)
                }
                return Button(
                    id = Id.systemCamera,
                    vectorIcon = vectorIcon,
                    text = text,
                    tint = tint,
                    enabled = enabled,
                    onClick = onClick,
                    updateTrigger = updateTrigger,
                    visible = visible,
                    enterTransition = enterTransition,
                    exitTransition = exitTransition,
                    `_` = `_`,
                )
            }

            /**
             * A helper function that returns a [Dock.Button] that opens the imgly camera via [EditorEvent.LaunchContract].
             * IMPORTANT: Make sure your app has the dependency of ly.img:camera:<version> next to the ly.img:editor:<version> dependency.
             * Also make sure that their versions match. Failing to provide the dependency will result to a crash.
             *
             * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
             * @param text the text content of the button as a string. If null then text is not rendered.
             * @param tint the tint color of the content. If null then no tint is applied.
             * @param enabled whether the button is enabled.
             * @param onClick the callback that is invoked when the button is clicked.
             * @param updateTrigger a flow that is used to trigger recomposition of all functions with signature @Composable Scope.() -> {}
             * in this component.
             * @param visible whether the button should be visible based on the [Engine]'s current state.
             * @param enterTransition transition of the button when it enters the parent composable.
             * @param exitTransition transition of the button when it exits the parent composable.
             */
            fun imglyCamera(
                vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.Addcameraforegound },
                text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_camera) },
                tint: (@Composable ButtonScope.() -> Color)? = null,
                enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
                onClick: ButtonScope.() -> Unit = {
                    EditorEvent.LaunchContract(
                        contract = CaptureVideo(),
                        input =
                            CaptureVideo.Input(
                                engineConfiguration =
                                    EngineConfiguration(
                                        license = editorContext.engine.editor.getActiveLicense(),
                                        userId = editorContext.userId,
                                    ),
                            ),
                        onOutput = {
                            (it as? CameraResult.Record)
                                ?.recordings
                                ?.map { recording ->
                                    Pair(recording.videos.first().uri, recording.duration)
                                }?.let { recordings ->
                                    editorContext.eventHandler.send(
                                        EditorEvent.AddCameraRecordingsToScene(
                                            uploadAssetSourceType = AssetSourceType.VideoUploads,
                                            recordings = recordings,
                                        ),
                                    )
                                }
                        },
                    ).let { editorContext.eventHandler.send(it) }
                },
                updateTrigger: (ButtonScope.() -> Flow<Any?>)? = null,
                visible: @Composable ButtonScope.() -> Boolean = alwaysVisible,
                enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
                exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
                `_`: Nothing = nothing,
            ) = Button(
                id = Id.imglyCamera,
                vectorIcon = vectorIcon,
                text = text,
                tint = tint,
                enabled = enabled,
                onClick = onClick,
                updateTrigger = updateTrigger,
                visible = visible,
                enterTransition = enterTransition,
                exitTransition = exitTransition,
                `_` = `_`,
            ).apply {
                // This will throw NoClassDefFoundError if img.ly camera is not available
                CaptureVideo()
            }

            /**
             * A helper function that returns a [Dock.Button] that opens reorder sheet via [EditorEvent.OpenReorderSheet].
             * This button is applicable for scenes with mode [SceneMode.VIDEO].
             *
             * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
             * @param text the text content of the button as a string. If null then text is not rendered.
             * @param tint the tint color of the content. If null then no tint is applied.
             * @param enabled whether the button is enabled.
             * @param onClick the callback that is invoked when the button is clicked.
             * @param updateTrigger a flow that is used to trigger recomposition of all functions with signature @Composable Scope.() -> {}
             * in this component.
             * @param visible whether the button should be visible based on the [Engine]'s current state.
             * @param enterTransition transition of the button when it enters the parent composable.
             * @param exitTransition transition of the button when it exits the parent composable.
             */
            fun reorder(
                vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.Reorderhorizontally },
                text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_reorder) },
                tint: (@Composable ButtonScope.() -> Color)? = null,
                enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
                onClick: ButtonScope.() -> Unit = {
                    editorContext.eventHandler.send(EditorEvent.OpenReorderSheet(isFloating = false))
                },
                updateTrigger: (ButtonScope.() -> Flow<Any?>)? = null,
                visible: @Composable ButtonScope.() -> Boolean = {
                    remember(this) {
                        editorContext.engine.block.getBackgroundTrack()?.let { backgroundTrack ->
                            editorContext.engine.block.getChildren(backgroundTrack).size >= 2
                        } ?: false
                    }
                },
                enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
                exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
                `_`: Nothing = nothing,
            ) = Button(
                id = Id.reorder,
                vectorIcon = vectorIcon,
                text = text,
                tint = tint,
                enabled = enabled,
                onClick = onClick,
                updateTrigger = updateTrigger,
                visible = visible,
                enterTransition = enterTransition,
                exitTransition = exitTransition,
                `_` = `_`,
            )

            /**
             * A helper function that returns a [Dock.Button] that opens adjustments sheet via [EditorEvent.OpenAdjustmentsSheet].
             *
             * @param targetDesignBlock the design block that should be used to show the sheet.
             * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
             * @param text the text content of the button as a string. If null then text is not rendered.
             * @param tint the tint color of the content. If null then no tint is applied.
             * @param enabled whether the button is enabled.
             * @param onClick the callback that is invoked when the button is clicked.
             * @param updateTrigger a flow that is used to trigger recomposition of all functions with signature @Composable Scope.() -> {}
             * in this component.
             * @param visible whether the button should be visible based on the [Engine]'s current state.
             * @param enterTransition transition of the button when it enters the parent composable.
             * @param exitTransition transition of the button when it exits the parent composable.
             */
            fun adjustments(
                targetDesignBlock: ButtonScope.() -> DesignBlock = { editorContext.engine.scene.getPages().first() },
                vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.Adjustments },
                text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_adjustments) },
                tint: (@Composable ButtonScope.() -> Color)? = null,
                enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
                onClick: ButtonScope.() -> Unit = {
                    editorContext.eventHandler.send(EditorEvent.OpenAdjustmentsSheet(isFloating = false))
                },
                updateTrigger: (ButtonScope.() -> Flow<Any?>)? = null,
                visible: @Composable ButtonScope.() -> Boolean = {
                    remember(this) {
                        editorContext.engine.block.isAllowedByScope(targetDesignBlock(), EngineScope.AppearanceAdjustment)
                    }
                },
                enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
                exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
                `_`: Nothing = nothing,
            ) = Button(
                id = Id.adjustments,
                vectorIcon = vectorIcon,
                text = text,
                tint = tint,
                enabled = enabled,
                onClick = onClick,
                updateTrigger = updateTrigger,
                visible = visible,
                enterTransition = enterTransition,
                exitTransition = exitTransition,
                `_` = `_`,
            )

            /**
             * A helper function that returns a [Dock.Button] that opens filter sheet via [EditorEvent.OpenFilterSheet].
             *
             * @param targetDesignBlock the design block that should be used to show the sheet.
             * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
             * @param text the text content of the button as a string. If null then text is not rendered.
             * @param tint the tint color of the content. If null then no tint is applied.
             * @param enabled whether the button is enabled.
             * @param onClick the callback that is invoked when the button is clicked.
             * @param updateTrigger a flow that is used to trigger recomposition of all functions with signature @Composable Scope.() -> {}
             * in this component.
             * @param visible whether the button should be visible based on the [Engine]'s current state.
             * @param enterTransition transition of the button when it enters the parent composable.
             * @param exitTransition transition of the button when it exits the parent composable.
             */
            fun filter(
                targetDesignBlock: ButtonScope.() -> DesignBlock = { editorContext.engine.scene.getPages().first() },
                vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.Filter },
                text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_filter) },
                tint: (@Composable ButtonScope.() -> Color)? = null,
                enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
                onClick: ButtonScope.() -> Unit = {
                    editorContext.eventHandler.send(EditorEvent.OpenFilterSheet(isFloating = false))
                },
                updateTrigger: (ButtonScope.() -> Flow<Any?>)? = null,
                visible: @Composable ButtonScope.() -> Boolean = {
                    remember(this) {
                        editorContext.engine.block.isAllowedByScope(targetDesignBlock(), EngineScope.AppearanceFilter)
                    }
                },
                enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
                exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
                `_`: Nothing = nothing,
            ) = Button(
                id = Id.filter,
                vectorIcon = vectorIcon,
                text = text,
                tint = tint,
                enabled = enabled,
                onClick = onClick,
                updateTrigger = updateTrigger,
                visible = visible,
                enterTransition = enterTransition,
                exitTransition = exitTransition,
                `_` = `_`,
            )

            /**
             * A helper function that returns a [Dock.Button] that opens effect sheet via [EditorEvent.OpenEffectSheet].
             *
             * @param targetDesignBlock the design block that should be used to show the sheet.
             * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
             * @param text the text content of the button as a string. If null then text is not rendered.
             * @param tint the tint color of the content. If null then no tint is applied.
             * @param enabled whether the button is enabled.
             * @param onClick the callback that is invoked when the button is clicked.
             * @param updateTrigger a flow that is used to trigger recomposition of all functions with signature @Composable Scope.() -> {}
             * in this component.
             * @param visible whether the button should be visible based on the [Engine]'s current state.
             * @param enterTransition transition of the button when it enters the parent composable.
             * @param exitTransition transition of the button when it exits the parent composable.
             */
            fun effect(
                targetDesignBlock: ButtonScope.() -> DesignBlock = { editorContext.engine.scene.getPages().first() },
                vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.Effect },
                text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_effect) },
                tint: (@Composable ButtonScope.() -> Color)? = null,
                enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
                onClick: ButtonScope.() -> Unit = {
                    editorContext.eventHandler.send(EditorEvent.OpenEffectSheet(isFloating = false))
                },
                updateTrigger: (ButtonScope.() -> Flow<Any?>)? = null,
                visible: @Composable ButtonScope.() -> Boolean = {
                    remember(this) {
                        editorContext.engine.block.isAllowedByScope(targetDesignBlock(), EngineScope.AppearanceEffect)
                    }
                },
                enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
                exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
                `_`: Nothing = nothing,
            ) = Button(
                id = Id.effect,
                vectorIcon = vectorIcon,
                text = text,
                tint = tint,
                enabled = enabled,
                onClick = onClick,
                updateTrigger = updateTrigger,
                visible = visible,
                enterTransition = enterTransition,
                exitTransition = exitTransition,
                `_` = `_`,
            )

            /**
             * A helper function that returns a [Dock.Button] that opens blur sheet via [EditorEvent.OpenBlurSheet].
             *
             * @param targetDesignBlock the design block that should be used to show the sheet.
             * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
             * @param text the text content of the button as a string. If null then text is not rendered.
             * @param tint the tint color of the content. If null then no tint is applied.
             * @param enabled whether the button is enabled.
             * @param onClick the callback that is invoked when the button is clicked.
             * @param updateTrigger a flow that is used to trigger recomposition of all functions with signature @Composable Scope.() -> {}
             * in this component.
             * @param visible whether the button should be visible based on the [Engine]'s current state.
             * @param enterTransition transition of the button when it enters the parent composable.
             * @param exitTransition transition of the button when it exits the parent composable.
             */
            fun blur(
                targetDesignBlock: ButtonScope.() -> DesignBlock = { editorContext.engine.scene.getPages().first() },
                vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.Blur },
                text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_blur) },
                tint: (@Composable ButtonScope.() -> Color)? = null,
                enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
                onClick: ButtonScope.() -> Unit = {
                    editorContext.eventHandler.send(EditorEvent.OpenBlurSheet(isFloating = false))
                },
                updateTrigger: (ButtonScope.() -> Flow<Any?>)? = null,
                visible: @Composable ButtonScope.() -> Boolean = {
                    remember(this) {
                        editorContext.engine.block.isAllowedByScope(targetDesignBlock(), EngineScope.AppearanceBlur)
                    }
                },
                enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
                exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
                `_`: Nothing = nothing,
            ) = Button(
                id = Id.blur,
                vectorIcon = vectorIcon,
                text = text,
                tint = tint,
                enabled = enabled,
                onClick = onClick,
                updateTrigger = updateTrigger,
                visible = visible,
                enterTransition = enterTransition,
                exitTransition = exitTransition,
                `_` = `_`,
            )

            /**
             * A helper function that returns a [Dock.Button] that opens crop sheet via [EditorEvent.OpenCropSheet].
             *
             * @param targetDesignBlock the design block that should be used to show the sheet.
             * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
             * @param text the text content of the button as a string. If null then text is not rendered.
             * @param tint the tint color of the content. If null then no tint is applied.
             * @param enabled whether the button is enabled.
             * @param onClick the callback that is invoked when the button is clicked.
             * @param updateTrigger a flow that is used to trigger recomposition of all functions with signature @Composable Scope.() -> {}
             * in this component.
             * @param visible whether the button should be visible based on the [Engine]'s current state.
             * @param enterTransition transition of the button when it enters the parent composable.
             * @param exitTransition transition of the button when it exits the parent composable.
             */
            fun crop(
                targetDesignBlock: ButtonScope.() -> DesignBlock = { editorContext.engine.scene.getPages().first() },
                vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = { IconPack.Croprotate },
                text: (@Composable ButtonScope.() -> String)? = { stringResource(R.string.ly_img_editor_crop) },
                tint: (@Composable ButtonScope.() -> Color)? = null,
                enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
                onClick: ButtonScope.() -> Unit = {
                    editorContext.eventHandler.send(EditorEvent.OpenCropSheet(isFloating = false))
                },
                updateTrigger: (ButtonScope.() -> Flow<Any?>)? = null,
                visible: @Composable ButtonScope.() -> Boolean = {
                    remember(this) {
                        targetDesignBlock().let {
                            editorContext.engine.block.supportsCrop(it) &&
                                editorContext.engine.block.isAllowedByScope(it, EngineScope.LayerCrop)
                        }
                    }
                },
                enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
                exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
                `_`: Nothing = nothing,
            ) = Button(
                id = Id.crop,
                vectorIcon = vectorIcon,
                text = text,
                tint = tint,
                enabled = enabled,
                onClick = onClick,
                updateTrigger = updateTrigger,
                visible = visible,
                enterTransition = enterTransition,
                exitTransition = exitTransition,
                `_` = `_`,
            )
        }
    }

    companion object {
        /**
         * The default implementation of the dock update trigger.
         * This method is used and is strongly tied with [rememberDefault].
         * Whenever a background track design block is added/removed and whenever the number of its children becomes
         * less or more than 2, a trigger event is emitted.
         *
         * @param scope the scope of the [Dock] component.
         * @return a flow that will render the dock when any value is emitted.
         */
        @OptIn(ExperimentalCoroutinesApi::class)
        fun getDefaultTrigger(scope: Scope): Flow<Any?> {
            // Background track might not be immediately available
            val engine = scope.run { editorContext.engine }
            return engine.event.subscribe(engine.scene.getPages())
                .onStart { emit(emptyList()) }
                .map { engine.block.getBackgroundTrack() }
                .distinctUntilChanged() // Recollect when background track is removed or changed.
                .flatMapLatest { backgroundTrack ->
                    if (backgroundTrack == null) return@flatMapLatest flowOf(Unit)
                    engine.event.subscribe(listOf(backgroundTrack))
                        .map {
                            // Map to the number of children in the background track
                            engine.block.getChildren(backgroundTrack).size
                        }
                        .distinctUntilChanged { old, new ->
                            // Trigger should only occur if the number of children has changed compared to 2.
                            val hasChanged = (new >= 2 && old < 2) || (new < 2 && old >= 2)
                            hasChanged.not()
                        }
                }
        }

        /**
         * A composable function that creates and remembers a [Dock] instance.
         *
         * @param items a callback that is invoked when the dock is about to be rendered. The returned list of components is displayed in
         * a horizontal list at the bottom of the editor.
         * Note that the height of components should be at most 64dp which is enforced in the [Item.Content].
         * @param horizontalArrangement the horizontal arrangement that should be used to render the items in the dock horizontally.
         * Default value is [Arrangement.SpaceEvenly].
         * @param scopeProvider a provider of the scope of the current component based on the parent scope.
         * @param updateTrigger a flow that is used to trigger recomposition of all functions with signature @Composable Scope.() -> {}
         * in this component and in all [items] if [EditorComponent.shouldReceiveUpdateFromParent] returns true for them.
         * @param visible whether the dock should be visible based on the [Engine]'s current state.
         * @param enterTransition transition of the dock when it enters the parent composable.
         * @param exitTransition transition of the dock when it exits the parent composable.
         */
        @Composable
        fun remember(
            items: Scope.() -> List<Item<*>>,
            horizontalArrangement: @Composable Scope.() -> Arrangement.Horizontal = { Arrangement.SpaceEvenly },
            scopeProvider: (EditorScope) -> Scope = { Scope(it) },
            updateTrigger: (Scope.() -> Flow<Any?>)? = ::getDefaultTrigger,
            visible: @Composable Scope.() -> Boolean = alwaysVisible,
            enterTransition: @Composable Scope.() -> EnterTransition = noneEnterTransition,
            exitTransition: @Composable Scope.() -> ExitTransition = noneExitTransition,
            `_`: Nothing = nothing,
        ): Dock =
            remember {
                Dock(
                    items = items,
                    horizontalArrangement = horizontalArrangement,
                    scopeProvider = scopeProvider,
                    updateTrigger = updateTrigger,
                    visible = visible,
                    enterTransition = enterTransition,
                    exitTransition = exitTransition,
                    `_` = `_`,
                )
            }

        /**
         * A composable helper function that creates and remembers a [Dock] instance when launching [ly.img.editor.DesignEditor].
         * By default, following items are displayed in the dock:
         *
         * - Dock.Button.elementsLibrary
         * - Dock.Button.systemGallery
         * - Dock.Button.systemCamera
         * - Dock.Button.imagesLibrary
         * - Dock.Button.textLibrary
         * - Dock.Button.shapesLibrary
         * - Dock.Button.stickersLibrary
         *
         * For more information and an example on how [replaceDefaultItem], [additionalItems] and [order] work with the default list
         * check [rememberDefault].
         *
         * @param replaceDefaultItem a callback that allows overriding the [Dock.Item]s in the default list.
         * Default value is null, which makes no changes to the original list.
         * @param additionalItems an optional list of additional [Dock.Item]s that will be appended to the default list.
         * Default value is null.
         * @param order an optional list that sets the display order of the [Dock.Item]s. It should only contain the ids of
         * the default list after the [replaceDefaultItem] + [additionalItems] ids. If an id that is present in the default list or
         * [additionalItems] is skipped in this list, then it will not be rendered in the dock.
         * Default value is null, which makes no changes after [replaceDefaultItem] and appending [additionalItems].
         * @param horizontalArrangement the horizontal arrangement that should be used to render the items in the dock horizontally.
         * Default value is [Arrangement.SpaceEvenly].
         * @param updateTrigger a flow that is used to trigger recomposition of all functions with signature @Composable Scope.() -> {}
         * in this component and in all [items] if [EditorComponent.shouldReceiveUpdateFromParent] returns true for them.
         * @param visible whether the dock should be visible based on the [Engine]'s current state.
         * Default value is always visible.
         * @param enterTransition transition of the dock when it enters the parent composable.
         * Default value is no enter transition.
         * @param exitTransition transition of the dock when it exits the parent composable.
         * Default value is no exit transition.
         * @return a dock that will be displayed when launching a [ly.img.editor.DesignEditor].
         */
        @UnstableEditorApi
        @Composable
        fun rememberForDesign(
            replaceDefaultItem: ((Item<*>) -> Item<*>)? = null,
            additionalItems: List<Item<*>>? = null,
            order: List<EditorComponentId>? = null,
            horizontalArrangement: @Composable Scope.() -> Arrangement.Horizontal = { Arrangement.SpaceEvenly },
            updateTrigger: (Scope.() -> Flow<Any?>)? = ::getDefaultTrigger,
            visible: @Composable Scope.() -> Boolean = alwaysVisible,
            enterTransition: @Composable Scope.() -> EnterTransition = noneEnterTransition,
            exitTransition: @Composable Scope.() -> ExitTransition = noneExitTransition,
            `_`: Nothing = nothing,
        ): Dock =
            rememberDefault(
                defaultItems =
                    remember {
                        listOf(
                            Button.elementsLibrary(),
                            Button.systemGallery(),
                            Button.systemCamera(),
                            Button.imagesLibrary(),
                            Button.textLibrary(),
                            Button.shapesLibrary(),
                            Button.stickersLibrary(),
                        )
                    },
                replaceDefaultItem = replaceDefaultItem,
                additionalItems = additionalItems,
                order = order,
                horizontalArrangement = horizontalArrangement,
                updateTrigger = updateTrigger,
                visible = visible,
                enterTransition = enterTransition,
                exitTransition = exitTransition,
                `_` = `_`,
            )

        /**
         * A composable helper function that creates and remembers a [Dock] instance when launching [ly.img.editor.PhotoEditor].
         * By default, following items are displayed in the dock:
         *
         * - Dock.Button.adjustments
         * - Dock.Button.filter
         * - Dock.Button.effect
         * - Dock.Button.blur
         * - Dock.Button.crop
         * - Dock.Button.textLibrary
         * - Dock.Button.shapesLibrary
         * - Dock.Button.stickersLibrary
         *
         * For more information and an example on how [replaceDefaultItem], [additionalItems] and [order] work with the default list
         * check [rememberDefault].
         *
         * @param replaceDefaultItem a callback that allows overriding the [Dock.Item]s in the default list.
         * Default value is null, which makes no changes to the original list.
         * @param additionalItems an optional list of additional [Dock.Item]s that will be appended to the default list.
         * Default value is null.
         * @param order an optional list that sets the display order of the [Dock.Item]s. It should only contain the ids of
         * the default list after the [replaceDefaultItem] + [additionalItems] ids. If an id that is present in the default list or
         * [additionalItems] is skipped in this list, then it will not be rendered in the dock.
         * Default value is null, which makes no changes after [replaceDefaultItem] and appending [additionalItems].
         * @param horizontalArrangement the horizontal arrangement that should be used to render the items in the dock horizontally.
         * Default value is [Arrangement.SpaceEvenly].
         * @param updateTrigger a flow that is used to trigger recomposition of all functions with signature @Composable Scope.() -> {}
         * in this component and in all [items] if [EditorComponent.shouldReceiveUpdateFromParent] returns true for them.
         * @param visible whether the dock should be visible based on the [Engine]'s current state.
         * Default value is always visible.
         * @param enterTransition transition of the dock when it enters the parent composable.
         * Default value is no enter transition.
         * @param exitTransition transition of the dock when it exits the parent composable.
         * Default value is no exit transition.
         * @return a dock that will be displayed when launching a [ly.img.editor.PhotoEditor].
         */
        @UnstableEditorApi
        @Composable
        fun rememberForPhoto(
            replaceDefaultItem: ((Item<*>) -> Item<*>)? = null,
            additionalItems: List<Item<*>>? = null,
            order: List<EditorComponentId>? = null,
            horizontalArrangement: @Composable Scope.() -> Arrangement.Horizontal = { Arrangement.SpaceEvenly },
            updateTrigger: (Scope.() -> Flow<Any?>)? = ::getDefaultTrigger,
            visible: @Composable Scope.() -> Boolean = alwaysVisible,
            enterTransition: @Composable Scope.() -> EnterTransition = noneEnterTransition,
            exitTransition: @Composable Scope.() -> ExitTransition = noneExitTransition,
            `_`: Nothing = nothing,
        ): Dock =
            rememberDefault(
                defaultItems =
                    remember {
                        listOf(
                            Button.adjustments(),
                            Button.filter(),
                            Button.effect(),
                            Button.blur(),
                            Button.crop(),
                            Button.textLibrary(),
                            Button.shapesLibrary(),
                            Button.stickersLibrary(),
                        )
                    },
                replaceDefaultItem = replaceDefaultItem,
                additionalItems = additionalItems,
                order = order,
                horizontalArrangement = horizontalArrangement,
                updateTrigger = updateTrigger,
                visible = visible,
                enterTransition = enterTransition,
                exitTransition = exitTransition,
                `_` = `_`,
            )

        /**
         * A composable helper function that creates and remembers a [Dock] instance when launching [ly.img.editor.VideoEditor].
         * Make sure to add the gradle dependency of our camera library if you want to use the [Dock.Button.imglyCamera] button:
         * implementation "ly.img:camera:<same version as editor>".
         * If the dependency is missing, then [Dock.Button.systemCamera] is be used.
         *
         * By default, following items are displayed in the dock:
         *
         * - Dock.Button.systemGallery
         * - Dock.Button.imglyCamera or Dock.Button.systemCamera // depending on ly.img:camera dependency presence
         * - Dock.Button.overlaysLibrary
         * - Dock.Button.textLibrary
         * - Dock.Button.stickersLibrary
         * - Dock.Button.audiosLibrary
         * - Dock.Button.reorder
         *
         * For more information and an example on how [replaceDefaultItem], [additionalItems] and [order] work with the default list
         * check [rememberDefault].
         *
         * @param replaceDefaultItem a callback that allows overriding the [Dock.Item]s in the default list.
         * Default value is null, which makes no changes to the original list.
         * @param additionalItems an optional list of additional [Dock.Item]s that will be appended to the default list.
         * Default value is null.
         * @param order an optional list that sets the display order of the [Dock.Item]s. It should only contain the ids of
         * the default list after the [replaceDefaultItem] + [additionalItems] ids. If an id that is present in the default list or
         * [additionalItems] is skipped in this list, then it will not be rendered in the dock.
         * Default value is null, which makes no changes after [replaceDefaultItem] and appending [additionalItems].
         * @param horizontalArrangement the horizontal arrangement that should be used to render the items in the dock horizontally.
         * Default value is [Arrangement.SpaceEvenly].
         * @param updateTrigger a flow that is used to trigger recomposition of all functions with signature @Composable Scope.() -> {}
         * in this component and in all [items] if [EditorComponent.shouldReceiveUpdateFromParent] returns true for them.
         * @param visible whether the dock should be visible based on the [Engine]'s current state.
         * Default value is always visible.
         * @param enterTransition transition of the dock when it enters the parent composable.
         * Default value is no enter transition.
         * @param exitTransition transition of the dock when it exits the parent composable.
         * Default value is no exit transition.
         * @return a dock that will be displayed when launching a [ly.img.editor.VideoEditor].
         */
        @UnstableEditorApi
        @Composable
        fun rememberForVideo(
            replaceDefaultItem: ((Item<*>) -> Item<*>)? = null,
            additionalItems: List<Item<*>>? = null,
            order: List<EditorComponentId>? = null,
            horizontalArrangement: @Composable Scope.() -> Arrangement.Horizontal = { Arrangement.SpaceEvenly },
            updateTrigger: (Scope.() -> Flow<Any?>)? = ::getDefaultTrigger,
            visible: @Composable Scope.() -> Boolean = alwaysVisible,
            enterTransition: @Composable Scope.() -> EnterTransition = noneEnterTransition,
            exitTransition: @Composable Scope.() -> ExitTransition = noneExitTransition,
            `_`: Nothing = nothing,
        ): Dock =
            rememberDefault(
                defaultItems =
                    remember {
                        listOf(
                            Button.systemGallery(
                                vectorIcon = { IconPack.Addgallerybackground },
                            ),
                            runCatching {
                                Button.imglyCamera(
                                    vectorIcon = { IconPack.Addcamerabackground },
                                )
                            }.getOrElse {
                                Button.systemCamera(
                                    vectorIcon = { IconPack.Addcamerabackground },
                                )
                            },
                            Button.overlaysLibrary(),
                            Button.textLibrary(),
                            Button.stickersLibrary(),
                            Button.audiosLibrary(),
                            Button.reorder(),
                        )
                    },
                replaceDefaultItem = replaceDefaultItem,
                additionalItems = additionalItems,
                order = order,
                horizontalArrangement = horizontalArrangement,
                updateTrigger = updateTrigger,
                visible = visible,
                enterTransition = enterTransition,
                exitTransition = exitTransition,
                `_` = `_`,
            )

        /**
         * A composable helper function that creates and remembers a [Dock] based on [defaultItems].
         * Let's say we have the following default [defaultItems] that matches with [rememberForDesign]:
         *
         * - Dock.Button.elementsLibrary
         * - Dock.Button.systemGallery
         * - Dock.Button.systemCamera
         * - Dock.Button.imagesLibrary
         * - Dock.Button.textLibrary
         * - Dock.Button.shapesLibrary
         * - Dock.Button.stickersLibrary
         *
         * When [replaceDefaultItem], [additionalItems] and [order] are not specified, the final list of items matches with the [defaultItems].
         *
         * Parameter [replaceDefaultItem] replaces [Dock.Item]s of the default list above.
         * Parameter [additionalItems] appends [Dock.Item]s to the default list above.
         * Parameter [order] filters out some elements and sets the order.
         *
         * For example, if you want to
         *  - replace the icon of Dock.Button.elementsLibrary,
         *  - drop Dock.Button.systemCamera and Dock.Button.shapesLibrary,
         *  - swap Dock.Button.textLibrary and Dock.Button.stickersLibrary,
         *  - show Dock.Button.stickersLibrary on a changeable condition,
         *  - move additionalItems to the front,
         *  - recompose and rerender the dock and the items on any engine event
         * you should invoke [Dock.rememberForDesign] with the following parameters:
         *
         * var condition by remember {
         *     mutableStateOf(true)
         * }
         * val additionalItems = remember {
         *     listOf(additionalItem0, additionalItem1, ... additionalItemN)
         * }
         * Dock.rememberForDesign(
         *     updateTrigger = {
         *         merge(
         *             Dock.getDefaultTrigger(this),
         *             // Whenever there is a change in the engine, the dock and its items will be recomposed and rerendered.
         *             // Be careful with the trigger as it causes recomposition of all functions with signature @Composable Scope.() -> {}.
         *             // Try to use composable state objects (i.e. "condition" that is declared above) over this whenever possible.
         *             editorContext.engine.event.subscribe()
         *         )
         *     ),
         *     replaceDefaultItem = {
         *         when (it.id) {
         *             Dock.Button.Id.elementsLibrary -> Dock.Button.elementsLibrary(vectorIcon = ...)
         *             Dock.Button.Id.stickersLibrary -> Dock.Button.stickersLibrary(visible = { condition })
         *             else -> it
         *         }
         *     },
         *     additionalItems = additionalItems,
         *     order = listOf(
         *         additionalItems[0].id,
         *         additionalItems[1].id,
         *         ...
         *         additionalItems[additionalItems.size - 1].id,
         *         Dock.Button.Id.elementsLibrary,
         *         Dock.Button.Id.systemGallery,
         *         Dock.Button.Id.imagesLibrary,
         *         Dock.Button.Id.stickersLibrary
         *         Dock.Button.Id.textLibrary
         *     )
         * ).
         *
         * After overriding existing items, adding [additionalItems] and setting the [order], predicate [Item.visible]
         * configures which buttons should be displayed during every render operation. By default [Item.visible] is
         * based on the current state of the [Engine] for all the default buttons.
         *
         * @param defaultItems the default item list.
         * @param replaceDefaultItem a callback that allows overriding the [Dock.Item]s in the default list.
         * Default value is null, which makes no changes to the original list.
         * @param additionalItems an optional list of additional [Dock.Item]s that will be appended to the default list.
         * Default value is null.
         * @param order an optional list that sets the display order of the [Dock.Item]s. It should only contain the ids of
         * the default list after the [replaceDefaultItem] + [additionalItems] ids. If an id that is present in the default list or
         * [additionalItems] is skipped in this list, then it will not be rendered in the dock.
         * Default value is null, which makes no changes after [replaceDefaultItem] and appending [additionalItems].
         * @return a dock that will be displayed when launching an editor.
         * @param horizontalArrangement the horizontal arrangement that should be used to render the items in the dock horizontally.
         * Default value is [Arrangement.SpaceEvenly].
         * @param updateTrigger a flow that is used to trigger recomposition of all functions with signature @Composable Scope.() -> {}
         * in this component and in all [items] if [EditorComponent.shouldReceiveUpdateFromParent] returns true for them.
         * @param visible whether the dock should be visible based on the [Engine]'s current state.
         * Default value is always visible.
         * @param enterTransition transition of the dock when it enters the parent composable.
         * Default value is no enter transition.
         * @param exitTransition transition of the dock when it exits the parent composable.
         * Default value is no exit transition.
         */
        @Composable
        private fun rememberDefault(
            defaultItems: List<Item<*>>,
            replaceDefaultItem: ((Item<*>) -> Item<*>)? = null,
            additionalItems: List<Item<*>>? = null,
            order: List<EditorComponentId>? = null,
            horizontalArrangement: @Composable Scope.() -> Arrangement.Horizontal = { Arrangement.SpaceEvenly },
            updateTrigger: (Scope.() -> Flow<Any?>)? = ::getDefaultTrigger,
            visible: @Composable Scope.() -> Boolean = alwaysVisible,
            enterTransition: @Composable Scope.() -> EnterTransition = noneEnterTransition,
            exitTransition: @Composable Scope.() -> ExitTransition = noneExitTransition,
            `_`: Nothing = nothing,
        ): Dock =
            remember(
                items = {
                    val replacedItems = replaceDefaultItem?.let { defaultItems.map(it) } ?: defaultItems
                    additionalItems?.let {
                        (replacedItems + additionalItems).orderBy(order)
                    } ?: replacedItems
                },
                horizontalArrangement = horizontalArrangement,
                updateTrigger = updateTrigger,
                visible = visible,
                enterTransition = enterTransition,
                exitTransition = exitTransition,
                `_` = `_`,
            )

        private fun List<Item<*>>.orderBy(order: List<EditorComponentId>?): List<Item<*>> {
            return order?.map { id ->
                requireNotNull(firstOrNull { it.id == id }) {
                    "Order should only contain ids that are present in the defaultItems or in additionalItems."
                }
            } ?: this
        }
    }
}
