package ly.img.editor.core.ui.permissions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.editor.core.ui.utils.activity

class PermissionManager(private val context: Context) {
    companion object {
        fun Context.hasCameraPermission() = hasPermission(Manifest.permission.CAMERA)

        fun Context.hasMicPermission() = hasPermission(Manifest.permission.RECORD_AUDIO)

        fun Context.hasCameraPermissionInManifest(): Boolean {
            val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            return packageInfo.requestedPermissions?.any { it == Manifest.permission.CAMERA } ?: false
        }

        private fun Context.hasPermission(permission: String): Boolean {
            return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("PermissionPrefs", Context.MODE_PRIVATE)
    }

    suspend fun checkPermission(permission: String): PermissionStatus =
        withContext(Dispatchers.IO) {
            val isGranted = context.hasPermission(permission)

            if (isGranted) {
                return@withContext PermissionStatus.GRANTED
            }

            val shouldShowRationale =
                withContext(Dispatchers.Main) {
                    shouldShowRequestPermissionRationale(checkNotNull(context.activity), permission)
                }
            val previousShouldShowRationale = sharedPreferences.getBoolean(permission, false)

            updateSharedPreferences(permission, shouldShowRationale)

            return@withContext when {
                shouldShowRationale -> PermissionStatus.DENIED_CAN_REQUEST
                !shouldShowRationale && previousShouldShowRationale -> PermissionStatus.DENIED_DONT_ASK
                else -> PermissionStatus.DENIED_FIRST_TIME
            }
        }

    fun openAppSettings() {
        val intent =
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
        context.startActivity(intent)
    }

    private suspend fun updateSharedPreferences(
        permission: String,
        shouldShowRationale: Boolean,
    ) = withContext(Dispatchers.IO) {
        val previousValue = sharedPreferences.getBoolean(permission, false)

        if (!previousValue || shouldShowRationale) {
            sharedPreferences.edit().apply {
                putBoolean(permission, shouldShowRationale)
                apply()
            }
        }
    }
}

enum class PermissionStatus {
    GRANTED,
    DENIED_CAN_REQUEST,
    DENIED_DONT_ASK,
    DENIED_FIRST_TIME,
}
