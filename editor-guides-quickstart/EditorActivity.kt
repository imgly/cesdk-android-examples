import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import ly.img.editor.DesignEditor
import ly.img.editor.EngineConfiguration

// Launch this activity via intent
class EditorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // highlight-engine-configuration
        val engineConfiguration =
            EngineConfiguration.getForDesign(
                license = "<your license here>",
                userId = "<your unique user id>",
            )
        // highlight-engine-configuration
        setContent {
            // highlight-editor-invoke
            DesignEditor(engineConfiguration = engineConfiguration) {
                // You can set result here
                finish()
            }
            // highlight-editor-invoke
        }
    }
}
