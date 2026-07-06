package ly.img.editor.showcase

import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.ExportOptions
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.SceneLayout
import ly.img.engine.ShapeType
import ly.img.engine.ZoomAutoFitAxis

suspend fun blankCanvas(engine: Engine): BlankCanvasResult {
    // highlight-android-create-scene
    val scene = engine.scene.create(sceneLayout = SceneLayout.FREE)
    // highlight-android-create-scene

    // highlight-android-configure-page
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 800F)
    engine.block.setHeight(block = page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)
    // highlight-android-configure-page

    // highlight-android-background
    val pageFill = engine.block.createFill(FillType.Color)
    engine.block.setFill(block = page, fill = pageFill)
    engine.block.setFillSolidColor(
        block = page,
        color = Color.fromRGBA(r = 0.95F, g = 0.95F, b = 0.96F, a = 1F),
    )
    // highlight-android-background

    // highlight-android-add-block
    val block = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block = block, shape = engine.block.createShape(ShapeType.Star))
    val fill = engine.block.createFill(FillType.Color)
    engine.block.setFill(block = block, fill = fill)
    engine.block.setFillSolidColor(
        block = block,
        color = Color.fromRGBA(r = 0.27F, g = 0.52F, b = 0.96F, a = 1F),
    )
    engine.block.setWidth(block = block, value = 300F)
    engine.block.setHeight(block = block, value = 300F)
    engine.block.setPositionX(block = block, value = 250F)
    engine.block.setPositionY(block = block, value = 150F)
    engine.block.appendChild(parent = page, child = block)
    // highlight-android-add-block

    // highlight-android-zoom
    engine.scene.enableZoomAutoFit(
        block = page,
        axis = ZoomAutoFitAxis.BOTH,
        paddingLeft = 40F,
        paddingTop = 40F,
        paddingRight = 40F,
        paddingBottom = 40F,
    )
    // highlight-android-zoom

    val previewPng = engine.block.export(
        block = page,
        mimeType = MimeType.PNG,
        options = ExportOptions(targetWidth = 800F, targetHeight = 600F),
    )

    return BlankCanvasResult(
        page = page,
        block = block,
        previewPng = previewPng,
    )
}
