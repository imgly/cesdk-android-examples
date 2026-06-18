import android.net.Uri
import ly.img.engine.ContentFillMode
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.ExportOptions
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import java.nio.ByteBuffer

suspend fun createStickers(engine: Engine): ByteBuffer {
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 450F)
    engine.block.setHeight(page, value = 250F)
    engine.block.appendChild(parent = scene, child = page)

    // highlight-android-create-from-image
    fun createStickerFromImage(
        engine: Engine,
        parent: DesignBlock,
        imageUri: Uri,
        x: Float,
        y: Float,
        size: Float,
    ): DesignBlock {
        val sticker = engine.block.create(DesignBlockType.Graphic)
        engine.block.setShape(sticker, shape = engine.block.createShape(ShapeType.Rect))

        val imageFill = engine.block.createFill(FillType.Image)
        engine.block.setUri(
            block = imageFill,
            property = "fill/image/imageFileURI",
            value = imageUri,
        )
        engine.block.setFill(sticker, fill = imageFill)

        engine.block.setWidth(sticker, value = size)
        engine.block.setHeight(sticker, value = size)
        engine.block.setPositionX(sticker, value = x)
        engine.block.setPositionY(sticker, value = y)

        if (engine.block.supportsContentFillMode(sticker)) {
            engine.block.setContentFillMode(sticker, mode = ContentFillMode.CONTAIN)
        }
        engine.block.setKind(sticker, kind = "sticker")
        engine.block.appendChild(parent = parent, child = sticker)

        return sticker
    }
    // highlight-android-create-from-image

    // highlight-android-add-stickers
    val firstStickerUri = Uri.parse(
        "file:///android_asset/imgly-assets/" +
            "ly.img.sticker/images/emoticons/imgly_sticker_emoticons_grin.svg",
    )
    val secondStickerUri = Uri.parse(
        "file:///android_asset/imgly-assets/" +
            "ly.img.sticker/images/emoticons/imgly_sticker_emoticons_blush.svg",
    )

    val firstSticker = createStickerFromImage(
        engine = engine,
        parent = page,
        imageUri = firstStickerUri,
        x = 95F,
        y = 85F,
        size = 80F,
    )

    val secondSticker = createStickerFromImage(
        engine = engine,
        parent = page,
        imageUri = secondStickerUri,
        x = 275F,
        y = 85F,
        size = 80F,
    )
    // highlight-android-add-stickers

    check(engine.block.getKind(firstSticker) == "sticker")
    check(engine.block.getKind(secondSticker) == "sticker")
    check(engine.block.getContentFillMode(firstSticker) == ContentFillMode.CONTAIN)
    check(engine.block.getWidth(firstSticker) == 80F)
    check(engine.block.getHeight(secondSticker) == 80F)

    engine.block.forceLoadResources(listOf(page, firstSticker, secondSticker))
    val exportedPage = engine.block.export(
        block = page,
        mimeType = MimeType.PNG,
        options = ExportOptions(targetWidth = 900F, targetHeight = 500F),
    )

    return exportedPage
}
