import android.net.Uri
import java.nio.ByteBuffer

data class UseTemplatesGenerateResult(
    val variableNames: List<String>,
    val defaultRecipient: String,
    val placeholderCount: Int,
    val namedPlaceholder: String,
    val replacementImageUri: Uri,
    val pngData: ByteBuffer,
    val pdfData: ByteBuffer,
    val batchExports: List<ByteBuffer>,
)
