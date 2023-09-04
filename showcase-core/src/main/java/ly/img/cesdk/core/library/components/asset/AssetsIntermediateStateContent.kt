package ly.img.cesdk.core.library.components.asset

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.cesdk.core.R
import ly.img.cesdk.core.library.state.AssetSourceGroupType
import ly.img.cesdk.core.library.util.AssetLibraryUiConfig
import ly.img.cesdk.core.library.util.shimmerWithLocalShimmer
import ly.img.cesdk.core.ui.GradientCard
import ly.img.cesdk.core.ui.utils.ifTrue

@Composable
internal fun AssetsIntermediateStateContent(
    state: IntermediateState,
    assetSourceGroupType: AssetSourceGroupType,
    inGrid: Boolean = false
) {
    val isLoading = state == IntermediateState.Loading

    Box(
        Modifier
            .fillMaxWidth()
            .ifTrue(isLoading) {
                shimmerWithLocalShimmer()
            }
    ) {
        if (assetSourceGroupType == AssetSourceGroupType.Audio) {
            Column(modifier = Modifier.ifTrue(!isLoading) {
                drawMask()
            }) {
                repeat(3) {
                    ListItem(
                        headlineContent = {
                            GradientCard(
                                modifier = Modifier
                                    .height(24.dp)
                                    .width(120.dp)
                            )
                        },
                        leadingContent = {
                            GradientCard(modifier = Modifier.size(56.dp))
                        },
                    )
                }
            }
        } else if (assetSourceGroupType == AssetSourceGroupType.Text) {
            Column(modifier = Modifier.ifTrue(!isLoading) {
                drawMask()
            }) {
                repeat(3) {
                    ListItem(
                        headlineContent = {
                            GradientCard(
                                modifier = Modifier
                                    .height(24.dp)
                                    .width(160.dp)
                            )
                        }
                    )
                }
            }
        } else {
            if (inGrid) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(AssetLibraryUiConfig.assetGridColumns(assetSourceGroupType)),
                    verticalArrangement = AssetLibraryUiConfig.assetGridVerticalArrangement(assetSourceGroupType),
                    horizontalArrangement = AssetLibraryUiConfig.assetGridHorizontalArrangement(assetSourceGroupType),
                    contentPadding = PaddingValues(4.dp),
                    modifier = Modifier.ifTrue(!isLoading) {
                        drawMask()
                    }
                ) {
                    items(AssetLibraryUiConfig.assetGridColumns(assetSourceGroupType)) {
                        GradientCard(modifier = Modifier.aspectRatio(1f))
                    }
                }
            } else {
                LazyRow(
                    modifier = Modifier
                        .height(AssetLibraryUiConfig.contentRowHeight(assetSourceGroupType))
                        .ifTrue(!isLoading) {
                            drawMask()
                        },
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    userScrollEnabled = false,
                ) {
                    items(5) {
                        GradientCard(modifier = Modifier.aspectRatio(1f))
                    }
                }
            }
        }

        if (state == IntermediateState.Empty) {
            Text(
                stringResource(id = R.string.cesdk_no_elements),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

internal enum class IntermediateState {
    Loading,
    Empty
}

private fun Modifier.drawMask() = then(
    // Workaround to enable alpha compositing
    // TODO: Use https://developer.android.com/jetpack/compose/graphics/draw/modifiers#compositing-strategy when available
    graphicsLayer { alpha = 0.99F }.drawWithContent {
        drawContent()
        drawRect(
            brush = Brush.linearGradient(
                0f to Color.Black,
                1f to Color.Transparent
            ),
            blendMode = BlendMode.DstIn
        )
    }
)