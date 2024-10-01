package ly.img.editor.showcase

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
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
import ly.img.editor.version.details.VersionDetails

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
            val launcher =
                rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { imageUri ->
                    imageUri?.let {
                        navigateTo(Screen.PhotoUi.getRoute(imageUri.toString()))
                    }
                }
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (ShowcasesBuildConfig.BUILD_NAME.isNotEmpty()) {
                    item {
                        VersionDetails(
                            versionInfo = ShowcasesBuildConfig.BUILD_NAME,
                            branchName = ShowcasesBuildConfig.BRANCH_NAME,
                            versionCode = ShowcasesBuildConfig.VERSION_CODE,
                        )
                    }
                }
                item {
                    ShowcaseItem(
                        title = "Photo UI",
                        subtitle = "Customize any image with a mobile photo editor.",
                        onClick = {
                            launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                    )
                }
                item {
                    ShowcaseItem(
                        title = "Default Design UI",
                        subtitle = "An empty page.",
                        onClick = {
                            navigateTo(Screen.DesignUi.getRoute("default"))
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
                item {
                    ShowcaseItem(
                        title = "Default Video UI",
                        subtitle = "Loads empty video scene.",
                        onClick = {
                            navigateTo(Screen.VideoUi.getRoute("default"))
                        },
                    )
                }
                item {
                    ShowcaseItem(
                        title = "Video UI",
                        subtitle = "Loads a custom video scene with additional asset sources.",
                        onClick = {
                            navigateTo(Screen.VideoUi.getRoute("red-dot"))
                        },
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
            modifier = Modifier.padding(12.dp),
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
