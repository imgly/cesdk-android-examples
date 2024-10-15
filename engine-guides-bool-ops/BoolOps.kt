import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.BooleanOperation
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType

fun usingBoolOps(
    license: String,
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 100, height = 100)

    // highlight-setup
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)
    // highlight-setup

    // highlight-combine-union
    val circle1 = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(circle1, shape = engine.block.createShape(ShapeType.Ellipse))
    engine.block.setFill(circle1, fill = engine.block.createFill(FillType.Color))
    engine.block.setPositionX(circle1, value = 30F)
    engine.block.setPositionY(circle1, value = 30F)
    engine.block.setWidth(circle1, value = 40F)
    engine.block.setHeight(circle1, value = 40F)
    engine.block.appendChild(parent = page, child = circle1)

    val circle2 = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(circle2, shape = engine.block.createShape(ShapeType.Ellipse))
    engine.block.setFill(circle2, fill = engine.block.createFill(FillType.Color))
    engine.block.setPositionX(circle2, value = 80F)
    engine.block.setPositionY(circle2, value = 30F)
    engine.block.setWidth(circle2, value = 40F)
    engine.block.setHeight(circle2, value = 40F)
    engine.block.appendChild(parent = page, child = circle2)

    val circle3 = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(circle3, shape = engine.block.createShape(ShapeType.Ellipse))
    engine.block.setFill(circle3, fill = engine.block.createFill(FillType.Color))
    engine.block.setPositionX(circle3, value = 50F)
    engine.block.setPositionY(circle3, value = 50F)
    engine.block.setWidth(circle3, value = 50F)
    engine.block.setHeight(circle3, value = 50F)
    engine.block.appendChild(parent = page, child = circle3)

    engine.block.combine(listOf(circle1, circle2, circle3), op = BooleanOperation.UNION)
    // highlight-combine-unions

    // highlight-combine-difference
    val text = engine.block.create(DesignBlockType.Text)
    engine.block.replaceText(text, "Removed text")
    engine.block.setPositionX(text, value = 10F)
    engine.block.setPositionY(text, value = 40F)
    engine.block.setWidth(text, value = 80F)
    engine.block.setHeight(text, value = 10F)
    engine.block.appendChild(parent = page, child = text)

    val block = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block, shape = engine.block.createShape(ShapeType.Rect))
    val imageFill = engine.block.createFill(FillType.Image)
    engine.block.setFill(block = block, fill = imageFill)
    engine.block.setPositionX(block, value = 0F)
    engine.block.setPositionY(block, value = 0F)
    engine.block.setWidth(block, value = 100F)
    engine.block.setHeight(block, value = 100F)
    engine.block.setString(
        block = imageFill,
        property = "fill/image/imageFileURI",
        value = "https://img.ly/static/ubq_samples/sample_1.jpg",
    )
    engine.block.appendChild(parent = page, child = block)

    engine.block.sendToBack(block)
    engine.block.forceLoadResources(listOf(block))
    val difference = engine.block.combine(listOf(block, text), op = BooleanOperation.DIFFERENCE)
    // highlight-combine-difference

    engine.stop()
}
