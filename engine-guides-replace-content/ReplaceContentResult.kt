import android.net.Uri
import java.nio.ByteBuffer

data class ReplaceContentResult(
    val namedPlaceholder: String,
    val placeholderNames: List<String>,
    val imagePlaceholderSupportsBehavior: Boolean,
    val imagePlaceholderEnabled: Boolean,
    val firstNameVariable: String,
    val variableNamesAfterSet: List<String>,
    val campaignTagRemoved: Boolean,
    val replacedImageUri: Uri,
    val directSubtitleText: String,
    val generatedDesigns: List<GeneratedTemplateDesign>,
)

data class GeneratedTemplateDesign(
    val label: String,
    val pngBuffer: ByteBuffer,
)
