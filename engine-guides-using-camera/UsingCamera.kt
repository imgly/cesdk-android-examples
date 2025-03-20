import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.MirrorMode
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ly.img.engine.DesignBlockType
import ly.img.engine.EffectType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.camera.setCameraPreview
import java.io.File

fun usingCamera(
    activity: AppCompatActivity,
    surfaceView: SurfaceView,
    cameraProvider: ProcessCameraProvider,
    license: String,
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindSurfaceView(surfaceView)

    // highlight-setupCamera
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
        .build()
    val preview = Preview.Builder().build()
    val qualitySelector = QualitySelector.from(Quality.FHD)
    val recorder = Recorder.Builder()
        .setQualitySelector(qualitySelector)
        .build()
    val videoCapture = VideoCapture.Builder(recorder)
        .setMirrorMode(MirrorMode.MIRROR_MODE_ON_FRONT_ONLY)
        .build()

    cameraProvider.bindToLifecycle(activity, cameraSelector, preview, videoCapture)
    // highlight-setupCamera

    // highlight-setupScene
    val scene = engine.scene.createForVideo()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    val pixelStreamFill = engine.block.createFill(FillType.PixelStream)
    engine.block.setFill(block = page, fill = pixelStreamFill)
    engine.setCameraPreview(pixelStreamFill, preview, mirrored = false)
    engine.block.appendEffect(
        block = page,
        effectBlock = engine.block.createEffect(EffectType.HalfTone),
    )
    // highlight-setupScene

    // highlight-orientation
    // If camerax preview transformation info rotation is 90, this will return Left. If we passed mirrored = true, this would be LeftMirrored.
    val orientation = engine.block.getEnum(
        block = pixelStreamFill,
        property = "fill/pixelStream/orientation",
    )
    // highlight-orientation

    // highlight-camera
    val recordingFile = File(surfaceView.context.filesDir, "temp.mp4")
    val fileOutputOptions = FileOutputOptions.Builder(recordingFile).build()
    val recording = videoCapture.output
        .prepareRecording(activity, fileOutputOptions)
        .start(ContextCompat.getMainExecutor(surfaceView.context)) {
            if (it !is VideoRecordEvent.Finalize) return@start
            val videoFill = engine.block.createFill(FillType.Video)
            engine.block.setFill(block = page, fill = videoFill)
            engine.block.setString(
                block = videoFill,
                property = "fill/video/fileURI",
                value = recordingFile.toUri().toString(),
            )
        }
    delay(5000L)
    recording.stop()
    // highlight-camera
}
