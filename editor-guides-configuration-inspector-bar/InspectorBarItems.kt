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
import ly.img.editor.core.component.EditorComponent
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.InspectorBar
import ly.img.editor.core.component.InspectorBar.Scope
import ly.img.editor.core.component.remember
import ly.img.editor.core.compose.rememberLastValue
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.Music
import ly.img.editor.core.sheet.SheetType

// highlight-inspectorBarItems-newButton
@Composable
fun rememberInspectorBarButton() = InspectorBar.Button.remember {
    id = { EditorComponentId("my.package.inspectorBar.button.newButton") }
    scope = {
        val parentScope = this as Scope
        rememberLastValue(parentScope) {
            if (editorContext.safeSelection == null) lastValue else InspectorBar.ItemScope(parentScope = parentScope)
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
// highlight-inspectorBarItems-newButton

// highlight-inspectorBarItems-newButton-simple
@Composable
fun rememberInspectorBarButtonSimple() = InspectorBar.Button.remember {
    id = { EditorComponentId("my.package.inspectorBar.button.newButton") }
    scope = {
        val parentScope = this as Scope
        rememberLastValue(parentScope) {
            if (editorContext.safeSelection == null) lastValue else InspectorBar.ItemScope(parentScope = parentScope)
        }
    }
    modifier = { Modifier }
    onClick = { editorContext.eventHandler.send(EditorEvent.CloseEditor()) }
    visible = { true }
    enterTransition = { EnterTransition.None }
    exitTransition = { ExitTransition.None }
    // Default value is { it() }
    decoration = {
        Surface(color = MaterialTheme.colorScheme.background) {
            it()
        }
    }
    // Default value is null
    vectorIcon = { IconPack.Music }
    // Default value is null
    textString = { "Hello World" }
    tint = { MaterialTheme.colorScheme.onSurfaceVariant }
    enabled = { true }
    contentDescription = null
}
// highlight-inspectorBarItems-newButton-simple

// highlight-inspectorBarItems-newCustomItem
@Composable
fun rememberInspectorBarCustomItem() = EditorComponent.remember {
    id = { EditorComponentId("my.package.inspectorBar.newCustomItem") }
    scope = {
        val parentScope = this as Scope
        rememberLastValue(parentScope) {
            if (editorContext.safeSelection == null) lastValue else InspectorBar.ItemScope(parentScope = parentScope)
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
// highlight-inspectorBarItems-newCustomItem
