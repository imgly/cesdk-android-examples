package ly.img.editor

import android.net.Uri
import ly.img.editor.core.event.EditorEventHandler
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.engine.AssetDefinition
import ly.img.engine.Engine
import ly.img.engine.MimeType

/**
 * Configuration class of the [ly.img.engine.Engine] when launching the editor. All the properties are optional
 * except [license] and [onCreate]. Check helper implementations in the companion object of the class.
 *
 * @param license the license to activate the [ly.img.engine.Engine] with.
 * @param userId an optional unique ID tied to your application's user. This helps us accurately calculate monthly active users (MAU).
 * Especially useful when one person uses the app on multiple devices with a sign-in feature, ensuring they're counted once.
 * Providing this aids in better data accuracy.
 * @param baseUri the base Uri that is used to construct absolute paths from relative paths.
 * absolutePath = baseUri + relativePath.
 * For instance, baseUri can be set to android assets: file:///android_asset/.
 * After setting this path you can, for instance, load example.scene from assets/scenes folder using relative path:
 *      engine.scene.load(sceneUri = Uri.parse("scenes/example.scene"))
 * @param onCreate the callback that is invoked when the editor is created. This is the main initialization block of both the editor
 * and engine. Normally, you should create/load a scene as well as prepare asset sources in this block.
 * We recommend that you check the availability of the scene before creating/loading a new scene since a recreated scene may already
 * exist if the callback is invoked after a process recreation.
 * Note that the "create" coroutine job will survive configuration changes and will be cancelled only if the editor is closed or the process is killed
 * when in the background.
 * Check [EditorDefaults.onCreate] for an example implementation.
 * @param onExport the callback that is invoked when the export button is clicked.
 * You may want to call one of the following functions in this callback: [ly.img.engine.BlockApi.export],
 * [ly.img.engine.BlockApi.exportWithColorMask], [ly.img.engine.BlockApi.exportVideo]. When the job is done, you can interact with
 * the UI by sending an event with [EditorEventHandler]. The event will be captured in [EditorConfiguration.onEvent]. Check
 * the default implementation of [onExport] for sample implementation.
 * Note that the "export" coroutine job will survive configuration changes and will be cancelled only if the editor is closed or the process is killed
 * when in the background.
 * @param onUpload the callback that is invoked after an asset is added to [UploadAssetSourceType]. When selecting an asset to upload,
 * a default [AssetDefinition] object is constructed based on the selected asset and the callback is invoked. You can either leave
 * the asset definition unmodified and do nothing (that's what the default implementation of the callback does), or adjust the properties
 * of the object, or maybe even upload the asset asset file to your server and adjust the uri property of the asset.
 * Note that the "upload" coroutine job will survive configuration changes and will be cancelled only if the editor is closed or the process is killed
 * when in the background.
 * @param onClose the callback that is invoked after a tap on the navigation icon of the toolbar or on the system back button.
 * The callback receives a Boolean parameter that indicates whether editor has unsaved changes. In case the flag is true,
 * [ShowCloseConfirmationDialogEvent] event is sent in the default implementation which displays a confirmation dialog. If
 * the flag is false, the editor is closed.
 * Note that the "close" coroutine job will survive configuration changes and will be cancelled only if the editor is closed or
 * the process is killed when in the background.
 * @param onError the callback that is invoked after the editor captures an error. A [ShowErrorDialogEvent] event is sent in
 * the default implementation which displays a popup dialog with action button that closes the editor.
 * Note that the "error" coroutine job will survive configuration changes and will be cancelled only if the editor is closed or
 * the process is killed when in the background.
 */
