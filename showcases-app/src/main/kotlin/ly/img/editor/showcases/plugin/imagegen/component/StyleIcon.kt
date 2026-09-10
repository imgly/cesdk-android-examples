package ly.img.editor.showcases.plugin.imagegen.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ly.img.editor.showcases.plugin.imagegen.api.FalImageStyle
import ly.img.editor.showcases.plugin.imagegen.api.getImageUrl
import ly.img.editor.showcases.ui.preview.PreviewTheme

@Composable
fun StyleIcon(
    style: FalImageStyle,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = style.getImageUrl(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .padding(2.dp)
            .clip(
                RoundedCornerShape(
                    topStart = 24.dp,
                    bottomStart = 24.dp,
                    topEnd = 8.dp,
                    bottomEnd = 8.dp,
                ),
            )
            .background(color = MaterialTheme.colorScheme.primary)
            .border(
                width = 1.dp,
                color = Color.Black.copy(alpha = .1f),
                shape = RoundedCornerShape(
                    topStart = 24.dp,
                    bottomStart = 24.dp,
                    topEnd = 8.dp,
                    bottomEnd = 8.dp,
                ),
            ),
    )
}

@Composable
@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun StyleIconPreview() {
    PreviewTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = "Style Icons",
                    style = MaterialTheme.typography.titleMedium,
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        StyleIcon(
                            style = FalImageStyle.RealisticImage,
                            modifier = Modifier.size(40.dp),
                        )
                        Text(
                            text = "Realistic",
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        StyleIcon(
                            style = FalImageStyle.VectorIllustrationBoldStroke,
                            modifier = Modifier.size(40.dp),
                        )
                        Text(
                            text = "Vector Bold",
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}
