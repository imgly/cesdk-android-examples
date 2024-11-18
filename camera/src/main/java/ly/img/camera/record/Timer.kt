package ly.img.camera.record

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import ly.img.camera.R
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.iconpack.TimerOutline
import ly.img.editor.core.ui.iconpack.TimerTenOutline
import ly.img.editor.core.ui.iconpack.TimerThreeOutline

internal sealed class Timer(
    val duration: Int,
    val icon: ImageVector,
    @StringRes val label: Int,
) {
    data object Off : Timer(0, IconPack.TimerOutline, R.string.ly_img_camera_timer_off)

    data object Three : Timer(3, IconPack.TimerThreeOutline, R.string.ly_img_camera_timer_3)

    data object Ten : Timer(10, IconPack.TimerTenOutline, R.string.ly_img_camera_timer_10)
}
