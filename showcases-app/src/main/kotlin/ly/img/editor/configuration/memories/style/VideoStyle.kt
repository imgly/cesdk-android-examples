package ly.img.editor.configuration.memories.style

/**
 * Catalog of the video styles offered in the Styles sheet.
 *
 * This is the single source of truth for a style: its name, typeface, preferred font
 * weights, the image-filter adjustments, the scene background and media scale, and the title
 * text color all live here. To add, remove, or tune a style, edit [VideoStyles.ALL] below —
 * nothing else needs to change. The generic application logic lives in [StyleApplier].
 *
 * The bundled files a style needs — its looping backdrop [StyleBackground.Video] and its picker
 * thumbnail — are supplied by the [STYLE_SOURCE_ID] custom local asset source (see
 * [StyleAssetSource]). A style references them by [id], never by a hard-coded `file://` path.
 */
data class VideoStyle(
    /** Stable lowercase identifier used as the key everywhere (e.g. "noir"). */
    val id: String,
    /** Human-facing name shown in the sheet (e.g. "Noir"). */
    val displayName: String,
    /** Typeface name as it appears in the "ly.img.typeface" asset source. */
    val typeface: String,
    /** Preferred font sub-families, most-preferred first; falls back to the first available. */
    val fontWeights: List<String>,
    /** Adjustment effect properties, e.g. ("effect/adjustments/saturation", -1.0f). Empty = no adjustments. */
    val adjustments: List<Pair<String, Float>> = emptyList(),
    /** How much of the page the media fills. 1.0 = full-bleed; < 1.0 reveals the [background]. */
    val mediaScale: Float = 1f,
    /** The backdrop shown behind the (scaled-down) media. */
    val background: StyleBackground = StyleBackground.None,
    /** Title text color for this style (hex). White reads on the dark/video backdrops; Noir uses black. */
    val titleTextColorHex: String = "#FFFFFF",
    /** Opaque ARGB color for the picker tile (shown behind the icon / as a placeholder). */
    val previewBackground: Long,
)

/** The backdrop a style paints behind the media once the media is scaled below full-bleed. */
sealed interface StyleBackground {
    /** No backdrop: the media fills the whole page (used by the unstyled default). */
    object None : StyleBackground

    /** A flat color fill behind the media (e.g. white for Noir). [colorHex] is an "#RRGGBB" string. */
    data class Solid(
        val colorHex: String,
    ) : StyleBackground

    /**
     * A looping video behind the media, supplied by the [STYLE_SOURCE_ID] asset source.
     * [assetId] is the id of the backdrop asset in that source (e.g. "hologram").
     */
    data class Video(
        val assetId: String,
    ) : StyleBackground
}

object VideoStyles {
    /** Neutral, unstyled: clean modern sans, full-bleed media, no filter or backdrop. */
    val DEFAULT = VideoStyle(
        id = "default",
        displayName = "Default",
        typeface = "Montserrat",
        fontWeights = listOf("SemiBold", "Medium", "Regular"),
        previewBackground = 0xFFEFEBE9,
    )

    /** Professional black & white on a clean white backdrop, with black title type. */
    val NOIR = VideoStyle(
        id = "noir",
        displayName = "Noir",
        typeface = "Playfair Display",
        fontWeights = listOf("Bold", "SemiBold"),
        adjustments = listOf(
            "effect/adjustments/saturation" to -1.0f,
            "effect/adjustments/contrast" to 0.15f,
            "effect/adjustments/clarity" to 0.1f,
        ),
        mediaScale = 0.8f,
        background = StyleBackground.Solid(colorHex = "#FFFFFF"),
        titleTextColorHex = "#000000",
        previewBackground = 0xFF222222,
    )

    /** Futuristic cool cast over a looping hologram backdrop. A blue temperature shift cools the media. */
    val HOLOGRAM = VideoStyle(
        id = "hologram",
        displayName = "Hologram",
        typeface = "VT323",
        fontWeights = listOf("Regular"),
        adjustments = listOf(
            "effect/adjustments/temperature" to -0.4f,
            "effect/adjustments/contrast" to 0.1f,
        ),
        mediaScale = 0.8f,
        background = StyleBackground.Video(assetId = "hologram"),
        previewBackground = 0xFFE1F5FE,
    )

    /** Playful, poppy filter over a looping bubblegum backdrop: punchy saturation, bright tones. */
    val BUBBLEGUM = VideoStyle(
        id = "bubblegum",
        displayName = "Bubblegum",
        typeface = "Lobster Two",
        fontWeights = listOf("Bold", "Regular"),
        adjustments = listOf(
            "effect/adjustments/saturation" to 0.5f,
            "effect/adjustments/brightness" to 0.05f,
            "effect/adjustments/contrast" to 0.1f,
        ),
        mediaScale = 0.8f,
        background = StyleBackground.Video(assetId = "bubblegum"),
        previewBackground = 0xFFFCE4EC,
    )

    /** All styles, in the order they appear in the Styles sheet. */
    val ALL = listOf(DEFAULT, NOIR, HOLOGRAM, BUBBLEGUM)

    /** Resolves a style by its [id] (case-insensitive), falling back to [DEFAULT]. */
    fun byId(id: String): VideoStyle = ALL.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: DEFAULT
}
