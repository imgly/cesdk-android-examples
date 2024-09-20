package ly.img.editor.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.editor.core.R
import ly.img.editor.core.ui.iconpack.Expandmore
import ly.img.editor.core.ui.iconpack.IconPack

@Composable
fun SheetHeader(
    title: String,
    onClose: () -> Unit,
    actionContent: @Composable (BoxScope.() -> Unit)? = null,
    icon: ImageVector = IconPack.Expandmore,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(UiDefaults.sheetHeaderHeight),
    ) {
        actionContent?.let {
            Box(
                Modifier
                    .padding(horizontal = 4.dp)
                    .align(Alignment.CenterStart),
            ) {
                it()
            }
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.Center),
        )
        IconButton(
            onClick = onClose,
            modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .padding(horizontal = 4.dp),
        ) {
            Icon(icon, contentDescription = stringResource(R.string.ly_img_editor_close))
        }
    }
}
