package ly.img.editor.configuration.memories.sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ly.img.editor.configuration.memories.iconPack.VolumeDown
import ly.img.editor.configuration.memories.iconPack.VolumeUp
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.sheet.SheetStyle
import ly.img.editor.core.sheet.SheetType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VolumeSheet(
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Title
        Text(
            text = "Volume",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
        )

        // Volume control
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = IconPack.VolumeDown,
                contentDescription = "Volume Down",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp),
            )

            Slider(
                value = volume,
                onValueChange = onVolumeChange,
                valueRange = 0f..1f,
                modifier = Modifier.weight(1f),
            )

            Icon(
                imageVector = IconPack.VolumeUp,
                contentDescription = "Volume Up",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp),
            )
        }

        // Volume percentage
        Text(
            text = "${(volume * 100).toInt()}%",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

fun createVolumeSheetType(
    initialVolume: Float,
    onVolumeChange: (Float) -> Unit,
): SheetType.Custom = SheetType.Custom(
    style = SheetStyle(),
    content = {
        var volume by remember { mutableFloatStateOf(initialVolume) }

        VolumeSheet(
            volume = volume,
            onVolumeChange = { newVolume ->
                volume = newVolume
                onVolumeChange(newVolume)
            },
        )
    },
)
