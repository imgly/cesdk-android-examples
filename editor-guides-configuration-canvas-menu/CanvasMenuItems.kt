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
import ly.img.editor.ShowLoading
import ly.img.editor.core.LocalEditorScope
import ly.img.editor.core.component.CanvasMenu
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.Music
import ly.img.editor.core.sheet.SheetType

// highlight-canvasMenuItems-newButton
@Composable
fun rememberCanvasMenuButton(): CanvasMenu.Button = CanvasMenu.Button.remember(
    // highlight-canvasMenuItems-newButton-id
    id = EditorComponentId("my.package.canvasMenu.button.newButton"),
    // highlight-canvasMenuItems-newButton-scope
    scope = LocalEditorScope.current.run {
        remember(this) { CanvasMenu.ButtonScope(parentScope = this) }
    },
    // highlight-canvasMenuItems-newButton-scope
    // highlight-canvasMenuItems-newButton-visible
    visible = { true },
    // highlight-canvasMenuItems-newButton-enterTransition
    enterTransition = { EnterTransition.None },
    // highlight-canvasMenuItems-newButton-exitTransition
    exitTransition = { ExitTransition.None },
    // highlight-canvasMenuItems-newButton-decoration
    // default value is { it() }
    decoration = {
        Surface(color = MaterialTheme.colorScheme.background) {
            it()
        }
    },
    // highlight-canvasMenuItems-newButton-decoration
    // highlight-canvasMenuItems-newButton-onClick
    onClick = { editorContext.eventHandler.send(EditorEvent.Sheet.Open(SheetType.Volume())) },
    // highlight-canvasMenuItems-newButton-icon
    // default value is null
    icon = {
        Icon(
            imageVector = IconPack.Music,
            contentDescription = null,
        )
    },
    // highlight-canvasMenuItems-newButton-icon
    // highlight-canvasMenuItems-newButton-text
    // default value is null
    text = {
        Text(
            text = "Hello World",
        )
    },
    // highlight-canvasMenuItems-newButton-text
    // highlight-canvasMenuItems-newButton-enabled
    enabled = { true },
)
// highlight-canvasMenuItems-newButton

// highlight-canvasMenuItems-newButton-simpleOverload
@Composable
fun rememberCanvasMenuButtonSimpleOverload(): CanvasMenu.Button = CanvasMenu.Button.remember(
    id = EditorComponentId("my.package.canvasMenu.button.newButton"),
    scope = LocalEditorScope.current.run {
        remember(this) { CanvasMenu.ButtonScope(parentScope = this) }
    },
    visible = { true },
    enterTransition = { EnterTransition.None },
    exitTransition = { ExitTransition.None },
    decoration = {
        Surface(color = MaterialTheme.colorScheme.background) {
            it()
        }
    },
    onClick = { editorContext.eventHandler.send(ShowLoading) },
    vectorIcon = { IconPack.Music }, // default value is null
    text = { "Hello World" }, // default value is null
    tint = null,
    enabled = { true },
)
// highlight-canvasMenuItems-newButton-simpleOverload

// highlight-canvasMenuItems-newDivider
@Composable
fun rememberCanvasMenuDivider(): CanvasMenu.Divider = CanvasMenu.Divider.remember(
    // highlight-canvasMenuItems-newDivider-scope
    scope = LocalEditorScope.current.run {
        remember(this) { CanvasMenu.DividerScope(parentScope = this) }
    },
    // highlight-canvasMenuItems-newDivider-scope
    // highlight-canvasMenuItems-newDivider-visible
    visible = { true },
    // highlight-canvasMenuItems-newDivider-enterTransition
    enterTransition = { EnterTransition.None },
    // highlight-canvasMenuItems-newDivider-exitTransition
    exitTransition = { ExitTransition.None },
    // highlight-canvasMenuItems-newDivider-decoration
    decoration = { it() },
    // highlight-canvasMenuItems-newDivider-modifier
    modifier = {
        remember(this) {
            Modifier
                .padding(horizontal = 8.dp)
                .size(width = 1.dp, height = 24.dp)
        }
    },
    // highlight-canvasMenuItems-newDivider-modifier
)
// highlight-canvasMenuItems-newDivider

// highlight-canvasMenuItems-newCustomItem
@Composable
fun rememberCanvasMenuCustomItem(): CanvasMenu.Item<CanvasMenu.ItemScope> = CanvasMenu.Custom.remember(
    // highlight-canvasMenuItems-newCustomItem-id
    id = EditorComponentId("my.package.canvasMenu.newCustomItem"),
    // highlight-canvasMenuItems-newCustomItem-scope
    scope = LocalEditorScope.current.run {
        remember(this) { CanvasMenu.ItemScope(parentScope = this) }
    },
    // highlight-canvasMenuItems-newCustomItem-scope
    // highlight-canvasMenuItems-newCustomItem-visible
    visible = { true },
    // highlight-canvasMenuItems-newCustomItem-enterTransition
    enterTransition = { EnterTransition.None },
    // highlight-canvasMenuItems-newCustomItem-exitTransition
    exitTransition = { ExitTransition.None },
) {
    // highlight-canvasMenuItems-newCustomItem-content
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
    // highlight-canvasMenuItems-newCustomItem-content
}
// highlight-canvasMenuItems-newCustomItem
