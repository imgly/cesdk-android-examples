import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType

suspend fun grouping(engine: Engine) {
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    // highlight-android-create-blocks
    val block1 = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block1, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(block1, value = 120F)
    engine.block.setHeight(block1, value = 120F)
    engine.block.setPositionX(block1, value = 200F)
    engine.block.setPositionY(block1, value = 240F)
    engine.block.setFill(block1, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = block1,
        color = Color.fromRGBA(r = 0.4F, g = 0.6F, b = 0.9F, a = 1.0F),
    )
    engine.block.appendChild(parent = page, child = block1)
    // highlight-android-create-blocks

    val block2 = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block2, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(block2, value = 120F)
    engine.block.setHeight(block2, value = 120F)
    engine.block.setPositionX(block2, value = 340F)
    engine.block.setPositionY(block2, value = 240F)
    engine.block.setFill(block2, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = block2,
        color = Color.fromRGBA(r = 0.9F, g = 0.5F, b = 0.4F, a = 1.0F),
    )
    engine.block.appendChild(parent = page, child = block2)

    val block3 = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block3, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(block3, value = 120F)
    engine.block.setHeight(block3, value = 120F)
    engine.block.setPositionX(block3, value = 480F)
    engine.block.setPositionY(block3, value = 240F)
    engine.block.setFill(block3, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = block3,
        color = Color.fromRGBA(r = 0.5F, g = 0.8F, b = 0.5F, a = 1.0F),
    )
    engine.block.appendChild(parent = page, child = block3)

    // highlight-android-check-groupable
    val blocks = listOf(block1, block2, block3)
    val canGroup = engine.block.isGroupable(blocks)
    // highlight-android-check-groupable

    // highlight-android-create-group
    val group = if (canGroup) {
        engine.block.group(blocks)
    } else {
        error("Select blocks that can be grouped before calling group(...).")
    }
    engine.block.setSelected(group, selected = true)
    // highlight-android-create-group

    // highlight-android-enter-group
    engine.block.enterGroup(group)
    // enterGroup selects the first member by default; select another member when needed.
    engine.block.select(block2)
    // highlight-android-enter-group

    // highlight-android-exit-group
    engine.block.exitGroup(block2)
    // highlight-android-exit-group

    // highlight-android-find-groups
    val allGroups = engine.block.findByType(DesignBlockType.Group)
    val groupType = engine.block.getType(group)
    val members = engine.block.getChildren(group)
    // highlight-android-find-groups

    // highlight-android-ungroup
    engine.block.ungroup(group)
    val groupsAfterUngroup = engine.block.findByType(DesignBlockType.Group)
    // highlight-android-ungroup

    check(allGroups.isNotEmpty())
    check(groupType == DesignBlockType.Group.key)
    check(members.size == blocks.size)
    check(groupsAfterUngroup.isEmpty())
}
