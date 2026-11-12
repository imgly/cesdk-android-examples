import android.util.Log
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.EngineException
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode
import ly.img.engine.ValueSetId
import ly.img.engine.VariableTarget
import java.nio.ByteBuffer

private const val TAG = "ValueSets"

data class ValueSets(
    val valueSets: List<ValueSetId>,
    val valueSetName: String,
    val shownName: String,
    val defaultName: String,
    val shownAccent: Color,
    val writtenName: String,
    val defaultAfterWrite: String,
    val cardColor: Color,
    val previewPng: ByteBuffer,
    val activeSetKeepsId: Boolean,
    val foundSetKeepsId: Boolean,
    val cityAfterLoad: String,
    val missingVariableErrorCode: String?,
    val nameTakenErrorCode: String?,
    val activeSetAfterDestroy: ValueSetId?,
    val nameAfterDestroy: String,
)

suspend fun valueSets(engine: Engine): ValueSets {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 800F)
    engine.block.setHeight(block = page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    // highlight-android-scene-variables
    // Scene variables belong to the scene. A save writes them, and undo reverts them.
    engine.variable.set(key = "first_name", value = "Ada")
    engine.variable.setColor(key = "accent", value = Color.fromRGBA(r = 0.48F, g = 0.23F, b = 0.93F, a = 1F))

    val card = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block = card, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setFill(block = card, fill = engine.block.createFill(FillType.Color))
    engine.block.setWidth(block = card, value = 720F)
    engine.block.setHeight(block = card, value = 520F)
    engine.block.setPositionX(block = card, value = 40F)
    engine.block.setPositionY(block = card, value = 40F)
    engine.block.appendChild(parent = page, child = card)
    val cardFill = engine.block.getFill(block = card)
    engine.block.setVariableBinding(block = cardFill, property = "fill/color/value", key = "accent")

    val text = engine.block.create(DesignBlockType.Text)
    engine.block.replaceText(block = text, text = "Hello {{first_name}}")
    engine.block.setWidth(block = text, value = 640F)
    engine.block.setHeightMode(block = text, mode = SizeMode.AUTO)
    engine.block.setPositionX(block = text, value = 80F)
    engine.block.setPositionY(block = text, value = 80F)
    engine.block.appendChild(parent = page, child = text)
    // highlight-android-scene-variables

    // highlight-android-create-value-set
    // A value set holds alternative values for some scene variables.
    val longNames = engine.variable.createValueSet(name = "Long names")
    engine.variable.set(
        key = "first_name",
        value = "Maximiliane Huber-Schwarzenberg",
        target = VariableTarget.ValueSet(longNames),
    )
    val valueSets = engine.variable.findAllValueSets() // [longNames]
    val valueSetName = engine.variable.getValueSetName(set = longNames) // Long names
    // highlight-android-create-value-set

    // highlight-android-activate-value-set
    // The active set wins over the default values in text, bound properties and getters.
    engine.variable.setActiveValueSet(set = longNames)
    val shownName = engine.variable.get(key = "first_name") // Maximiliane Huber-Schwarzenberg
    val defaultName = engine.variable.get(key = "first_name", target = VariableTarget.Default) // Ada
    // A variable that the set has no value for shows its default.
    val shownAccent = engine.variable.getColor(key = "accent") // the purple default
    // highlight-android-activate-value-set

    // highlight-android-write-through
    // A setter without a target writes the layer that the getter reads: the active set
    // when it holds the key, else the default value.
    engine.variable.set(key = "first_name", value = "Maximiliane Huber")
    val writtenName = engine.variable.get(key = "first_name") // Maximiliane Huber
    val defaultAfterWrite = engine.variable.get(key = "first_name", target = VariableTarget.Default) // Ada
    engine.variable.setColor(key = "accent", value = Color.fromRGBA(r = 0.95F, g = 0.45F, b = 0.1F, a = 1F))
    val cardColor = engine.block.getColor(block = cardFill, property = "fill/color/value") // the new orange default
    // highlight-android-write-through

    // highlight-android-export
    // Exports show the values of the active set, as the canvas does.
    val previewPng = engine.block.export(block = page, mimeType = MimeType.PNG)
    // highlight-android-export

    // highlight-android-save-and-load
    // A save holds the default values, the value sets and the active set.
    val saved = engine.scene.saveToString(scene = scene)
    engine.scene.load(scene = saved)
    // The scene saves the id of a set, so it stays the same across a load.
    val activeSetKeepsId = engine.variable.getActiveValueSet() == longNames // true
    val foundSetKeepsId = engine.variable.findValueSetByName(name = "Long names") == longNames // true
    // highlight-android-save-and-load

    // highlight-android-before-load
    // A variable set while no scene exists waits for the next scene. That scene takes it over
    // its own value, for example to fill a template with the data of one record.
    engine.block.destroy(block = requireNotNull(engine.scene.get()))
    engine.variable.set(key = "city", value = "Berlin")
    engine.scene.load(scene = saved)
    val cityAfterLoad = engine.variable.get(key = "city") // Berlin
    // highlight-android-before-load

    // highlight-android-errors
    // A value set holds values only for variables of the scene.
    val missingVariableErrorCode = try {
        engine.variable.set(key = "missing", value = "x", target = VariableTarget.ValueSet(longNames))
        null
    } catch (error: EngineException) {
        // EngineErrorCode.BLOCK_VARIABLE_NOT_FOUND
        error.code
    }
    val nameTakenErrorCode = try {
        engine.variable.createValueSet(name = "Long names")
        null
    } catch (error: EngineException) {
        // EngineErrorCode.BLOCK_VALUE_SET_NAME_TAKEN
        error.code
    }
    // highlight-android-errors

    // highlight-android-destroy-value-set
    // Destroying the active set shows the default values again.
    engine.variable.destroyValueSet(set = longNames)
    val activeSetAfterDestroy = engine.variable.getActiveValueSet() // null
    val nameAfterDestroy = engine.variable.get(key = "first_name") // Ada
    // highlight-android-destroy-value-set

    Log.i(TAG, "$valueSetName, $shownName, $defaultName, $writtenName, $cityAfterLoad")

    return ValueSets(
        valueSets = valueSets,
        valueSetName = valueSetName,
        shownName = shownName,
        defaultName = defaultName,
        shownAccent = shownAccent,
        writtenName = writtenName,
        defaultAfterWrite = defaultAfterWrite,
        cardColor = cardColor,
        previewPng = previewPng,
        activeSetKeepsId = activeSetKeepsId,
        foundSetKeepsId = foundSetKeepsId,
        cityAfterLoad = cityAfterLoad,
        missingVariableErrorCode = missingVariableErrorCode,
        nameTakenErrorCode = nameTakenErrorCode,
        activeSetAfterDestroy = activeSetAfterDestroy,
        nameAfterDestroy = nameAfterDestroy,
    )
}
