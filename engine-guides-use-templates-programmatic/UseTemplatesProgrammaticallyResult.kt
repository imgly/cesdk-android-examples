import java.nio.ByteBuffer

data class UseTemplatesProgrammaticallyResult(
    val templateString: String,
    val templateArchive: ByteBuffer,
    val variableNames: List<String>,
    val currentRecipientName: String,
    val titleUsesVariables: Boolean,
    val placeholderCount: Int,
    val placeholderBehaviorEnabled: Boolean,
    val placeholderEnabled: Boolean,
    val personalizedCards: List<ByteBuffer>,
    val resetExports: List<ByteBuffer>,
    val variablesAfterRemoval: List<String>,
    val loadedTemplatePageCount: Int,
)
