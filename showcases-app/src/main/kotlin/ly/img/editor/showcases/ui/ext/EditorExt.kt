package ly.img.editor.showcases.ui.ext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import ly.img.editor.core.component.NavigationBar
import ly.img.editor.core.component.closeEditor
import ly.img.editor.core.component.modify
import ly.img.editor.core.component.rememberCloseEditor
import ly.img.editor.showcases.icon.Homeoutline
import ly.img.editor.showcases.icon.IconPack

@Composable
fun NavigationBar<NavigationBar.Scope>.modifiedCloseEditor(): NavigationBar<NavigationBar.Scope> {
    val updatedListBuilder = listBuilder.modify {
        replace(id = NavigationBar.Button.Id.closeEditor) {
            NavigationBar.Button.rememberCloseEditor {
                vectorIcon = { IconPack.Homeoutline }
            }
        }
    }
    return remember(this, updatedListBuilder) {
        copy(listBuilder = updatedListBuilder)
    }
}
