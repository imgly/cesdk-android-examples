import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.img.editor.BasicConfigurationBuilder
import ly.img.editor.Editor
import ly.img.editor.configuration.design.DesignConfigurationBuilder
import ly.img.editor.core.EditorScope
import ly.img.editor.core.ScopedProperty
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.EditorComponent
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.modify
import ly.img.editor.core.component.remember
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.EditorConfigurationBuilder
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.configuration.then
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.Image
import ly.img.engine.ContentFillMode
import ly.img.engine.DesignBlockType
import ly.img.engine.FillType
import ly.img.engine.HorizontalBlockAlignment
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode
import ly.img.engine.VerticalBlockAlignment
import java.io.File
import java.nio.ByteBuffer
import java.util.UUID

@Composable
fun CustomFeaturePluginEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    // highlight-android-apply-plugin
    Editor(
        license = license,
        configuration = {
            EditorConfiguration
                .remember(::DesignConfigurationBuilder)
                .then(::CustomFeaturePlugin) {
                    randomImageUri = "https://img.ly/static/ubq_samples/sample_1.jpg".toUri()
                }
        },
        onClose = onClose,
    )
    // highlight-android-apply-plugin
}

open class CustomFeaturePlugin : EditorConfigurationBuilder() {
    // highlight-android-plugin-state
    var randomImageUri: Uri by editorContext.mutableStateOf(
        key = "com.example.editor.customFeature.randomImageUri",
        initial = Uri.parse("https://img.ly/static/ubq_samples/sample_1.jpg"),
    )

    private var isLoading: Boolean by editorContext.mutableStateOf(
        key = BasicConfigurationBuilder.KEY_STATE_SHOW_LOADING,
        initial = false,
    )
    // highlight-android-plugin-state

    // highlight-android-on-create
    override var onCreate: (suspend EditorScope.() -> Unit)? = {
        try {
            isLoading = true
            val editorScope = this
            coroutineScope {
                launch {
                    parentConfiguration?.onCreate?.invoke(editorScope)
                    // The parent Design configuration hides loading after scene setup.
                    isLoading = true
                }
                launch {
                    Log.d(TAG, "CustomFeaturePlugin setup started.")
                    delay(3_000)
                    Log.d(TAG, "CustomFeaturePlugin setup finished.")
                }
            }
        } finally {
            isLoading = false
        }
    }
    // highlight-android-on-create

    // highlight-android-on-export
    override var onExport: (suspend EditorScope.() -> Unit)? = {
        try {
            isLoading = true
            val scene = requireNotNull(editorContext.engine.scene.get()) {
                "A scene is required before exporting."
            }
            val archive = editorContext.engine.scene.saveToArchive(scene = scene)
            val file = writeArchiveToTempFile(archive)
            Toast.makeText(editorContext.activity, "Saved scene archive to ${file.name}", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Saved scene archive to ${file.absolutePath}")
        } finally {
            isLoading = false
        }
    }
    // highlight-android-on-export

    // highlight-android-dock
    override var dock: ScopedProperty<EditorScope, EditorComponent<*>?>? = dockComponent@{
        val sourceDock = parentConfiguration?.dock as? Dock ?: return@dockComponent null
        val updatedListBuilder = sourceDock.listBuilder.modify {
            addFirst {
                Dock.Button.remember {
                    id = { EditorComponentId("com.example.component.dock.button.customFeature") }
                    vectorIcon = { IconPack.Image }
                    textString = { "Image" }
                    contentDescription = { "Add image" }
                    onClick = { addImageBlockFromPlugin() }
                }
            }
        }
        remember(sourceDock, updatedListBuilder) {
            sourceDock.copy(listBuilder = updatedListBuilder)
        }
    }
    // highlight-android-dock

    // highlight-android-canvas-menu
    override var canvasMenu: ScopedProperty<EditorScope, EditorComponent<*>?>? = {
        null
    }
    // highlight-android-canvas-menu

    // highlight-android-add-image
    private fun EditorScope.addImageBlockFromPlugin() {
        val engine = editorContext.engine
        val page = requireNotNull(engine.scene.getCurrentPage()) {
            "A current page is required before adding an image block."
        }
        val imageBlock = engine.block.create(DesignBlockType.Graphic)
        val shape = engine.block.createShape(ShapeType.Rect)
        val fill = engine.block.createFill(FillType.Image)

        engine.block.setShape(block = imageBlock, shape = shape)
        engine.block.setUri(
            block = fill,
            property = "fill/image/imageFileURI",
            value = randomImageUri,
        )
        engine.block.setFill(block = imageBlock, fill = fill)
        engine.block.setContentFillMode(block = imageBlock, mode = ContentFillMode.COVER)
        engine.block.setWidthMode(block = imageBlock, mode = SizeMode.PERCENT)
        engine.block.setWidth(block = imageBlock, value = 0.5F)
        engine.block.appendChild(parent = page, child = imageBlock)
        if (engine.block.isAlignable(listOf(imageBlock))) {
            engine.block.alignHorizontally(listOf(imageBlock), alignment = HorizontalBlockAlignment.CENTER)
            engine.block.alignVertically(listOf(imageBlock), alignment = VerticalBlockAlignment.CENTER)
        }
        engine.block.setSelected(block = imageBlock, selected = true)
    }
    // highlight-android-add-image

    // highlight-android-write-archive
    private suspend fun writeArchiveToTempFile(archive: ByteBuffer): File = withContext(Dispatchers.IO) {
        File.createTempFile("custom-feature-${UUID.randomUUID()}", ".scene.zip").apply {
            outputStream().channel.use { channel ->
                channel.write(archive)
            }
        }
    }
    // highlight-android-write-archive

    private companion object {
        const val TAG = "CustomFeaturePlugin"
    }
}
