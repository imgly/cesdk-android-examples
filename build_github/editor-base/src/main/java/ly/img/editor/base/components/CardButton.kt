package ly.img.editor.base.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ly.img.editor.compose.material3.Card
import ly.img.editor.core.ui.UiDefaults

@Composable
fun CardButton(
    text: String,
    icon: ImageVector,
    modifier: Modifier,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        colors = UiDefaults.cardColorsExperimental,
    ) {
        Column(
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(imageVector = icon, contentDescription = null)
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 4.dp),
                textAlign = TextAlign.Center,
            )
        }
    }
}
