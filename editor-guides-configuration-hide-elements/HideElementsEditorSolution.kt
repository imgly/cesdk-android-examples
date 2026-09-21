import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import ly.img.editor.Editor
import ly.img.editor.core.component.CanvasMenu
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.InspectorBar
import ly.img.editor.core.component.NavigationBar
import ly.img.editor.core.component.closeEditor
import ly.img.editor.core.component.crop
import ly.img.editor.core.component.delete
import ly.img.editor.core.component.duplicate
import ly.img.editor.core.component.modify
import ly.img.editor.core.component.redo
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberBringForward
import ly.img.editor.core.component.rememberCloseEditor
import ly.img.editor.core.component.rememberCrop
import ly.img.editor.core.component.rememberDelete
import ly.img.editor.core.component.rememberDuplicate
import ly.img.editor.core.component.rememberExport
import ly.img.editor.core.component.rememberFormatText
import ly.img.editor.core.component.rememberLayer
import ly.img.editor.core.component.rememberRedo
import ly.img.editor.core.component.rememberSendBackward
import ly.img.editor.core.component.rememberShapesLibrary
import ly.img.editor.core.component.rememberSystemCamera
import ly.img.editor.core.component.rememberSystemGallery
import ly.img.editor.core.component.rememberTextLibrary
import ly.img.editor.core.component.rememberUndo
import ly.img.editor.core.component.shapesLibrary
import ly.img.editor.core.component.textLibrary
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember

@Composable
fun HideElementsEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license,
        configuration = {
            EditorConfiguration.remember {
                // highlight-android-minimal-ui
                dock = { rememberHiddenDock() }
                navigationBar = { rememberMinimalNavigationBar() }
                // highlight-android-minimal-ui
                canvasMenu = { rememberCanvasMenuWithoutDuplicateAndDelete() }
                inspectorBar = { rememberInspectorBarWithoutCropAndDelete() }
            }
        },
        onClose = onClose,
    )
}

// highlight-android-hide-dock
@Composable
private fun rememberHiddenDock() = Dock.remember {
    visible = { false }
}
// highlight-android-hide-dock

// highlight-android-remove-dock-items
@Composable
private fun rememberDockWithoutTextAndShapes() = Dock.remember {
    horizontalArrangement = { Arrangement.Center }
    listBuilder = {
        Dock.ListBuilder.remember {
            add { Dock.Button.rememberSystemGallery() }
            add { Dock.Button.rememberSystemCamera() }
            add { Dock.Button.rememberTextLibrary() }
            add { Dock.Button.rememberShapesLibrary() }
        }.modify {
            remove(id = Dock.Button.Id.textLibrary, failIfNotFound = true)
            remove(id = Dock.Button.Id.shapesLibrary, failIfNotFound = true)
        }
    }
}
// highlight-android-remove-dock-items

// highlight-android-remove-navigation-bar-items
@Composable
private fun rememberNavigationBarWithoutCloseAndRedo() = NavigationBar.remember {
    listBuilder = {
        NavigationBar.ListBuilder.remember {
            aligned(alignment = Alignment.Start) {
                add { NavigationBar.Button.rememberCloseEditor() }
            }
            aligned(
                alignment = Alignment.End,
                arrangement = Arrangement.spacedBy(2.dp),
            ) {
                add { NavigationBar.Button.rememberUndo() }
                add { NavigationBar.Button.rememberRedo() }
                add { NavigationBar.Button.rememberExport() }
            }
        }.modify {
            remove(id = NavigationBar.Button.Id.closeEditor, failIfNotFound = true)
            remove(id = NavigationBar.Button.Id.redo, failIfNotFound = true)
        }
    }
}
// highlight-android-remove-navigation-bar-items

// highlight-android-remove-canvas-menu-items
@Composable
private fun rememberCanvasMenuWithoutDuplicateAndDelete() = CanvasMenu.remember {
    listBuilder = {
        CanvasMenu.ListBuilder.remember {
            add { CanvasMenu.Button.rememberBringForward() }
            add { CanvasMenu.Button.rememberSendBackward() }
            add { CanvasMenu.Button.rememberDuplicate() }
            add { CanvasMenu.Button.rememberDelete() }
        }.modify {
            remove(id = CanvasMenu.Button.Id.duplicate, failIfNotFound = true)
            remove(id = CanvasMenu.Button.Id.delete, failIfNotFound = true)
        }
    }
}
// highlight-android-remove-canvas-menu-items

// highlight-android-remove-inspector-bar-items
@Composable
private fun rememberInspectorBarWithoutCropAndDelete() = InspectorBar.remember {
    listBuilder = {
        InspectorBar.ListBuilder.remember {
            add { InspectorBar.Button.rememberLayer() }
            add { InspectorBar.Button.rememberCrop() }
            add { InspectorBar.Button.rememberFormatText() }
            add { InspectorBar.Button.rememberDelete() }
        }.modify {
            remove(id = InspectorBar.Button.Id.crop, failIfNotFound = true)
            remove(id = InspectorBar.Button.Id.delete, failIfNotFound = true)
        }
    }
}
// highlight-android-remove-inspector-bar-items

// highlight-android-minimal-navigation-bar
@Composable
private fun rememberMinimalNavigationBar() = NavigationBar.remember {
    listBuilder = {
        NavigationBar.ListBuilder.remember {
            aligned(alignment = Alignment.Start) {
                add { NavigationBar.Button.rememberCloseEditor() }
            }
            aligned(
                alignment = Alignment.End,
                arrangement = Arrangement.spacedBy(2.dp),
            ) {
                add { NavigationBar.Button.rememberUndo() }
                add { NavigationBar.Button.rememberExport() }
            }
        }
    }
}
// highlight-android-minimal-navigation-bar
