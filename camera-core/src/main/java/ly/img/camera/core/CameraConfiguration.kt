package ly.img.camera.core

import android.os.Parcel
import android.os.Parcelable
import android.util.SizeF
import androidx.compose.ui.graphics.Color
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Configuration for the camera.
 *
 * @param recordingColor The color of the record button while recording, and all the other recording indicators.
 * @param maxTotalDuration The target duration for the recording.
 * @param allowExceedingMaxDuration Adjusts the segments visualization to use the max duration, but does not enforce the limit.
 */
class CameraConfiguration(
    val recordingColor: Color = Color(0xFFDE6F62),
    val maxTotalDuration: Duration = Duration.INFINITE,
    val allowExceedingMaxDuration: Boolean = false,
) : Parcelable {
    /**
     * Dimensions of the recorded video(s) / camera preview.
     */
    val videoSize = SizeF(1080f, 1920f)

    constructor(parcel: Parcel) : this(
        recordingColor = Color(parcel.readLong()),
        maxTotalDuration = parcel.readLong().milliseconds,
        allowExceedingMaxDuration = parcel.readByte() != 0.toByte(),
    )

    override fun writeToParcel(
        parcel: Parcel,
        flags: Int,
    ) {
        // Look at Color(color: Long) implementation to understand why we need to shift the color
        parcel.writeLong((recordingColor.value shr 32).toLong())
        parcel.writeLong(maxTotalDuration.inWholeMilliseconds)
        parcel.writeByte(if (allowExceedingMaxDuration) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CameraConfiguration> {
        override fun createFromParcel(parcel: Parcel): CameraConfiguration {
            return CameraConfiguration(parcel)
        }

        override fun newArray(size: Int): Array<CameraConfiguration?> {
            return arrayOfNulls(size)
        }
    }
}
