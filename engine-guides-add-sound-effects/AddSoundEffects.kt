import android.net.Uri
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin

private const val SAMPLE_RATE = 48_000
private const val CHANNEL_COUNT = 2
private const val BITS_PER_SAMPLE = 16
private const val BYTES_PER_SAMPLE = BITS_PER_SAMPLE / 8
const val WAV_HEADER_SIZE = 44

// highlight-android-sound-definitions
private data class Note(
    val frequencyHz: Double,
    val startSeconds: Double,
    val durationSeconds: Double,
)

private data class SoundEffect(
    val notes: List<Note>,
    val totalDurationSeconds: Double,
)

private object NoteFrequencies {
    const val C4 = 261.63
    const val E4 = 329.63
    const val G4 = 392.0
    const val A4 = 440.0
    const val C5 = 523.25
    const val D5 = 587.33
    const val E5 = 659.25
    const val F5 = 698.46
    const val G5 = 783.99
    const val A5 = 880.0
}

private val successChime =
    SoundEffect(
        notes = listOf(
            Note(frequencyHz = NoteFrequencies.C4, startSeconds = 0.0, durationSeconds = 0.3),
            Note(frequencyHz = NoteFrequencies.E4, startSeconds = 0.1, durationSeconds = 0.4),
            Note(frequencyHz = NoteFrequencies.G4, startSeconds = 0.2, durationSeconds = 0.5),
            Note(frequencyHz = NoteFrequencies.C5, startSeconds = 0.35, durationSeconds = 1.65),
            Note(frequencyHz = NoteFrequencies.E5, startSeconds = 0.4, durationSeconds = 1.6),
            Note(frequencyHz = NoteFrequencies.G5, startSeconds = 0.45, durationSeconds = 1.55),
        ),
        totalDurationSeconds = 2.0,
    )

private val notificationMelody =
    SoundEffect(
        notes = listOf(
            Note(frequencyHz = NoteFrequencies.E5, startSeconds = 0.0, durationSeconds = 0.4),
            Note(frequencyHz = NoteFrequencies.G5, startSeconds = 0.25, durationSeconds = 0.5),
            Note(frequencyHz = NoteFrequencies.A5, startSeconds = 0.6, durationSeconds = 0.3),
            Note(frequencyHz = NoteFrequencies.G5, startSeconds = 0.85, durationSeconds = 0.4),
            Note(frequencyHz = NoteFrequencies.E5, startSeconds = 1.15, durationSeconds = 0.85),
        ),
        totalDurationSeconds = 2.0,
    )

private val alertTone =
    SoundEffect(
        notes = listOf(
            Note(frequencyHz = NoteFrequencies.A5, startSeconds = 0.0, durationSeconds = 0.25),
            Note(frequencyHz = NoteFrequencies.A5, startSeconds = 0.3, durationSeconds = 0.25),
            Note(frequencyHz = NoteFrequencies.F5, startSeconds = 0.6, durationSeconds = 0.4),
            Note(frequencyHz = NoteFrequencies.D5, startSeconds = 0.9, durationSeconds = 0.5),
            Note(frequencyHz = NoteFrequencies.A4, startSeconds = 1.3, durationSeconds = 0.7),
        ),
        totalDurationSeconds = 2.0,
    )
// highlight-android-sound-definitions

data class GeneratedSoundEffect(
    val name: String,
    val block: DesignBlock,
    val bufferUri: Uri,
    val bufferLength: Int,
    val startSeconds: Double,
    val durationSeconds: Double,
    val volume: Float,
)

data class SoundEffectsSummary(
    val page: DesignBlock,
    val totalDurationSeconds: Double,
    val effects: List<GeneratedSoundEffect>,
)

