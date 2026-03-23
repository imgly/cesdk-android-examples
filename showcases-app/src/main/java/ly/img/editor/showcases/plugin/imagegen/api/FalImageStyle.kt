package ly.img.editor.showcases.plugin.imagegen.api

sealed class FalImageStyle(
    val apiValue: String,
    val category: String,
    val displayName: String,
) {
    // Category helpers
    private companion object {
        fun readableName(value: String): String = value
            .substringAfter('/')
            .replace('_', ' ')
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }

    // realistic_image
    data object RealisticImage :
        FalImageStyle(
            apiValue = "realistic_image",
            category = "Image",
            displayName = readableName(value = "realistic_image"),
        )

    data object RealisticBAndW :
        FalImageStyle(
            apiValue = "realistic_image/b_and_w",
            category = "Image",
            displayName = readableName(value = "realistic_image/b_and_w"),
        )

    data object RealisticHardFlash : FalImageStyle(
        apiValue = "realistic_image/hard_flash",
        category = "Image",
        displayName = readableName(value = "realistic_image/hard_flash"),
    )

    data object RealisticHdr :
        FalImageStyle(apiValue = "realistic_image/hdr", category = "Image", displayName = readableName(value = "realistic_image/hdr"))

    data object RealisticNaturalLight : FalImageStyle(
        apiValue = "realistic_image/natural_light",
        category = "Image",
        displayName = readableName(value = "realistic_image/natural_light"),
    )

    data object RealisticStudioPortrait : FalImageStyle(
        apiValue = "realistic_image/studio_portrait",
        category = "Image",
        displayName = readableName(value = "realistic_image/studio_portrait"),
    )

    data object RealisticEnterprise : FalImageStyle(
        apiValue = "realistic_image/enterprise",
        category = "Image",
        displayName = readableName(value = "realistic_image/enterprise"),
    )

    data object RealisticMotionBlur : FalImageStyle(
        apiValue = "realistic_image/motion_blur",
        category = "Image",
        displayName = readableName(value = "realistic_image/motion_blur"),
    )

    data object RealisticEveningLight : FalImageStyle(
        apiValue = "realistic_image/evening_light",
        category = "Image",
        displayName = readableName(value = "realistic_image/evening_light"),
    )

    data object RealisticFadedNostalgia : FalImageStyle(
        apiValue = "realistic_image/faded_nostalgia",
        category = "Image",
        displayName = readableName(value = "realistic_image/faded_nostalgia"),
    )

    data object RealisticForestLife : FalImageStyle(
        apiValue = "realistic_image/forest_life",
        category = "Image",
        displayName = readableName(value = "realistic_image/forest_life"),
    )

    data object RealisticMysticNaturalism : FalImageStyle(
        apiValue = "realistic_image/mystic_naturalism",
        category = "Image",
        displayName = readableName(value = "realistic_image/mystic_naturalism"),
    )

    data object RealisticNaturalTones : FalImageStyle(
        apiValue = "realistic_image/natural_tones",
        category = "Image",
        displayName = readableName(value = "realistic_image/natural_tones"),
    )

    data object RealisticOrganicCalm : FalImageStyle(
        apiValue = "realistic_image/organic_calm",
        category = "Image",
        displayName = readableName(value = "realistic_image/organic_calm"),
    )

    data object RealisticRealLifeGlow : FalImageStyle(
        apiValue = "realistic_image/real_life_glow",
        category = "Image",
        displayName = readableName(value = "realistic_image/real_life_glow"),
    )

    data object RealisticRetroRealism : FalImageStyle(
        apiValue = "realistic_image/retro_realism",
        category = "Image",
        displayName = readableName(value = "realistic_image/retro_realism"),
    )

    data object RealisticRetroSnapshot : FalImageStyle(
        apiValue = "realistic_image/retro_snapshot",
        category = "Image",
        displayName = readableName(value = "realistic_image/retro_snapshot"),
    )

    data object RealisticUrbanDrama : FalImageStyle(
        apiValue = "realistic_image/urban_drama",
        category = "Image",
        displayName = readableName(value = "realistic_image/urban_drama"),
    )

    data object RealisticVillageRealism : FalImageStyle(
        apiValue = "realistic_image/village_realism",
        category = "Image",
        displayName = readableName(value = "realistic_image/village_realism"),
    )

    data object RealisticWarmFolk : FalImageStyle(
        apiValue = "realistic_image/warm_folk",
        category = "Image",
        displayName = readableName(value = "realistic_image/warm_folk"),
    )

    // digital_illustration
    data object DigitalIllustrationPixelArt : FalImageStyle(
        apiValue = "digital_illustration/pixel_art",
        category = "Image",
        displayName = readableName(value = "digital_illustration/pixel_art"),
    )

    data object DigitalIllustrationHandDrawn : FalImageStyle(
        apiValue = "digital_illustration/hand_drawn",
        category = "Image",
        displayName = readableName(value = "digital_illustration/hand_drawn"),
    )

    data object DigitalIllustrationGrain : FalImageStyle(
        apiValue = "digital_illustration/grain",
        category = "Image",
        displayName = readableName(value = "digital_illustration/grain"),
    )

    data object DigitalIllustrationInfantileSketch : FalImageStyle(
        apiValue = "digital_illustration/infantile_sketch",
        category = "Image",
        displayName = readableName(value = "digital_illustration/infantile_sketch"),
    )

    data object DigitalIllustration2dArtPoster : FalImageStyle(
        apiValue = "digital_illustration/2d_art_poster",
        category = "Image",
        displayName = readableName(value = "digital_illustration/2d_art_poster"),
    )

    data object DigitalIllustrationHandmade3d : FalImageStyle(
        apiValue = "digital_illustration/handmade_3d",
        category = "Image",
        displayName = readableName(value = "digital_illustration/handmade_3d"),
    )

    data object DigitalIllustrationHandDrawnOutline : FalImageStyle(
        apiValue = "digital_illustration/hand_drawn_outline",
        category = "Image",
        displayName = readableName(value = "digital_illustration/hand_drawn_outline"),
    )

    data object DigitalIllustrationEngravingColor : FalImageStyle(
        apiValue = "digital_illustration/engraving_color",
        category = "Image",
        displayName = readableName(value = "digital_illustration/engraving_color"),
    )

    data object DigitalIllustration2dArtPoster2 : FalImageStyle(
        apiValue = "digital_illustration/2d_art_poster_2",
        category = "Image",
        displayName = readableName(value = "digital_illustration/2d_art_poster_2"),
    )

    data object DigitalIllustrationAntiquarian : FalImageStyle(
        apiValue = "digital_illustration/antiquarian",
        category = "Image",
        displayName = readableName(value = "digital_illustration/antiquarian"),
    )

    data object DigitalIllustrationBoldFantasy : FalImageStyle(
        apiValue = "digital_illustration/bold_fantasy",
        category = "Image",
        displayName = readableName(value = "digital_illustration/bold_fantasy"),
    )

    data object DigitalIllustrationChildBook : FalImageStyle(
        apiValue = "digital_illustration/child_book",
        category = "Image",
        displayName = readableName(value = "digital_illustration/child_book"),
    )

    data object DigitalIllustrationChildBooks : FalImageStyle(
        apiValue = "digital_illustration/child_books",
        category = "Image",
        displayName = readableName(value = "digital_illustration/child_books"),
    )

    data object DigitalIllustrationCover : FalImageStyle(
        apiValue = "digital_illustration/cover",
        category = "Image",
        displayName = readableName(value = "digital_illustration/cover"),
    )

    data object DigitalIllustrationCrosshatch : FalImageStyle(
        apiValue = "digital_illustration/crosshatch",
        category = "Image",
        displayName = readableName(value = "digital_illustration/crosshatch"),
    )

    data object DigitalIllustrationDigitalEngraving : FalImageStyle(
        apiValue = "digital_illustration/digital_engraving",
        category = "Image",
        displayName = readableName(value = "digital_illustration/digital_engraving"),
    )

    data object DigitalIllustrationExpressionism : FalImageStyle(
        apiValue = "digital_illustration/expressionism",
        category = "Image",
        displayName = readableName(value = "digital_illustration/expressionism"),
    )

    data object DigitalIllustrationFreehandDetails : FalImageStyle(
        apiValue = "digital_illustration/freehand_details",
        category = "Image",
        displayName = readableName(value = "digital_illustration/freehand_details"),
    )

    data object DigitalIllustrationGrain20 : FalImageStyle(
        apiValue = "digital_illustration/grain_20",
        category = "Image",
        displayName = readableName(value = "digital_illustration/grain_20"),
    )

    data object DigitalIllustrationGraphicIntensity : FalImageStyle(
        apiValue = "digital_illustration/graphic_intensity",
        category = "Image",
        displayName = readableName(value = "digital_illustration/graphic_intensity"),
    )

    data object DigitalIllustrationHardComics : FalImageStyle(
        apiValue = "digital_illustration/hard_comics",
        category = "Image",
        displayName = readableName(value = "digital_illustration/hard_comics"),
    )

    data object DigitalIllustrationLongShadow : FalImageStyle(
        apiValue = "digital_illustration/long_shadow",
        category = "Image",
        displayName = readableName(value = "digital_illustration/long_shadow"),
    )

    data object DigitalIllustrationModernFolk : FalImageStyle(
        apiValue = "digital_illustration/modern_folk",
        category = "Image",
        displayName = readableName(value = "digital_illustration/modern_folk"),
    )

    data object DigitalIllustrationMulticolor : FalImageStyle(
        apiValue = "digital_illustration/multicolor",
        category = "Image",
        displayName = readableName(value = "digital_illustration/multicolor"),
    )

    data object DigitalIllustrationNeonCalm : FalImageStyle(
        apiValue = "digital_illustration/neon_calm",
        category = "Image",
        displayName = readableName(value = "digital_illustration/neon_calm"),
    )

    data object DigitalIllustrationNoir : FalImageStyle(
        apiValue = "digital_illustration/noir",
        category = "Image",
        displayName = readableName(value = "digital_illustration/noir"),
    )

    data object DigitalIllustrationNostalgicPastel : FalImageStyle(
        apiValue = "digital_illustration/nostalgic_pastel",
        category = "Image",
        displayName = readableName(value = "digital_illustration/nostalgic_pastel"),
    )

    data object DigitalIllustrationOutlineDetails : FalImageStyle(
        apiValue = "digital_illustration/outline_details",
        category = "Image",
        displayName = readableName(value = "digital_illustration/outline_details"),
    )

    data object DigitalIllustrationPastelGradient : FalImageStyle(
        apiValue = "digital_illustration/pastel_gradient",
        category = "Image",
        displayName = readableName(value = "digital_illustration/pastel_gradient"),
    )

    data object DigitalIllustrationPastelSketch : FalImageStyle(
        apiValue = "digital_illustration/pastel_sketch",
        category = "Image",
        displayName = readableName(value = "digital_illustration/pastel_sketch"),
    )

    data object DigitalIllustrationPopArt : FalImageStyle(
        apiValue = "digital_illustration/pop_art",
        category = "Image",
        displayName = readableName(value = "digital_illustration/pop_art"),
    )

    data object DigitalIllustrationPopRenaissance : FalImageStyle(
        apiValue = "digital_illustration/pop_renaissance",
        category = "Image",
        displayName = readableName(value = "digital_illustration/pop_renaissance"),
    )

    data object DigitalIllustrationStreetArt : FalImageStyle(
        apiValue = "digital_illustration/street_art",
        category = "Image",
        displayName = readableName(value = "digital_illustration/street_art"),
    )

    data object DigitalIllustrationTabletSketch : FalImageStyle(
        apiValue = "digital_illustration/tablet_sketch",
        category = "Image",
        displayName = readableName(value = "digital_illustration/tablet_sketch"),
    )

    data object DigitalIllustrationUrbanGlow : FalImageStyle(
        apiValue = "digital_illustration/urban_glow",
        category = "Image",
        displayName = readableName(value = "digital_illustration/urban_glow"),
    )

    data object DigitalIllustrationUrbanSketching : FalImageStyle(
        apiValue = "digital_illustration/urban_sketching",
        category = "Image",
        displayName = readableName(value = "digital_illustration/urban_sketching"),
    )

    data object DigitalIllustrationVanillaDreams : FalImageStyle(
        apiValue = "digital_illustration/vanilla_dreams",
        category = "Image",
        displayName = readableName(value = "digital_illustration/vanilla_dreams"),
    )

    data object DigitalIllustrationYoungAdultBook : FalImageStyle(
        apiValue = "digital_illustration/young_adult_book",
        category = "Image",
        displayName = readableName(value = "digital_illustration/young_adult_book"),
    )

    data object DigitalIllustrationYoungAdultBook2 : FalImageStyle(
        apiValue = "digital_illustration/young_adult_book_2",
        category = "Image",
        displayName = readableName(value = "digital_illustration/young_adult_book_2"),
    )

    // vector_illustration
    data object VectorIllustrationBoldStroke : FalImageStyle(
        apiValue = "vector_illustration/bold_stroke",
        category = "Vector",
        displayName = readableName(value = "vector_illustration/bold_stroke"),
    )

    data object VectorIllustrationChemistry : FalImageStyle(
        apiValue = "vector_illustration/chemistry",
        category = "Vector",
        displayName = readableName(value = "vector_illustration/chemistry"),
    )

    data object VectorIllustrationColoredStencil : FalImageStyle(
        apiValue = "vector_illustration/colored_stencil",
        category = "Vector",
        displayName = readableName(value = "vector_illustration/colored_stencil"),
    )

    data object VectorIllustrationContourPopArt : FalImageStyle(
        apiValue = "vector_illustration/contour_pop_art",
        category = "Vector",
        displayName = readableName(value = "vector_illustration/contour_pop_art"),
    )

    data object VectorIllustrationCosmics : FalImageStyle(
        apiValue = "vector_illustration/cosmics",
        category = "Vector",
        displayName = readableName(value = "vector_illustration/cosmics"),
    )

    data object VectorIllustrationCutout : FalImageStyle(
        apiValue = "vector_illustration/cutout",
        category = "Vector",
        displayName = readableName(value = "vector_illustration/cutout"),
    )

    data object VectorIllustrationDepressive : FalImageStyle(
        apiValue = "vector_illustration/depressive",
        category = "Vector",
        displayName = readableName(value = "vector_illustration/depressive"),
    )

    data object VectorIllustrationEditorial : FalImageStyle(
        apiValue = "vector_illustration/editorial",
        category = "Vector",
        displayName = readableName(value = "vector_illustration/editorial"),
    )

    data object VectorIllustrationEmotionalFlat : FalImageStyle(
        apiValue = "vector_illustration/emotional_flat",
        category = "Vector",
        displayName = readableName(value = "vector_illustration/emotional_flat"),
    )

    data object VectorIllustrationInfographical : FalImageStyle(
        apiValue = "vector_illustration/infographical",
        category = "Vector",
        displayName = readableName(value = "vector_illustration/infographical"),
    )

    data object VectorIllustrationMarkerOutline : FalImageStyle(
        apiValue = "vector_illustration/marker_outline",
        category = "Vector",
        displayName = readableName(value = "vector_illustration/marker_outline"),
    )

    data object VectorIllustrationMosaic : FalImageStyle(
        apiValue = "vector_illustration/mosaic",
        category = "Vector",
        displayName = readableName(value = "vector_illustration/mosaic"),
    )

    data object VectorIllustrationNaivector : FalImageStyle(
        apiValue = "vector_illustration/naivector",
        category = "Vector",
        displayName = readableName(value = "vector_illustration/naivector"),
    )

    data object VectorIllustrationRoundishFlat : FalImageStyle(
        apiValue = "vector_illustration/roundish_flat",
        category = "Vector",
        displayName = readableName(value = "vector_illustration/roundish_flat"),
    )

    data object VectorIllustrationSegmentedColors : FalImageStyle(
        apiValue = "vector_illustration/segmented_colors",
        category = "Vector",
        displayName = readableName(value = "vector_illustration/segmented_colors"),
    )

    data object VectorIllustrationSharpContrast : FalImageStyle(
        apiValue = "vector_illustration/sharp_contrast",
        category = "Vector",
        displayName = readableName(value = "vector_illustration/sharp_contrast"),
    )

    data object VectorIllustrationThin : FalImageStyle(
        apiValue = "vector_illustration/thin",
        category = "Vector",
        displayName = readableName(value = "vector_illustration/thin"),
    )

    data object VectorIllustrationVectorPhoto : FalImageStyle(
        apiValue = "vector_illustration/vector_photo",
        category = "Vector",
        displayName = readableName(value = "vector_illustration/vector_photo"),
    )

    data object VectorIllustrationVividShapes : FalImageStyle(
        apiValue = "vector_illustration/vivid_shapes",
        category = "Vector",
        displayName = readableName(value = "vector_illustration/vivid_shapes"),
    )

    data object VectorIllustrationEngraving : FalImageStyle(
        apiValue = "vector_illustration/engraving",
        category = "Vector",
        displayName = readableName(value = "vector_illustration/engraving"),
    )

    data object VectorIllustrationLineArt : FalImageStyle(
        apiValue = "vector_illustration/line_art",
        category = "Vector",
        displayName = readableName(value = "vector_illustration/line_art"),
    )

    data object VectorIllustrationLineCircuit : FalImageStyle(
        apiValue = "vector_illustration/line_circuit",
        category = "Vector",
        displayName = readableName(value = "vector_illustration/line_circuit"),
    )

    data object VectorIllustrationLinocut : FalImageStyle(
        apiValue = "vector_illustration/linocut",
        category = "Vector",
        displayName = readableName(value = "vector_illustration/linocut"),
    )
}

