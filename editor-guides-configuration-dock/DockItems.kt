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
import ly.img.editor.ShowLoading
import ly.img.editor.core.LocalEditorScope
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.Music
import ly.img.editor.core.sheet.SheetType

// highlight-dockItems-newButton
@Composable
fun rememberDockButton(): Dock.Button {
    return Dock.Button.remember(
        // highlight-dockItems-newButton-id
        id = EditorComponentId("my.package.dock.button.newButton"),
        // highlight-dockItems-newButton-scope
        scope =
            LocalEditorScope.current.run {
                remember(this) { Dock.ButtonScope(parentScope = this) }
            },
        // highlight-dockItems-newButton-scope
        // highlight-dockItems-newButton-onClick
        onClick = { editorContext.eventHandler.send(EditorEvent.Sheet.Open(SheetType.Volume())) },
        // highlight-dockItems-newButton-icon
        // default value is null
        icon = {
            Icon(
                imageVector = IconPack.Music,
                contentDescription = null,
            )
        },
        // highlight-dockItems-newButton-icon
        // highlight-dockItems-newButton-text
        // default value is null
        text = {
            Text(
                text = "Hello World",
            )
        },
        // highlight-dockItems-newButton-text
        // highlight-dockItems-newButton-enabled
        enabled = { true },
        // highlight-dockItems-newButton-visible
        visible = { true },
        // highlight-dockItems-newButton-enterTransition
        enterTransition = { EnterTransition.None },
        // highlight-dockItems-newButton-exitTransition
        exitTransition = { ExitTransition.None },
        // highlight-dockItems-newButton-decoration
        // default value is { it() }
        decoration = {
            Surface(color = MaterialTheme.colorScheme.background) {
                it()
            }
        },
        // highlight-dockItems-newButton-decoration
    )
}
// highlight-dockItems-newButton

// highlight-dockItems-newButton-simpleOverload
@Composable
fun rememberDockButtonSimpleOverload(): Dock.Button {
    return Dock.Button.remember(
        id = EditorComponentId("my.package.dock.button.newButton"),
        scope =
            LocalEditorScope.current.run {
                remember(this) { Dock.ButtonScope(parentScope = this) }
            },
        onClick = { editorContext.eventHandler.send(ShowLoading) },
        vectorIcon = { IconPack.Music }, // default value is null
        text = { "Hello World" }, // default value is null
        tint = null,
        enabled = { true },
        visible = { true },
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        decoration = {
            Surface(color = MaterialTheme.colorScheme.background) {
                it()
            }
        },
    )
}
// highlight-dockItems-newButton-simpleOverload

// highlight-dockItems-newCustomItem
@Composable
fun rememberCustomItem(): Dock.Item<Dock.ItemScope> {
    return Dock.Custom.remember(
        // highlight-dockItems-newCustomItem-id
        id = EditorComponentId("my.package.dock.button.newCustomItem"),
        // highlight-dockItems-newCustomItem-scope
        scope =
            LocalEditorScope.current.run {
                remember(this) { Dock.ItemScope(parentScope = this) }
            },
        // highlight-dockItems-newCustomItem-scope
        // highlight-dockItems-newCustomItem-visible
        visible = { true },
        // highlight-dockItems-newCustomItem-enterTransition
        enterTransition = { EnterTransition.None },
        // highlight-dockItems-newCustomItem-exitTransition
        exitTransition = { ExitTransition.None },
    ) {
        // highlight-dockItems-newCustomItem-content
        Box(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .clickable {
                        Toast.makeText(editorContext.activity, "Hello World Clicked!", Toast.LENGTH_SHORT).show()
                    },
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Hello World",
            )
        }
        // highlight-dockItems-newCustomItem-content
    }
}
// highlight-dockItems-newCustomItem
