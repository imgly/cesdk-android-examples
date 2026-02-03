package ly.img.editor.showcases

import android.app.Application
import android.os.Build
import android.os.StrictMode
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.crashlytics

class ShowcasesApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val isFirebaseActive = FirebaseApp.getApps(this).isNotEmpty()
        if (isFirebaseActive) {
            Firebase.crashlytics.setCustomKey("build_name", ShowcasesBuildConfig.BUILD_NAME)
        }

        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy
                .Builder()
                .detectAll()
                .penaltyLog()
                .apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && isFirebaseActive) {
                        // Setting a penalty listener is only possible on API levels >= 28.
                        // This should be enough to catch most issues anyway.
                        penaltyListener(ContextCompat.getMainExecutor(this@ShowcasesApp)) { violation ->
                            Firebase.crashlytics.recordException(violation)
                        }
                    }
                }.build(),
        )

        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectLeakedClosableObjects()
                .detectLeakedSqlLiteObjects()
                .detectActivityLeaks()
                .detectLeakedRegistrationObjects()
                .detectFileUriExposure()
                .apply {
                    detectCleartextNetwork()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        detectContentUriWithoutPermission()
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        detectCredentialProtectedWhileLocked()
                    }
                    // Note: detectUntaggedSockets() is intentionally excluded because
                    // third-party SDKs don't tag their sockets and we can't fix them.
                }
                .penaltyLog()
                .apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && isFirebaseActive) {
                        // Setting a penalty listener is only possible on API levels >= 28.
                        // This should be enough to catch most issues anyway.
                        penaltyListener(ContextCompat.getMainExecutor(this@ShowcasesApp)) { violation ->
                            Firebase.crashlytics.recordException(violation)
                        }
                    }
                }
                .build(),
        )
    }
}
