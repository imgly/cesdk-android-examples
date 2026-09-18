import android.net.Uri
import ly.img.engine.AssetDefinition
import ly.img.engine.AssetPayload
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.FindAssetsQuery
import ly.img.engine.Font
import ly.img.engine.FontStyle
import ly.img.engine.FontWeight
import ly.img.engine.GlobalScope
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode
import ly.img.engine.Typeface
import java.nio.ByteBuffer

suspend fun enforceBrandGuidelines(engine: Engine): ByteBuffer {
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    val logo = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(logo, name = "Locked brand logo")
    engine.block.setShape(logo, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(logo, value = 56F)
    engine.block.setPositionY(logo, value = 56F)
    engine.block.setWidth(logo, value = 184F)
    engine.block.setHeight(logo, value = 96F)
    engine.block.setFill(logo, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(logo, color = Color.fromHex("#FF17233A"))
    engine.block.appendChild(parent = page, child = logo)

    val logoText = engine.block.create(DesignBlockType.Text)
    engine.block.setName(logoText, name = "Locked brand wordmark")
    engine.block.setWidthMode(logoText, mode = SizeMode.AUTO)
    engine.block.setHeightMode(logoText, mode = SizeMode.AUTO)
    engine.block.setPositionX(logoText, value = 88F)
    engine.block.setPositionY(logoText, value = 90F)
    engine.block.replaceText(logoText, text = "BRAND")
    engine.block.setTextColor(logoText, color = Color.fromHex("#FFFFFFFF"))
    engine.block.appendChild(parent = page, child = logoText)

    val legalText = engine.block.create(DesignBlockType.Text)
    engine.block.setName(legalText, name = "Locked legal text")
    engine.block.setWidth(legalText, value = 680F)
    engine.block.setHeightMode(legalText, mode = SizeMode.AUTO)
    engine.block.setPositionX(legalText, value = 56F)
    engine.block.setPositionY(legalText, value = 520F)
    engine.block.replaceText(legalText, text = "Approved brand disclaimer")
    engine.block.setTextFontSize(legalText, fontSize = 12F)
    engine.block.setTextColor(legalText, color = Color.fromHex("#FF17233A"))
    engine.block.appendChild(parent = page, child = legalText)

    val headline = engine.block.create(DesignBlockType.Text)
    engine.block.setName(headline, name = "Editable campaign headline")
    engine.block.setWidth(headline, value = 680F)
    engine.block.setHeightMode(headline, mode = SizeMode.AUTO)
    engine.block.setPositionX(headline, value = 56F)
    engine.block.setPositionY(headline, value = 236F)
    engine.block.replaceText(headline, text = "Brand Offer")
    engine.block.setTextFontSize(headline, fontSize = 28F)
    engine.block.setTextColor(headline, color = Color.fromHex("#FF1F5EFF"))
    engine.block.appendChild(parent = page, child = headline)

    // highlight-android-restrict-fonts
    // Replace this sample asset URI with your bundled brand font file.
    val brandRegular = Font(
        uri = Uri.parse(
            "file:///android_asset/imgly-assets/ly.img.typeface/fonts/FiraSans/FiraSans-Regular.ttf",
        ),
        subFamily = "Regular",
        weight = FontWeight.NORMAL,
        style = FontStyle.NORMAL,
    )
    val brandTypeface = Typeface(name = "Brand Sans", fonts = listOf(brandRegular))
    val typefaceSourceId = "ly.img.typeface"
    val brandTextBlocks = listOf(logoText, legalText, headline)

    if (typefaceSourceId in engine.asset.findAllSources()) {
        engine.asset.removeSource(sourceId = typefaceSourceId)
    }
    engine.asset.addLocalSource(sourceId = typefaceSourceId, supportedMimeTypes = emptyList())
    engine.asset.addAsset(
        sourceId = typefaceSourceId,
        asset = AssetDefinition(
            id = "brand-sans",
            label = mapOf("en" to "Brand Sans"),
            payload = AssetPayload(typeface = brandTypeface),
        ),
    )
    engine.asset.assetSourceContentsChanged(sourceId = typefaceSourceId)

    brandTextBlocks.forEach { textBlock ->
        engine.block.setTypeface(block = textBlock, typeface = brandTypeface)
    }
    // highlight-android-restrict-fonts

    // highlight-android-global-scope-defer
    val brandControlledScopes = listOf(
        "editor/select",
        "text/edit",
        "text/character",
        "fill/change",
        "layer/move",
        "layer/resize",
        "layer/rotate",
        "lifecycle/destroy",
        "lifecycle/duplicate",
    )

    brandControlledScopes.forEach { scope ->
        engine.editor.setGlobalScope(key = scope, globalScope = GlobalScope.DEFER)
    }
    // highlight-android-global-scope-defer

    // highlight-android-lock-brand-elements
    // Use the IDs of brand blocks that already exist in your scene.
    val lockedBrandBlocks = listOf(logo, logoText, legalText)
    lockedBrandBlocks.forEach { brandBlock ->
        brandControlledScopes.forEach { scope ->
            engine.block.setScopeEnabled(block = brandBlock, key = scope, enabled = false)
        }
    }
    // highlight-android-lock-brand-elements

    // highlight-android-editable-content
    // Use the ID of an editable text block that already exists in your scene.
    engine.block.setScopeEnabled(block = headline, key = "editor/select", enabled = true)
    engine.block.setScopeEnabled(block = headline, key = "text/edit", enabled = true)
    engine.block.setScopeEnabled(block = headline, key = "text/character", enabled = true)
    engine.block.setScopeEnabled(block = headline, key = "fill/change", enabled = true)
    engine.block.setScopeEnabled(block = headline, key = "layer/move", enabled = false)
    engine.block.setScopeEnabled(block = headline, key = "layer/resize", enabled = false)
    engine.block.setScopeEnabled(block = headline, key = "layer/rotate", enabled = false)
    engine.block.setScopeEnabled(block = headline, key = "lifecycle/destroy", enabled = false)
    // highlight-android-editable-content

    // highlight-android-validate-compliance
    val approvedTypefaces = engine.asset.findAssets(
        sourceId = typefaceSourceId,
        query = FindAssetsQuery(perPage = 10, page = 0),
    )
    val logoCanMove = engine.block.isAllowedByScope(block = logo, key = "layer/move")
    val logoCanBeDeleted = engine.block.isAllowedByScope(block = logo, key = "lifecycle/destroy")
    val legalTextCanBeEdited = engine.block.isAllowedByScope(block = legalText, key = "text/edit")
    val headlineCanBeEdited = engine.block.isAllowedByScope(block = headline, key = "text/edit")
    val headlineCanChangeTypeface = engine.block.isAllowedByScope(block = headline, key = "text/character")
    val approvedTypefaceDefinitions = approvedTypefaces.assets.mapNotNull { it.payload.typeface }.toSet()
    val brandTextUsesApprovedTypeface = brandTextBlocks.all { textBlock ->
        val typefaces = engine.block.getTypefaces(textBlock)
        typefaces.isNotEmpty() && typefaces.all { it in approvedTypefaceDefinitions }
    }

    check(approvedTypefaces.assets.size == 1)
    check(approvedTypefaceDefinitions.size == 1)
    check(brandTextUsesApprovedTypeface)
    check(!logoCanMove)
    check(!logoCanBeDeleted)
    check(!legalTextCanBeEdited)
    check(headlineCanBeEdited)
    check(headlineCanChangeTypeface)
    // highlight-android-validate-compliance

    // highlight-android-export
    val exportedPng = engine.block.export(block = page, mimeType = MimeType.PNG)
    // highlight-android-export

    return exportedPng
}
