package ly.img.editor.core.library

import androidx.annotation.StringRes
import ly.img.editor.core.R
import ly.img.editor.core.library.LibraryCategory.Companion.sourceTypes
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.library.data.UploadAssetSourceType

/**
 * Each [LibraryCategory] has a [LibraryContent] that is used to render the UI of the category. There are 2 different content types:
 * [LibraryContent.Sections] and [LibraryContent.Grid]. Sections are used to render preview sections vertically while Grid is used to render assets in a grid view.
 * Each section can be recursively expanded into another [LibraryContent.Sections] or into [LibraryContent.Grid].
 */
sealed interface LibraryContent {
    /**
     * First subtype of [LibraryContent]. It is used to vertically render preview [sections].
     *
     * @param titleRes the string resource of the content's title. It will be rendered on top of the content.
     * @param sections the list of sections that are rendered vertically.
     */
    data class Sections(
        @StringRes val titleRes: Int,
        val sections: List<Section>,
    ) : LibraryContent

    /**
     * Second subtype of [LibraryContent]. It is used to render the assets of [sourceType] as a grid.
     *
     * @param titleRes the string resource of the content's title. It will be rendered on top of the content.
     * @param sourceType the asset source whose assets should be rendered in the grid.
     * @param groups the list of groups that are used to filter the elements of the asset source. If null, no filtering will occur
     * and all the assets of the [sourceType] will be displayed. Check [ly.img.engine.AssetSource.getGroups] for more information.
     * @param perPage the number of elements that should loaded on each page.
     * @param assetType the type of assets in this source.
     */
    data class Grid(
        @StringRes val titleRes: Int,
        val sourceType: AssetSourceType,
        val groups: List<String>? = null,
        val perPage: Int = 20,
        val assetType: AssetType,
    ) : LibraryContent

    /**
     * This class configures each section in the [Sections].
     *
     * @param titleRes the string resource of the section's title. It will be rendered on top of the section. If null, the header will not
     * be rendered.
     * @param sourceTypes the list of sources that are used to load the assets. It should contain at least 1 source.
     * @param groups the list of groups that are used to filter the elements of the asset source. Note that it can be set to a
     * non-null value only in case [sourceTypes] contains a single source. If null, no filtering will occur and all the assets of
     * [sourceTypes] will be displayed. Check [ly.img.engine.AssetSource.getGroups] for more information.
     * @param addGroupedSubSections if true, the section will be constructed of vertically aligned subsections based on the
     * available groups of the source. Note that when the flag is set, then [sourceTypes] should contain a single element and
     * [groups] should be null as group filtering contradicts to displaying all grouped subsections. Also note that instead of
     * rendering a single section with subsections you may decide to add multiple sections, each containing a single group filter.
     * The result will be identical.
     * @param showUpload whether upload button should be displayed. It makes sense to display this button only if section contains
     * a single source in [sourceTypes] with type [UploadAssetSourceType]. However, you can customize and provide your own logic.
     * @param count the number of assets in the horizontal list.
     * @param assetType the type of assets in this section.
     * @param expandContent the content that should be displayed when the section is expanded. By default, it will expand to a grid
     * and will display the assets of the first source in [sourceTypes], however, you may decide to show another page of sections
     * (i.e. each item of [sourceTypes] as a separate section) before displaying the final grid. Check [LibraryCategory.getElements]
     * for an example of recursion.
     */
    data class Section(
        @StringRes val titleRes: Int? = null,
        val sourceTypes: List<AssetSourceType>,
        val groups: List<String>? = null,
        val addGroupedSubSections: Boolean = false,
        val showUpload: Boolean = sourceTypes.size == 1 && sourceTypes[0] is UploadAssetSourceType,
        val count: Int = 10,
        val assetType: AssetType,
        val expandContent: LibraryContent? =
            Grid(
                titleRes = requireNotNull(titleRes),
                sourceType = sourceTypes[0],
                groups = groups,
                assetType = assetType,
            ),
    ) {
        init {
            require(sourceTypes.isNotEmpty()) {
                "Number of sources should be at least 1."
            }
            require(groups == null || sourceTypes.size == 1) {
                "If specifying a groupIndex, number of sources should be 1."
            }
            require(groups == null || addGroupedSubSections.not()) {
                "If addGroupSubSections is true, then there should not be filtering by groups."
            }
            require(showUpload.not() || (sourceTypes.size == 1 && sourceTypes[0] is UploadAssetSourceType)) {
                "If upload button should be visible, then sources should contain only 1 UploadAssetSourceType."
            }
        }
    }

