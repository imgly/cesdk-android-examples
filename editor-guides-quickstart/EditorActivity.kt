import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

// Launch this activity via intent
class EditorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EditorComposable { throwable ->
                // You can set result here
                finish()
            }
        }
    }
}
