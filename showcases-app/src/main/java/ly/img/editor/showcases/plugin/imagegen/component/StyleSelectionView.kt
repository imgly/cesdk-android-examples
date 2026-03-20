package ly.img.editor.showcases.plugin.imagegen.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ly.img.editor.core.ui.SheetHeader
import ly.img.editor.showcases.R
import ly.img.editor.showcases.plugin.imagegen.OutputType
import ly.img.editor.showcases.plugin.imagegen.api.FalImageStyle
import ly.img.editor.showcases.plugin.imagegen.api.FalImageStylesImage
import ly.img.editor.showcases.plugin.imagegen.api.FalImageStylesVector
import ly.img.editor.showcases.plugin.imagegen.api.getImageUrl
import ly.img.editor.showcases.ui.preview.PreviewTheme
import androidx.compose.foundation.lazy.grid.items as gridItems

@Composable
fun StyleSelectionView(
    selectedStyle: FalImageStyle,
    selectedOutput: OutputType,
    onStyleSelected: (FalImageStyle) -> Unit,
    onBack: () -> Unit,
    onCloseSheet: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SheetHeader(
            title = stringResource(R.string.ly_img_showcases_ai_image_style_selection_title, selectedOutput.label),
            onClose = onCloseSheet,
            actionContent = {
                IconButton(
                    onClick = onBack,
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.ly_img_showcases_back_content_description),
                    )
                }
            },
        )

        StyleSelectionContent(
            modifier = Modifier.weight(1f),
            selectedStyle = selectedStyle,
            selectedOutput = selectedOutput,
            onStyleSelected = onStyleSelected,
        )
    }
}

// Style Selection Content (without header)
@Composable
fun StyleSelectionContent(
    selectedStyle: FalImageStyle,
    selectedOutput: OutputType,
    onStyleSelected: (FalImageStyle) -> Unit,
    modifier: Modifier = Modifier,
) {
    val allStyles = when (selectedOutput) {
        OutputType.IMAGE -> FalImageStylesImage
        OutputType.VECTOR -> FalImageStylesVector
    }

    val groupedStyles = if (selectedOutput == OutputType.IMAGE) {
        val realisticStyles = allStyles.filter { it.apiValue.startsWith("realistic_image") }
        val digitalIllustrationStyles = allStyles.filter { it.apiValue.startsWith("digital_illustration") }

        listOf(
            stringResource(R.string.ly_img_showcases_ai_image_style_realistic_image) to realisticStyles,
            stringResource(R.string.ly_img_showcases_ai_image_style_digital_illustration) to digitalIllustrationStyles,
        )
    } else {
        listOf(stringResource(R.string.ly_img_showcases_ai_image_style_vector_illustration) to allStyles)
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 96.dp),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            bottom = 56.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        groupedStyles.forEach { (categoryName, styles) ->
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = "$categoryName (${styles.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }

            gridItems(styles) { style ->
                StyleSelectionItem(
                    style = style,
                    isSelected = style == selectedStyle,
                    onClick = { onStyleSelected(style) },
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

@Composable
fun StyleSelectionItem(
    style: FalImageStyle,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SelectionBorder(
            isSelected = isSelected,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                    )
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onClick() },
            ) {
                AsyncImage(
                    model = style.getImageUrl(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(6.dp)),
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .fillMaxHeight(.5f)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = .3f),
                                ),
                            ),
                        ),
                )
            }
        }

        Text(
            text = style.displayName,
            modifier = Modifier,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
        )
    }
}

@Composable
@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun StyleSelectionViewPreview() {
    PreviewTheme {
        Surface {
            StyleSelectionView(
                selectedStyle = FalImageStyle.RealisticImage,
                selectedOutput = OutputType.IMAGE,
                onStyleSelected = {},
                onBack = {},
                onCloseSheet = {},
            )
        }
    }
}

@Composable
@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun StyleSelectionItemPreview() {
    PreviewTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StyleSelectionItem(
                        style = FalImageStyle.RealisticImage,
                        isSelected = true,
                        onClick = {},
                        modifier = Modifier.weight(1f),
                    )

                    StyleSelectionItem(
                        style = FalImageStyle.VectorIllustrationBoldStroke,
                        isSelected = false,
                        onClick = {},
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}
