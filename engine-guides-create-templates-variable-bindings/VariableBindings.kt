import android.net.Uri
import android.util.Log
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.EngineException
import ly.img.engine.FillType
import ly.img.engine.Font
import ly.img.engine.FontStyle
import ly.img.engine.FontWeight
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode
import ly.img.engine.Typeface
import ly.img.engine.VariableType
import java.nio.ByteBuffer

private const val TAG = "VariableBindings"

data class VariableBindings(
    val accentType: VariableType,
    val cornerRadius: Float,
    val colorVariables: List<String>,
    val textVariables: List<String>,
    val updatedCardColor: Color,
    val cardFillBinding: String?,
    val opacityBinding: String?,
    val shapeBindings: List<String>,
    val boundWriteErrorCode: String?,
    val detachedCornerRadius: Float,
    val boundCornerRadius: Float,
    val renamedBinding: String?,
    val renamedText: String,
    val renameErrorCodes: List<String>,
    val typeMismatchErrorCode: String?,
    val bindingTypeErrorCode: String?,
    val previewPng: ByteBuffer,
)

suspend fun variableBindings(engine: Engine): VariableBindings {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 800F)
    engine.block.setHeight(block = page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    try {
        // highlight-android-create-variables
        // One variable per type. A setter creates the variable when the key is new.
        val bundledAssetsBaseUri = "file:///android_asset/imgly-assets"
        val brandTypeface = Typeface(
            name = "Fira Sans",
            fonts = listOf(
                Font(
                    uri = Uri.parse("$bundledAssetsBaseUri/ly.img.typeface/fonts/FiraSans/FiraSans-Regular.ttf"),
                    subFamily = "Regular",
                    weight = FontWeight.NORMAL,
                    style = FontStyle.NORMAL,
                ),
            ),
        )
        engine.variable.setColor(key = "accent", value = Color.fromRGBA(r = 0.48F, g = 0.23F, b = 0.93F, a = 1F))
        engine.variable.setNumber(key = "cornerRadius", value = 40F)
        engine.variable.setImage(key = "heroImage", uri = "https://img.ly/static/ubq_samples/sample_1.jpg")
        engine.variable.setTypeface(key = "headline", typeface = brandTypeface)
        engine.variable.set(key = "title", value = "Summer Sale")
        // highlight-android-create-variables

        // highlight-android-read-types
        val accentType = engine.variable.getType(key = "accent") // VariableType.COLOR
        val cornerRadius = engine.variable.getNumber(key = "cornerRadius") // 40
        val colorVariables = engine.variable.findAllOfType(type = VariableType.COLOR) // [accent]
        // findAll() lists text variables only
        val textVariables = engine.variable.findAll() // [title]
        Log.i(TAG, "$accentType, $cornerRadius, $colorVariables, $textVariables")
        // highlight-android-read-types

        // highlight-android-create-blocks
        // A card with a color fill and a rect shape
        val card = engine.block.create(DesignBlockType.Graphic)
        engine.block.setShape(block = card, shape = engine.block.createShape(ShapeType.Rect))
        engine.block.setFill(block = card, fill = engine.block.createFill(FillType.Color))
        engine.block.setWidth(block = card, value = 720F)
        engine.block.setHeight(block = card, value = 520F)
        engine.block.setPositionX(block = card, value = 40F)
        engine.block.setPositionY(block = card, value = 40F)
        engine.block.appendChild(parent = page, child = card)
        val cardFill = engine.block.getFill(block = card)
        val cardShape = engine.block.getShape(block = card)

        // An image with an image fill
        val image = engine.block.create(DesignBlockType.Graphic)
        engine.block.setShape(block = image, shape = engine.block.createShape(ShapeType.Rect))
        val imageFill = engine.block.createFill(FillType.Image)
        engine.block.setFill(block = image, fill = imageFill)
        engine.block.setWidth(block = image, value = 640F)
        engine.block.setHeight(block = image, value = 300F)
        engine.block.setPositionX(block = image, value = 80F)
        engine.block.setPositionY(block = image, value = 80F)
        engine.block.appendChild(parent = page, child = image)

        // A headline that shows the text variable
        val text = engine.block.create(DesignBlockType.Text)
        engine.block.replaceText(block = text, text = "{{title}}")
        engine.block.setWidth(block = text, value = 640F)
        engine.block.setHeightMode(block = text, mode = SizeMode.AUTO)
        engine.block.setPositionX(block = text, value = 80F)
        engine.block.setPositionY(block = text, value = 420F)
        engine.block.appendChild(parent = page, child = text)
        // highlight-android-create-blocks

        // highlight-android-bind-properties
        // Each binding applies the current value of the variable at once
        engine.block.setVariableBinding(block = cardFill, property = "fill/color/value", key = "accent")
        for (corner in listOf("TL", "TR", "BL", "BR")) {
            engine.block.setVariableBinding(
                block = cardShape,
                property = "shape/rect/cornerRadius$corner",
                key = "cornerRadius",
            )
        }
        engine.block.setVariableBinding(block = imageFill, property = "fill/image/imageFileURI", key = "heroImage")
        engine.block.setVariableBinding(block = text, property = "text/typeface", key = "headline")
        // highlight-android-bind-properties

        // highlight-android-update-variables
        // Every bound property follows the new values
        engine.variable.setColor(key = "accent", value = Color.fromRGBA(r = 0.05F, g = 0.55F, b = 0.55F, a = 1F))
        engine.variable.setNumber(key = "cornerRadius", value = 24F)
        engine.variable.setImage(key = "heroImage", uri = "https://img.ly/static/ubq_samples/sample_2.jpg")
        val updatedCardColor = engine.block.getColor(block = cardFill, property = "fill/color/value")
        // highlight-android-update-variables

        // highlight-android-read-bindings
        val cardFillBinding = engine.block.getVariableBinding(block = cardFill, property = "fill/color/value") // accent
        val opacityBinding = engine.block.getVariableBinding(block = card, property = "opacity") // null
        // The four corner radius properties of the shape
        val shapeBindings = engine.block.findAllVariableBindings(block = cardShape)
        // highlight-android-read-bindings

        // highlight-android-bound-property-locked
        // A bound property changes only through its variable
        val boundWriteErrorCode = try {
            engine.block.setColor(
                block = cardFill,
                property = "fill/color/value",
                value = Color.fromRGBA(r = 1F, g = 0F, b = 0F, a = 1F),
            )
            null
        } catch (error: EngineException) {
            // EngineErrorCode.BLOCK_PROPERTY_BOUND
            error.code
        }
        // highlight-android-bound-property-locked

        // highlight-android-remove-binding
        // The bottom corners keep their current radius and stop following
        engine.block.removeVariableBinding(block = cardShape, property = "shape/rect/cornerRadiusBL")
        engine.block.removeVariableBinding(block = cardShape, property = "shape/rect/cornerRadiusBR")
        engine.variable.setNumber(key = "cornerRadius", value = 60F)
        val detachedCornerRadius = engine.block.getFloat(block = cardShape, property = "shape/rect/cornerRadiusBL") // 24
        val boundCornerRadius = engine.block.getFloat(block = cardShape, property = "shape/rect/cornerRadiusTL") // 60
        // highlight-android-remove-binding

        // highlight-android-rename
        // Bindings and {{tokens}} in text follow the new name
        engine.variable.rename(from = "accent", to = "brand/accent")
        engine.variable.rename(from = "title", to = "campaign/title")
        val renamedBinding = engine.block.getVariableBinding(block = cardFill, property = "fill/color/value")
        val renamedText = engine.block.getString(block = text, property = "text/text") // {{campaign/title}}
        // highlight-android-rename

        // highlight-android-rename-errors
        // BLOCK.VARIABLE_NAME_INVALID for the first three names,
        // BLOCK.VARIABLE_NAME_TAKEN for the name of another variable
        val renameErrorCodes = listOf("", " padded", "a{{b", "campaign/title").mapNotNull { name ->
            try {
                engine.variable.rename(from = "brand/accent", to = name)
                null
            } catch (error: EngineException) {
                error.code
            }
        }
        // highlight-android-rename-errors

        // highlight-android-type-errors
        // A color variable has no number value
        val typeMismatchErrorCode = try {
            engine.variable.getNumber(key = "brand/accent")
            null
        } catch (error: EngineException) {
            // EngineErrorCode.BLOCK_VARIABLE_TYPE_MISMATCH
            error.code
        }
        // Opacity is a number property, so it cannot bind to a color variable
        val bindingTypeErrorCode = try {
            engine.block.setVariableBinding(block = card, property = "opacity", key = "brand/accent")
            null
        } catch (error: EngineException) {
            // EngineErrorCode.BLOCK_VARIABLE_BINDING_UNSUPPORTED
            error.code
        }
        // highlight-android-type-errors

        // highlight-android-export
        val previewPng = engine.block.export(block = page, mimeType = MimeType.PNG)
        // highlight-android-export

        return VariableBindings(
            accentType = accentType,
            cornerRadius = cornerRadius,
            colorVariables = colorVariables,
            textVariables = textVariables,
            updatedCardColor = updatedCardColor,
            cardFillBinding = cardFillBinding,
            opacityBinding = opacityBinding,
            shapeBindings = shapeBindings,
            boundWriteErrorCode = boundWriteErrorCode,
            detachedCornerRadius = detachedCornerRadius,
            boundCornerRadius = boundCornerRadius,
            renamedBinding = renamedBinding,
            renamedText = renamedText,
            renameErrorCodes = renameErrorCodes,
            typeMismatchErrorCode = typeMismatchErrorCode,
            bindingTypeErrorCode = bindingTypeErrorCode,
            previewPng = previewPng,
        )
    } finally {
        // Variables belong to the engine and outlive the scene
        listOf("accent", "brand/accent", "cornerRadius", "heroImage", "headline", "title", "campaign/title")
            .filter { key -> runCatching { engine.variable.getType(key) }.isSuccess }
            .forEach(engine.variable::remove)
    }
}
