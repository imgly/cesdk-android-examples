import android.net.Uri
import ly.img.engine.Color
import ly.img.engine.ContentFillMode
import ly.img.engine.DesignBlockType
import ly.img.engine.DesignUnit
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.Font
import ly.img.engine.FontStyle
import ly.img.engine.FontUnit
import ly.img.engine.FontWeight
import ly.img.engine.GlobalScope
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode
import ly.img.engine.Typeface

suspend fun createTemplateFromScratch(engine: Engine): TemplateFromScratchResult {
    val scopeKeys = listOf("layer/move", "layer/resize", "fill/change")
    val previousGlobalScopes = scopeKeys.associateWith(engine.editor::getGlobalScope)
    val previousVariables = engine.variable.findAll().associateWith { key ->
        engine.variable.get(key)
    }

    return try {
        previousVariables.keys.forEach(engine.variable::remove)

        // highlight-android-create-scene
        val scene = engine.scene.create(designUnit = DesignUnit.PIXEL, fontSizeUnit = FontUnit.PIXEL)

        val page = engine.block.create(DesignBlockType.Page)
        engine.block.setWidth(page, value = 800F)
        engine.block.setHeight(page, value = 1000F)
        engine.block.appendChild(parent = scene, child = page)
        // highlight-android-create-scene

        // highlight-android-add-background
        val backgroundFill = engine.block.createFill(FillType.Color)
        engine.block.setColor(
            block = backgroundFill,
            property = "fill/color/value",
            value = Color.fromRGBA(r = 0.98F, g = 0.98F, b = 0.99F, a = 1F),
        )
        engine.block.setFill(block = page, fill = backgroundFill)
        // highlight-android-add-background

        // highlight-android-add-text
        val bundledAssetsBaseUri = "file:///android_asset/imgly-assets"
        val brandTypeface = Typeface(
            name = "Brand Sans",
            fonts = listOf(
                Font(
                    uri = Uri.parse("$bundledAssetsBaseUri/ly.img.typeface/fonts/FiraSans/FiraSans-Regular.ttf"),
                    subFamily = "Regular",
                    weight = FontWeight.NORMAL,
                    style = FontStyle.NORMAL,
                ),
                Font(
                    uri = Uri.parse("$bundledAssetsBaseUri/ly.img.typeface/fonts/FiraSans/FiraSans-Bold.ttf"),
                    subFamily = "Bold",
                    weight = FontWeight.BOLD,
                    style = FontStyle.NORMAL,
                ),
            ),
        )
        val brandRegularFont = brandTypeface.fonts.firstOrNull {
            it.weight == FontWeight.NORMAL && it.style == FontStyle.NORMAL
        } ?: error("Brand Sans must include a regular font.")

        val headline = engine.block.create(DesignBlockType.Text)
        engine.block.replaceText(headline, text = "{{title}}")
        engine.block.setFont(headline, fontFileUri = brandRegularFont.uri, typeface = brandTypeface)
        engine.block.setTextFontSize(headline, fontSize = 72F)
        engine.block.setTextColor(headline, color = Color.fromHex("#171717"))
        engine.block.setPositionX(headline, value = 72F)
        engine.block.setPositionY(headline, value = 96F)
        engine.block.setWidth(headline, value = 656F)
        engine.block.setHeightMode(headline, mode = SizeMode.AUTO)
        engine.block.appendChild(parent = page, child = headline)

        val subtitle = engine.block.create(DesignBlockType.Text)
        engine.block.replaceText(subtitle, text = "{{subtitle}}")
        engine.block.setFont(subtitle, fontFileUri = brandRegularFont.uri, typeface = brandTypeface)
        engine.block.setTextFontSize(subtitle, fontSize = 32F)
        engine.block.setTextColor(subtitle, color = Color.fromHex("#525252"))
        engine.block.setPositionX(subtitle, value = 72F)
        engine.block.setPositionY(subtitle, value = 194F)
        engine.block.setWidth(subtitle, value = 620F)
        engine.block.setHeightMode(subtitle, mode = SizeMode.AUTO)
        engine.block.appendChild(parent = page, child = subtitle)

        val cta = engine.block.create(DesignBlockType.Text)
        engine.block.replaceText(cta, text = "{{cta}}")
        engine.block.setFont(cta, fontFileUri = brandRegularFont.uri, typeface = brandTypeface)
        engine.block.setTextFontSize(cta, fontSize = 36F)
        engine.block.setTextColor(cta, color = Color.fromHex("#171717"))
        engine.block.setPositionX(cta, value = 72F)
        engine.block.setPositionY(cta, value = 858F)
        engine.block.setWidth(cta, value = 400F)
        engine.block.setHeightMode(cta, mode = SizeMode.AUTO)
        engine.block.appendChild(parent = page, child = cta)
        // highlight-android-add-text

        // highlight-android-add-variables
        engine.variable.set(key = "title", value = "Summer Sale")
        engine.variable.set(key = "subtitle", value = "Up to 50% off all items")
        engine.variable.set(key = "cta", value = "Learn More")

        val variableNames = engine.variable.findAll()
        // highlight-android-add-variables

        // highlight-android-add-graphic
        val imageBlock = engine.block.create(DesignBlockType.Graphic)
        engine.block.setName(imageBlock, name = "hero-image")
        engine.block.setShape(imageBlock, shape = engine.block.createShape(ShapeType.Rect))
        engine.block.setPositionX(imageBlock, value = 72F)
        engine.block.setPositionY(imageBlock, value = 300F)
        engine.block.setWidth(imageBlock, value = 656F)
        engine.block.setHeight(imageBlock, value = 500F)
        engine.block.setContentFillMode(imageBlock, mode = ContentFillMode.COVER)

        val imageFill = engine.block.createFill(FillType.Image)
        engine.block.setString(
            block = imageFill,
            property = "fill/image/imageFileURI",
            value = "$bundledAssetsBaseUri/ly.img.image/images/sample_1.jpg",
        )
        engine.block.setFill(block = imageBlock, fill = imageFill)
        engine.block.appendChild(parent = page, child = imageBlock)
        // highlight-android-add-graphic

        // highlight-android-configure-placeholder
        val placeholderFill = engine.block.getFill(imageBlock)
        if (engine.block.supportsPlaceholderBehavior(placeholderFill)) {
            engine.block.setPlaceholderBehaviorEnabled(placeholderFill, enabled = true)
        }

        engine.block.setPlaceholderEnabled(imageBlock, enabled = true)
        if (engine.block.supportsPlaceholderControls(imageBlock)) {
            engine.block.setPlaceholderControlsOverlayEnabled(imageBlock, enabled = true)
            engine.block.setPlaceholderControlsButtonEnabled(imageBlock, enabled = true)
        }
        // highlight-android-configure-placeholder

        // highlight-android-apply-constraints
        engine.editor.setGlobalScope(key = "layer/move", globalScope = GlobalScope.DEFER)
        engine.editor.setGlobalScope(key = "layer/resize", globalScope = GlobalScope.DEFER)
        engine.editor.setGlobalScope(key = "fill/change", globalScope = GlobalScope.DEFER)

        listOf(headline, subtitle, cta, imageBlock).forEach { block ->
            engine.block.setScopeEnabled(block = block, key = "layer/move", enabled = false)
            engine.block.setScopeEnabled(block = block, key = "layer/resize", enabled = false)
        }
        engine.block.setScopeEnabled(imageBlock, key = "fill/change", enabled = true)
        // highlight-android-apply-constraints

        // highlight-android-save-template
        engine.block.forceLoadResources(blocks = listOf(page))
        val templateString = engine.scene.saveToString(scene = scene)
        val templateArchive = engine.scene.saveToArchive(scene = scene)
        // highlight-android-save-template

        val previewPng = engine.block.export(page, mimeType = MimeType.PNG)

        TemplateFromScratchResult(
            scene = scene,
            page = page,
            titleBlock = headline,
            imageBlock = imageBlock,
            templateString = templateString,
            templateArchive = templateArchive,
            previewPng = previewPng,
            variables = variableNames,
            placeholderBehaviorEnabled = engine.block.isPlaceholderBehaviorEnabled(placeholderFill),
            placeholderEnabled = engine.block.isPlaceholderEnabled(imageBlock),
            overlayEnabled = engine.block.isPlaceholderControlsOverlayEnabled(imageBlock),
            buttonEnabled = engine.block.isPlaceholderControlsButtonEnabled(imageBlock),
            imageCanMove = engine.block.isAllowedByScope(imageBlock, key = "layer/move"),
            imageCanResize = engine.block.isAllowedByScope(imageBlock, key = "layer/resize"),
            imageFillCanChange = engine.block.isAllowedByScope(imageBlock, key = "fill/change"),
        )
    } finally {
        previousGlobalScopes.forEach { (key, scope) ->
            engine.editor.setGlobalScope(key = key, globalScope = scope)
        }
        engine.variable.findAll().forEach { key ->
            runCatching { engine.variable.remove(key) }
        }
        previousVariables.forEach(engine.variable::set)
    }
}
