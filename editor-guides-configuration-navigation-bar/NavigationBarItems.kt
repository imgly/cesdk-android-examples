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
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.NavigationBar
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.Music
import ly.img.editor.core.sheet.SheetType

// highlight-navigationBarItems-newButton
@Composable
fun rememberNavigationBarButton() = NavigationBar.Button.remember(
    // highlight-navigationBarItems-newButton-id
    id = EditorComponentId("my.package.navigationBar.button.newButton"),
    // highlight-navigationBarItems-newButton-scope
    scope = LocalEditorScope.current.run {
        remember(this) { NavigationBar.ButtonScope(parentScope = this) }
    },
    // highlight-navigationBarItems-newButton-scope
    // highlight-navigationBarItems-newButton-visible
    visible = { true },
    // highlight-navigationBarItems-newButton-enterTransition
    enterTransition = { EnterTransition.None },
    // highlight-navigationBarItems-newButton-exitTransition
    exitTransition = { ExitTransition.None },
    // highlight-navigationBarItems-newButton-decoration
    // default value is { it() }
    decoration = {
        Surface(color = MaterialTheme.colorScheme.background) {
            it()
        }
    },
    // highlight-navigationBarItems-newButton-decoration
    // highlight-navigationBarItems-newButton-onClick
    onClick = { editorContext.eventHandler.send(EditorEvent.Sheet.Open(SheetType.Volume())) },
    // highlight-navigationBarItems-newButton-icon
    // default value is null
    icon = {
        Icon(
            imageVector = IconPack.Music,
            contentDescription = null,
        )
    },
    // highlight-navigationBarItems-newButton-icon
    // highlight-navigationBarItems-newButton-text
    // default value is null
    text = {
        Text(
            text = "Hello World",
        )
    },
    // highlight-navigationBarItems-newButton-text
    // highlight-navigationBarItems-newButton-enabled
    enabled = { true },
)
// highlight-navigationBarItems-newButton

// highlight-navigationBarItems-newButton-simpleOverload
@Composable
fun rememberNavigationBarButtonSimpleOverload() = NavigationBar.Button.remember(
    id = EditorComponentId("my.package.navigationBar.button.newButton"),
    scope = LocalEditorScope.current.run {
        remember(this) { NavigationBar.ButtonScope(parentScope = this) }
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
    contentDescription = null,
)
// highlight-navigationBarItems-newButton-simpleOverload

// highlight-navigationBarItems-newCustomItem
@Composable
fun rememberNavigationBarCustomItem() = NavigationBar.Custom.remember(
    // highlight-navigationBarItems-newCustomItem-id
    id = EditorComponentId("my.package.navigationBar.newCustomItem"),
    // highlight-navigationBarItems-newCustomItem-scope
    scope = LocalEditorScope.current.run {
        remember(this) { NavigationBar.ItemScope(parentScope = this) }
    },
    // highlight-navigationBarItems-newCustomItem-scope
    // highlight-navigationBarItems-newCustomItem-visible
    visible = { true },
    // highlight-navigationBarItems-newCustomItem-enterTransition
    enterTransition = { EnterTransition.None },
    // highlight-navigationBarItems-newCustomItem-exitTransition
    exitTransition = { ExitTransition.None },
) {
    // highlight-navigationBarItems-newCustomItem-content
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
    // highlight-navigationBarItems-newCustomItem-content
}
// highlight-navigationBarItems-newCustomItem
