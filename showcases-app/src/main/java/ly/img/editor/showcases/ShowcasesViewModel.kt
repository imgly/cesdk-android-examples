package ly.img.editor.showcases

import RemoteAssetSource
import addRemoteAssetSources
import android.net.Uri
import android.util.SizeF
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.launch
import ly.img.editor.EditorConfiguration
import ly.img.editor.EditorDefaults
import ly.img.editor.EngineConfiguration
import ly.img.editor.core.component.EditorComponent.ListBuilder
import ly.img.editor.core.component.EditorComponent.ListBuilder.Companion.modify
import ly.img.editor.core.component.NavigationBar
import ly.img.editor.core.component.closeEditor
import ly.img.editor.core.component.rememberCloseEditor
import ly.img.editor.core.component.rememberForApparel
import ly.img.editor.core.component.rememberForDesign
import ly.img.editor.core.component.rememberForPhoto
import ly.img.editor.core.component.rememberForPostcard
import ly.img.editor.core.component.rememberForVideo
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.LibraryContent
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.rememberForApparel
import ly.img.editor.rememberForDesign
import ly.img.editor.rememberForPhoto
import ly.img.editor.rememberForPostcard
import ly.img.editor.rememberForVideo
import ly.img.editor.showcases.icon.Homeoutline
import ly.img.editor.showcases.icon.IconPack
import ly.img.engine.MimeType
import ly.img.engine.SceneMode
import ly.img.engine.populateAssetSource

class ShowcasesViewModel : ViewModel() {
    private val remoteAssetsSupported = Secrets.remoteAssetSourceHost.isNotEmpty()

