import ly.img.engine.AnimationEasingType
import ly.img.engine.AnimationType
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType

suspend fun editAnimations(engine: Engine): EditAnimationsSummary {
    val scene = engine.scene.createForVideo()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setWidth(block = page, value = 800F)
    engine.block.setHeight(block = page, value = 600F)
    engine.block.setDuration(block = page, duration = 5.0)

    val block = engine.block.create(DesignBlockType.Graphic)
    engine.block.appendChild(parent = page, child = block)
    engine.block.setShape(block = block, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(block = block, value = 100F)
    engine.block.setPositionY(block = block, value = 80F)
    engine.block.setWidth(block = block, value = 320F)
    engine.block.setHeight(block = block, value = 240F)

    val fill = engine.block.createFill(FillType.Color)
    engine.block.setColor(
        block = fill,
        property = "fill/color/value",
        value = Color.fromRGBA(r = 0.12F, g = 0.38F, b = 0.95F, a = 1F),
    )
    engine.block.setFill(block = block, fill = fill)

    check(engine.block.supportsAnimation(block = block))

    val slideInAnimation = engine.block.createAnimation(AnimationType.Slide)
    val fadeOutAnimation = engine.block.createAnimation(AnimationType.Fade)
    val breathingLoopAnimation = engine.block.createAnimation(AnimationType.BreathingLoop)

    engine.block.setInAnimation(block = block, animation = slideInAnimation)
    engine.block.setOutAnimation(block = block, animation = fadeOutAnimation)
    engine.block.setLoopAnimation(block = block, animation = breathingLoopAnimation)

    engine.block.setDuration(block = slideInAnimation, duration = 1.0)
    engine.block.setDuration(block = fadeOutAnimation, duration = 0.6)
    engine.block.setDuration(block = breathingLoopAnimation, duration = 1.5)
    engine.block.setEnum(
        block = slideInAnimation,
        property = "animationEasing",
        value = AnimationEasingType.EASE_OUT.key,
    )

    // highlight-android-retrieve-animations
    val inAnimation = engine.block.getInAnimation(block = block)
    val outAnimation = engine.block.getOutAnimation(block = block)
    val loopAnimation = engine.block.getLoopAnimation(block = block)

    check(engine.block.isValid(block = inAnimation)) { "Expected an In animation." }
    check(engine.block.isValid(block = outAnimation)) { "Expected an Out animation." }
    check(engine.block.isValid(block = loopAnimation)) { "Expected a Loop animation." }

    val inAnimationType = engine.block.getType(block = inAnimation)
    val outAnimationType = engine.block.getType(block = outAnimation)
    val loopAnimationType = engine.block.getType(block = loopAnimation)
    // highlight-android-retrieve-animations

    // highlight-android-read-properties
    val initialDuration = engine.block.getDuration(block = inAnimation)
    val initialEasing = engine.block.getEnum(
        block = inAnimation,
        property = "animationEasing",
    )
    val slideProperties = engine.block.findAllProperties(block = inAnimation)
    // highlight-android-read-properties

    // highlight-android-modify-duration
    engine.block.setDuration(block = inAnimation, duration = 0.8)
    engine.block.setDuration(block = loopAnimation, duration = 2.0)
    val updatedDuration = engine.block.getDuration(block = inAnimation)
    // highlight-android-modify-duration

    // highlight-android-change-easing
    // animationEasing is the Engine property key for animation acceleration curves.
    engine.block.setEnum(
        block = inAnimation,
        property = "animationEasing",
        value = AnimationEasingType.EASE_IN_OUT.key,
    )
    val updatedEasing = engine.block.getEnum(
        block = inAnimation,
        property = "animationEasing",
    )
    val easingOptions = engine.block.getEnumValues(enumProperty = "animationEasing")
    // highlight-android-change-easing

    // highlight-android-adjust-properties
    // Slide direction is exposed as radians.
    engine.block.setFloat(
        block = inAnimation,
        property = "animation/slide/direction",
        value = Math.PI.toFloat(),
    )
    val slideDirection = engine.block.getFloat(
        block = inAnimation,
        property = "animation/slide/direction",
    )
    // Slide fade is exposed as a boolean property.
    engine.block.setBoolean(
        block = inAnimation,
        property = "animation/slide/fade",
        value = true,
    )
    val slideFade = engine.block.getBoolean(
        block = inAnimation,
        property = "animation/slide/fade",
    )
    // highlight-android-adjust-properties

    // highlight-android-replace-animation
    val currentInAnimation = engine.block.getInAnimation(block = block)
    if (engine.block.isValid(block = currentInAnimation)) {
        engine.block.destroy(block = currentInAnimation)
    }

    val zoomAnimation = engine.block.createAnimation(type = AnimationType.Zoom)
    engine.block.setInAnimation(block = block, animation = zoomAnimation)
    engine.block.setDuration(block = zoomAnimation, duration = 0.6)
    engine.block.setEnum(
        block = zoomAnimation,
        property = "animationEasing",
        value = AnimationEasingType.EASE_IN_OUT.key,
    )

    val replacementType = engine.block.getType(
        block = engine.block.getInAnimation(block = block),
    )
    // highlight-android-replace-animation

    // highlight-android-remove-animation
    val currentLoopAnimation = engine.block.getLoopAnimation(block = block)
    if (engine.block.isValid(block = currentLoopAnimation)) {
        engine.block.destroy(block = currentLoopAnimation)
    }

    val loopAnimationRemoved = !engine.block.isValid(
        block = engine.block.getLoopAnimation(block = block),
    )
    // highlight-android-remove-animation

    return EditAnimationsSummary(
        inAnimationType = inAnimationType,
        outAnimationType = outAnimationType,
        loopAnimationType = loopAnimationType,
        initialDuration = initialDuration,
        updatedDuration = updatedDuration,
        initialEasing = initialEasing,
        updatedEasing = updatedEasing,
        easingOptions = easingOptions,
        slideProperties = slideProperties,
        slideDirection = slideDirection,
        slideFade = slideFade,
        replacementType = replacementType,
        loopAnimationRemoved = loopAnimationRemoved,
    )
}
