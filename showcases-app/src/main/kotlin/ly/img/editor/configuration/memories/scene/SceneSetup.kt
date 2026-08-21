package ly.img.editor.configuration.memories.scene

import ly.img.editor.configuration.memories.util.PAGE_HEIGHT
import ly.img.editor.configuration.memories.util.PAGE_WIDTH
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType

/** Build the slideshow scene in code (no serialized blob): a video scene with one sized page. */
internal fun createSlideshowScene(engine: Engine): DesignBlock {
    val scene = engine.scene.createForVideo()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setWidth(page, PAGE_WIDTH)
    engine.block.setHeight(page, PAGE_HEIGHT)

    val fill = engine.block.createFill(FillType.Color)
    engine.block.setColor(fill, "fill/color/value", Color.fromRGBA(0f, 0f, 0f, 1f))
    engine.block.setFill(page, fill)
    return page
}

/**
 * The persistent, single-purpose tracks of the slideshow, bottom-to-top in render order. The per-
 * slide media tracks are created later (in [createMainImageSequence]) and stack on top of these.
 */
internal data class TrackReferences(
    val textTrack: DesignBlock,
    val backgroundTrack: DesignBlock,
    val backgroundBlock: DesignBlock,
    val matteTrack: DesignBlock,
    val matteBlock: DesignBlock,
)

internal fun setupTracks(
    engine: Engine,
    page: DesignBlock,
): TrackReferences {
    // background = per-style backdrop (hidden by default); matte = black rectangle so a crossfade
    // fades to black instead of revealing the backdrop. Appended first → they render behind the
    // media. The text (title) track sits just above them; the media tracks are appended on top later.
    val backgroundTrack = engine.block.create(DesignBlockType.Track)
    val matteTrack = engine.block.create(DesignBlockType.Track)
    val textTrack = engine.block.create(DesignBlockType.Track)

    // Tags let the rest of the kit re-locate tracks by role instead of retaining stale block ids.
    tagSlideshowTracks(engine, textTrack = textTrack, backgroundTrack = backgroundTrack, matteTrack = matteTrack)

    engine.block.appendChild(parent = page, child = backgroundTrack)
    engine.block.appendChild(parent = page, child = matteTrack)
    engine.block.appendChild(parent = page, child = textTrack)

    val backgroundBlock = fullPageBlock(engine, page)
    engine.block.setVisible(backgroundBlock, false)
    engine.block.appendChild(parent = backgroundTrack, child = backgroundBlock)

    val matteBlock = fullPageBlock(engine, page)
    val matteFill = engine.block.createFill(FillType.Color)
    engine.block.setColor(matteFill, "fill/color/value", Color.fromRGBA(0f, 0f, 0f, 1f))
    engine.block.setFill(matteBlock, matteFill)
    engine.block.appendChild(parent = matteTrack, child = matteBlock)

    return TrackReferences(textTrack, backgroundTrack, backgroundBlock, matteTrack, matteBlock)
}

private fun fullPageBlock(
    engine: Engine,
    page: DesignBlock,
): DesignBlock {
    val block = engine.block.create(DesignBlockType.Graphic)
    engine.block.setScopeEnabled(block, "editor/select", false)
    engine.block.setShape(block, engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(block, engine.block.getWidth(page))
    engine.block.setHeight(block, engine.block.getHeight(page))
    return block
}
