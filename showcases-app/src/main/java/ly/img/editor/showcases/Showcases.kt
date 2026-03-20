package ly.img.editor.showcases

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ly.img.editor.DebugMenu
import ly.img.editor.core.theme.LocalIsDarkTheme
import ly.img.editor.showcases.ui.component.CustomFunctionalityCard
import ly.img.editor.showcases.ui.component.versionFooterItem
import ly.img.editor.showcases.ui.modifier.linearGradientBackground
import ly.img.editor.showcases.ui.sections.quickActionsSection

@Composable
fun Showcases(
    showcasesViewModel: ShowcasesViewModel = viewModel(),
    navigateTo: (String) -> Unit,
    navController: NavHostController,
) {
    Scaffold(
        topBar = {
            Surface(
                tonalElevation = 2.dp,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_logo),
                        contentDescription = null,
                    )
                }
            }
        },
    ) { paddingValues ->
        val items = remember { showcasesViewModel.getItems(ShowcasesViewModel.CATALOG_COLUMNS_SIZE) }
        Box(
            modifier = Modifier
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding(),
                    start = 0.dp,
                    end = 0.dp,
                )
                .fillMaxHeight(),
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(top = 8.dp),
            ) {
                if (ShowcasesBuildConfig.BUILD_NAME.isNotEmpty()) {
                    item(key = "updateBlock") {
                        DebugMenu(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            versionInfo = ShowcasesBuildConfig.BUILD_NAME,
                            branchName = ShowcasesBuildConfig.BRANCH_NAME,
                            commitId = ShowcasesBuildConfig.COMMIT_ID,
                        )
                    }
                }
                quickActionsSection(navController)
                for (item in items) {
                    this@LazyColumn.showcaseItem(item, navigateTo)
                }
                versionFooterItem()
            }
        }
    }
}

/**
 * Handles the click actions for showcase items with consistent behavior
 * @param actionScreen The screen to navigate to
 * @param uri The URI to use (can be null)
 * @param clickAction The type of click action to perform
 * @param navigateTo Function to navigate to a specific route
 * @param fileLauncher Optional launcher for file picking (only needed for PICK_SCENE actions)
 * @param imageLauncher Launcher for image picking
 */
private fun handleShowcaseItemClick(
    actionScreen: Screen,
    uri: String?,
    clickAction: ShowcaseItem.CarouselContent.ClickAction,
    navigateTo: (String) -> Unit,
    fileLauncher: (() -> Unit)? = null,
    imageLauncher: () -> Unit,
) {
    when {
        clickAction == ShowcaseItem.CarouselContent.ClickAction.OPEN_SCENE && uri != null -> {
            navigateTo(actionScreen.getRoute("scene" to uri))
        }

        clickAction == ShowcaseItem.CarouselContent.ClickAction.OPEN_SCENE && uri == null -> {
            // Direct navigation for screens that don't need scene parameters
            navigateTo(actionScreen.routeScheme)
        }

        clickAction == ShowcaseItem.CarouselContent.ClickAction.PICK_SCENE -> {
            fileLauncher?.invoke()
        }

        clickAction == ShowcaseItem.CarouselContent.ClickAction.PICK_IMAGE -> {
            imageLauncher.invoke()
        }

        else -> {
            // Default to direct navigation
            navigateTo(actionScreen.routeScheme)
        }
    }
}

