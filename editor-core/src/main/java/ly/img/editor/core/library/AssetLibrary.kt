package ly.img.editor.core.library

import ly.img.engine.SceneMode

/**
 * Configuration class for the asset library.
 *
 * @param tabs a provider for the list of categories that are displayed as tabs in the asset library.
 * @param imagesTab a provider for the category that is displayed when replacing an image.
 * @param stickersTab a provider for the category that is displayed when replacing a sticker.
 */
data class AssetLibrary(
    val tabs: (SceneMode) -> List<LibraryCategory>,
    val imagesTab: (SceneMode) -> LibraryCategory = { LibraryCategory.Images },
    val stickersTab: (SceneMode) -> LibraryCategory = { LibraryCategory.Stickers },
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
         * @param images the images category that is used in the tabs and the [imagesTab].
         * @param videos the videos category that is used in the tabs.
         * @param audios the audios category that is used in the tabs.
         * @param text the text category that is used in the tabs.
         * @param shapes the shapes category that is used in the tabs.
         * @param stickers the stickers category that is used in the tabs and the [stickersTab].
         */
        fun getDefault(
            tabs: List<Tab> = Tab.entries,
            images: LibraryCategory = LibraryCategory.Images,
            videos: LibraryCategory = LibraryCategory.Video,
            audios: LibraryCategory = LibraryCategory.Audio,
            text: LibraryCategory = LibraryCategory.Text,
            shapes: LibraryCategory = LibraryCategory.Shapes,
            stickers: LibraryCategory = LibraryCategory.Stickers,
        ) = AssetLibrary(
            tabs = { sceneMode ->
                tabs.mapNotNull {
                    when (it) {
                        Tab.ELEMENTS ->
                            LibraryCategory.getElements(
                                sceneMode = sceneMode,
                                images = images,
                                videos = videos,
                                audios = audios,
                                text = text,
                                shapes = shapes,
                                stickers = stickers,
                            )
                        Tab.IMAGES -> images
                        Tab.VIDEOS -> if (sceneMode == SceneMode.VIDEO) videos else null
                        Tab.AUDIOS -> if (sceneMode == SceneMode.VIDEO) audios else null
                        Tab.TEXT -> text
                        Tab.SHAPES -> shapes
                        Tab.STICKERS -> stickers
                    }
                }
            },
            imagesTab = { images },
            stickersTab = { stickers },
        )
    }
}
