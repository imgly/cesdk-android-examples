package ly.img.editor.examples

import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.editor.defaultBaseUri
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode
import java.io.File
import ly.img.engine.Color as EngineColor

// highlight-android-record
private data class AutomationJob(
    val fileStem: String,
    val headline: String,
    val subline: String,
    val cta: String,
    val heroImageUri: String,
)
// highlight-android-record

data class AutomationResult(
    val variableKeys: List<String>,
    val tokenizedBlockNames: List<String>,
    val exportedFiles: List<File>,
)

private sealed interface AutomationUiState {
    object Loading : AutomationUiState

    data class Success(
        val result: AutomationResult,
    ) : AutomationUiState

    data class Error(
        val message: String,
    ) : AutomationUiState
}

@Composable
fun AutomateWorkflowsScreen(license: String) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf<AutomationUiState>(AutomationUiState.Loading) }

    LaunchedEffect(context, license) {
        uiState = runCatching { runAutomationWorkflow(context, license) }
            .fold(
                onSuccess = { AutomationUiState.Success(it) },
                onFailure = { AutomationUiState.Error(it.message ?: "Unknown automation error.") },
            )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        when (val state = uiState) {
            AutomationUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is AutomationUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Automation failed",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }

            is AutomationUiState.Success -> {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = "Automate Workflows",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Text(
                        text = "Variable store: ${state.result.variableKeys.joinToString()}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = "Tokenized blocks: ${state.result.tokenizedBlockNames.joinToString()}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    state.result.exportedFiles.forEach { file ->
                        val bitmap = remember(file.absolutePath) {
                            BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
                        }
                        Text(
                            text = file.name,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap,
                                contentDescription = file.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(4f / 5f),
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

suspend fun runAutomationWorkflow(
    context: Context,
    license: String,
): AutomationResult = withContext(Dispatchers.Main) {
    val application = context.applicationContext as Application
    Engine.init(application)
    val engine = Engine.getInstance(id = "ly.img.engine.automateWorkflows")

    // highlight-android-start
    engine.start(
        license = license,
        userId = "automation-guide",
    )
    engine.bindOffscreen(width = 1080, height = 1350)
    val existingAssetSources = engine.asset.findAllSources().toSet()
    listOf(
        "ly.img.typeface",
    ).filterNot(existingAssetSources::contains)
        .forEach { assetSource ->
            engine.asset.addLocalSourceFromJSON(
                contentUri = defaultBaseUri.buildUpon()
                    .appendPath(assetSource)
                    .appendPath("content.json")
                    .build(),
            )
        }
    // highlight-android-start

    try {
        val outputDirectory = withContext(Dispatchers.IO) {
            File(context.cacheDir, "automate-workflows").apply {
                mkdirs()
                listFiles()?.forEach(File::delete)
            }
        }
        val templateScene = createTemplateScene(engine)
        val tokenizedBlockNames = discoverTokenizedBlocks(engine)
        val jobs = listOf(
            AutomationJob(
                fileStem = "summer-sale",
                headline = "Summer Sale",
                subline = "Save 25% on the launch collection.",
                cta = "Shop Now",
                heroImageUri = "https://img.ly/static/ubq_samples/sample_1.jpg",
            ),
            AutomationJob(
                fileStem = "autumn-launch",
                headline = "Autumn Launch",
                subline = "New arrivals for cozy desk setups.",
                cta = "Explore",
                heroImageUri = "https://img.ly/static/ubq_samples/sample_4.jpg",
            ),
        )

        // highlight-android-batch
        val exportedFiles = jobs.map { job ->
            exportAutomationJob(
                engine = engine,
                templateScene = templateScene,
                job = job,
                outputDirectory = outputDirectory,
            )
        }
        // highlight-android-batch

        AutomationResult(
            variableKeys = engine.variable.findAll().sorted(),
            tokenizedBlockNames = tokenizedBlockNames,
            exportedFiles = exportedFiles,
        )
    } finally {
        engine.stop()
    }
}

private suspend fun createTemplateScene(engine: Engine): String {
    // highlight-android-template
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setWidth(page, value = 1080F)
    engine.block.setHeight(page, value = 1350F)

    val background = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(background, shape = engine.block.createShape(ShapeType.Rect))
    val backgroundFill = engine.block.createFill(FillType.Color)
    engine.block.setColor(
        block = backgroundFill,
        property = "fill/color/value",
        value = EngineColor.fromRGBA(r = 0.96F, g = 0.94F, b = 0.90F, a = 1F),
    )
    engine.block.setFill(background, fill = backgroundFill)
    engine.block.setWidth(background, value = 1080F)
    engine.block.setHeight(background, value = 1350F)
    engine.block.appendChild(parent = page, child = background)

    val heroImage = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(heroImage, name = "hero-image")
    engine.block.setShape(heroImage, shape = engine.block.createShape(ShapeType.Rect))
    val heroFill = engine.block.createFill(FillType.Image)
    engine.block.setString(
        block = heroFill,
        property = "fill/image/imageFileURI",
        value = "https://img.ly/static/ubq_samples/sample_2.jpg",
    )
    engine.block.setFill(heroImage, fill = heroFill)
    engine.block.setWidth(heroImage, value = 860F)
    engine.block.setHeight(heroImage, value = 720F)
    engine.block.setPositionX(heroImage, value = 110F)
    engine.block.setPositionY(heroImage, value = 100F)
    engine.block.appendChild(parent = page, child = heroImage)

    val copyPanel = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(copyPanel, shape = engine.block.createShape(ShapeType.Rect))
    val copyPanelFill = engine.block.createFill(FillType.Color)
    engine.block.setColor(
        block = copyPanelFill,
        property = "fill/color/value",
        value = EngineColor.fromRGBA(r = 1F, g = 1F, b = 1F, a = 0.92F),
    )
    engine.block.setFill(copyPanel, fill = copyPanelFill)
    engine.block.setWidth(copyPanel, value = 860F)
    engine.block.setHeight(copyPanel, value = 360F)
    engine.block.setPositionX(copyPanel, value = 110F)
    engine.block.setPositionY(copyPanel, value = 860F)
    engine.block.appendChild(parent = page, child = copyPanel)

    val headline = engine.block.create(DesignBlockType.Text)
    engine.block.setName(headline, name = "headline-copy")
    engine.block.setString(headline, property = "text/text", value = "{{headline}}")
    engine.block.setTextFontSize(headline, fontSize = 14F)
    engine.block.setTextColor(
        headline,
        color = EngineColor.fromRGBA(r = 0.12F, g = 0.10F, b = 0.15F, a = 1F),
    )
    engine.block.setWidth(headline, value = 700F)
    engine.block.setWidthMode(headline, mode = SizeMode.ABSOLUTE)
    engine.block.setHeightMode(headline, mode = SizeMode.AUTO)
    engine.block.setBoolean(headline, property = "text/clipLinesOutsideOfFrame", value = false)
    engine.block.setPositionX(headline, value = 160F)
    engine.block.setPositionY(headline, value = 915F)
    engine.block.appendChild(parent = page, child = headline)

    val subline = engine.block.create(DesignBlockType.Text)
    engine.block.setName(subline, name = "subline-copy")
    engine.block.setString(subline, property = "text/text", value = "{{subline}}")
    engine.block.setTextFontSize(subline, fontSize = 8F)
    engine.block.setTextColor(
        subline,
        color = EngineColor.fromRGBA(r = 0.28F, g = 0.24F, b = 0.32F, a = 1F),
    )
    engine.block.setWidth(subline, value = 700F)
    engine.block.setWidthMode(subline, mode = SizeMode.ABSOLUTE)
    engine.block.setHeightMode(subline, mode = SizeMode.AUTO)
    engine.block.setBoolean(subline, property = "text/clipLinesOutsideOfFrame", value = false)
    engine.block.setPositionX(subline, value = 160F)
    engine.block.setPositionY(subline, value = 1000F)
    engine.block.appendChild(parent = page, child = subline)

    val cta = engine.block.create(DesignBlockType.Text)
    engine.block.setName(cta, name = "cta-copy")
    engine.block.setString(cta, property = "text/text", value = "{{cta}}")
    engine.block.setTextFontSize(cta, fontSize = 9F)
    engine.block.setTextColor(
        cta,
        color = EngineColor.fromRGBA(r = 0.16F, g = 0.29F, b = 0.82F, a = 1F),
    )
    engine.block.setWidth(cta, value = 700F)
    engine.block.setWidthMode(cta, mode = SizeMode.ABSOLUTE)
    engine.block.setHeightMode(cta, mode = SizeMode.AUTO)
    engine.block.setBoolean(cta, property = "text/clipLinesOutsideOfFrame", value = false)
    engine.block.setPositionX(cta, value = 160F)
    engine.block.setPositionY(cta, value = 1090F)
    engine.block.appendChild(parent = page, child = cta)

    val serializedTemplate = engine.scene.saveToString(scene = scene)
    // highlight-android-template

    return serializedTemplate
}

private fun discoverTokenizedBlocks(engine: Engine): List<String> {
    // highlight-android-discover-slots
    return engine.block.findAll()
        .filter { block -> engine.block.referencesAnyVariables(block) }
        .map { block -> engine.block.getName(block) }
        .filter(String::isNotBlank)
        .sorted()
    // highlight-android-discover-slots
}

private suspend fun exportAutomationJob(
    engine: Engine,
    templateScene: String,
    job: AutomationJob,
    outputDirectory: File,
): File {
    // highlight-android-apply-data
    engine.scene.load(
        scene = templateScene,
        waitForResources = true,
    )
    engine.variable.set(key = "headline", value = job.headline)
    engine.variable.set(key = "subline", value = job.subline)
    engine.variable.set(key = "cta", value = job.cta)

    val heroImage = engine.block.findByName(name = "hero-image").first()
    val heroFill = engine.block.getFill(heroImage)
    engine.block.setString(
        block = heroFill,
        property = "fill/image/imageFileURI",
        value = job.heroImageUri,
    )
    engine.block.resetCrop(heroImage)
    // highlight-android-apply-data

    val page = requireNotNull(engine.scene.getCurrentPage()) { "Expected a page in the automation template." }

    // highlight-android-export
    val exportData = engine.block.export(
        block = page,
        mimeType = MimeType.PNG,
    )
    val outputFile = File(outputDirectory, "${job.fileStem}.png")
    withContext(Dispatchers.IO) {
        outputFile.outputStream().channel.use { channel ->
            while (exportData.hasRemaining()) {
                channel.write(exportData)
            }
        }
    }
    // highlight-android-export

    return outputFile
}
