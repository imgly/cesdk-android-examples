package ly.img.editor.core.library

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import ly.img.editor.core.R
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.Image
import ly.img.editor.core.iconpack.ImageOutline
import ly.img.editor.core.iconpack.LibraryElements
import ly.img.editor.core.iconpack.LibraryElementsOutline
import ly.img.editor.core.iconpack.Music
import ly.img.editor.core.iconpack.PlayBox
import ly.img.editor.core.iconpack.PlayBoxOutline
import ly.img.editor.core.iconpack.Shapes
import ly.img.editor.core.iconpack.ShapesOutline
import ly.img.editor.core.iconpack.StickerEmoji
import ly.img.editor.core.iconpack.StickerEmojiOutline
import ly.img.editor.core.iconpack.TextFields
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.engine.SceneMode

/**
 * Configuration class of the UI of each library category. Each category contains a title (check [tabTitleRes]) and a [content]
 * to render the UI. If the category is part of the tabs specified in [AssetLibrary.tabs], [tabSelectedIcon] and
 * [tabUnselectedIcon] are used to display the icon of the category in the tabs.
 *
 * @param tabTitleRes string resource of the category's title that is displayed at the very top of the UI of [content].
 * @param tabSelectedIcon the icon to display when the category is displayed as a tab element and the tab is selected.
 * @param tabSelectedIcon the icon to display when the category is displayed as a tab element and the tab is not selected.
 * @param isHalfExpandedInitially whether bottom sheet should open half expanded or fully expanded first.
 * @param content the content of the category. Check the documentation of [LibraryContent] for more information.
 */
data class LibraryCategory(
    @StringRes val tabTitleRes: Int,
    val tabSelectedIcon: ImageVector,
    val tabUnselectedIcon: ImageVector,
    @Deprecated("Parameter is unused. Consider configuring it via SheetType.LibraryAdd.mode or SheetType.LibraryReplace.mode")
    val isHalfExpandedInitially: Boolean = false,
    val content: LibraryContent,
) {
    companion object {
        /**
         * A helper function to construct an abstract "Elements" category that is a combination of categories.
         */
        fun getElements(
            sceneMode: SceneMode,
            images: LibraryCategory = Images,
            videos: LibraryCategory = Video,
            audios: LibraryCategory = Audio,
            text: LibraryCategory = Text,
            shapes: LibraryCategory = Shapes,
            stickers: LibraryCategory = Stickers,
        ): LibraryCategory {
            val isSceneModeVideo = sceneMode == SceneMode.VIDEO
            return LibraryCategory(
                tabTitleRes = R.string.ly_img_editor_elements,
                tabSelectedIcon = IconPack.LibraryElements,
                tabUnselectedIcon = IconPack.LibraryElementsOutline,
                content =
                    LibraryContent.Sections(
                        titleRes = R.string.ly_img_editor_elements,
                        sections =
                            buildList {
                                LibraryContent.Section(
                                    titleRes = R.string.ly_img_editor_gallery,
                                    sourceTypes =
                                        buildList {
                                            add(AssetSourceType.ImageUploads)
                                            if (isSceneModeVideo) {
                                                add(AssetSourceType.VideoUploads)
                                            }
                                        },
                                    showUpload = false,
                                    assetType = AssetType.Gallery,
                                    expandContent =
                                        LibraryContent.Sections(
                                            titleRes = R.string.ly_img_editor_gallery,
                                            sections =
                                                buildList {
                                                    LibraryContent.Section(
                                                        titleRes = R.string.ly_img_editor_image_uploads,
                                                        sourceTypes = listOf(AssetSourceType.ImageUploads),
                                                        assetType = AssetType.Image,
                                                    ).let(::add)
                                                    if (isSceneModeVideo) {
                                                        LibraryContent.Section(
                                                            titleRes = R.string.ly_img_editor_video_uploads,
                                                            sourceTypes = listOf(AssetSourceType.VideoUploads),
                                                            assetType = AssetType.Video,
                                                        ).let(::add)
                                                    }
                                                },
                                        ),
                                ).let(::add)

                                if (isSceneModeVideo) {
                                    videos.apply {
                                        LibraryContent.Section(
                                            titleRes = R.string.ly_img_editor_videos,
                                            sourceTypes = content.sourceTypes,
                                            assetType = AssetType.Video,
                                            expandContent = content,
                                        ).let(::add)
                                    }
                                    audios.apply {
                                        LibraryContent.Section(
                                            titleRes = R.string.ly_img_editor_audio,
                                            sourceTypes = content.sourceTypes,
                                            count = 3,
                                            assetType = AssetType.Audio,
                                            expandContent = content,
                                        ).let(::add)
                                    }
                                }

                                images.apply {
                                    LibraryContent.Section(
                                        titleRes = R.string.ly_img_editor_images,
                                        sourceTypes = content.sourceTypes,
                                        assetType = AssetType.Image,
                                        expandContent = content,
                                    ).let(::add)
                                }

                                text.apply {
                                    LibraryContent.Section(
                                        titleRes = R.string.ly_img_editor_text,
                                        sourceTypes = content.sourceTypes,
                                        assetType = AssetType.Text,
                                        expandContent = content,
                                    ).let(::add)
                                }

                                shapes.apply {
                                    LibraryContent.Section(
                                        titleRes = R.string.ly_img_editor_shapes,
                                        sourceTypes = content.sourceTypes,
                                        assetType = AssetType.Shape,
                                        expandContent = content,
                                    ).let(::add)
                                }

                                stickers.apply {
                                    LibraryContent.Section(
                                        titleRes = R.string.ly_img_editor_stickers,
                                        sourceTypes = content.sourceTypes,
                                        assetType = AssetType.Sticker,
                                        expandContent = content,
                                    ).let(::add)
                                }
                            },
                    ),
            )
        }

        /**
         * The default library category for video assets.
         */
        val Video by lazy {
            LibraryCategory(
                tabTitleRes = R.string.ly_img_editor_videos,
                tabSelectedIcon = IconPack.PlayBox,
                tabUnselectedIcon = IconPack.PlayBoxOutline,
                content = LibraryContent.Video,
            )
        }

        /**
         * The default library category for audio assets.
         */
        val Audio by lazy {
            LibraryCategory(
                tabTitleRes = R.string.ly_img_editor_audio,
                tabSelectedIcon = IconPack.Music,
                tabUnselectedIcon = IconPack.Music,
                content = LibraryContent.Audio,
            )
        }

        /**
         * The default library category for image assets.
         */
        val Images by lazy {
            LibraryCategory(
                tabTitleRes = R.string.ly_img_editor_images,
                tabSelectedIcon = IconPack.Image,
                tabUnselectedIcon = IconPack.ImageOutline,
                content = LibraryContent.Images,
            )
        }

        /**
         * The default library category for text assets.
         */
        val Text by lazy {
            LibraryCategory(
                tabTitleRes = R.string.ly_img_editor_text,
                tabSelectedIcon = IconPack.TextFields,
                tabUnselectedIcon = IconPack.TextFields,
                isHalfExpandedInitially = true,
                content = LibraryContent.Text,
            )
        }

        /**
         * The default library category for shape assets.
         */
        val Shapes by lazy {
            LibraryCategory(
                tabTitleRes = R.string.ly_img_editor_shapes,
                tabSelectedIcon = IconPack.Shapes,
                tabUnselectedIcon = IconPack.ShapesOutline,
                content = LibraryContent.Shapes,
            )
        }

        /**
         * The default library category for sticker assets.
         */
        val Stickers by lazy {
            LibraryCategory(
                tabTitleRes = R.string.ly_img_editor_stickers,
                tabSelectedIcon = IconPack.StickerEmoji,
                tabUnselectedIcon = IconPack.StickerEmojiOutline,
                content = LibraryContent.Stickers,
            )
        }

        /**
         * The default library category for overlay assets.
         */
        val Overlays by lazy {
            LibraryCategory(
                tabTitleRes = R.string.ly_img_editor_overlays,
                tabSelectedIcon = IconPack.PlayBox,
                tabUnselectedIcon = IconPack.PlayBoxOutline,
                content = LibraryContent.Overlays,
            )
        }

        /**
         * The default library category for clip assets.
         */
        val Clips by lazy {
            LibraryCategory(
                tabTitleRes = R.string.ly_img_editor_clips,
                tabSelectedIcon = IconPack.PlayBox,
                tabUnselectedIcon = IconPack.PlayBoxOutline,
                content = LibraryContent.Clips,
            )
        }

        /**
         * All the source types of the library content.
         */
        val LibraryContent.sourceTypes: List<AssetSourceType>
            get() =
                when (this) {
                    is LibraryContent.Sections ->
                        sections
                            .flatMap { it.sourceTypes }
                            .toSet()
                            .toList()
                    is LibraryContent.Grid -> listOf(sourceType)
                }
    }
}

