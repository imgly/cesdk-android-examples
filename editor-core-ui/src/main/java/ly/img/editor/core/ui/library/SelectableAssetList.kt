package ly.img.editor.core.ui.library

import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ly.img.editor.core.R
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.ui.GradientCard
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.iconpack.None
import ly.img.editor.core.ui.library.components.LibraryImageCard
import ly.img.editor.core.ui.library.components.asset.SelectableAssetWrapper
import ly.img.editor.core.ui.library.components.section.LibrarySectionItem
import ly.img.editor.core.ui.library.state.WrappedAsset
import ly.img.editor.core.ui.library.util.AssetLibraryUiConfig

@Composable
fun SelectableAssetList(
    modifier: Modifier,
    selectedAsset: WrappedAsset?,
    libraryCategory: LibraryCategory,
    listState: LazyListState,
    selectedIcon: (WrappedAsset) -> ImageVector?,
    onAssetSelected: (WrappedAsset?) -> Unit,
    onAssetReselected: (WrappedAsset) -> Unit,
    onAssetLongClick: (WrappedAsset?) -> Unit,
) {
    val viewModel = viewModel<LibraryViewModel>()
    val uiState = viewModel.getAssetLibraryUiState(libraryCategory).collectAsState()
    var selectedAssetIndex by remember {
        mutableStateOf(0)
    }

    LaunchedEffect(selectedAsset, uiState.value.sectionItems) {
        if (selectedAsset == null) {
            selectedAssetIndex = 0
            return@LaunchedEffect
        }
        var sectionStartIndex = 2 // "None" element + first section spacer
        uiState.value.sectionItems.forEach { section ->
            if (section !is LibrarySectionItem.Content) return@forEach
            val assetIndex =
                section.wrappedAssets.indexOfFirst { wrappedAsset ->
                    wrappedAsset == selectedAsset
                }
            if (assetIndex != -1) {
                selectedAssetIndex = sectionStartIndex + assetIndex
                return@LaunchedEffect
            }
            sectionStartIndex += section.wrappedAssets.size + 1
        }
    }

    suspend fun centerSelectedItem(animate: Boolean = true) {
        val target =
            listState.layoutInfo.visibleItemsInfo.firstOrNull {
                it.index == selectedAssetIndex
            } ?: run {
                listState.scrollToItem(selectedAssetIndex)
                centerSelectedItem(animate = false)
                return
            }
        val center = listState.layoutInfo.viewportEndOffset / 2
        val childCenter = target.offset + target.size / 2
        val diff = (childCenter - center).toFloat()
        if (animate) {
            listState.animateScrollBy(diff)
        } else {
            listState.scrollBy(diff)
        }
    }

    LaunchedEffect(selectedAssetIndex) {
        centerSelectedItem()
    }

    Surface(
        modifier =
            modifier
                .fillMaxWidth()
                .wrapContentHeight(),
    ) {
        LazyRow(
            modifier =
                Modifier
                    .height(130.dp)
                    .padding(top = 8.dp, bottom = 8.dp)
                    .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            state = listState,
        ) {
            item {
                Column {
                    Column(
                        modifier =
                            Modifier
                                .wrapContentSize()
                                .padding(bottom = 8.dp),
                    ) {
                        SelectableAssetWrapper(
                            isSelected = selectedAsset == null,
                            selectedIcon = null,
                        ) {
                            GradientCard(
                                modifier = Modifier.size(80.dp),
                                onClick = { onAssetSelected(null) },
                                onLongClick = { },
                            ) {
                                Icon(
                                    IconPack.None,
                                    contentDescription = null,
                                    modifier =
                                        Modifier
                                            .fillMaxSize()
                                            .padding(12.dp)
                                            .align(Alignment.Center),
                                )
                            }
                        }
                    }
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = stringResource(R.string.ly_img_editor_remove),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }

            uiState.value.sectionItems.forEach { sectionItem ->
                item {
                    Spacer(modifier = Modifier.width(16.dp))
                }
                if (sectionItem is LibrarySectionItem.Content) {
                    val assetType = sectionItem.assetType
                    val wrappedAssets = sectionItem.wrappedAssets
                    items(wrappedAssets) { wrappedAsset ->
                        Column {
                            Column(
                                modifier =
                                    Modifier
                                        .wrapContentSize()
                                        .padding(bottom = 8.dp),
                            ) {
                                SelectableAssetWrapper(
                                    isSelected = wrappedAsset == selectedAsset,
                                    selectedIcon = selectedIcon(wrappedAsset),
                                    selectedIconTint = Color.White,
                                ) {
                                    val asset = wrappedAsset.asset
                                    LibraryImageCard(
                                        modifier = Modifier.width(80.dp),
                                        uri = asset.getThumbnailUri(),
                                        onClick = {
                                            if (wrappedAsset == selectedAsset) {
                                                onAssetReselected(wrappedAsset)
                                            } else {
                                                onAssetSelected(wrappedAsset)
                                            }
                                        },
                                        onLongClick = { onAssetLongClick(wrappedAsset) },
                                        contentPadding = AssetLibraryUiConfig.contentPadding(assetType),
                                        contentScale = AssetLibraryUiConfig.contentScale(assetType),
                                        tintImages = AssetLibraryUiConfig.shouldTintImages(assetType),
                                    )
                                }
                            }

                            Text(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                text = wrappedAsset.asset.label ?: "",
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }
                }
            }
        }
    }
}
