import ly.img.engine.AnimationEasingType
import ly.img.engine.AnimationType
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine

suspend fun textAnimations(engine: Engine): TextAnimationSummary {
    val scene = engine.scene.createForVideo()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 1920F)
    engine.block.setHeight(block = page, value = 1080F)
    engine.block.setDuration(block = page, duration = 10.0)
    engine.block.appendChild(parent = scene, child = page)

    val introText = engine.block.create(DesignBlockType.Text)
    engine.block.setPositionX(block = introText, value = 100F)
    engine.block.setPositionY(block = introText, value = 100F)
    engine.block.setWidth(block = introText, value = 600F)
    engine.block.setHeight(block = introText, value = 200F)
    engine.block.replaceText(block = introText, text = "Creating\nText\nAnimations")
    engine.block.appendChild(parent = page, child = introText)

    check(engine.block.supportsAnimation(block = introText))

    // highlight-android-create-animation
    val baselineAnimation = engine.block.createAnimation(type = AnimationType.Baseline)
    engine.block.setInAnimation(block = introText, animation = baselineAnimation)
    engine.block.setDuration(block = baselineAnimation, duration = 2.0)
    // highlight-android-create-animation

    val blockText = engine.block.create(DesignBlockType.Text)
    engine.block.setPositionX(block = blockText, value = 1300F)
    engine.block.setPositionY(block = blockText, value = 700F)
    engine.block.setWidth(block = blockText, value = 500F)
    engine.block.setHeight(block = blockText, value = 200F)
    engine.block.replaceText(block = blockText, text = "Animate the complete text block")
    engine.block.appendChild(parent = page, child = blockText)

    val blockAnimation = engine.block.createAnimation(type = AnimationType.Baseline)
    engine.block.setInAnimation(block = blockText, animation = blockAnimation)
    engine.block.setDuration(block = blockAnimation, duration = 2.0)
    // highlight-android-writing-style-block
    engine.block.setEnum(block = blockAnimation, property = "textAnimationWritingStyle", value = "Block")
    // highlight-android-writing-style-block
    engine.block.setEnum(block = blockAnimation, property = "animationEasing", value = AnimationEasingType.EASE_OUT.key)

    val lineText = engine.block.create(DesignBlockType.Text)
    engine.block.setPositionX(block = lineText, value = 700F)
    engine.block.setPositionY(block = lineText, value = 100F)
    engine.block.setWidth(block = lineText, value = 600F)
    engine.block.setHeight(block = lineText, value = 200F)
    engine.block.replaceText(block = lineText, text = "Line by line\nanimation\nfor text")
    engine.block.appendChild(parent = page, child = lineText)

    val lineAnimation = engine.block.createAnimation(type = AnimationType.Baseline)
    engine.block.setInAnimation(block = lineText, animation = lineAnimation)
    engine.block.setDuration(block = lineAnimation, duration = 2.0)
    // highlight-android-writing-style-line
    engine.block.setEnum(block = lineAnimation, property = "textAnimationWritingStyle", value = "Line")
    // highlight-android-writing-style-line
    engine.block.setEnum(block = lineAnimation, property = "animationEasing", value = AnimationEasingType.EASE_OUT.key)

    val wordText = engine.block.create(DesignBlockType.Text)
    engine.block.setPositionX(block = wordText, value = 1300F)
    engine.block.setPositionY(block = wordText, value = 100F)
    engine.block.setWidth(block = wordText, value = 600F)
    engine.block.setHeight(block = wordText, value = 200F)
    engine.block.replaceText(block = wordText, text = "Animate word by word for emphasis")
    engine.block.appendChild(parent = page, child = wordText)

    val wordAnimation = engine.block.createAnimation(type = AnimationType.Baseline)
    engine.block.setInAnimation(block = wordText, animation = wordAnimation)
    engine.block.setDuration(block = wordAnimation, duration = 2.5)
    // highlight-android-writing-style-word
    engine.block.setEnum(block = wordAnimation, property = "textAnimationWritingStyle", value = "Word")
    // highlight-android-writing-style-word
    engine.block.setEnum(block = wordAnimation, property = "animationEasing", value = AnimationEasingType.EASE_OUT.key)

    val characterText = engine.block.create(DesignBlockType.Text)
    engine.block.setPositionX(block = characterText, value = 100F)
    engine.block.setPositionY(block = characterText, value = 400F)
    engine.block.setWidth(block = characterText, value = 600F)
    engine.block.setHeight(block = characterText, value = 200F)
    engine.block.replaceText(block = characterText, text = "Character by character for typewriter effect")
    engine.block.appendChild(parent = page, child = characterText)

    val characterAnimation = engine.block.createAnimation(type = AnimationType.Baseline)
    engine.block.setInAnimation(block = characterText, animation = characterAnimation)
    engine.block.setDuration(block = characterAnimation, duration = 3.0)
    // highlight-android-writing-style-character
    engine.block.setEnum(block = characterAnimation, property = "textAnimationWritingStyle", value = "Character")
    // highlight-android-writing-style-character
    engine.block.setEnum(block = characterAnimation, property = "animationEasing", value = AnimationEasingType.LINEAR.key)

    val sequentialText = engine.block.create(DesignBlockType.Text)
    engine.block.setPositionX(block = sequentialText, value = 700F)
    engine.block.setPositionY(block = sequentialText, value = 400F)
    engine.block.setWidth(block = sequentialText, value = 600F)
    engine.block.setHeight(block = sequentialText, value = 200F)
    engine.block.replaceText(block = sequentialText, text = "Sequential animation with zero overlap")
    engine.block.appendChild(parent = page, child = sequentialText)

    val sequentialAnimation = engine.block.createAnimation(type = AnimationType.Pan)
    engine.block.setInAnimation(block = sequentialText, animation = sequentialAnimation)
    engine.block.setDuration(block = sequentialAnimation, duration = 2.0)
    engine.block.setEnum(block = sequentialAnimation, property = "textAnimationWritingStyle", value = "Word")
    // highlight-android-overlap-sequential
    engine.block.setFloat(block = sequentialAnimation, property = "textAnimationOverlap", value = 0.0F)
    // highlight-android-overlap-sequential
    engine.block.setEnum(block = sequentialAnimation, property = "animationEasing", value = AnimationEasingType.EASE_OUT.key)

    val cascadingText = engine.block.create(DesignBlockType.Text)
    engine.block.setPositionX(block = cascadingText, value = 1300F)
    engine.block.setPositionY(block = cascadingText, value = 400F)
    engine.block.setWidth(block = cascadingText, value = 600F)
    engine.block.setHeight(block = cascadingText, value = 200F)
    engine.block.replaceText(block = cascadingText, text = "Cascading animation with partial overlap")
    engine.block.appendChild(parent = page, child = cascadingText)

    val cascadingAnimation = engine.block.createAnimation(type = AnimationType.Pan)
    engine.block.setInAnimation(block = cascadingText, animation = cascadingAnimation)
    engine.block.setDuration(block = cascadingAnimation, duration = 1.5)
    engine.block.setEnum(block = cascadingAnimation, property = "textAnimationWritingStyle", value = "Word")
    // highlight-android-overlap-cascading
    engine.block.setFloat(block = cascadingAnimation, property = "textAnimationOverlap", value = 0.4F)
    // highlight-android-overlap-cascading
    engine.block.setEnum(block = cascadingAnimation, property = "animationEasing", value = AnimationEasingType.EASE_OUT.key)

    val combinedText = engine.block.create(DesignBlockType.Text)
    engine.block.setPositionX(block = combinedText, value = 100F)
    engine.block.setPositionY(block = combinedText, value = 700F)
    engine.block.setWidth(block = combinedText, value = 1200F)
    engine.block.setHeight(block = combinedText, value = 200F)
    engine.block.replaceText(block = combinedText, text = "Combine writing style, overlap, duration, and easing")
    engine.block.appendChild(parent = page, child = combinedText)

    val combinedAnimation = engine.block.createAnimation(type = AnimationType.Fade)
    engine.block.setInAnimation(block = combinedText, animation = combinedAnimation)
    // highlight-android-duration-easing
    engine.block.setEnum(block = combinedAnimation, property = "textAnimationWritingStyle", value = "Word")
    engine.block.setFloat(block = combinedAnimation, property = "textAnimationOverlap", value = 0.3F)
    engine.block.setDuration(block = combinedAnimation, duration = 1.5)
    engine.block.setEnum(block = combinedAnimation, property = "animationEasing", value = AnimationEasingType.EASE_IN_OUT.key)

    val writingStyleOptions = engine.block.getEnumValues(enumProperty = "textAnimationWritingStyle")
    val easingOptions = engine.block.getEnumValues(enumProperty = "animationEasing")
    // highlight-android-duration-easing

    return TextAnimationSummary(
        writingStyleOptions = writingStyleOptions,
        easingOptions = easingOptions,
        blockWritingStyle = engine.block.getEnum(block = blockAnimation, property = "textAnimationWritingStyle"),
        lineWritingStyle = engine.block.getEnum(block = lineAnimation, property = "textAnimationWritingStyle"),
        wordWritingStyle = engine.block.getEnum(block = wordAnimation, property = "textAnimationWritingStyle"),
        characterWritingStyle = engine.block.getEnum(block = characterAnimation, property = "textAnimationWritingStyle"),
        sequentialOverlap = engine.block.getFloat(block = sequentialAnimation, property = "textAnimationOverlap"),
        cascadingOverlap = engine.block.getFloat(block = cascadingAnimation, property = "textAnimationOverlap"),
        combinedDuration = engine.block.getDuration(block = combinedAnimation),
    )
}
