package ly.img.editor.configuration.video.iconPack

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Icon pack of the starter kit to access all the icons.
 */
object IconPack

@Composable
internal fun ImageVector.IconPreview() = Icon(this, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
