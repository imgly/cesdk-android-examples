package ly.img.editor.examples

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(onClick: (Destination) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
    ) {
        if (Destination.starterKits.isNotEmpty()) {
            item {
                Header(title = "Starter Kits")
            }
            items(
                count = Destination.starterKits.size,
                key = { index -> Destination.starterKits[index].route },
            ) { index ->
                val destination = Destination.starterKits[index]
                Item(
                    title = destination.title,
                    onClick = { onClick(destination) },
                )
            }
        }
        if (Destination.guides.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Header(title = "Guides")
            }
            items(
                count = Destination.guides.size,
                key = { index -> Destination.guides[index].route },
            ) { index ->
                val destination = Destination.guides[index]
                Item(
                    title = destination.title,
                    onClick = { onClick(destination) },
                )
            }
        }
    }
}

@Composable
private fun Item(
    title: String,
    onClick: () -> Unit,
) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        text = title,
        style = MaterialTheme.typography.bodyLarge,
    )
    Divider()
}

@Composable
private fun Header(title: String) {
    Text(
        modifier = Modifier.padding(horizontal = 16.dp),
        text = title,
        style = MaterialTheme.typography.headlineSmall,
    )
    Spacer(modifier = Modifier.height(16.dp))
}
