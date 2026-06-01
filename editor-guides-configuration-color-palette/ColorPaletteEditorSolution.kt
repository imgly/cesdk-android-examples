import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import ly.img.editor.Editor
import ly.img.editor.core.component.InspectorBar
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberFillStroke
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.engine.AssetColor
import ly.img.engine.AssetDefinition
import ly.img.engine.AssetPayload
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import androidx.compose.ui.graphics.Color as ComposeColor

// highlight-android-defining-color-assets
private const val BRAND_COLOR_SOURCE_ID = "my-brand-colors"
private const val BRAND_CORAL_ID = "brand-coral"

private data class BrandColorAsset(
    val definition: AssetDefinition,
    val paletteColor: ComposeColor?,
)

private fun brandColorAssets() = listOf(
    BrandColorAsset(
        definition = AssetDefinition(
            id = "brand-blue",
            label = mapOf("en" to "Brand Blue"),
            tags = mapOf("en" to listOf("brand", "blue", "primary")),
            payload = AssetPayload(
                color = AssetColor.RGB(r = 0.2F, g = 0.4F, b = 0.8F),
            ),
        ),
        paletteColor = ComposeColor(red = 0.2F, green = 0.4F, blue = 0.8F),
    ),
    BrandColorAsset(
        definition = AssetDefinition(
            id = BRAND_CORAL_ID,
            label = mapOf("en" to "Brand Coral"),
            tags = mapOf("en" to listOf("brand", "coral", "secondary")),
            payload = AssetPayload(
                color = AssetColor.RGB(r = 0.95F, g = 0.45F, b = 0.4F),
            ),
        ),
        paletteColor = ComposeColor(red = 0.95F, green = 0.45F, blue = 0.4F),
    ),
    BrandColorAsset(
        definition = AssetDefinition(
            id = "print-magenta",
            label = mapOf("en" to "Print Magenta"),
            tags = mapOf("en" to listOf("print", "magenta", "cmyk")),
            payload = AssetPayload(
                color = AssetColor.CMYK(c = 0F, m = 0.9F, y = 0.2F, k = 0F),
            ),
        ),
        paletteColor = ComposeColor(red = 1F, green = 0.1F, blue = 0.8F),
    ),
    BrandColorAsset(
        definition = AssetDefinition(
            id = "metallic-gold",
            label = mapOf("en" to "Metallic Gold"),
            tags = mapOf("en" to listOf("spot", "metallic", "gold")),
            payload = AssetPayload(
                color = AssetColor.SpotColor(
                    name = "Metallic Gold Ink",
                    externalReference = "Custom Inks",
                    representation = AssetColor.RGB(r = 0.85F, g = 0.65F, b = 0.13F),
                ),
            ),
        ),
        paletteColor = ComposeColor(red = 0.85F, green = 0.65F, blue = 0.13F),
    ),
)

private fun brandPaletteColors() = brandColorAssets().mapNotNull { it.paletteColor }
// highlight-android-defining-color-assets

// highlight-android-add-library
private fun createBrandColorLibrary(engine: Engine) {
    // Keep repeated guide launches idempotent inside the same editor process.
    if (BRAND_COLOR_SOURCE_ID in engine.asset.findAllSources()) {
        engine.asset.removeSource(sourceId = BRAND_COLOR_SOURCE_ID)
    }
    engine.asset.addLocalSource(
        sourceId = BRAND_COLOR_SOURCE_ID,
        supportedMimeTypes = emptyList(),
    )
    brandColorAssets().forEach { color ->
        engine.asset.addAsset(sourceId = BRAND_COLOR_SOURCE_ID, asset = color.definition)
    }
    engine.asset.assetSourceContentsChanged(sourceId = BRAND_COLOR_SOURCE_ID)
}
// highlight-android-add-library

// highlight-android-remove-color
private fun removeBrandColor(
    engine: Engine,
    assetId: String = BRAND_CORAL_ID,
) {
    engine.asset.removeAsset(sourceId = BRAND_COLOR_SOURCE_ID, assetId = assetId)
    engine.asset.assetSourceContentsChanged(sourceId = BRAND_COLOR_SOURCE_ID)
}
// highlight-android-remove-color

private fun selectFirstPageForDemo(engine: Engine) {
    // Select the page so the Fill/Stroke inspector button is visible immediately.
    engine.block.findByType(DesignBlockType.Page)
        .firstOrNull()
        ?.let { engine.block.setSelected(it, selected = true) }
}

// Add this composable to your NavHost
@Composable
fun ColorPaletteEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license, // pass null or empty for evaluation mode with watermark
        configuration = {
            EditorConfiguration.remember {
                // highlight-android-register-library
                onCreate = {
                    val scene = editorContext.engine.scene.create()
                    val page = editorContext.engine.block.create(DesignBlockType.Page)
                    editorContext.engine.block.setWidth(block = page, value = 1080F)
                    editorContext.engine.block.setHeight(block = page, value = 1080F)
                    editorContext.engine.block.appendChild(parent = scene, child = page)

                    createBrandColorLibrary(editorContext.engine)
                }
                // highlight-android-register-library
                onLoaded = {
                    selectFirstPageForDemo(editorContext.engine)
                }
                // highlight-android-config-palette
                colorPalette = {
                    remember {
                        brandPaletteColors()
                    }
                }
                // highlight-android-config-palette
                inspectorBar = {
                    InspectorBar.remember {
                        listBuilder = {
                            InspectorBar.ListBuilder.remember {
                                add { InspectorBar.Button.rememberFillStroke() }
                            }
                        }
                    }
                }
            }
        },
        onClose = onClose,
    )
}
