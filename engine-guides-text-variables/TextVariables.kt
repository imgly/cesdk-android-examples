import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.MimeType
import ly.img.engine.SizeMode

suspend fun textVariables(engine: Engine): TextVariablesResult {
    val sampleKeys = listOf("firstName", "lastName")
    sampleKeys.forEach { key ->
        if (engine.variable.findAll().contains(key)) {
            engine.variable.remove(key)
        }
    }

    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 400F)
    engine.block.appendChild(parent = scene, child = page)

    // highlight-android-bind-tokens
    val textBlock = engine.block.create(DesignBlockType.Text)
    engine.block.replaceText(
        block = textBlock,
        text = "Certificate for {{firstName}} {{lastName}}",
    )
    engine.block.setPositionX(textBlock, value = 120F)
    engine.block.setPositionY(textBlock, value = 164F)
    engine.block.setWidth(textBlock, value = 560F)
    engine.block.setHeightMode(textBlock, mode = SizeMode.AUTO)
    engine.block.setTextFontSize(textBlock, fontSize = 42F)
    engine.block.setTextColor(textBlock, color = Color.fromHex("#FF14171F"))
    engine.block.appendChild(parent = page, child = textBlock)
    // highlight-android-bind-tokens

    // highlight-android-set-values
    val recipientData = mapOf(
        "firstName" to "Alex",
        "lastName" to "Smith",
    )

    recipientData.forEach { (key, value) ->
        engine.variable.set(key = key, value = value)
    }
    // highlight-android-set-values

    // highlight-android-discover-variables
    val variableNames = engine.variable.findAll().sorted()
    // highlight-android-discover-variables

    // highlight-android-read-variable
    val firstName = engine.variable.get(key = "firstName")
    // highlight-android-read-variable

    // highlight-android-detect-references
    val hasVariableReferences = engine.block.referencesAnyVariables(textBlock)
    // highlight-android-detect-references

    // highlight-android-scan-tokens
    val tokenRegex = Regex("""\{\{\s*([^{}]+?)\s*\}\}""")
    val tokenKeys = engine.block.findByType(DesignBlockType.Text)
        .flatMap { block ->
            tokenRegex.findAll(engine.block.getString(block, property = "text/text"))
                .map { match -> match.groupValues[1].trim() }
                .toList()
        }
        .distinct()
    // highlight-android-scan-tokens

    val pagePngData = engine.block.export(page, mimeType = MimeType.PNG)

    // highlight-android-remove-variable
    engine.variable.remove(key = "lastName")
    val remainingVariableNames = engine.variable.findAll().sorted()
    // highlight-android-remove-variable

    val result = TextVariablesResult(
        variablesAfterUpdate = variableNames,
        firstName = firstName,
        hasVariableReferences = hasVariableReferences,
        tokenKeys = tokenKeys,
        variablesAfterRemoval = remainingVariableNames,
        templateText = engine.block.getString(textBlock, property = "text/text"),
        templateTextBlockIsAttached = engine.block.getParent(textBlock) == page,
        pagePngData = pagePngData,
    )

    engine.variable.remove(key = "firstName")

    return result
}
