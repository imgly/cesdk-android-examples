package ly.img.editor.configuration.memories.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ly.img.editor.configuration.memories.iconPack.Image
import ly.img.editor.configuration.memories.style.VideoStyle
import ly.img.editor.configuration.memories.style.VideoStyles
import ly.img.editor.core.iconpack.IconPack

@Composable
fun FilterStylesSheet(
    onFilterSelected: (VideoStyle) -> Unit,
    thumbnails: Map<String, String>,
    initialSelectedId: String = VideoStyles.DEFAULT.id,
    modifier: Modifier = Modifier,
) {
    // Seed from the currently-applied style so reopening the sheet keeps it highlighted.
    var selectedStyleId by remember(initialSelectedId) { mutableStateOf(initialSelectedId) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Title
        Text(
            text = "Styles",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 24.dp),
        )

        // Filter styles row — centered when it fits, horizontally scrollable on smaller screens.
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(VideoStyles.ALL) { style ->
                FilterStyleItem(
                    style = style,
                    thumbnailUri = thumbnails[style.id],
                    isSelected = style.id == selectedStyleId,
                    onClick = {
                        selectedStyleId = style.id
                        onFilterSelected(style)
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun FilterStyleItem(
    style: VideoStyle,
    thumbnailUri: String?,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable { onClick() },
    ) {
        // Filter preview tile
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (isSelected) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    } else {
                        Color.Transparent
                    },
                )
                .border(
                    width = if (isSelected) 3.dp else 0.dp,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color.Transparent
                    },
                    shape = RoundedCornerShape(16.dp),
                )
                .padding(if (isSelected) 4.dp else 0.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(style.previewBackground)),
                contentAlignment = Alignment.Center,
            ) {
                if (thumbnailUri != null) {
                    // Thumbnail supplied by the custom style asset source, loaded with Coil.
                    AsyncImage(
                        model = thumbnailUri,
                        contentDescription = style.displayName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    // Default style: an icon on the tile color (no filter / original look).
                    Icon(
                        imageVector = IconPack.Image,
                        contentDescription = style.displayName,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(32.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Style name
        Text(
            text = style.displayName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            textAlign = TextAlign.Center,
        )
    }
}
