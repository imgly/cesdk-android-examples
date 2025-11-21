import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.Color
import ly.img.engine.CutoutOperation
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine

fun cutouts(
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1080, height = 1920)

    // highlight-setup
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)
    // highlight-setup

    // highlight-create-cutouts
    val circle = engine.block.createCutoutFromPath(
        "M 0,25 a 25,25 0 1,1 50,0 a 25,25 0 1,1 -50,0 Z",
    )
    engine.block.setFloat(circle, "cutout/offset", 3F)
    engine.block.setEnum(circle, "cutout/type", "Dashed")

    val square = engine.block.createCutoutFromPath("M 0,0 H 50 V 50 H 0 Z")
    engine.block.setFloat(square, "cutout/offset", 6F)
    // highlight-create-cutouts

    // highlight-cutout-union
    val union = engine.block.createCutoutFromOperation(
        listOf(circle, square),
        op = CutoutOperation.UNION,
    )
    engine.block.destroy(circle)
    engine.block.destroy(square)
    // highlight-cutout-union

    // highlight-spot-color-solid
    engine.editor.setSpotColor("CutContour", Color.fromRGBA(r = 0F, g = 0F, b = 1F, a = 1F))
    // highlight-spot-color-solid

    engine.stop()
}
