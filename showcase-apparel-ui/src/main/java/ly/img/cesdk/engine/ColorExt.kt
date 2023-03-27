package ly.img.cesdk.engine

import ly.img.engine.Color
import ly.img.engine.RGBAColor
import androidx.compose.ui.graphics.Color as ComposeColor

fun RGBAColor.toComposeColor(): ComposeColor {
    return ComposeColor(
        red = this.r,
        green = this.g,
        blue = this.b,
        alpha = this.a
    )
}

fun ComposeColor.toEngineColor(): RGBAColor {
    return Color.fromRGBA(
        r = this.red,
        g = this.green,
        b = this.blue,
        a = this.alpha
    )
}
