import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ly.img.editor.core.component.EditorComponent
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.InspectorBar
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberFormatText
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.iconpack.Close
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.Preview
import ly.img.editor.core.iconpack.Typeface
import ly.img.editor.core.sheet.SheetType
import ly.img.engine.DesignBlockType

// highlight-android-new-button
@Composable
fun rememberInspectorBarButton() = InspectorBar.Button.remember {
    id = { EditorComponentId("my.package.inspectorBar.button.showMessage") }
    onClick = {
        Toast
            .makeText(editorContext.activity, "Inspector action tapped", Toast.LENGTH_SHORT)
            .show()
    }
    icon = {
        Icon(
            imageVector = IconPack.Preview,
            contentDescription = "Show inspector message",
        )
    }
    text = {
        Text(
            text = "Show message",
        )
    }
}
// highlight-android-new-button

// highlight-android-simple-button
@Composable
fun rememberInspectorBarButtonSimple() = InspectorBar.Button.remember {
    id = { EditorComponentId("my.package.inspectorBar.button.closeEditor") }
    onClick = { editorContext.eventHandler.send(EditorEvent.CloseEditor()) }
    vectorIcon = { IconPack.Close }
    textString = { "Close editor" }
    contentDescription = { "Close editor" }
}
// highlight-android-simple-button

// highlight-android-customize-predefined-button
@Composable
fun rememberCustomizedFormatTextButton() = InspectorBar.Button.rememberFormatText {
    onClick = {
        editorContext.eventHandler.send(EditorEvent.Sheet.Open(SheetType.FormatText()))
    }
    vectorIcon = { IconPack.Typeface }
    textString = { "Format" }
    enabled = {
        editorContext.selection.type == DesignBlockType.Text &&
            editorContext.engine.block.isAllowedByScope(
                editorContext.selection.designBlock,
                "text/character",
            )
    }
    visible = {
        editorContext.selection.type == DesignBlockType.Text &&
            editorContext.engine.block.isAllowedByScope(
                editorContext.selection.designBlock,
                "text/character",
            )
    }
}
// highlight-android-customize-predefined-button

// highlight-android-custom-item
@Composable
fun rememberInspectorBarCustomItem() = EditorComponent.remember {
    id = { EditorComponentId("my.package.inspectorBar.newCustomItem") }
    decoration = {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .clickable {
                    Toast
                        .makeText(editorContext.activity, "Custom inspector item clicked", Toast.LENGTH_SHORT)
                        .show()
                },
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Show toast",
            )
        }
    }
}
// highlight-android-custom-item

// highlight-android-insert-custom-items
@Composable
fun rememberCustomInspectorBarList() = InspectorBar.ListBuilder.remember {
    add { rememberCustomizedFormatTextButton() }
    add { rememberInspectorBarButton() }
    add { rememberInspectorBarButtonSimple() }
    add { rememberInspectorBarCustomItem() }
}
// highlight-android-insert-custom-items
