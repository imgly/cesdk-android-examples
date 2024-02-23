package ly.img.editor

import android.os.Parcel
import android.os.Parcelable

/**
 * The state class of the editor that is used in the default implementation of [EditorConfiguration.getDefault].
 *
 * @param showLoading whether a loading should be displayed.
 * @param showCloseConfirmationDialog whether a confirmation dialog should be shown before closing the editor.
 * @param error the latest caught error in the editor. For instance, it can be [ly.img.engine.LicenseValidationException] if the
 * provided apiKey/license is invalid or [ly.img.engine.EngineException] for other Engine errors.
 */
data class EditorUiState(
    val showLoading: Boolean,
    val showCloseConfirmationDialog: Boolean,
    val error: Throwable?,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        showLoading = parcel.readByte() != 0.toByte(),
        showCloseConfirmationDialog = parcel.readByte() != 0.toByte(),
        error = null,
    )

    override fun writeToParcel(
        parcel: Parcel,
        flags: Int,
    ) {
        parcel.writeByte(if (showLoading) 1 else 0)
        parcel.writeByte(if (showCloseConfirmationDialog) 1 else 0)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<EditorUiState> {
        override fun createFromParcel(parcel: Parcel): EditorUiState {
            return EditorUiState(parcel)
        }

        override fun newArray(size: Int): Array<EditorUiState?> {
            return arrayOfNulls(size)
        }
    }
}
