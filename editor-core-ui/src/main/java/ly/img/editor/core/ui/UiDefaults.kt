package ly.img.editor.core.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ly.img.editor.core.theme.surface1

object UiDefaults {
    val sheetHeaderHeight = 48.dp

    val cardColors: CardColors
        @Composable
        get() = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface1)

    val CornerLargeTop =
        RoundedCornerShape(
            topStart = 16.0.dp,
            topEnd = 16.0.dp,
            bottomEnd = 0.0.dp,
            bottomStart = 0.0.dp,
        )
}

fun Modifier.inspectorSheetPadding() =
    this.then(
        Modifier.padding(top = 8.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
    )
