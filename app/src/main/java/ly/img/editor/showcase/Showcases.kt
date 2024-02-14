package ly.img.editor.showcase

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Showcases(navigateTo: (String) -> Unit) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("CE.SDK Showcases") })
    }) {
        Box(
            modifier =
                Modifier
                    .padding(
                        top = it.calculateTopPadding(),
                        start = 8.dp,
                        end = 8.dp,
                        bottom = it.calculateBottomPadding(),
                    )
                    .fillMaxHeight(),
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    ShowcaseItem(
                        title = "Default UI",
                        subtitle = "Multiple empty pages.",
                        onClick = {
                            navigateTo(Screen.CreativeUi.getRoute("default"))
                        },
                    )
                }
                item {
                    ShowcaseItem(
                        title = "Default Apparel UI",
                        subtitle = "Customize and export a print-ready design with a mobile apparel editor.",
                        onClick = {
                            navigateTo(Screen.ApparelUi.getRoute("default"))
                        },
                    )
                }
                item {
                    ShowcaseItem(
                        title = "Apparel UI",
                        subtitle = "Customize and export a print-ready design with a mobile apparel editor.",
                        onClick = {
                            navigateTo(Screen.ApparelUi.getRoute("apparel-ui-b-1"))
                        },
                    )
                }
                item {
                    ShowcaseItem(
                        title = "Default Post- & Greeting-Card UI",
                        subtitle = "Built to facilitate optimal card design, from changing accent colors to selecting fonts.",
                        onClick = {
                            navigateTo(Screen.PostcardUi.getRoute("default"))
                        },
                    )
                }
                item {
                    ShowcaseItem(
                        title = "Post- & Greeting-Card UI",
                        subtitle = "Built to facilitate optimal card design, from changing accent colors to selecting fonts.",
                        onClick = {
                            navigateTo(Screen.PostcardUi.getRoute("bonjour_paris"))
                        },
                    )
                }
            }
            if (BuildConfig.BUILD_NAME.isNotEmpty()) {
                Card(
                    modifier =
                        Modifier
                            .padding(bottom = 16.dp)
                            .align(Alignment.BottomCenter),
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = BuildConfig.BUILD_NAME,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowcaseItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier =
                Modifier
                    .padding(12.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
