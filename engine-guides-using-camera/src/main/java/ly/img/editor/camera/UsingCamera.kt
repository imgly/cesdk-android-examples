package ly.img.editor.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.util.Size
import android.view.SurfaceView
import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.MirrorMode
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import ly.img.engine.DesignBlockType
import ly.img.engine.EffectType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.camera.setCameraPreview
import java.io.File
import java.io.IOException

private const val TAG = "UsingCamera"

suspend fun recordVideoFromCamera(
    engine: Engine,
    activity: ComponentActivity,
    surfaceView: SurfaceView,
    cameraProvider: ProcessCameraProvider,
) = withContext(Dispatchers.Main.immediate) {
    // highlight-android-setup-camera
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
        .build()
    val previewSize = Size(1920, 1080)
    val resolutionSelector = ResolutionSelector.Builder()
        .setResolutionStrategy(
            ResolutionStrategy(
                previewSize,
                ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER,
            ),
        )
        .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
        .build()
    val preview = Preview.Builder()
        .setResolutionSelector(resolutionSelector)
        .build()
    val qualitySelector = QualitySelector.from(Quality.FHD)
    val recorder = Recorder.Builder()
        .setQualitySelector(qualitySelector)
        .build()
    val videoCapture = VideoCapture.Builder(recorder)
        .setMirrorMode(MirrorMode.MIRROR_MODE_ON_FRONT_ONLY)
        .build()

    cameraProvider.bindToLifecycle(activity, cameraSelector, preview, videoCapture)
    // highlight-android-setup-camera

    // highlight-android-create-preview
    val scene = engine.scene.createForVideo()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    // Request a 16:9 FHD preview target. CameraX may choose the closest supported size.
    val cameraWidth = 1920F
    val cameraHeight = 1080F
    engine.block.setWidth(block = scene, value = cameraWidth)
    engine.block.setHeight(block = scene, value = cameraHeight)
    engine.block.setWidth(block = page, value = cameraWidth)
    engine.block.setHeight(block = page, value = cameraHeight)

    val pixelStreamFill = engine.block.createFill(FillType.PixelStream)
    engine.block.setFill(block = page, fill = pixelStreamFill)
    engine.setCameraPreview(pixelStreamFill, preview, mirrored = true)
    engine.block.appendEffect(
        block = page,
        effectBlock = engine.block.createEffect(EffectType.HalfTone),
    )
    engine.scene.zoomToBlock(
        block = page,
        paddingLeft = 40F,
        paddingTop = 40F,
        paddingRight = 40F,
        paddingBottom = 40F,
    )
    // highlight-android-create-preview

    // highlight-android-record-video
    val recordingFile = File(surfaceView.context.filesDir, "temp.mp4")
    val fileOutputOptions = FileOutputOptions.Builder(recordingFile).build()
    val finalizeEvent = CompletableDeferred<VideoRecordEvent.Finalize>()
    var recording: Recording? = null
    var recordingStopped = false
    val finalizedRecording = try {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            throw SecurityException("Grant RECORD_AUDIO before enabling audio recording.")
        }
        recording = videoCapture.output
            .prepareRecording(activity, fileOutputOptions)
            .withAudioEnabled()
            .start(ContextCompat.getMainExecutor(surfaceView.context)) { event: VideoRecordEvent ->
                if (event !is VideoRecordEvent.Finalize) return@start
                finalizeEvent.complete(event)
            }
        // Stop after five seconds for this sample. Replace this with your app's own recording controls.
        delay(5000L)
        recordingStopped = true
        recording.stop()
        finalizeEvent.await()
    } finally {
        withContext(NonCancellable) {
            try {
                recording?.let {
                    if (!recordingStopped) {
                        recordingStopped = true
                        it.stop()
                    }
                    finalizeEvent.await()
                }
            } finally {
                cameraProvider.unbind(preview, videoCapture)
            }
        }
    }

    if (finalizedRecording.hasError()) {
        val exception = IllegalStateException(
            "CameraX recording failed with error ${finalizedRecording.error}",
            finalizedRecording.cause,
        )
        Log.e(TAG, "CameraX recording failed with error ${finalizedRecording.error}", exception)
        throw exception
    }

    val recordingUri = finalizedRecording.outputResults.outputUri
    requireNonEmptyRecording(surfaceView.context, recordingUri)
    val videoFill = engine.block.createFill(FillType.Video)
    engine.block.setFill(block = page, fill = videoFill)
    engine.block.setUri(
        block = videoFill,
        property = "fill/video/fileURI",
        value = recordingUri,
    )
}

private suspend fun requireNonEmptyRecording(
    context: Context,
    uri: Uri,
) = withContext(Dispatchers.IO) {
    val hasData = try {
        context.contentResolver.openInputStream(uri)?.use { input ->
            input.read() != -1
        } ?: false
    } catch (exception: IOException) {
        throw IllegalStateException("CameraX finalized recording is not readable: $uri", exception)
    }

    check(hasData) { "CameraX finalized recording is missing or empty: $uri" }
}
// highlight-android-record-video
