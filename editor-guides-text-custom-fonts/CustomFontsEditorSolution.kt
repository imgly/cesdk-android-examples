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
import ly.img.engine.Engine
import ly.img.engine.FindAssetsQuery
import ly.img.engine.Font
import ly.img.engine.FontStyle
import ly.img.engine.FontWeight
import ly.img.engine.SizeMode
import ly.img.engine.Typeface

// highlight-android-typeface-structure
private const val CUSTOM_TYPEFACE_SOURCE_ID = "my-custom-typefaces"
private const val BRAND_TYPEFACE_ASSET_ID = "ly.img.typeface.brand_sans"
private const val BRAND_SANS_REGULAR_FILE = "BrandSans-Regular.ttf"
private const val BRAND_SANS_BOLD_FILE = "BrandSans-Bold.ttf"

private fun brandFontUri(fileName: String): Uri = "file:///android_asset"
    .let(Uri::parse)
    .buildUpon()
    .appendEncodedPath("custom-fonts")
    .appendEncodedPath(fileName)
    .build()

private fun brandTypeface(): Typeface = Typeface(
    name = "Brand Sans",
    fonts = listOf(
        Font(
            uri = brandFontUri(BRAND_SANS_REGULAR_FILE),
            subFamily = "Regular",
            weight = FontWeight.NORMAL,
            style = FontStyle.NORMAL,
        ),
        Font(
            uri = brandFontUri(BRAND_SANS_BOLD_FILE),
            subFamily = "Bold",
            weight = FontWeight.BOLD,
            style = FontStyle.NORMAL,
        ),
    ),
)
// highlight-android-typeface-structure

// highlight-android-typeface-asset
private fun brandTypefaceAsset() = AssetDefinition(
    id = BRAND_TYPEFACE_ASSET_ID,
    label = mapOf("en" to "Brand Sans"),
    tags = mapOf("en" to listOf("brand", "sans", "headline")),
    payload = AssetPayload(
        typeface = brandTypeface(),
    ),
)
// highlight-android-typeface-asset

// highlight-android-register-source
private fun createCustomTypefaceSource(
    engine: Engine,
    sourceId: String = CUSTOM_TYPEFACE_SOURCE_ID,
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
        asset = brandTypefaceAsset(),
    )
    engine.asset.assetSourceContentsChanged(sourceId = sourceId)
}
// highlight-android-register-source

// highlight-android-update-library
private fun replaceEditorTypefaceSource(engine: Engine) {
    createCustomTypefaceSource(
        engine = engine,
        sourceId = AssetSourceType.Typeface.sourceId,
    )
}
// highlight-android-update-library

// highlight-android-font-sheet-filter
@OptIn(UnstableEditorApi::class)
private fun EditorScope.openBrandTypefaceSheet(text: DesignBlock) {
    editorContext.eventHandler.send(
        EditorEvent.Sheet.Open(
            SheetType.Font(
                designBlock = text,
                fontFamilies = listOf(BRAND_TYPEFACE_ASSET_ID),
            ),
        ),
    )
}
// highlight-android-font-sheet-filter

// highlight-android-apply-font
private fun applyBrandTypefaceToText(
    engine: Engine,
    text: DesignBlock,
    typeface: Typeface,
) {
    val regularFont = typeface.fonts.firstOrNull {
        it.subFamily == "Regular" &&
            it.weight == FontWeight.NORMAL &&
            it.style == FontStyle.NORMAL
    } ?: error("Brand Sans Regular font is missing.")

    engine.block.setFont(
        block = text,
        fontFileUri = regularFont.uri,
        typeface = typeface,
    )
    engine.block.setTypeface(
        block = text,
        typeface = typeface,
        from = 0,
        to = 5,
    )

    val currentTypeface = engine.block.getTypeface(block = text)
    val rangeTypefaces = engine.block.getTypefaces(block = text, from = 0, to = 5)

    check(currentTypeface.name == typeface.name)
    check(rangeTypefaces.any { it.name == typeface.name })
}
// highlight-android-apply-font

