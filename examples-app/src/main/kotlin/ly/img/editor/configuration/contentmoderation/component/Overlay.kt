package ly.img.editor.configuration.contentmoderation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import ly.img.editor.configuration.contentmoderation.ContentModerationConfigurationBuilder
import ly.img.editor.configuration.contentmoderation.iconPack.IconPack
import ly.img.editor.configuration.contentmoderation.iconPack.WarningTriangle
import ly.img.editor.core.component.EditorComponent
import ly.img.editor.core.component.remember

/**
 * The configuration of the component that is displayed over the editor.
 *
 * It renders the standard overlay (loading, error and close-confirmation dialogs) and, on top of
 * that, the export warning dialog shown when content moderation flags content during export.
 */
@Composable
fun ContentModerationConfigurationBuilder.rememberOverlay() = EditorComponent.remember {
    decoration = {
        // Standard overlay: loading spinner, error dialog, close confirmation and back handling.
        Overlay()

        // Export warning: shown while onExport waits for the user to confirm exporting flagged content.
        moderationExportPrompt?.let { prompt ->
            val count = prompt.flagged.size
            val title: String
            val message: String
            if (prompt.moderated && count > 0) {
                // The last check of the current scene found flagged content.
                title = "Restricted content detected"
                message = "This design may contain restricted content in $count " +
                    "place${if (count == 1) "" else "s"}. Export anyway?"
            } else {
                // The scene has changes that were never checked (or was never checked at all).
                title = "Content not moderated"
                message = "This design hasn't been moderated since your last change and may contain " +
                    "restricted content. Export anyway?"
            }
            AlertDialog(
                onDismissRequest = { prompt.decision.complete(false) },
                icon = { Icon(imageVector = IconPack.WarningTriangle, contentDescription = null) },
                title = { Text(text = title) },
                text = { Text(text = message) },
                // Three actions do not fit on one line, so stack them vertically and end-aligned per
                // the Material dialog guidelines rather than wrapping into a ragged two-row layout.
                // All actions live in confirmButton so AlertDialog renders them as one clean stack.
                confirmButton = {
                    Column(horizontalAlignment = Alignment.End) {
                        // Cancel the export and open the moderation sheet, running the check right away.
                        TextButton(
                            onClick = {
                                prompt.decision.complete(false)
                                editorContext.eventHandler.send(
                                    createModerationSheetOpenEvent(builder = this@rememberOverlay, autoRun = true),
                                )
                            },
                        ) {
                            Text(text = "Moderate now")
                        }
                        TextButton(onClick = { prompt.decision.complete(true) }) {
                            Text(text = "Export anyway")
                        }
                        TextButton(onClick = { prompt.decision.complete(false) }) {
                            Text(text = "Cancel")
                        }
                    }
                },
            )
        }
    }
}
