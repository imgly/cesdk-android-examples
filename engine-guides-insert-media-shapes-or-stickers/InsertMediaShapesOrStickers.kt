import android.net.Uri
import android.util.Log
import ly.img.editor.defaultBaseUri
import ly.img.engine.AssetDefinition
import ly.img.engine.Color
import ly.img.engine.ContentFillMode
import ly.img.engine.DefaultAssetSource
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.ExportOptions
import ly.img.engine.FillType
import ly.img.engine.FindAssetsQuery
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import ly.img.engine.populateAssetSource

private const val TAG = "ShapesOrStickers"
private const val GUIDE_STICKER_SOURCE_ID = "ly.img.guide.sticker"

suspend fun insertMediaShapesOrStickers(engine: Engine): InsertMediaShapesOrStickersResult {
    // highlight-android-setup
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    val stickerAssetsBaseUri: Uri = defaultBaseUri
    // highlight-android-setup

    // highlight-android-check-shape-support
    val graphicBlock = engine.block.create(DesignBlockType.Graphic)
    val graphicSupportsShape = engine.block.supportsShape(graphicBlock)
    Log.i(TAG, "Graphic block supports shapes: $graphicSupportsShape")

    val textBlock = engine.block.create(DesignBlockType.Text)
    val textSupportsShape = engine.block.supportsShape(textBlock)
    Log.i(TAG, "Text block supports shapes: $textSupportsShape")

    engine.block.destroy(textBlock)
    engine.block.destroy(graphicBlock)
    // highlight-android-check-shape-support

    // highlight-android-create-rectangle
    val rectBlock = engine.block.create(DesignBlockType.Graphic)
    val rectShape = engine.block.createShape(ShapeType.Rect)
    engine.block.setShape(block = rectBlock, shape = rectShape)

    val rectFill = engine.block.createFill(FillType.Color)
    engine.block.setColor(
        block = rectFill,
        property = "fill/color/value",
        value = Color.fromRGBA(r = 0.2F, g = 0.5F, b = 0.9F, a = 1F),
    )
    engine.block.setFill(block = rectBlock, fill = rectFill)

    engine.block.setWidth(rectBlock, value = 160F)
    engine.block.setHeight(rectBlock, value = 140F)
    engine.block.appendChild(parent = page, child = rectBlock)
    // highlight-android-create-rectangle

    // highlight-android-create-rounded-rectangle
    val roundedBlock = engine.block.create(DesignBlockType.Graphic)
    val roundedShape = engine.block.createShape(ShapeType.Rect)
    engine.block.setShape(block = roundedBlock, shape = roundedShape)

    engine.block.setFloat(roundedShape, property = "shape/rect/cornerRadiusTL", value = 20F)
    engine.block.setFloat(roundedShape, property = "shape/rect/cornerRadiusTR", value = 20F)
    engine.block.setFloat(roundedShape, property = "shape/rect/cornerRadiusBL", value = 20F)
    engine.block.setFloat(roundedShape, property = "shape/rect/cornerRadiusBR", value = 20F)

    val roundedFill = engine.block.createFill(FillType.Color)
    engine.block.setColor(
        block = roundedFill,
        property = "fill/color/value",
        value = Color.fromRGBA(r = 0.9F, g = 0.4F, b = 0.2F, a = 1F),
    )
    engine.block.setFill(block = roundedBlock, fill = roundedFill)

    engine.block.setWidth(roundedBlock, value = 160F)
    engine.block.setHeight(roundedBlock, value = 140F)
    engine.block.appendChild(parent = page, child = roundedBlock)
    // highlight-android-create-rounded-rectangle

    // highlight-android-create-ellipse
    val ellipseBlock = engine.block.create(DesignBlockType.Graphic)
    val ellipseShape = engine.block.createShape(ShapeType.Ellipse)
    engine.block.setShape(block = ellipseBlock, shape = ellipseShape)

    val ellipseFill = engine.block.createFill(FillType.Color)
    engine.block.setColor(
        block = ellipseFill,
        property = "fill/color/value",
        value = Color.fromRGBA(r = 0.3F, g = 0.8F, b = 0.4F, a = 1F),
    )
    engine.block.setFill(block = ellipseBlock, fill = ellipseFill)

    engine.block.setWidth(ellipseBlock, value = 160F)
    engine.block.setHeight(ellipseBlock, value = 140F)
    engine.block.appendChild(parent = page, child = ellipseBlock)
    // highlight-android-create-ellipse

    // highlight-android-create-star
    val starBlock = engine.block.create(DesignBlockType.Graphic)
    val starShape = engine.block.createShape(ShapeType.Star)
    engine.block.setShape(block = starBlock, shape = starShape)

    engine.block.setInt(starShape, property = "shape/star/points", value = 5)
    engine.block.setFloat(starShape, property = "shape/star/innerDiameter", value = 0.4F)

    val starFill = engine.block.createFill(FillType.Color)
    engine.block.setColor(
        block = starFill,
        property = "fill/color/value",
        value = Color.fromRGBA(r = 1F, g = 0.8F, b = 0F, a = 1F),
    )
    engine.block.setFill(block = starBlock, fill = starFill)

    engine.block.setWidth(starBlock, value = 160F)
    engine.block.setHeight(starBlock, value = 140F)
    engine.block.appendChild(parent = page, child = starBlock)
    // highlight-android-create-star

    // highlight-android-create-polygon
    val polygonBlock = engine.block.create(DesignBlockType.Graphic)
    val polygonShape = engine.block.createShape(ShapeType.Polygon)
    engine.block.setShape(block = polygonBlock, shape = polygonShape)

    engine.block.setInt(polygonShape, property = "shape/polygon/sides", value = 6)

    val polygonFill = engine.block.createFill(FillType.Color)
    engine.block.setColor(
        block = polygonFill,
        property = "fill/color/value",
        value = Color.fromRGBA(r = 0.6F, g = 0.2F, b = 0.8F, a = 1F),
    )
    engine.block.setFill(block = polygonBlock, fill = polygonFill)

    engine.block.setWidth(polygonBlock, value = 160F)
    engine.block.setHeight(polygonBlock, value = 140F)
    engine.block.appendChild(parent = page, child = polygonBlock)
    // highlight-android-create-polygon

    // highlight-android-create-line
    val lineBlock = engine.block.create(DesignBlockType.Graphic)
    val lineShape = engine.block.createShape(ShapeType.Line)
    engine.block.setShape(block = lineBlock, shape = lineShape)

    engine.block.setStrokeEnabled(lineBlock, enabled = true)
    engine.block.setStrokeWidth(lineBlock, width = 6F)
    engine.block.setStrokeColor(
        block = lineBlock,
        color = Color.fromRGBA(r = 0.9F, g = 0.2F, b = 0.5F, a = 1F),
    )

    engine.block.setWidth(lineBlock, value = 160F)
    // Line shapes use block height as the visible stroke thickness.
    engine.block.setHeight(lineBlock, value = 6F)
    engine.block.appendChild(parent = page, child = lineBlock)
    // highlight-android-create-line

    // highlight-android-create-vector-path
    val vectorPathBlock = engine.block.create(DesignBlockType.Graphic)
    val vectorPathShape = engine.block.createShape(ShapeType.VectorPath)
    engine.block.setShape(block = vectorPathBlock, shape = vectorPathShape)

    val trianglePath = "M 50,0 L 100,100 L 0,100 Z"
    engine.block.setString(
        block = vectorPathShape,
        property = "shape/vector_path/path",
        value = trianglePath,
    )

    val vectorPathFill = engine.block.createFill(FillType.Color)
    engine.block.setColor(
        block = vectorPathFill,
        property = "fill/color/value",
        value = Color.fromRGBA(r = 0.2F, g = 0.7F, b = 0.7F, a = 1F),
    )
    engine.block.setFill(block = vectorPathBlock, fill = vectorPathFill)

    engine.block.setWidth(vectorPathBlock, value = 160F)
    engine.block.setHeight(vectorPathBlock, value = 140F)
    engine.block.appendChild(parent = page, child = vectorPathBlock)
    // highlight-android-create-vector-path

    // highlight-android-discover-shape-properties
    val starProperties = engine.block.findAllProperties(starShape)
    Log.i(TAG, "Star shape properties: $starProperties")
    // highlight-android-discover-shape-properties

    // highlight-android-sticker-manual-construction
    val stickerBlock = engine.block.create(DesignBlockType.Graphic)
    val stickerShape = engine.block.createShape(ShapeType.Rect)
    engine.block.setShape(block = stickerBlock, shape = stickerShape)

    val stickerFill = engine.block.createFill(FillType.Image)
    val stickerUri = stickerAssetsBaseUri.buildUpon()
        .appendPath(DefaultAssetSource.STICKER.key)
        .appendPath("images")
        .appendPath("emoji")
        .appendPath("emoji_happyface.svg")
        .build()
    engine.block.setUri(
        block = stickerFill,
        property = "fill/image/imageFileURI",
        value = stickerUri,
    )
    engine.block.setFill(block = stickerBlock, fill = stickerFill)

    if (engine.block.supportsContentFillMode(stickerBlock)) {
        engine.block.setContentFillMode(stickerBlock, mode = ContentFillMode.CONTAIN)
    }
    engine.block.setKind(stickerBlock, kind = "sticker")

    engine.block.setWidth(stickerBlock, value = 160F)
    engine.block.setHeight(stickerBlock, value = 140F)
    engine.block.appendChild(parent = page, child = stickerBlock)
    // highlight-android-sticker-manual-construction

    // highlight-android-query-stickers
    val stickerSourceId = DefaultAssetSource.STICKER.key
    if (stickerSourceId !in engine.asset.findAllSources()) {
        engine.populateAssetSource(
            id = stickerSourceId,
            jsonUri = stickerAssetsBaseUri.buildUpon()
                .appendPath(stickerSourceId)
                .appendPath("content.json")
                .build(),
            replaceBaseUri = stickerAssetsBaseUri,
        )
    }

    var stickerResults = findStickerAssets(engine = engine, sourceId = stickerSourceId)
    var stickerAssetSourceId = stickerSourceId
    if (stickerResults.assets.isEmpty()) {
        try {
            engine.populateAssetSource(
                id = stickerSourceId,
                jsonUri = stickerAssetsBaseUri.buildUpon()
                    .appendPath(stickerSourceId)
                    .appendPath("content.json")
                    .build(),
                replaceBaseUri = stickerAssetsBaseUri,
            )
        } catch (error: Exception) {
            Log.w(TAG, "Could not populate sticker source $stickerSourceId.", error)
        }
        stickerResults = findStickerAssets(engine = engine, sourceId = stickerSourceId)
    }
    if (stickerResults.assets.isEmpty()) {
        stickerAssetSourceId = GUIDE_STICKER_SOURCE_ID
        ensureGuideStickerAssetSource(engine = engine, stickerUri = stickerUri)
        stickerResults = findStickerAssets(engine = engine, sourceId = stickerAssetSourceId)
    }
    Log.i(TAG, "Stickers in emoji category: ${stickerResults.total}")
    // highlight-android-query-stickers

    // highlight-android-apply-sticker
    val stickerAsset = checkNotNull(stickerResults.assets.firstOrNull()) {
        "No sticker assets found."
    }
    val stickerFromLibrary = checkNotNull(
        engine.asset.applyAssetSourceAsset(sourceId = stickerAssetSourceId, asset = stickerAsset),
    ) {
        "The sticker asset source did not create a block."
    }

    if (engine.block.supportsContentFillMode(stickerFromLibrary)) {
        engine.block.setContentFillMode(stickerFromLibrary, mode = ContentFillMode.CONTAIN)
    }
    engine.block.setWidth(stickerFromLibrary, value = 160F)
    engine.block.setHeight(stickerFromLibrary, value = 140F)
    // highlight-android-apply-sticker

    val shapeBlocks = listOf(
        rectBlock,
        roundedBlock,
        ellipseBlock,
        starBlock,
        polygonBlock,
        lineBlock,
        vectorPathBlock,
    )
    val stickerBlocks = listOf(stickerBlock, stickerFromLibrary)
    val gridBlocks = shapeBlocks + stickerBlocks

    gridBlocks.forEachIndexed { index, block ->
        val column = index % 3
        val row = index / 3
        engine.block.setPositionX(block, value = 130F + column * 190F)
        engine.block.setPositionY(block, value = 60F + row * 170F)
    }

    engine.block.forceLoadResources(blocks = listOf(page) + gridBlocks)
    val pngData = engine.block.export(
        block = page,
        mimeType = MimeType.PNG,
        options = ExportOptions(targetWidth = 800F, targetHeight = 600F),
    )
    check(pngData.hasRemaining()) { "shapes and stickers PNG export is empty" }

    return InsertMediaShapesOrStickersResult(
        graphicSupportsShape = graphicSupportsShape,
        textSupportsShape = textSupportsShape,
        starPoints = engine.block.getInt(starShape, property = "shape/star/points"),
        starInnerDiameter = engine.block.getFloat(starShape, property = "shape/star/innerDiameter"),
        polygonSides = engine.block.getInt(polygonShape, property = "shape/polygon/sides"),
        vectorPath = engine.block.getString(vectorPathShape, property = "shape/vector_path/path"),
        starPropertyCount = starProperties.size,
        stickerResultCount = stickerResults.total,
        stickerBlockCount = stickerBlocks.size,
        pngData = pngData.asReadOnlyBuffer(),
    )
}

