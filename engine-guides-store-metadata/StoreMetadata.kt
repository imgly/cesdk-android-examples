import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.ShapeType
import org.json.JSONObject

data class StoreMetadata(
    val externalId: String?,
    val metadataEntries: Map<String, String>,
    val generationModel: String,
    val hasUploadedByAfterRemoval: Boolean,
    val remainingKeys: List<String>,
    val persistedExternalId: String,
)

suspend fun storeCustomMetadata(engine: Engine): StoreMetadata {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    val trackedBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(trackedBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(trackedBlock, value = 400F)
    engine.block.setHeight(trackedBlock, value = 300F)
    engine.block.setPositionX(trackedBlock, value = 200F)
    engine.block.setPositionY(trackedBlock, value = 150F)
    engine.block.appendChild(parent = page, child = trackedBlock)

    // highlight-android-set-metadata
    engine.block.setMetadata(trackedBlock, key = "externalId", value = "asset-12345")
    engine.block.setMetadata(trackedBlock, key = "source", value = "user-upload")
    engine.block.setMetadata(trackedBlock, key = "uploadedBy", value = "designer@example.com")
    // highlight-android-set-metadata

    // highlight-android-get-metadata
    val externalId = if (engine.block.hasMetadata(trackedBlock, key = "externalId")) {
        engine.block.getMetadata(trackedBlock, key = "externalId")
    } else {
        null
    }
    // highlight-android-get-metadata

    // highlight-android-find-all-metadata
    val metadataEntries = engine.block
        .findAllMetadata(trackedBlock)
        .associateWith { key -> engine.block.getMetadata(trackedBlock, key = key) }
    // highlight-android-find-all-metadata

    // highlight-android-store-structured-data
    val generationInfo = JSONObject()
        .put("source", "internal-generator")
        .put("model", "image-model-v1")
        .put("appVersion", "2026.1")

    engine.block.setMetadata(
        trackedBlock,
        key = "generationInfo",
        value = generationInfo.toString(),
    )

    val decodedInfo = JSONObject(engine.block.getMetadata(trackedBlock, key = "generationInfo"))
    val generationModel = decodedInfo.getString("model")
    // highlight-android-store-structured-data

    // highlight-android-remove-metadata
    if (engine.block.hasMetadata(trackedBlock, key = "uploadedBy")) {
        engine.block.removeMetadata(trackedBlock, key = "uploadedBy")
    }
    // highlight-android-remove-metadata

    // highlight-android-verify-removal
    val hasUploadedByAfterRemoval = engine.block.hasMetadata(trackedBlock, key = "uploadedBy")
    val remainingKeys = engine.block.findAllMetadata(trackedBlock)
    // highlight-android-verify-removal

    // highlight-android-metadata-persistence
    val savedScene = engine.scene.saveToString(scene = scene)
    engine.scene.load(scene = savedScene)

    val reloadedBlock = engine.block.findByType(DesignBlockType.Graphic).first()
    val persistedExternalId = engine.block.getMetadata(reloadedBlock, key = "externalId")
    // highlight-android-metadata-persistence

    return StoreMetadata(
        externalId = externalId,
        metadataEntries = metadataEntries,
        generationModel = generationModel,
        hasUploadedByAfterRemoval = hasUploadedByAfterRemoval,
        remainingKeys = remainingKeys,
        persistedExternalId = persistedExternalId,
    )
}
