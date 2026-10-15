@file:Suppress("UnusedReceiverParameter")

package ly.img.editor.configuration.contentmoderation.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import ly.img.editor.configuration.contentmoderation.ContentModerationConfigurationBuilder
import ly.img.editor.configuration.contentmoderation.iconPack.IconPack
import ly.img.editor.configuration.contentmoderation.iconPack.ShieldAlert
import ly.img.editor.configuration.contentmoderation.iconPack.ShieldAlertFilled
import ly.img.editor.configuration.contentmoderation.iconPack.ShieldCheck
import ly.img.editor.configuration.contentmoderation.iconPack.ShieldCheckFilled
import ly.img.editor.core.component.Button
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.EditorTrigger
import ly.img.editor.core.component.NavigationBar
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberCloseEditor
import ly.img.editor.core.component.rememberExport
import ly.img.editor.core.component.rememberRedo
import ly.img.editor.core.component.rememberUndo
import ly.img.editor.core.event.EditorEvent

/**
 * The configuration of the component that is displayed as horizontal list of items at the top of the editor.
 */
@Composable
fun ContentModerationConfigurationBuilder.rememberNavigationBar() = NavigationBar.remember {
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
                // The moderation button sits directly to the left of the export/share button.
                add { NavigationBar.Button.rememberModerate(builder = this@rememberNavigationBar) }
                add { NavigationBar.Button.rememberExport() }
            }
        }
    }
}

// highlight-starter-kit-moderate-button

/**
 * A navigation bar button that runs content moderation on the scene and opens the results bottom sheet.
 *
 * @param builder the configuration builder, used to reach the [ContentModerationProvider].
 */
@Composable
fun NavigationBar.Button.rememberModerate(builder: ContentModerationConfigurationBuilder): Button<NavigationBar.ItemScope> =
    NavigationBar.Button.remember {
        id = { EditorComponentId("ly.img.contentmoderation.navigationBar.button.moderate") }
        // Reflect the last completed check (a warning persists across edits until a clean
        // re-check), and fill the icon while the moderation sheet is open.
        vectorIcon = {
            val flagged = builder.lastModerationHadIssues
            val open = builder.isModerationSheetOpen
            when {
                flagged && open -> IconPack.ShieldAlertFilled
                flagged -> IconPack.ShieldAlert
                open -> IconPack.ShieldCheckFilled
                else -> IconPack.ShieldCheck
            }
        }
        contentDescription = { "Content moderation" }
        onClick = {
            // Toggle: tapping the icon while the sheet is open closes it.
            if (builder.isModerationSheetOpen) {
                editorContext.eventHandler.send(EditorEvent.Sheet.Close(animate = true))
            } else {
                editorContext.eventHandler.send(createModerationSheetOpenEvent(builder = builder))
            }
        }
    }
// highlight-starter-kit-moderate-button