suspend fun addSoundEffects(engine: Engine): SoundEffectsSummary {
    val scene = engine.scene.createForVideo()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setWidth(page, value = 1920F)
    engine.block.setHeight(page, value = 1080F)

    // highlight-android-timeline-setup
    val effectDurationSeconds = 2.0
    val gapDurationSeconds = 0.5
    val totalDurationSeconds = 3 * effectDurationSeconds + 2 * gapDurationSeconds

    engine.block.setDuration(block = page, duration = totalDurationSeconds)
    // highlight-android-timeline-setup

    // highlight-android-buffer-create
    val chimeBuffer = engine.editor.createBuffer()
    // highlight-android-buffer-create

    val chimeWav =
        createWavBuffer(
            sampleRate = SAMPLE_RATE,
            durationSeconds = successChime.totalDurationSeconds,
        ) { timeSeconds ->
            generateSoundEffectSample(
                effect = successChime,
                timeSeconds = timeSeconds,
                attackSeconds = 0.02,
                decaySeconds = 0.08,
                sustainLevel = 0.7,
                releaseSeconds = 0.25,
                gain = 0.3,
                harmonic2Gain = 0.25,
                harmonic3Gain = 0.1,
            )
        }

    // highlight-android-buffer-write
    engine.editor.setBufferData(uri = chimeBuffer, offset = 0, data = chimeWav)
    // highlight-android-buffer-write

    // highlight-android-read-buffer
    val chimeBufferLength = engine.editor.getBufferLength(uri = chimeBuffer)
    val riffHeader = engine.editor.getBufferData(uri = chimeBuffer, offset = 0, length = 4)
    val riffBytes = ByteArray(size = 4)
    riffHeader.get(riffBytes)
    check(riffBytes.contentEquals("RIFF".toByteArray(Charsets.US_ASCII)))
    // highlight-android-read-buffer

    // highlight-android-audio-track
    val chimeBlock = engine.block.create(DesignBlockType.Audio)
    engine.block.setUri(
        block = chimeBlock,
        property = "audio/fileURI",
        value = chimeBuffer,
    )
    engine.block.appendChild(parent = page, child = chimeBlock)
    engine.block.forceLoadAVResource(block = chimeBlock)
    // highlight-android-audio-track

    // highlight-android-create-melody
    val melodyBuffer = engine.editor.createBuffer()
    val melodyWav =
        createWavBuffer(
            sampleRate = SAMPLE_RATE,
            durationSeconds = notificationMelody.totalDurationSeconds,
        ) { timeSeconds ->
            generateSoundEffectSample(
                effect = notificationMelody,
                timeSeconds = timeSeconds,
                attackSeconds = 0.01,
                decaySeconds = 0.06,
                sustainLevel = 0.6,
                releaseSeconds = 0.2,
                gain = 0.4,
                harmonic2Gain = 0.15,
                harmonic3Gain = 0.0,
            )
        }

    engine.editor.setBufferData(uri = melodyBuffer, offset = 0, data = melodyWav)
    val melodyBufferLength = engine.editor.getBufferLength(uri = melodyBuffer)

    val melodyBlock = engine.block.create(DesignBlockType.Audio)
    engine.block.setUri(
        block = melodyBlock,
        property = "audio/fileURI",
        value = melodyBuffer,
    )
    engine.block.appendChild(parent = page, child = melodyBlock)
    engine.block.forceLoadAVResource(block = melodyBlock)
    // highlight-android-create-melody

    val alertBuffer = engine.editor.createBuffer()
    val alertWav =
        createWavBuffer(
            sampleRate = SAMPLE_RATE,
            durationSeconds = alertTone.totalDurationSeconds,
        ) { timeSeconds ->
            generateSoundEffectSample(
                effect = alertTone,
                timeSeconds = timeSeconds,
                attackSeconds = 0.005,
                decaySeconds = 0.05,
                sustainLevel = 0.5,
                releaseSeconds = 0.15,
                gain = 0.35,
                harmonic2Gain = 0.2,
                harmonic3Gain = 0.15,
            )
        }

    engine.editor.setBufferData(uri = alertBuffer, offset = 0, data = alertWav)
    val alertBufferLength = engine.editor.getBufferLength(uri = alertBuffer)

    val alertBlock = engine.block.create(DesignBlockType.Audio)
    engine.block.setUri(
        block = alertBlock,
        property = "audio/fileURI",
        value = alertBuffer,
    )
    engine.block.appendChild(parent = page, child = alertBlock)
    engine.block.forceLoadAVResource(block = alertBlock)

    // highlight-android-position-effects
    engine.block.setTimeOffset(block = chimeBlock, offset = 0.0)
    engine.block.setDuration(block = chimeBlock, duration = successChime.totalDurationSeconds)
    engine.block.setVolume(block = chimeBlock, volume = 0.8F)

    engine.block.setTimeOffset(block = melodyBlock, offset = effectDurationSeconds + gapDurationSeconds)
    engine.block.setDuration(block = melodyBlock, duration = notificationMelody.totalDurationSeconds)
    engine.block.setVolume(block = melodyBlock, volume = 0.8F)

    engine.block.setTimeOffset(block = alertBlock, offset = 2 * (effectDurationSeconds + gapDurationSeconds))
    engine.block.setDuration(block = alertBlock, duration = alertTone.totalDurationSeconds)
    engine.block.setVolume(block = alertBlock, volume = 0.75F)
    // highlight-android-position-effects

    val effects =
        listOf(
            GeneratedSoundEffect(
                name = "Success chime",
                block = chimeBlock,
                bufferUri = chimeBuffer,
                bufferLength = chimeBufferLength,
                startSeconds = 0.0,
                durationSeconds = successChime.totalDurationSeconds,
                volume = 0.8F,
            ),
            GeneratedSoundEffect(
                name = "Notification melody",
                block = melodyBlock,
                bufferUri = melodyBuffer,
                bufferLength = melodyBufferLength,
                startSeconds = effectDurationSeconds + gapDurationSeconds,
                durationSeconds = notificationMelody.totalDurationSeconds,
                volume = 0.8F,
            ),
            GeneratedSoundEffect(
                name = "Alert tone",
                block = alertBlock,
                bufferUri = alertBuffer,
                bufferLength = alertBufferLength,
                startSeconds = 2 * (effectDurationSeconds + gapDurationSeconds),
                durationSeconds = alertTone.totalDurationSeconds,
                volume = 0.75F,
            ),
        )

    return SoundEffectsSummary(
        page = page,
        totalDurationSeconds = totalDurationSeconds,
        effects = effects,
    )
}

