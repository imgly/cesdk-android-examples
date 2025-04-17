package ly.img.editor.showcases.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ly.img.editor.core.theme.LocalIsDarkTheme
import ly.img.editor.showcases.R
import ly.img.editor.showcases.ui.preview.PreviewTheme

@Composable
fun QuickActionButton(
    title: String,
    @DrawableRes iconId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = title,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .size(56.dp)
                .background(
                    color = if (LocalIsDarkTheme.current) Color(0xFF_353438) else Color(0xFF_E4E1E6),
                    shape = CircleShape,
                )
                .padding(12.dp),
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            modifier = Modifier.width(56.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelSmall.copy(
                lineHeight = 13.sp,
            ),
        )
    }
}

@PreviewLightDark
@Composable
private fun QuickActionButtonPreview() {
    PreviewTheme {
        Surface {
            QuickActionButton(
                title = "Action Title",
                iconId = R.drawable.quick_action_record_video,
                onClick = {},
            )
        }
    }
}
