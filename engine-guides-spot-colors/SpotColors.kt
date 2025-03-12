import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType

fun spotColors(
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

    engine.scene.zoomToBlock(
        page,
        paddingLeft = 40F,
        paddingTop = 40F,
        paddingRight = 40F,
        paddingBottom = 40F,
    )

    val text = engine.block.create(DesignBlockType.Text)
    engine.block.setPositionX(text, value = 350F)
    engine.block.setPositionY(text, value = 100F)

    val block = engine.block.create(DesignBlockType.Graphic)
    val fill = engine.block.createFill(FillType.Color)
    engine.block.setShape(block, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(block, value = 350F)
    engine.block.setPositionY(block, value = 400F)
    engine.block.setWidth(block, value = 100F)
    engine.block.setHeight(block, value = 100F)
    engine.block.setFill(block, fill = fill)
    // highlight-setup

    // highlight-create
    engine.editor.setSpotColor(
        name = "Crayola-Pink-Flamingo",
        Color.fromRGBA(r = 0.988F, g = 0.455F, b = 0.992F, a = 1F),
    )
    engine.editor.setSpotColor(
        name = "Pantone-ColorOfTheYear-2022",
        Color.fromRGBA(r = 0.4F, g = 0.404F, b = 0.671F, a = 1F),
    )
    engine.editor.setSpotColor(name = "Yellow", Color.fromRGBA(r = 1F, g = 1F, b = 0F, a = 1F))
    engine.editor.getSpotColorRGB(name = "Yellow") // (r: 1F, g: 1F, b: 0F)
    engine.editor.findAllSpotColors() // ["Crayola-Pink-Flamingo", "Pantone-ColorOfTheYear-2022", "Yellow"]
    // highlight-create

    // highlight-apply-star
    engine.block.setColor(
        fill,
        property = "fill/color/value",
        value = Color.fromSpotColor(name = "Crayola-Pink-Flamingo"),
    )
    engine.block.setColor(
        block,
        property = "stroke/color",
        value = Color.fromSpotColor(name = "Yellow", tint = 0.8F),
    )
    engine.block.setStrokeEnabled(block, enabled = true)

    engine.block.getColor(
        fill,
        property = "fill/color/value",
    ) // SpotColor with name "Crayola-Pink-Flamingo" with 1F tint
    engine.block.getColor(
        block,
        property = "stroke/color",
    ) // SpotColor with name "Yellow" and 0.8F tint
    // highlight-apply-star

    // highlight-apply-text
    engine.block.setColor(
        fill,
        property = "fill/color/value",
        value = Color.fromSpotColor(name = "Yellow"),
    )
    engine.block.setColor(
        text,
        property = "stroke/color",
        value = Color.fromSpotColor(name = "Crayola-Pink-Flamingo", tint = 0.5F),
    )
    engine.block.setStrokeEnabled(text, enabled = true)

    engine.block.setColor(
        text,
        property = "backgroundColor/color",
        value = Color.fromSpotColor(name = "Pantone-ColorOfTheYear-2022", tint = 0.9F),
    )
    engine.block.setBackgroundColorEnabled(text, enabled = true)
    // highlight-apply-text

    // highlight-change
    engine.editor.setSpotColor(name = "Yellow", Color.fromRGBA(r = 1F, g = 1F, b = 0.4F, a = 1F))
    // highlight-change

    // highlight-undefine
    engine.editor.removeSpotColor(name = "Yellow")
    // highlight-undefine

    engine.stop()
}