// Style collections for UI selection
val FalImageStylesImage: List<FalImageStyle> = listOf(
    FalImageStyle.RealisticImage,
    FalImageStyle.RealisticHardFlash,
    FalImageStyle.RealisticHdr,
    FalImageStyle.RealisticNaturalLight,
    FalImageStyle.RealisticStudioPortrait,
    FalImageStyle.RealisticEnterprise,
    FalImageStyle.RealisticMotionBlur,
    FalImageStyle.RealisticEveningLight,
    FalImageStyle.RealisticFadedNostalgia,
    FalImageStyle.RealisticForestLife,
    FalImageStyle.RealisticMysticNaturalism,
    FalImageStyle.RealisticNaturalTones,
    FalImageStyle.RealisticBAndW,
    FalImageStyle.RealisticOrganicCalm,
    FalImageStyle.RealisticRealLifeGlow,
    FalImageStyle.RealisticRetroRealism,
    FalImageStyle.RealisticRetroSnapshot,
    FalImageStyle.RealisticUrbanDrama,
    FalImageStyle.RealisticVillageRealism,
    FalImageStyle.RealisticWarmFolk,
    FalImageStyle.DigitalIllustrationPixelArt,
    FalImageStyle.DigitalIllustrationHandDrawn,
    FalImageStyle.DigitalIllustrationGrain,
    FalImageStyle.DigitalIllustrationInfantileSketch,
    FalImageStyle.DigitalIllustration2dArtPoster,
    FalImageStyle.DigitalIllustrationHandmade3d,
    FalImageStyle.DigitalIllustrationHandDrawnOutline,
    FalImageStyle.DigitalIllustrationEngravingColor,
    FalImageStyle.DigitalIllustration2dArtPoster2,
    FalImageStyle.DigitalIllustrationAntiquarian,
    FalImageStyle.DigitalIllustrationBoldFantasy,
    FalImageStyle.DigitalIllustrationChildBook,
    FalImageStyle.DigitalIllustrationChildBooks,
    FalImageStyle.DigitalIllustrationCover,
    FalImageStyle.DigitalIllustrationCrosshatch,
    FalImageStyle.DigitalIllustrationDigitalEngraving,
    FalImageStyle.DigitalIllustrationExpressionism,
    FalImageStyle.DigitalIllustrationFreehandDetails,
    FalImageStyle.DigitalIllustrationGrain20,
    FalImageStyle.DigitalIllustrationGraphicIntensity,
    FalImageStyle.DigitalIllustrationHardComics,
    FalImageStyle.DigitalIllustrationLongShadow,
    FalImageStyle.DigitalIllustrationModernFolk,
    FalImageStyle.DigitalIllustrationMulticolor,
    FalImageStyle.DigitalIllustrationNeonCalm,
    FalImageStyle.DigitalIllustrationNoir,
    FalImageStyle.DigitalIllustrationNostalgicPastel,
    FalImageStyle.DigitalIllustrationOutlineDetails,
    FalImageStyle.DigitalIllustrationPastelGradient,
    FalImageStyle.DigitalIllustrationPastelSketch,
    FalImageStyle.DigitalIllustrationPopArt,
    FalImageStyle.DigitalIllustrationPopRenaissance,
    FalImageStyle.DigitalIllustrationStreetArt,
    FalImageStyle.DigitalIllustrationTabletSketch,
    FalImageStyle.DigitalIllustrationUrbanGlow,
    FalImageStyle.DigitalIllustrationUrbanSketching,
    FalImageStyle.DigitalIllustrationVanillaDreams,
    FalImageStyle.DigitalIllustrationYoungAdultBook,
    FalImageStyle.DigitalIllustrationYoungAdultBook2,
)

