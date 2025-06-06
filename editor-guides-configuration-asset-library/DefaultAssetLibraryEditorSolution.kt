import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import ly.img.editor.DesignEditor
import ly.img.editor.EditorConfiguration
import ly.img.editor.EditorDefaults
import ly.img.editor.EngineConfiguration
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.LibraryContent
import ly.img.editor.core.library.addSection
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.library.dropSection
import ly.img.editor.core.library.replaceSection
import ly.img.editor.rememberForDesign
import ly.img.editor.smoketests.R

// Add this composable to your NavHost
@Composable
fun DefaultAssetLibraryEditorSolution(navController: NavHostController) {
    // highlight-configuration-custom-asset-source
    val unsplashAssetSource = remember {
        UnsplashAssetSource("<your Unsplash API host endpoint here>")
    }
    val engineConfiguration = EngineConfiguration.remember(
        license = "<your license here>",
        onCreate = {
            EditorDefaults.onCreate(
                engine = editorContext.engine,
                sceneUri = EngineConfiguration.defaultDesignSceneUri,
                eventHandler = editorContext.eventHandler,
            ) { _, _ ->
                editorContext.engine.asset.addSource(unsplashAssetSource)
            }
        },
    )
    // highlight-configuration-custom-asset-source
    // highlight-configuration-default-asset-library
    val assetLibrary = remember {
        val unsplashSection = LibraryContent.Section(
            titleRes = R.string.unsplash,
            sourceTypes = listOf(AssetSourceType(sourceId = unsplashAssetSource.sourceId)),
            assetType = AssetType.Image,
        )
        AssetLibrary.getDefault(
            tabs = listOf(
                AssetLibrary.Tab.IMAGES,
                AssetLibrary.Tab.SHAPES,
                AssetLibrary.Tab.STICKERS,
                AssetLibrary.Tab.TEXT,
            ),
            images = LibraryCategory.Images
                .replaceSection(index = 0) {
                    // We replace the title: "Image Uploads" -> "Uploads"
                    copy(titleRes = R.string.uploads)
                }
                .dropSection(index = 1)
                .addSection(unsplashSection),
        )
    }
    val editorConfiguration = EditorConfiguration.rememberForDesign(
        assetLibrary = assetLibrary,
    )
    // highlight-configuration-default-asset-library
    DesignEditor(
        engineConfiguration = engineConfiguration,
        editorConfiguration = editorConfiguration,
    ) {
        // You can set result here
        navController.popBackStack()
    }
}
