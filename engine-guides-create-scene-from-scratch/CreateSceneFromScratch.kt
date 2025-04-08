import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType

fun createSceneFromScratch(
    license: String,
    userId: String,
) = CoroutineScope(
    Dispatchers.Main,
).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1080, height = 1920)

    // highlight-create
    val scene = engine.scene.create()
    // highlight-create

    // highlight-add-page
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    // highlight-add-page

    // highlight-add-block-with-star-shape
    val block = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block = block, shape = engine.block.createShape(ShapeType.Star))
    engine.block.setFill(block = block, fill = engine.block.createFill(FillType.Color))
    engine.block.appendChild(parent = page, child = block)
    // highlight-add-block-with-star-shape

    engine.stop()
}
