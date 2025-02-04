package ly.img.cesdk.engine

import androidx.compose.ui.graphics.Color as ComposeColor
import ly.img.engine.Color as EngineColor

fun EngineColor.toComposeColor(): ComposeColor {
    return ComposeColor(
        red = this.r,
        green = this.g,
        blue = this.b,
        alpha = this.a
    )
}

fun ComposeColor.toEngineColor(): EngineColor {
    return EngineColor.fromRGBA(
        r = this.red,
        g = this.green,
        b = this.blue,
        a = this.alpha
    )
}