package ly.img.editor.showcases.plugin.imagegen.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ly.img.editor.showcases.plugin.imagegen.OutputType
import ly.img.editor.showcases.plugin.imagegen.api.FalImageStyle
import ly.img.editor.showcases.ui.preview.PreviewTheme

@Composable
fun FullScreenStyleDialog(
    selectedStyle: FalImageStyle,
    selectedOutput: OutputType,
    onStyleSelected: (FalImageStyle) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            StyleSelectionView(
                selectedStyle = selectedStyle,
                selectedOutput = selectedOutput,
                onStyleSelected = onStyleSelected,
                onBack = onDismiss,
                onCloseSheet = onDismiss,
            )
        }
    }
}

@Composable
@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun FullScreenStyleDialogPreview() {
    PreviewTheme {
        Surface {
            FullScreenStyleDialog(
                selectedStyle = FalImageStyle.RealisticImage,
                selectedOutput = OutputType.IMAGE,
                onStyleSelected = {},
                onDismiss = {},
            )
        }
    }
}
