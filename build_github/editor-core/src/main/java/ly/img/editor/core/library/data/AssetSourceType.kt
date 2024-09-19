package ly.img.editor.core.library.data

/**
 * A class that wraps the asset source id and is used in [ly.img.editor.core.library.LibraryContent]. Note that you should
 * register an asset source or local asset source with the same id as [sourceId] before using the asset source:
 * [ly.img.engine.AssetApi.addSource] / [ly.img.engine.AssetApi.addLocalSource].
 */
open class AssetSourceType(val sourceId: String) {
    companion object {
        /**
         * The default source type for shapes.
         */
        val Shapes by lazy {
            AssetSourceType(sourceId = "ly.img.vectorpath")
        }

        /**
         * The default source type for stickers.
         */
        val Stickers by lazy {
            AssetSourceType(sourceId = "ly.img.sticker")
        }

        /**
         * The default source type for images.
         */
        val Images by lazy {
            AssetSourceType(sourceId = "ly.img.image")
        }

        /**
         * The default source type for videos.
         */
        val Videos by lazy {
            AssetSourceType(sourceId = "ly.img.video")
        }

        /**
         * The default source type for audios.
         */
        val Audio by lazy {
            AssetSourceType(sourceId = "ly.img.audio")
        }

        /**
         * The default source type for text.
         */
        val Text by lazy {
            AssetSourceType(sourceId = "ly.img.asset.source.text")
        }

        /**
         * The default source type for typeface.
         */
        val Typeface by lazy {
            AssetSourceType(sourceId = "ly.img.typeface")
        }

        /**
         * The default source type for image uploads.
         */
        val ImageUploads by lazy {
            UploadAssetSourceType(sourceId = "ly.img.image.upload", mimeTypeFilter = "image/*")
        }

        /**
         * The default source type for video uploads.
         */
        val VideoUploads by lazy {
            UploadAssetSourceType(sourceId = "ly.img.video.upload", mimeTypeFilter = "video/*")
        }

        /**
         * The default source type for audio uploads.
         */
        val AudioUploads by lazy {
            UploadAssetSourceType(sourceId = "ly.img.audio.upload", mimeTypeFilter = "audio/*")
        }
    }
}

/**
 * Same as [AssetSourceType] but for assets that should be uploaded from your device and that have mime type of [mimeTypeFilter].
 *
 * @param mimeTypeFilter the mime type filter that is used to filter out the device assets when system picker is displayed.
 */
class UploadAssetSourceType(sourceId: String, val mimeTypeFilter: String) : AssetSourceType(sourceId)
