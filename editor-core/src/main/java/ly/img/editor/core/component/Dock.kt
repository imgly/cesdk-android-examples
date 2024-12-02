package ly.img.editor.core.component

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
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import ly.img.editor.core.EditorContext
import ly.img.editor.core.EditorScope
import ly.img.editor.core.LocalEditorScope
import ly.img.editor.core.component.Dock.Companion.remember
import ly.img.editor.core.component.Dock.Scope
import ly.img.editor.core.component.data.Nothing
import ly.img.editor.core.component.data.nothing
import ly.img.editor.core.theme.surface1
import ly.img.editor.core.ui.IconTextButton
import ly.img.engine.Engine

/**
 * A component for rendering the dock at the bottom of the editor.
 * Use [remember] composable function in the companion object to create an instance of this class, or use
 * [rememberForDesign], [rememberForPhoto] and [rememberForVideo] helpers that construct solution specific docks.
 *
 * @param listBuilder the list builder of this dock.
 * @param horizontalArrangement the horizontal arrangement that should be used to render the items in the dock horizontally.
 * Default value is [Arrangement.SpaceEvenly].
 * @param scope the scope of this component. Every new value will trigger recomposition of all functions with
 * signature @Composable Scope.() -> {}.
 * If you need to access [EditorScope] to construct the scope, use [LocalEditorScope].
 * @param visible whether the dock should be visible based on the [Engine]'s current state.
 * @param enterTransition transition of the dock when it enters the parent composable.
 * @param exitTransition transition of the dock when it exits the parent composable.
 */
