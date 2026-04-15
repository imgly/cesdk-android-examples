@file:Suppress("UnusedReceiverParameter", "UnusedFlow")
@file:OptIn(ExperimentalCoroutinesApi::class)

package ly.img.editor.configuration.video.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ly.img.editor.configuration.video.VideoConfigurationBuilder
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.EditorTrigger
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberAudiosLibrary
import ly.img.editor.core.component.rememberOverlaysLibrary
import ly.img.editor.core.component.rememberReorder
import ly.img.editor.core.component.rememberResizeAll
import ly.img.editor.core.component.rememberStickersAndShapesLibrary
import ly.img.editor.core.component.rememberSystemCamera
import ly.img.editor.core.component.rememberSystemGallery
import ly.img.editor.core.component.rememberTextLibrary
import ly.img.editor.core.component.rememberVoiceoverRecord
import ly.img.editor.core.iconpack.AddCameraBackground
import ly.img.editor.core.iconpack.AddGalleryBackground
import ly.img.editor.core.iconpack.IconPack

/**
 * The configuration of the component that is displayed as horizontal list of items at the bottom of the editor.
 */
@Composable
fun VideoConfigurationBuilder.rememberDock() = Dock.remember {
    scope = {
        val activeSceneTrigger by EditorTrigger.remember {
            editorContext.engine.scene.onActiveChanged()
        }
        // Update Dock whenever the active scene changes.
        remember(this, activeSceneTrigger) {
            Dock.Scope(parentScope = this)
        }
    }
    horizontalArrangement = {
        // All buttons should have equal size
        Arrangement.SpaceEvenly
    }
    listBuilder = {
        // highlight-starter-kit-imgly-camera
        Dock.ListBuilder.remember {
            add {
                Dock.Button.rememberSystemGallery {
                    vectorIcon = { IconPack.AddGalleryBackground }
                }
            }
            add {
                // As an alternative to the system camera we also provide our own camera tech accessible via [Dock.Button.rememberImglyCamera].
                // In order to make it work the following dependency is required:
                // implementation "ly.img:camera:<same version as editor>".
                Dock.Button.rememberSystemCamera(captureVideo = { true }) {
                    vectorIcon = { IconPack.AddCameraBackground }
                }
            }
            add { Dock.Button.rememberOverlaysLibrary() }
            add { Dock.Button.rememberTextLibrary() }
            add { Dock.Button.rememberStickersAndShapesLibrary() }
            add { Dock.Button.rememberAudiosLibrary() }
            add { Dock.Button.rememberVoiceoverRecord() }
            add { Dock.Button.rememberResizeAll() }
            add { Dock.Button.rememberReorder() }
        }
        // highlight-starter-kit-imgly-camera
    }
}
