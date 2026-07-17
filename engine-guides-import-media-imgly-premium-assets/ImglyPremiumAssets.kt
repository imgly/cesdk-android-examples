import android.net.Uri
import androidx.annotation.StringRes
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.LibraryContent
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.engine.Asset
import ly.img.engine.Engine
import ly.img.engine.FindAssetsQuery

// highlight-android-content-uri
private const val PREMIUM_ASSET_SOURCE_ID = "ly.img.templates.premium"

fun imglyPremiumContentUri(premiumAssetsBaseUri: Uri): Uri = premiumAssetsBaseUri.buildUpon()
    .appendPath(PREMIUM_ASSET_SOURCE_ID)
    .appendPath("content.json")
    .build()

// highlight-android-content-uri

// highlight-android-create-local-source
fun createImglyPremiumAssetSource(engine: Engine) {
    if (PREMIUM_ASSET_SOURCE_ID in engine.asset.findAllSources()) {
        engine.asset.removeSource(sourceId = PREMIUM_ASSET_SOURCE_ID)
    }

    engine.asset.addLocalSource(
        sourceId = PREMIUM_ASSET_SOURCE_ID,
        supportedMimeTypes = emptyList(),
        applyAsset = { asset: Asset ->
            val archiveUri = asset.meta?.get("uri")?.let(Uri::parse)
                ?: error("Premium template ${asset.id} is missing meta.uri")
            engine.scene.loadArchive(
                archiveUri = archiveUri,
                waitForResources = true,
            )
            null
        },
    )
}

// highlight-android-create-local-source

// highlight-android-process-assets
suspend fun addImglyPremiumAssetsToSource(
    engine: Engine,
    contentUri: Uri,
    matcher: List<String>? = null,
) {
    val sourceId = engine.asset.addLocalSourceFromJSON(
        contentUri = contentUri,
        matcher = matcher,
    )
    check(sourceId == PREMIUM_ASSET_SOURCE_ID)
}

// highlight-android-process-assets

// highlight-android-register-source
suspend fun imglyPremiumAssets(
    engine: Engine,
    premiumAssetsBaseUri: Uri,
    matcher: List<String>? = null,
): String {
    val contentUri = imglyPremiumContentUri(premiumAssetsBaseUri)

    createImglyPremiumAssetSource(engine = engine)
    addImglyPremiumAssetsToSource(
        engine = engine,
        contentUri = contentUri,
        matcher = matcher,
    )

    return PREMIUM_ASSET_SOURCE_ID
}

// highlight-android-register-source

// highlight-android-premium-library-category
fun imglyPremiumAddCategory(
    @StringRes titleRes: Int,
): LibraryCategory {
    val premiumSourceType = AssetSourceType(sourceId = PREMIUM_ASSET_SOURCE_ID)
    val premiumSection = LibraryContent.Section(
        titleRes = titleRes,
        sourceTypes = listOf(premiumSourceType),
        assetType = AssetType.Image,
    )

    return LibraryCategory.Images.copy(
        tabTitleRes = titleRes,
        content = LibraryContent.Sections(
            titleRes = titleRes,
            sections = listOf(premiumSection),
        ),
    )
}

// highlight-android-premium-library-category

// highlight-android-query-and-apply
suspend fun applyFirstImglyPremiumTemplate(
    engine: Engine,
    sourceId: String,
) {
    val templates = engine.asset.findAssets(
        sourceId = sourceId,
        query = FindAssetsQuery(page = 0, perPage = 10),
    )
    val firstTemplate = templates.assets.firstOrNull()
        ?: error("No premium templates are available in $sourceId.")

    val createdBlock = engine.asset.applyAssetSourceAsset(
        sourceId = sourceId,
        asset = firstTemplate,
    )
    check(createdBlock == null)
}

// highlight-android-query-and-apply
