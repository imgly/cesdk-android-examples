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
import ly.img.editor.core.component.InspectorBar
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.Music
import ly.img.editor.core.sheet.SheetType

// highlight-inspectorBarItems-newButton
@Composable
fun rememberInspectorBarButton(): InspectorBar.Button {
    return InspectorBar.Button.remember(
        // highlight-inspectorBarItems-newButton-id
        id = EditorComponentId("my.package.inspectorBar.button.newButton"),
        // highlight-inspectorBarItems-newButton-scope
        scope =
            LocalEditorScope.current.run {
                remember(this) { InspectorBar.ButtonScope(parentScope = this) }
            },
        // highlight-inspectorBarItems-newButton-scope
        // highlight-inspectorBarItems-newButton-visible
        visible = { true },
        // highlight-inspectorBarItems-newButton-enterTransition
        enterTransition = { EnterTransition.None },
        // highlight-inspectorBarItems-newButton-exitTransition
        exitTransition = { ExitTransition.None },
        // highlight-inspectorBarItems-newButton-decoration
        // default value is { it() }
        decoration = {
            Surface(color = MaterialTheme.colorScheme.background) {
                it()
            }
        },
        // highlight-inspectorBarItems-newButton-decoration
        // highlight-inspectorBarItems-newButton-onClick
        onClick = { editorContext.eventHandler.send(EditorEvent.Sheet.Open(SheetType.Volume())) },
        // highlight-inspectorBarItems-newButton-icon
        // default value is null
        icon = {
            Icon(
                imageVector = IconPack.Music,
                contentDescription = null,
            )
        },
        // highlight-inspectorBarItems-newButton-icon
        // highlight-inspectorBarItems-newButton-text
        // default value is null
        text = {
            Text(
                text = "Hello World",
            )
        },
        // highlight-inspectorBarItems-newButton-text
        // highlight-inspectorBarItems-newButton-enabled
        enabled = { true },
    )
}
// highlight-inspectorBarItems-newButton

// highlight-inspectorBarItems-newButton-simpleOverload
@Composable
fun rememberInspectorBarButtonSimpleOverload(): InspectorBar.Button {
    return InspectorBar.Button.remember(
        id = EditorComponentId("my.package.inspectorBar.button.newButton"),
        scope =
            LocalEditorScope.current.run {
                remember(this) { InspectorBar.ButtonScope(parentScope = this) }
            },
        onClick = { editorContext.eventHandler.send(ShowLoading) },
        visible = { true },
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        decoration = {
            Surface(color = MaterialTheme.colorScheme.background) {
                it()
            }
        },
        vectorIcon = { IconPack.Music }, // default value is null
        text = { "Hello World" }, // default value is null
        tint = null,
        enabled = { true },
    )
}
// highlight-inspectorBarItems-newButton-simpleOverload

// highlight-inspectorBarItems-newCustomItem
@Composable
fun rememberInspectorBarCustomItem(): InspectorBar.Item<InspectorBar.ItemScope> {
    return InspectorBar.Custom.remember(
        // highlight-inspectorBarItems-newCustomItem-id
        id = EditorComponentId("my.package.inspectorBar.newCustomItem"),
        // highlight-inspectorBarItems-newCustomItem-scope
        scope =
            LocalEditorScope.current.run {
                remember(this) { InspectorBar.ItemScope(parentScope = this) }
            },
        // highlight-inspectorBarItems-newCustomItem-scope
        // highlight-inspectorBarItems-newCustomItem-visible
        visible = { true },
        // highlight-inspectorBarItems-newCustomItem-enterTransition
        enterTransition = { EnterTransition.None },
        // highlight-inspectorBarItems-newCustomItem-exitTransition
        exitTransition = { ExitTransition.None },
    ) {
        // highlight-inspectorBarItems-newCustomItem-content
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
        // highlight-inspectorBarItems-newCustomItem-content
    }
}
// highlight-inspectorBarItems-newCustomItem
