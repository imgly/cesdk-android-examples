package ly.img.camera.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper
import androidx.core.os.ParcelCompat
import ly.img.camera.core.CaptureVideo.Input

/**
 * An [ActivityResultContract] to start the IMG.LY Camera with the [Input].
 * The output is a [CameraResult].
 */
open class CaptureVideo : ActivityResultContract<Input, CameraResult?>() {
    @CallSuper
    override fun createIntent(
        context: Context,
        input: Input,
    ): Intent {
        return Intent(context, Class.forName(ACTIVITY_CLASS_NAME)).apply {
            putExtra(INTENT_KEY_CAMERA_INPUT, input)
        }
    }

    final override fun getSynchronousResult(
        context: Context,
        input: Input,
    ): SynchronousResult<CameraResult?>? = null

    override fun parseResult(
        resultCode: Int,
        intent: Intent?,
    ): CameraResult? {
        return intent.takeIf { resultCode == Activity.RESULT_OK }?.getParcelableExtra(INTENT_KEY_CAMERA_RESULT)
    }

    companion object {
        private const val ACTIVITY_CLASS_NAME = "ly.img.camera.CameraActivity"
        const val INTENT_KEY_CAMERA_INPUT = "imgly_camera_input"
        const val INTENT_KEY_CAMERA_RESULT = "imgly_camera_result"
    }

    /**
     * Basic configuration settings to initialize the camera.
     * @param engineConfiguration configuration to initialize the underlying engine.
     * @param cameraConfiguration configuration to customise the camera experience and behaviour.
     */
    class Input(
        val engineConfiguration: EngineConfiguration,
        val cameraConfiguration: CameraConfiguration = CameraConfiguration(),
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            ParcelCompat.readParcelable(parcel, EngineConfiguration::class.java.classLoader, EngineConfiguration::class.java)!!,
            ParcelCompat.readParcelable(parcel, CameraConfiguration::class.java.classLoader, CameraConfiguration::class.java)!!,
        )

        override fun writeToParcel(
            parcel: Parcel,
            flags: Int,
        ) {
            parcel.writeParcelable(engineConfiguration, flags)
            parcel.writeParcelable(cameraConfiguration, flags)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Input> {
            override fun createFromParcel(parcel: Parcel): Input {
                return Input(parcel)
            }

            override fun newArray(size: Int): Array<Input?> {
                return arrayOfNulls(size)
            }
        }
    }
}
