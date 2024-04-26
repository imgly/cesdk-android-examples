package ly.img.editor.base.dock.options.format

import android.net.Uri
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
import ly.img.editor.core.ui.inspectorSheetPadding
import ly.img.editor.core.ui.library.SelectableAssetListProvider
import ly.img.editor.core.ui.library.data.font.FontData
import ly.img.editor.core.ui.library.data.font.FontDataMapper
import ly.img.engine.Typeface

@Composable
fun FontListUi(
    libraryCategory: LibraryCategory,
    fontFamily: String,
    filter: List<String>,
    onSelectFont: (Uri, Typeface) -> Unit,
) {
    var listData by remember {
        mutableStateOf<List<FontData>>(emptyList())
    }
    val fontDataMapper = remember { FontDataMapper() }
    SelectableAssetListProvider(libraryCategory, filter, fontDataMapper::map) {
        listData = it
    }
    if (listData.isNotEmpty()) {
        Card(
            colors = UiDefaults.cardColors,
            modifier = Modifier.inspectorSheetPadding(),
        ) {
            val selectedIndex =
                remember(fontFamily) { listData.indexOfFirst { fontFamily == it.typeface.name } }
            val initialFirstVisibleItemIndex = if (selectedIndex == -1) 0 else selectedIndex
            val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = initialFirstVisibleItemIndex)
            LazyColumn(state = lazyListState) {
                items(listData) {
                    CheckedTextRow(
                        isChecked = fontFamily == it.typeface.name,
                        text = it.typeface.name,
                        fontData = it,
                        onClick = {
                            if (it.typeface.name != fontFamily) {
                                onSelectFont(it.uri, it.typeface)
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
