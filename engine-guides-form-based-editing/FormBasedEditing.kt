import android.net.Uri
import ly.img.engine.Color
import ly.img.engine.ContentFillMode
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode
import java.nio.ByteBuffer

// highlight-android-form-state
data class TemplateFormState(
    val headline: String,
    val subline: String,
    val heroImageUri: Uri,
)
// highlight-android-form-state

data class FormBasedEditingResult(
    val variableKeys: List<String>,
    val placeholderNames: List<String>,
    val initialValues: Map<String, String>,
    val resolvedVariables: Map<String, String>,
    val initialHeroImageUri: Uri,
    val pngData: ByteBuffer,
)

suspend fun formBasedEditing(engine: Engine): FormBasedEditingResult {
    engine.variable.findAll().forEach { key -> engine.variable.remove(key) }

    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 1080F)
    engine.block.setHeight(page, value = 1350F)
    engine.block.appendChild(parent = scene, child = page)

    val background = engine.block.create(DesignBlockType.Graphic)
    val backgroundFill = engine.block.createFill(FillType.Color)
    engine.block.setShape(background, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setFill(background, fill = backgroundFill)
    engine.block.setFillSolidColor(background, color = Color.fromHex("#F8F4EC"))
    engine.block.appendChild(parent = page, child = background)
    engine.block.fillParent(background)

    val heroImage = engine.block.create(DesignBlockType.Graphic)
    val heroFill = engine.block.createFill(FillType.Image)
    engine.block.setName(heroImage, name = "hero-image")
    engine.block.setShape(heroImage, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(heroImage, value = 72F)
    engine.block.setPositionY(heroImage, value = 72F)
    engine.block.setWidth(heroImage, value = 936F)
    engine.block.setHeight(heroImage, value = 612F)
    engine.block.setContentFillMode(heroImage, mode = ContentFillMode.COVER)
    engine.block.setUri(
        block = heroFill,
        property = "fill/image/imageFileURI",
        value = Uri.parse("https://img.ly/static/ubq_samples/sample_1.jpg"),
    )
    engine.block.setFill(heroImage, fill = heroFill)
    engine.block.appendChild(parent = page, child = heroImage)
    engine.block.setPlaceholderEnabled(heroImage, enabled = true)

    val headline = engine.block.create(DesignBlockType.Text)
    engine.block.replaceText(headline, text = "{{headline}}")
    engine.block.setPositionX(headline, value = 96F)
    engine.block.setPositionY(headline, value = 760F)
    engine.block.setWidth(headline, value = 888F)
    engine.block.setHeightMode(headline, mode = SizeMode.AUTO)
    engine.block.setTextFontSize(headline, fontSize = 30F)
    engine.block.setTextColor(headline, color = Color.fromHex("#26211C"))
    engine.block.appendChild(parent = page, child = headline)

    val subline = engine.block.create(DesignBlockType.Text)
    engine.block.replaceText(subline, text = "{{subline}}")
    engine.block.setPositionX(subline, value = 96F)
    engine.block.setPositionY(subline, value = 1030F)
    engine.block.setWidth(subline, value = 790F)
    engine.block.setHeightMode(subline, mode = SizeMode.AUTO)
    engine.block.setTextFontSize(subline, fontSize = 15F)
    engine.block.setTextColor(subline, color = Color.fromHex("#5A5046"))
    engine.block.appendChild(parent = page, child = subline)

    engine.variable.set(key = "headline", value = "Spring Workshop")
    engine.variable.set(key = "subline", value = "Reserve your place today.")

    // highlight-android-discover-fields
    val variableKeys = engine.variable.findAll().sorted()

    val imagePlaceholders = engine.block.findByType(DesignBlockType.Graphic)
        .filter { block ->
            engine.block.isPlaceholderEnabled(block) && engine.block.supportsFill(block)
        }

    val placeholderNames = imagePlaceholders.map { block -> engine.block.getName(block) }
    // highlight-android-discover-fields

    // highlight-android-read-values
    val initialValues = variableKeys.associateWith { key -> engine.variable.get(key) }

    val heroPlaceholder = imagePlaceholders.first { block ->
        engine.block.getName(block) == "hero-image"
    }
    val currentHeroFill = engine.block.getFill(heroPlaceholder)
    val initialHeroImageUri = engine.block.getUri(
        block = currentHeroFill,
        property = "fill/image/imageFileURI",
    )
    // highlight-android-read-values

    // highlight-android-collected-values
    val submittedForm = TemplateFormState(
        headline = "Launch Workshop",
        subline = "Join the live product walkthrough.",
        heroImageUri = Uri.parse("https://img.ly/static/ubq_samples/sample_4.jpg"),
    )
    // highlight-android-collected-values

    // highlight-android-validate
    val missingRequiredFields = listOfNotNull(
        "headline".takeIf { submittedForm.headline.isBlank() },
        "subline".takeIf { submittedForm.subline.isBlank() },
        "heroImageUri".takeIf { submittedForm.heroImageUri.toString().isBlank() },
    )

    check(missingRequiredFields.isEmpty()) {
        "Missing required form fields: ${missingRequiredFields.joinToString()}"
    }
    // highlight-android-validate

    // highlight-android-update-variables
    engine.variable.set(key = "headline", value = submittedForm.headline)
    engine.variable.set(key = "subline", value = submittedForm.subline)
    // highlight-android-update-variables

    // highlight-android-replace-placeholder
    val updatedHeroFill = engine.block.getFill(heroPlaceholder)
    engine.block.setUri(
        block = updatedHeroFill,
        property = "fill/image/imageFileURI",
        value = submittedForm.heroImageUri,
    )
    engine.block.resetCrop(heroPlaceholder)
    // highlight-android-replace-placeholder

    val resolvedVariables = variableKeys.associateWith { key -> engine.variable.get(key) }

    // highlight-android-export
    val pngData = engine.block.export(
        block = page,
        mimeType = MimeType.PNG,
    )
    // highlight-android-export

    return FormBasedEditingResult(
        variableKeys = variableKeys,
        placeholderNames = placeholderNames,
        initialValues = initialValues,
        resolvedVariables = resolvedVariables,
        initialHeroImageUri = initialHeroImageUri,
        pngData = pngData,
    )
}
