package ly.img.cesdk.showcase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Showcases(navigateTo: (String) -> Unit) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("CE.SDK Showcases") })
    }) {
        LazyColumn(
            modifier = Modifier.padding(vertical = it.calculateTopPadding(), horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                ShowcaseItem(
                    title = "Apparel UI",
                    subtitle = "Customize and export a print-ready design with a mobile apparel editor.",
                    onClick = {
                        navigateTo(Screen.ApparelUi.getRoute("apparel-ui-b-1"))
                    }
                )
            }
            item {
                ShowcaseItem(
                    title = "Post- & Greeting-Card UI",
                    subtitle = "Built to facilitate optimal card design, from changing accent colors to selecting fonts.",
                    onClick = {
                        navigateTo(Screen.PostcardUi.getRoute("bonjour_paris"))
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowcaseItem(title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}