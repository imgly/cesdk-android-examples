import ly.img.engine.AnimationEasingType
import ly.img.engine.AnimationType
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode

data class ProgrammaticAnimations(
    val slideInType: String,
    val firstLoopType: String,
    val replacementLoopType: String,
    val outType: String,
    val slideDirection: Float,
    val slideDuration: Double,
    val slideEasing: String,
    val slideProperties: List<String>,
    val easingValues: List<String>,
    val textWritingStyle: String,
    val textOverlap: Float,
)

suspend fun programmaticAnimations(engine: Engine): ProgrammaticAnimations {
    val scene = engine.scene.createForVideo()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setWidth(block = page, value = 1080F)
    engine.block.setHeight(block = page, value = 1080F)
    engine.block.setDuration(block = page, duration = 5.0)

    val block = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block = block, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(block = block, value = 290F)
    engine.block.setPositionY(block = block, value = 260F)
    engine.block.setWidth(block = block, value = 500F)
    engine.block.setHeight(block = block, value = 320F)
    engine.block.setDuration(block = block, duration = 4.0)
    engine.block.setFill(block = block, fill = engine.block.createFill(FillType.Color))
    engine.block.appendChild(parent = page, child = block)

    val textBlock = engine.block.create(DesignBlockType.Text)
    engine.block.appendChild(parent = page, child = textBlock)
    engine.block.setPositionX(block = textBlock, value = 180F)
    engine.block.setPositionY(block = textBlock, value = 700F)
    engine.block.setWidth(block = textBlock, value = 720F)
    engine.block.setHeightMode(block = textBlock, mode = SizeMode.AUTO)
    engine.block.replaceText(textBlock, "Animate text one word at a time")

    // highlight-android-check-support
    check(engine.block.supportsAnimation(block)) {
        "This block does not support animations."
    }
    // highlight-android-check-support

    // highlight-android-create-animations
    val slideInAnimation = engine.block.createAnimation(AnimationType.Slide)
    val breathingLoopAnimation = engine.block.createAnimation(AnimationType.BreathingLoop)
    val fadeOutAnimation = engine.block.createAnimation(AnimationType.Fade)
    // highlight-android-create-animations

    // highlight-android-attach-animations
    engine.block.setInAnimation(block = block, animation = slideInAnimation)
    engine.block.setLoopAnimation(block = block, animation = breathingLoopAnimation)
    engine.block.setOutAnimation(block = block, animation = fadeOutAnimation)
    // highlight-android-attach-animations

    // highlight-android-configure-properties
    val slideProperties = engine.block.findAllProperties(slideInAnimation)
    val easingValues = engine.block.getEnumValues("animationEasing")

    check(slideProperties.contains("animation/slide/direction")) {
        "Slide animations do not expose animation/slide/direction."
    }
    check(easingValues.contains(AnimationEasingType.EASE_OUT.key)) {
        "The animationEasing enum does not expose ${AnimationEasingType.EASE_OUT.key}."
    }

    engine.block.setDuration(block = slideInAnimation, duration = 0.6)
    engine.block.setEnum(
        block = slideInAnimation,
        property = "animationEasing",
        value = AnimationEasingType.EASE_OUT.key,
    )
    // No type-safe Android helper exists for this animation-specific property yet.
    engine.block.setFloat(
        block = slideInAnimation,
        property = "animation/slide/direction",
        value = 0.5F * Math.PI.toFloat(),
    )
    // highlight-android-configure-properties

    // highlight-android-read-animations
    val currentInAnimation = engine.block.getInAnimation(block)
    val currentLoopAnimation = engine.block.getLoopAnimation(block)
    val currentOutAnimation = engine.block.getOutAnimation(block)

    check(engine.block.isValid(currentInAnimation))
    val currentLoopType = engine.block.getType(currentLoopAnimation)
    // highlight-android-read-animations

    // highlight-android-replace-animation
    val previousLoopAnimation = engine.block.getLoopAnimation(block)
    val squeezeLoopAnimation = engine.block.createAnimation(AnimationType.SqueezeLoop)

    engine.block.destroy(previousLoopAnimation)
    engine.block.setLoopAnimation(block = block, animation = squeezeLoopAnimation)
    // highlight-android-replace-animation

    // highlight-android-text-animation-properties
    val textAnimation = engine.block.createAnimation(AnimationType.Baseline)
    engine.block.setInAnimation(block = textBlock, animation = textAnimation)
    engine.block.setEnum(block = textAnimation, property = "textAnimationWritingStyle", value = "Word")
    engine.block.setFloat(block = textAnimation, property = "textAnimationOverlap", value = 0.4F)
    // highlight-android-text-animation-properties

    val replacementLoopAnimation = engine.block.getLoopAnimation(block)

    check(engine.block.getType(currentInAnimation) == AnimationType.Slide.key)
    check(currentLoopType == AnimationType.BreathingLoop.key)
    check(engine.block.getType(replacementLoopAnimation) == AnimationType.SqueezeLoop.key)
    check(engine.block.getType(currentOutAnimation) == AnimationType.Fade.key)
    check(slideProperties.contains("animation/slide/direction"))
    check(easingValues.contains(AnimationEasingType.EASE_OUT.key))
    check(engine.block.getFloat(slideInAnimation, "animation/slide/direction") == 0.5F * Math.PI.toFloat())
    check(engine.block.getDuration(slideInAnimation) == 0.6)
    check(engine.block.getEnum(slideInAnimation, "animationEasing") == AnimationEasingType.EASE_OUT.key)
    check(engine.block.getEnum(textAnimation, "textAnimationWritingStyle") == "Word")
    check(engine.block.getFloat(textAnimation, "textAnimationOverlap") == 0.4F)

    return ProgrammaticAnimations(
        slideInType = engine.block.getType(currentInAnimation),
        firstLoopType = currentLoopType,
        replacementLoopType = engine.block.getType(replacementLoopAnimation),
        outType = engine.block.getType(currentOutAnimation),
        slideDirection = engine.block.getFloat(slideInAnimation, "animation/slide/direction"),
        slideDuration = engine.block.getDuration(slideInAnimation),
        slideEasing = engine.block.getEnum(slideInAnimation, "animationEasing"),
        slideProperties = slideProperties,
        easingValues = easingValues,
        textWritingStyle = engine.block.getEnum(textAnimation, "textAnimationWritingStyle"),
        textOverlap = engine.block.getFloat(textAnimation, "textAnimationOverlap"),
    )
}
