package ly.img.editor.showcases.plugin.imagegen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.editor.core.LocalEditorScope
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.InspectorBar
import ly.img.editor.core.component.InspectorBar.ButtonScope
import ly.img.editor.core.component.data.Selection
import ly.img.editor.core.compose.rememberLastValue
import ly.img.editor.showcases.R
import ly.img.editor.showcases.icon.DockCreateWithAi
import ly.img.editor.showcases.icon.IconPack
import ly.img.editor.showcases.plugin.imagegen.utils.createTextToImageSheetOpenEvent
import ly.img.engine.FillType

@Composable
fun InspectorBar.Button.Companion.rememberEditWithAIInspectorBarButton(apiConfig: String): InspectorBar.Button {
    val scope = rememberCoroutineScope()
    return InspectorBar.Button.remember(
        id = EditorComponentId("ly.img.component.inspectorBar.button.textToImage"),
        scope = (LocalEditorScope.current as InspectorBar.Scope).run {
            rememberLastValue(this) {
                if (editorContext.safeSelection == null) lastValue else ButtonScope(parentScope = this@run)
            }
        },
        visible = {
            remember(this) {
                val designBlock = editorContext.selection.designBlock
                editorContext.selection.isNotAnyKindOfSticker() &&
                    editorContext.selection.fillType == FillType.Image
            }
        },
        icon = {
            Icon(
                imageVector = IconPack.DockCreateWithAi,
                contentDescription = stringResource(R.string.ly_img_showcases_dock_edit_image_content_description),
                modifier = Modifier.size(24.dp),
            )
        },
        text = {
            Text(
                stringResource(R.string.ly_img_showcases_edit_text),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 4.dp),
            )
        },
        onClick = {
            editorContext.safeSelection?.designBlock?.let { block ->
                val initialImageUri = editorContext.engine.block.getString(
                    editorContext.engine.block.getFill(block),
                    "fill/image/imageFileURI",
                )

                editorContext.eventHandler.send(
                    event = createTextToImageSheetOpenEvent(
                        apiConfig = apiConfig,
                        initialImageUri = initialImageUri,
                        targetBlock = block,
                        coroutineScope = scope,
                    ),
                )
            }
        },
    )
}

private fun Selection.isAnyKindOfSticker(): Boolean = this.kind == KIND_STICKER || this.kind == KIND_ANIMATED_STICKER

private fun Selection.isNotAnyKindOfSticker() = !this.isAnyKindOfSticker()

private const val KIND_STICKER = "sticker"
private const val KIND_ANIMATED_STICKER = "animatedSticker"
