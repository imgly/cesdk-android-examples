package ly.img.editor.base.components

import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.editor.core.iconpack.ArrowBack
import ly.img.editor.core.ui.UiDefaults
import ly.img.editor.core.ui.iconpack.Expandmore
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.R as coreR
import ly.img.editor.core.iconpack.IconPack as CoreIconPack

@Composable
fun NestedSheetHeader(
    title: String,
    onBack: () -> Unit,
    onClose: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(UiDefaults.sheetHeaderHeight),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            modifier = Modifier.padding(horizontal = 4.dp),
            onClick = onBack,
        ) {
            Icon(CoreIconPack.ArrowBack, contentDescription = stringResource(coreR.string.ly_img_editor_back))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f),
        )
        IconButton(
            modifier = Modifier.padding(horizontal = 4.dp),
            onClick = onClose,
        ) {
            Icon(IconPack.Expandmore, contentDescription = stringResource(coreR.string.ly_img_editor_close))
        }
    }
}
