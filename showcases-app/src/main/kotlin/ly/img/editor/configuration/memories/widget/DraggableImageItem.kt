package ly.img.editor.configuration.memories.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ly.img.editor.configuration.memories.iconPack.CheckCircle
import ly.img.editor.configuration.memories.iconPack.PlayArrow
import ly.img.editor.configuration.memories.model.ImageItem
import ly.img.editor.core.iconpack.IconPack

@Composable
fun DraggableImageItem(
    image: ImageItem,
    elevation: Dp,
    index: Int,
    isSelectedForDeletion: Boolean = false,
    onToggleSelection: (() -> Unit)? = null,
) {
    // The long-press-drag gesture is owned by the parent grid (ImageGrid); this item only renders.
    Card(
        modifier = Modifier
            .aspectRatio(4 / 5f)
            .then(
                if (onToggleSelection != null) {
                    Modifier.clickable { onToggleSelection() }
                } else {
                    Modifier
                },
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        shape = RoundedCornerShape(8.dp),
        border = if (isSelectedForDeletion) {
            BorderStroke(3.dp, MaterialTheme.colorScheme.primary)
        } else {
            null
        },
    ) {
        Box {
            // For video, show a static middle frame; for images, load the URL directly.
            val model: Any? = if (image.isVideo) rememberVideoThumbnail(image.url) else image.url
            AsyncImage(
                model = model,
                contentDescription = if (image.isVideo) "Video ${image.id}" else "Image ${image.id}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )

            // Play badge to mark video items.
            if (image.isVideo) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(34.dp)
                        .background(Color.Black.copy(alpha = 0.45f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = IconPack.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }

            // Highlight scrim when marked for deletion
            if (isSelectedForDeletion) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                )
            }

            // Position index badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(4.dp)
                    .background(
                        Color.Black.copy(alpha = 0.7f),
                        RoundedCornerShape(4.dp),
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Text(
                    text = "${index + 1}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            // Selection checkbox (only when selection is enabled by the host)
            if (onToggleSelection != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp),
                ) {
                    if (isSelectedForDeletion) {
                        Icon(
                            imageVector = IconPack.CheckCircle,
                            contentDescription = "Selected for deletion",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color.White, CircleShape),
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color.Black.copy(alpha = 0.35f), CircleShape)
                                .border(2.dp, Color.White, CircleShape),
                        )
                    }
                }
            }
        }
    }
}
