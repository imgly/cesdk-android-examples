import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType

fun usingShapes(
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1080, height = 1920)

    // highlight-setup
    val scene = engine.scene.create()

    val graphic = engine.block.create(DesignBlockType.Graphic)
    val imageFill = engine.block.createFill(FillType.Image)
    engine.block.setString(
        block = imageFill,
        property = "fill/image/imageFileURI",
        value = "https://img.ly/static/ubq_samples/sample_1.jpg",
    )
    engine.block.setFill(graphic, fill = imageFill)
    engine.block.setWidth(graphic, value = 100F)
    engine.block.setHeight(graphic, value = 100F)
    engine.block.appendChild(parent = scene, child = graphic)

    engine.scene.zoomToBlock(
        graphic,
        paddingLeft = 40F,
        paddingTop = 40F,
        paddingRight = 40F,
        paddingBottom = 40F,
    )

    // highlight-setup

    // highlight-supportsShape
    engine.block.supportsShape(graphic) // Returns true
    val text = engine.block.create(DesignBlockType.Text)
    engine.block.supportsShape(text) // Returns false
    // highlight-supportsShape

    // highlight-createShape
    val rectShape = engine.block.createShape(ShapeType.Rect)
    // highlight-setShape
    engine.block.setShape(graphic, shape = rectShape)
    // highlight-getShape
    val shape = engine.block.getShape(graphic)
    val shapeType = engine.block.getType(shape)
    // highlight-getShape

    // highlight-replaceShape
    val starShape = engine.block.createShape(ShapeType.Star)
    engine.block.destroy(engine.block.getShape(graphic))
    engine.block.setShape(graphic, shape = starShape)
    // The following line would also destroy the currently attached starShape
    // engine.block.destroy(graphic)
    // highlight-replaceShape

    // highlight-getProperties
    val allShapeProperties = engine.block.findAllProperties(starShape)
    // highlight-getProperties
    // highlight-modifyProperties
    engine.block.setInt(starShape, property = "shape/star/points", value = 6)
    // highlight-modifyProperties

    engine.stop()
}