// highlight-android-wav-data
private fun createWavBuffer(
    sampleRate: Int,
    durationSeconds: Double,
    generator: (timeSeconds: Double) -> Double,
): ByteBuffer {
    val sampleCount = (durationSeconds * sampleRate).roundToInt()
    val dataSize = sampleCount * CHANNEL_COUNT * BYTES_PER_SAMPLE
    val wavFileSize = WAV_HEADER_SIZE + dataSize
    val wavData = ByteBuffer.allocateDirect(wavFileSize).order(ByteOrder.LITTLE_ENDIAN)

    wavData.put("RIFF".toByteArray(Charsets.US_ASCII))
    wavData.putInt(wavFileSize - 8)
    wavData.put("WAVE".toByteArray(Charsets.US_ASCII))
    wavData.put("fmt ".toByteArray(Charsets.US_ASCII))
    wavData.putInt(16)
    wavData.putShort(1.toShort())
    wavData.putShort(CHANNEL_COUNT.toShort())
    wavData.putInt(sampleRate)
    wavData.putInt(sampleRate * CHANNEL_COUNT * BYTES_PER_SAMPLE)
    wavData.putShort((CHANNEL_COUNT * BYTES_PER_SAMPLE).toShort())
    wavData.putShort(BITS_PER_SAMPLE.toShort())
    wavData.put("data".toByteArray(Charsets.US_ASCII))
    wavData.putInt(dataSize)

    for (sampleIndex in 0 until sampleCount) {
        val timeSeconds = sampleIndex / sampleRate.toDouble()
        val clampedValue = generator(timeSeconds).coerceIn(-1.0, 1.0)
        val pcmScale = if (clampedValue < 0.0) 32768.0 else 32767.0
        val pcmSample = (clampedValue * pcmScale).roundToInt().toShort()
        wavData.putShort(pcmSample)
        wavData.putShort(pcmSample)
    }

    wavData.flip()
    return wavData
}
// highlight-android-wav-data

// highlight-android-envelope
private fun adsr(
    timeSeconds: Double,
    noteStartSeconds: Double,
    noteDurationSeconds: Double,
    attackSeconds: Double,
    decaySeconds: Double,
    sustainLevel: Double,
    releaseSeconds: Double,
): Double {
    val noteTime = timeSeconds - noteStartSeconds
    if (noteTime < 0.0) return 0.0

    val releaseStartSeconds = noteDurationSeconds - releaseSeconds
    return when {
        noteTime < attackSeconds -> noteTime / attackSeconds
        noteTime < attackSeconds + decaySeconds ->
            1.0 - ((noteTime - attackSeconds) / decaySeconds) * (1.0 - sustainLevel)
        noteTime < releaseStartSeconds -> sustainLevel
        noteTime < noteDurationSeconds -> sustainLevel * (1.0 - (noteTime - releaseStartSeconds) / releaseSeconds)
        else -> 0.0
    }
}
// highlight-android-envelope

// highlight-android-sample-generator
private fun generateSoundEffectSample(
    effect: SoundEffect,
    timeSeconds: Double,
    attackSeconds: Double,
    decaySeconds: Double,
    sustainLevel: Double,
    releaseSeconds: Double,
    gain: Double,
    harmonic2Gain: Double,
    harmonic3Gain: Double,
): Double {
    var sample = 0.0
    for (note in effect.notes) {
        val envelope =
            adsr(
                timeSeconds = timeSeconds,
                noteStartSeconds = note.startSeconds,
                noteDurationSeconds = note.durationSeconds,
                attackSeconds = attackSeconds,
                decaySeconds = decaySeconds,
                sustainLevel = sustainLevel,
                releaseSeconds = releaseSeconds,
            )

        if (envelope > 0.0) {
            val fundamental = sin(2 * PI * note.frequencyHz * timeSeconds)
            val secondHarmonic = sin(4 * PI * note.frequencyHz * timeSeconds) * harmonic2Gain
            val thirdHarmonic = sin(6 * PI * note.frequencyHz * timeSeconds) * harmonic3Gain
            sample += (fundamental + secondHarmonic + thirdHarmonic) * envelope * gain
        }
    }
    return sample
}
// highlight-android-sample-generator

// highlight-android-cleanup
fun destroyGeneratedSoundEffectBuffers(
    engine: Engine,
    effects: List<GeneratedSoundEffect>,
) {
    effects.forEach { effect ->
        engine.editor.destroyBuffer(uri = effect.bufferUri)
    }
}
// highlight-android-cleanup
