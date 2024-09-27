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
import ly.img.editor.showcase.R
import ly.img.editor.showcase.Secrets

// Add this composable to your NavHost
@Composable
fun DefaultAssetLibraryEditorSolution(navController: NavHostController) {
    // highlight-configuration-custom-asset-source
    val unsplashAssetSource = UnsplashAssetSource(Secrets.unsplashHost)
    val engineConfiguration =
        remember {
            EngineConfiguration(
                license = "<your license here>",
                onCreate = { engine, eventHandler ->
                    EditorDefaults.onCreate(
                        engine = engine,
                        sceneUri = EngineConfiguration.defaultDesignSceneUri,
                        eventHandler = eventHandler,
                    ) { _, _ ->
                        engine.asset.addSource(unsplashAssetSource)
                    }
                },
            )
        }
    // highlight-configuration-custom-asset-source
    // highlight-configuration-default-asset-library
    val unsplashSection =
        LibraryContent.Section(
            titleRes = R.string.unsplash,
            sourceTypes = listOf(AssetSourceType(sourceId = unsplashAssetSource.sourceId)),
            assetType = AssetType.Image,
        )
    val assetLibrary =
        AssetLibrary.getDefault(
            tabs =
                listOf(
                    AssetLibrary.Tab.IMAGES,
                    AssetLibrary.Tab.SHAPES,
                    AssetLibrary.Tab.STICKERS,
                    AssetLibrary.Tab.TEXT,
                ),
            images =
                LibraryCategory.Images
                    .replaceSection(index = 0) {
                        // We replace the title: "Image Uploads" -> "Uploads"
                        copy(titleRes = R.string.uploads)
                    }
                    .dropSection(index = 1)
                    .addSection(unsplashSection),
        )
    // highlight-configuration-default-asset-library
    val editorConfiguration =
        EditorConfiguration.getDefault(
            assetLibrary = assetLibrary,
        )
    DesignEditor(
        engineConfiguration = engineConfiguration,
        editorConfiguration = editorConfiguration,
    ) {
        // You can set result here
        navController.popBackStack()
    }
}
