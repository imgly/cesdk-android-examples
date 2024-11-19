package ly.img.editor.core.iconpack

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val IconPack.Effect: ImageVector
    get() {
        if (_effect != null) {
            return _effect!!
        }
        _effect =
            Builder(
                name = "Effect",
                defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp,
                viewportWidth = 24.0f,
                viewportHeight = 24.0f,
            ).apply {
                path(
                    fill = SolidColor(Color(0xFF46464F)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(7.7705f, 18.9999f)
                    horizontalLineTo(5.1147f)
                    lineTo(7.186f, 6.8797f)
                    curveTo(7.2886f, 6.0868f, 7.5278f, 5.3998f, 7.9038f, 4.8187f)
                    curveTo(8.2798f, 4.2308f, 8.7754f, 3.7796f, 9.3906f, 3.4652f)
                    curveTo(10.0059f, 3.1439f, 10.7236f, 2.9901f, 11.5439f, 3.0038f)
                    curveTo(11.8037f, 3.0106f, 12.0601f, 3.0345f, 12.313f, 3.0755f)
                    curveTo(12.5659f, 3.1097f, 12.8154f, 3.1542f, 13.0615f, 3.2088f)
                    lineTo(12.8257f, 5.2904f)
                    curveTo(12.6821f, 5.2562f, 12.5317f, 5.2289f, 12.3745f, 5.2084f)
                    curveTo(12.2241f, 5.1878f, 12.0737f, 5.1742f, 11.9233f, 5.1673f)
                    curveTo(11.561f, 5.1673f, 11.2363f, 5.2357f, 10.9492f, 5.3724f)
                    curveTo(10.6621f, 5.5091f, 10.4229f, 5.704f, 10.2314f, 5.9569f)
                    curveTo(10.0469f, 6.2098f, 9.9238f, 6.5174f, 9.8623f, 6.8797f)
                    lineTo(7.7705f, 18.9999f)
                    close()
                    moveTo(11.8926f, 7.9051f)
                    lineTo(11.5439f, 9.8534f)
                    horizontalLineTo(5.084f)
                    lineTo(5.4224f, 7.9051f)
                    horizontalLineTo(11.8926f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFF46464F)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(13.4302f, 7.9051f)
                    lineTo(14.7837f, 11.412f)
                    lineTo(17.2856f, 7.9051f)
                    horizontalLineTo(20.29f)
                    lineTo(16.1885f, 13.4628f)
                    lineTo(18.5469f, 18.9999f)
                    horizontalLineTo(15.8193f)
                    lineTo(14.3325f, 15.3085f)
                    lineTo(11.7075f, 18.9999f)
                    horizontalLineTo(8.7031f)
                    lineTo(12.9585f, 13.2167f)
                    lineTo(10.7026f, 7.9051f)
                    horizontalLineTo(13.4302f)
                    close()
                }
            }.build()
        return _effect!!
    }

private var _effect: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Effect.IconPreview()
