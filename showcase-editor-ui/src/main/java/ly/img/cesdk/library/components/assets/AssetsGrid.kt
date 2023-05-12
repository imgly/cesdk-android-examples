package ly.img.cesdk.library.components.assets

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ly.img.cesdk.core.components.LibraryImageCard
import ly.img.cesdk.core.iconpack.Erroroutline
import ly.img.cesdk.core.iconpack.IconPack
import ly.img.cesdk.core.iconpack.Search
import ly.img.cesdk.core.iconpack.iconpack.Folder
import ly.img.cesdk.editorui.R
import ly.img.cesdk.library.LibraryViewModel
import ly.img.engine.Asset

@Composable
fun AssetsGrid(
    assetSource: AssetSource,
    onClick: (Asset) -> Unit,
    columns: Int = 3,
    contentScale: ContentScale = ContentScale.Crop,
    contentPadding: Dp = 0.dp,
    tintImages: Boolean = false,
    onImagePicked: (Uri) -> Unit = {},
    viewModel: LibraryViewModel = viewModel(),
) {
    val lazyGridState = rememberLazyGridState()
    val uiState by viewModel.getUiStateFlow(assetSource).collectAsState()

    val shouldStartPaginate by remember {
        derivedStateOf {
            uiState.canPaginate && (lazyGridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -9) >=
                lazyGridState.layoutInfo.totalItemsCount - 6
        }
    }

    LaunchedEffect(shouldStartPaginate, uiState.listState) {
        if (shouldStartPaginate && uiState.listState == ListState.IDLE)
            viewModel.fetchAssets(assetSource)
    }

    LaunchedEffect(assetSource) {
        viewModel.fetchAssets(assetSource)
    }

    when (uiState.listState) {
        ListState.LOADING -> {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            ) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }

        ListState.ERROR -> {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                ) {
                    Icon(IconPack.Erroroutline, contentDescription = null)
                    Text(
                        stringResource(R.string.cesdk_error_text),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )
                }
                Button(onClick = {
                    viewModel.fetchAssets(assetSource)
                }) {
                    Text(text = stringResource(R.string.cesdk_retry))
                }
            }
        }

        ListState.EMPTY_RESULT -> {
            EmptyResult(
                IconPack.Search,
                stringResource(R.string.cesdk_no_search_results)
            )
        }

        ListState.UPLOADS_EMPTY_MAIN_RESULT -> {
            EmptyResult(
                IconPack.Folder,
                stringResource(R.string.cesdk_nothing_here_yet)
            ) {
                val launcher =
                    rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
                        uri?.let { onImagePicked(it) }
                    }
                Button(onClick = {
                    launcher.launch("image/*")
                }) {
                    Text(text = stringResource(R.string.cesdk_add_image))
                }
            }
        }

        else -> {
            LazyVerticalGrid(
                state = lazyGridState,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(4.dp),
                columns = GridCells.Fixed(columns)
            ) {
                items(uiState.assets) { asset ->
                    LibraryImageCard(
                        uri = asset.getThumbnailUri(),
                        onClick = {
                            onClick(asset)
                        },
                        contentScale = contentScale,
                        contentPadding = contentPadding,
                        tintImages = tintImages
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyResult(
    icon: ImageVector,
    text: String,
    button: @Composable (() -> Unit)? = null
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
        ) {
            Icon(icon, contentDescription = null)
            Text(
                text,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )
        }
        button?.invoke()
    }
}