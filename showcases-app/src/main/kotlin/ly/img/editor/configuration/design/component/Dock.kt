@file:Suppress("UnusedReceiverParameter")

package ly.img.editor.configuration.design.component

import androidx.compose.runtime.Composable
import ly.img.editor.configuration.design.DesignConfigurationBuilder
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberElementsLibrary
import ly.img.editor.core.component.rememberImagesLibrary
import ly.img.editor.core.component.rememberResizeAll
import ly.img.editor.core.component.rememberShapesLibrary
import ly.img.editor.core.component.rememberStickersLibrary
import ly.img.editor.core.component.rememberSystemCamera
import ly.img.editor.core.component.rememberSystemGallery
import ly.img.editor.core.component.rememberTextLibrary

/**
 * The configuration of the component that is displayed as horizontal list of items at the bottom of the editor.
 */
@Composable
fun DesignConfigurationBuilder.rememberDock() = Dock.remember {
    listBuilder = {
        Dock.ListBuilder.remember {
            add { Dock.Button.rememberElementsLibrary() }
            add { Dock.Button.rememberSystemGallery() }
            add { Dock.Button.rememberSystemCamera() }
            add { Dock.Button.rememberImagesLibrary() }
            add { Dock.Button.rememberTextLibrary() }
            add { Dock.Button.rememberShapesLibrary() }
            add { Dock.Button.rememberStickersLibrary() }
            add { Dock.Button.rememberResizeAll() }
        }
    }
}
