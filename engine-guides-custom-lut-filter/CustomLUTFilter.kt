import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.DesignBlockType
import ly.img.engine.EffectType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType

fun customLUTFilter(
    license: String,
    userId: String,
) = CoroutineScope(
    Dispatchers.Main,
).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 100, height = 100)
    val scene = engine.scene.create()

    // highlight-load-scene
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 100F)
    engine.block.setHeight(page, value = 100F)
    engine.block.appendChild(parent = scene, child = page)
    engine.scene.zoomToBlock(
        scene,
        paddingLeft = 40F,
        paddingTop = 40F,
        paddingRight = 40F,
        paddingBottom = 40F,
    )
    // highlight-load-scene

    // highlight-create-rect
    val rect = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(rect, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(rect, value = 100F)
    engine.block.setHeight(rect, value = 100F)
    engine.block.appendChild(parent = page, child = rect)
    // highlight-create-rect

    // highlight-create-image-fill
    val imageFill = engine.block.createFill(FillType.Image)
    engine.block.setString(
        imageFill,
        property = "fill/image/imageFileURI",
        value = "https://img.ly/static/ubq_samples/sample_1.jpg",
    )
    // highlight-create-image-fill

    // highlight-create-lut-filter
    val lutFilter = engine.block.createEffect(EffectType.LutFilter)
    engine.block.setBoolean(lutFilter, property = "effect/enabled", value = true)
    engine.block.setFloat(lutFilter, property = "effect/lut_filter/intensity", value = 0.9F)

    @Suppress("ktlint:standard:max-line-length")
    val lutUri = "https://cdn.img.ly/packages/imgly/cesdk-js/1.26.0/assets/extensions/ly.img.cesdk.filters.lut/LUTs/imgly_lut_ad1920_5_5_128.png"

    engine.block.setString(
        lutFilter,
        property = "effect/lut_filter/lutFileURI",
        value = lutUri,
    )
    engine.block.setInt(lutFilter, property = "effect/lut_filter/verticalTileCount", value = 5)
    engine.block.setInt(lutFilter, property = "effect/lut_filter/horizontalTileCount", value = 5)
    // highlight-create-lut-filter

    // highlight-apply-lut-filter
    engine.block.appendEffect(rect, effectBlock = lutFilter)
    engine.block.setFill(rect, fill = imageFill)
    // highlight-apply-lut-filter

    engine.stop()
}
