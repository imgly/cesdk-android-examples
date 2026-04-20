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
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.Music
import ly.img.editor.core.sheet.SheetType

// highlight-dockItems-newButton
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
    // Default value is null
    icon = {
        Icon(
            imageVector = IconPack.Music,
            contentDescription = null,
        )
    }
    // Default value is null
    text = {
        Text(
            text = "Hello World",
        )
    }
    enabled = { true }
}
// highlight-dockItems-newButton

// highlight-dockItems-newButton-simple
@Composable
fun rememberDockButtonSimple() = Dock.Button.remember {
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
    onClick = { editorContext.eventHandler.send(EditorEvent.CloseEditor()) }
    // Default value is null
    vectorIcon = { IconPack.Music }
    // Default value is null
    textString = { "Hello World" }
    tint = { MaterialTheme.colorScheme.onSurfaceVariant }
    enabled = { true }
    contentDescription = null
}
// highlight-dockItems-newButton-simple

// highlight-dockItems-newCustomItem
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
                        .makeText(editorContext.activity, "Hello World Clicked!", Toast.LENGTH_SHORT)
                        .show()
                },
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Hello World",
            )
        }
    }
}
// highlight-dockItems-newCustomItem
