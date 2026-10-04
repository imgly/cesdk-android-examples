// highlight-starter-kit-activity-full
package ly.img.editor.configuration.apparel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ly.img.editor.Editor
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember

/**
 * Encapsulated editor to be used in legacy activity navigation.
 * Delete this file if you are using jetpack compose navigation.
 */
class EditorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This is required to remove the default action bar on top.
        setTheme(android.R.style.Theme_Material_Light_NoActionBar)
        // This is required, so that the editor is displayed full screen on relatively older devices.
        enableEdgeToEdge()
        setContent {
            Editor(
                license = null, // pass null or empty for evaluation mode with watermark
                configuration = {
                    EditorConfiguration.remember(::ApparelConfigurationBuilder)
                },
                onClose = {
                    // Finish the activity, potentially handle errors.
                    finish()
                },
            )
        }
    }
}
// highlight-starter-kit-activity-full
