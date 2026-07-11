import java.nio.ByteBuffer

data class TextWithEmojisResult(
    val configuredEmojiFontUri: String,
    val defaultEmojiFontUri: String,
    val activeEmojiFontUri: String,
    val forceSystemEmojis: Boolean,
    val primaryText: String,
    val emojiExamples: List<String>,
    val exportedPng: ByteBuffer,
)
