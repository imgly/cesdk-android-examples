@file:Suppress("UnusedReceiverParameter", "UnusedFlow")

package ly.img.editor.configuration.postcard.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import ly.img.editor.configuration.postcard.PostcardConfigurationBuilder
import ly.img.editor.configuration.postcard.ext.getCurrentPageIndex
import ly.img.editor.core.R
import ly.img.editor.core.component.EditorTrigger
import ly.img.editor.core.component.NavigationBar
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberCloseEditor
import ly.img.editor.core.component.rememberExport
import ly.img.editor.core.component.rememberNextPage
import ly.img.editor.core.component.rememberPreviousPage
import ly.img.editor.core.component.rememberRedo
import ly.img.editor.core.component.rememberTogglePreviewMode
import ly.img.editor.core.component.rememberUndo
import ly.img.engine.DesignBlockType

/**
 * The configuration of the component that is displayed as horizontal list of items at the top of the editor.
 */
@Composable
fun PostcardConfigurationBuilder.rememberNavigationBar() = NavigationBar.remember {
    scope = {
        val activeSceneTrigger by EditorTrigger.remember {
            editorContext.engine.scene.onActiveChanged()
        }
        val historyTrigger by EditorTrigger.remember(activeSceneTrigger) {
            editorContext.engine.editor.onHistoryUpdated()
        }
        val pageIndex by remember(this, activeSceneTrigger) {
            if (editorContext.engine.scene.get() == null) return@remember emptyFlow()
            val stack = editorContext.engine.block.findByType(DesignBlockType.Stack).first()
            editorContext.engine.event.subscribe(listOf(stack))
                .map { editorContext.engine.getCurrentPageIndex() }
        }.collectAsState(initial = 0)
        // Update NavigationBar whenever the active scene, editor history or current page index changes
        remember(this, pageIndex, historyTrigger) {
            NavigationBar.Scope(parentScope = this)
        }
    }
    listBuilder = {
        NavigationBar.ListBuilder.remember {
            aligned(alignment = Alignment.Start) {
                add { NavigationBar.Button.rememberCloseEditor() }
                add {
                    NavigationBar.Button.rememberPreviousPage {
                        textString = { stringResource(R.string.ly_img_editor_navigation_bar_button_design) }
                    }
                }
            }
            aligned(alignment = Alignment.CenterHorizontally) {
                add { NavigationBar.Button.rememberUndo() }
                add { NavigationBar.Button.rememberRedo() }
                add { NavigationBar.Button.rememberTogglePreviewMode() }
            }
            aligned(alignment = Alignment.End) {
                add {
                    NavigationBar.Button.rememberNextPage {
                        textString = { stringResource(R.string.ly_img_editor_navigation_bar_button_write) }
                    }
                }
                add { NavigationBar.Button.rememberExport() }
            }
        }
    }
}
