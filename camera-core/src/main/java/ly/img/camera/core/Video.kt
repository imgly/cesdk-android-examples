package ly.img.camera.core

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

/**
 * A video in a [Recording].
 *
 * @param uri the uri of the video
 */
data class Video(
    val uri: Uri,
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readParcelable(Uri::class.java.classLoader)!!)

    override fun writeToParcel(
        parcel: Parcel,
        flags: Int,
    ) {
        parcel.writeParcelable(uri, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Video> {
        override fun createFromParcel(parcel: Parcel): Video {
            return Video(parcel)
        }

        override fun newArray(size: Int): Array<Video?> {
            return arrayOfNulls(size)
        }
    }
}