@Stable
class Dock private constructor(
    val listBuilder: EditorComponent.ListBuilder<Item<*>>,
    val horizontalArrangement: @Composable Scope.() -> Arrangement.Horizontal,
    override val scope: Scope,
    override val visible: @Composable Scope.() -> Boolean,
    override val enterTransition: @Composable Scope.() -> EnterTransition,
    override val exitTransition: @Composable Scope.() -> ExitTransition,
    private val `_`: Nothing,
) : EditorComponent<Scope>() {
    override val id: EditorComponentId = EditorComponentId("ly.img.component.dock")

    @Stable
    class ListBuilder {
        companion object {
            /**
             * A composable function that creates and remembers a [ListBuilder] instance.
             *
             * @param block the building block of [ListBuilder].
             * @return a new [ListBuilder] instance.
             */
            @Composable
            fun remember(block: @DisallowComposableCalls EditorComponent.ListBuilder.Scope.New<Item<*>>.() -> Unit) =
                EditorComponent.ListBuilder.remember(block)
        }
    }

    @Composable
    override fun Scope.Content(animatedVisibilityScope: AnimatedVisibilityScope?) {
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
                horizontalArrangement = horizontalArrangement(),
            ) {
                listBuilder.scope.items.forEach {
                    EditorComponent(component = it)
                }
            }
        }
    }

    /**
     * The scope of the [Dock] component.
     *
     * @param parentScope the scope of the parent component.
     */
    @Stable
    open class Scope(
        parentScope: EditorScope,
    ) : EditorScope() {
        override val impl: EditorContext = parentScope.editorContext
    }

    override fun toString(): String {
        return "$`_`Dock(id=$id)"
    }

    /**
     * The scope of the [Item] component.
     *
     * @param parentScope the scope of the parent component.
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
     * @param scope the scope of this component. Every new value will trigger recomposition of all functions with
     * signature @Composable Scope.() -> {}.
     * If you need to access [EditorScope] to construct the scope, use [LocalEditorScope].
     * @param visible whether the custom item should be visible.
     * @param enterTransition transition of the custom item when it enters the parent composable.
     * @param exitTransition transition of the custom item when it exits the parent composable.
     * @param content the content of the component.
     */
    @Stable
    class Custom<Scope : ItemScope> private constructor(
        override val id: EditorComponentId,
        override val scope: Scope,
        override val visible: @Composable Scope.() -> Boolean,
        override val enterTransition: @Composable Scope.() -> EnterTransition,
        override val exitTransition: @Composable Scope.() -> ExitTransition,
        val content: @Composable Scope.() -> Unit,
    ) : Item<Scope>() {
        @Composable
        override fun Scope.ItemContent() {
            content()
        }

        override fun toString(): String {
            return "Dock.Custom(id=$id)"
        }

        companion object {
            /**
             * A composable function that creates and remembers a [Dock.Custom] instance.
             *
             * @param id the id of the custom view.
             * Note that it is highly recommended that every unique [EditorComponent] has a unique id.
             * @param scope the scope of this component. Every new value will trigger recomposition of all functions with
             * signature @Composable Scope.() -> {}.
             * If you need to access [EditorScope] to construct the scope, use [LocalEditorScope].
             * @param visible whether the custom item should be visible.
             * Default value is always true.
             * @param enterTransition transition of the custom item when it enters the parent composable.
             * Default value is always no enter transition.
             * @param exitTransition transition of the custom item when it exits the parent composable.
             * Default value is always no exit transition.
             * @param content the content of the component.
             * @return a custom item that will be displayed in the dock.
             */
            @Composable
            fun <Scope : ItemScope> remember(
                id: EditorComponentId,
                scope: Scope,
                visible: @Composable Scope.() -> Boolean = alwaysVisible,
                enterTransition: @Composable Scope.() -> EnterTransition = noneEnterTransition,
                exitTransition: @Composable Scope.() -> ExitTransition = noneExitTransition,
                content: @Composable Scope.() -> Unit,
            ) = remember(scope, visible, enterTransition, exitTransition, content) {
                Custom(
                    id = id,
                    scope = scope,
                    visible = visible,
                    enterTransition = enterTransition,
                    exitTransition = exitTransition,
                    content = content,
                )
            }
        }
    }

    /**
     * The scope of the [Button] component.
     *
     * @param parentScope the scope of the parent component.
     */
    @Stable
    open class ButtonScope(
        parentScope: EditorScope,
    ) : ItemScope(parentScope)

    /**
     * The scope of the [rememberReorder] button in the dock.
     *
     * @param parentScope the scope of the parent component.
     * @param visible whether reorder button is visible.
     */
    @Stable
    open class ReorderButtonScope(
        parentScope: EditorScope,
        private val visible: Boolean,
    ) : ButtonScope(parentScope) {
        /**
         * Whether reorder button is visible.
         */
        val EditorContext.visible: Boolean
            get() = this@ReorderButtonScope.visible
    }

    /**
     * A component that represents a button in the [Dock].
     *
     * @param id the id of the button.
     * Note that it is highly recommended that every unique [EditorComponent] has a unique id.
     * @param onClick the callback that is invoked when the button is clicked.
     * @param icon the icon content of the button. If null, it will not be rendered.
     * @param text the text content of the button. If null, it will not be rendered.
     * @param enabled whether the button is enabled.
     * @param scope the scope of this component. Every new value will trigger recomposition of all functions with
     * signature @Composable Scope.() -> {}.
     * If you need to access [EditorScope] to construct the scope, use [LocalEditorScope].
     * @param visible whether the button should be visible.
     * @param enterTransition transition of the button when it enters the parent composable.
     * @param exitTransition transition of the button when it exits the parent composable.
     */
    @Stable
    class Button private constructor(
        override val id: EditorComponentId,
        val onClick: ButtonScope.() -> Unit,
        val icon: (@Composable ButtonScope.() -> Unit)?,
        val text: (@Composable ButtonScope.() -> Unit)?,
        val enabled: @Composable ButtonScope.() -> Boolean,
        override val scope: ButtonScope,
        override val visible: @Composable ButtonScope.() -> Boolean,
        override val enterTransition: @Composable ButtonScope.() -> EnterTransition,
        override val exitTransition: @Composable ButtonScope.() -> ExitTransition,
        private val `_`: Nothing,
    ) : Item<ButtonScope>() {
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
            return "$`_`Dock.Button(id=$id)"
        }

        class Id {
            companion object
        }

        companion object {
            /**
             * Predicate to be used when the [EditorComponent] is always enabled.
             */
            val alwaysEnabled: @Composable ButtonScope.() -> Boolean = { true }

            /**
             * A composable function that creates and remembers a [Dock.Button] instance.
             *
             * @param id the id of the button.
             * Note that it is highly recommended that every unique [EditorComponent] has a unique id.
             * @param onClick the callback that is invoked when the button is clicked.
             * @param icon the icon content of the button. If null, it will not be rendered.
             * Default value is null.
             * @param text the text content of the button. If null, it will not be rendered.
             * Default value is null.
             * @param enabled whether the button is enabled.
             * Default value is always true.
             * @param scope the scope of this component. Every new value will trigger recomposition of all functions with
             * signature @Composable Scope.() -> {}.
             * If you need to access [EditorScope] to construct the scope, use [LocalEditorScope].
             * By default it is updated only when the parent component scope ([Dock.scope], accessed via [LocalEditorScope]) is updated.             * @param visible whether the button should be visible.
             * Default value is always true.
             * @param enterTransition transition of the button when it enters the parent composable.
             * Default value is always no enter transition.
             * @param exitTransition transition of the button when it exits the parent composable.
             * Default value is always no exit transition.
             * @return a button that will be displayed in the dock.
             */
            @Composable
            fun remember(
                id: EditorComponentId,
                onClick: ButtonScope.() -> Unit,
                icon: (@Composable ButtonScope.() -> Unit)? = null,
                text: (@Composable ButtonScope.() -> Unit)? = null,
                enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
                scope: ButtonScope =
                    LocalEditorScope.current.run {
                        remember(this) { ButtonScope(parentScope = this) }
                    },
                visible: @Composable ButtonScope.() -> Boolean = alwaysVisible,
                enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
                exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
                `_`: Nothing = nothing,
            ): Button {
                return remember(onClick, icon, text, enabled, scope, visible, enterTransition, exitTransition) {
                    Button(
                        id = id,
                        onClick = onClick,
                        icon = icon,
                        text = text,
                        enabled = enabled,
                        scope = scope,
                        visible = visible,
                        enterTransition = enterTransition,
                        exitTransition = exitTransition,
                        `_` = `_`,
                    )
                }
            }

            /**
             * A composable helper function that creates and remembers a [Dock.Button] instance where [icon] composable is
             * provided via [ImageVector] and [text] composable via [String].
             *
             * @param id the id of the button.
             * Note that it is highly recommended that every unique [EditorComponent] has a unique id.
             * @param onClick the callback that is invoked when the button is clicked.
             * @param vectorIcon the icon content of the button as a vector. If null then icon is not rendered.
             * Default value is null.
             * @param text the text content of the button as a string. If null then text is not rendered.
             * Default value is null.
             * @param tint the tint color of the content. If null then no tint is applied.
             * Default value is null.
             * @param enabled whether the button is enabled.
             * Default value is always true.
             * @param scope the scope of this component. Every new value will trigger recomposition of all functions with
             * signature @Composable Scope.() -> {}.
             * If you need to access [EditorScope] to construct the scope, use [LocalEditorScope].
             * By default it is updated only when the parent component scope ([Dock.scope], accessed via [LocalEditorScope]) is updated.
             * @param visible whether the button should be visible.
             * Default value is always true.
             * @param enterTransition transition of the button when it enters the parent composable.
             * Default value is always no enter transition.
             * @param exitTransition transition of the button when it exits the parent composable.
             * Default value is always no exit transition.
             * @return a button that will be displayed in the dock.
             */
            @Composable
            fun remember(
                id: EditorComponentId,
                onClick: ButtonScope.() -> Unit,
                vectorIcon: (@Composable ButtonScope.() -> ImageVector)? = null,
                text: (@Composable ButtonScope.() -> String)? = null,
                tint: (@Composable ButtonScope.() -> Color)? = null,
                enabled: @Composable ButtonScope.() -> Boolean = alwaysEnabled,
                scope: ButtonScope =
                    LocalEditorScope.current.run {
                        remember(this) { ButtonScope(parentScope = this) }
                    },
                visible: @Composable ButtonScope.() -> Boolean = alwaysVisible,
                enterTransition: @Composable ButtonScope.() -> EnterTransition = noneEnterTransition,
                exitTransition: @Composable ButtonScope.() -> ExitTransition = noneExitTransition,
                `_`: Nothing = nothing,
            ): Button =
                remember(
                    id = id,
                    onClick = onClick,
                    icon =
                        vectorIcon?.let {
                            {
                                Icon(
                                    imageVector = vectorIcon(this),
                                    contentDescription = null,
                                    tint = tint?.invoke(this) ?: LocalContentColor.current,
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
                    scope = scope,
                    visible = visible,
                    enterTransition = enterTransition,
                    exitTransition = exitTransition,
                    `_` = `_`,
                )
        }
    }

    companion object {
        /**
         * A composable function that creates and remembers a [Dock] instance.
         * Consider using [rememberForDesign], [rememberForPhoto] and [rememberForVideo] helpers that construct solution
         * specific docks instead.
         *
         * @param listBuilder a builder that builds the list of [Dock.Item]s that should be part of the dock.
         * Note that adding items to the list does not mean displaying. The items will be displayed if [Dock.Item.visible] is true for them.
         * Also note that items will be rebuilt when [scope] is updated.
         * By default itemBuilder does not add anything to the dock.
         * @param horizontalArrangement the horizontal arrangement that should be used to render the items in the dock horizontally.
         * Default value is [Arrangement.SpaceEvenly].
         * @param scope the scope of this component. Every new value will trigger recomposition of all functions with
         * signature @Composable Scope.() -> {}.
         * If you need to access [EditorScope] to construct the scope, use [LocalEditorScope].
         * By default it is updated only when the parent scope (accessed via [LocalEditorScope]) is updated.
         * @param visible whether the dock should be visible based on the [Engine]'s current state.
         * Default value is always true.
         * @param enterTransition transition of the button when it enters the parent composable.
         * Default value is always no enter transition.
         * @param exitTransition transition of the button when it exits the parent composable.
         * Default value is always no exit transition.
         * @return a dock that will be displayed when launching an editor.
         */
        @Composable
        fun remember(
            listBuilder: EditorComponent.ListBuilder<Item<*>> = EditorComponent.ListBuilder.remember { },
            horizontalArrangement: @Composable Scope.() -> Arrangement.Horizontal = { Arrangement.SpaceEvenly },
            scope: Scope =
                LocalEditorScope.current.run {
                    remember(this) { Scope(parentScope = this) }
                },
            visible: @Composable Scope.() -> Boolean = alwaysVisible,
            enterTransition: @Composable Scope.() -> EnterTransition = noneEnterTransition,
            exitTransition: @Composable Scope.() -> ExitTransition = noneExitTransition,
            `_`: Nothing = nothing,
        ): Dock {
            return remember(listBuilder, horizontalArrangement, scope, visible, enterTransition, exitTransition) {
                Dock(
                    listBuilder = listBuilder,
                    horizontalArrangement = horizontalArrangement,
                    scope = scope,
                    visible = visible,
                    enterTransition = enterTransition,
                    exitTransition = exitTransition,
                    `_` = `_`,
                )
            }
        }
    }
}
