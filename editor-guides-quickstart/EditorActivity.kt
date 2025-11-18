import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import ly.img.editor.DesignEditor
import ly.img.editor.EngineConfiguration
import ly.img.editor.rememberForDesign

class EditorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val engineConfiguration = EngineConfiguration.rememberForDesign(
                // Get your license from https://img.ly/forms/free-trial
                // pass null or empty for evaluation mode with watermark
                license = "<your license here>",
                userId = "<your unique user id>", // A unique string to identify your user/session
            )

            DesignEditor(
                engineConfiguration = engineConfiguration,
                onClose = {
                    // Close the editor here
                    finish()
                },
            )
        }
    }
}
