import java.nio.ByteBuffer

data class TemplateManagementSummary(
    val sourceId: String,
    val originalTemplateCount: Int,
    val finalTemplateCount: Int,
    val updatedTemplateLabel: String,
    val temporaryTemplateRemoved: Boolean,
    val templateAppliedFromSource: Boolean,
    val refreshedTemplateContent: String,
    val refreshedTemplateContentKey: String,
    val refreshedTemplatePreview: ByteBuffer,
    val archiveByteCount: Int,
    val archiveLoaded: Boolean,
    val validatedBeforeSaving: Boolean,
)
