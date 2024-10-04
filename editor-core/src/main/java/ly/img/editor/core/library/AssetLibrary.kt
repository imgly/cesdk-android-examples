package ly.img.editor.core.library

import ly.img.engine.SceneMode

/**
 * Configuration class for the asset library.
 *
 * @param tabs a provider for the list of categories that are displayed as tabs in the asset library.
 * @param elements a provider for the category that is displaying elements tab.
 * @param images a provider for the category that is displayed when inserting/replacing an image asset.
 * @param videos a provider for the category that is displayed when inserting/replacing a video asset.
 * @param audios a provider for the category that is displayed when inserting/replacing an audio asset.
 * @param text a provider for the category that is displayed when inserting a text asset.
 * @param shapes a provider for the category that is displayed when inserting/replacing a shape asset.
 * @param stickers a provider for the category that is displayed when inserting/replacing a sticker asset.
 * @param overlays the category for displaying overlays.
 * @param clips the category for displaying clips.
 */
data class AssetLibrary(
    val tabs: (SceneMode) -> List<LibraryCategory>,
    val elements: (SceneMode) -> LibraryCategory = { LibraryCategory.getElements(it) },
    val images: (SceneMode) -> LibraryCategory = { LibraryCategory.Images },
    val videos: (SceneMode) -> LibraryCategory = { LibraryCategory.Video },
    val audios: (SceneMode) -> LibraryCategory = { LibraryCategory.Audio },
    val text: (SceneMode) -> LibraryCategory = { LibraryCategory.Text },
    val shapes: (SceneMode) -> LibraryCategory = { LibraryCategory.Shapes },
    val stickers: (SceneMode) -> LibraryCategory = { LibraryCategory.Stickers },
    val overlays: LibraryCategory = LibraryCategory.Overlays,
    val clips: LibraryCategory = LibraryCategory.Clips,
) {
    /**
     * Predefined tabs that can be displayed in the asset library.
     */
    enum class Tab {
        ELEMENTS,
        IMAGES,
        VIDEOS,
        AUDIOS,
        TEXT,
        SHAPES,
        STICKERS,
    }

    companion object {
        /**
         * A helper function for creating an [AssetLibrary] instance. This is an ideal builder in case you just want
         * to swap positions of tabs or drop some of the tabs. You can also modify a specific category by completely replacing it
         * or modifying using the helper functions [LibraryCategory.addSection], [LibraryCategory.dropSection] and
         * [LibraryCategory.replaceSection].
         *
         * @param tabs the list of tabs that should be displayed. The tabs are displayed in the same order as that of this list.
         * @param images the images category that is used in the tabs and the [images].
         * @param videos the videos category that is used in the tabs and the [videos].
         * @param audios the audios category that is used in the tabs and the [audios].
         * @param text the text category that is used in the tabs and the [text].
         * @param shapes the shapes category that is used in the tabs and the [shapes].
         * @param stickers the stickers category that is used in the tabs and the [stickers].
         * @param overlays the overlays category.
         * @param clips the clips category.
         */
        fun getDefault(
            tabs: List<Tab> = Tab.entries,
            images: LibraryCategory = LibraryCategory.Images,
            videos: LibraryCategory = LibraryCategory.Video,
            audios: LibraryCategory = LibraryCategory.Audio,
            text: LibraryCategory = LibraryCategory.Text,
            shapes: LibraryCategory = LibraryCategory.Shapes,
            stickers: LibraryCategory = LibraryCategory.Stickers,
            overlays: LibraryCategory = LibraryCategory.Overlays,
            clips: LibraryCategory = LibraryCategory.Clips,
        ): AssetLibrary {
            fun getElements(sceneMode: SceneMode): LibraryCategory =
                LibraryCategory.getElements(
                    sceneMode = sceneMode,
                    images = images,
                    videos = videos,
                    audios = audios,
                    text = text,
                    shapes = shapes,
                    stickers = stickers,
                )
            return AssetLibrary(
                tabs = { sceneMode ->
                    tabs.mapNotNull {
                        when (it) {
                            Tab.ELEMENTS -> getElements(sceneMode)
                            Tab.IMAGES -> images
                            Tab.VIDEOS -> if (sceneMode == SceneMode.VIDEO) videos else null
                            Tab.AUDIOS -> if (sceneMode == SceneMode.VIDEO) audios else null
                            Tab.TEXT -> text
                            Tab.SHAPES -> shapes
                            Tab.STICKERS -> stickers
                        }
                    }
                },
                elements = ::getElements,
                images = { images },
                videos = { videos },
                audios = { audios },
                text = { text },
                shapes = { shapes },
                stickers = { stickers },
                overlays = overlays,
                clips = clips,
            )
        }
    }
}
