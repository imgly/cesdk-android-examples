package ly.img.editor.showcases.plugin.imagegen

import ly.img.editor.showcases.plugin.imagegen.api.FalImageStyle

data class AIImageGenerationState(
    val prompt: String = "",
    val selectedOutput: OutputType = OutputType.IMAGE,
    val selectedStyle: FalImageStyle = FalImageStyle.RealisticImage,
    val selectedFormat: Format = Format.SQUARE_HD,
    val customWidth: String = "1920",
    val customHeight: String = "1080",
    val imageUri: String? = null,
    val isImageSelected: Boolean = false,
)

enum class OutputType(
    val label: String,
) {
    IMAGE("Image"),
    VECTOR("Vector"),
}

enum class Format(
    val label: String,
    val ratio: String,
    val falImageSize: String,
) {
    SQUARE_HD(
        label = "Square HD",
        ratio = "1:1",
        falImageSize = "square_hd",
    ),
    SQUARE(
        label = "Square",
        ratio = "1:1",
        falImageSize = "square",
    ),
    PORTRAIT_4_3(
        label = "Portrait",
        ratio = "3:4",
        falImageSize = "portrait_4_3",
    ),
    PORTRAIT_16_9(
        label = "Portrait",
        ratio = "9:16",
        falImageSize = "portrait_16_9",
    ),
    LANDSCAPE_4_3(
        label = "Landscape",
        ratio = "4:3",
        falImageSize = "landscape_4_3",
    ),
    LANDSCAPE_16_9(
        label = "Landscape",
        ratio = "16:9",
        falImageSize = "landscape_16_9",
    ),
    CUSTOM(
        label = "Custom",
        ratio = "",
        falImageSize = "",
    ),
}
