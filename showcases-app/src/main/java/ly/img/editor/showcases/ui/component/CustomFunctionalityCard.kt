package ly.img.editor.showcases.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ly.img.editor.showcases.R
import ly.img.editor.showcases.Screen
import ly.img.editor.showcases.ShowcaseItem
import ly.img.editor.showcases.icon.CustomFunctionalitiesTextToImage
import ly.img.editor.showcases.icon.IconPack
import ly.img.editor.showcases.ui.modifier.linearGradientBackground
import ly.img.editor.showcases.ui.preview.PreviewTheme

@Composable
fun CustomFunctionalityCard(
    item: ShowcaseItem.CustomFunctionality,
    modifier: Modifier = Modifier,
    onClick: (String?, clickAction: ShowcaseItem.CarouselContent.ClickAction) -> Unit,
) {
    Row(
        modifier = modifier
            .linearGradientBackground(
                height = 152.dp,
                shape = MaterialTheme.shapes.small,
            )
            .height(152.dp)
            .clip(MaterialTheme.shapes.small)
            .clickable { onClick(item.actionScene, item.clickAction) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(144.dp)
                .padding(
                    start = 16.dp,
                    top = 16.dp,
                    bottom = 16.dp,
                ),
        ) {
            Icon(
                imageVector = item.vectorIcon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.weight(1f))

            Text(
                text = stringResource(item.label),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    lineHeight = 18.sp,
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(Modifier.height(6.dp))
            item.sublabel?.let {
                Text(
                    text = stringResource(item.sublabel),
                    style = MaterialTheme.typography.labelSmall.copy(
                        lineHeight = 13.sp,
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        AsyncImage(
            model = ImageRequest
                .Builder(LocalContext.current)
                .apply {
                    data(item.thumbnailRes)
                    placeholder(item.thumbnailRes)
                }.build(),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .fillMaxHeight(),
        )
    }
}

@Composable
@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun CustomFunctionalityCardPreview() {
    PreviewTheme {
        Surface {
            CustomFunctionalityCard(
                item = ShowcaseItem.CustomFunctionality(
                    vectorIcon = IconPack.CustomFunctionalitiesTextToImage,
                    thumbnailRes = R.drawable.custom_functionality_text_to_image,
                    label = R.string.ly_img_showcases_button_text_to_image_title,
                    sublabel = R.string.ly_img_showcases_button_text_to_image_subtitle,
                    actionScene = null,
                    actionScreen = Screen.TextToImage,
                ),
                onClick = { _, _ -> },
            )
        }
    }
}
