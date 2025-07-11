import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import ly.img.editor.core.R
import ly.img.editor.core.component.NavigationBar
import ly.img.editor.core.component.rememberCloseEditor
import ly.img.editor.core.component.rememberExport
import ly.img.editor.core.component.rememberNextPage
import ly.img.editor.core.component.rememberPreviousPage
import ly.img.editor.core.component.rememberRedo
import ly.img.editor.core.component.rememberTogglePagesMode
import ly.img.editor.core.component.rememberTogglePreviewMode
import ly.img.editor.core.component.rememberUndo

// highlight-listBuilders
// highlight-listBuilders-design
@Composable
fun NavigationBar.ListBuilder.rememberForDesign() = NavigationBar.ListBuilder.remember {
    aligned(alignment = Alignment.Start) {
        add { NavigationBar.Button.rememberCloseEditor() }
    }
    aligned(alignment = Alignment.End) {
        add { NavigationBar.Button.rememberUndo() }
        add { NavigationBar.Button.rememberRedo() }
        add { NavigationBar.Button.rememberTogglePagesMode() }
        add { NavigationBar.Button.rememberExport() }
    }
}
// highlight-listBuilders-design

// highlight-listBuilders-photo
@Composable
fun NavigationBar.ListBuilder.rememberForPhoto() = NavigationBar.ListBuilder.remember {
    aligned(alignment = Alignment.Start) {
        add { NavigationBar.Button.rememberCloseEditor() }
    }
    aligned(alignment = Alignment.End) {
        add { NavigationBar.Button.rememberUndo() }
        add { NavigationBar.Button.rememberRedo() }
        add { NavigationBar.Button.rememberTogglePreviewMode() }
        add { NavigationBar.Button.rememberExport() }
    }
}
// highlight-listBuilders-photo

// highlight-listBuilders-video
@Composable
fun NavigationBar.ListBuilder.rememberForVideo() = NavigationBar.ListBuilder.remember {
    aligned(alignment = Alignment.Start) {
        add { NavigationBar.Button.rememberCloseEditor() }
    }
    aligned(alignment = Alignment.End) {
        add { NavigationBar.Button.rememberUndo() }
        add { NavigationBar.Button.rememberRedo() }
        add { NavigationBar.Button.rememberExport() }
    }
}
// highlight-listBuilders-video

// highlight-listBuilders-apparel
@Composable
fun NavigationBar.ListBuilder.rememberForApparel() = NavigationBar.ListBuilder.remember {
    aligned(alignment = Alignment.Start) {
        add { NavigationBar.Button.rememberCloseEditor() }
    }
    aligned(alignment = Alignment.End) {
        add { NavigationBar.Button.rememberUndo() }
        add { NavigationBar.Button.rememberRedo() }
        add { NavigationBar.Button.rememberTogglePreviewMode() }
        add { NavigationBar.Button.rememberExport() }
    }
}
// highlight-listBuilders-apparel

// highlight-listBuilders-postcard
@Composable
fun NavigationBar.ListBuilder.rememberForPostcard() = NavigationBar.ListBuilder.remember {
    aligned(alignment = Alignment.Start) {
        add { NavigationBar.Button.rememberCloseEditor() }
        add {
            NavigationBar.Button.rememberPreviousPage(
                text = { stringResource(R.string.ly_img_editor_navigation_bar_button_design) },
            )
        }
    }

    aligned(alignment = Alignment.CenterHorizontally) {
        add { NavigationBar.Button.rememberUndo() }
        add { NavigationBar.Button.rememberRedo() }
        add { NavigationBar.Button.rememberTogglePreviewMode() }
    }

    aligned(alignment = Alignment.End) {
        add {
            NavigationBar.Button.rememberNextPage(
                text = { stringResource(R.string.ly_img_editor_navigation_bar_button_write) },
            )
        }
        add { NavigationBar.Button.rememberExport() }
    }
}
// highlight-listBuilders-postcard
// highlight-listBuilders
