package ly.img.editor.core.ui.library.resultcontract

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract

internal class CaptureContent(private val action: String) : ActivityResultContract<Uri, Uri?>() {
    override fun createIntent(
        context: Context,
        input: Uri,
    ): Intent {
        return Intent(action)
            .putExtra(MediaStore.EXTRA_OUTPUT, input)
    }

    override fun getSynchronousResult(
        context: Context,
        input: Uri,
    ): SynchronousResult<Uri?>? = null

    override fun parseResult(
        resultCode: Int,
        intent: Intent?,
    ): Uri? {
        return if (resultCode == Activity.RESULT_OK) {
            intent?.data
        } else {
            null
        }
    }
}
