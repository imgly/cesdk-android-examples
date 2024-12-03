import android.os.Parcel
import android.os.Parcelable
import ly.img.editor.EditorUiState

// highlight-configuration-custom-state
data class OverlayCustomState(
    // hide default loading so we can use custom loading
    val baseState: EditorUiState = EditorUiState(showLoading = false),
    val showCustomLoading: Boolean = true,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        baseState = parcel.readParcelable(EditorUiState::class.java.classLoader)!!,
        showCustomLoading = parcel.readByte() != 0.toByte(),
    ) {
    }

    override fun writeToParcel(
        parcel: Parcel,
        flags: Int,
    ) {
        parcel.writeParcelable(baseState, flags)
        parcel.writeByte(if (showCustomLoading) 1 else 0)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<OverlayCustomState> {
        override fun createFromParcel(parcel: Parcel): OverlayCustomState {
            return OverlayCustomState(parcel)
        }

        override fun newArray(size: Int): Array<OverlayCustomState?> {
            return arrayOfNulls(size)
        }
    }
}
// highlight-configuration-custom-state
