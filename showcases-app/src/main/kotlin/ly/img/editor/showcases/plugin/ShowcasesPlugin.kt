package ly.img.editor.showcases.plugin

import RemoteAssetSource
import addRemoteAssetSources
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ly.img.editor.BasicConfigurationBuilder
import ly.img.editor.core.EditorScope
import ly.img.editor.core.ScopedProperty
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.EditorComponent
import ly.img.editor.core.component.NavigationBar
import ly.img.editor.core.component.closeEditor
import ly.img.editor.core.component.modify
import ly.img.editor.core.component.rememberCloseEditor
import ly.img.editor.core.component.rememberImglyCamera
import ly.img.editor.core.component.systemCamera
import ly.img.editor.core.configuration.EditorConfigurationBuilder
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.LibraryContent
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.showcases.R
import ly.img.editor.showcases.Secrets
import ly.img.editor.showcases.icon.Homeoutline
import ly.img.editor.showcases.icon.IconPack

class ShowcasesPlugin : EditorConfigurationBuilder() {
    var isVideoScene by editorContext.mutableStateOf(
        key = KEY_STATE_IS_VIDEO_SCENE,
        initial = false,
    )

    var sceneUri by editorContext.mutableStateOf<Uri?>(
        key = KEY_STATE_SCENE_URI,
        initial = null,
    )

    // This property renders loading in BasicConfigurationBuilder
    private var showLoading: Boolean by editorContext.mutableStateOf(
        key = BasicConfigurationBuilder.KEY_STATE_SHOW_LOADING,
        initial = false,
    )

    private val remoteAssetsSupported = Secrets.remoteAssetSourceHost.isNotEmpty()

    override var onCreate: (suspend EditorScope.() -> Unit)? = {
        try {
            showLoading = true
            val editorScope = this
            coroutineScope {
                launch {
                    parentConfiguration?.onCreate?.invoke(editorScope)
                    // In case parent configuration hides the loading when done, immediately show again
                    showLoading = true
                }
                launch {
                    addRemoteAssetSources()
                }
            }
        } finally {
            showLoading = false
        }
    }

    private suspend fun EditorScope.addRemoteAssetSources() {
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

    override var assetLibrary: ScopedProperty<EditorScope, AssetLibrary?>? = {
        remember(isVideoScene) {
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

            AssetLibrary.getDefault(
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
    }

    override var colorPalette: ScopedProperty<EditorScope, List<Color>?>? = {
        remember(sceneUri) {
            sceneUri ?: return@remember parentConfiguration?.colorPalette
            val scene = sceneUri?.pathSegments?.lastOrNull() ?: return@remember parentConfiguration?.colorPalette
            colorPaletteMapping[scene] ?: parentConfiguration?.colorPalette
        }
    }

    override var navigationBar: ScopedProperty<EditorScope, EditorComponent<*>?>? = impl@{
        val parentNavigationBar = parentConfiguration?.navigationBar as? NavigationBar ?: return@impl null
        val updatedListBuilder = parentNavigationBar.listBuilder.modify {
            replace(id = NavigationBar.Button.Id.closeEditor) {
                NavigationBar.Button.rememberCloseEditor {
                    vectorIcon = { IconPack.Homeoutline }
                }
            }
        }
        remember(parentNavigationBar, updatedListBuilder) {
            parentNavigationBar.copy(listBuilder = updatedListBuilder)
        }
    }

    override var dock: ScopedProperty<EditorScope, EditorComponent<*>?>? = impl@{
        val parentDock = parentConfiguration?.dock as? Dock ?: return@impl null
        val updatedListBuilder = parentDock.listBuilder.modify {
            if (isVideoScene) {
                replace(id = Dock.Button.Id.systemCamera, failIfNotFound = false) {
                    Dock.Button.rememberImglyCamera()
                }
            }
        }
        remember(parentDock, updatedListBuilder) {
            parentDock.copy(listBuilder = updatedListBuilder)
        }
    }

    companion object {
        private const val KEY_STATE_IS_VIDEO_SCENE = "ly.img.editor.showcases.plugin.isVideoScene"
        private const val KEY_STATE_SCENE_URI = "ly.img.editor.showcases.plugin.sceneUri"

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
    }
}
