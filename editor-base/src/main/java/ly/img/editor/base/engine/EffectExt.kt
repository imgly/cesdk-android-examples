package ly.img.editor.base.engine

import androidx.compose.ui.graphics.Color
import ly.img.editor.base.R
import ly.img.engine.BlockApi
import ly.img.engine.BlurType
import ly.img.engine.DesignBlock
import ly.img.engine.EffectType
import ly.img.engine.Engine
import ly.img.engine.ObjectType
import ly.img.engine.RGBAColor

/*
 * Only one effect of the same group can be applied at a time.
 * So if you have two effects of the same group, we need to remove the first one before applying the second one.
 * This Group helps to identify the group of an effect, in the effect list.
 */
enum class EffectGroup {
    Filter,
    FxEffect,
    Adjustments,
}

fun EffectType.getGroup() =
    when (this) {
        EffectType.LutFilter,
        EffectType.DuoToneFilter,
        ->
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
        EffectType.Vignette,
        EffectType.Recolor,
        EffectType.GreenScreen,
        ->
            EffectGroup.FxEffect

        EffectType.Adjustments ->
            EffectGroup.Adjustments
    }

/**
 * State of an effect or blur adjustment, including the type and the current value of this adjustment.
 */
data class AdjustmentState(
    val type: EffectAndBlurOptions,
    val value: Value,
) {
    sealed interface Value {
        data class Int(val value: kotlin.Int) : Value

        data class Float(val value: kotlin.Float) : Value

        data class Color(val value: androidx.compose.ui.graphics.Color) : Value
    }
}

/**
 * Returns the type of the blur or effect applied to the given [DesignBlock], or null if no blur or effect is applied.
 */
