import android.net.Uri
import androidx.compose.runtime.Composable
import kotlinx.coroutines.delay
import ly.img.editor.Editor
import ly.img.editor.core.EditorScope
import ly.img.editor.core.UnstableEditorApi
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.sheet.SheetType
import ly.img.engine.AssetDefinition
import ly.img.engine.AssetPayload
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.DesignUnit
import ly.img.engine.Engine
import ly.img.engine.Font
import ly.img.engine.FontStyle
import ly.img.engine.FontWeight
import ly.img.engine.HorizontalAlignment
import ly.img.engine.SizeMode
import ly.img.engine.Typeface

private const val VARIABLE_TYPEFACE_SOURCE_ID = "my-variable-fonts"
private const val JOST_TYPEFACE_ASSET_ID = "ly.img.typeface.jost"

// highlight-android-generate-variants
// Jost is a variable font: one file covers all weights from 100 to 900.
private val JOST_VARIABLE_FONT_URI: Uri =
    Uri.parse("https://cdn.jsdelivr.net/fontsource/fonts/jost:vf@5/latin-wght-normal.woff2")

// The sub-family name CE.SDK shows for each of the nine standard weights.
private val WEIGHT_SUB_FAMILIES = linkedMapOf(
    FontWeight.THIN to "Thin",
    FontWeight.EXTRA_LIGHT to "Extra Light",
    FontWeight.LIGHT to "Light",
    FontWeight.NORMAL to "Regular",
    FontWeight.MEDIUM to "Medium",
    FontWeight.SEMI_BOLD to "Semi Bold",
    FontWeight.BOLD to "Bold",
    FontWeight.EXTRA_BOLD to "Extra Bold",
    FontWeight.HEAVY to "Heavy",
)

/**
 * Builds one [Font] entry per weight and style combination. Every entry points at the
 * same file, which is what marks the typeface as a variable font.
 */
private fun variableFontCombinations(
    uri: Uri,
    variantWeight: Boolean,
    variantItalic: Boolean,
): List<Font> {
    val weights = if (variantWeight) {
        WEIGHT_SUB_FAMILIES.keys.toList()
    } else {
        listOf(FontWeight.NORMAL)
    }
    val styles = if (variantItalic) {
        listOf(FontStyle.NORMAL, FontStyle.ITALIC)
    } else {
        listOf(FontStyle.NORMAL)
    }

    return styles.flatMap { style ->
        weights.map { weight ->
            val weightLabel = WEIGHT_SUB_FAMILIES.getValue(weight)
            Font(
                uri = uri,
                subFamily = if (style == FontStyle.ITALIC) "$weightLabel Italic" else weightLabel,
                weight = weight,
                style = style,
            )
        }
    }
}

private fun jostTypeface(): Typeface = Typeface(
    name = "Jost",
    fonts = variableFontCombinations(
        uri = JOST_VARIABLE_FONT_URI,
        variantWeight = true,
        // This file has no `ital` axis, so italic entries would render upright.
        variantItalic = false,
    ),
)
// highlight-android-generate-variants

// highlight-android-register-typeface
private fun jostTypefaceAsset() = AssetDefinition(
    id = JOST_TYPEFACE_ASSET_ID,
    label = mapOf("en" to "Jost"),
    tags = mapOf("en" to listOf("variable", "sans")),
    meta = mapOf("languages" to "latin"),
    payload = AssetPayload(
        typeface = jostTypeface(),
    ),
)

private fun createVariableFontSource(
    engine: Engine,
    sourceId: String = VARIABLE_TYPEFACE_SOURCE_ID,
) {
    if (sourceId in engine.asset.findAllSources()) {
        engine.asset.removeSource(sourceId = sourceId)
    }
    engine.asset.addLocalSource(
        sourceId = sourceId,
        supportedMimeTypes = emptyList(),
    )
    engine.asset.addAsset(
        sourceId = sourceId,
        asset = jostTypefaceAsset(),
    )
    engine.asset.assetSourceContentsChanged(sourceId = sourceId)
}
// highlight-android-register-typeface

// highlight-android-update-library

/**
 * Adds the variable font to the source the editor font sheet reads from, keeping the
 * built-in typefaces in place.
 */
private fun addVariableFontToEditorLibrary(engine: Engine) {
    val typefaceSourceId = AssetSourceType.Typeface.sourceId
    if (typefaceSourceId !in engine.asset.findAllSources()) {
        // The font sheet source only exists once an editor configuration has registered it.
        engine.asset.addLocalSource(
            sourceId = typefaceSourceId,
            supportedMimeTypes = emptyList(),
        )
    }
    engine.asset.addAsset(
        sourceId = typefaceSourceId,
        asset = jostTypefaceAsset(),
    )
    engine.asset.assetSourceContentsChanged(sourceId = typefaceSourceId)
}
// highlight-android-update-library

// highlight-android-font-sheet-filter
@OptIn(UnstableEditorApi::class)
private fun EditorScope.openVariableFontSheet(text: DesignBlock) {
    editorContext.eventHandler.send(
        EditorEvent.Sheet.Open(
            SheetType.Font(
                designBlock = text,
                fontFamilies = listOf(JOST_TYPEFACE_ASSET_ID),
            ),
        ),
    )
}
// highlight-android-font-sheet-filter

// highlight-android-apply-weights
private data class WeightSample(
    val weight: FontWeight,
    val label: String,
)

private val WEIGHT_SAMPLES = listOf(
    WeightSample(weight = FontWeight.THIN, label = "Thin 100"),
    WeightSample(weight = FontWeight.NORMAL, label = "Regular 400"),
    WeightSample(weight = FontWeight.BOLD, label = "Bold 700"),
    WeightSample(weight = FontWeight.HEAVY, label = "Heavy 900"),
)