fun LazyListScope.showcaseItem(
    element: ShowcaseItem,
    navigateTo: (String) -> Unit,
) {
    when (element) {
        is ShowcaseItem.Header -> {
            item(key = element.key) {
                HeaderItem(element)
            }

            item(key = element.key + "HeaderContent") {
                LazyRow(
                    contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    for (item in element.items) {
                        this@LazyRow.showcaseItem(item, navigateTo)
                    }
                }
            }
        }

        is ShowcaseItem.Content -> {
            item(key = element.key) {
                ContentItem(element) { uri -> navigateTo(element.actionScreen.getRoute("scene" to uri)) }
            }
        }

        is ShowcaseItem.CarouselContent -> {
            item(key = element.key) {
                val context = LocalContext.current
                val fileLauncher = rememberLauncherForActivityResult(
                    object : ActivityResultContracts.OpenDocument() {
                        override fun createIntent(
                            context: Context,
                            input: Array<String>,
                        ) = super.createIntent(context, input).also {
                            it.addCategory(Intent.CATEGORY_OPENABLE)
                            it.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                            it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                    },
                ) { uri ->
                    uri ?: return@rememberLauncherForActivityResult
                    context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    navigateTo(element.actionScreen.getRoute("scene" to uri))
                }
                val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { imageUri ->
                    imageUri?.let { uri ->
                        navigateTo(element.actionScreen.getRoute("scene" to element.actionScene, "image" to uri))
                    }
                }
                CarouselItem(element) { uri, clickAction ->
                    handleShowcaseItemClick(
                        actionScreen = element.actionScreen,
                        uri = uri,
                        clickAction = clickAction,
                        navigateTo = navigateTo,
                        fileLauncher = {
                            fileLauncher.launch(arrayOf("text/plain", "application/force-download", "application/octet-stream"))
                        },
                        imageLauncher = {
                            imageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                    )
                }
            }
        }

        is ShowcaseItem.CustomFunctionality -> {
            item(key = element.key) {
                val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { imageUri ->
                    imageUri?.let { uri ->
                        navigateTo(element.actionScreen.getRoute("scene" to element.actionScene, "image" to uri))
                    }
                }
                CustomFunctionalityCard(element) { uri, clickAction ->
                    handleShowcaseItemClick(
                        actionScreen = element.actionScreen,
                        uri = uri,
                        clickAction = clickAction,
                        navigateTo = navigateTo,
                        imageLauncher = {
                            imageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderItem(item: ShowcaseItem.Header) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(id = item.title),
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 24.sp,
        )
    }
}

@Composable
fun ContentItem(
    item: ShowcaseItem.Content,
    onClick: (String) -> Unit,
) {
    AsyncImage(
        model = ImageRequest
            .Builder(LocalContext.current)
            .apply {
                data(item.thumbnailRes)
                placeholder(item.thumbnailRes)
            }.build(),
        contentDescription = null,
        modifier = Modifier
            .height(152.dp)
            .aspectRatio(item.thumbnailAspectRatio)
            .linearGradientBackground(
                height = 152.dp,
                shape = MaterialTheme.shapes.small,
            )
            .padding(4.dp)
            .clip(shape = MaterialTheme.shapes.extraSmall)
            .clickable { onClick(item.actionScene) },
    )
}

@Composable
fun CarouselItem(
    item: ShowcaseItem.CarouselContent,
    onClick: (String?, clickAction: ShowcaseItem.CarouselContent.ClickAction) -> Unit,
) {
    val isDarkTheme = LocalIsDarkTheme.current
    Column(
        modifier = Modifier
            .linearGradientBackground(
                height = 152.dp,
                shape = MaterialTheme.shapes.small,
            )
            .clip(shape = MaterialTheme.shapes.extraSmall)
            .size(152.dp)
            .clickable { onClick(item.actionScene, item.clickAction) }
            .drawWithContent {
                drawContent()
                if (item.hasDotLine) {
                    drawPath(
                        path = Path().apply {
                            val padding = 12.dp.toPx()
                            val cornerRadius = 4.dp.toPx()
                            addRoundRect(
                                RoundRect(
                                    left = padding,
                                    top = padding,
                                    right = size.width - padding,
                                    bottom = size.height - padding,
                                    cornerRadius = CornerRadius(x = cornerRadius, y = cornerRadius),
                                ),
                            )
                        },
                        color = if (isDarkTheme) Color.White else Color(0x7F000000),
                        style = Stroke(
                            width = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.dp.toPx(), 2.dp.toPx()), 0f),
                        ),
                    )
                }
            },
    ) {
        AsyncImage(
            model = item.iconRes,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp)
                .size(88.dp, 80.dp),
        )
        Text(
            text = stringResource(item.label),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(4.dp))
        item.sublabel?.let {
            Text(
                text = stringResource(item.sublabel),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .alpha(0.5f),
            )
        }
    }
}

@Composable
@Preview
fun ContentItemPreview() {
    val item = ShowcaseItem.Content(
        thumbnailRes = R.drawable.thumbnail_apparel_ui_b_2,
        actionScreen = Screen.ApparelUi,
        actionScene = "",
    )
    ContentItem(item = item, onClick = {})
}
