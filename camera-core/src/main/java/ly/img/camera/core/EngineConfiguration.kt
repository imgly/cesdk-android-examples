package ly.img.camera.core

import android.os.Parcel
import android.os.Parcelable

/**
 * Basic configuration settings to initialize the engine.
 * @param license the license to activate the [ly.img.engine.Engine] with.
 * @param userId an optional unique ID tied to your application's user. This helps us accurately calculate monthly active users (MAU).
 * Especially useful when one person uses the app on multiple devices with a sign-in feature, ensuring they're counted once.
 * Providing this aids in better data accuracy.
 */
data class EngineConfiguration(
    val license: String,
    val userId: String? = null,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString(),
    )

    override fun writeToParcel(
        parcel: Parcel,
        flags: Int,
    ) {
        parcel.writeString(license)
        parcel.writeString(userId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EngineConfiguration> {
        override fun createFromParcel(parcel: Parcel): EngineConfiguration {
            return EngineConfiguration(parcel)
        }

        override fun newArray(size: Int): Array<EngineConfiguration?> {
            return arrayOfNulls(size)
        }
    }
}