    companion object {
        /**
         * The default content for displaying video assets.
         */
        val Video by lazy {
            Sections(
                titleRes = R.string.ly_img_editor_videos,
                sections =
                    listOf(
                        Section(
                            titleRes = R.string.ly_img_editor_videos,
                            sourceTypes = listOf(AssetSourceType.Videos),
                            assetType = AssetType.Video,
                        ),
                        Section(
                            titleRes = R.string.ly_img_editor_video_uploads,
                            sourceTypes = listOf(AssetSourceType.VideoUploads),
                            assetType = AssetType.Video,
                        ),
                    ),
            )
        }

        /**
         * The default content for displaying audio assets.
         */
        val Audio by lazy {
            Sections(
                titleRes = R.string.ly_img_editor_audio,
                sections =
                    listOf(
                        Section(
                            titleRes = R.string.ly_img_editor_audio,
                            sourceTypes = listOf(AssetSourceType.Audio),
                            count = 3,
                            assetType = AssetType.Audio,
                        ),
                        Section(
                            titleRes = R.string.ly_img_editor_audio_uploads,
                            sourceTypes = listOf(AssetSourceType.AudioUploads),
                            count = 3,
                            assetType = AssetType.Audio,
                        ),
                    ),
            )
        }

        /**
         * The default content for displaying image assets.
         */
        val Images by lazy {
            Sections(
                titleRes = R.string.ly_img_editor_images,
                sections =
                    listOf(
                        Section(
                            titleRes = R.string.ly_img_editor_images,
                            sourceTypes = listOf(AssetSourceType.Images),
                            assetType = AssetType.Image,
                        ),
                        Section(
                            titleRes = R.string.ly_img_editor_gallery,
                            sourceTypes = listOf(AssetSourceType.ImageUploads),
                            assetType = AssetType.Image,
                        ),
                    ),
            )
        }

        /**
         * The default content for displaying text assets.
         */
        val Text by lazy {
            Sections(
                titleRes = R.string.ly_img_editor_text,
                sections =
                    listOf(
                        Section(
                            sourceTypes = listOf(AssetSourceType.Text),
                            count = Int.MAX_VALUE,
                            assetType = AssetType.Text,
                            expandContent = null,
                        ),
                    ),
            )
        }

        /**
         * The default content for displaying shape assets.
         */
        val Shapes by lazy {
            Sections(
                titleRes = R.string.ly_img_editor_shapes,
                sections =
                    listOf(
                        Section(
                            titleRes = R.string.ly_img_editor_shapes_basic,
                            sourceTypes = listOf(AssetSourceType.Shapes),
                            groups = listOf("//ly.img.cesdk.vectorpaths/category/vectorpaths"),
                            assetType = AssetType.Shape,
                        ),
                        Section(
                            titleRes = R.string.ly_img_editor_shapes_abstract,
                            sourceTypes = listOf(AssetSourceType.Shapes),
                            groups = listOf("//ly.img.cesdk.vectorpaths.abstract/category/abstract"),
                            assetType = AssetType.Shape,
                        ),
                    ),
            )
        }

        /**
         * The default content for displaying sticker assets.
         */
        val Stickers by lazy {
            Sections(
                titleRes = R.string.ly_img_editor_stickers,
                sections =
                    listOf(
                        Section(
                            titleRes = R.string.ly_img_editor_stickers_doodle,
                            sourceTypes = listOf(AssetSourceType.Stickers),
                            groups = listOf("//ly.img.cesdk.stickers.doodle/category/doodle"),
                            assetType = AssetType.Sticker,
                        ),
                        Section(
                            titleRes = R.string.ly_img_editor_stickers_emoji,
                            sourceTypes = listOf(AssetSourceType.Stickers),
                            groups = listOf("//ly.img.cesdk.stickers.emoji/category/emoji"),
                            assetType = AssetType.Sticker,
                        ),
                        Section(
                            titleRes = R.string.ly_img_editor_stickers_emoticons,
                            sourceTypes = listOf(AssetSourceType.Stickers),
                            groups = listOf("//ly.img.cesdk.stickers.emoticons/category/emoticons"),
                            assetType = AssetType.Sticker,
                        ),
                        Section(
                            titleRes = R.string.ly_img_editor_stickers_hands,
                            sourceTypes = listOf(AssetSourceType.Stickers),
                            groups = listOf("//ly.img.cesdk.stickers.hand/category/hand"),
                            assetType = AssetType.Sticker,
                        ),
                    ),
            )
        }

        /**
         * The default content for displaying overlay assets.
         */
        val Overlays by lazy {
            Sections(
                titleRes = R.string.ly_img_editor_overlays,
                sections =
                    listOf(
                        Section(
                            titleRes = R.string.ly_img_editor_videos,
                            sourceTypes = Video.sourceTypes,
                            assetType = AssetType.Video,
                            expandContent = Video,
                        ),
                        Section(
                            titleRes = R.string.ly_img_editor_images,
                            sourceTypes = Images.sourceTypes,
                            assetType = AssetType.Image,
                            expandContent = Images,
                        ),
                        Section(
                            titleRes = R.string.ly_img_editor_gallery,
                            sourceTypes = listOf(AssetSourceType.ImageUploads, AssetSourceType.VideoUploads),
                            assetType = AssetType.Gallery,
                            expandContent =
                                Sections(
                                    titleRes = R.string.ly_img_editor_gallery,
                                    sections =
                                        listOf(
                                            Section(
                                                titleRes = R.string.ly_img_editor_image_uploads,
                                                sourceTypes = listOf(AssetSourceType.ImageUploads),
                                                assetType = AssetType.Image,
                                            ),
                                            Section(
                                                titleRes = R.string.ly_img_editor_video_uploads,
                                                sourceTypes = listOf(AssetSourceType.VideoUploads),
                                                assetType = AssetType.Video,
                                            ),
                                        ),
                                ),
                        ),
                    ),
            )
        }

        /**
         * The default content for displaying clip assets.
         */
        val Clips by lazy {
            Overlays.copy(titleRes = R.string.ly_img_editor_clips)
        }
    }
}
