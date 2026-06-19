import android.net.Uri
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FindAssetsQuery
import kotlin.math.abs

data class AddMusic(
    val pageWidth: Float,
    val pageHeight: Float,
    val pageDurationSeconds: Double,
    val musicUri: Uri,
    val timeOffsetSeconds: Double,
    val durationSeconds: Double,
    val computedMusicDurationSeconds: Double,
    val volume: Float,
    val availableAssetCount: Int,
    val secondTrackUri: Uri,
    val secondTrackTimeOffsetSeconds: Double,
    val secondTrackDurationSeconds: Double,
    val secondTrackVolume: Float,
    val audioBlockCountAfterCleanup: Int,
)

suspend fun addMusic(engine: Engine): AddMusic {
    // highlight-android-create-scene
    val scene = engine.scene.createForVideo()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setWidth(page, value = 1920F)
    engine.block.setHeight(page, value = 1080F)
    engine.block.setDuration(page, duration = 30.0)
    // highlight-android-create-scene

    // highlight-android-create-audio-block
    val music = engine.block.create(DesignBlockType.Audio)
    engine.block.appendChild(parent = page, child = music)

    val musicUri = Uri.parse("https://cdn.img.ly/assets/demo/v3/ly.img.audio/audios/far_from_home.m4a")
    engine.block.setString(block = music, property = "audio/fileURI", value = musicUri.toString())
    // highlight-android-create-audio-block

    check(engine.block.getString(block = music, property = "audio/fileURI") == musicUri.toString())

    // highlight-android-configure-timeline
    engine.block.forceLoadAVResource(block = music)
    val sourceDuration = engine.block.getAVResourceTotalDuration(block = music)
    val musicDuration = minOf(sourceDuration, 30.0)

    engine.block.setTimeOffset(block = music, offset = 0.0)
    engine.block.setDuration(block = music, duration = musicDuration)
    // highlight-android-configure-timeline

    // highlight-android-configure-volume
    engine.block.setVolume(block = music, volume = 0.8F)
    val musicVolume = engine.block.getVolume(block = music)
    // highlight-android-configure-volume

    check(abs(musicVolume - 0.8F) < 0.001F)

    // highlight-android-query-audio-assets
    val audioSourceId = "ly.img.audio"
    val demoAssetsBaseUri = Uri.parse("https://cdn.img.ly/assets/demo/v3")
    if (audioSourceId !in engine.asset.findAllSources()) {
        engine.asset.addLocalSourceFromJSON(
            contentUri = demoAssetsBaseUri.buildUpon()
                .appendPath(audioSourceId)
                .appendPath("content.json")
                .build(),
        )
    }

    val audioAssets = engine.asset.findAssets(
        sourceId = audioSourceId,
        query = FindAssetsQuery(page = 0, perPage = 10),
    )
    val secondAudioAsset = audioAssets.assets.first { it.id.endsWith("dance_harder") }
    val secondAudioUri = Uri.parse(requireNotNull(secondAudioAsset.meta?.get("uri")))
    // highlight-android-query-audio-assets

    // highlight-android-add-second-track
    val secondAudio = engine.block.create(DesignBlockType.Audio)
    engine.block.appendChild(parent = page, child = secondAudio)
    engine.block.setString(block = secondAudio, property = "audio/fileURI", value = secondAudioUri.toString())

    engine.block.forceLoadAVResource(block = secondAudio)
    val secondAudioDuration = engine.block.getAVResourceTotalDuration(block = secondAudio)

    // Start the secondary track inside the 30-second page so it is audible in this Android sample.
    engine.block.setTimeOffset(block = secondAudio, offset = 10.0)
    val secondAudioTimeOffset = engine.block.getTimeOffset(secondAudio)
    engine.block.setDuration(block = secondAudio, duration = minOf(secondAudioDuration, 15.0))
    val secondAudioPlaybackDuration = engine.block.getDuration(secondAudio)
    engine.block.setVolume(block = secondAudio, volume = 0.5F)
    val secondAudioVolume = engine.block.getVolume(secondAudio)
    // highlight-android-add-second-track

    // highlight-android-list-audio-blocks
    val audioBlocks = engine.block.findByType(DesignBlockType.Audio)
    audioBlocks.forEach { audioBlock ->
        println(
            "Audio starts at ${engine.block.getTimeOffset(audioBlock)}s " +
                "with volume ${engine.block.getVolume(audioBlock)}",
        )
    }
    // highlight-android-list-audio-blocks

    // highlight-android-remove-audio
    engine.block.destroy(secondAudio)
    val remainingAudioBlocks = engine.block.findByType(DesignBlockType.Audio)
    // highlight-android-remove-audio

    check(music in remainingAudioBlocks)
    check(secondAudio !in remainingAudioBlocks)

    return AddMusic(
        pageWidth = engine.block.getWidth(page),
        pageHeight = engine.block.getHeight(page),
        pageDurationSeconds = engine.block.getDuration(page),
        musicUri = musicUri,
        timeOffsetSeconds = engine.block.getTimeOffset(music),
        durationSeconds = engine.block.getDuration(music),
        computedMusicDurationSeconds = musicDuration,
        volume = musicVolume,
        availableAssetCount = audioAssets.total,
        secondTrackUri = secondAudioUri,
        secondTrackTimeOffsetSeconds = secondAudioTimeOffset,
        secondTrackDurationSeconds = secondAudioPlaybackDuration,
        secondTrackVolume = secondAudioVolume,
        audioBlockCountAfterCleanup = remainingAudioBlocks.size,
    )
}
