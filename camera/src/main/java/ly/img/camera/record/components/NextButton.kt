package ly.img.camera.record.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.editor.compose.foundation.clickable
import ly.img.editor.core.theme.LocalExtendedColorScheme
import ly.img.editor.core.ui.iconpack.Arrowrightbig
import ly.img.editor.core.ui.iconpack.IconPack

@Composable
internal fun NextButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val extendedColorScheme = LocalExtendedColorScheme.current
    val shape = CircleShape

    CompositionLocalProvider(LocalContentColor provides extendedColorScheme.black) {
        Icon(
            imageVector = IconPack.Arrowrightbig,
            contentDescription = stringResource(ly.img.editor.core.R.string.ly_img_editor_close),
            modifier =
                modifier
                    .size(56.dp)
                    .clip(shape)
                    .clickable(onClick = onClick)
                    .background(extendedColorScheme.white)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.onBackground,
                        shape = shape,
                    )
                    .padding(16.dp),
        )
    }
}
