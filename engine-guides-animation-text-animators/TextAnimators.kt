import android.util.Log
import ly.img.engine.AnimationEasingType
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.DiscreteValue
import ly.img.engine.Engine
import ly.img.engine.EngineException
import ly.img.engine.Keyframe
import ly.img.engine.KeyframeEasing
import ly.img.engine.KeyframeValue
import ly.img.engine.MimeType
import ly.img.engine.SizeMode
import java.nio.ByteBuffer

private const val TAG = "TextAnimators"

data class TextAnimators(
    val textSupportsAnimators: Boolean,
    val pageSupportsAnimators: Boolean,
    val reveal: DesignBlock,
    val highlight: DesignBlock,
    val wave: DesignBlock,
    val orderAfterInsert: List<DesignBlock>,
    val highlightIsValidAfterRemove: Boolean,
    val orderAfterReappend: List<DesignBlock>,
    val revealAnimatedProperties: List<String>,
    val countdownKeyframeCount: Int,
    val errorCode: String?,
    val previewPng: ByteBuffer,
)

suspend fun textAnimators(engine: Engine): TextAnimators {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 1920F)
    engine.block.setHeight(block = page, value = 1080F)
    engine.block.appendChild(parent = scene, child = page)

    // highlight-android-create-text
    // A 5 second headline in the middle of the page
    val text = engine.block.create(DesignBlockType.Text)
    engine.block.replaceText(block = text, text = "Text animators move every letter")
    engine.block.setTextFontSize(block = text, fontSize = 120F)
    engine.block.setEnum(block = text, property = "text/horizontalAlignment", value = "Center")
    engine.block.setWidth(block = text, value = 1600F)
    engine.block.setHeightMode(block = text, mode = SizeMode.AUTO)
    engine.block.setPositionX(block = text, value = 160F)
    engine.block.setPositionY(block = text, value = 380F)
    engine.block.appendChild(parent = page, child = text)
    engine.block.setDuration(block = text, duration = 5.0)
    // highlight-android-create-text

    // highlight-android-create-animator
    val textSupportsAnimators = engine.block.supportsTextAnimators(block = text) // true
    val pageSupportsAnimators = engine.block.supportsTextAnimators(block = page) // false

    // The covered glyphs sit 60 units lower and are hidden
    val reveal = engine.block.createTextAnimator()
    engine.block.setFloat(block = reveal, property = "text_animator/positionY", value = 60F)
    engine.block.setBoolean(block = reveal, property = "text_animator/opacityEnabled", value = true)
    engine.block.setFloat(block = reveal, property = "text_animator/opacity", value = 0F)
    engine.block.appendTextAnimator(block = text, animatorBlock = reveal)
    // highlight-android-create-animator

    // highlight-android-keyframed-reveal
    // Move the start of the range from 0 to 1 in the first half of the
    // text's duration. The uncovered glyphs rise and fade in, left to right.
    engine.block.setKeyframes(
        block = reveal,
        property = "text_animator/selectorStart",
        keyframes = listOf(
            Keyframe(
                time = 0F,
                value = KeyframeValue.Float(0F),
                easing = KeyframeEasing.Preset(AnimationEasingType.EASE_OUT),
            ),
            Keyframe(time = 0.5F, value = KeyframeValue.Float(1F)),
        ),
    )
    // highlight-android-keyframed-reveal

    // highlight-android-range-selector
    // Cover the second word only: words are counted from 0 in index units
    val highlight = engine.block.createTextAnimator()
    engine.block.setEnum(block = highlight, property = "text_animator/selectorBasedOn", value = "Words")
    engine.block.setEnum(block = highlight, property = "text_animator/selectorUnits", value = "Index")
    engine.block.setFloat(block = highlight, property = "text_animator/selectorStart", value = 1F)
    engine.block.setFloat(block = highlight, property = "text_animator/selectorEnd", value = 2F)

    // Color and scale the covered glyphs
    engine.block.setBoolean(block = highlight, property = "text_animator/fillColorEnabled", value = true)
    engine.block.setColor(
        block = highlight,
        property = "text_animator/fillColor",
        value = Color.fromRGBA(r = 1F, g = 0.5F, b = 0F, a = 1F),
    )
    engine.block.setFloat(block = highlight, property = "text_animator/scaleX", value = 1.2F)
    engine.block.setFloat(block = highlight, property = "text_animator/scaleY", value = 1.2F)
    // highlight-android-range-selector

    // highlight-android-stack-order
    // Insert the highlight before the reveal
    engine.block.insertTextAnimator(block = text, animatorBlock = highlight, index = 0)
    val orderAfterInsert = engine.block.getTextAnimators(block = text) // [highlight, reveal]
    // highlight-android-stack-order

    // highlight-android-wave
    // A triangle covers a quarter of the characters and peaks in its middle
    val wave = engine.block.createTextAnimator()
    engine.block.setEnum(block = wave, property = "text_animator/selectorShape", value = "Triangle")
    engine.block.setFloat(block = wave, property = "text_animator/selectorEnd", value = 0.25F)
    engine.block.setFloat(block = wave, property = "text_animator/positionY", value = -40F)
    engine.block.setFloat(block = wave, property = "text_animator/rotation", value = -0.2F)

    // Move the range across the text over the whole duration
    engine.block.setKeyframes(
        block = wave,
        property = "text_animator/selectorOffset",
        keyframes = listOf(
            Keyframe(time = 0F, value = KeyframeValue.Float(-0.25F)),
            Keyframe(time = 1F, value = KeyframeValue.Float(1F)),
        ),
    )
    engine.block.appendTextAnimator(block = text, animatorBlock = wave)
    // highlight-android-wave

    // highlight-android-remove
    // Remove the highlight at index 0. The animator stays valid.
    engine.block.removeTextAnimator(block = text, index = 0)
    val highlightIsValidAfterRemove = engine.block.isValid(block = highlight) // true

    // Append it again so that it applies last
    engine.block.appendTextAnimator(block = text, animatorBlock = highlight)
    val orderAfterReappend = engine.block.getTextAnimators(block = text) // [reveal, wave, highlight]
    Log.i(TAG, "$orderAfterInsert, $highlightIsValidAfterRemove, $orderAfterReappend")
    // highlight-android-remove

    // highlight-android-text-content
    // A second text block counts down from 3 to 1
    val countdown = engine.block.create(DesignBlockType.Text)
    engine.block.replaceText(block = countdown, text = "3")
    engine.block.setTextFontSize(block = countdown, fontSize = 160F)
    engine.block.setWidthMode(block = countdown, mode = SizeMode.AUTO)
    engine.block.setHeightMode(block = countdown, mode = SizeMode.AUTO)
    engine.block.setPositionX(block = countdown, value = 900F)
    engine.block.setPositionY(block = countdown, value = 700F)
    engine.block.appendChild(parent = page, child = countdown)
    engine.block.setDuration(block = countdown, duration = 5.0)

    engine.block.setKeyframes(
        block = countdown,
        property = "text/text",
        keyframes = listOf(
            Keyframe(time = 0F, value = KeyframeValue.Discrete(DiscreteValue.Text("3"))),
            Keyframe(time = 0.33F, value = KeyframeValue.Discrete(DiscreteValue.Text("2"))),
            Keyframe(time = 0.67F, value = KeyframeValue.Discrete(DiscreteValue.Text("1"))),
        ),
    )
    // highlight-android-text-content

    // highlight-android-errors
    val errorCode = try {
        // The reveal already belongs to the headline
        engine.block.appendTextAnimator(block = countdown, animatorBlock = reveal)
        null
    } catch (error: EngineException) {
        // BLOCK.TEXT_ANIMATOR_ALREADY_ATTACHED
        error.code
    }
    // highlight-android-errors

    // highlight-android-export
    // Export the frame at 2 seconds, during the reveal
    engine.block.setPlaybackTime(block = page, time = 2.0)
    val previewPng = engine.block.export(block = page, mimeType = MimeType.PNG)
    // highlight-android-export

    return TextAnimators(
        textSupportsAnimators = textSupportsAnimators,
        pageSupportsAnimators = pageSupportsAnimators,
        reveal = reveal,
        highlight = highlight,
        wave = wave,
        orderAfterInsert = orderAfterInsert,
        highlightIsValidAfterRemove = highlightIsValidAfterRemove,
        orderAfterReappend = orderAfterReappend,
        revealAnimatedProperties = engine.block.getAnimatedProperties(block = reveal),
        countdownKeyframeCount = engine.block.getKeyframes(block = countdown, property = "text/text").size,
        errorCode = errorCode,
        previewPng = previewPng,
    )
}
