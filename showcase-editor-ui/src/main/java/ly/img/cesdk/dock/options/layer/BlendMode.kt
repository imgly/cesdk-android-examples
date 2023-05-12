package ly.img.cesdk.dock.options.layer

import ly.img.cesdk.editorui.R
import ly.img.engine.BlendMode

private val blendModes = linkedMapOf(
    BlendMode.PASS_THROUGH to R.string.cesdk_blendmode_pass_through,
    BlendMode.NORMAL to R.string.cesdk_blendmode_normal,
    BlendMode.DARKEN to R.string.cesdk_blendmode_darken,
    BlendMode.MULTIPLY to R.string.cesdk_blendmode_multiply,
    BlendMode.COLOR_BURN to R.string.cesdk_blendmode_color_burn,
    BlendMode.LIGHTEN to R.string.cesdk_blendmode_lighten,
    BlendMode.SCREEN to R.string.cesdk_blendmode_screen,
    BlendMode.COLOR_DODGE to R.string.cesdk_blendmode_color_dodge,
    BlendMode.OVERLAY to R.string.cesdk_blendmode_overlay,
    BlendMode.SOFT_LIGHT to R.string.cesdk_blendmode_soft_light,
    BlendMode.HARD_LIGHT to R.string.cesdk_blendmode_hard_light,
    BlendMode.DIFFERENCE to R.string.cesdk_blendmode_difference,
    BlendMode.EXCLUSION to R.string.cesdk_blendmode_exclusion,
    BlendMode.HUE to R.string.cesdk_blendmode_hue,
    BlendMode.SATURATION to R.string.cesdk_blendmode_saturation,
    BlendMode.COLOR to R.string.cesdk_blendmode_color,
    BlendMode.LUMINOSITY to R.string.cesdk_blendmode_luminosity
)

val blendModesList = blendModes.toList()

fun getBlendModeStringResource(blendMode: BlendMode) = blendModes[blendMode]