class EngineConfiguration(
    val license: String,
    val userId: String? = null,
    val baseUri: Uri = Uri.parse(BASE_URI),
    val onCreate: suspend (Engine, EditorEventHandler) -> Unit,
    val onExport: suspend (Engine, EditorEventHandler) -> Unit = { engine, eventHandler ->
        EditorDefaults.run {
            eventHandler.send(ShowLoading)
            val blob =
                engine.block.export(
                    block = requireNotNull(engine.scene.get()),
                    mimeType = MimeType.PDF,
                ) {
                    scene.getPages().forEach {
                        block.setScopeEnabled(it, key = "layer/visibility", enabled = true)
                        block.setVisible(it, visible = true)
                    }
                }
            val tempFile = writeToTempFile(blob)
            eventHandler.send(HideLoading)
            eventHandler.send(ShareFileEvent(tempFile))
        }
    },
    val onUpload: suspend AssetDefinition.(
        Engine,
        EditorEventHandler,
        UploadAssetSourceType,
    ) -> AssetDefinition = { _, _, _ -> this },
    val onClose: suspend (Engine, Boolean, EditorEventHandler) -> Unit = { _, hasUnsavedChanges, eventHandler ->
        if (hasUnsavedChanges) {
            eventHandler.send(ShowCloseConfirmationDialogEvent)
        } else {
            eventHandler.sendCloseEditorEvent()
        }
    },
    val onError: suspend (Throwable, Engine, EditorEventHandler) -> Unit = { error, _, eventHandler ->
        eventHandler.send(ShowErrorDialogEvent(error))
    },
) {
    companion object {
        private const val BASE_URI = "https://cdn.img.ly/packages/imgly/cesdk-engine/1.19.0/assets"

        /**
         * Helper function for creating an [EngineConfiguration] object when launching [ApparelEditor]. It is helpful to use this
         * function if you want to launch the editor in default configuration. The only thing that you need to provide is the [license].
         *
         * @param license the license to activate the [ly.img.engine.Engine] with.
         * @param userId an optional unique ID tied to your application's user. This helps us accurately calculate monthly active users (MAU).
         * Especially useful when one person uses the app on multiple devices with a sign-in feature, ensuring they're counted once.
         * Providing this aids in better data accuracy.
         * @param baseUri the base uri that is used to construct absolute paths from relative paths.
         * absolutePath = baseUri + relativePath.
         * For instance, baseUri can be set to android assets: file:///android_asset/.
         * After setting this path you can, for instance, load example.scene from assets/scenes folder using relative path:
         *      engine.scene.load(sceneUri = Uri.parse("scenes/example.scene"))
         * @param sceneUri the scene Uri that is used to load the content of the [ApparelEditor]. This Uri is delegated to
         * [EditorDefaults.onCreate] in order to load the scene and asset sources.
         */
        fun getForApparel(
            license: String,
            userId: String? = null,
            baseUri: Uri = Uri.parse(BASE_URI),
            sceneUri: Uri = Uri.parse("file:///android_asset/scenes/apparel.scene"),
        ) = EngineConfiguration(
            license = license,
            userId = userId,
            baseUri = baseUri,
            onCreate = { engine, eventHandler ->
                EditorDefaults.onCreate(engine, sceneUri, eventHandler)
            },
        )

        /**
         * Helper function for creating an [EngineConfiguration] object when launching [PostcardEditor]. It is helpful to use this
         * function if you want to launch the editor in default configuration. The only thing that you need to provide is the [license].
         *
         * @param license the license to activate the [ly.img.engine.Engine] with.
         * @param userId an optional unique ID tied to your application's user. This helps us accurately calculate monthly active users (MAU).
         * Especially useful when one person uses the app on multiple devices with a sign-in feature, ensuring they're counted once.
         * Providing this aids in better data accuracy.
         * @param baseUri the base uri that is used to construct absolute paths from relative paths.
         * absolutePath = baseUri + relativePath.
         * For instance, baseUri can be set to android assets: file:///android_asset/.
         * After setting this path you can, for instance, load example.scene from assets/scenes folder using relative path:
         *      engine.scene.load(sceneUri = Uri.parse("scenes/example.scene"))
         * @param sceneUri the scene Uri that is used to load the content of the [PostcardEditor]. This Uri is delegated to
         * [EditorDefaults.onCreate] in order to load the scene and asset sources.
         */
        fun getForPostcard(
            license: String,
            userId: String? = null,
            baseUri: Uri = Uri.parse(BASE_URI),
            sceneUri: Uri = Uri.parse("file:///android_asset/scenes/postcard.scene"),
        ) = EngineConfiguration(
            license = license,
            userId = userId,
            baseUri = baseUri,
            onCreate = { engine, eventHandler ->
                EditorDefaults.onCreate(engine, sceneUri, eventHandler)
            },
        )

        /**
         * Helper function for creating an [EngineConfiguration] object when launching [CreativeEditor]. This function simplifies
         * the initialization process, offering a default configuration ideal for a wide range of applications. The essential requirement
         * is the [license], with options to specify a [userId], [baseUri], and [sceneUri] for further customization.
         *
         * @param license the license required to activate the [ly.img.engine.Engine].
         * @param userId an optional identifier for the application's user, enhancing the accuracy of monthly active users (MAU) calculations.
         * This is particularly beneficial for tracking users across multiple devices when they sign in, ensuring they are counted uniquely.
         * @param baseUri the foundational URI for constructing absolute paths from relative ones. For example, setting it to
         * the Android assets directory allows loading resources directly from there: file:///android_asset/.
         * This base URI enables the loading of specific scenes or assets using their relative paths.
         * @param sceneUri the specific scene URI to load content within the [CreativeEditor]. This URI is passed to
         * [EditorDefaults.onCreate] to facilitate scene and asset loading at initialization.
         */
        fun getForCreative(
            license: String,
            userId: String? = null,
            baseUri: Uri = Uri.parse(BASE_URI),
            sceneUri: Uri = Uri.parse("file:///android_asset/scenes/empty.scene"),
        ) = EngineConfiguration(
            license = license,
            userId = userId,
            baseUri = baseUri,
            onCreate = { engine, eventHandler ->
                EditorDefaults.onCreate(engine, sceneUri, eventHandler)
            },
        )
    }
}
