package ly.img.cesdk.dock.options.format

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ly.img.cesdk.core.UiDefaults
import ly.img.cesdk.core.components.CheckedTextRow
import ly.img.cesdk.core.inspectorSheetPadding
import ly.img.cesdk.library.data.font.FontData
import ly.img.cesdk.library.data.font.FontFamilyData

@Composable
fun FontListUi(
    fontFamily: String,
    fontFamilies: List<FontFamilyData>,
    onSelectFont: (FontData) -> Unit
) {
    Card(
        colors = UiDefaults.cardColors,
        modifier = Modifier.inspectorSheetPadding()
    ) {
        val selectedIndex = remember(fontFamily) { fontFamilies.indexOfFirst { fontFamily == it.name } }
        val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex)
        LazyColumn(state = lazyListState) {
            items(fontFamilies) {
                val isChecked = fontFamily == it.name
                CheckedTextRow(
                    isChecked = isChecked,
                    text = it.name,
                    fontFamily = it.fontFamily,
                    fontWeight = it.displayFont.fontWeight,
                    onClick = { onSelectFont(it.displayFont) }
                )
            }
        }
    }
}