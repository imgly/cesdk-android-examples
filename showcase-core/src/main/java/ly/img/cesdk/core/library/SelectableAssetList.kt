package ly.img.cesdk.core.library

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ly.img.cesdk.core.iconpack.IconPack
import ly.img.cesdk.core.iconpack.None
import ly.img.cesdk.core.library.components.asset.AssetCreditsContent
import ly.img.cesdk.core.library.components.section.LibrarySectionItem
import ly.img.cesdk.core.library.state.LibraryCategory
import ly.img.cesdk.core.library.util.LibraryEvent
import ly.img.cesdk.core.library.util.LibraryUiEvent
import ly.img.cesdk.core.ui.AnyComposable
import ly.img.cesdk.core.ui.GradientCard
import ly.img.cesdk.core.R
import ly.img.cesdk.core.data.WrappedAsset
import ly.img.cesdk.core.data.getThumbnailUri
import ly.img.cesdk.core.library.components.LibraryImageCard
import ly.img.cesdk.core.library.components.asset.SelectableAssetWrapper
import ly.img.cesdk.core.library.util.AssetLibraryUiConfig

@Composable
fun SelectableAssetList(
    onCloseAssetDetails: () -> Unit,
    showAnyComposable: (AnyComposable) -> Unit,
    libraryCategory: LibraryCategory,
    onAssetReselected: (WrappedAsset?) -> Unit,
    onAssetSelected: (WrappedAsset?) -> Unit,
    onAssetLongClick: (WrappedAsset?) -> Unit,
    checkInitialSelection: (WrappedAsset?) -> Boolean,
    selectedIcon: (WrappedAsset) -> ImageVector?,
    listState: LazyListState,
) {
    val viewModel = viewModel<LibraryViewModel>()
    val uiState = viewModel.getAssetLibraryUiState(libraryCategory).collectAsState()

    var currentSelected: WrappedAsset? = null
    val noneIsSelected = remember(libraryCategory) {
        mutableStateOf(checkInitialSelection(null))
    }

    LaunchedEffect(libraryCategory) {
        viewModel.onEvent(LibraryEvent.OnFetch(libraryCategory, true))
    }
    LaunchedEffect(uiState.value.sectionItems) {
        fun findSelectedIndex() : Int {
            var currentSectionIndex = 0
            uiState.value.sectionItems.forEach {
                if (it is LibrarySectionItem.Content) {
                    val indexOf = it.wrappedAssets.indexOfFirst { wrappedAsset ->
                        checkInitialSelection(wrappedAsset)
                    }
                    if (indexOf >= 0) {
                        return currentSectionIndex + indexOf
                    }
                    currentSectionIndex += it.wrappedAssets.count() + 1 // + 1 for the Spacer
                }
            }
            return -1
        }
        if (currentSelected == null) {
            val index = findSelectedIndex()
            if (index > 0) {
                listState.scrollToItem(index, scrollOffset = 1)
            }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {

        LazyRow(
            modifier = Modifier
                .height(130.dp)
                .padding(top = 8.dp, bottom = 8.dp)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            state = listState
        ) {
            item {
                Column {
                    Column(
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(bottom = 8.dp)) {
                        // This can happen if history is changed while the library is open.
                        val isNoneSelected = checkInitialSelection(null)
                        if (isNoneSelected != noneIsSelected.value) {
                            noneIsSelected.value = isNoneSelected
                        }
                        SelectableAssetWrapper(
                            isSelected = noneIsSelected.value,
                            selectedIcon = null
                        ) {
                            GradientCard(
                                modifier = Modifier.size(80.dp),
                                onClick = {
                                    noneIsSelected.value = true
                                    currentSelected?.setSelected(false)
                                    onAssetSelected(null)
                                },
                                onLongClick = { }
                            ) {
                                Icon(
                                    IconPack.None,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp)
                                        .align(Alignment.Center)
                                )
                            }
                        }
                    }
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = stringResource(R.string.cesdk_remove),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            uiState.value.sectionItems.forEach { sectionItem ->
                if (sectionItem is LibrarySectionItem.Header) {
                    item {
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                }
                if (sectionItem is LibrarySectionItem.Content) {
                    val assetSourceGroupType = sectionItem.assetSourceGroupType
                    val wrappedAssets = sectionItem.wrappedAssets
                    items(wrappedAssets) { wrappedAsset ->
                        val isSelected = checkInitialSelection(wrappedAsset)
                        if (isSelected) {
                            currentSelected = wrappedAsset
                            wrappedAsset.setSelected(true)
                        } else {
                            wrappedAsset.setSelected(false)
                        }
                        Column {
                            Column(modifier = Modifier
                                .wrapContentSize()
                                .padding(bottom = 8.dp)) {
                                SelectableAssetWrapper(
                                    isSelected = wrappedAsset.isSelected.value,
                                    selectedIcon = selectedIcon(wrappedAsset),
                                    selectedIconTint = Color.White
                                ) {
                                    val asset = wrappedAsset.asset
                                    LibraryImageCard(
                                        modifier = Modifier.width(80.dp),
                                        uri = asset.getThumbnailUri(),
                                        onClick = {
                                            if (wrappedAsset.isSelected.value) {
                                                onAssetReselected(wrappedAsset)
                                            } else {
                                                onAssetSelected(wrappedAsset)
                                                currentSelected?.setSelected(checkInitialSelection(currentSelected))
                                                noneIsSelected.value = checkInitialSelection(null)
                                            }
                                        },
                                        onLongClick = { onAssetLongClick(wrappedAsset) },
                                        contentPadding = AssetLibraryUiConfig.contentPadding(assetSourceGroupType),
                                        contentScale = AssetLibraryUiConfig.contentScale(assetSourceGroupType),
                                        tintImages = AssetLibraryUiConfig.shouldTintImages(assetSourceGroupType)
                                    )
                                }
                            }

                            Text(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                text = wrappedAsset.asset.label ?: "",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
