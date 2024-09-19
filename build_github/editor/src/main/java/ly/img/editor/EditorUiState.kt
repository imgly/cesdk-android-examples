package ly.img.editor

import android.os.Parcel
import android.os.Parcelable
import java.io.File

/**
 * The state class of the editor that is used in the default implementation of [EditorConfiguration.getDefault].
 *
 * @param showLoading whether a loading should be displayed.
 * @param showCloseConfirmationDialog whether a confirmation dialog should be shown before closing the editor.
 * @param error the latest caught error in the editor. For instance, it can be [ly.img.engine.LicenseValidationException] if the
 * provided apiKey/license is invalid or [ly.img.engine.EngineException] for other Engine errors.
 * @param videoExportStatus the status of the video export.
 */
data class EditorUiState(
    val showLoading: Boolean = true,
    val showCloseConfirmationDialog: Boolean = false,
    val error: Throwable? = null,
    val videoExportStatus: VideoExportStatus = VideoExportStatus.Idle,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        showLoading = parcel.readByte() != 0.toByte(),
        showCloseConfirmationDialog = parcel.readByte() != 0.toByte(),
        error = null,
        videoExportStatus = parcel.readParcelableCompat(VideoExportStatus::class.java)!!,
    )

    override fun writeToParcel(
        parcel: Parcel,
        flags: Int,
    ) {
        parcel.writeByte(if (showLoading) 1 else 0)
        parcel.writeByte(if (showCloseConfirmationDialog) 1 else 0)
        parcel.writeParcelable(videoExportStatus, flags)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<EditorUiState> {
        override fun createFromParcel(parcel: Parcel): EditorUiState = EditorUiState(parcel)

        override fun newArray(size: Int): Array<EditorUiState?> = arrayOfNulls(size)
    }
}

/**
 * A sealed class representing the status of video export.
 */
sealed class VideoExportStatus : Parcelable {
    abstract override fun writeToParcel(
        dest: Parcel,
        flags: Int,
    )

    override fun describeContents() = 0

    companion object {
        @JvmField
        val CREATOR =
            object : Parcelable.Creator<VideoExportStatus> {
                override fun createFromParcel(source: Parcel): VideoExportStatus =
                    when (val type = source.readInt()) {
                        0 -> Loading(source.readFloat())
                        1 -> Success(source.readSerializable() as File, source.readString()!!)
                        2 -> Error
                        3 -> Idle
                        else -> throw IllegalArgumentException("Unknown type: $type")
                    }

                override fun newArray(size: Int): Array<VideoExportStatus?> = arrayOfNulls(size)
            }
    }

    data class Loading(
        val progress: Float,
    ) : VideoExportStatus() {
        override fun writeToParcel(
            dest: Parcel,
            flags: Int,
        ) {
            dest.writeInt(0)
            dest.writeFloat(progress)
        }
    }

    data class Success(
        val file: File,
        val mimeType: String,
    ) : VideoExportStatus() {
        override fun writeToParcel(
            dest: Parcel,
            flags: Int,
        ) {
            dest.writeInt(1)
            dest.writeSerializable(file)
            dest.writeString(mimeType)
        }
    }

    data object Error : VideoExportStatus() {
        override fun writeToParcel(
            dest: Parcel,
            flags: Int,
        ) {
            dest.writeInt(2)
        }
    }

    data object Idle : VideoExportStatus() {
        override fun writeToParcel(
            dest: Parcel,
            flags: Int,
        ) {
            dest.writeInt(3)
        }
    }
}

private fun <T> Parcel.readParcelableCompat(clazz: Class<T>): T? =
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        this.readParcelable(clazz.classLoader, clazz)
    } else {
        this.readParcelable(clazz.classLoader)
    }
