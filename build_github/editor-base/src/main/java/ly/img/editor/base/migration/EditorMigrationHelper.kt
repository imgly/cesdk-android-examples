package ly.img.editor.base.migration

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.editor.core.ui.Environment
import java.io.File

class EditorMigrationHelper {
    suspend fun migrate() =
        withContext(Dispatchers.IO) {
            val editorDir = Environment.getEditorDir()
            editorDir.mkdirs()
            val oldFontsDir = File(editorDir.parentFile, "editor_fonts")
            if (oldFontsDir.exists()) {
                oldFontsDir.deleteRecursively()
            }
        }
}
