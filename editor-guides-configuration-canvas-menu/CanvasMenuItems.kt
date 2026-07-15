import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ly.img.editor.core.component.CanvasMenu
import ly.img.editor.core.component.EditorComponent
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberDelete
import ly.img.editor.core.component.rememberDuplicate
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.Music
import ly.img.engine.DesignBlockType

// highlight-android-predefined-button
@Composable
fun rememberPredefinedCanvasMenuButton() = CanvasMenu.Button.rememberDuplicate()
// highlight-android-predefined-button

// highlight-android-customize-predefined-button
@Composable
fun rememberCustomizedDeleteButton() = CanvasMenu.Button.rememberDelete {
    textString = { "Remove" }
    contentDescription = { "Remove selected block" }
    enabled = {
        editorContext.selection.type != DesignBlockType.Text
    }
}
// highlight-android-customize-predefined-button

// highlight-android-new-button
@Composable
fun rememberReviewCanvasMenuButton() = CanvasMenu.Button.remember {
    id = { EditorComponentId("com.example.canvasMenu.button.review") }
    onClick = {
        Toast
            .makeText(editorContext.activity, "Review action", Toast.LENGTH_SHORT)
            .show()
    }
    vectorIcon = { IconPack.Music }
    textString = { "Review" }
    contentDescription = { "Review selected block" }
}
// highlight-android-new-button

// highlight-android-divider
@Composable
fun rememberCanvasMenuDivider() = CanvasMenu.Divider.remember {
    modifier = {
        remember(this) {
            Modifier
                .padding(horizontal = 8.dp)
                .size(width = 1.dp, height = 24.dp)
        }
    }
}
// highlight-android-divider

// highlight-android-custom-item
@Composable
fun rememberCanvasMenuCustomItem() = EditorComponent.remember {
    id = { EditorComponentId("com.example.canvasMenu.customItem") }
    decoration = {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .clickable {
                    Toast
                        .makeText(editorContext.activity, "Custom item clicked", Toast.LENGTH_SHORT)
                        .show()
                },
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Review",
            )
        }
    }
}
// highlight-android-custom-item
