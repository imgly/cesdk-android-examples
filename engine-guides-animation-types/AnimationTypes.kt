import ly.img.engine.AnimationType
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import kotlin.math.PI

data class AnimationTypes(
    val slideDirection: Float,
    val fadeEasing: String,
    val zoomUsesFade: Boolean,
    val wipeDirection: String,
    val breathingIntensity: Float,
    val spinDirection: String,
    val spinIntensity: Float,
    val slideProperties: List<String>,
    val easingOptions: List<String>,
)

fun animationTypes(engine: Engine): AnimationTypes {
    val scene = engine.scene.createForVideo()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setWidth(page, value = 1920F)
    engine.block.setHeight(page, value = 1080F)
    engine.block.setDuration(page, duration = 6.0)

    val pageFill = engine.block.createFill(FillType.Color)
    engine.block.setColor(pageFill, property = "fill/color/value", value = Color.fromRGBA(250, 250, 252))
    engine.block.setFill(page, fill = pageFill)

    val demoColors = listOf(
        Color.fromRGBA(67, 97, 238),
        Color.fromRGBA(239, 71, 111),
        Color.fromRGBA(255, 209, 102),
        Color.fromRGBA(6, 214, 160),
        Color.fromRGBA(17, 138, 178),
        Color.fromRGBA(131, 56, 236),
    )
    val columns = 2
    val blockWidth = 900F
    val blockHeight = 300F
    val blocks = demoColors.mapIndexed { index, color ->
        val block = engine.block.create(DesignBlockType.Graphic)
        engine.block.setShape(block, shape = engine.block.createShape(ShapeType.Rect))
        engine.block.setPositionX(block, value = 30F + (index % columns) * (blockWidth + 60F))
        engine.block.setPositionY(block, value = 30F + (index / columns) * (blockHeight + 60F))
        engine.block.setWidth(block, value = blockWidth)
        engine.block.setHeight(block, value = blockHeight)
        engine.block.setDuration(block, duration = 5.0)

        val fill = engine.block.createFill(FillType.Color)
        engine.block.setColor(fill, property = "fill/color/value", value = color)
        engine.block.setFill(block, fill = fill)
        engine.block.appendChild(parent = page, child = block)
        block
    }
    blocks.forEach { block ->
        check(engine.block.supportsAnimation(block))
    }

    val slideBlock = blocks[0]

    // highlight-android-entrance-slide
    val slideAnimation = engine.block.createAnimation(AnimationType.Slide)
    engine.block.setInAnimation(block = slideBlock, animation = slideAnimation)
    engine.block.setDuration(block = slideAnimation, duration = 1.0)
    // Animation-specific fields are exposed through the generic property API.
    engine.block.setFloat(
        block = slideAnimation,
        property = "animation/slide/direction",
        value = PI.toFloat(),
    )
    engine.block.setEnum(block = slideAnimation, property = "animationEasing", value = "EaseOut")
    // highlight-android-entrance-slide

    val fadeBlock = blocks[1]

    // highlight-android-entrance-fade
    val fadeAnimation = engine.block.createAnimation(AnimationType.Fade)
    engine.block.setInAnimation(block = fadeBlock, animation = fadeAnimation)
    engine.block.setDuration(block = fadeAnimation, duration = 1.0)
    engine.block.setEnum(block = fadeAnimation, property = "animationEasing", value = "EaseInOut")
    // highlight-android-entrance-fade

    val zoomBlock = blocks[2]

    // highlight-android-entrance-zoom
    val zoomAnimation = engine.block.createAnimation(AnimationType.Zoom)
    engine.block.setInAnimation(block = zoomBlock, animation = zoomAnimation)
    engine.block.setDuration(block = zoomAnimation, duration = 1.0)
    engine.block.setBoolean(block = zoomAnimation, property = "animation/zoom/fade", value = true)
    // highlight-android-entrance-zoom

    val exitBlock = blocks[3]

    // highlight-android-exit-animation
    val wipeIn = engine.block.createAnimation(AnimationType.Wipe)
    engine.block.setInAnimation(block = exitBlock, animation = wipeIn)
    engine.block.setDuration(block = wipeIn, duration = 1.0)
    engine.block.setEnum(block = wipeIn, property = "animation/wipe/direction", value = "Right")

    val fadeOut = engine.block.createAnimation(AnimationType.Fade)
    engine.block.setOutAnimation(block = exitBlock, animation = fadeOut)
    engine.block.setDuration(block = fadeOut, duration = 1.0)
    engine.block.setEnum(block = fadeOut, property = "animationEasing", value = "EaseIn")
    // highlight-android-exit-animation

    val loopBlock = blocks[4]

    // highlight-android-loop-animation
    val breathingLoop = engine.block.createAnimation(AnimationType.BreathingLoop)
    engine.block.setLoopAnimation(block = loopBlock, animation = breathingLoop)
    engine.block.setDuration(block = breathingLoop, duration = 2.0)
    // Intensity 0 scales to 1.25, while intensity 1 scales to 2.5.
    engine.block.setFloat(
        block = breathingLoop,
        property = "animation/breathing_loop/intensity",
        value = 0.3F,
    )
    // highlight-android-loop-animation

    val combinedBlock = blocks[5]

    // highlight-android-combined-animations
    val spinIn = engine.block.createAnimation(AnimationType.Spin)
    engine.block.setInAnimation(block = combinedBlock, animation = spinIn)
    engine.block.setDuration(block = spinIn, duration = 1.0)
    engine.block.setEnum(block = spinIn, property = "animation/spin/direction", value = "Clockwise")
    engine.block.setFloat(block = spinIn, property = "animation/spin/intensity", value = 0.5F)

    val blurOut = engine.block.createAnimation(AnimationType.Blur)
    engine.block.setOutAnimation(block = combinedBlock, animation = blurOut)
    engine.block.setDuration(block = blurOut, duration = 1.0)

    val swayLoop = engine.block.createAnimation(AnimationType.SwayLoop)
    engine.block.setLoopAnimation(block = combinedBlock, animation = swayLoop)
    engine.block.setDuration(block = swayLoop, duration = 1.5)
    // highlight-android-combined-animations

    // highlight-android-discover-properties
    val slideProperties = engine.block.findAllProperties(slideAnimation)
    val easingOptions = engine.block.getEnumValues("animationEasing")
    // highlight-android-discover-properties

    engine.block.setPlaybackTime(page, time = 1.9)

    return AnimationTypes(
        slideDirection = engine.block.getFloat(slideAnimation, property = "animation/slide/direction"),
        fadeEasing = engine.block.getEnum(fadeAnimation, property = "animationEasing"),
        zoomUsesFade = engine.block.getBoolean(zoomAnimation, property = "animation/zoom/fade"),
        wipeDirection = engine.block.getEnum(wipeIn, property = "animation/wipe/direction"),
        breathingIntensity = engine.block.getFloat(breathingLoop, property = "animation/breathing_loop/intensity"),
        spinDirection = engine.block.getEnum(spinIn, property = "animation/spin/direction"),
        spinIntensity = engine.block.getFloat(spinIn, property = "animation/spin/intensity"),
        slideProperties = slideProperties,
        easingOptions = easingOptions,
    )
}
