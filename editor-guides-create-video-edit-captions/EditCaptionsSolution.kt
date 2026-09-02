import androidx.compose.runtime.Composable
import ly.img.editor.Editor
import ly.img.editor.configuration.video.VideoConfigurationBuilder
import ly.img.editor.configuration.video.callback.onCreate
import ly.img.editor.configuration.video.callback.onLoadAssetSources
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.InspectorBar
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberAudiosLibrary
import ly.img.editor.core.component.rememberCaptionStyle
import ly.img.editor.core.component.rememberCaptions
import ly.img.editor.core.component.rememberDelete
import ly.img.editor.core.component.rememberEditCaptions
import ly.img.editor.core.component.rememberFormatText
import ly.img.editor.core.component.rememberSplit
import ly.img.editor.core.component.rememberSystemGallery
import ly.img.editor.core.component.rememberTextLibrary
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember

// Add this composable to your NavHost.
@Composable
fun EditCaptionsSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    // The Video Editor Starter Kit registers the caption preset source, the Captions dock button and both
    // caption inspector buttons, so captions are available without any configuration of your own.
    Editor(
        license = license, // pass null or empty for evaluation mode with watermark
        configuration = {
            EditorConfiguration.remember(::VideoConfigurationBuilder) {
                onCreate = {
                    // Demo scaffolding — not part of the lesson: open a sample clip so the canvas shows
                    // footage behind the captions sheet.
                    onCreate(
                        createScene = {
                            editorContext.engine.scene.createFromVideo(
                                videoUri = editorContext.baseUri
                                    .buildUpon()
                                    .appendPath("ly.img.video")
                                    .appendPath("videos")
                                    .appendPath("pexels-kampus-production-8154913.mp4")
                                    .build(),
                            )
                        },
                    )
                }
            }
        },
        onClose = onClose,
    )
}

/**
 * The three registrations captions need, for a video editor configuration that does not already carry them.
 *
 * Compiled against the starter kit so the sample builds, but written for a configuration without captions —
 * adding a button the list already holds would render it twice.
 */
@Composable
private fun CustomVideoCaptionsSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license,
        configuration = {
            EditorConfiguration.remember(::VideoConfigurationBuilder) {
                onCreate = {
                    onCreate(
                        loadAssetSources = {
                            // highlight-android-caption-presets-source
                            onLoadAssetSources()
                            editorContext.engine.asset.addLocalSourceFromJSON(
                                contentUri = editorContext.baseUri
                                    .buildUpon()
                                    .appendPath("ly.img.caption.presets")
                                    .appendPath("content.json")
                                    .build(),
                            )
                            // highlight-android-caption-presets-source
                        },
                    )
                }
                dock = {
                    // highlight-android-captions-dock
                    Dock.remember {
                        listBuilder = {
                            Dock.ListBuilder.remember {
                                add { Dock.Button.rememberSystemGallery() }
                                add { Dock.Button.rememberTextLibrary() }
                                add { Dock.Button.rememberCaptions() }
                                add { Dock.Button.rememberAudiosLibrary() }
                            }
                        }
                    }
                    // highlight-android-captions-dock
                }
                inspectorBar = {
                    // highlight-android-captions-inspector-bar
                    InspectorBar.remember {
                        listBuilder = {
                            InspectorBar.ListBuilder.remember {
                                add { InspectorBar.Button.rememberEditCaptions() }
                                add { InspectorBar.Button.rememberCaptionStyle() }
                                add { InspectorBar.Button.rememberFormatText() }
                                add { InspectorBar.Button.rememberSplit() }
                                add { InspectorBar.Button.rememberDelete() }
                            }
                        }
                    }
                    // highlight-android-captions-inspector-bar
                }
            }
        },
        onClose = onClose,
    )
}
