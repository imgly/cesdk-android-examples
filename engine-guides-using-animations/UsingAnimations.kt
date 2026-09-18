import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.AnimationEasingType
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
    engine.block.setUri(
        block = fill,
        property = "fill/image/imageFileURI",
        value = Uri.parse("https://img.ly/static/ubq_samples/sample_1.jpg"),
    )
    engine.block.setFill(block, fill = fill)
    // highlight-setup

    // highlight-android-supports-animation
    val supportsAnimations = engine.block.supportsAnimation(block)

    if (supportsAnimations) {
        val slideAnimation = engine.block.createAnimation(AnimationType.Slide)
        engine.block.setInAnimation(block = block, animation = slideAnimation)
        engine.block.setDuration(block = slideAnimation, duration = 1.0)
    }
    // highlight-android-supports-animation

    if (supportsAnimations) {
        val initialIn = engine.block.getInAnimation(block)
        if (engine.block.isValid(initialIn)) {
            engine.block.destroy(initialIn)
        }

        // highlight-android-entrance-animation
        val fadeInAnimation = engine.block.createAnimation(AnimationType.Fade)
        engine.block.setInAnimation(block = block, animation = fadeInAnimation)
        engine.block.setDuration(block = fadeInAnimation, duration = 1.0)
        engine.block.setEnum(
            block = fadeInAnimation,
            property = "animationEasing",
            value = AnimationEasingType.EASE_OUT.key,
        )
        // highlight-android-entrance-animation

        val entranceIn = engine.block.getInAnimation(block)
        if (engine.block.isValid(entranceIn)) {
            engine.block.destroy(entranceIn)
        }

        val entranceForTiming = engine.block.createAnimation(AnimationType.Zoom)
        engine.block.setInAnimation(block = block, animation = entranceForTiming)
        engine.block.setDuration(block = entranceForTiming, duration = 1.0)

        // highlight-android-exit-animation
        val fadeOutAnimation = engine.block.createAnimation(AnimationType.Fade)
        engine.block.setOutAnimation(block = block, animation = fadeOutAnimation)
        engine.block.setDuration(block = fadeOutAnimation, duration = 1.0)
        engine.block.setEnum(
            block = fadeOutAnimation,
            property = "animationEasing",
            value = AnimationEasingType.EASE_IN.key,
        )
        // highlight-android-exit-animation

        val timingIn = engine.block.getInAnimation(block)
        if (engine.block.isValid(timingIn)) {
            engine.block.destroy(timingIn)
        }

        // highlight-android-loop-animation
        val breathingLoop = engine.block.createAnimation(AnimationType.BreathingLoop)
        engine.block.setLoopAnimation(block = block, animation = breathingLoop)
        engine.block.setDuration(block = breathingLoop, duration = 2.0)
        // highlight-android-loop-animation

        // highlight-android-animation-properties
        val slideFromTop = engine.block.createAnimation(AnimationType.Slide)
        engine.block.setInAnimation(block = block, animation = slideFromTop)
        engine.block.setDuration(block = slideFromTop, duration = 1.0)

        val slideProperties = engine.block.findAllProperties(slideFromTop)
        val slideDirectionProperty = "animation/slide/direction"
        check(slideDirectionProperty in slideProperties)
        engine.block.setFloat(
            block = slideFromTop,
            property = slideDirectionProperty,
            value = 0.5F * Math.PI.toFloat(),
        )
        engine.block.setEnum(
            block = slideFromTop,
            property = "animationEasing",
            value = AnimationEasingType.EASE_IN_OUT.key,
        )
        // highlight-android-animation-properties

        val currentLoop = engine.block.getLoopAnimation(block)
        val currentOut = engine.block.getOutAnimation(block)

        // highlight-android-manage-animations
        val currentIn = engine.block.getInAnimation(block)

        if (engine.block.isValid(currentIn)) {
            engine.block.destroy(currentIn)
        }
        val replacementIn = engine.block.createAnimation(AnimationType.Wipe)
        engine.block.setInAnimation(block = block, animation = replacementIn)
        engine.block.setDuration(block = replacementIn, duration = 1.0)
        // highlight-android-manage-animations

        // highlight-android-easing-options
        val easingOptions = engine.block.getEnumValues("animationEasing")
        // highlight-android-easing-options

        check(engine.block.isValid(currentLoop))
        check(engine.block.isValid(currentOut))
        check(easingOptions.contains(AnimationEasingType.EASE_OUT.key))
        check(engine.block.getDuration(replacementIn) == 1.0)
    }

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