/**
 * Creates one text block per sample weight. Every block renders from the same font file,
 * because the typeface resolves the weight to an axis value instead of another file.
 */
private fun createWeightSamples(
    engine: Engine,
    page: DesignBlock,
    typeface: Typeface,
): List<DesignBlock> = WEIGHT_SAMPLES.mapIndexed { index, sample ->
    val text = engine.block.create(blockType = DesignBlockType.Text)
    engine.block.appendChild(parent = page, child = text)
    engine.block.replaceText(block = text, text = sample.label)
    engine.block.setTextFontSize(block = text, fontSize = 56F)
    engine.block.setTextHorizontalAlignment(block = text, alignment = HorizontalAlignment.Center)
    engine.block.setWidthMode(block = text, mode = SizeMode.ABSOLUTE)
    engine.block.setWidth(block = text, value = 700F)
    engine.block.setHeightMode(block = text, mode = SizeMode.AUTO)
    engine.block.setPositionX(block = text, value = 50F)
    // 200 rather than 160 keeps the first sample clear of the headline's selection handles.
    engine.block.setPositionY(block = text, value = 200F + index * 105F)

    engine.block.setTypeface(block = text, typeface = typeface)
    engine.block.setTextFontWeight(block = text, fontWeight = sample.weight)
    text
}
// highlight-android-apply-weights

// highlight-android-switch-weight

/**
 * Switches an existing text block to another weight. The engine resolves the matching
 * variant from the typeface and renders it from the already loaded font file.
 */
private fun switchHeadlineWeight(
    engine: Engine,
    headline: DesignBlock,
): List<FontWeight> {
    engine.block.setTextFontWeight(block = headline, fontWeight = FontWeight.EXTRA_BOLD)

    // If the font file also provides an `ital` axis, styles switch the same way:
    // engine.block.setTextFontStyle(block = headline, fontStyle = FontStyle.ITALIC)

    return engine.block.getTextFontWeights(block = headline)
}
// highlight-android-switch-weight

/**
 * Demo scaffolding: builds the sample page and the headline the weight-switching snippet
 * operates on. Replace this with your own scene setup.
 */
private fun createSampleScene(
    engine: Engine,
    typeface: Typeface,
): Pair<DesignBlock, DesignBlock> {
    // A Pixel design unit also makes setTextFontSize interpret its value in pixels,
    // so the page size and the font sizes below share one unit.
    val scene = engine.scene.create(designUnit = DesignUnit.PIXEL)
    val page = engine.block.create(blockType = DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 800F)
    engine.block.setHeight(block = page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    val headline = engine.block.create(blockType = DesignBlockType.Text)
    engine.block.appendChild(parent = page, child = headline)
    engine.block.replaceText(block = headline, text = "Variable Fonts")
    engine.block.setTextFontSize(block = headline, fontSize = 64F)
    engine.block.setTextHorizontalAlignment(block = headline, alignment = HorizontalAlignment.Center)
    engine.block.setWidthMode(block = headline, mode = SizeMode.ABSOLUTE)
    engine.block.setWidth(block = headline, value = 700F)
    engine.block.setHeightMode(block = headline, mode = SizeMode.AUTO)
    engine.block.setPositionX(block = headline, value = 50F)
    engine.block.setPositionY(block = headline, value = 48F)
    engine.block.setTypeface(block = headline, typeface = typeface)

    return page to headline
}

data class VariableFontsResult(
    val sourceId: String,
    val generatedFontCount: Int,
    val sampleWeights: List<FontWeight>,
    val headlineWeights: List<FontWeight>,
)

fun variableFonts(
    engine: Engine,
    sourceId: String = VARIABLE_TYPEFACE_SOURCE_ID,
): VariableFontsResult {
    createVariableFontSource(engine = engine, sourceId = sourceId)

    val typeface = jostTypeface()
    val (page, headline) = createSampleScene(engine = engine, typeface = typeface)

    val samples = createWeightSamples(engine = engine, page = page, typeface = typeface)
    val headlineWeights = switchHeadlineWeight(engine = engine, headline = headline)

    return VariableFontsResult(
        sourceId = sourceId,
        generatedFontCount = typeface.fonts.size,
        sampleWeights = samples.flatMap { engine.block.getTextFontWeights(block = it) },
        headlineWeights = headlineWeights,
    )
}

@Composable
fun VariableFontsEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license,
        configuration = {
            EditorConfiguration.remember {
                onCreate = {
                    val engine = editorContext.engine
                    val typeface = jostTypeface()
                    val (page, headline) = createSampleScene(engine = engine, typeface = typeface)
                    createWeightSamples(engine = engine, page = page, typeface = typeface)
                    switchHeadlineWeight(engine = engine, headline = headline)
                    engine.scene.zoomToBlock(
                        block = page,
                        paddingLeft = 40F,
                        paddingTop = 40F,
                        paddingRight = 40F,
                        paddingBottom = 40F,
                    )
                }
                onLoaded = {
                    val engine = editorContext.engine
                    addVariableFontToEditorLibrary(engine = engine)

                    val headline = engine.block.findByType(DesignBlockType.Text).firstOrNull()
                    if (headline != null) {
                        engine.block.setSelected(block = headline, selected = true)
                        // Let the editor publish the new selection before opening the contextual font sheet.
                        delay(500)
                        openVariableFontSheet(text = headline)
                    }
                }
            }
        },
        onClose = onClose,
    )
}
