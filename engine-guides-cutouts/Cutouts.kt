import ly.img.engine.Color
import ly.img.engine.CutoutOperation
import ly.img.engine.CutoutType
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType

data class Cutouts(
    val blockCutoutSmoothing: Float,
    val combinedCutoutType: String,
    val combinedCutoutOffset: Float,
    val dashedSpotColorName: String,
    val pdfByteCount: Int,
)

suspend fun cutouts(engine: Engine): Cutouts {
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    val sourceShape = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(sourceShape, shape = engine.block.createShape(ShapeType.Star))
    engine.block.setWidth(sourceShape, value = 140F)
    engine.block.setHeight(sourceShape, value = 140F)
    engine.block.setPositionX(sourceShape, value = 70F)
    engine.block.setPositionY(sourceShape, value = 80F)
    engine.block.setFill(sourceShape, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = sourceShape,
        color = Color.fromRGBA(r = 0.95F, g = 0.75F, b = 0.15F, a = 1F),
    )
    engine.block.appendChild(parent = page, child = sourceShape)

    // highlight-android-create-cutout-from-path
    val circle = engine.block.createCutoutFromPath(
        "M 0,75 a 75,75 0 1,1 150,0 a 75,75 0 1,1 -150,0 Z",
    )
    engine.block.appendChild(parent = page, child = circle)
    engine.block.setPositionX(circle, value = 200F)
    engine.block.setPositionY(circle, value = 225F)
    // highlight-android-create-cutout-from-path

    // highlight-android-create-cutout-from-blocks
    val blockCutout = engine.block.createCutoutFromBlocks(
        blocks = listOf(sourceShape),
        vectorizeDistanceThreshold = 2F,
        simplifyDistanceThreshold = 4F,
        useExistingShapeInformation = true,
    )
    engine.block.appendChild(parent = page, child = blockCutout)
    engine.block.setPositionX(blockCutout, value = 70F)
    engine.block.setPositionY(blockCutout, value = 80F)
    // highlight-android-create-cutout-from-blocks

    // highlight-android-configure-cutout-type
    engine.block.setEnum(
        block = circle,
        property = "cutout/type",
        value = CutoutType.DASHED.key,
    )
    // highlight-android-configure-cutout-type

    // highlight-android-configure-cutout-offset
    engine.block.setFloat(block = circle, property = "cutout/offset", value = 5F)
    engine.block.setFloat(block = blockCutout, property = "cutout/smoothing", value = 2F)
    // highlight-android-configure-cutout-offset

    // highlight-android-create-square-cutout
    val square = engine.block.createCutoutFromPath("M 0,0 H 150 V 150 H 0 Z")
    engine.block.appendChild(parent = page, child = square)
    engine.block.setPositionX(square, value = 450F)
    engine.block.setPositionY(square, value = 225F)
    engine.block.setFloat(block = square, property = "cutout/offset", value = 8F)
    // highlight-android-create-square-cutout

    // highlight-android-combine-cutouts
    val combined = engine.block.createCutoutFromOperation(
        blocks = listOf(circle, square),
        op = CutoutOperation.UNION,
    )
    engine.block.appendChild(parent = page, child = combined)

    engine.block.destroy(circle)
    engine.block.destroy(square)
    // highlight-android-combine-cutouts

    // highlight-android-customize-spot-color
    engine.block.setSpotColorForCutoutType(
        type = CutoutType.DASHED,
        name = "KissCutContour",
    )
    engine.editor.setSpotColor(
        name = "KissCutContour",
        color = Color.fromRGBA(r = 0F, g = 0.4F, b = 0.9F, a = 1F),
    )
    // highlight-android-customize-spot-color

    // highlight-android-export
    val pdfData = engine.block.export(block = page, mimeType = MimeType.PDF)
    // highlight-android-export

    return Cutouts(
        blockCutoutSmoothing = engine.block.getFloat(blockCutout, "cutout/smoothing"),
        combinedCutoutType = engine.block.getEnum(combined, "cutout/type"),
        combinedCutoutOffset = engine.block.getFloat(combined, "cutout/offset"),
        dashedSpotColorName = engine.block.getSpotColorForCutoutType(CutoutType.DASHED),
        pdfByteCount = pdfData.remaining(),
    )
}
