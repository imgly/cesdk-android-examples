package ly.img.editor.base.engine

import ly.img.editor.base.R
import ly.img.engine.BlockApi
import ly.img.engine.BlurType
import ly.img.engine.DesignBlock
import ly.img.engine.EffectType
import ly.img.engine.Engine
import ly.img.engine.ObjectType

enum class AdjustmentsValueType {
    INT,
    FLOAT,
}

/*
 * Only one effect of the same group can be applied at a time.
 * So if you have two effects of the same group, we need to remove the first one before applying the second one.
 * This Group helps to identify the group of an effect, in the effect list.
 */
enum class EffectGroup {
    Filter,
    FxEffect,
    Adjustments
}

fun EffectType.getGroup() = when(this) {
    EffectType.LutFilter,
    EffectType.DuoToneFilter ->
        EffectGroup.Filter

    EffectType.CrossCut,
    EffectType.DotPattern,
    EffectType.ExtrudeBlur,
    EffectType.Glow,
    EffectType.HalfTone,
    EffectType.Linocut,
    EffectType.Liquid,
    EffectType.Mirror,
    EffectType.Outliner,
    EffectType.Pixelize,
    EffectType.Posterize,
    EffectType.RadialPixel,
    EffectType.Sharpie,
    EffectType.Shifter,
    EffectType.TiltShift,
    EffectType.TvGlitch,
    EffectType.Vignette ->
        EffectGroup.FxEffect

    EffectType.Adjustments ->
        EffectGroup.Adjustments
}

/**
 * State of an effect or blur adjustment, including the type and the current value of this adjustment.
 */
data class AdjustmentState(
    val type: EffectAndBlurOptions,
    var value: Float
)

/**
 * Returns the type of the blur or effect applied to the given [DesignBlock], or null if no blur or effect is applied.
 */
fun BlockApi.getBlurOrEffectType(effect: DesignBlock?) : ObjectType? {
    effect ?: return null
    val typeString = this.getType(effect)
    return BlurType.getOrNull(typeString) ?: EffectType.getOrNull(typeString)
}

/**
 * Enum of all available blur and effect options.
 * It can be filtered by a BlurType or EffectType to get only the options available for a specific blur or effect.
 */
