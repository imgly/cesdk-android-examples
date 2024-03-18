import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import ly.img.editor.DesignEditor
import ly.img.editor.EngineConfiguration

// Add this fragment via fragmentManager API
class EditorFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
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
                    parentFragmentManager.popBackStack()
                }
                // highlight-editor-invoke
            }
        }
    }
}
