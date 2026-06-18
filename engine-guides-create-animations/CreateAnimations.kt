import ly.img.engine.AnimationEasingType
import ly.img.engine.AnimationType
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode

data class CreateAnimations(
    val supportsGraphicAnimation: Boolean,
    val slideDirection: Float,
    val slideEasing: String,
    val entranceDuration: Double,
    val exitDuration: Double,
    val loopAnimationType: String,
    val textWritingStyle: String,
    val textOverlap: Float,
    val replacedLoopAnimationIsValid: Boolean,
)

fun createAnimations(engine: Engine): CreateAnimations {
    val scene = engine.scene.createForVideo()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setWidth(page, value = 1080F)
    engine.block.setHeight(page, value = 1080F)
    engine.block.setDuration(block = page, duration = 5.0)

    val block = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(block, value = 180F)
    engine.block.setPositionY(block, value = 180F)
    engine.block.setWidth(block, value = 360F)
    engine.block.setHeight(block, value = 360F)
    val fill = engine.block.createFill(FillType.Color)
    engine.block.setFill(block = block, fill = fill)
    engine.block.setFillSolidColor(block = block, color = Color.fromRGBA(r = 0.12F, g = 0.44F, b = 0.95F, a = 1F))
    engine.block.appendChild(parent = page, child = block)

    // highlight-android-check-support
    val supportsGraphicAnimation = engine.block.supportsAnimation(block)
    if (!supportsGraphicAnimation) {
        error("Graphic block does not support animations.")
    }
    // highlight-android-check-support

    // highlight-android-entrance-animation
    val slideIn = engine.block.createAnimation(AnimationType.Slide)
    engine.block.setInAnimation(block = block, animation = slideIn)
    engine.block.setDuration(block = slideIn, duration = 1.0)
    // highlight-android-entrance-animation

    // highlight-android-exit-animation
    val fadeOut = engine.block.createAnimation(AnimationType.Fade)
    engine.block.setOutAnimation(block = block, animation = fadeOut)
    engine.block.setDuration(block = fadeOut, duration = 0.75)
    engine.block.setEnum(
        block = fadeOut,
        property = "animationEasing",
        value = AnimationEasingType.EASE_IN.key,
    )
    // highlight-android-exit-animation

    // highlight-android-loop-animation
    val breathingLoop = engine.block.createAnimation(AnimationType.BreathingLoop)
    engine.block.setLoopAnimation(block = block, animation = breathingLoop)
    engine.block.setDuration(block = breathingLoop, duration = 2.0)
    // highlight-android-loop-animation
    check(engine.block.getDuration(breathingLoop) == 2.0)

    // highlight-android-animation-properties
    val slideProperties = engine.block.findAllProperties(slideIn)
    if ("animation/slide/direction" in slideProperties) {
        engine.block.setFloat(
            block = slideIn,
            property = "animation/slide/direction",
            value = 1.5F * Math.PI.toFloat(),
        )
    }

    val easingOptions = engine.block.getEnumValues(enumProperty = "animationEasing")
    check(AnimationEasingType.EASE_OUT.key in easingOptions)
    engine.block.setEnum(
        block = slideIn,
        property = "animationEasing",
        value = AnimationEasingType.EASE_OUT.key,
    )
    // highlight-android-animation-properties

    val textBlock = engine.block.create(DesignBlockType.Text)
    engine.block.appendChild(parent = page, child = textBlock)
    engine.block.setPositionX(textBlock, value = 160F)
    engine.block.setPositionY(textBlock, value = 620F)
    engine.block.setWidth(textBlock, value = 720F)
    engine.block.setHeightMode(textBlock, mode = SizeMode.AUTO)
    engine.block.replaceText(
        textBlock,
        "Create animations\nline by line,\nword by word,\nor character by character.",
    )
    check(engine.block.supportsAnimation(textBlock))

    val groupedTextBlock = engine.block.create(DesignBlockType.Text)
    engine.block.appendChild(parent = page, child = groupedTextBlock)
    engine.block.setPositionX(groupedTextBlock, value = 160F)
    engine.block.setPositionY(groupedTextBlock, value = 820F)
    engine.block.setWidth(groupedTextBlock, value = 720F)
    engine.block.setHeightMode(groupedTextBlock, mode = SizeMode.AUTO)
    engine.block.replaceText(groupedTextBlock, "Reveal this line word by word.")
    check(engine.block.supportsAnimation(groupedTextBlock))

    // highlight-android-text-animation
    val typewriterText = engine.block.createAnimation(AnimationType.TypewriterText)
    engine.block.setInAnimation(block = textBlock, animation = typewriterText)
    engine.block.setDuration(block = typewriterText, duration = 2.0)

    val wordReveal = engine.block.createAnimation(AnimationType.Baseline)
    engine.block.setInAnimation(block = groupedTextBlock, animation = wordReveal)
    engine.block.setEnum(
        block = wordReveal,
        property = "textAnimationWritingStyle",
        value = "Word",
    )
    engine.block.setFloat(
        block = wordReveal,
        property = "textAnimationOverlap",
        value = 0.4F,
    )
    // highlight-android-text-animation

    // highlight-android-manage-lifecycle
    val previousLoop = engine.block.getLoopAnimation(block)
    val spinLoop = engine.block.createAnimation(AnimationType.SpinLoop)
    engine.block.setLoopAnimation(block = block, animation = spinLoop)
    if (engine.block.isValid(previousLoop)) {
        engine.block.destroy(previousLoop)
    }
    // highlight-android-manage-lifecycle

    val attachedIn = engine.block.getInAnimation(block)
    val attachedOut = engine.block.getOutAnimation(block)
    val attachedLoop = engine.block.getLoopAnimation(block)

    check(engine.block.isValid(attachedIn))
    check(engine.block.isValid(attachedOut))
    check(engine.block.isValid(attachedLoop))
    check(engine.block.getType(attachedIn) == AnimationType.Slide.key)
    check(engine.block.getType(attachedOut) == AnimationType.Fade.key)
    check(engine.block.getType(attachedLoop) == AnimationType.SpinLoop.key)
    check(engine.block.getEnum(slideIn, "animationEasing") == AnimationEasingType.EASE_OUT.key)
    check(engine.block.getEnum(fadeOut, "animationEasing") == AnimationEasingType.EASE_IN.key)
    check(engine.block.getType(typewriterText) == AnimationType.TypewriterText.key)
    check(engine.block.getEnum(wordReveal, "textAnimationWritingStyle") == "Word")
    check(engine.block.getFloat(wordReveal, "textAnimationOverlap") == 0.4F)
    check(engine.block.getDuration(attachedIn) == 1.0)
    check(engine.block.getDuration(attachedOut) == 0.75)

    return CreateAnimations(
        supportsGraphicAnimation = supportsGraphicAnimation,
        slideDirection = engine.block.getFloat(slideIn, "animation/slide/direction"),
        slideEasing = engine.block.getEnum(slideIn, "animationEasing"),
        entranceDuration = engine.block.getDuration(attachedIn),
        exitDuration = engine.block.getDuration(attachedOut),
        loopAnimationType = engine.block.getType(attachedLoop),
        textWritingStyle = engine.block.getEnum(wordReveal, "textAnimationWritingStyle"),
        textOverlap = engine.block.getFloat(wordReveal, "textAnimationOverlap"),
        replacedLoopAnimationIsValid = engine.block.isValid(previousLoop),
    )
}
