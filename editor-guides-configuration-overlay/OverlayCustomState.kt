import android.os.Parcel
import android.os.Parcelable
import androidx.core.os.ParcelCompat
import ly.img.editor.EditorUiState

// highlight-configuration-custom-state
data class OverlayCustomState(
    // hide default loading so we can use custom loading
    val baseState: EditorUiState = EditorUiState(showLoading = false),
    val showCustomLoading: Boolean = true,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        baseState = ParcelCompat.readParcelable(parcel, EditorUiState::class.java.classLoader, EditorUiState::class.java)!!,
        showCustomLoading = parcel.readByte() != 0.toByte(),
    )

    override fun writeToParcel(
        parcel: Parcel,
        flags: Int,
    ) {
        parcel.writeParcelable(baseState, flags)
        parcel.writeByte(if (showCustomLoading) 1 else 0)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<OverlayCustomState> {
        override fun createFromParcel(parcel: Parcel): OverlayCustomState = OverlayCustomState(parcel)

        override fun newArray(size: Int): Array<OverlayCustomState?> = arrayOfNulls(size)
    }
}
// highlight-configuration-custom-state
