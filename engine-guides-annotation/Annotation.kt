import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode

fun annotation(engine: Engine) {
    val page = createAnnotationScene(engine)
    val text = addTextAnnotation(engine = engine, page = page)
    val highlight = addShapeAnnotation(engine = engine, page = page)
    val annotations = listOf(text, highlight)

    val sync = TimelineSync(engine = engine, page = page)
    sync.refresh(annotations)
    seekToAnnotation(engine = engine, page = page, annotation = highlight)

    setAnnotationPlayback(engine = engine, page = page, playing = true, looping = true)
    updateAnnotationText(engine = engine, annotation = text, text = "Replay this part")
    moveAnnotation(engine = engine, annotation = highlight, x = 780F, y = 260F)
    updateAnnotationTiming(engine = engine, annotation = highlight, start = 13.0, duration = 3.0)
    removeAnnotation(engine = engine, annotation = text)
}

// highlight-android-timeline-placement
fun createAnnotationScene(engine: Engine): DesignBlock {
    val scene = engine.scene.createForVideo()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setWidth(page, value = 1280F)
    engine.block.setHeight(page, value = 720F)
    engine.block.setDuration(page, duration = 20.0)

    val video = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(video, shape = engine.block.createShape(ShapeType.Rect))
    val videoFill = engine.block.createFill(FillType.Video)
    val videoUri = Uri.parse(
        "https://cdn.img.ly/assets/demo/v1/ly.img.video/videos/pexels-drone-footage-of-a-surfer-barrelling-a-wave-12715991.mp4",
    )
    // Video fills expose a URI-valued file property; set it with the typed URI API.
    engine.block.setUri(
        block = videoFill,
        property = "fill/video/fileURI",
        value = videoUri,
    )
    engine.block.setFill(video, fill = videoFill)

    val videoTrack = engine.block.create(DesignBlockType.Track)
    engine.block.appendChild(parent = page, child = videoTrack)
    engine.block.appendChild(parent = videoTrack, child = video)
    engine.block.fillParent(videoTrack)

    return page
}
// highlight-android-timeline-placement

// highlight-android-text-annotation
fun addTextAnnotation(
    engine: Engine,
    page: DesignBlock,
): DesignBlock {
    val text = engine.block.create(DesignBlockType.Text)
    engine.block.replaceText(text, text = "Watch this part!")
    engine.block.setTextFontSize(text, fontSize = 32F)
    engine.block.setWidthMode(text, mode = SizeMode.AUTO)
    engine.block.setHeightMode(text, mode = SizeMode.AUTO)
    engine.block.setPositionX(text, value = 160F)
    engine.block.setPositionY(text, value = 560F)
    engine.block.setTimeOffset(text, offset = 5.0)
    engine.block.setDuration(text, duration = 5.0)

    engine.block.appendChild(parent = page, child = text)
    return text
}
// highlight-android-text-annotation

// highlight-android-shape-annotation
fun addShapeAnnotation(
    engine: Engine,
    page: DesignBlock,
): DesignBlock {
    val highlight = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(highlight, shape = engine.block.createShape(ShapeType.Star))
    engine.block.setWidth(highlight, value = 140F)
    engine.block.setHeight(highlight, value = 140F)
    engine.block.setPositionX(highlight, value = 700F)
    engine.block.setPositionY(highlight, value = 240F)

    val fill = engine.block.createFill(FillType.Color)
    engine.block.setFill(highlight, fill = fill)
    engine.block.setFillSolidColor(
        block = highlight,
        color = Color.fromRGBA(r = 1F, g = 0F, b = 0F, a = 1F),
    )
    engine.block.setTimeOffset(highlight, offset = 12.0)
    engine.block.setDuration(highlight, duration = 4.0)

    engine.block.appendChild(parent = page, child = highlight)
    return highlight
}
// highlight-android-shape-annotation

// highlight-android-playback-sync
data class AnnotationTimelineState(
    val currentTime: Double,
    val activeAnnotation: DesignBlock?,
)

class TimelineSync(
    private val engine: Engine,
    private val page: DesignBlock,
) {
    private val mutableState = MutableStateFlow(
        AnnotationTimelineState(
            currentTime = 0.0,
            activeAnnotation = null,
        ),
    )
    val state: StateFlow<AnnotationTimelineState> = mutableState.asStateFlow()
    private var pollingJob: Job? = null

    // Call this from UI code that owns a lifecycle scope.
    fun start(
        annotations: List<DesignBlock>,
        scope: CoroutineScope,
    ) {
        pollingJob?.cancel()
        pollingJob = scope.launch(Dispatchers.Main.immediate) {
            while (isActive) {
                refresh(annotations)
                delay(200)
            }
        }
    }

    fun refresh(annotations: List<DesignBlock>) {
        val currentTime = engine.block.getPlaybackTime(page)
        val active = annotations.firstOrNull { annotation ->
            engine.block.isValid(annotation) &&
                engine.block.isVisibleAtCurrentPlaybackTime(annotation)
        }
        mutableState.value = AnnotationTimelineState(
            currentTime = currentTime,
            activeAnnotation = active,
        )
    }

    fun stop() {
        pollingJob?.cancel()
        pollingJob = null
    }
}
// highlight-android-playback-sync

// highlight-android-seek-to-annotation
fun seekToAnnotation(
    engine: Engine,
    page: DesignBlock,
    annotation: DesignBlock,
) {
    if (!engine.block.supportsPlaybackTime(page)) return

    val start = engine.block.getTimeOffset(annotation)
    engine.block.setPlaybackTime(block = page, time = start)
}
// highlight-android-seek-to-annotation

// highlight-android-edit-annotation
fun updateAnnotationText(
    engine: Engine,
    annotation: DesignBlock,
    text: String,
) {
    engine.block.replaceText(annotation, text = text)
}
// highlight-android-edit-annotation

// highlight-android-move-annotation
fun moveAnnotation(
    engine: Engine,
    annotation: DesignBlock,
    x: Float,
    y: Float,
) {
    engine.block.setPositionX(annotation, value = x)
    engine.block.setPositionY(annotation, value = y)
}
// highlight-android-move-annotation

// highlight-android-retime-annotation
fun updateAnnotationTiming(
    engine: Engine,
    annotation: DesignBlock,
    start: Double,
    duration: Double,
) {
    engine.block.setTimeOffset(annotation, offset = start)
    engine.block.setDuration(annotation, duration = duration)
}
// highlight-android-retime-annotation

// highlight-android-remove-annotation
fun removeAnnotation(
    engine: Engine,
    annotation: DesignBlock,
) {
    engine.block.destroy(annotation)
}
// highlight-android-remove-annotation

// highlight-android-playback-controls
fun setAnnotationPlayback(
    engine: Engine,
    page: DesignBlock,
    playing: Boolean,
    looping: Boolean,
): Pair<Boolean, Boolean> {
    engine.block.setPlaying(block = page, enabled = playing)
    val isPlaying = engine.block.isPlaying(page)

    engine.block.setLooping(block = page, looping = looping)
    val isLooping = engine.block.isLooping(page)

    return isPlaying to isLooping
}
// highlight-android-playback-controls
