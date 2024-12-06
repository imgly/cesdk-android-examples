package ly.img.editor.core.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import ly.img.editor.core.EditorScope
import ly.img.editor.core.LocalEditorScope

/**
 * A class that is used as an identifier for class [EditorComponent].
 */
@JvmInline
@Immutable
value class EditorComponentId(val id: String)

/**
 * A class that represents a component that can be rendered in the editor.
 */
@Stable
abstract class EditorComponent<Scope : EditorScope> {
    /**
     * The unique id of the component.
     */
    abstract val id: EditorComponentId

    /**
     * Provides the scope of the current component based on the parent scope.
     */
    abstract val scopeProvider: (EditorScope) -> Scope

    /**
     * A flow that is used to trigger the following when a new value is emitted:
     * 1. Recomposition of all functions with signature @Composable Scope.() -> {} in this component.
     * such as [visible], [enterTransition], [exitTransition].
     * 2. Recomposition of all functions with signature @Composable Scope.() -> {} in all child components recursively
     * if [EditorComponent.shouldReceiveUpdateFromParent] returns true for them.
     */
    abstract val updateTrigger: (Scope.() -> Flow<Any?>)?

    /**
     * Whether the component should be visible.
     */
    abstract val visible: @Composable Scope.() -> Boolean

    /**
     * The transition of the component when it enters the parent composable.
     */
    abstract val enterTransition: @Composable Scope.() -> EnterTransition

    /**
     * The transition of the component when it exits the parent composable.
     */
    abstract val exitTransition: @Composable Scope.() -> ExitTransition

    /**
     * This method is invoked when the parent component of this component is updated via [updateTrigger]. If true is returned,
     * then this component will be updated as if [updateTrigger] of this component has emitted.
     *
     * @return true if the component should receive an update from the parent component.
     */
    open fun Scope.shouldReceiveUpdateFromParent(parentScope: EditorScope): Boolean = true

    /**
     * The content of the component.
     */
    @Composable
    fun Content(parentScope: EditorScope = requireNotNull(LocalEditorScope.current)) {
        CommonContent(parentScope) { visible, enter, exit, content ->
            AnimatedVisibility(
                visible = visible,
                enter = enter,
                exit = exit,
            ) {
                content()
            }
        }
    }

    /**
     * The content of the component when rendered in a [ColumnScope].
     * Prefer using this overload over without [ColumnScope] when the component is being rendered in a row.
     *
     * @param columnScope the [ColumnScope] of the column, where the component is rendered.
     */
    @Composable
    fun Content(
        columnScope: ColumnScope,
        parentScope: EditorScope = requireNotNull(LocalEditorScope.current),
    ) {
        CommonContent(parentScope) { visible, enter, exit, content ->
            columnScope.AnimatedVisibility(
                visible = visible,
                enter = enter,
                exit = exit,
            ) {
                content()
            }
        }
    }

    /**
     * The content of the component when rendered in a [ColumnScope].
     * Prefer using this overload over without [ColumnScope] when the component is being rendered in a column.
     *
     * @param rowScope the [RowScope] of the row, where the component is rendered.
     */
    @Composable
    fun Content(
        rowScope: RowScope,
        parentScope: EditorScope = requireNotNull(LocalEditorScope.current),
    ) {
        CommonContent(parentScope) { visible, enter, exit, content ->
            rowScope.AnimatedVisibility(
                visible = visible,
                enter = enter,
                exit = exit,
            ) {
                content()
            }
        }
    }

    /**
     * The main content of the component that should be provided.
     *
     * @param animatedVisibilityScope the animated visibility scope of this component. This scope can be used to
     * animate children of this component separately when [enterTransition] and [exitTransition]s are running.
     * Check the documentation of [AnimatedVisibilityScope.animateEnterExit] for more details.
     * Note that the value is null if [enterTransition] and [exitTransition] are [EnterTransition.None] and [ExitTransition.None]
     * respectively.
     */
    @Composable
    protected abstract fun Scope.Content(animatedVisibilityScope: AnimatedVisibilityScope?)

    @Composable
    private fun CommonContent(
        parentScope: EditorScope,
        animatedVisibility: @Composable (
            Boolean,
            EnterTransition,
            ExitTransition,
            @Composable AnimatedVisibilityScope.() -> Unit,
        ) -> Unit,
    ) {
        var scope by remember { mutableStateOf(scopeProvider(parentScope)) }
        var triggerForUpdateTrigger by remember { mutableStateOf(false) }

        fun updateScope() {
            // Recreate instance of scope to force recomposition
            val scopeBefore = scope
            scope = scopeProvider(parentScope)
            if (scope == scopeBefore) {
                error(
                    "The scope provided via scopeProvider is equal to the instance provided last time. Make sure it is unique every time.",
                )
            }
        }
        LaunchedEffect(parentScope) {
            if (scope.shouldReceiveUpdateFromParent(parentScope)) {
                updateScope()
                triggerForUpdateTrigger = triggerForUpdateTrigger.not()
            }
        }
        LaunchedEffect(triggerForUpdateTrigger) {
            updateTrigger?.let {
                it(scope)
                    .onEach { updateScope() }
                    .collect()
            }
        }
        CompositionLocalProvider(LocalEditorScope provides scope) {
            val visible = scope.visible()
            val enterTransition = scope.enterTransition()
            val exitTransition = scope.exitTransition()
            // AnimatedVisibility is unstable and causes unexpected behaviors sometimes.
            // It is best to avoid it unless enterTransition and exitTransition are provided.
            if (enterTransition == EnterTransition.None && exitTransition == ExitTransition.None) {
                if (visible) {
                    scope.Content(null)
                }
            } else {
                animatedVisibility(visible, enterTransition, exitTransition) {
                    scope.Content(this)
                }
            }
        }
    }

    companion object {
        /**
         * Predicate to be used when the [EditorComponent] is always visible.
         */
        val alwaysVisible: @Composable EditorScope.() -> Boolean = { true }

        /**
         * A helper lambda for getting [EnterTransition.None] in the [EditorScope].
         */
        val noneEnterTransition: @Composable EditorScope.() -> EnterTransition = { EnterTransition.None }

        /**
         * A helper lambda for getting [EnterTransition.None] in the [EditorScope].
         */
        val noneExitTransition: @Composable EditorScope.() -> ExitTransition = { ExitTransition.None }
    }
}
