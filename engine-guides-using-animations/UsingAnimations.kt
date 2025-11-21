import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.AnimationType
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode

fun usingAnimations(
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1080, height = 1920)

    // highlight-setup
    val scene = engine.scene.createForVideo()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    engine.scene.zoomToBlock(
        page,
        paddingLeft = 40F,
        paddingTop = 40F,
        paddingRight = 40F,
        paddingBottom = 40F,
    )

    val block = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(block, value = 100F)
    engine.block.setPositionY(block, value = 50F)
    engine.block.setWidth(block, value = 300F)
    engine.block.setHeight(block, value = 300F)
    engine.block.appendChild(parent = page, child = block)
    val fill = engine.block.createFill(FillType.Image)
    engine.block.setString(
        block = fill,
        property = "fill/image/imageFileURI",
        value = "https://img.ly/static/ubq_samples/sample_1.jpg",
    )
    engine.block.setFill(block, fill = fill)
    // highlight-setup

    // highlight-supportsAnimation
    if (!engine.block.supportsAnimation(block)) {
        engine.stop()
        return@launch
    }
    // highlight-supportsAnimation

    // highlight-createAnimation
    val slideInAnimation = engine.block.createAnimation(AnimationType.Slide)
    val breathingLoopAnimation = engine.block.createAnimation(AnimationType.BreathingLoop)
    val fadeOutAnimation = engine.block.createAnimation(AnimationType.Fade)
    // highlight-createAnimation
    // highlight-setInAnimation
    engine.block.setInAnimation(block, slideInAnimation)
    // highlight-setInAnimation
    // highlight-setLoopAnimation
    engine.block.setLoopAnimation(block, breathingLoopAnimation)
    // highlight-setLoopAnimation
    // highlight-setOutAnimation
    engine.block.setOutAnimation(block, fadeOutAnimation)
    // highlight-setOutAnimation
    // highlight-getAnimation
    val animation = engine.block.getLoopAnimation(block)
    val animationType = engine.block.getType(animation)
    // highlight-getAnimation

    // highlight-replaceAnimation
    val squeezeLoopAnimation = engine.block.createAnimation(AnimationType.SqueezeLoop)
    engine.block.destroy(engine.block.getLoopAnimation(block))
    engine.block.setLoopAnimation(block, squeezeLoopAnimation)
    // The following line would also destroy all currently attached animations
    // engine.block.destroy(block)
    // highlight-replaceAnimation

    // highlight-getProperties
    val allAnimationProperties = engine.block.findAllProperties(slideInAnimation)
    // highlight-getProperties
    // highlight-modifyProperties
    engine.block.setFloat(slideInAnimation, "animation/slide/direction", 0.5F * Math.PI.toFloat())
    // highlight-modifyProperties
    // highlight-changeDuration
    engine.block.setDuration(slideInAnimation, 0.6)
    // highlight-changeDuration
    // highlight-changeEasing
    engine.block.setEnum(slideInAnimation, "animationEasing", "EaseOut")
    println("Available easing options: ${engine.block.getEnumValues("animationEasing")}")
    // highlight-changeEasing

    // highlight-textAnimationWritingStyle
    val text = engine.block.create(DesignBlockType.Text)
    val textAnimation = engine.block.createAnimation(AnimationType.Baseline)
    engine.block.setInAnimation(text, textAnimation)
    engine.block.appendChild(page, text)
    engine.block.setPositionX(text, 100F)
    engine.block.setPositionY(text, 100F)
    engine.block.setWidthMode(text, SizeMode.AUTO)
    engine.block.setHeightMode(text, SizeMode.AUTO)
    engine.block.replaceText(text, "You can animate text\nline by line,\nword by word,\nor character by character\nwith CE.SDK")
    engine.block.setEnum(textAnimation, "textAnimationWritingStyle", "Word")
    engine.block.setDuration(textAnimation, 2.0)
    engine.block.setEnum(textAnimation, "animationEasing", "EaseOut")
    // highlight-textAnimationWritingStyle

    // highlight-textAnimationOverlap
    val text2 = engine.block.create(DesignBlockType.Text)
    val textAnimation2 = engine.block.createAnimation(AnimationType.Pan)
    engine.block.setInAnimation(text2, textAnimation2)
    engine.block.appendChild(page, text2)
    engine.block.setPositionX(text2, 100F)
    engine.block.setPositionY(text2, 500F)
    engine.block.setWidth(text2, 500F)
    engine.block.setHeightMode(text2, SizeMode.AUTO)
    engine.block.replaceText(text2, "You can use the textAnimationOverlap property to control the overlap between text animation segments.")
    engine.block.setFloat(textAnimation2, "textAnimationOverlap", 0.4F)
    engine.block.setDuration(textAnimation2, 1.0)
    engine.block.setEnum(textAnimation2, "animationEasing", "EaseOut")
    // highlight-textAnimationOverlap

    engine.stop()
}
