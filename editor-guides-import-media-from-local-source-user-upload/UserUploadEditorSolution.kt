import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.editor.Editor
import ly.img.editor.core.R
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.NavigationBar
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberCloseEditor
import ly.img.editor.core.component.rememberImagesLibrary
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.LibraryContent
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.engine.AssetDefinition
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.UUID

@Composable
fun UserUploadEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license,
        configuration = {
            EditorConfiguration.remember {
                // highlight-android-create-scene-and-sources
                onCreate = {
                    val engine = editorContext.engine
                    val scene = engine.scene.create()
                    val page = engine.block.create(DesignBlockType.Page)
                    engine.block.setWidth(block = page, value = 1080F)
                    engine.block.setHeight(block = page, value = 1080F)
                    engine.block.setFill(block = page, fill = engine.block.createFill(FillType.Color))
                    engine.block.appendChild(parent = scene, child = page)
                    registerUploadAssetSources(engine)
                }
                // highlight-android-create-scene-and-sources
                // highlight-android-show-upload-sources
                assetLibrary = {
                    remember {
                        userUploadAssetLibrary()
                    }
                }
                // highlight-android-show-upload-sources
                // highlight-android-handle-upload
                onUpload = { assetDefinition, uploadSource ->
                    persistUploadedAsset(
                        context = editorContext.activity,
                        assetDefinition = assetDefinition,
                        uploadSource = uploadSource,
                    )
                }
                // highlight-android-handle-upload
                navigationBar = {
                    NavigationBar.remember {
                        listBuilder = {
                            NavigationBar.ListBuilder.remember {
                                aligned(alignment = Alignment.Start) {
                                    add { NavigationBar.Button.rememberCloseEditor() }
                                }
                            }
                        }
                    }
                }
                dock = {
                    Dock.remember {
                        listBuilder = {
                            Dock.ListBuilder.remember {
                                add { Dock.Button.rememberImagesLibrary() }
                            }
                        }
                        horizontalArrangement = { Arrangement.Center }
                    }
                }
            }
        },
        onClose = onClose,
    )
}

// highlight-android-register-upload-sources
private fun registerUploadAssetSources(engine: Engine) {
    val existingSources = engine.asset.findAllSources().toSet()

    fun addIfMissing(
        source: UploadAssetSourceType,
        mimeTypes: List<String>,
    ) {
        if (source.sourceId !in existingSources) {
            engine.asset.addLocalSource(
                sourceId = source.sourceId,
                supportedMimeTypes = mimeTypes,
            )
        }
    }

    addIfMissing(
        source = AssetSourceType.ImageUploads,
        mimeTypes = listOf(
            "image/jpeg",
            "image/png",
            "image/heic",
            "image/heif",
            "image/svg+xml",
            "image/gif",
            "image/apng",
            "image/bmp",
            "image/webp",
        ),
    )
}
// highlight-android-register-upload-sources

// highlight-android-upload-asset-library
private fun userUploadAssetLibrary(): AssetLibrary {
    val images = LibraryCategory.Images.copy(
        content = LibraryContent.Sections(
            titleRes = R.string.ly_img_editor_asset_library_title_images,
            sections = listOf(
                LibraryContent.Section(
                    titleRes = R.string.ly_img_editor_asset_library_section_gallery,
                    sourceTypes = listOf(AssetSourceType.GalleryImage),
                    assetType = AssetType.Image,
                    expandContent = LibraryContent.Grid(
                        titleRes = R.string.ly_img_editor_asset_library_section_gallery,
                        sourceType = AssetSourceType.GalleryImage,
                        assetType = AssetType.Image,
                    ),
                ),
                LibraryContent.Section(
                    titleRes = R.string.ly_img_editor_asset_library_section_image_uploads,
                    sourceTypes = listOf(AssetSourceType.ImageUploads),
                    assetType = AssetType.Image,
                    expandContent = LibraryContent.Grid(
                        titleRes = R.string.ly_img_editor_asset_library_section_image_uploads,
                        sourceType = AssetSourceType.ImageUploads,
                        assetType = AssetType.Image,
                    ),
                ),
            ),
        ),
    )
    return AssetLibrary.getDefault(
        tabs = listOf(
            AssetLibrary.Tab.IMAGES,
        ),
        images = images,
    )
}
// highlight-android-upload-asset-library

// highlight-android-persist-upload
private suspend fun persistUploadedAsset(
    context: Context,
    assetDefinition: AssetDefinition,
    uploadSource: UploadAssetSourceType,
): AssetDefinition {
    val meta = assetDefinition.meta ?: return assetDefinition
    val sourceUri = meta["uri"]?.toUri() ?: return assetDefinition
    val persistentUri = copyUriToAppStorage(
        context = context,
        sourceUri = sourceUri,
        uploadSource = uploadSource,
        fileLabel = "media",
    )

    val updatedMeta = meta.toMutableMap()
    updatedMeta["uri"] = persistentUri.toString()
    meta["thumbUri"]?.let { thumbnail ->
        updatedMeta["thumbUri"] = if (thumbnail == sourceUri.toString()) {
            persistentUri.toString()
        } else {
            copyUriToAppStorage(
                context = context,
                sourceUri = thumbnail.toUri(),
                uploadSource = uploadSource,
                fileLabel = "thumbnail",
            ).toString()
        }
    }

    return assetDefinition.copy(meta = updatedMeta)
}

private suspend fun copyUriToAppStorage(
    context: Context,
    sourceUri: Uri,
    uploadSource: UploadAssetSourceType,
    fileLabel: String,
): Uri = withContext(Dispatchers.IO) {
    val directory = File(context.filesDir, "cesdk-user-uploads/${uploadSource.sourceId}")
    directory.mkdirs()
    val extension = sourceUri.extension(context, uploadSource)
    val outputFile = File(directory, "$fileLabel-${UUID.randomUUID()}.$extension")

    openUriInputStream(context, sourceUri).use { input ->
        FileOutputStream(outputFile).use { output ->
            input.copyTo(output)
        }
    }

    Uri.fromFile(outputFile)
}

private fun openUriInputStream(
    context: Context,
    sourceUri: Uri,
) = context.contentResolver.openInputStream(sourceUri)
    ?: if (sourceUri.scheme == "file") {
        FileInputStream(requireNotNull(sourceUri.path) { "File URI has no path." })
    } else {
        error("Cannot read uploaded asset URI: $sourceUri")
    }

private fun Uri.extension(
    context: Context,
    uploadSource: UploadAssetSourceType,
): String {
    val detectedExtension = context.contentResolver.getType(this)?.let { mimeType ->
        MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
    }
    if (!detectedExtension.isNullOrBlank()) return detectedExtension

    return when (uploadSource.sourceId) {
        AssetSourceType.VideoUploads.sourceId -> "mp4"
        AssetSourceType.AudioUploads.sourceId -> "m4a"
        else -> "jpg"
    }
}
// highlight-android-persist-upload
