package ly.img.editor.showcases

import android.app.Application
import androidx.compose.runtime.Stable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ly.img.editor.plugin.backgroundRemoval.IMGLYBackgroundRemovalConfig
import ly.img.editor.plugin.backgroundRemoval.iconPack.BackgroundRemoval
import ly.img.editor.plugin.backgroundRemoval.remover.IMGLYBackgroundRemover
import ly.img.editor.showcases.ShowcaseItem.ClickAction
import ly.img.editor.showcases.icon.IconPack
import ly.img.editor.showcases.icon.TextToImage
import ly.img.editor.plugin.backgroundRemoval.iconPack.IconPack as BackgroundRemovalIconPack

@Stable
class ShowcasesViewModel(
    application: Application,
) : AndroidViewModel(application) {
    init {
        // Pre-download the model for faster background removal preparation time.
        viewModelScope.launch {
            runCatching {
                IMGLYBackgroundRemover(IMGLYBackgroundRemovalConfig())
                    .forceDownloadModel(application)
            }
        }
    }

    fun getItems() = listOfNotNull(
        ShowcaseItem.Header(
            title = R.string.ly_img_showcases_title_photo,
            span = CATALOG_COLUMNS_SIZE,
            items = listOf(
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.photo_ui_choose_from_gallery,
                    label = R.string.ly_img_showcases_button_design_gallery_title,
                    sublabel = null,
                    clickAction = ClickAction(
                        requestImage = true,
                        destination = Screen.PhotoUi,
                    ),
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.photo_ui_1_1,
                    label = R.string.ly_img_showcases_button_photo_1_1_title,
                    sublabel = R.string.ly_img_showcases_button_photo_1_1_subtitle,
                    clickAction = ClickAction(
                        requestImage = true,
                        sceneId = "1_1",
                        destination = Screen.PhotoUi,
                    ),
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.photo_ui_9_16,
                    label = R.string.ly_img_showcases_button_photo_9_16_title,
                    sublabel = R.string.ly_img_showcases_button_photo_9_16_subtitle,
                    clickAction = ClickAction(
                        requestImage = true,
                        sceneId = "9_16",
                        destination = Screen.PhotoUi,
                    ),
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.photo_ui_16_9,
                    label = R.string.ly_img_showcases_button_photo_16_9_title,
                    sublabel = R.string.ly_img_showcases_button_design_full_hd_subtitle,
                    clickAction = ClickAction(
                        requestImage = true,
                        sceneId = "16_9",
                        destination = Screen.PhotoUi,
                    ),
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.photo_ui_2_3,
                    label = R.string.ly_img_showcases_button_photo_2_3_title,
                    sublabel = R.string.ly_img_showcases_button_photo_2_3_subtitle,
                    clickAction = ClickAction(
                        requestImage = true,
                        sceneId = "2_3",
                        destination = Screen.PhotoUi,
                    ),
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.photo_ui_3_2,
                    label = R.string.ly_img_showcases_button_photo_3_2_title,
                    sublabel = R.string.ly_img_showcases_button_photo_3_2_subtitle,
                    clickAction = ClickAction(
                        requestImage = true,
                        sceneId = "3_2",
                        destination = Screen.PhotoUi,
                    ),
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.photo_ui_3_4,
                    label = R.string.ly_img_showcases_button_photo_3_4_title,
                    sublabel = R.string.ly_img_showcases_button_photo_3_4_subtitle,
                    clickAction = ClickAction(
                        requestImage = true,
                        sceneId = "3_4",
                        destination = Screen.PhotoUi,
                    ),
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.photo_ui_4_3,
                    label = R.string.ly_img_showcases_button_photo_4_3_title,
                    sublabel = R.string.ly_img_showcases_button_photo_4_3_subtitle,
                    clickAction = ClickAction(
                        requestImage = true,
                        sceneId = "4_3",
                        destination = Screen.PhotoUi,
                    ),
                ),
            ),
        ),
        ShowcaseItem.Header(
            title = R.string.ly_img_showcases_title_design,
            span = CATALOG_COLUMNS_SIZE,
            items = listOf(
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.design_ui_instagram_story_template,
                    clickAction = ClickAction(
                        sceneId = "design_ui_instagram_story_template",
                        destination = Screen.DesignUi,
                    ),
                ),
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.design_ui_instagram_post_template,
                    clickAction = ClickAction(
                        sceneId = "design_ui_instagram_post_template",
                        destination = Screen.DesignUi,
                    ),
                ),
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.design_ui_video_full_hd_template,
                    clickAction = ClickAction(
                        sceneId = "design_ui_video_full_hd_template",
                        destination = Screen.DesignUi,
                    ),
                ),
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.design_ui_x_profile_picture_template,
                    clickAction = ClickAction(
                        sceneId = "design_ui_x_profile_picture_template",
                        destination = Screen.DesignUi,
                    ),
                ),
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.design_ui_x_header_template,
                    clickAction = ClickAction(
                        sceneId = "design_ui_x_header_template",
                        destination = Screen.DesignUi,
                    ),
                ),
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.design_ui_booklet_template,
                    clickAction = ClickAction(
                        sceneId = "design_ui_booklet_template",
                        destination = Screen.DesignUi,
                    ),
                ),
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.design_ui_business_card_template,
                    clickAction = ClickAction(
                        sceneId = "design_ui_business_card_template",
                        destination = Screen.DesignUi,
                    ),
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.design_ui_instagram_story,
                    label = R.string.ly_img_showcases_button_design_instagram_story_title,
                    sublabel = R.string.ly_img_showcases_button_design_instagram_story_subtitle,
                    clickAction = ClickAction(
                        sceneId = "design_ui_instagram_story",
                        destination = Screen.DesignUi,
                    ),
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.design_ui_instagram_post,
                    label = R.string.ly_img_showcases_button_design_instagram_post_title,
                    sublabel = R.string.ly_img_showcases_button_design_instagram_post_subtitle,
                    clickAction = ClickAction(
                        sceneId = "design_ui_instagram_post",
                        destination = Screen.DesignUi,
                    ),
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.design_ui_video_full_hd,
                    label = R.string.ly_img_showcases_button_design_full_hd_title,
                    sublabel = R.string.ly_img_showcases_button_design_full_hd_subtitle,
                    clickAction = ClickAction(
                        sceneId = "design_ui_video_full_hd",
                        destination = Screen.DesignUi,
                    ),
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.design_ui_x_header,
                    label = R.string.ly_img_showcases_button_design_x_header_title,
                    sublabel = R.string.ly_img_showcases_button_design_x_header_subtitle,
                    clickAction = ClickAction(
                        sceneId = "design_ui_x_header",
                        destination = Screen.DesignUi,
                    ),
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.design_ui_x_profile_picture,
                    label = R.string.ly_img_showcases_button_design_x_profile_picture_title,
                    sublabel = R.string.ly_img_showcases_button_design_x_profile_picture_subtitle,
                    clickAction = ClickAction(
                        sceneId = "design_ui_x_profile_picture",
                        destination = Screen.DesignUi,
                    ),
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.design_ui_booklet,
                    label = R.string.ly_img_showcases_button_design_booklet_title,
                    sublabel = R.string.ly_img_showcases_button_design_booklet_subtitle,
                    clickAction = ClickAction(
                        sceneId = "design_ui_booklet",
                        destination = Screen.DesignUi,
                    ),
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.design_ui_business_card,
                    label = R.string.ly_img_showcases_button_design_business_card_title,
                    sublabel = R.string.ly_img_showcases_button_design_business_card_subtitle,
                    clickAction = ClickAction(
                        sceneId = "design_ui_business_card",
                        destination = Screen.DesignUi,
                    ),
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.design_ui_open_design,
                    label = R.string.ly_img_showcases_button_design_import_title,
                    sublabel = R.string.ly_img_showcases_button_design_import_subtitle,
                    hasDotLine = true,
                    clickAction = ClickAction(
                        destination = Screen.DesignUi,
                        requestScene = true,
                    ),
                ),
            ),
        ),
        ShowcaseItem.Header(
            title = R.string.ly_img_showcases_title_video,
            span = CATALOG_COLUMNS_SIZE,
            items = listOf(
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.thumbnail_red_dot,
                    thumbnailAspectRatio = VIDEO_THUMBNAIL_ASPECT_RATIO,
                    clickAction = ClickAction(
                        sceneId = "red-dot",
                        destination = Screen.VideoUi,
                    ),
                ),
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.thumbnail_milli_surf_school,
                    thumbnailAspectRatio = VIDEO_THUMBNAIL_ASPECT_RATIO,
                    clickAction = ClickAction(
                        sceneId = "milli-surf-school",
                        destination = Screen.VideoUi,
                    ),
                ),
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.thumbnail_my_plants,
                    thumbnailAspectRatio = VIDEO_THUMBNAIL_ASPECT_RATIO,
                    clickAction = ClickAction(
                        sceneId = "my-plants",
                        destination = Screen.VideoUi,
                    ),
                ),
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.thumbnail_monthly_review,
                    thumbnailAspectRatio = VIDEO_THUMBNAIL_ASPECT_RATIO,
                    clickAction = ClickAction(
                        sceneId = "monthly-review",
                        destination = Screen.VideoUi,
                    ),
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.video_ui_blank,
                    label = R.string.ly_img_showcases_button_video_blank_title,
                    sublabel = R.string.ly_img_showcases_button_video_blank_subtitle,
                    clickAction = ClickAction(
                        sceneId = "video", // From editor package
                        destination = Screen.VideoUi,
                    ),
                ),
            ),
        ),
        ShowcaseItem.Header(
            title = R.string.ly_img_showcases_title_apparel,
            span = CATALOG_COLUMNS_SIZE,
            items = listOf(
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.thumbnail_apparel_ui_b_1,
                    thumbnailAspectRatio = APPAREL_THUMBNAIL_ASPECT_RATIO,
                    clickAction = ClickAction(
                        sceneId = "apparel-ui-b-1",
                        destination = Screen.ApparelUi,
                    ),
                ),
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.thumbnail_apparel_ui_b_2,
                    thumbnailAspectRatio = APPAREL_THUMBNAIL_ASPECT_RATIO,
                    clickAction = ClickAction(
                        sceneId = "apparel-ui-b-2",
                        destination = Screen.ApparelUi,
                    ),
                ),
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.thumbnail_apparel_ui_b_3,
                    thumbnailAspectRatio = APPAREL_THUMBNAIL_ASPECT_RATIO,
                    clickAction = ClickAction(
                        sceneId = "apparel-ui-b-3",
                        destination = Screen.ApparelUi,
                    ),
                ),
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.thumbnail_apparel_ui_b_4,
                    thumbnailAspectRatio = APPAREL_THUMBNAIL_ASPECT_RATIO,
                    clickAction = ClickAction(
                        sceneId = "apparel-ui-b-4",
                        destination = Screen.ApparelUi,
                    ),
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.apparel_ui_blank,
                    label = R.string.ly_img_showcases_button_apparel_blank_title,
                    sublabel = R.string.ly_img_showcases_button_apparel_blank_subtitle,
                    clickAction = ClickAction(
                        sceneId = "apparel", // From editor package
                        destination = Screen.ApparelUi,
                    ),
                ),
            ),
        ),
        ShowcaseItem.Header(
            title = R.string.ly_img_showcases_title_postcard,
            span = CATALOG_COLUMNS_SIZE,
            items = listOf(
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.thumbnail_bonjour_paris,
                    thumbnailAspectRatio = POSTCARD_THUMBNAIL_ASPECT_RATIO,
                    clickAction = ClickAction(
                        sceneId = "bonjour_paris",
                        destination = Screen.PostcardUi,
                    ),
                ),
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.thumbnail_merry_christmas,
                    thumbnailAspectRatio = POSTCARD_THUMBNAIL_ASPECT_RATIO,
                    clickAction = ClickAction(
                        sceneId = "merry_christmas",
                        destination = Screen.PostcardUi,
                    ),
                ),
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.thumbnail_thank_you,
                    thumbnailAspectRatio = POSTCARD_THUMBNAIL_ASPECT_RATIO,
                    clickAction = ClickAction(
                        sceneId = "thank_you",
                        destination = Screen.PostcardUi,
                    ),
                ),
                ShowcaseItem.Content(
                    thumbnailRes = R.drawable.thumbnail_wish_you_were_here,
                    thumbnailAspectRatio = POSTCARD_THUMBNAIL_ASPECT_RATIO,
                    clickAction = ClickAction(
                        sceneId = "wish_you_were_here",
                        destination = Screen.PostcardUi,
                    ),
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.postcard_ui_blank,
                    label = R.string.ly_img_showcases_button_postcard_blank_title,
                    sublabel = R.string.ly_img_showcases_button_postcard_blank_subtitle,
                    clickAction = ClickAction(
                        sceneId = "postcard", // From editor package
                        destination = Screen.PostcardUi,
                    ),
                ),
            ),
        ),
        ShowcaseItem.Header(
            title = R.string.ly_img_showcases_title_custom_functionalities,
            span = CATALOG_COLUMNS_SIZE,
            items = listOf(
                ShowcaseItem.CustomFunctionality(
                    vectorIcon = IconPack.TextToImage,
                    thumbnailRes = R.drawable.custom_functionality_text_to_image,
                    label = R.string.ly_img_showcases_button_custom_ai_text_to_image_title,
                    sublabel = R.string.ly_img_showcases_button_custom_ai_text_to_image_subtitle,
                    clickAction = ClickAction(
                        destination = Screen.TextToImage,
                        requestApiKey = true,
                    ),
                ),
                ShowcaseItem.CustomFunctionality(
                    vectorIcon = BackgroundRemovalIconPack.BackgroundRemoval,
                    thumbnailRes = R.drawable.custom_functionality_background_removal,
                    label = R.string.ly_img_showcases_button_custom_bg_removal_title,
                    sublabel = R.string.ly_img_showcases_button_custom_bg_removal_subtitle,
                    clickAction = ClickAction(
                        destination = Screen.BackgroundRemoval,
                        requestImage = true,
                    ),
                ),
            ),
        ),
    )

    private companion object {
        const val CATALOG_COLUMNS_SIZE = 2
        const val APPAREL_THUMBNAIL_ASPECT_RATIO = 123F / 152F
        const val POSTCARD_THUMBNAIL_ASPECT_RATIO = 208F / 152F
        const val VIDEO_THUMBNAIL_ASPECT_RATIO = 95F / 152F
    }
}
