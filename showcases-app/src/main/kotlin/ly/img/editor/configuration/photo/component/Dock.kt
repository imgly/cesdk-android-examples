@file:Suppress("UnusedReceiverParameter")

package ly.img.editor.configuration.photo.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import ly.img.editor.configuration.photo.PhotoConfigurationBuilder
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberAdjustments
import ly.img.editor.core.component.rememberBlur
import ly.img.editor.core.component.rememberCrop
import ly.img.editor.core.component.rememberEffect
import ly.img.editor.core.component.rememberFilter
import ly.img.editor.core.component.rememberShapesLibrary
import ly.img.editor.core.component.rememberStickersLibrary
import ly.img.editor.core.component.rememberTextLibrary
import ly.img.editor.core.state.EditorViewMode

/**
 * The configuration of the component that is displayed as horizontal list of items at the bottom of the editor.
 */
@Composable
fun PhotoConfigurationBuilder.rememberDock() = Dock.remember {
    visible = {
        val state by editorContext.state.collectAsState()
        // Hide Dock in preview mode
        state.viewMode !is EditorViewMode.Preview
    }
    listBuilder = {
        Dock.ListBuilder.remember {
            add { Dock.Button.rememberAdjustments() }
            add { Dock.Button.rememberFilter() }
            add { Dock.Button.rememberEffect() }
            add { Dock.Button.rememberBlur() }
            add { Dock.Button.rememberCrop() }
            add { Dock.Button.rememberTextLibrary() }
            add { Dock.Button.rememberShapesLibrary() }
            add { Dock.Button.rememberStickersLibrary() }
        }
    }
}
