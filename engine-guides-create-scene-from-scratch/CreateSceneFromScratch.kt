import ly.img.engine.*

fun createSceneFromScratch() {
    val engine = Engine.also { it.start() }
    engine.bindOffscreen(width = 100, height = 100)

    // highlight-create
    val scene = engine.scene.create()
    // highlight-create

    // highlight-add-page
    val page = engine.block.create(DesignBlockType.PAGE)
    engine.block.appendChild(parent = scene, child = page)
    // highlight-add-page

    // highlight-add-star
    val star = engine.block.create(DesignBlockType.STAR_SHAPE)
    engine.block.appendChild(parent = page, child = star)
    // highlight-add-star

    engine.stop()
}
