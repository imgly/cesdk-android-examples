package ly.img.cesdk.core.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import ly.img.cesdk.core.UiDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardButton(text: String, icon: ImageVector, modifier: Modifier, enabled: Boolean, onClick: () -> Unit) {
    Card(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        colors = UiDefaults.cardColors,
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp, bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(imageVector = icon, contentDescription = null)
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}