enum class EffectAndBlurOptions(
    val type: ObjectType,
    relativePath: String,
    val nameRes: Int,
    val range: ClosedFloatingPointRange<Float> = 0.0f..1.0f,
    val step: Float = 0.0f,
    val defaultValue: Float = 1.0f,
    val propertyType: AdjustmentsValueType = AdjustmentsValueType.FLOAT,
) {
    //ADJUSTMENT_CONTRAST
    ADJUSTMENT_BRIGHTNESS(EffectType.Adjustments, "brightness", R.string.cesdk_adjustment_brightness, -1f..1f, defaultValue = .0f),
    ADJUSTMENT_SATURATION(EffectType.Adjustments, "saturation", R.string.cesdk_adjustment_saturation, -1f..1f, defaultValue = .0f),
    ADJUSTMENT_CONTRAST(EffectType.Adjustments, "contrast", R.string.cesdk_adjustment_contrast, -1f..1f, defaultValue = .0f),
    ADJUSTMENT_GAMMA(EffectType.Adjustments, "gamma", R.string.cesdk_adjustment_gamma, -1f..1f, defaultValue = .0f),

    ADJUSTMENT_CLARITY(EffectType.Adjustments, "clarity", R.string.cesdk_adjustment_clarity, -1f..1f, defaultValue = .0f),
    ADJUSTMENT_EXPOSURE(EffectType.Adjustments, "exposure", R.string.cesdk_adjustment_exposure, -1f..1f, defaultValue = .0f),
    ADJUSTMENT_SHADOWS(EffectType.Adjustments, "shadows", R.string.cesdk_adjustment_shadows, -1f..1f, defaultValue = .0f),
    ADJUSTMENT_HIGHLIGHTS(EffectType.Adjustments, "highlights", R.string.cesdk_adjustment_highlights, -1f..1f, defaultValue = .0f),
    ADJUSTMENT_BLACKS(EffectType.Adjustments, "blacks" , R.string.cesdk_adjustment_blacks, -1f..1f, defaultValue = .0f),
    ADJUSTMENT_WHITES(EffectType.Adjustments, "whites", R.string.cesdk_adjustment_whites, -1f..1f, defaultValue = .0f),
    ADJUSTMENT_TEMPERATURE(EffectType.Adjustments, "temperature", R.string.cesdk_adjustment_temperature, -1f..1f, defaultValue = .0f),
    ADJUSTMENT_SHARPNESS(EffectType.Adjustments, "sharpness", R.string.cesdk_adjustment_sharpness, -1f..1f, defaultValue = .0f),

    // Color Filter
    LUT_INTENSITY(EffectType.LutFilter, "intensity", R.string.cesdk_filter_intensity),

    // DouTone Filter
    DUOTONE_INTENSITY(EffectType.DuoToneFilter, "intensity", R.string.cesdk_filter_intensity, -1f..1f, defaultValue = 0.0f),

    // Pixelize Effect
    PIXELIZE_HORIZONTALPIXELSIZE(EffectType.Pixelize, "horizontalPixelSize", R.string.cesdk_filter_pixelize_horizontalpixelsize, 5.0f..50.0f, 1.0f, 20.0f, propertyType = AdjustmentsValueType.INT),
    PIXELIZE_VERTICALPIXELSIZE(EffectType.Pixelize, "verticalPixelSize", R.string.cesdk_filter_pixelize_verticalpixelsize, 5.0f..50.0f, 1.0f, 20.0f, propertyType = AdjustmentsValueType.INT),

    // Radial Pixel Effect
    RADIAL_PIXEL_RADIUS(EffectType.RadialPixel, "radius", R.string.cesdk_filter_radial_pixel_radius, 0.05f..1.0f, 0.01f, 0.1f),
    RADIAL_PIXEL_SEGMENTS(EffectType.RadialPixel, "segments", R.string.cesdk_filter_radial_pixel_segments, 0.01f..1.0f, 0.01f, 0.01f),

    // Cross Cut Effect
    CROSS_CUT_SLICES(EffectType.CrossCut, "slices", R.string.cesdk_filter_cross_cut_slices, 1.0f..10.0f, 1.0f, 5.0f),
    CROSS_CUT_OFFSET(EffectType.CrossCut, "offset", R.string.cesdk_filter_cross_cut_offset, 0.0f..1.0f, 0.01f, 0.07f),
    CROSS_CUT_SPEEDV(EffectType.CrossCut, "speedV", R.string.cesdk_filter_cross_cut_speedv, 0.0f..1.0f, 0.01f, 0.5f),
    CROSS_CUT_TIME(EffectType.CrossCut, "time", R.string.cesdk_filter_cross_cut_time, 0.0f..1.0f, 0.01f, 1.0f),

    // Liquid Effect
    LIQUID_AMOUNT(EffectType.Liquid, "amount", R.string.cesdk_filter_liquid_amount, 0.0f..1.0f, 0.01f, 0.06f),
    LIQUID_SCALE(EffectType.Liquid, "scale", R.string.cesdk_filter_liquid_scale, 0.0f..1.0f, 0.01f, 0.62f),
    LIQUID_TIME(EffectType.Liquid, "time", R.string.cesdk_filter_liquid_time, 0.0f..1.0f, 0.01f, 0.5f),

    // Outliner Effect
    OUTLINER_AMOUNT(EffectType.Outliner, "amount", R.string.cesdk_filter_outliner_amount, 0.0f..1.0f, 0.01f, 0.5f),
    OUTLINER_PASSTHROUGH(EffectType.Outliner, "passthrough", R.string.cesdk_filter_outliner_passthrough, 0.0f..1.0f, 0.01f, 0.5f),

    // Dot Pattern Effect
    DOT_PATTERN_DOTS(EffectType.DotPattern, "dots", R.string.cesdk_filter_dot_pattern_dots, 1.0f..80.0f, 1.0f, 30.0f),
    DOT_PATTERN_SIZE(EffectType.DotPattern, "size", R.string.cesdk_filter_dot_pattern_size, 0.0f..1.0f, 0.01f, 0.5f),
    DOT_PATTERN_BLUR(EffectType.DotPattern, "blur", R.string.cesdk_filter_dot_pattern_blur, 0.0f..1.0f, 0.01f, 0.3f),

    // Posterize Effect
    POSTERIZE_LEVELS(EffectType.Posterize, "levels", R.string.cesdk_filter_posterize_levels, 1.0f..15.0f, 1.0f, 3.0f),

    // TV Glitch Effect
    TV_GLITCH_DISTORTION(EffectType.TvGlitch, "distortion", R.string.cesdk_filter_tv_glitch_distortion, 0.0f..10.0f, 0.1f, 3.0f),
    TV_GLITCH_DISTORTION2(EffectType.TvGlitch, "distortion2", R.string.cesdk_filter_tv_glitch_distortion2, 0.0f..5.0f, 0.05f, 1.0f),
    TV_GLITCH_SPEED(EffectType.TvGlitch, "speed", R.string.cesdk_filter_tv_glitch_speed, 0.0f..5.0f, 0.05f, 2.0f),
    TV_GLITCH_ROLLSPEED(EffectType.TvGlitch, "rollSpeed", R.string.cesdk_filter_tv_glitch_rollspeed, 0.0f..3.0f, 0.1f, 1.0f),

    // Half Tone Effect
    HALF_TONE_ANGLE(EffectType.HalfTone, "angle", R.string.cesdk_filter_half_tone_angle, 0.0f..1.0f, 0.01f, 0.0f),
    HALF_TONE_SCALE(EffectType.HalfTone, "scale", R.string.cesdk_filter_half_tone_scale, 0.0f..1.0f, 0.01f, 0.5f),

    // Linocut Effect
    LINOCUT_SCALE(EffectType.Linocut, "scale", R.string.cesdk_filter_linocut_scale, 0.0f..1.0f, 0.01f, 0.5f),

    // Shifter Effect
    SHIFTER_AMOUNT(EffectType.Shifter, "amount", R.string.cesdk_filter_shifter_amount, 0.0f..1.0f, 0.01f, 0.05f),
    SHIFTER_ANGLE(EffectType.Shifter, "angle", R.string.cesdk_filter_shifter_angle, 0.0f..6.3f, 0.1f, 0.3f),

    // Mirror Effect
    MIRROR_SIDE(EffectType.Mirror, "side", R.string.cesdk_filter_mirror_side, 0.0f..3.0f, 1.0f, 1.0f, propertyType = AdjustmentsValueType.INT),

    // Glow Effect
    GLOW_SIZE(EffectType.Glow, "size", R.string.cesdk_filter_glow_size, 0.0f..10.0f, 0.1f, 4.0f),
    GLOW_AMOUNT(EffectType.Glow, "amount", R.string.cesdk_filter_glow_amount, 0.0f..1.0f, 0.01f, 0.5f),
    GLOW_DARKNESS(EffectType.Glow, "darkness", R.string.cesdk_filter_glow_darkness, 0.0f..1.0f, 0.01f, 0.3f),

    // Vignette Effect
    VIGNETTE_OFFSET(EffectType.Vignette, "offset", R.string.cesdk_filter_vignette_offset, 0.0f..5.0f, 0.05f, 1.0f),
    VIGNETTE_DARKNESS(EffectType.Vignette, "darkness", R.string.cesdk_filter_vignette_darkness, 0.0f..1.0f, 0.01f, 1.0f),

    // Tilt Shift Effect
    TILT_SHIFT_AMOUNT(EffectType.TiltShift, "amount", R.string.cesdk_filter_tilt_shift_amount, 0.0f..0.02f, 0.001f, 0.016f),
    TILT_SHIFT_POSITION(EffectType.TiltShift, "position", R.string.cesdk_filter_tilt_shift_position, 0.0f..1.0f, 0.01f, 0.4f),

    // Extrude Blur Effect
    EXTRUDE_BLUR_AMOUNT(EffectType.ExtrudeBlur, "amount", R.string.cesdk_filter_extrude_blur_amount, 0.0f..1.0f, 0.01f, 0.2f),

    // Uniform Blur Effect
    BLUR_UNIFORM_INTENSITY(BlurType.Uniform, "intensity", R.string.cesdk_filter_blur_uniform_intensity, 0.0f..1.0f, 0.01f, 0.5f),

    // Linear Blur
    BLUR_LINEAR_BLURRADIUS(BlurType.Linear, "blurRadius", R.string.cesdk_filter_blur_linear_blurradius, 0.0f..100.0f, 0.5f, 30.0f),
    BLUR_LINEAR_X1(BlurType.Linear, "x1", R.string.cesdk_filter_blur_linear_x1, 0.0f..1.0f, 0.01f, 0.0f),
    BLUR_LINEAR_Y1(BlurType.Linear, "y1", R.string.cesdk_filter_blur_linear_y1, 0.0f..1.0f, 0.01f, 0.5f),
    BLUR_LINEAR_X2(BlurType.Linear, "x2", R.string.cesdk_filter_blur_linear_x2, 0.0f..1.0f, 0.01f, 1.0f),
    BLUR_LINEAR_Y2(BlurType.Linear, "y2", R.string.cesdk_filter_blur_linear_y2, 0.0f..1.0f, 0.01f, 0.5f),

    // Mirrored Blur
    BLUR_MIRRORED_BLURRADIUS(BlurType.Mirrored, "blurRadius", R.string.cesdk_filter_blur_mirrored_blurradius, 0.0f..100.0f, 1.0f, 30.0f),
    BLUR_MIRRORED_GRADIENTSIZE(BlurType.Mirrored, "gradientSize", R.string.cesdk_filter_blur_mirrored_gradientsize, 0.0f..1000.0f, 1.0f, 50.0f),
    BLUR_MIRRORED_SIZE(BlurType.Mirrored, "size", R.string.cesdk_filter_blur_mirrored_size, 0.0f..1000.0f, 1.0f, 75.0f),
    BLUR_MIRRORED_X1(BlurType.Mirrored, "x1", R.string.cesdk_filter_blur_mirrored_x1, 0.0f..1.0f, 0.01f, 0.0f),
    BLUR_MIRRORED_Y1(BlurType.Mirrored, "y1", R.string.cesdk_filter_blur_mirrored_y1, 0.0f..1.0f, 0.01f, 0.5f),
    BLUR_MIRRORED_X2(BlurType.Mirrored, "x2", R.string.cesdk_filter_blur_mirrored_x2, 0.0f..1.0f, 0.01f, 1.0f),
    BLUR_MIRRORED_Y2(BlurType.Mirrored, "y2", R.string.cesdk_filter_blur_mirrored_y2, 0.0f..1.0f, 0.01f, 0.5f),

    // Radial Blur
    BLUR_RADIAL_BLURRADIUS(BlurType.Radial, "blurRadius", R.string.cesdk_filter_blur_radial_blurradius, 0.0f..100.0f, 1.0f, 30.0f),
    BLUR_RADIAL_GRADIENTRADIUS(BlurType.Radial, "gradientRadius", R.string.cesdk_filter_blur_radial_gradientradius, 0.0f..1000.0f, 1.0f, 50.0f),
    BLUR_RADIAL_RADIUS(BlurType.Radial, "radius", R.string.cesdk_filter_blur_radial_radius, 0.0f..1000f, 1.0f, 75.0f),
    BLUR_RADIAL_X(BlurType.Radial, "x", R.string.cesdk_filter_blur_radial_x, 0.0f..1.0f, 0.01f, 0.5f),
    BLUR_RADIAL_Y(BlurType.Radial, "y", R.string.cesdk_filter_blur_radial_y, 0.0f..1.0f, 0.01f, 0.5f);

    val propertyPath: String = "${type.key}/$relativePath"

    companion object {
        fun getEffectAdjustments(engine: Engine, effect: DesignBlock?) : List<AdjustmentState> {
            effect ?: return listOf()
            val effectType = engine.block.getBlurOrEffectType(effect) ?: return listOf()

            return EffectAndBlurOptions.values().filter {
                it.type == effectType
            }.map {
                val currentValue = when (it.propertyType) {
                    AdjustmentsValueType.INT ->
                        engine.block.getInt(effect, it.propertyPath).toFloat()

                    AdjustmentsValueType.FLOAT ->
                        engine.block.getFloat(effect, it.propertyPath)
                }
                AdjustmentState(
                    type = it,
                    value = currentValue
                )
            }
        }

        fun getEffectAdjustments(engine: Engine, designBlock: DesignBlock, effectType: ObjectType?) : List<AdjustmentState> {
            val effect = if (effectType is BlurType) {
                if (engine.block.isBlurEnabled(designBlock)) {
                    engine.block.getBlur(designBlock).takeIf { it != NoneDesignBlock }
                } else null
            } else if (effectType is EffectType) {
                engine.block.findEffect(designBlock, effectType)
            } else null

            return EffectAndBlurOptions.values().filter {
                it.type == effectType
            }.map {
                val currentValue = when (it.propertyType) {
                    AdjustmentsValueType.INT ->
                        if (effect != null) {
                            engine.block.getInt(effect, it.propertyPath).toFloat()
                        } else {
                            it.defaultValue
                        }

                    AdjustmentsValueType.FLOAT ->
                        if (effect != null) {
                            engine.block.getFloat(effect, it.propertyPath)
                        } else {
                            it.defaultValue
                        }
                }
                AdjustmentState(
                    type = it,
                    value = currentValue
                )
            }
        }
    }
}