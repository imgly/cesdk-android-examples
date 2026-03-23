package ly.img.editor.showcases.plugin.imagegen.utils

import android.widget.Toast
import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ly.img.editor.core.EditorScope
import ly.img.editor.core.component.data.Height
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.sheet.SheetStyle
import ly.img.editor.core.sheet.SheetType
import ly.img.editor.showcases.R
import ly.img.editor.showcases.plugin.imagegen.Format
import ly.img.editor.showcases.plugin.imagegen.TextToImageSheetContent
import ly.img.engine.BlockState
import ly.img.engine.DesignBlock

fun EditorScope.createTextToImageSheetOpenEvent(
    apiConfig: String,
    initialImageUri: String? = null,
    targetBlock: DesignBlock? = null,
    coroutineScope: CoroutineScope,
): EditorEvent.Sheet.Open = EditorEvent.Sheet.Open(
    SheetType.Custom(
        style = SheetStyle(
            isFloating = true,
            maxHeight = Height.Fraction(.8f),
        ),
        content = {
            CreateTextToImageSheetContent(
                apiConfig = apiConfig,
                initialImageUri = initialImageUri,
                targetBlock = targetBlock,
                coroutineScope = coroutineScope,
            )
        },
    ),
)

@Composable
private fun EditorScope.CreateTextToImageSheetContent(
    apiConfig: String,
    initialImageUri: String?,
    targetBlock: DesignBlock?,
    coroutineScope: CoroutineScope,
) {
    TextToImageSheetContent(
        apiConfig = apiConfig,
        initialImageUri = initialImageUri,
        handleImageGeneration = { state, generateImages ->
            coroutineScope.launch {
                val block = targetBlock?.apply {
                    editorContext.engine.block.setState(
                        block = this,
                        state = BlockState.Pending(0f),
                    )
                } ?: if (state.imageUri != null) {
                    val imageSize = TextToImageUtils.getImageSize(context = editorContext.activity, imageUri = state.imageUri)
                    editorContext.engine.createPendingBlock(
                        format = Format.CUSTOM,
                        customWidth = imageSize?.width?.toString() ?: state.customWidth,
                        customHeight = imageSize?.height?.toString() ?: state.customHeight,
                    )
                } else {
                    editorContext.engine.createPendingBlock(
                        format = state.selectedFormat,
                        customWidth = state.customWidth,
                        customHeight = state.customHeight,
                    )
                }
                editorContext.eventHandler.send(
                    EditorEvent.Sheet.Close(animate = true),
                )
                try {
                    val images = generateImages()
                    val imageUri = images.first()
                    editorContext.engine.editor.addUndoStep()
                    editorContext.engine.addImageToBlock(block = block, imageUri = imageUri)
                } catch (e: Exception) {
                    Toast.makeText(
                        editorContext.activity,
                        editorContext.activity.getString(R.string.ly_img_showcases_failed_to_generate_image),
                        Toast.LENGTH_SHORT,
                    ).show()
                    editorContext.engine.block.setState(block = block, state = BlockState.Error(BlockState.Error.Type.UNKNOWN))
                }
            }
        },
        onCloseSheet = {
            editorContext.eventHandler.send(
                EditorEvent.Sheet.Close(animate = true),
            )
        },
    )
}
