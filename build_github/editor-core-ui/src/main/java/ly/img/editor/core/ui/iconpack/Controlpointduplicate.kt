package ly.img.editor.core.ui.iconpack

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

val IconPack.Controlpointduplicate: ImageVector
    get() {
        if (_controlpointduplicate != null) {
            return _controlpointduplicate!!
        }
        _controlpointduplicate =
            Builder(
                name = "Controlpointduplicate",
                defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp,
                viewportWidth = 24.0f,
                viewportHeight = 24.0f,
            ).apply {
                path(
                    fill = SolidColor(Color(0xFF49454F)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(14.0f, 16.0f)
                    horizontalLineTo(16.0f)
                    verticalLineTo(13.0f)
                    horizontalLineTo(19.0f)
                    verticalLineTo(11.0f)
                    horizontalLineTo(16.0f)
                    verticalLineTo(8.0f)
                    horizontalLineTo(14.0f)
                    verticalLineTo(11.0f)
                    horizontalLineTo(11.0f)
                    verticalLineTo(13.0f)
                    horizontalLineTo(14.0f)
                    verticalLineTo(16.0f)
                    close()
                    moveTo(6.0f, 20.5f)
                    curveTo(4.1833f, 19.8667f, 2.7293f, 18.7707f, 1.638f, 17.212f)
                    curveTo(0.546f, 15.654f, 0.0f, 13.9167f, 0.0f, 12.0f)
                    curveTo(0.0f, 10.0833f, 0.546f, 8.3457f, 1.638f, 6.787f)
                    curveTo(2.7293f, 5.229f, 4.1833f, 4.1333f, 6.0f, 3.5f)
                    verticalLineTo(5.7f)
                    curveTo(4.7667f, 6.2833f, 3.7917f, 7.1417f, 3.075f, 8.275f)
                    curveTo(2.3583f, 9.4083f, 2.0f, 10.65f, 2.0f, 12.0f)
                    curveTo(2.0f, 13.35f, 2.3583f, 14.5917f, 3.075f, 15.725f)
                    curveTo(3.7917f, 16.8583f, 4.7667f, 17.7167f, 6.0f, 18.3f)
                    verticalLineTo(20.5f)
                    close()
                    moveTo(15.0f, 21.0f)
                    curveTo(13.75f, 21.0f, 12.5793f, 20.7627f, 11.488f, 20.288f)
                    curveTo(10.396f, 19.8127f, 9.446f, 19.1707f, 8.638f, 18.362f)
                    curveTo(7.8293f, 17.554f, 7.1877f, 16.604f, 6.713f, 15.512f)
                    curveTo(6.2377f, 14.4207f, 6.0f, 13.25f, 6.0f, 12.0f)
                    curveTo(6.0f, 10.75f, 6.2377f, 9.579f, 6.713f, 8.487f)
                    curveTo(7.1877f, 7.3957f, 7.8293f, 6.4457f, 8.638f, 5.637f)
                    curveTo(9.446f, 4.829f, 10.396f, 4.1873f, 11.488f, 3.712f)
                    curveTo(12.5793f, 3.2373f, 13.75f, 3.0f, 15.0f, 3.0f)
                    curveTo(16.25f, 3.0f, 17.421f, 3.2373f, 18.513f, 3.712f)
                    curveTo(19.6043f, 4.1873f, 20.5543f, 4.829f, 21.363f, 5.637f)
                    curveTo(22.171f, 6.4457f, 22.8127f, 7.3957f, 23.288f, 8.487f)
                    curveTo(23.7627f, 9.579f, 24.0f, 10.75f, 24.0f, 12.0f)
                    curveTo(24.0f, 13.25f, 23.7627f, 14.4207f, 23.288f, 15.512f)
                    curveTo(22.8127f, 16.604f, 22.171f, 17.554f, 21.363f, 18.362f)
                    curveTo(20.5543f, 19.1707f, 19.6043f, 19.8127f, 18.513f, 20.288f)
                    curveTo(17.421f, 20.7627f, 16.25f, 21.0f, 15.0f, 21.0f)
                    close()
                }
            }.build()
        return _controlpointduplicate!!
    }

private var _controlpointduplicate: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Controlpointduplicate.IconPreview()
