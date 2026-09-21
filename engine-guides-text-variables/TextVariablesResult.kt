import java.nio.ByteBuffer

data class TextVariablesResult(
    val variablesAfterUpdate: List<String>,
    val firstName: String,
    val hasVariableReferences: Boolean,
    val tokenKeys: List<String>,
    val variablesAfterRemoval: List<String>,
    val templateText: String,
    val templateTextBlockIsAttached: Boolean,
    val pagePngData: ByteBuffer,
)
