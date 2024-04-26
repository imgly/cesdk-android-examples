package ly.img.editor.core.ui.utils

import android.net.Uri
import androidx.core.net.toFile
import java.io.File

private const val FILE_SCHEME = "file://"
private const val ANDROID_ASSET_SCHEME = "file:///android_asset/"

/**
 * Return the path of the android asset if the uri is of an android asset.
 */
fun Uri.toAssetPathOrNull(): String? {
    val path = toString()
    if (path.startsWith(ANDROID_ASSET_SCHEME)) {
        return path.removePrefix(ANDROID_ASSET_SCHEME)
    }
    return null
}

/**
 * Return the path of the file if the uri is of a file.
 */
fun Uri.toFileOrNull(): File? {
    val path = toString()
    if (path.startsWith(FILE_SCHEME)) {
        return toFile()
    }
    return null
}
