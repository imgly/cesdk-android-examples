package ly.img.editor.configuration.memories.component

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ly.img.editor.configuration.memories.MemoriesViewModel
import ly.img.editor.core.EditorScope
import ly.img.editor.core.component.EditorComponent
import ly.img.editor.core.component.EditorTrigger
import ly.img.editor.core.component.NavigationBar
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberCloseEditor
import ly.img.editor.core.component.rememberExport
import ly.img.editor.core.component.rememberRedo
import ly.img.editor.core.component.rememberUndo
import ly.img.editor.core.theme.surface3

fun navigationBarConfiguration(viewModel: MemoriesViewModel): @Composable (EditorScope.() -> EditorComponent<*>) = {
    val isPreviewMode by viewModel.isPreviewMode.collectAsState()
    val shouldHideUIForPreview by viewModel.shouldHideUIForPreview.collectAsState()

    NavigationBar.remember {
        scope = {
            val historyTrigger by EditorTrigger.remember {
                editorContext.engine.editor.onHistoryUpdated()
            }
            remember(this, historyTrigger) {
                NavigationBar.Scope(parentScope = this)
            }
        }
        visible = { !shouldHideUIForPreview }
        enterTransition = {
            slideInVertically(
                animationSpec = tween(100),
                initialOffsetY = { -it },
            ) + fadeIn(tween(100))
        }
        exitTransition = {
            slideOutVertically(
                animationSpec = tween(100),
                targetOffsetY = { -it },
            ) + fadeOut(tween(100))
        }
        decoration = { content ->
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surface3.copy(alpha = 0.95f),
                    ),
            ) {
                content()
            }
        }
        listBuilder = {
            NavigationBar.ListBuilder.remember {
                this.aligned(Alignment.Start) {
                    add { NavigationBar.Button.rememberCloseEditor {} }
                }
                this.aligned(Alignment.CenterHorizontally) {
                    add { NavigationBar.Button.rememberUndo {} }
                    add { NavigationBar.Button.rememberRedo {} }
                }
                this.aligned(Alignment.End) {
                    add { NavigationBar.Button.rememberExport() }
                }
            }
        }
    }
}
