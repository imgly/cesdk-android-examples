package ly.img.editor.core.library.data

sealed class AssetSourceType(val sourceId: String) {
    data object Shapes : AssetSourceType(sourceId = "ly.img.vectorpath")
    data object Stickers : AssetSourceType(sourceId = "ly.img.sticker")

    data object DuoToneFilter : AssetSourceType(sourceId = "ly.img.filter.duotone")
    data object LutFilter : AssetSourceType(sourceId = "ly.img.filter.lut")
    data object FxEffect : AssetSourceType(sourceId = "ly.img.effect")

    data object Blur : AssetSourceType(sourceId = "ly.img.blur")
    data object Images : AssetSourceType(sourceId = "ly.img.image")
    data object Unsplash : AssetSourceType(sourceId = "ly.img.asset.source.unsplash")
    data object Videos : AssetSourceType(sourceId = "ly.img.video")
    data object Audio : AssetSourceType(sourceId = "ly.img.audio")
    data object Text : AssetSourceType(sourceId = "ly.img.asset.source.text")

    data object ImageUploads : UploadAssetSourceType(sourceId = "ly.img.image.upload", mimeTypeFilter = "image/*")
    data object VideoUploads : UploadAssetSourceType(sourceId = "ly.img.video.upload", mimeTypeFilter = "video/*")
    data object AudioUploads : UploadAssetSourceType(sourceId = "ly.img.audio.upload", mimeTypeFilter = "audio/*")
}

sealed class UploadAssetSourceType(sourceId: String, val mimeTypeFilter: String) : AssetSourceType(sourceId)
