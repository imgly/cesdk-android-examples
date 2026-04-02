package ly.img.editor.showcases.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val IconPack.CustomFunctionalitiesTextToImage: ImageVector
    get() {
        if (customFunctionalitiesTextToImage != null) {
            return customFunctionalitiesTextToImage!!
        }
        customFunctionalitiesTextToImage = ImageVector.Builder(
            name = "CustomFunctionalitiesTextToImage",
            defaultWidth = 20.dp,
            defaultHeight = 20.dp,
            viewportWidth = 20f,
            viewportHeight = 20f,
        ).apply {
            path(fill = SolidColor(Color(0xFF1B1B1F))) {
                moveTo(8.333f, 5f)
                horizontalLineTo(4.166f)
                curveTo(3.706f, 5f, 3.333f, 5.373f, 3.333f, 5.833f)
                verticalLineTo(15.833f)
                curveTo(3.333f, 16.294f, 3.706f, 16.667f, 4.166f, 16.667f)
                horizontalLineTo(14.166f)
                curveTo(14.626f, 16.667f, 14.999f, 16.294f, 14.999f, 15.833f)
                verticalLineTo(12.211f)
                lineTo(15.765f, 12.567f)
                lineTo(16.365f, 13.859f)
                curveTo(16.438f, 14.016f, 16.542f, 14.151f, 16.666f, 14.263f)
                verticalLineTo(15.833f)
                curveTo(16.666f, 17.214f, 15.547f, 18.333f, 14.166f, 18.333f)
                horizontalLineTo(4.166f)
                curveTo(2.785f, 18.333f, 1.666f, 17.214f, 1.666f, 15.833f)
                verticalLineTo(5.833f)
                curveTo(1.666f, 4.453f, 2.785f, 3.333f, 4.166f, 3.333f)
                horizontalLineTo(8.333f)
                verticalLineTo(5f)
                close()
            }
            path(fill = SolidColor(Color(0xFF1B1B1F))) {
                moveTo(14.166f, 15f)
                horizontalLineTo(4.166f)
                lineTo(6.666f, 11.667f)
                lineTo(8.541f, 14.167f)
                lineTo(11.041f, 10.833f)
                lineTo(14.166f, 15f)
                close()
            }
            path(fill = SolidColor(Color(0xFF1B1B1F))) {
                moveTo(6.666f, 6.875f)
                curveTo(7.471f, 6.875f, 8.124f, 7.528f, 8.124f, 8.333f)
                curveTo(8.124f, 9.139f, 7.471f, 9.792f, 6.666f, 9.792f)
                curveTo(5.861f, 9.792f, 5.208f, 9.139f, 5.208f, 8.333f)
                curveTo(5.208f, 7.528f, 5.861f, 6.875f, 6.666f, 6.875f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF1B1B1F)),
                pathFillType = PathFillType.EvenOdd,
            ) {
                moveTo(16.666f, 0.833f)
                curveTo(17.587f, 0.833f, 18.333f, 1.58f, 18.333f, 2.5f)
                verticalLineTo(7.403f)
                curveTo(18.108f, 7.202f, 17.813f, 7.083f, 17.499f, 7.083f)
                curveTo(17.043f, 7.083f, 16.625f, 7.332f, 16.406f, 7.727f)
                lineTo(16.365f, 7.808f)
                lineTo(15.765f, 9.099f)
                lineTo(15.62f, 9.167f)
                horizontalLineTo(11.666f)
                curveTo(10.745f, 9.167f, 9.999f, 8.421f, 9.999f, 7.5f)
                verticalLineTo(2.5f)
                curveTo(9.999f, 1.58f, 10.745f, 0.833f, 11.666f, 0.833f)
                horizontalLineTo(16.666f)
                close()
                moveTo(12.291f, 2.5f)
                curveTo(11.946f, 2.5f, 11.666f, 2.78f, 11.666f, 3.125f)
                curveTo(11.666f, 3.47f, 11.946f, 3.75f, 12.291f, 3.75f)
                horizontalLineTo(13.541f)
                verticalLineTo(7.292f)
                curveTo(13.541f, 7.637f, 13.821f, 7.917f, 14.166f, 7.917f)
                curveTo(14.511f, 7.917f, 14.791f, 7.637f, 14.791f, 7.292f)
                verticalLineTo(3.75f)
                horizontalLineTo(16.041f)
                curveTo(16.386f, 3.75f, 16.666f, 3.47f, 16.666f, 3.125f)
                curveTo(16.666f, 2.78f, 16.386f, 2.5f, 16.041f, 2.5f)
                horizontalLineTo(12.291f)
                close()
            }
            path(fill = SolidColor(Color(0xFF1B1B1F))) {
                moveTo(18.291f, 10.042f)
                lineTo(19.999f, 10.833f)
                lineTo(18.291f, 11.625f)
                lineTo(17.499f, 13.333f)
                lineTo(16.708f, 11.625f)
                lineTo(14.999f, 10.833f)
                lineTo(16.708f, 10.042f)
                lineTo(17.499f, 8.333f)
                lineTo(18.291f, 10.042f)
                close()
            }
        }.build()

        return customFunctionalitiesTextToImage!!
    }

private var customFunctionalitiesTextToImage: ImageVector? = null
