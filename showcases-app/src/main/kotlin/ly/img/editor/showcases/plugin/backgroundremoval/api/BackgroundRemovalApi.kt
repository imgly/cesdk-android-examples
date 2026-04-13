package ly.img.editor.showcases.plugin.backgroundremoval.api

import androidx.core.net.toUri
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.editor.core.EditorScope
import ly.img.editor.showcases.plugin.backgroundremoval.util.ImageLoader
import ly.img.editor.showcases.plugin.backgroundremoval.util.ImageProcessor
import ly.img.editor.showcases.plugin.backgroundremoval.util.MLKitSegmenter
import ly.img.engine.BlockState
import ly.img.engine.DesignBlock
import ly.img.engine.FillType

object BackgroundRemovalApi {
    /**
     * Removes the background from an image block using ML Kit segmentation
     */
    suspend fun EditorScope.removeBackground(targetBlock: DesignBlock) {
        val engine = editorContext.engine
        val pageFill = engine.block.getFill(targetBlock)
        val fillUri = engine.block.getString(block = pageFill, property = "fill/image/imageFileURI")

        engine.block.setState(block = targetBlock, state = BlockState.Pending(0f))

        val image: InputImage = ImageLoader.loadImageFromUri(
            uri = fillUri.toUri(),
            context = editorContext.activity,
        )

        val result = withContext(Dispatchers.Default) {
            MLKitSegmenter.processImage(image = image)
        }

        val originalBitmap = ImageLoader.loadBitmapFromUri(
            uri = fillUri.toUri(),
            context = editorContext.activity,
        )

        if (originalBitmap != null) {
            val maskedBitmap = ImageProcessor.applyMaskToBitmap(
                originalBitmap = originalBitmap,
                maskBuffer = result.buffer,
                maskWidth = result.width,
                maskHeight = result.height,
            )

            val newUri = ImageProcessor.saveBitmapAsTempFile(
                bitmap = maskedBitmap,
                context = editorContext.activity,
            )

            if (newUri != null) {
                engine.editor.addUndoStep()
                val newFill = engine.block.createFill(FillType.Image)
                engine.block.setString(block = newFill, property = "fill/image/imageFileURI", value = newUri.toString())
                engine.block.setFill(block = targetBlock, fill = newFill)
            }
        }
        engine.block.setState(block = targetBlock, state = BlockState.Ready)
    }
}
