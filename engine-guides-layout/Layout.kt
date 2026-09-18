import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.SceneLayout
import ly.img.engine.ShapeType

fun layout(
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example.layout")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1080, height = 1920)

    // highlight-android-vertical-stack
    // Create a scene with VerticalStack layout. Pages appended to the stack
    // container arrange top-to-bottom automatically.
    engine.scene.create(sceneLayout = SceneLayout.VERTICAL_STACK)

    // Get the stack container that was created with the scene.
    val stack = engine.block.findByType(DesignBlockType.Stack).first()

    // Create two pages that will stack vertically.
    val page1 = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page1, value = 400F)
    engine.block.setHeight(page1, value = 300F)
    engine.block.appendChild(parent = stack, child = page1)

    val page2 = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page2, value = 400F)
    engine.block.setHeight(page2, value = 300F)
    engine.block.appendChild(parent = stack, child = page2)

    // Configure spacing between stacked pages.
    engine.block.setFloat(stack, property = "stack/spacing", value = 20F)
    engine.block.setBoolean(stack, property = "stack/spacingInScreenspace", value = true)
    // highlight-android-vertical-stack

    // highlight-android-add-blocks
    // Add an image block to the first page.
    val block1 = engine.block.create(DesignBlockType.Graphic)
    val shape1 = engine.block.createShape(ShapeType.Rect)
    engine.block.setShape(block1, shape = shape1)
    engine.block.setWidth(block1, value = 350F)
    engine.block.setHeight(block1, value = 250F)
    engine.block.setPositionX(block1, value = 25F)
    engine.block.setPositionY(block1, value = 25F)
    val imageFill = engine.block.createFill(FillType.Image)
    engine.block.setString(
        block = imageFill,
        property = "fill/image/imageFileURI",
        value = "https://img.ly/static/ubq_samples/sample_1.jpg",
    )
    engine.block.setFill(block1, fill = imageFill)
    engine.block.appendChild(parent = page1, child = block1)

    // Add a colored rectangle to the second page.
    val block2 = engine.block.create(DesignBlockType.Graphic)
    val shape2 = engine.block.createShape(ShapeType.Rect)
    engine.block.setShape(block2, shape = shape2)
    engine.block.setWidth(block2, value = 350F)
    engine.block.setHeight(block2, value = 250F)
    engine.block.setPositionX(block2, value = 25F)
    engine.block.setPositionY(block2, value = 25F)
    engine.block.setFill(block2, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = block2,
        color = Color.fromRGBA(r = 0.3F, g = 0.6F, b = 0.9F, a = 1F),
    )
    engine.block.appendChild(parent = page2, child = block2)
    // highlight-android-add-blocks

    // highlight-android-horizontal-stack
    // Switch to a horizontal stack. Existing pages reposition left-to-right.
    engine.scene.setLayout(SceneLayout.HORIZONTAL_STACK)

    // Verify the layout type.
    val currentLayout = engine.scene.getLayout()
    println("Current layout: $currentLayout")
    // highlight-android-horizontal-stack

    // highlight-android-add-page
    // Append a new page to the existing stack. It snaps to the end with the
    // configured spacing.
    val page3 = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page3, value = 400F)
    engine.block.setHeight(page3, value = 300F)
    engine.block.appendChild(parent = stack, child = page3)

    // Add content to the new page.
    val block3 = engine.block.create(DesignBlockType.Graphic)
    val shape3 = engine.block.createShape(ShapeType.Rect)
    engine.block.setShape(block3, shape = shape3)
    engine.block.setWidth(block3, value = 350F)
    engine.block.setHeight(block3, value = 250F)
    engine.block.setPositionX(block3, value = 25F)
    engine.block.setPositionY(block3, value = 25F)
    engine.block.setFill(block3, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = block3,
        color = Color.fromRGBA(r = 0.9F, g = 0.5F, b = 0.3F, a = 1F),
    )
    engine.block.appendChild(parent = page3, child = block3)
    // highlight-android-add-page

    // highlight-android-reorder
    // Move page3 to the first position using insertChild.
    engine.block.insertChild(parent = stack, child = page3, index = 0)

    // Verify the new order.
    val pageOrder = engine.block.getChildren(stack)
    println("Page order after reordering: $pageOrder")
    // highlight-android-reorder

    // highlight-android-spacing
    // Update the spacing between stacked pages.
    engine.block.setFloat(stack, property = "stack/spacing", value = 40F)

    // Verify the spacing value.
    val updatedSpacing = engine.block.getFloat(stack, property = "stack/spacing")
    println("Updated spacing: $updatedSpacing")
    // highlight-android-spacing

    // highlight-android-free-layout
    // Switch back to a free layout to position pages manually.
    engine.scene.setLayout(SceneLayout.FREE)

    // Position a page directly; stacks no longer manage placement.
    val page = engine.block.findByType(DesignBlockType.Page).first()
    engine.block.setPositionX(page, value = 100F)
    engine.block.setPositionY(page, value = 200F)
    // highlight-android-free-layout

    engine.stop()
}
