package ly.img.editor.configuration.video.component

import androidx.compose.runtime.Composable
import ly.img.editor.configuration.video.VideoConfigurationBuilder
import ly.img.editor.core.component.Timeline

/**
 * The configuration of the component that is displayed over the editor. It is useful if you want to display a popup dialog or anything in the
 * overlay.
 * Implementation shows [Timeline] in the bottom panel.
 */
@Composable
fun VideoConfigurationBuilder.rememberBottomPanel() = rememberTimeline()