fun BlockApi.getBlurOrEffectType(effect: DesignBlock?): ObjectType? {
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
    val valueOptions: ValueOptions,
) {
    // ADJUSTMENT_CONTRAST
    ADJUSTMENT_BRIGHTNESS(
        type = EffectType.Adjustments,
        relativePath = "brightness",
        nameRes = R.string.ly_img_editor_adjustment_brightness,
        valueOptions =
            ValueOptions.Float(
                range = -1F..1F,
                defaultValue = 0F,
            ),
    ),
    ADJUSTMENT_SATURATION(
        type = EffectType.Adjustments,
        relativePath = "saturation",
        nameRes = R.string.ly_img_editor_adjustment_saturation,
        valueOptions =
            ValueOptions.Float(
                range = -1F..1F,
                defaultValue = 0F,
            ),
    ),
    ADJUSTMENT_CONTRAST(
        type = EffectType.Adjustments,
        relativePath = "contrast",
        nameRes = R.string.ly_img_editor_adjustment_contrast,
        valueOptions =
            ValueOptions.Float(
                range = -1F..1F,
                defaultValue = 0F,
            ),
    ),
    ADJUSTMENT_GAMMA(
        type = EffectType.Adjustments,
        relativePath = "gamma",
        nameRes = R.string.ly_img_editor_adjustment_gamma,
        valueOptions =
            ValueOptions.Float(
                range = -1F..1F,
                defaultValue = 0F,
            ),
    ),

    ADJUSTMENT_CLARITY(
        type = EffectType.Adjustments,
        relativePath = "clarity",
        nameRes = R.string.ly_img_editor_adjustment_clarity,
        valueOptions =
            ValueOptions.Float(
                range = -1F..1F,
                defaultValue = 0F,
            ),
    ),
    ADJUSTMENT_EXPOSURE(
        type = EffectType.Adjustments,
        relativePath = "exposure",
        nameRes = R.string.ly_img_editor_adjustment_exposure,
        valueOptions =
            ValueOptions.Float(
                range = -1F..1F,
                defaultValue = 0F,
            ),
    ),
    ADJUSTMENT_SHADOWS(
        type = EffectType.Adjustments,
        relativePath = "shadows",
        nameRes = R.string.ly_img_editor_adjustment_shadows,
        valueOptions =
            ValueOptions.Float(
                range = -1F..1F,
                defaultValue = 0F,
            ),
    ),
    ADJUSTMENT_HIGHLIGHTS(
        type = EffectType.Adjustments,
        relativePath = "highlights",
        nameRes = R.string.ly_img_editor_adjustment_highlights,
        valueOptions =
            ValueOptions.Float(
                range = -1F..1F,
                defaultValue = 0F,
            ),
    ),
    ADJUSTMENT_BLACKS(
        type = EffectType.Adjustments,
        relativePath = "blacks",
        nameRes = R.string.ly_img_editor_adjustment_blacks,
        valueOptions =
            ValueOptions.Float(
                range = -1F..1F,
                defaultValue = 0F,
            ),
    ),
    ADJUSTMENT_WHITES(
        type = EffectType.Adjustments,
        relativePath = "whites",
        nameRes = R.string.ly_img_editor_adjustment_whites,
        valueOptions =
            ValueOptions.Float(
                range = -1F..1F,
                defaultValue = 0F,
            ),
    ),
    ADJUSTMENT_TEMPERATURE(
        type = EffectType.Adjustments,
        relativePath = "temperature",
        nameRes = R.string.ly_img_editor_adjustment_temperature,
        valueOptions =
            ValueOptions.Float(
                range = -1F..1F,
                defaultValue = 0F,
            ),
    ),
    ADJUSTMENT_SHARPNESS(
        type = EffectType.Adjustments,
        relativePath = "sharpness",
        nameRes = R.string.ly_img_editor_adjustment_sharpness,
        valueOptions =
            ValueOptions.Float(
                range = -1F..1F,
                defaultValue = 0F,
            ),
    ),

    // Color Filter
    LUT_INTENSITY(
        type = EffectType.LutFilter,
        relativePath = "intensity",
        nameRes = R.string.ly_img_editor_filter_intensity,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                defaultValue = 1F,
            ),
    ),

    // DouTone Filter
    DUOTONE_INTENSITY(
        type = EffectType.DuoToneFilter,
        relativePath = "intensity",
        nameRes = R.string.ly_img_editor_filter_intensity,
        valueOptions =
            ValueOptions.Float(
                range = -1F..1F,
                defaultValue = 0F,
            ),
    ),

    // Pixelize Effect
    PIXELIZE_HORIZONTALPIXELSIZE(
        type = EffectType.Pixelize,
        relativePath = "horizontalPixelSize",
        nameRes = R.string.ly_img_editor_filter_pixelize_horizontalpixelsize,
        valueOptions =
            ValueOptions.Int(
                range = 5..50,
                step = 1,
                defaultValue = 20,
            ),
    ),
    PIXELIZE_VERTICALPIXELSIZE(
        type = EffectType.Pixelize,
        relativePath = "verticalPixelSize",
        nameRes = R.string.ly_img_editor_filter_pixelize_verticalpixelsize,
        valueOptions =
            ValueOptions.Int(
                range = 5..50,
                step = 1,
                defaultValue = 20,
            ),
    ),

    // Radial Pixel Effect
    RADIAL_PIXEL_RADIUS(
        type = EffectType.RadialPixel,
        relativePath = "radius",
        nameRes = R.string.ly_img_editor_filter_radial_pixel_radius,
        valueOptions =
            ValueOptions.Float(
                range = 0.05F..1F,
                step = 0.01F,
                defaultValue = 0.1F,
            ),
    ),
    RADIAL_PIXEL_SEGMENTS(
        type = EffectType.RadialPixel,
        relativePath = "segments",
        nameRes = R.string.ly_img_editor_filter_radial_pixel_segments,
        valueOptions =
            ValueOptions.Float(
                range = 0.01F..1F,
                step = 0.01F,
                defaultValue = 0.01F,
            ),
    ),

    // Cross Cut Effect
    CROSS_CUT_SLICES(
        type = EffectType.CrossCut,
        relativePath = "slices",
        nameRes = R.string.ly_img_editor_filter_cross_cut_slices,
        valueOptions =
            ValueOptions.Float(
                range = 1F..10F,
                step = 1F,
                defaultValue = 5F,
            ),
    ),
    CROSS_CUT_OFFSET(
        type = EffectType.CrossCut,
        relativePath = "offset",
        nameRes = R.string.ly_img_editor_filter_cross_cut_offset,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 0.07F,
            ),
    ),
    CROSS_CUT_SPEEDV(
        type = EffectType.CrossCut,
        relativePath = "speedV",
        nameRes = R.string.ly_img_editor_filter_cross_cut_speedv,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 0.5F,
            ),
    ),
    CROSS_CUT_TIME(
        type = EffectType.CrossCut,
        relativePath = "time",
        nameRes = R.string.ly_img_editor_filter_cross_cut_time,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 1F,
            ),
    ),

    // Liquid Effect
    LIQUID_AMOUNT(
        type = EffectType.Liquid,
        relativePath = "amount",
        nameRes = R.string.ly_img_editor_filter_liquid_amount,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 0.06F,
            ),
    ),
    LIQUID_SCALE(
        type = EffectType.Liquid,
        relativePath = "scale",
        nameRes = R.string.ly_img_editor_filter_liquid_scale,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 0.62F,
            ),
    ),
    LIQUID_TIME(
        type = EffectType.Liquid,
        relativePath = "time",
        nameRes = R.string.ly_img_editor_filter_liquid_time,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 0.5F,
            ),
    ),

    // Outliner Effect
    OUTLINER_AMOUNT(
        type = EffectType.Outliner,
        relativePath = "amount",
        nameRes = R.string.ly_img_editor_filter_outliner_amount,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 0.5F,
            ),
    ),
    OUTLINER_PASSTHROUGH(
        type = EffectType.Outliner,
        relativePath = "passthrough",
        nameRes = R.string.ly_img_editor_filter_outliner_passthrough,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 0.5F,
            ),
    ),

    // Dot Pattern Effect
    DOT_PATTERN_DOTS(
        type = EffectType.DotPattern,
        relativePath = "dots",
        nameRes = R.string.ly_img_editor_filter_dot_pattern_dots,
        valueOptions =
            ValueOptions.Float(
                range = 1F..80F,
                step = 1F,
                defaultValue = 30F,
            ),
    ),
    DOT_PATTERN_SIZE(
        type = EffectType.DotPattern,
        relativePath = "size",
        nameRes = R.string.ly_img_editor_filter_dot_pattern_size,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 0.5F,
            ),
    ),
    DOT_PATTERN_BLUR(
        type = EffectType.DotPattern,
        relativePath = "blur",
        nameRes = R.string.ly_img_editor_filter_dot_pattern_blur,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 0.3F,
            ),
    ),

    // Posterize Effect
    POSTERIZE_LEVELS(
        type = EffectType.Posterize,
        relativePath = "levels",
        nameRes = R.string.ly_img_editor_filter_posterize_levels,
        valueOptions =
            ValueOptions.Float(
                range = 1F..15F,
                step = 1F,
                defaultValue = 3F,
            ),
    ),

    // TV Glitch Effect
    TV_GLITCH_DISTORTION(
        type = EffectType.TvGlitch,
        relativePath = "distortion",
        nameRes = R.string.ly_img_editor_filter_tv_glitch_distortion,
        valueOptions =
            ValueOptions.Float(
                range = 0F..10F,
                step = 0.1F,
                defaultValue = 3F,
            ),
    ),
    TV_GLITCH_DISTORTION2(
        type = EffectType.TvGlitch,
        relativePath = "distortion2",
        nameRes = R.string.ly_img_editor_filter_tv_glitch_distortion2,
        valueOptions =
            ValueOptions.Float(
                range = 0F..5F,
                step = 0.05F,
                defaultValue = 1F,
            ),
    ),
    TV_GLITCH_SPEED(
        type = EffectType.TvGlitch,
        relativePath = "speed",
        nameRes = R.string.ly_img_editor_filter_tv_glitch_speed,
        valueOptions =
            ValueOptions.Float(
                range = 0F..5F,
                step = 0.05F,
                defaultValue = 2F,
            ),
    ),
    TV_GLITCH_ROLLSPEED(
        type = EffectType.TvGlitch,
        relativePath = "rollSpeed",
        nameRes = R.string.ly_img_editor_filter_tv_glitch_rollspeed,
        valueOptions =
            ValueOptions.Float(
                range = 0F..3F,
                step = 0.1F,
                defaultValue = 1F,
            ),
    ),

    // Half Tone Effect
    HALF_TONE_ANGLE(
        type = EffectType.HalfTone,
        relativePath = "angle",
        nameRes = R.string.ly_img_editor_filter_half_tone_angle,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 0F,
            ),
    ),
    HALF_TONE_SCALE(
        type = EffectType.HalfTone,
        relativePath = "scale",
        nameRes = R.string.ly_img_editor_filter_half_tone_scale,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 0.5F,
            ),
    ),

    // Linocut Effect
    LINOCUT_SCALE(
        type = EffectType.Linocut,
        relativePath = "scale",
        nameRes = R.string.ly_img_editor_filter_linocut_scale,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 0.5F,
            ),
    ),

    // Shifter Effect
    SHIFTER_AMOUNT(
        type = EffectType.Shifter,
        relativePath = "amount",
        nameRes = R.string.ly_img_editor_filter_shifter_amount,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 0.05F,
            ),
    ),
    SHIFTER_ANGLE(
        type = EffectType.Shifter,
        relativePath = "angle",
        nameRes = R.string.ly_img_editor_filter_shifter_angle,
        valueOptions =
            ValueOptions.Float(
                range = 0F..6.3F,
                step = 0.1F,
                defaultValue = 0.3F,
            ),
    ),

    // Mirror Effect
    MIRROR_SIDE(
        type = EffectType.Mirror,
        relativePath = "side",
        nameRes = R.string.ly_img_editor_filter_mirror_side,
        valueOptions =
            ValueOptions.Int(
                range = 0..3,
                step = 1,
                defaultValue = 1,
            ),
    ),

    // Glow Effect
    GLOW_SIZE(
        type = EffectType.Glow,
        relativePath = "size",
        nameRes = R.string.ly_img_editor_filter_glow_size,
        valueOptions =
            ValueOptions.Float(
                range = 0F..10F,
                step = 0.1F,
                defaultValue = 4F,
            ),
    ),
    GLOW_AMOUNT(
        type = EffectType.Glow,
        relativePath = "amount",
        nameRes = R.string.ly_img_editor_filter_glow_amount,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 0.5F,
            ),
    ),
    GLOW_DARKNESS(
        type = EffectType.Glow,
        relativePath = "darkness",
        nameRes = R.string.ly_img_editor_filter_glow_darkness,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 0.3F,
            ),
    ),

    // Vignette Effect
    VIGNETTE_OFFSET(
        type = EffectType.Vignette,
        relativePath = "offset",
        nameRes = R.string.ly_img_editor_filter_vignette_offset,
        valueOptions =
            ValueOptions.Float(
                range = 0F..5F,
                step = 0.05F,
                defaultValue = 1F,
            ),
    ),
    VIGNETTE_DARKNESS(
        type = EffectType.Vignette,
        relativePath = "darkness",
        nameRes = R.string.ly_img_editor_filter_vignette_darkness,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 1F,
            ),
    ),

    // Tilt Shift Effect
    TILT_SHIFT_AMOUNT(
        type = EffectType.TiltShift,
        relativePath = "amount",
        nameRes = R.string.ly_img_editor_filter_tilt_shift_amount,
        valueOptions =
            ValueOptions.Float(
                range = 0F..0.02F,
                step = 0.001F,
                defaultValue = 0.016F,
            ),
    ),
    TILT_SHIFT_POSITION(
        type = EffectType.TiltShift,
        relativePath = "position",
        nameRes = R.string.ly_img_editor_filter_tilt_shift_position,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 0.4F,
            ),
    ),

    // Recolor Effect
    RECOLOR_FROM_COLOR(
        type = EffectType.Recolor,
        relativePath = "fromColor",
        nameRes = R.string.ly_img_editor_filter_recolor_from_color,
        valueOptions =
            ValueOptions.Color(
                defaultValue = Color.Black,
            ),
    ),
    RECOLOR_TO_COLOR(
        type = EffectType.Recolor,
        relativePath = "toColor",
        nameRes = R.string.ly_img_editor_filter_recolor_to_color,
        valueOptions =
            ValueOptions.Color(
                defaultValue = Color.Black,
            ),
    ),
    RECOLOR_COLOR_MATCH(
        type = EffectType.Recolor,
        relativePath = "colorMatch",
        nameRes = R.string.ly_img_editor_filter_recolor_color_match,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 0.4F,
            ),
    ),
    RECOLOR_BRIGHTNESS_MATCH(
        type = EffectType.Recolor,
        relativePath = "brightnessMatch",
        nameRes = R.string.ly_img_editor_filter_recolor_brightness_match,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 1F,
            ),
    ),
    RECOLOR_SMOOTHNESS(
        type = EffectType.Recolor,
        relativePath = "smoothness",
        nameRes = R.string.ly_img_editor_filter_recolor_smoothness,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 0.08F,
            ),
    ),

    // Green Screen Effect
    GREEN_SCREEN_FROM_COLOR(
        type = EffectType.GreenScreen,
        relativePath = "fromColor",
        nameRes = R.string.ly_img_editor_filter_green_screen_from_color,
        valueOptions =
            ValueOptions.Color(
                defaultValue = Color.Black,
            ),
    ),
    GREEN_SCREEN_COLOR_MATCH(
        type = EffectType.GreenScreen,
        relativePath = "colorMatch",
        nameRes = R.string.ly_img_editor_filter_green_screen_color_match,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 0.4F,
            ),
    ),
    GREEN_SCREEN_SMOOTHNESS(
        type = EffectType.GreenScreen,
        relativePath = "smoothness",
        nameRes = R.string.ly_img_editor_filter_green_screen_smoothness,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 0.08F,
            ),
    ),
    GREEN_SCREEN_SPILL(
        type = EffectType.GreenScreen,
        relativePath = "spill",
        nameRes = R.string.ly_img_editor_filter_green_screen_spill,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 0F,
            ),
    ),

    // Extrude Blur Effect
    EXTRUDE_BLUR_AMOUNT(
        type = EffectType.ExtrudeBlur,
        relativePath = "amount",
        nameRes = R.string.ly_img_editor_filter_extrude_blur_amount,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 0.2F,
            ),
    ),

    // Uniform Blur Effect
    BLUR_UNIFORM_INTENSITY(
        type = BlurType.Uniform,
        relativePath = "intensity",
        nameRes = R.string.ly_img_editor_filter_blur_uniform_intensity,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 0.5F,
            ),
    ),

    // Linear Blur
    BLUR_LINEAR_BLURRADIUS(
        type = BlurType.Linear,
        relativePath = "blurRadius",
        nameRes = R.string.ly_img_editor_filter_blur_linear_blurradius,
        valueOptions =
            ValueOptions.Float(
                range = 0F..100F,
                step = 0.5F,
                defaultValue = 30F,
            ),
    ),
    BLUR_LINEAR_X1(
        type = BlurType.Linear,
        relativePath = "x1",
        nameRes = R.string.ly_img_editor_filter_blur_linear_x1,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 0F,
            ),
    ),
    BLUR_LINEAR_Y1(
        type = BlurType.Linear,
        relativePath = "y1",
        nameRes = R.string.ly_img_editor_filter_blur_linear_y1,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 0.5F,
            ),
    ),
    BLUR_LINEAR_X2(
        type = BlurType.Linear,
        relativePath = "x2",
        nameRes = R.string.ly_img_editor_filter_blur_linear_x2,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 1F,
            ),
    ),
    BLUR_LINEAR_Y2(
        type = BlurType.Linear,
        relativePath = "y2",
        nameRes = R.string.ly_img_editor_filter_blur_linear_y2,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 0.5F,
            ),
    ),

    // Mirrored Blur
    BLUR_MIRRORED_BLURRADIUS(
        type = BlurType.Mirrored,
        relativePath = "blurRadius",
        nameRes = R.string.ly_img_editor_filter_blur_mirrored_blurradius,
        valueOptions =
            ValueOptions.Float(
                range = 0F..100F,
                step = 1F,
                defaultValue = 30F,
            ),
    ),
    BLUR_MIRRORED_GRADIENTSIZE(
        type = BlurType.Mirrored,
        relativePath = "gradientSize",
        nameRes = R.string.ly_img_editor_filter_blur_mirrored_gradientsize,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1000F,
                step = 1F,
                defaultValue = 50F,
            ),
    ),
    BLUR_MIRRORED_SIZE(
        type = BlurType.Mirrored,
        relativePath = "size",
        nameRes = R.string.ly_img_editor_filter_blur_mirrored_size,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1000F,
                step = 1F,
                defaultValue = 75F,
            ),
    ),
    BLUR_MIRRORED_X1(
        type = BlurType.Mirrored,
        relativePath = "x1",
        nameRes = R.string.ly_img_editor_filter_blur_mirrored_x1,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 0F,
            ),
    ),
    BLUR_MIRRORED_Y1(
        type = BlurType.Mirrored,
        relativePath = "y1",
        nameRes = R.string.ly_img_editor_filter_blur_mirrored_y1,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 0.5F,
            ),
    ),
    BLUR_MIRRORED_X2(
        type = BlurType.Mirrored,
        relativePath = "x2",
        nameRes = R.string.ly_img_editor_filter_blur_mirrored_x2,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 1F,
            ),
    ),
    BLUR_MIRRORED_Y2(
        type = BlurType.Mirrored,
        relativePath = "y2",
        nameRes = R.string.ly_img_editor_filter_blur_mirrored_y2,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 0.5F,
            ),
    ),

    // Radial Blur
    BLUR_RADIAL_BLURRADIUS(
        type = BlurType.Radial,
        relativePath = "blurRadius",
        nameRes = R.string.ly_img_editor_filter_blur_radial_blurradius,
        valueOptions =
            ValueOptions.Float(
                range = 0F..100F,
                step = 1F,
                defaultValue = 30F,
            ),
    ),
    BLUR_RADIAL_GRADIENTRADIUS(
        type = BlurType.Radial,
        relativePath = "gradientRadius",
        nameRes = R.string.ly_img_editor_filter_blur_radial_gradientradius,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1000F,
                step = 1F,
                defaultValue = 50F,
            ),
    ),
    BLUR_RADIAL_RADIUS(
        type = BlurType.Radial,
        relativePath = "radius",
        nameRes = R.string.ly_img_editor_filter_blur_radial_radius,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1000F,
                step = 1F,
                defaultValue = 75F,
            ),
    ),
    BLUR_RADIAL_X(
        type = BlurType.Radial,
        relativePath = "x",
        nameRes = R.string.ly_img_editor_filter_blur_radial_x,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 0.5F,
            ),
    ),
    BLUR_RADIAL_Y(
        type = BlurType.Radial,
        relativePath = "y",
        nameRes = R.string.ly_img_editor_filter_blur_radial_y,
        valueOptions =
            ValueOptions.Float(
                range = 0F..1F,
                step = 0.01F,
                defaultValue = 0.5F,
            ),
    ),
    ;

    sealed interface ValueOptions {
        data class Int(
            val range: IntRange,
            val step: kotlin.Int = 1,
            val defaultValue: kotlin.Int,
        ) : ValueOptions

        data class Float(
            val range: ClosedFloatingPointRange<kotlin.Float>,
            val step: kotlin.Float = 0F,
            val defaultValue: kotlin.Float,
        ) : ValueOptions

        data class Color(
            val defaultValue: androidx.compose.ui.graphics.Color,
        ) : ValueOptions
    }

    val propertyPath: String = "${type.key}/$relativePath"

    companion object {
        fun getEffectAdjustments(
            engine: Engine,
            effect: DesignBlock?,
        ): List<AdjustmentState> {
            effect ?: return listOf()
            val effectType = engine.block.getBlurOrEffectType(effect) ?: return listOf()

            return EffectAndBlurOptions.values().filter {
                it.type == effectType
            }.map { options ->
                val currentValue =
                    when (options.valueOptions) {
                        is ValueOptions.Int ->
                            engine.block.getInt(effect, options.propertyPath).let {
                                AdjustmentState.Value.Int(it)
                            }

                        is ValueOptions.Float ->
                            engine.block.getFloat(effect, options.propertyPath).let {
                                AdjustmentState.Value.Float(it)
                            }

                        is ValueOptions.Color ->
                            engine.block.getColor(effect, options.propertyPath).let {
                                AdjustmentState.Value.Color((it as RGBAColor).toComposeColor())
                            }
                    }
                AdjustmentState(
                    type = options,
                    value = currentValue,
                )
            }
        }

        fun getEffectAdjustments(
            engine: Engine,
            designBlock: DesignBlock,
            effectType: ObjectType?,
        ): List<AdjustmentState> {
            val effect =
                if (effectType is BlurType) {
                    if (engine.block.isBlurEnabled(designBlock)) {
                        engine.block.getBlur(designBlock).takeIf { it != NoneDesignBlock }
                    } else {
                        null
                    }
                } else if (effectType is EffectType) {
                    engine.block.findEffect(designBlock, effectType)
                } else {
                    null
                }

            return EffectAndBlurOptions.values().filter {
                it.type == effectType
            }.map { options ->
                val currentValue =
                    when (options.valueOptions) {
                        is ValueOptions.Int ->
                            if (effect != null) {
                                engine.block.getInt(effect, options.propertyPath)
                            } else {
                                options.valueOptions.defaultValue
                            }.let {
                                AdjustmentState.Value.Int(it)
                            }

                        is ValueOptions.Float ->
                            if (effect != null) {
                                engine.block.getFloat(effect, options.propertyPath)
                            } else {
                                options.valueOptions.defaultValue
                            }.let {
                                AdjustmentState.Value.Float(it)
                            }
                        is ValueOptions.Color ->
                            if (effect != null) {
                                (engine.block.getColor(effect, options.propertyPath) as RGBAColor).toComposeColor()
                            } else {
                                options.valueOptions.defaultValue
                            }.let {
                                AdjustmentState.Value.Color(it)
                            }
                    }
                AdjustmentState(
                    type = options,
                    value = currentValue,
                )
            }
        }
    }
}