/**
 * Add a new section to the content of the library category. Note that the function will throw an exception if the
 * content of the library category is not [LibraryContent.Sections].
 *
 * @param section the section to add at the bottom.
 */
fun LibraryCategory.addSection(section: LibraryContent.Section): LibraryCategory {
    require(content is LibraryContent.Sections) {
        "addSection can be called only for categories that have sections content."
    }
    return copy(content = content.copy(sections = content.sections + section))
}

/**
 * Drop a section from the content of the library category. Note that the function will throw an exception if the
 * content of the library category is not [LibraryContent.Sections].
 *
 * @param index the index to drop.
 */
fun LibraryCategory.dropSection(index: Int): LibraryCategory {
    require(content is LibraryContent.Sections) {
        "addSection can be called only for categories that have sections content."
    }
    return content.sections.toMutableList().run {
        removeAt(index)
        copy(content = content.copy(sections = this))
    }
}

/**
 * Replace a section in the content of the library category. Note that the function will throw an exception if the
 * content of the library category is not [LibraryContent.Sections].
 *
 * @param index the index to replace.
 * @param sectionReducer the reducer that converts the existing section at [index] into a new one.
 */
fun LibraryCategory.replaceSection(
    index: Int,
    sectionReducer: LibraryContent.Section.() -> LibraryContent.Section,
): LibraryCategory {
    require(content is LibraryContent.Sections) {
        "replaceSection can be called only for categories that have sections content."
    }
    val sections =
        content.sections.mapIndexed { internalIndex, section ->
            if (index == internalIndex) sectionReducer(section) else section
        }
    return copy(content = content.copy(sections = sections))
}
