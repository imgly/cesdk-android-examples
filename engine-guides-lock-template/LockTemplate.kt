import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.GlobalScope
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode

fun lockTemplate(engine: Engine) {
    // highlight-android-creator-surface
    engine.editor.setRole("Creator")
    val creatorRole = engine.editor.getRole()

    require(creatorRole == "Creator")
    // highlight-android-creator-surface

    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 720F)
    engine.block.setHeight(page, value = 1080F)
    engine.block.appendChild(parent = scene, child = page)

    val background = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(background, "Template background")
    engine.block.setShape(background, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(background, value = 720F)
    engine.block.setHeight(background, value = 1080F)
    engine.block.setFill(background, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(background, color = Color.fromHex("#FFF7F9FC"))
    engine.block.appendChild(parent = page, child = background)

    val brandBanner = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(brandBanner, "Locked brand banner")
    engine.block.setShape(brandBanner, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(brandBanner, value = 640F)
    engine.block.setHeight(brandBanner, value = 220F)
    engine.block.setPositionX(brandBanner, value = 40F)
    engine.block.setPositionY(brandBanner, value = 48F)
    engine.block.setFill(brandBanner, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(brandBanner, color = Color.fromHex("#FF11203A"))
    engine.block.appendChild(parent = page, child = brandBanner)

    val brandName = engine.block.create(DesignBlockType.Text)
    engine.block.setName(brandName, "Locked brand name")
    engine.block.setWidthMode(brandName, mode = SizeMode.AUTO)
    engine.block.setHeightMode(brandName, mode = SizeMode.AUTO)
    engine.block.setPositionX(brandName, value = 88F)
    engine.block.setPositionY(brandName, value = 124F)
    engine.block.replaceText(brandName, text = "Brand Studio")
    engine.block.setTextColor(brandName, color = Color.fromHex("#FFFFFFFF"))
    engine.block.appendChild(parent = page, child = brandName)

    val headline = engine.block.create(DesignBlockType.Text)
    engine.block.setName(headline, "Editable campaign headline")
    engine.block.setWidthMode(headline, mode = SizeMode.AUTO)
    engine.block.setHeightMode(headline, mode = SizeMode.AUTO)
    engine.block.setPositionX(headline, value = 88F)
    engine.block.setPositionY(headline, value = 404F)
    engine.block.replaceText(headline, text = "Spring Launch")
    engine.block.setTextColor(headline, color = Color.fromHex("#FF11203A"))
    engine.block.setBackgroundColor(headline, color = Color.fromRGBA(231, 240, 255, 255))
    engine.block.setBackgroundColorEnabled(headline, enabled = true)
    engine.block.setFloat(headline, property = "backgroundColor/paddingLeft", value = 24F)
    engine.block.setFloat(headline, property = "backgroundColor/paddingTop", value = 20F)
    engine.block.setFloat(headline, property = "backgroundColor/paddingRight", value = 24F)
    engine.block.setFloat(headline, property = "backgroundColor/paddingBottom", value = 20F)
    engine.block.setFloat(headline, property = "backgroundColor/cornerRadius", value = 18F)
    engine.block.appendChild(parent = page, child = headline)

    // highlight-android-configure-scopes
    val templateScopes = listOf(
        "editor/select",
        "text/edit",
        "text/character",
        "fill/change",
        "layer/move",
        "layer/resize",
        "layer/rotate",
        "lifecycle/destroy",
    )

    listOf(page, background, brandBanner, brandName).forEach { lockedBlock ->
        templateScopes.forEach { scope ->
            engine.block.setScopeEnabled(block = lockedBlock, key = scope, enabled = false)
        }
    }

    engine.block.setScopeEnabled(block = headline, key = "editor/select", enabled = true)
    engine.block.setScopeEnabled(block = headline, key = "text/edit", enabled = true)
    engine.block.setScopeEnabled(block = headline, key = "text/character", enabled = false)
    engine.block.setScopeEnabled(block = headline, key = "fill/change", enabled = false)
    engine.block.setScopeEnabled(block = headline, key = "layer/move", enabled = false)
    engine.block.setScopeEnabled(block = headline, key = "layer/resize", enabled = false)
    engine.block.setScopeEnabled(block = headline, key = "layer/rotate", enabled = false)
    engine.block.setScopeEnabled(block = headline, key = "lifecycle/destroy", enabled = false)
    // highlight-android-configure-scopes

    // highlight-android-check-creator-permissions
    engine.editor.setRole("Creator")

    val creatorCanSelectBrandBanner = engine.block.isAllowedByScope(brandBanner, key = "editor/select")
    val creatorCanEditHeadline = engine.block.isAllowedByScope(headline, key = "text/edit")

    require(creatorCanSelectBrandBanner)
    require(creatorCanEditHeadline)
    // highlight-android-check-creator-permissions

    // highlight-android-adopter-surface
    engine.editor.setRole("Adopter")
    engine.editor.setGlobalScope(key = "editor/add", globalScope = GlobalScope.DENY)

    val adopterCanAddBlocks = engine.block.isAllowedByScope(headline, key = "editor/add")
    val adopterCanSelectBrandBanner = engine.block.isAllowedByScope(brandBanner, key = "editor/select")
    val adopterCanEditHeadline = engine.block.isAllowedByScope(headline, key = "text/edit")
    val adopterCanRestyleHeadline = engine.block.isAllowedByScope(headline, key = "text/character")
    val adopterCanMoveHeadline = engine.block.isAllowedByScope(headline, key = "layer/move")
    val adopterCanDeleteHeadline = engine.block.isAllowedByScope(headline, key = "lifecycle/destroy")

    require(!adopterCanAddBlocks)
    require(!adopterCanSelectBrandBanner)
    require(adopterCanEditHeadline)
    require(!adopterCanRestyleHeadline)
    require(!adopterCanMoveHeadline)
    require(!adopterCanDeleteHeadline)
    // highlight-android-adopter-surface

    // highlight-android-viewer-surface
    engine.editor.setRole("Viewer")
    val viewerRole = engine.editor.getRole()

    require(viewerRole == "Viewer")
    // highlight-android-viewer-surface

    engine.editor.setRole("Creator")
}