val FalImageStylesVector: List<FalImageStyle> = listOf(
    FalImageStyle.VectorIllustrationBoldStroke,
    FalImageStyle.VectorIllustrationChemistry,
    FalImageStyle.VectorIllustrationColoredStencil,
    FalImageStyle.VectorIllustrationContourPopArt,
    FalImageStyle.VectorIllustrationCosmics,
    FalImageStyle.VectorIllustrationCutout,
    FalImageStyle.VectorIllustrationDepressive,
    FalImageStyle.VectorIllustrationEditorial,
    FalImageStyle.VectorIllustrationEmotionalFlat,
    FalImageStyle.VectorIllustrationInfographical,
    FalImageStyle.VectorIllustrationMarkerOutline,
    FalImageStyle.VectorIllustrationMosaic,
    FalImageStyle.VectorIllustrationNaivector,
    FalImageStyle.VectorIllustrationRoundishFlat,
    FalImageStyle.VectorIllustrationSegmentedColors,
    FalImageStyle.VectorIllustrationSharpContrast,
    FalImageStyle.VectorIllustrationThin,
    FalImageStyle.VectorIllustrationVectorPhoto,
    FalImageStyle.VectorIllustrationVividShapes,
    FalImageStyle.VectorIllustrationEngraving,
    FalImageStyle.VectorIllustrationLineArt,
    FalImageStyle.VectorIllustrationLineCircuit,
    FalImageStyle.VectorIllustrationLinocut,
)

// Sample thumbnail:
// https://cdn.img.ly/assets/plugins/plugin-ai-image-generation-web/v1/recraft-v3/thumbnails/digital_illustration.webp

fun FalImageStyle.getImageUrl(): String = buildThumbnailUrl(
    apiValue = this.apiValue,
    category = this.category,
)

private fun buildThumbnailUrl(
    apiValue: String,
    category: String,
): String {
    val baseUrl = "https://cdn.img.ly/assets/plugins/plugin-ai-image-generation-web/v1/recraft-v3/thumbnails"
    val fileName = apiValue.replace("/", "_")
    val fileExtension = if (category == "Image") "webp" else "svg"

    return "$baseUrl/$fileName.$fileExtension"
}
