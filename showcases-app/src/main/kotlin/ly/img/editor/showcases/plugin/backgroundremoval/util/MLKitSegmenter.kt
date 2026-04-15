package ly.img.editor.showcases.plugin.backgroundremoval.util

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.SegmentationMask
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * ML Kit segmentation utilities for background removal
 */
object MLKitSegmenter {
    /**
     * Processes an image with ML Kit selfie segmentation
     * Returns a SegmentationMask that can be used to remove backgrounds
     */
    suspend fun processImage(image: InputImage): SegmentationMask = withContext(Dispatchers.Default) {
        suspendCancellableCoroutine { continuation ->
            val selfieOptions = SelfieSegmenterOptions.Builder()
                .setDetectorMode(SelfieSegmenterOptions.SINGLE_IMAGE_MODE)
                .enableRawSizeMask()
                .build()

            val selfieSegmenter = Segmentation.getClient(selfieOptions)

            selfieSegmenter.process(image)
                .addOnSuccessListener { segmentationMask ->
                    if (continuation.isActive) {
                        continuation.resume(segmentationMask)
                    }
                }
                .addOnFailureListener { exception ->
                    if (continuation.isActive) {
                        continuation.resumeWithException(exception)
                    }
                }

            continuation.invokeOnCancellation {
                selfieSegmenter.close()
            }
        }
    }
}
