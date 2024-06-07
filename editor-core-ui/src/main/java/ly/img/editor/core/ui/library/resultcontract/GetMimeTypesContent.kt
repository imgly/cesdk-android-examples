package ly.img.editor.core.ui.library.resultcontract

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper

internal class GetMimeTypesContent : ActivityResultContract<Array<String>, Uri?>() {
    @CallSuper
    override fun createIntent(
        context: Context,
        input: Array<String>,
    ): Intent {
        return Intent(Intent.ACTION_GET_CONTENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
            .setType("*/*")
            .putExtra(Intent.EXTRA_MIME_TYPES, input)
    }

    override fun getSynchronousResult(
        context: Context,
        input: Array<String>,
    ): SynchronousResult<Uri?>? = null

    override fun parseResult(
        resultCode: Int,
        intent: Intent?,
    ): Uri? {
        return intent.takeIf { resultCode == Activity.RESULT_OK }?.data
    }
}
