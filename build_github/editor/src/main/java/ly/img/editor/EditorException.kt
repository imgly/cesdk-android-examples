package ly.img.editor

class EditorException(
    val errorCode: Code,
) : Exception("Editor exception: errorCode = $errorCode") {
    enum class Code {
        NO_INTERNET,
    }
}
