import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ly.img.engine.AssetDefinition
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FindAssetsQuery

suspend fun templateLibrary(
    engine: Engine,
    monitoringScope: CoroutineScope,
    assetBaseUri: Uri = Uri.parse("https://cdn.img.ly/packages/imgly/cesdk-android/1.79.0-rc.0/assets"),
): TemplateLibraryResult = coroutineScope {
    val templateSourceId = "my.custom.templates"
    val hostedSourceId = "ly.img.templates"

    listOf(templateSourceId, hostedSourceId).forEach { sourceId ->
        if (sourceId in engine.asset.findAllSources()) {
            engine.asset.removeSource(sourceId = sourceId)
        }
    }

    val observedAddedSourceIds = mutableListOf<String>()
    val observedRemovedSourceIds = mutableListOf<String>()
    val sourceMonitorJobs = monitorTemplateSources(
        engine = engine,
        monitoringScope = monitoringScope,
        observedAddedSourceIds = observedAddedSourceIds,
        observedRemovedSourceIds = observedRemovedSourceIds,
    )

    // highlight-android-setup
    // Create the design scene that selected templates will be applied to.
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)
    // highlight-android-setup

    // highlight-android-create-source
    engine.asset.addLocalSource(
        sourceId = templateSourceId,
        supportedMimeTypes = emptyList(),
        applyAsset = { asset ->
            val templateUri = asset.meta?.get("uri")?.let(Uri::parse) ?: return@addLocalSource null
            engine.scene.applyTemplate(templateUri = templateUri)
            null
        },
    )
    // highlight-android-create-source

    // highlight-android-add-assets
    val businessCardTemplateUri = assetBaseUri.buildUpon()
        .appendPath("ly.img.templates")
        .appendPath("templates")
        .appendPath("cesdk_business_card_1.scene")
        .build()
    val businessCardThumbnailUri = assetBaseUri.buildUpon()
        .appendPath("ly.img.templates")
        .appendPath("thumbnails")
        .appendPath("cesdk_business_card_1.jpg")
        .build()
    val blankTemplateUri = assetBaseUri.buildUpon()
        .appendPath("ly.img.templates")
        .appendPath("templates")
        .appendPath("cesdk_blank_1.scene")
        .build()
    val blankThumbnailUri = assetBaseUri.buildUpon()
        .appendPath("ly.img.templates")
        .appendPath("thumbnails")
        .appendPath("cesdk_blank_1.png")
        .build()

    val templates = listOf(
        AssetDefinition(
            id = "business-card",
            label = mapOf("en" to "Business Card"),
            tags = mapOf("en" to listOf("business", "card")),
            groups = listOf("business"),
            meta = mapOf(
                "uri" to businessCardTemplateUri.toString(),
                "thumbUri" to businessCardThumbnailUri.toString(),
            ),
        ),
        AssetDefinition(
            id = "blank-canvas",
            label = mapOf("en" to "Blank Canvas"),
            tags = mapOf("en" to listOf("blank", "canvas")),
            groups = listOf("basics"),
            meta = mapOf(
                "uri" to blankTemplateUri.toString(),
                "thumbUri" to blankThumbnailUri.toString(),
            ),
        ),
    )

    templates.forEach { template ->
        engine.asset.addAsset(sourceId = templateSourceId, asset = template)
    }
    engine.asset.assetSourceContentsChanged(sourceId = templateSourceId)
    // highlight-android-add-assets

    val blankTemplate = engine.asset.fetchAsset(
        sourceId = templateSourceId,
        assetId = "blank-canvas",
    ) ?: error("Expected the blank template asset to be registered.")

    // highlight-android-apply-template
    val createdBlock = engine.asset.applyAssetSourceAsset(
        sourceId = templateSourceId,
        asset = blankTemplate,
    )
    // highlight-android-apply-template
    check(createdBlock == null)

    // highlight-android-load-json-source
    val contentJsonUri = assetBaseUri.buildUpon()
        .appendPath("ly.img.templates")
        .appendPath("content.json")
        .build()
    val loadedHostedSourceId = engine.asset.addLocalSourceFromJSON(
        contentUri = contentJsonUri,
        matcher = null,
    )
    // highlight-android-load-json-source

    // highlight-android-query-templates
    val businessTemplates = engine.asset.findAssets(
        sourceId = templateSourceId,
        query = FindAssetsQuery(page = 0, perPage = 20, groups = listOf("business")),
    )
    val allTemplates = engine.asset.findAssets(
        sourceId = templateSourceId,
        query = FindAssetsQuery(page = 0, perPage = 20),
    )
    val groups = engine.asset.getGroups(sourceId = templateSourceId).orEmpty()
    // highlight-android-query-templates

    // highlight-android-manage-sources
    val registeredSources = engine.asset.findAllSources()
    check(templateSourceId in registeredSources)
    check(loadedHostedSourceId in registeredSources)

    engine.asset.removeSource(sourceId = loadedHostedSourceId)
    // highlight-android-manage-sources

    val result = TemplateLibraryResult(
        templateSourceRegistered = templateSourceId in engine.asset.findAllSources(),
        businessTemplateIds = businessTemplates.assets.map { asset -> asset.id },
        allTemplateCount = allTemplates.total,
        groups = groups,
        hostedSourceId = loadedHostedSourceId,
        hostedSourceRemoved = loadedHostedSourceId !in engine.asset.findAllSources(),
        observedAddedSourceIds = observedAddedSourceIds,
        observedRemovedSourceIds = observedRemovedSourceIds,
        sourceMonitorJobs = sourceMonitorJobs,
        pageCountAfterApply = engine.block.findByType(DesignBlockType.Page).size,
    )
    result
}

// highlight-android-monitor-sources
fun monitorTemplateSources(
    engine: Engine,
    monitoringScope: CoroutineScope,
    observedAddedSourceIds: MutableList<String>,
    observedRemovedSourceIds: MutableList<String>,
): List<Job> {
    val addedJob = monitoringScope.launch(Dispatchers.Main.immediate) {
        engine.asset.onAssetSourceAdded().collect { sourceId ->
            observedAddedSourceIds += sourceId
        }
    }
    val removedJob = monitoringScope.launch(Dispatchers.Main.immediate) {
        engine.asset.onAssetSourceRemoved().collect { sourceId ->
            observedRemovedSourceIds += sourceId
        }
    }
    return listOf(addedJob, removedJob)
}
// highlight-android-monitor-sources
