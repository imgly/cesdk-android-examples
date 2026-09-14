package ly.img.editor.showcases

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import ly.img.editor.plugin.backgroundRemoval.iconPack.BackgroundRemoval
import ly.img.editor.showcases.icon.CustomFunctionalitiesTextToImage
import ly.img.editor.showcases.icon.IconPack
import ly.img.editor.plugin.backgroundRemoval.iconPack.IconPack as BackgroundRemovalIconPack

@Stable
class ShowcasesViewModel : ViewModel() {
    fun getItems() = listOfNotNull(
        ShowcaseItem.Header(
            title = R.string.ly_img_showcases_title_photo,
            span = CATALOG_COLUMNS_SIZE,
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
            span = CATALOG_COLUMNS_SIZE,
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
            span = CATALOG_COLUMNS_SIZE,
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
            span = CATALOG_COLUMNS_SIZE,
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
            span = CATALOG_COLUMNS_SIZE,
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
            span = CATALOG_COLUMNS_SIZE,
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
                    vectorIcon = BackgroundRemovalIconPack.BackgroundRemoval,
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

    private companion object {
        const val CATALOG_COLUMNS_SIZE = 2
        const val APPAREL_THUMBNAIL_ASPECT_RATIO = 123F / 152F
        const val POSTCARD_THUMBNAIL_ASPECT_RATIO = 208F / 152F
        const val VIDEO_THUMBNAIL_ASPECT_RATIO = 95F / 152F
    }
}
