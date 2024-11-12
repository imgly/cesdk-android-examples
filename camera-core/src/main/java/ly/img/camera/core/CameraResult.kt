package ly.img.camera.core

import android.os.Parcel
import android.os.Parcelable

/**
 * Wraps the result of the camera.
 */
sealed interface CameraResult : Parcelable {
    /**
     * Result representing the recordings done by the user using the standard camera mode.
     */
    data class Record(val recordings: List<Recording>) : CameraResult {
        constructor(parcel: Parcel) : this(
            recordings = parcel.createTypedArrayList(Recording)!!,
        )

        override fun writeToParcel(
            parcel: Parcel,
            flags: Int,
        ) {
            parcel.writeTypedList(recordings)
        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<Record> {
            override fun createFromParcel(parcel: Parcel): Record {
                return Record(parcel)
            }

            override fun newArray(size: Int): Array<Record?> {
                return arrayOfNulls(size)
            }
        }
    }
}
