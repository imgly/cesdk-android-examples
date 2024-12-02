package ly.img.camera

import android.util.Size
import androidx.camera.core.MirrorMode.MIRROR_MODE_ON_FRONT_ONLY
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ly.img.camera.core.CameraConfiguration
import ly.img.camera.core.CaptureVideo
import ly.img.camera.core.EngineConfiguration
import ly.img.camera.preview.CameraState
import ly.img.camera.record.RecordingManager
import ly.img.camera.record.VideoRecorder
import ly.img.editor.core.ui.engine.Scope
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.GlobalScope
import ly.img.engine.ShapeType
import ly.img.engine.UnstableEngineApi
import ly.img.engine.camera.setCameraPreview

internal class CameraViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    @OptIn(UnstableEngineApi::class)
    val engine =
        Engine.getInstance("ly.img.camera").also {
            it.idlingEnabled = true
        }

    private val cameraInput = savedStateHandle.get<CaptureVideo.Input>(CaptureVideo.INTENT_KEY_CAMERA_INPUT)
    val engineConfiguration: EngineConfiguration? = cameraInput?.engineConfiguration
    val cameraConfiguration: CameraConfiguration = cameraInput?.cameraConfiguration ?: CameraConfiguration()

    private val previewUseCase =
        Preview.Builder()
            .setResolutionSelector(
                ResolutionSelector.Builder()
                    .setResolutionStrategy(
                        ResolutionStrategy(
                            Size(
                                cameraConfiguration.videoSize.width.toInt(),
                                cameraConfiguration.videoSize.height.toInt(),
                            ),
                            ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER,
                        ),
                    )
                    .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
                    .build(),
            )
            .build()

    private val videoCaptureUseCase =
        VideoCapture.Builder(Recorder.Builder().build())
            .setMirrorMode(MIRROR_MODE_ON_FRONT_ONLY)
            .build()

    val cameraState = CameraState(previewUseCase, videoCaptureUseCase)
    val recordingManager =
        RecordingManager(
            maxDuration = cameraConfiguration.maxTotalDuration,
            allowExceedingMaxDuration = cameraConfiguration.allowExceedingMaxDuration,
            coroutineScope = viewModelScope,
            videoRecorder = VideoRecorder(videoCaptureUseCase),
        )

    private var page: DesignBlock = -1
    private var pixelStreamFill1: DesignBlock = -1
    private var streamRect1: DesignBlock = -1

    fun loadScene() {
        if (engine.scene.get() != null) {
            page = engine.scene.getPages()[0]
            streamRect1 = engine.block.findByName(STREAM_RECT1_NAME)[0]
            pixelStreamFill1 = engine.block.getFill(streamRect1)
        }

        val scene = engine.scene.createForVideo()
        engine.editor.setGlobalScope(Scope.EditorSelect, GlobalScope.DENY)
        engine.editor.setSettingBoolean("touch/singlePointPanning", false)
        engine.editor.setSettingFloat("positionSnappingThreshold", 8f)

        // Set up the page
        page = engine.block.create(DesignBlockType.Page)
        engine.block.appendChild(scene, page)

        val canvasWidth = cameraConfiguration.videoSize.width
        val canvasHeight = cameraConfiguration.videoSize.height

        engine.block.setWidth(scene, canvasWidth)
        engine.block.setHeight(scene, canvasHeight)

        engine.block.setWidth(page, canvasWidth)
        engine.block.setHeight(page, canvasHeight)

        // Set up the black background
        val backgroundRect = engine.block.create(DesignBlockType.Graphic)
        val backgroundShape = engine.block.createShape(ShapeType.Rect)
        engine.block.setShape(backgroundRect, backgroundShape)
        val backgroundFill = engine.block.createFill(FillType.Color)
        engine.block.setColor(backgroundFill, "fill/color/value", Color.fromRGBA(0, 0, 0, 255))

        engine.block.setFill(backgroundRect, backgroundFill)
        engine.block.setWidth(backgroundRect, canvasWidth)
        engine.block.setHeight(backgroundRect, canvasHeight)

        engine.block.appendChild(page, backgroundRect)

        // Set up the primary stream
        streamRect1 = engine.block.create(DesignBlockType.Graphic)
        val shape1 = engine.block.createShape(ShapeType.Rect)
        engine.block.setShape(streamRect1, shape1)
        engine.block.setName(streamRect1, STREAM_RECT1_NAME)
        engine.block.setVisible(streamRect1, false)

        engine.block.setWidth(streamRect1, canvasWidth)
        engine.block.setHeight(streamRect1, canvasHeight)

        engine.block.appendChild(page, streamRect1)

        pixelStreamFill1 = engine.block.createFill(FillType.PixelStream)
        engine.block.setFill(streamRect1, pixelStreamFill1)
    }

    suspend fun setCameraPreview(
        maxWidth: Float,
        maxHeight: Float,
    ) {
        engine.setCameraPreview(pixelStreamFill1, previewUseCase, mirrored = cameraState.showFrontCamera) {
            engine.block.setVisible(streamRect1, true)
            cameraState.isReady = true
        }
        val zoomedHeight = maxWidth * (cameraConfiguration.videoSize.height / cameraConfiguration.videoSize.width)
        val topPadding = (maxHeight - zoomedHeight).coerceAtLeast(0f)
        engine.scene.zoomToBlock(page, paddingTop = topPadding)
    }

    // It is needed to hide the preview initially and re-set the surface provider because if we just toggle the camera directly,
    // the `TransformationInfoListener` is called first even before the new frame from the new camera is available, this results in
    // the old camera preview being shown for a while with the new orientation being applied.
    fun toggleCamera() {
        val hasStartedRecording = recordingManager.hasStartedRecording
        if (hasStartedRecording) {
            recordingManager.pause()
        }
        engine.block.setVisible(streamRect1, false)
        cameraState.toggleCamera()
        engine.setCameraPreview(pixelStreamFill1, previewUseCase, mirrored = cameraState.showFrontCamera) {
            engine.block.setVisible(streamRect1, true)
        }
        if (hasStartedRecording) {
            recordingManager.resume()
        }
    }

    override fun onCleared() {
        super.onCleared()
        engine.stop()
    }

    companion object {
        private const val STREAM_RECT1_NAME = "Rect"
    }
}