data class CustomFontsResult(
    val sourceId: String,
    val queriedTypefaceName: String,
    val appliedTypefaceName: String,
    val rangeTypefaceNames: List<String>,
)

suspend fun customFonts(
    engine: Engine,
    sourceId: String = CUSTOM_TYPEFACE_SOURCE_ID,
): CustomFontsResult {
    createCustomTypefaceSource(
        engine = engine,
        sourceId = sourceId,
    )

    val typeface = brandTypeface()
    val scene = engine.scene.create()
    val page = engine.block.create(blockType = DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 1080F)
    engine.block.setHeight(block = page, value = 1080F)
    engine.block.appendChild(parent = scene, child = page)

    val text = engine.block.create(blockType = DesignBlockType.Text)
    engine.block.replaceText(block = text, text = "Brand fonts")
    engine.block.setWidthMode(block = text, mode = SizeMode.AUTO)
    engine.block.setHeightMode(block = text, mode = SizeMode.AUTO)
    engine.block.setTextFontSize(block = text, fontSize = 36F)
    engine.block.setPositionX(block = text, value = 240F)
    engine.block.setPositionY(block = text, value = 500F)
    engine.block.appendChild(parent = page, child = text)

    applyBrandTypefaceToText(
        engine = engine,
        text = text,
        typeface = typeface,
    )
    engine.block.setSelected(block = text, selected = true)
    engine.scene.zoomToBlock(
        block = page,
        paddingLeft = 80F,
        paddingTop = 80F,
        paddingRight = 80F,
        paddingBottom = 80F,
    )

    val queriedTypeface = engine.asset.findAssets(
        sourceId = sourceId,
        query = FindAssetsQuery(query = "Brand", page = 0, perPage = 10),
    ).assets.firstNotNullOf { it.payload.typeface }

    return CustomFontsResult(
        sourceId = sourceId,
        queriedTypefaceName = queriedTypeface.name,
        appliedTypefaceName = engine.block.getTypeface(block = text).name,
        rangeTypefaceNames = engine.block
            .getTypefaces(block = text, from = 0, to = 5)
            .map { it.name },
    )
}

// highlight-android-editor-setup
@Composable
fun CustomFontsEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license,
        configuration = {
            EditorConfiguration.remember {
                onCreate = {
                    val engine = editorContext.engine
                    replaceEditorTypefaceSource(
                        engine = engine,
                    )
                    val typeface = brandTypeface()
                    val scene = engine.scene.create()
                    val page = engine.block.create(blockType = DesignBlockType.Page)
                    engine.block.setWidth(block = page, value = 1080F)
                    engine.block.setHeight(block = page, value = 1080F)
                    engine.block.appendChild(parent = scene, child = page)

                    val text = engine.block.create(blockType = DesignBlockType.Text)
                    engine.block.replaceText(block = text, text = "Brand fonts")
                    engine.block.setWidthMode(block = text, mode = SizeMode.AUTO)
                    engine.block.setHeightMode(block = text, mode = SizeMode.AUTO)
                    engine.block.setTextFontSize(block = text, fontSize = 36F)
                    engine.block.setPositionX(block = text, value = 240F)
                    engine.block.setPositionY(block = text, value = 500F)
                    engine.block.appendChild(parent = page, child = text)

                    applyBrandTypefaceToText(
                        engine = engine,
                        text = text,
                        typeface = typeface,
                    )
                    engine.block.setSelected(block = text, selected = true)
                    engine.scene.zoomToBlock(
                        block = page,
                        paddingLeft = 80F,
                        paddingTop = 80F,
                        paddingRight = 80F,
                        paddingBottom = 80F,
                    )
                }
                onLoaded = {
                    val text = editorContext.engine.block.findByType(DesignBlockType.Text).firstOrNull()
                    if (text != null) {
                        editorContext.engine.block.setSelected(block = text, selected = true)
                        // Let the editor publish the new selection before opening the contextual font sheet.
                        delay(500)
                        openBrandTypefaceSheet(text = text)
                    }
                }
            }
        },
        onClose = onClose,
    )
}
// highlight-android-editor-setup
