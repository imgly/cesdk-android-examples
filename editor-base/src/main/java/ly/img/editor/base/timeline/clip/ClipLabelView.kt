package ly.img.editor.base.timeline.clip

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ly.img.editor.core.iconpack.ImageOutline
import ly.img.editor.core.iconpack.Music
import ly.img.editor.core.iconpack.PlayBoxOutline
import ly.img.editor.core.iconpack.ShapesOutline
import ly.img.editor.core.iconpack.StickerEmojiOutline
import ly.img.editor.core.iconpack.TextFields
import ly.img.editor.core.theme.LocalExtendedColorScheme
import ly.img.editor.core.theme.surface2
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.iconpack.Lockoutline
import ly.img.editor.core.ui.iconpack.Volumeoff

@Composable
fun ClipLabelView(
    modifier: Modifier,
    clip: Clip,
    duration: String,
    isSelected: Boolean,
) {
    val backgroundColor =
        when (clip.clipType) {
            ClipType.Audio -> LocalExtendedColorScheme.current.purple.onColor.copy(alpha = 0.24f)
            else -> MaterialTheme.colorScheme.surface2.copy(alpha = 0.75f)
        }

    Box(
        modifier =
            modifier
                .padding(2.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(backgroundColor),
    ) {
        Row(
            modifier =
                Modifier
                    .height(16.dp)
                    .wrapContentWidth(align = Alignment.Start, unbounded = true)
                    .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isSelected) {
                Text(
                    text = duration,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            if (clip.isMuted && (clip.clipType == ClipType.Video || clip.clipType == ClipType.Audio)) {
                Icon(IconPack.Volumeoff, contentDescription = null)
            }
            ClipIcon(clip)
            if (clip.clipType == ClipType.Audio) {
                Text(
                    text = clip.title,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun ClipIcon(clip: Clip) {
    val icon =
        if (!clip.allowsSelecting) {
            IconPack.Lockoutline
        } else {
            when (clip.clipType) {
                ClipType.Audio -> ly.img.editor.core.iconpack.IconPack.Music
                ClipType.Image -> ly.img.editor.core.iconpack.IconPack.ImageOutline
                ClipType.Shape -> ly.img.editor.core.iconpack.IconPack.ShapesOutline
                ClipType.Sticker -> ly.img.editor.core.iconpack.IconPack.StickerEmojiOutline
                ClipType.Text -> ly.img.editor.core.iconpack.IconPack.TextFields
                ClipType.Video -> ly.img.editor.core.iconpack.IconPack.PlayBoxOutline
            }
        }
    Icon(icon, contentDescription = null, modifier = Modifier.requiredSize(16.dp))
}
