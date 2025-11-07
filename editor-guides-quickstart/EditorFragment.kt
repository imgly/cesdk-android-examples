import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import ly.img.editor.DesignEditor
import ly.img.editor.EngineConfiguration
import ly.img.editor.rememberForDesign

class EditorFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
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
                    parentFragmentManager.popBackStack()
                },
            )
        }
    }
}
