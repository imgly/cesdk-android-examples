package ly.img.editor.core.ui

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ly.img.editor.core.theme.surface1

object UiDefaults {
    val sheetHeaderHeight = 48.dp

    val cardColors: CardColors
        @Composable
        get() = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface1)

    val cardColorsExperimental: ly.img.editor.compose.material3.CardColors
        @Composable
        get() = ly.img.editor.compose.material3.CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface1)

    val CornerLargeTop =
        RoundedCornerShape(
            topStart = 16.0.dp,
            topEnd = 16.0.dp,
            bottomEnd = 0.0.dp,
            bottomStart = 0.0.dp,
        )
}

fun Modifier.sheetCardContentModifier(top: Dp = 8.dp) =
    this
        .padding(top = top, bottom = 0.dp, start = 16.dp, end = 16.dp)
        .navigationBarsPadding()

fun Modifier.sheetScrollableContentModifier(top: Dp = 8.dp) =
    composed {
        this
            .verticalScroll(rememberScrollState())
            .padding(top = top, bottom = 16.dp, start = 16.dp, end = 16.dp)
            .navigationBarsPadding()
    }
