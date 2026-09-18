import android.widget.Toast
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.EditorComponent
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberImagesLibrary
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.iconpack.AddImageForeground
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.Music
import ly.img.editor.core.sheet.SheetType

// highlight-android-predefined-button
@Composable
fun rememberPredefinedDockButton() = Dock.Button.rememberImagesLibrary()
// highlight-android-predefined-button

// highlight-android-customize-button
@Composable
fun rememberCustomizedDockButton() = Dock.Button.rememberImagesLibrary {
    textString = { "Brand Images" }
    vectorIcon = { IconPack.AddImageForeground }
    contentDescription = { "Open brand images" }
    enabled = { true }
    visible = { true }
}
// highlight-android-customize-button

// highlight-android-new-button
@Composable
fun rememberDockButton() = Dock.Button.remember {
    id = { EditorComponentId("my.package.dock.button.newButton") }
    scope = {
        remember(this) { Dock.ItemScope(parentScope = this) }
    }
    modifier = { Modifier }
    visible = { true }
    enterTransition = { EnterTransition.None }
    exitTransition = { ExitTransition.None }
    // Default value is { it() }
    decoration = {
        Surface(color = MaterialTheme.colorScheme.background) {
            it()
        }
    }
    onClick = { editorContext.eventHandler.send(EditorEvent.Sheet.Open(SheetType.Volume())) }
    icon = {
        Icon(
            imageVector = IconPack.Music,
            contentDescription = null,
        )
    }
    text = {
        Text(
            text = "Volume",
        )
    }
    enabled = { true }
}
// highlight-android-new-button

// highlight-android-custom-item
@Composable
fun rememberCustomItem() = EditorComponent.remember {
    id = { EditorComponentId("my.package.dock.newCustomItem") }
    scope = {
        remember(this) { Dock.ItemScope(parentScope = this) }
    }
    modifier = { Modifier }
    visible = { true }
    enterTransition = { EnterTransition.None }
    exitTransition = { ExitTransition.None }
    decoration = {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .clickable {
                    Toast
                        .makeText(editorContext.activity, "Dock action clicked", Toast.LENGTH_SHORT)
                        .show()
                },
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Custom",
            )
        }
    }
}
// highlight-android-custom-item
