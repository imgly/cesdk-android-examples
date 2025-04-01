import androidx.compose.runtime.Composable
import ly.img.camera.core.CaptureVideo
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.rememberAdjustments
import ly.img.editor.core.component.rememberAudiosLibrary
import ly.img.editor.core.component.rememberBlur
import ly.img.editor.core.component.rememberCrop
import ly.img.editor.core.component.rememberEffect
import ly.img.editor.core.component.rememberElementsLibrary
import ly.img.editor.core.component.rememberFilter
import ly.img.editor.core.component.rememberImagesLibrary
import ly.img.editor.core.component.rememberImglyCamera
import ly.img.editor.core.component.rememberOverlaysLibrary
import ly.img.editor.core.component.rememberReorder
import ly.img.editor.core.component.rememberShapesLibrary
import ly.img.editor.core.component.rememberStickersLibrary
import ly.img.editor.core.component.rememberSystemCamera
import ly.img.editor.core.component.rememberSystemGallery
import ly.img.editor.core.component.rememberTextLibrary

// highlight-listBuilders
// highlight-listBuilders-design
@Composable
fun Dock.ListBuilder.rememberForDesign() = Dock.ListBuilder.remember {
    add { Dock.Button.rememberElementsLibrary() }
    add { Dock.Button.rememberSystemGallery() }
    add { Dock.Button.rememberSystemCamera() }
    add { Dock.Button.rememberImagesLibrary() }
    add { Dock.Button.rememberTextLibrary() }
    add { Dock.Button.rememberShapesLibrary() }
    add { Dock.Button.rememberStickersLibrary() }
}
// highlight-listBuilders-design

// highlight-listBuilders-photo
@Composable
fun Dock.ListBuilder.rememberForPhoto() = Dock.ListBuilder.remember {
    add { Dock.Button.rememberAdjustments() }
    add { Dock.Button.rememberFilter() }
    add { Dock.Button.rememberEffect() }
    add { Dock.Button.rememberBlur() }
    add { Dock.Button.rememberCrop() }
    add { Dock.Button.rememberTextLibrary() }
    add { Dock.Button.rememberShapesLibrary() }
    add { Dock.Button.rememberStickersLibrary() }
}
// highlight-listBuilders-photo

// highlight-listBuilders-video
@Composable
fun Dock.ListBuilder.rememberForVideo() = Dock.ListBuilder.remember {
    add { Dock.Button.rememberSystemGallery() }
    add {
            /*
            Make sure to add the gradle dependency of our camera library if you want to use the [rememberImglyCamera] button:
            implementation "ly.img:camera:<same version as editor>".
            If the dependency is missing, then [rememberSystemCamera] is used.
             */
        val isImglyCameraAvailable = androidx.compose.runtime.remember {
            runCatching { CaptureVideo() }.isSuccess
        }
        if (isImglyCameraAvailable) {
            Dock.Button.rememberImglyCamera()
        } else {
            Dock.Button.rememberSystemCamera()
        }
    }
    add { Dock.Button.rememberOverlaysLibrary() }
    add { Dock.Button.rememberTextLibrary() }
    add { Dock.Button.rememberStickersLibrary() }
    add { Dock.Button.rememberAudiosLibrary() }
    add { Dock.Button.rememberReorder() }
}
// highlight-listBuilders-video
// highlight-listBuilders
