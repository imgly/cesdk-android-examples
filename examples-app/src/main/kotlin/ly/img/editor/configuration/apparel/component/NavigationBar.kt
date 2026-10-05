@file:Suppress("UnusedReceiverParameter")

package ly.img.editor.configuration.apparel.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import ly.img.editor.configuration.apparel.ApparelConfigurationBuilder
import ly.img.editor.core.component.EditorTrigger
import ly.img.editor.core.component.NavigationBar
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberCloseEditor
import ly.img.editor.core.component.rememberExport
import ly.img.editor.core.component.rememberRedo
import ly.img.editor.core.component.rememberTogglePreviewMode
import ly.img.editor.core.component.rememberUndo

/**
 * The configuration of the component that is displayed as horizontal list of items at the top of the editor.
 */
@Composable
fun ApparelConfigurationBuilder.rememberNavigationBar() = NavigationBar.remember {
    scope = {
        val historyTrigger by EditorTrigger.remember {
            editorContext.engine.editor.onHistoryUpdated()
        }
        // Update NavigationBar whenever the editor history changes
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
                add { NavigationBar.Button.rememberTogglePreviewMode() }
                add { NavigationBar.Button.rememberExport() }
            }
        }
    }
}
