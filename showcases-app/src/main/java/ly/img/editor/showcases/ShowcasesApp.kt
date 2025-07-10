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
        // Setting a penalty listener is only possible on API levels >= 28.
        // This should be enough to catch most issues anyway.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy
                    .Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyListener(ContextCompat.getMainExecutor(this)) { violation ->
                        if (isFirebaseActive) {
                            Firebase.crashlytics.recordException(violation)
                        }
                    }.build(),
            )
        }
    }
}
