package ly.img.editor.base.dock.options.format

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ly.img.editor.base.components.CheckedTextRow
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.ui.UiDefaults
import ly.img.editor.core.ui.library.SelectableAssetListProvider
import ly.img.editor.core.ui.library.data.font.FontData
import ly.img.editor.core.ui.library.data.font.FontDataMapper
import ly.img.editor.core.ui.sheetCardContentModifier
import ly.img.engine.FontStyle
import ly.img.engine.FontWeight

@Composable
fun FontListUi(
    libraryCategory: LibraryCategory,
    fontFamily: String,
    filter: List<String>,
    onSelectFont: (FontData) -> Unit,
) {
    var listData by remember {
        mutableStateOf<List<FontData>>(emptyList())
    }
    val fontDataMapper = remember { FontDataMapper() }
    SelectableAssetListProvider(libraryCategory, filter, fontDataMapper::map) {
        listData = it
    }
    FontListUi(fontList = listData, selectedFontFamily = fontFamily, onSelectFont = onSelectFont)
}

@Composable
fun FontListUi(
    fontList: List<FontData>,
    selectedFontFamily: String,
    selectedWeight: FontWeight? = null,
    selectedStyle: FontStyle? = null,
    labelMap: @Composable (FontData) -> String = { it.typeface.name },
    onSelectFont: (FontData) -> Unit,
) {
    if (fontList.isNotEmpty()) {
        Card(
            colors = UiDefaults.cardColors,
            modifier = Modifier.sheetCardContentModifier(),
        ) {
            val selectedIndex =
                remember(selectedFontFamily) { fontList.indexOfFirst { selectedFontFamily == it.typeface.name } }
            val initialFirstVisibleItemIndex = if (selectedIndex == -1) 0 else selectedIndex
            val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = initialFirstVisibleItemIndex)
            LazyColumn(state = lazyListState) {
                items(fontList) {
                    CheckedTextRow(
                        isChecked =
                            selectedFontFamily == it.typeface.name &&
                                (selectedWeight == null || it.weight.weight == selectedWeight.value) &&
                                (selectedStyle == null || it.style == selectedStyle),
                        text = labelMap(it),
                        fontData = it,
                        onClick = {
                            if (it.typeface.name != selectedFontFamily ||
                                (selectedWeight != null && it.weight.weight != selectedWeight.value) ||
                                (selectedStyle != null && it.style != selectedStyle)
                            ) {
                                onSelectFont(it)
                            }
                        },
                    )
                }
            }
        }
    } else {
        // while fonts are loading a dummy placeholder. This is to avoid unpleasant blink
        LazyColumn {
            items(12) {
                CheckedTextRow(
                    isChecked = false,
                    text = "",
                )
            }
        }
    }
}