    fun getItems(columns: Int) = listOfNotNull(
        ShowcaseItem.Header(
            title = R.string.showcase_header_title_photo,
            span = columns,
            items = listOf(
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.photo_ui_choose_from_gallery,
                    label = R.string.showcase_label_choose_from_gallery,
                    clickAction = ShowcaseItem.CarouselContent.ClickAction.PICK_IMAGE,
                    sublabel = null,
                    actionScene = null,
                    actionScreen = Screen.PhotoUi,
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.photo_ui_1_1,
                    label = R.string.showcase_photo_editor_label_1_1,
                    sublabel = R.string.showcase_sublabel_instagram_post,
                    clickAction = ShowcaseItem.CarouselContent.ClickAction.PICK_IMAGE,
                    actionScene = "1_1",
                    actionScreen = Screen.PhotoUi,
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.photo_ui_9_16,
                    label = R.string.showcase_photo_editor_label_9_16,
                    sublabel = R.string.showcase_sublabel_instagram_story,
                    clickAction = ShowcaseItem.CarouselContent.ClickAction.PICK_IMAGE,
                    actionScene = "9_16",
                    actionScreen = Screen.PhotoUi,
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.photo_ui_16_9,
                    label = R.string.showcase_photo_editor_label_16_9,
                    sublabel = R.string.showcase_sublabel_full_hd,
                    clickAction = ShowcaseItem.CarouselContent.ClickAction.PICK_IMAGE,
                    actionScene = "16_9",
                    actionScreen = Screen.PhotoUi,
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.photo_ui_2_3,
                    label = R.string.showcase_photo_editor_label_2_3,
                    sublabel = R.string.showcase_sublabel_2_3,
                    clickAction = ShowcaseItem.CarouselContent.ClickAction.PICK_IMAGE,
                    actionScene = "2_3",
                    actionScreen = Screen.PhotoUi,
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.photo_ui_3_2,
                    label = R.string.showcase_photo_editor_label_3_2,
                    sublabel = R.string.showcase_sublabel_3_2,
                    clickAction = ShowcaseItem.CarouselContent.ClickAction.PICK_IMAGE,
                    actionScene = "3_2",
                    actionScreen = Screen.PhotoUi,
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.photo_ui_3_4,
                    label = R.string.showcase_photo_editor_label_3_4,
                    sublabel = R.string.showcase_sublabel_3_4,
                    clickAction = ShowcaseItem.CarouselContent.ClickAction.PICK_IMAGE,
                    actionScene = "3_4",
                    actionScreen = Screen.PhotoUi,
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.photo_ui_4_3,
                    label = R.string.showcase_photo_editor_label_4_3,
                    sublabel = R.string.showcase_sublabel_4_3,
                    clickAction = ShowcaseItem.CarouselContent.ClickAction.PICK_IMAGE,
                    actionScene = "4_3",
                    actionScreen = Screen.PhotoUi,
                ),
            ),
        ),
        ShowcaseItem.Header(
            title = R.string.showcase_header_title_design,
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
                    label = R.string.showcase_label_instagram_story,
                    sublabel = R.string.showcase_sublabel_instagram_story,
                    actionScene = "design_ui_instagram_story",
                    actionScreen = Screen.DesignUi,
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.design_ui_instagram_post,
                    label = R.string.showcase_label_instagram_post,
                    sublabel = R.string.showcase_sublabel_instagram_post,
                    actionScene = "design_ui_instagram_post",
                    actionScreen = Screen.DesignUi,
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.design_ui_video_full_hd,
                    label = R.string.showcase_label_full_hd,
                    sublabel = R.string.showcase_sublabel_full_hd,
                    actionScene = "design_ui_video_full_hd",
                    actionScreen = Screen.DesignUi,
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.design_ui_x_header,
                    label = R.string.showcase_label_x_header,
                    sublabel = R.string.showcase_sublabel_x_header,
                    actionScene = "design_ui_x_header",
                    actionScreen = Screen.DesignUi,
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.design_ui_x_profile_picture,
                    label = R.string.showcase_label_x_profile_picture,
                    sublabel = R.string.showcase_sublabel_x_profile_picture,
                    actionScene = "design_ui_x_profile_picture",
                    actionScreen = Screen.DesignUi,
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.design_ui_booklet,
                    label = R.string.showcase_label_booklet,
                    sublabel = R.string.showcase_sublabel_booklet,
                    actionScene = "design_ui_booklet",
                    actionScreen = Screen.DesignUi,
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.design_ui_business_card,
                    label = R.string.showcase_label_business_card,
                    sublabel = R.string.showcase_sublabel_business_card,
                    actionScene = "design_ui_business_card",
                    actionScreen = Screen.DesignUi,
                ),
                ShowcaseItem.CarouselContent(
                    iconRes = R.drawable.design_ui_open_design,
                    label = R.string.showcase_label_open_design,
                    sublabel = R.string.showcase_sublabel_open_design,
                    clickAction = ShowcaseItem.CarouselContent.ClickAction.PICK_SCENE,
                    hasDotLine = true,
                    actionScene = null,
                    actionScreen = Screen.DesignUi,
                ),
            ),
        ),
        ShowcaseItem.Header(
            title = R.string.showcase_header_title_video,
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
                    label = R.string.showcase_video_editor_label_blank,
                    sublabel = R.string.showcase_sublabel_instagram_story,
                    actionScene = "video-empty",
                    actionScreen = Screen.VideoUi,
                ),
            ),
        ),
        ShowcaseItem.Header(
            title = R.string.showcase_header_title_apparel,
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
                    label = R.string.showcase_apparel_editor_label_blank,
                    sublabel = R.string.showcase_sublabel_booklet,
                    actionScene = "apparel-ui-b-empty",
                    actionScreen = Screen.ApparelUi,
                ),
            ),
        ),
        ShowcaseItem.Header(
            title = R.string.showcase_header_title_postcard,
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
                    label = R.string.showcase_postcard_editor_label_blank,
                    sublabel = R.string.showcase_sublabel_postcard,
                    actionScene = "postcard-empty",
                    actionScreen = Screen.PostcardUi,
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

    private val defaultColorPalette by lazy {
        listOf(
            Color(0xFF4A67FF),
            Color(0xFFFFD333),
            Color(0xFFC41230),
            Color(0xFF000000),
            Color(0xFFFFFFFF),
        )
    }

    private val stickerMiscId = "ly.img.sticker.misc"

    @Composable
    fun rememberEngineConfigurationForScene(sceneUri: Uri): EngineConfiguration = EngineConfiguration.remember(
        license = Secrets.license,
        onCreate = {
            val engine = this.editorContext.engine
            val eventHandler = this.editorContext.eventHandler
            EditorDefaults.onCreate(engine, sceneUri, eventHandler) { _, scope ->
                val isVideoScene = engine.scene.getMode() == SceneMode.VIDEO
                if (remoteAssetsSupported) {
                    scope.launch {
                        val remoteAssetSourcesSet = buildSet {
                            add(RemoteAssetSource.Path.ImagePexels)
                            add(RemoteAssetSource.Path.ImageUnsplash)
                            if (isVideoScene) {
                                add(RemoteAssetSource.Path.VideoPexels)
                                add(RemoteAssetSource.Path.VideoGiphy)
                                add(RemoteAssetSource.Path.VideoGiphySicker)
                            }
                        }
                        engine.addRemoteAssetSources(
                            host = Secrets.remoteAssetSourceHost,
                            paths = remoteAssetSourcesSet,
                        )
                    }
                }
                if (isVideoScene) {
                    scope.launch {
                        engine.populateAssetSource(
                            id = stickerMiscId,
                            jsonUri = Uri.parse("https://cdn.img.ly/assets/demo/v2/ly.img.sticker.misc/content.json"),
                            replaceBaseUri = Uri.parse("https://cdn.img.ly/assets/demo/v2/ly.img.sticker.misc"),
                        )
                    }
                }
            }
        },
    )

    @Composable
    fun rememberEngineConfigurationForImage(
        imageUri: Uri?,
        dimensionsAsString: String?,
    ): EngineConfiguration {
        val size = remember {
            dimensionsAsString
                ?.let { param ->
                    runCatching {
                        val dimensions = param.split("_")
                        require(dimensions.size == 2)
                        val widthRatio = dimensions[0].toInt()
                        val heightRatio = dimensions[1].toInt()
                        val fixedDimension = 1080F
                        if (widthRatio < heightRatio) {
                            SizeF(fixedDimension, fixedDimension * heightRatio.toFloat() / widthRatio)
                        } else {
                            SizeF(fixedDimension * widthRatio.toFloat() / heightRatio, fixedDimension)
                        }
                    }.getOrNull()
                }
        }
        return EngineConfiguration.remember(
            license = Secrets.license,
            onCreate = {
                val engine = editorContext.engine
                val eventHandler = editorContext.eventHandler
                EditorDefaults.onCreateFromImage(
                    engine = engine,
                    imageUri = imageUri ?: Uri.parse("file:///android_asset/images/photo-ui-empty.png"),
                    eventHandler = eventHandler,
                    size = size,
                ) { _, _ ->
                    if (remoteAssetsSupported) {
                        engine.addRemoteAssetSources(
                            host = Secrets.remoteAssetSourceHost,
                            paths = setOf(RemoteAssetSource.Path.ImageUnsplash),
                        )
                    }
                }
            },
            onExport = {
                EditorDefaults.onExport(editorContext.engine, editorContext.eventHandler, MimeType.PNG)
            },
        )
    }

    @Composable
    fun rememberEditorConfiguration(
        sceneUri: Uri?,
        screen: Screen,
    ): EditorConfiguration<*> = when (screen) {
        Screen.DesignUi ->
            EditorConfiguration.rememberForDesign(
                navigationBar = {
                    NavigationBar.rememberForDesign(
                        listBuilder = buildNavigationBarList(NavigationBar.ListBuilder.rememberForDesign()),
                    )
                },
                assetLibrary = remember { getAssetLibrary(screen) },
                colorPalette = remember { getColorPalette(sceneUri) },
            )

        Screen.PhotoUi ->
            EditorConfiguration.rememberForPhoto(
                navigationBar = {
                    NavigationBar.rememberForPhoto(
                        listBuilder = buildNavigationBarList(NavigationBar.ListBuilder.rememberForPhoto()),
                    )
                },
                assetLibrary = remember { getAssetLibrary(screen) },
                colorPalette = remember { getColorPalette(sceneUri) },
            )

        Screen.VideoUi ->
            EditorConfiguration.rememberForVideo(
                navigationBar = {
                    NavigationBar.rememberForVideo(
                        listBuilder = buildNavigationBarList(NavigationBar.ListBuilder.rememberForVideo()),
                    )
                },
                assetLibrary = remember { getAssetLibrary(screen) },
                colorPalette = remember { getColorPalette(sceneUri) },
            )

        Screen.ApparelUi ->
            EditorConfiguration.rememberForApparel(
                navigationBar = {
                    NavigationBar.rememberForApparel(
                        listBuilder = buildNavigationBarList(NavigationBar.ListBuilder.rememberForApparel()),
                    )
                },
                assetLibrary = remember { getAssetLibrary(screen) },
                colorPalette = remember { getColorPalette(sceneUri) },
            )

        Screen.PostcardUi ->
            EditorConfiguration.rememberForPostcard(
                navigationBar = {
                    NavigationBar.rememberForPostcard(
                        listBuilder = buildNavigationBarList(NavigationBar.ListBuilder.rememberForPostcard()),
                    )
                },
                assetLibrary = remember { getAssetLibrary(screen) },
                colorPalette = remember { getColorPalette(sceneUri) },
            )

        else -> error("Unknown editor screen $screen")
    }

    @Composable
    private fun buildNavigationBarList(
        baseNavigationBar: ListBuilder<NavigationBar.Item<*>, Alignment.Horizontal, Arrangement.Horizontal>,
    ): ListBuilder<NavigationBar.Item<*>, Alignment.Horizontal, Arrangement.Horizontal> = baseNavigationBar.modify {
        replace(id = NavigationBar.Button.Id.closeEditor) {
            NavigationBar.Button.rememberCloseEditor(
                vectorIcon = { IconPack.Homeoutline },
            )
        }
    }

    private fun getAssetLibrary(screen: Screen): AssetLibrary {
        val unsplashSection = LibraryContent.Section(
            titleRes = R.string.unsplash,
            sourceTypes = listOf(AssetSourceType(sourceId = "ly.img.image.unsplash")),
            assetType = AssetType.Image,
        )

        val pexelsImagesSection = LibraryContent.Section(
            titleRes = R.string.pexels,
            sourceTypes = listOf(AssetSourceType(sourceId = "ly.img.image.pexels")),
            assetType = AssetType.Image,
        )

        val pexelsVideoSection = LibraryContent.Section(
            titleRes = R.string.pexels,
            sourceTypes = listOf(AssetSourceType(sourceId = "ly.img.video.pexels")),
            assetType = AssetType.Video,
        )

        val giphyVideoSection = LibraryContent.Section(
            titleRes = R.string.giphy,
            sourceTypes = listOf(AssetSourceType(sourceId = "ly.img.video.giphy")),
            assetType = AssetType.Video,
        )

        val defaultImagesContent = LibraryCategory.Images.content as LibraryContent.Sections
        val defaultVideoContent = LibraryCategory.Video.content as LibraryContent.Sections

        val stickersMiscAssetSourceType = AssetSourceType(stickerMiscId)

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

        return AssetLibrary.getDefault(
            images = images,
            videos = videos,
            stickers = if (screen != Screen.VideoUi) {
                LibraryCategory.Stickers
            } else {
                LibraryCategory.Stickers.copy(
                    content = LibraryContent.Stickers.copy(
                        sections = listOf(
                            LibraryContent.Section(
                                titleRes = R.string.ly_img_sticker_giphy,
                                sourceTypes = listOf(AssetSourceType(sourceId = "ly.img.video.giphy.sticker")),
                                assetType = AssetType.Sticker,
                            ),
                            LibraryContent.Section(
                                titleRes = ly.img.editor.core.R.string.ly_img_editor_stickers_hands,
                                sourceTypes = listOf(AssetSourceType.Stickers),
                                groups = listOf("//ly.img.cesdk.stickers.hand/category/hand"),
                                assetType = AssetType.Sticker,
                            ),
                            LibraryContent.Section(
                                titleRes = R.string.ly_img_sticker_misc_sketches,
                                sourceTypes = listOf(stickersMiscAssetSourceType),
                                groups = listOf("sketches"),
                                assetType = AssetType.Sticker,
                            ),
                            LibraryContent.Section(
                                titleRes = R.string.ly_img_sticker_misc_tape,
                                sourceTypes = listOf(stickersMiscAssetSourceType),
                                groups = listOf("tape"),
                                assetType = AssetType.Sticker,
                            ),
                            LibraryContent.Section(
                                titleRes = R.string.ly_img_sticker_misc_marker,
                                sourceTypes = listOf(stickersMiscAssetSourceType),
                                groups = listOf("marker"),
                                assetType = AssetType.Sticker,
                            ),
                            LibraryContent.Section(
                                titleRes = R.string.ly_img_sticker_misc_3dstickers,
                                sourceTypes = listOf(stickersMiscAssetSourceType),
                                groups = listOf("3dstickers"),
                                assetType = AssetType.Sticker,
                            ),
                        ),
                    ),
                )
            },
        )
    }

    private fun getColorPalette(sceneUri: Uri?): List<Color> {
        sceneUri ?: return defaultColorPalette
        val scene = sceneUri.pathSegments.lastOrNull() ?: return defaultColorPalette
        return colorPaletteMapping[scene] ?: defaultColorPalette
    }

    companion object {
        const val CATALOG_COLUMNS_SIZE = 2
        private const val APPAREL_THUMBNAIL_ASPECT_RATIO = 123F / 152F
        private const val POSTCARD_THUMBNAIL_ASPECT_RATIO = 208F / 152F
        private const val VIDEO_THUMBNAIL_ASPECT_RATIO = 95F / 152F
    }
}
