@file:Suppress("UnusedReceiverParameter")

package ly.img.editor.configuration.video.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import ly.img.editor.configuration.video.VideoConfigurationBuilder
import ly.img.editor.core.component.EditorTrigger
import ly.img.editor.core.component.NavigationBar
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberCloseEditor
import ly.img.editor.core.component.rememberExport
import ly.img.editor.core.component.rememberRedo
import ly.img.editor.core.component.rememberUndo

/**
 * The configuration of the component that is displayed as horizontal list of items at the top of the editor.
 */
@Composable
fun VideoConfigurationBuilder.rememberNavigationBar() = NavigationBar.remember {
    scope = {
        val activeSceneTrigger by EditorTrigger.remember {
            editorContext.engine.scene.onActiveChanged()
        }
        val historyTrigger by EditorTrigger.remember(activeSceneTrigger) {
            editorContext.engine.editor.onHistoryUpdated()
        }
        // Update NavigationBar whenever the active scene or editor history changes.
        remember(this, historyTrigger) {
            NavigationBar.Scope(parentScope = this)
        }
    }
    listBuilder = {
        NavigationBar.ListBuilder.remember {
            aligned(alignment = Alignment.Start) {
                add { NavigationBar.Button.rememberCloseEditor() }
            }
            aligned(alignment = Alignment.End) {
                add { NavigationBar.Button.rememberUndo() }
                add { NavigationBar.Button.rememberRedo() }
                add {
                    NavigationBar.Button.rememberExport {
                        enabled = {
                            // Show export button if the duration of scene is greater than 0
                            remember(this) {
                                editorContext.engine.scene.get()?.let {
                                    editorContext.engine.block.getDuration(it) > 0
                                } ?: false
                            }
                        }
                    }
                }
            }
        }
    }
}
