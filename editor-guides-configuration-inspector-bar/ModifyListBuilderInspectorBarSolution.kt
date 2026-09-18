import androidx.compose.runtime.Composable
import ly.img.editor.Editor
import ly.img.editor.core.component.InspectorBar
import ly.img.editor.core.component.crop
import ly.img.editor.core.component.delete
import ly.img.editor.core.component.formatText
import ly.img.editor.core.component.layer
import ly.img.editor.core.component.modify
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberAnimations
import ly.img.editor.core.component.rememberCrop
import ly.img.editor.core.component.rememberDelete
import ly.img.editor.core.component.rememberDuplicate
import ly.img.editor.core.component.rememberFillStroke
import ly.img.editor.core.component.rememberFormatText
import ly.img.editor.core.component.rememberLayer
import ly.img.editor.core.component.rememberReplace
import ly.img.editor.core.component.rememberTextBackground
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember

// Add this composable to your NavHost
@Composable
fun ModifyListBuilderInspectorBarSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license, // pass null or empty for evaluation mode with watermark
        configuration = {
            EditorConfiguration.remember {
                inspectorBar = {
                    InspectorBar.remember {
                        listBuilder = {
                            // highlight-android-modify-list-builder
                            val existingListBuilder = InspectorBar.ListBuilder.remember {
                                add { InspectorBar.Button.rememberLayer() }
                                add { InspectorBar.Button.rememberCrop() }
                                add { InspectorBar.Button.rememberFormatText() }
                                add { InspectorBar.Button.rememberDelete() }
                            }
                            existingListBuilder.modify {
                                addFirst {
                                    InspectorBar.Button.rememberDuplicate()
                                }
                                addLast {
                                    InspectorBar.Button.rememberReplace()
                                }
                                addAfter(id = InspectorBar.Button.Id.layer, failIfNotFound = true) {
                                    InspectorBar.Button.rememberFillStroke()
                                }
                                addBefore(id = InspectorBar.Button.Id.crop, failIfNotFound = true) {
                                    InspectorBar.Button.rememberAnimations()
                                }
                                replace(id = InspectorBar.Button.Id.formatText, failIfNotFound = true) {
                                    InspectorBar.Button.rememberTextBackground()
                                }
                                remove(id = InspectorBar.Button.Id.delete, failIfNotFound = true)
                            }
                            // highlight-android-modify-list-builder
                        }
                    }
                }
            }
        },
        onClose = onClose,
    )
}
