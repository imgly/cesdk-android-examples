package ly.img.camera.core

import android.os.Parcel
import android.os.Parcelable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * A camera recording.
 *
 * @param videos contains one or two [Video]s, for single camera output or dual camera output respectively
 * @param duration the duration of the recording
 */
data class Recording(
    val videos: List<Video>,
    val duration: Duration,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        videos = parcel.createTypedArrayList(Video)!!,
        duration = parcel.readLong().milliseconds,
    )

    override fun writeToParcel(
        parcel: Parcel,
        flags: Int,
    ) {
        parcel.writeTypedList(videos)
        parcel.writeLong(duration.inWholeMilliseconds)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Recording> {
        override fun createFromParcel(parcel: Parcel): Recording {
            return Recording(parcel)
        }

        override fun newArray(size: Int): Array<Recording?> {
            return arrayOfNulls(size)
        }
    }
}
