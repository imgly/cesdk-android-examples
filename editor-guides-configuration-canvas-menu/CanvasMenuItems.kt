import android.widget.Toast
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ly.img.editor.core.component.CanvasMenu
import ly.img.editor.core.component.EditorComponent
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.InspectorBar.Scope
import ly.img.editor.core.component.remember
import ly.img.editor.core.compose.rememberLastValue
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.Music
import ly.img.editor.core.sheet.SheetType

// highlight-canvasMenuItems-newButton
@Composable
fun rememberCanvasMenuButton() = CanvasMenu.Button.remember {
    id = { EditorComponentId("my.package.canvasMenu.button.newButton") }
    scope = {
        val parentScope = this as Scope
        rememberLastValue(parentScope) {
            if (editorContext.safeSelection == null) lastValue else CanvasMenu.ItemScope(parentScope = parentScope)
        }
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
// highlight-canvasMenuItems-newButton

// highlight-canvasMenuItems-newButton-simple
@Composable
fun rememberCanvasMenuButtonSimple() = CanvasMenu.Button.remember {
    id = { EditorComponentId("my.package.canvasMenu.button.newButton") }
    scope = {
        val parentScope = this as Scope
        rememberLastValue(parentScope) {
            if (editorContext.safeSelection == null) lastValue else CanvasMenu.ItemScope(parentScope = parentScope)
        }
    }
    modifier = { Modifier }
    visible = { true }
    enterTransition = { EnterTransition.None }
    exitTransition = { ExitTransition.None }
    // Default value is it
    decoration = {
        Surface(color = MaterialTheme.colorScheme.background) {
            it()
        }
    }
    onClick = { editorContext.eventHandler.send(ShowLoading) }
    // Default value is null
    vectorIcon = { IconPack.Music }
    // Default value is null
    text = { "Hello World" }
    tint = { MaterialTheme.colorScheme.onSurfaceVariant }
    enabled = { true }
    contentDescription = null
}
// highlight-canvasMenuItems-newButton-simple

// highlight-canvasMenuItems-newDivider
@Composable
fun rememberCanvasMenuDivider() = CanvasMenu.Divider.remember {
    scope = {
        val parentScope = this as Scope
        rememberLastValue(parentScope) {
            if (editorContext.safeSelection == null) lastValue else CanvasMenu.ItemScope(parentScope = parentScope)
        }
    }
    modifier = { Modifier }
    visible = { true }
    enterTransition = { EnterTransition.None }
    exitTransition = { ExitTransition.None }
    decoration = { it() }
    modifier = {
        remember(this) {
            Modifier
                .padding(horizontal = 8.dp)
                .size(width = 1.dp, height = 24.dp)
        }
    }
}
// highlight-canvasMenuItems-newDivider

// highlight-canvasMenuItems-newCustomItem
@Composable
fun rememberCanvasMenuCustomItem() = EditorComponent.remember {
    id = { EditorComponentId("my.package.canvasMenu.newCustomItem") }
    scope = {
        val parentScope = this as Scope
        rememberLastValue(parentScope) {
            if (editorContext.safeSelection == null) lastValue else CanvasMenu.ItemScope(parentScope = parentScope)
        }
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
// highlight-canvasMenuItems-newCustomItem
