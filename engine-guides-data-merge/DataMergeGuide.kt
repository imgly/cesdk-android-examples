import kotlinx.coroutines.withContext
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode
import java.nio.ByteBuffer

data class MergeRecord(
    val fullName: String,
    val jobTitle: String,
    val email: String,
    val photoUri: String,
)

data class MergedCard(
    val fileName: String,
    val pngData: ByteBuffer,
)

private val dataMergeVariableKeys = listOf("full_name", "job_title", "email")

suspend fun mergeBusinessCards(engine: Engine): List<MergedCard> = withContext(engine.dispatcher) {
    val currentVariableKeys = engine.variable.findAll().toSet()
    val previousVariables = dataMergeVariableKeys
        .filter(currentVariableKeys::contains)
        .associateWith(engine.variable::get)

    try {
        mergeBusinessCardsWithTemporaryVariables(engine)
    } finally {
        val variablesToRemove = engine.variable.findAll().toSet()
        dataMergeVariableKeys.filter(variablesToRemove::contains).forEach(engine.variable::remove)
        previousVariables.forEach { (key, value) -> engine.variable.set(key = key, value = value) }
    }
}

private suspend fun mergeBusinessCardsWithTemporaryVariables(engine: Engine): List<MergedCard> {
    // highlight-android-sample-data
    val records = listOf(
        MergeRecord(
            fullName = "Alex Rivera",
            jobTitle = "Senior Product Designer",
            email = "alex.rivera@example.com",
            photoUri = "https://img.ly/static/ubq_samples/sample_1.jpg",
        ),
        MergeRecord(
            fullName = "Jordan Lee",
            jobTitle = "Lifecycle Marketing Lead",
            email = "jordan.lee@example.com",
            photoUri = "https://img.ly/static/ubq_samples/sample_2.jpg",
        ),
    )
    // highlight-android-sample-data

    // highlight-android-create-template
    val templateScene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 1050F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = templateScene, child = page)

    val background = engine.block.create(DesignBlockType.Graphic)
    val backgroundFill = engine.block.createFill(FillType.Color)
    engine.block.setShape(background, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setFill(background, fill = backgroundFill)
    engine.block.appendChild(parent = page, child = background)
    engine.block.fillParent(background)
    engine.block.setColor(
        block = backgroundFill,
        property = "fill/color/value",
        value = Color.fromHex("#FFF7F0"),
    )

    val photoBlock = engine.block.create(DesignBlockType.Graphic)
    val placeholderFill = engine.block.createFill(FillType.Image)
    engine.block.setName(photoBlock, name = "profile-photo")
    engine.block.setShape(photoBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(photoBlock, value = 48F)
    engine.block.setPositionY(photoBlock, value = 48F)
    engine.block.setWidth(photoBlock, value = 280F)
    engine.block.setHeight(photoBlock, value = 504F)
    engine.block.setEnum(photoBlock, property = "contentFill/mode", value = "Cover")
    engine.block.setString(
        block = placeholderFill,
        property = "fill/image/imageFileURI",
        value = "https://img.ly/static/ubq_samples/sample_1.jpg",
    )
    engine.block.setFill(photoBlock, fill = placeholderFill)
    engine.block.appendChild(parent = page, child = photoBlock)

    val nameText = engine.block.create(DesignBlockType.Text)
    engine.block.replaceText(nameText, text = "{{full_name}}")
    engine.block.setPositionX(nameText, value = 368F)
    engine.block.setPositionY(nameText, value = 110F)
    engine.block.setWidth(nameText, value = 620F)
    engine.block.setHeightMode(nameText, mode = SizeMode.AUTO)
    engine.block.setTextFontSize(nameText, fontSize = 56F)
    engine.block.setTextColor(nameText, color = Color.fromHex("#211B17"))
    engine.block.appendChild(parent = page, child = nameText)

    val detailsText = engine.block.create(DesignBlockType.Text)
    engine.block.replaceText(detailsText, text = "{{job_title}}\n{{email}}")
    engine.block.setPositionX(detailsText, value = 368F)
    engine.block.setPositionY(detailsText, value = 214F)
    engine.block.setWidth(detailsText, value = 580F)
    engine.block.setHeightMode(detailsText, mode = SizeMode.AUTO)
    engine.block.setTextFontSize(detailsText, fontSize = 28F)
    engine.block.setTextColor(detailsText, color = Color.fromHex("#5D5248"))
    engine.block.appendChild(parent = page, child = detailsText)

    val templateSceneString = engine.scene.saveToString(scene = templateScene)
    // highlight-android-create-template

    engine.block.forceLoadResources(listOf(photoBlock, nameText, detailsText))

    val mergedCards = mutableListOf<MergedCard>()

    // highlight-android-batch-loop
    for (record in records) {
        val currentVariableKeys = engine.variable.findAll().toSet()
        dataMergeVariableKeys.filter(currentVariableKeys::contains).forEach(engine.variable::remove)

        engine.scene.load(scene = templateSceneString)
        val exportPage = engine.scene.getPages().first()
        // highlight-android-batch-loop

        // highlight-android-set-variables
        engine.variable.set(key = "full_name", value = record.fullName)
        engine.variable.set(key = "job_title", value = record.jobTitle)
        engine.variable.set(key = "email", value = record.email)
        // highlight-android-set-variables

        // highlight-android-check-variables
        val variableNames = engine.variable.findAll()
        check(variableNames.containsAll(listOf("full_name", "job_title", "email")))

        val variableBlocks = engine.block.findByType(DesignBlockType.Text).filter { block ->
            engine.block.referencesAnyVariables(block)
        }
        check(variableBlocks.isNotEmpty())
        // highlight-android-check-variables

        // highlight-android-find-by-name
        val profilePhoto = engine.block.findByName("profile-photo").first()
        val profileFill = engine.block.getFill(profilePhoto)
        engine.block.setString(
            block = profileFill,
            property = "fill/image/imageFileURI",
            value = record.photoUri,
        )
        engine.block.resetCrop(profilePhoto)
        // highlight-android-find-by-name

        engine.block.forceLoadResources(listOf(exportPage))

        // highlight-android-export
        val pngData = engine.block.export(exportPage, mimeType = MimeType.PNG).asReadOnlyBuffer()
        mergedCards += MergedCard(
            fileName = record.fullName.lowercase().replace(" ", "-") + ".png",
            pngData = pngData,
        )
        // highlight-android-export
    }

    return mergedCards
}