private suspend fun findStickerAssets(
    engine: Engine,
    sourceId: String,
) = engine.asset.findAssets(
    sourceId = sourceId,
    query = FindAssetsQuery(
        query = null,
        page = 0,
        groups = listOf("emoji"),
        perPage = 5,
    ),
)

private suspend fun ensureGuideStickerAssetSource(
    engine: Engine,
    stickerUri: Uri,
) {
    if (GUIDE_STICKER_SOURCE_ID !in engine.asset.findAllSources()) {
        engine.asset.addLocalSource(
            sourceId = GUIDE_STICKER_SOURCE_ID,
            supportedMimeTypes = listOf("image/svg+xml"),
        )
    }
    if (findStickerAssets(engine = engine, sourceId = GUIDE_STICKER_SOURCE_ID).assets.isNotEmpty()) {
        return
    }
    engine.asset.addAsset(
        sourceId = GUIDE_STICKER_SOURCE_ID,
        asset = AssetDefinition(
            id = "guide-emoji-happyface",
            label = mapOf("en" to "Happy Face"),
            tags = mapOf("en" to listOf("emoji", "happy", "face")),
            groups = listOf("emoji"),
            meta = mapOf(
                "uri" to stickerUri.toString(),
                "thumbUri" to stickerUri.toString(),
                "mimeType" to "image/svg+xml",
                "kind" to "sticker",
                "blockType" to DesignBlockType.Graphic.key,
                "fillType" to FillType.Image.key,
                "shapeType" to ShapeType.Rect.key,
                "width" to "512",
                "height" to "512",
            ),
        ),
    )
    engine.asset.assetSourceContentsChanged(sourceId = GUIDE_STICKER_SOURCE_ID)
}
