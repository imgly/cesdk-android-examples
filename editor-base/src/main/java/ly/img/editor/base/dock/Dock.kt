package ly.img.editor.base.dock

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.editor.base.ui.Block
import ly.img.editor.core.ui.SheetHeader
import ly.img.editor.core.ui.iconpack.Close
import ly.img.editor.core.ui.iconpack.IconPack

@Composable
fun Dock(
    selectedBlock: Block,
    onClose: () -> Unit,
    onClick: (OptionType) -> Unit,
    modifier: Modifier,
) {
    Surface(
        modifier = modifier,
    ) {
        Column {
            SheetHeader(
                title = stringResource(id = selectedBlock.type.titleRes),
                icon = IconPack.Close,
                onClose = onClose,
            )
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                selectedBlock.options.forEach {
                    DockMenuOption(
                        data = it,
                        onClick = onClick,
                    )
                }
            }
        }
    }
}
