package ly.img.editor.showcases.ui.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ly.img.editor.DebugMenu
import ly.img.editor.core.theme.LocalIsDarkTheme
import ly.img.editor.showcases.R
import ly.img.editor.showcases.Screen
import ly.img.editor.showcases.ShowcaseItem
import ly.img.editor.showcases.ShowcasesBuildConfig
import ly.img.editor.showcases.ui.component.CustomFunctionalityCard
import ly.img.editor.showcases.ui.component.versionFooterItem
import ly.img.editor.showcases.ui.modifier.linearGradientBackground
import ly.img.editor.showcases.ui.section.quickActionsSection

@Composable
fun ShowcasesScreen(
    items: List<ShowcaseItem>,
    onResult: (String, Any?) -> Unit,
    navigateTo: (String) -> Unit,
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
                quickActionsSection(
                    onResult = onResult,
                    navigateTo = navigateTo,
                )
                for (item in items) {
                    this@LazyColumn.showcaseItem(item, navigateTo)
                }
                versionFooterItem()
            }
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
                val onClick = rememberShowcaseItemOnClick(
                    item = element,
                    navigateTo = navigateTo,
                )
                ContentItem(item = element, onClick = onClick)
            }
        }

        is ShowcaseItem.CarouselContent -> {
            item(key = element.key) {
                val onClick = rememberShowcaseItemOnClick(
                    item = element,
                    navigateTo = navigateTo,
                )
                CarouselItem(item = element, onClick = onClick)
            }
        }

        is ShowcaseItem.CustomFunctionality -> {
            item(key = element.key) {
                val onClick = rememberShowcaseItemOnClick(
                    item = element,
                    navigateTo = navigateTo,
                )
                CustomFunctionalityCard(item = element, onClick = onClick)
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
    onClick: () -> Unit,
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
            .clickable(onClick = onClick),
    )
}

@Composable
fun CarouselItem(
    item: ShowcaseItem.CarouselContent,
    onClick: () -> Unit,
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
            .clickable(onClick = onClick)
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
private fun rememberShowcaseItemOnClick(
    item: ShowcaseItem.Clickable,
    navigateTo: (String) -> Unit,
): () -> Unit {
    val context = LocalContext.current
    var clickAction by remember { mutableStateOf(item.clickAction) }
    var showApiKeyDialog by remember { mutableStateOf(false) }
    var image by remember { mutableStateOf<Uri?>(null) }
    var scene by remember { mutableStateOf<Uri?>(null) }
    var gatewayApiKey by remember { mutableStateOf("") }
    lateinit var handleClick: () -> Unit

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
    ) { sceneUri ->
        sceneUri ?: return@rememberLauncherForActivityResult
        context.contentResolver.takePersistableUriPermission(sceneUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        scene = sceneUri
        clickAction = clickAction.copy(requestScene = false)
        handleClick()
    }
    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { imageUri ->
        imageUri ?: return@rememberLauncherForActivityResult
        image = imageUri
        clickAction = clickAction.copy(requestImage = false)
        handleClick()
    }

    handleClick = {
        when {
            clickAction.requestApiKey -> {
                showApiKeyDialog = true
            }
            clickAction.requestImage -> {
                imageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            clickAction.requestScene -> {
                fileLauncher.launch(arrayOf("text/plain", "application/force-download", "application/octet-stream"))
            }
            else -> {
                val route = clickAction.destination.getRoute(
                    "scene" to (clickAction.sceneId ?: scene),
                    "image" to image,
                    "gatewayApiKey" to gatewayApiKey,
                )
                navigateTo(route)
            }
        }
    }

    if (showApiKeyDialog) {
        ApiKeyDialog(
            onDismissRequest = { showApiKeyDialog = false },
            onResult = {
                gatewayApiKey = it
                clickAction = clickAction.copy(requestApiKey = false)
                handleClick()
            },
        )
    }
    return {
        handleClick()
    }
}

@Composable
private fun ApiKeyDialog(
    onDismissRequest: () -> Unit,
    onResult: (String) -> Unit,
) {
    var input by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = stringResource(
                    R.string.ly_img_showcases_gateway_dialog_title,
                ),
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = AnnotatedString(
                        stringResource(
                            R.string.ly_img_showcases_gateway_dialog_text,
                        ),
                    ),
                )
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it.trim() },
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = input.isNotEmpty(),
                onClick = {
                    onDismissRequest()
                    onResult(input)
                },
            ) {
                Text(stringResource(R.string.ly_img_showcases_gateway_dialog_button_confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
            ) {
                Text(stringResource(R.string.ly_img_showcases_gateway_dialog_button_dismiss))
            }
        },
    )
}

@Composable
@Preview
fun ContentItemPreview() {
    val item = ShowcaseItem.Content(
        thumbnailRes = R.drawable.thumbnail_apparel_ui_b_2,
        clickAction = ShowcaseItem.ClickAction(destination = Screen.ApparelUi),
    )
    ContentItem(item = item, onClick = {})
}
