package ly.img.editor.showcases

import RemoteAssetSource
import addRemoteAssetSources
import android.net.Uri
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import ly.img.editor.core.EditorScope
import ly.img.editor.core.UnstableEditorApi
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.LibraryContent
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.theme.fillAndStrokeColors
import ly.img.editor.showcases.icon.CustomFunctionalitiesBackgroundRemoval
import ly.img.editor.showcases.icon.CustomFunctionalitiesTextToImage
import ly.img.editor.showcases.icon.IconPack

@Stable
class ShowcasesViewModel : ViewModel() {
    private val remoteAssetsSupported = Secrets.remoteAssetSourceHost.isNotEmpty()

    fun getItems(columns: Int) = listOfNotNull(
        ShowcaseItem.Header(
            title = R.string.ly_img_showcases_title_photo,
            span = columns,
            items = listOf(
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.photo_ui_choose_from_gallery,
                    label = R.string.ly_img_showcases_button_design_gallery_title,
                    clickAction = ShowcaseItem.CarouselContent.ClickAction.PICK_IMAGE,
                    sublabel = null,
                    actionScene = null,
                    actionScreen = Screen.PhotoUi,
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.photo_ui_1_1,
                    label = R.string.ly_img_showcases_button_photo_1_1_title,
                    sublabel = R.string.ly_img_showcases_button_photo_1_1_subtitle,
                    clickAction = ShowcaseItem.CarouselContent.ClickAction.PICK_IMAGE,
                    actionScene = "1_1",
                    actionScreen = Screen.PhotoUi,
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.photo_ui_9_16,
                    label = R.string.ly_img_showcases_button_photo_9_16_title,
                    sublabel = R.string.ly_img_showcases_button_photo_9_16_subtitle,
                    clickAction = ShowcaseItem.CarouselContent.ClickAction.PICK_IMAGE,
                    actionScene = "9_16",
                    actionScreen = Screen.PhotoUi,
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.photo_ui_16_9,
                    label = R.string.ly_img_showcases_button_photo_16_9_title,
                    sublabel = R.string.ly_img_showcases_button_design_full_hd_subtitle,
                    clickAction = ShowcaseItem.CarouselContent.ClickAction.PICK_IMAGE,
                    actionScene = "16_9",
                    actionScreen = Screen.PhotoUi,
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.photo_ui_2_3,
                    label = R.string.ly_img_showcases_button_photo_2_3_title,
                    sublabel = R.string.ly_img_showcases_button_photo_2_3_subtitle,
                    clickAction = ShowcaseItem.CarouselContent.ClickAction.PICK_IMAGE,
                    actionScene = "2_3",
                    actionScreen = Screen.PhotoUi,
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.photo_ui_3_2,
                    label = R.string.ly_img_showcases_button_photo_3_2_title,
                    sublabel = R.string.ly_img_showcases_button_photo_3_2_subtitle,
                    clickAction = ShowcaseItem.CarouselContent.ClickAction.PICK_IMAGE,
                    actionScene = "3_2",
                    actionScreen = Screen.PhotoUi,
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.photo_ui_3_4,
                    label = R.string.ly_img_showcases_button_photo_3_4_title,
                    sublabel = R.string.ly_img_showcases_button_photo_3_4_subtitle,
                    clickAction = ShowcaseItem.CarouselContent.ClickAction.PICK_IMAGE,
                    actionScene = "3_4",
                    actionScreen = Screen.PhotoUi,
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.photo_ui_4_3,
                    label = R.string.ly_img_showcases_button_photo_4_3_title,
                    sublabel = R.string.ly_img_showcases_button_photo_4_3_subtitle,
                    clickAction = ShowcaseItem.CarouselContent.ClickAction.PICK_IMAGE,
                    actionScene = "4_3",
                    actionScreen = Screen.PhotoUi,
                ),
            ),
        ),
        ShowcaseItem.Header(
            title = R.string.ly_img_showcases_title_design,
            span = columns,
            items = listOf(
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.design_ui_instagram_story_template,
                    actionScene = "design_ui_instagram_story_template",
                    actionScreen = Screen.DesignUi,
                ),
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.design_ui_instagram_post_template,
                    actionScene = "design_ui_instagram_post_template",
                    actionScreen = Screen.DesignUi,
                ),
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.design_ui_video_full_hd_template,
                    actionScene = "design_ui_video_full_hd_template",
                    actionScreen = Screen.DesignUi,
                ),
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.design_ui_x_profile_picture_template,
                    actionScene = "design_ui_x_profile_picture_template",
                    actionScreen = Screen.DesignUi,
                ),
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.design_ui_x_header_template,
                    actionScene = "design_ui_x_header_template",
                    actionScreen = Screen.DesignUi,
                ),
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.design_ui_booklet_template,
                    actionScene = "design_ui_booklet_template",
                    actionScreen = Screen.DesignUi,
                ),
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.design_ui_business_card_template,
                    actionScene = "design_ui_business_card_template",
                    actionScreen = Screen.DesignUi,
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.design_ui_instagram_story,
                    label = R.string.ly_img_showcases_button_design_instagram_story_title,
                    sublabel = R.string.ly_img_showcases_button_design_instagram_story_subtitle,
                    actionScene = "design_ui_instagram_story",
                    actionScreen = Screen.DesignUi,
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.design_ui_instagram_post,
                    label = R.string.ly_img_showcases_button_design_instagram_post_title,
                    sublabel = R.string.ly_img_showcases_button_design_instagram_post_subtitle,
                    actionScene = "design_ui_instagram_post",
                    actionScreen = Screen.DesignUi,
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.design_ui_video_full_hd,
                    label = R.string.ly_img_showcases_button_design_full_hd_title,
                    sublabel = R.string.ly_img_showcases_button_design_full_hd_subtitle,
                    actionScene = "design_ui_video_full_hd",
                    actionScreen = Screen.DesignUi,
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.design_ui_x_header,
                    label = R.string.ly_img_showcases_button_design_x_header_title,
                    sublabel = R.string.ly_img_showcases_button_design_x_header_subtitle,
                    actionScene = "design_ui_x_header",
                    actionScreen = Screen.DesignUi,
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.design_ui_x_profile_picture,
                    label = R.string.ly_img_showcases_button_design_x_profile_picture_title,
                    sublabel = R.string.ly_img_showcases_button_design_x_profile_picture_subtitle,
                    actionScene = "design_ui_x_profile_picture",
                    actionScreen = Screen.DesignUi,
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.design_ui_booklet,
                    label = R.string.ly_img_showcases_button_design_booklet_title,
                    sublabel = R.string.ly_img_showcases_button_design_booklet_subtitle,
                    actionScene = "design_ui_booklet",
                    actionScreen = Screen.DesignUi,
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.design_ui_business_card,
                    label = R.string.ly_img_showcases_button_design_business_card_title,
                    sublabel = R.string.ly_img_showcases_button_design_business_card_subtitle,
                    actionScene = "design_ui_business_card",
                    actionScreen = Screen.DesignUi,
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.design_ui_open_design,
                    label = R.string.ly_img_showcases_button_design_import_title,
                    sublabel = R.string.ly_img_showcases_button_design_import_subtitle,
                    clickAction = ShowcaseItem.CarouselContent.ClickAction.PICK_SCENE,
                    hasDotLine = true,
                    actionScene = null,
                    actionScreen = Screen.DesignUi,
                ),
            ),
        ),
        ShowcaseItem.Header(
            title = R.string.ly_img_showcases_title_video,
            span = columns,
            items = listOf(
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.thumbnail_red_dot,
                    thumbnailAspectRatio = VIDEO_THUMBNAIL_ASPECT_RATIO,
                    actionScene = "red-dot",
                    actionScreen = Screen.VideoUi,
                ),
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.thumbnail_milli_surf_school,
                    thumbnailAspectRatio = VIDEO_THUMBNAIL_ASPECT_RATIO,
                    actionScene = "milli-surf-school",
                    actionScreen = Screen.VideoUi,
                ),
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.thumbnail_my_plants,
                    thumbnailAspectRatio = VIDEO_THUMBNAIL_ASPECT_RATIO,
                    actionScene = "my-plants",
                    actionScreen = Screen.VideoUi,
                ),
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.thumbnail_monthly_review,
                    thumbnailAspectRatio = VIDEO_THUMBNAIL_ASPECT_RATIO,
                    actionScene = "monthly-review",
                    actionScreen = Screen.VideoUi,
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.video_ui_blank,
                    label = R.string.ly_img_showcases_button_video_blank_title,
                    sublabel = R.string.ly_img_showcases_button_video_blank_subtitle,
                    actionScene = "video", // From editor package
                    actionScreen = Screen.VideoUi,
                ),
            ),
        ),
        ShowcaseItem.Header(
            title = R.string.ly_img_showcases_title_apparel,
            span = columns,
            items = listOf(
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.thumbnail_apparel_ui_b_1,
                    thumbnailAspectRatio = APPAREL_THUMBNAIL_ASPECT_RATIO,
                    actionScene = "apparel-ui-b-1",
                    actionScreen = Screen.ApparelUi,
                ),
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.thumbnail_apparel_ui_b_2,
                    thumbnailAspectRatio = APPAREL_THUMBNAIL_ASPECT_RATIO,
                    actionScene = "apparel-ui-b-2",
                    actionScreen = Screen.ApparelUi,
                ),
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.thumbnail_apparel_ui_b_3,
                    thumbnailAspectRatio = APPAREL_THUMBNAIL_ASPECT_RATIO,
                    actionScene = "apparel-ui-b-3",
                    actionScreen = Screen.ApparelUi,
                ),
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.thumbnail_apparel_ui_b_4,
                    thumbnailAspectRatio = APPAREL_THUMBNAIL_ASPECT_RATIO,
                    actionScene = "apparel-ui-b-4",
                    actionScreen = Screen.ApparelUi,
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.apparel_ui_blank,
                    label = R.string.ly_img_showcases_button_apparel_blank_title,
                    sublabel = R.string.ly_img_showcases_button_apparel_blank_subtitle,
                    actionScene = "apparel", // From editor package
                    actionScreen = Screen.ApparelUi,
                ),
            ),
        ),
        ShowcaseItem.Header(
            title = R.string.ly_img_showcases_title_postcard,
            span = columns,
            items = listOf(
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.thumbnail_bonjour_paris,
                    thumbnailAspectRatio = POSTCARD_THUMBNAIL_ASPECT_RATIO,
                    actionScene = "bonjour_paris",
                    actionScreen = Screen.PostcardUi,
                ),
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.thumbnail_merry_christmas,
                    thumbnailAspectRatio = POSTCARD_THUMBNAIL_ASPECT_RATIO,
                    actionScene = "merry_christmas",
                    actionScreen = Screen.PostcardUi,
                ),
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.thumbnail_thank_you,
                    thumbnailAspectRatio = POSTCARD_THUMBNAIL_ASPECT_RATIO,
                    actionScene = "thank_you",
                    actionScreen = Screen.PostcardUi,
                ),
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.thumbnail_wish_you_were_here,
                    thumbnailAspectRatio = POSTCARD_THUMBNAIL_ASPECT_RATIO,
                    actionScene = "wish_you_were_here",
                    actionScreen = Screen.PostcardUi,
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.postcard_ui_blank,
                    label = R.string.ly_img_showcases_button_postcard_blank_title,
                    sublabel = R.string.ly_img_showcases_button_postcard_blank_subtitle,
                    actionScene = "postcard", // From editor package
                    actionScreen = Screen.PostcardUi,
                ),
            ),
        ),
        ShowcaseItem.Header(
            title = R.string.ly_img_showcases_title_custom_functionalities,
            span = columns,
            items = listOf(
                ShowcaseItem.CustomFunctionality(
                    vectorIcon = IconPack.CustomFunctionalitiesTextToImage,
                    thumbnailRes = R.drawable.custom_functionality_text_to_image,
                    label = R.string.ly_img_showcases_button_custom_ai_text_to_image_title,
                    sublabel = R.string.ly_img_showcases_button_custom_ai_text_to_image_subtitle,
                    actionScene = null,
                    actionScreen = Screen.TextToImage,
                ),
                ShowcaseItem.CustomFunctionality(
                    vectorIcon = IconPack.CustomFunctionalitiesBackgroundRemoval,
                    thumbnailRes = R.drawable.custom_functionality_background_removal,
                    label = R.string.ly_img_showcases_button_custom_bg_removal_title,
                    sublabel = R.string.ly_img_showcases_button_custom_bg_removal_subtitle,
                    actionScene = null,
                    actionScreen = Screen.BackgroundRemoval,
                    clickAction = ShowcaseItem.CarouselContent.ClickAction.PICK_IMAGE,
                ),
            ),
        ),
    )

    private val colorPaletteMapping by lazy {
        mapOf(
            "bonjour_paris" to
                listOf(
                    Color(0xFF000000),
                    Color(0xFFFFFFFF),
                    Color(0xFF4932D1),
                    Color(0xFFFE6755),
                    Color(0xFF606060),
                    Color(0xFF696969),
                    Color(0xFF999999),
                ),
            "merry_christmas" to
                listOf(
                    Color(0xFF536F1A),
                    Color(0xFFFFFFFF),
                    Color(0xFF6B2923),
                    Color(0xFFF3AE2B),
                    Color(0xFF051111),
                    Color(0xFF696969),
                    Color(0xFF999999),
                ),
            "thank_you" to
                listOf(
                    Color(0xFFE09F96),
                    Color(0xFFFFFFFF),
                    Color(0xFF761E40),
                    Color(0xFF7471A3),
                    Color(0xFF20121F),
                    Color(0xFF696969),
                    Color(0xFF999999),
                ),
            "wish_you_were_here" to
                listOf(
                    Color(0xFFE75050),
                    Color(0xFFFFFFFF),
                    Color(0xFF111111),
                    Color(0xFF282929),
                    Color(0xFF619888),
                    Color(0xFF696969),
                    Color(0xFF999999),
                ),
            "postcard" to
                listOf(
                    Color.Blue,
                    Color.Green,
                    Color.Yellow,
                    Color.Red,
                    Color.Black,
                    Color.White,
                    Color.Gray,
                ),
        )
    }

    suspend fun addRemoteAssetSources(
        scope: EditorScope,
        isVideoScene: Boolean,
    ) = scope.run {
        if (remoteAssetsSupported.not()) return
        val remoteAssetSourcesSet = buildSet {
            add(RemoteAssetSource.Path.ImagePexels)
            add(RemoteAssetSource.Path.ImageUnsplash)
            if (isVideoScene) {
                add(RemoteAssetSource.Path.VideoPexels)
                add(RemoteAssetSource.Path.VideoGiphy)
                add(RemoteAssetSource.Path.VideoGiphySicker)
            }
        }
        editorContext.engine.addRemoteAssetSources(
            host = Secrets.remoteAssetSourceHost,
            paths = remoteAssetSourcesSet,
        )
    }

    fun getAssetLibrary(isVideoScene: Boolean): AssetLibrary {
        val unsplashSection = LibraryContent.Section(
            titleRes = R.string.ly_img_showcases_asset_library_section_unsplash,
            sourceTypes = listOf(AssetSourceType(sourceId = "ly.img.image.unsplash")),
            assetType = AssetType.Image,
        )

        val pexelsImagesSection = LibraryContent.Section(
            titleRes = R.string.ly_img_showcases_asset_library_section_pexels,
            sourceTypes = listOf(AssetSourceType(sourceId = "ly.img.image.pexels")),
            assetType = AssetType.Image,
        )

        val pexelsVideoSection = LibraryContent.Section(
            titleRes = R.string.ly_img_showcases_asset_library_section_pexels,
            sourceTypes = listOf(AssetSourceType(sourceId = "ly.img.video.pexels")),
            assetType = AssetType.Video,
        )

        val giphyVideoSection = LibraryContent.Section(
            titleRes = R.string.ly_img_showcases_asset_library_section_giphy,
            sourceTypes = listOf(AssetSourceType(sourceId = "ly.img.video.giphy")),
            assetType = AssetType.Video,
        )

        val defaultImagesContent = LibraryCategory.Images.content as LibraryContent.Sections
        val defaultVideoContent = LibraryCategory.Video.content as LibraryContent.Sections

        val videos = LibraryCategory.Video.copy(
            content = defaultVideoContent.copy(
                sections = listOf(pexelsVideoSection, giphyVideoSection) + defaultVideoContent.sections,
            ),
        )

        val images = LibraryCategory.Images.copy(
            content = defaultImagesContent.copy(
                sections = listOf(pexelsImagesSection, unsplashSection) + defaultImagesContent.sections,
            ),
        )

        val stickersCategory = LibraryCategory.Stickers

        return AssetLibrary.getDefault(
            includeAVResources = isVideoScene,
            images = images,
            videos = videos,
            stickers = if (isVideoScene.not()) {
                stickersCategory
            } else {
                val content = stickersCategory.content as LibraryContent.Sections
                stickersCategory.copy(
                    content = (
                        content.copy(
                            sections = listOf(
                                LibraryContent.Section(
                                    titleRes = R.string.ly_img_showcases_asset_library_section_giphy_stickers,
                                    sourceTypes = listOf(AssetSourceType(sourceId = "ly.img.video.giphy.sticker")),
                                    assetType = AssetType.Sticker,
                                ),
                            ) + content.sections,
                        )
                    ),
                )
            },
        )
    }

    @OptIn(UnstableEditorApi::class)
    fun getColorPalette(sceneUri: Uri?): List<Color> {
        sceneUri ?: return fillAndStrokeColors
        val scene = sceneUri.pathSegments.lastOrNull() ?: return fillAndStrokeColors
        return colorPaletteMapping[scene] ?: fillAndStrokeColors
    }

    companion object {
        const val CATALOG_COLUMNS_SIZE = 2
        private const val APPAREL_THUMBNAIL_ASPECT_RATIO = 123F / 152F
        private const val POSTCARD_THUMBNAIL_ASPECT_RATIO = 208F / 152F
        private const val VIDEO_THUMBNAIL_ASPECT_RATIO = 95F / 152F
    }
}
