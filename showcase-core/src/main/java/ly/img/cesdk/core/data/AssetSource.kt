package ly.img.cesdk.core.data

sealed class AssetSource(val sourceId: String) {
    object Shapes : AssetSource("ly.img.vectorpath")
    object Stickers : AssetSource("ly.img.sticker")

    object DuotoneFilter : AssetSource("ly.img.filter.duotone")
    object LutFilter : AssetSource("ly.img.filter.lut")
    object FxEffect : AssetSource("ly.img.effect")

    object Blur : AssetSource("ly.img.blur")
    object Images : AssetSource("ly.img.image")
    object Unsplash : AssetSource("ly.img.asset.source.unsplash")
    object Videos : AssetSource("ly.img.video")
    object Audio : AssetSource("ly.img.audio")
    object Text : AssetSource("ly.img.asset.source.text")

    object ImageUploads : UploadAssetSource("ly.img.image.upload", "image/*")
    object VideoUploads : UploadAssetSource("ly.img.video.upload", "video/*")
    object AudioUploads : UploadAssetSource("ly.img.audio.upload", "audio/*")
}

sealed class UploadAssetSource(sourceId: String, val mimeTypeFilter: String) : AssetSource(sourceId)