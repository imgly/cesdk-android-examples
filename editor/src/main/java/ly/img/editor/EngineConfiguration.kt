package ly.img.editor

import android.net.Uri
import android.util.SizeF
import androidx.compose.runtime.Composable
import ly.img.editor.core.EditorScope
import ly.img.editor.core.UnstableEditorApi
import ly.img.editor.core.component.data.Nothing
import ly.img.editor.core.component.data.nothing
import ly.img.editor.core.engine.EngineRenderTarget
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.event.EditorEventHandler
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.engine.AssetDefinition
import ly.img.engine.Engine
import ly.img.engine.MimeType

/**
 * Configuration class of the [ly.img.engine.Engine] when launching the editor. All the properties are optional
 * except [license] and [onCreate]. Check helper implementations in the companion object of the class.
 * Use remember composable functions in the companion object to create an instance of this class.
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
 * @param renderTarget the target which should be used by the [ly.img.engine.Engine] to render.
 * Default value is [EngineRenderTarget.SURFACE_VIEW].
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
 * The callback receives a boolean parameter that indicates whether editor has unsaved changes. In case the flag is true,
 * [ShowCloseConfirmationDialogEvent] event is sent in the default implementation which displays a confirmation dialog. If
 * the flag is false, the editor is closed.
 * Note that the "close" coroutine job will survive configuration changes and will be cancelled only if the editor is closed or
 * the process is killed when in the background.
 * @param onError the callback that is invoked after the editor captures an error. A [ShowErrorDialogEvent] event is sent in
 * the default implementation which displays a popup dialog with action button that closes the editor.
 * Note that the "error" coroutine job will survive configuration changes and will be cancelled only if the editor is closed or
 * the process is killed when in the background.
*/
class EngineConfiguration private constructor(
    val license: String,
    val userId: String? = null,
    val baseUri: Uri = defaultBaseUri,
    val renderTarget: EngineRenderTarget = EngineRenderTarget.SURFACE_VIEW,
    val onCreate: suspend EditorScope.() -> Unit,
    val onExport: suspend EditorScope.() -> Unit = {
        EditorDefaults.onExport(editorContext.engine, editorContext.eventHandler)
    },
    val onUpload: suspend EditorScope.(
        AssetDefinition,
        UploadAssetSourceType,
    ) -> AssetDefinition = { asset, _ -> asset },
    val onClose: suspend EditorScope.(Boolean) -> Unit = { hasUnsavedChanges ->
        if (hasUnsavedChanges) {
            editorContext.eventHandler.send(ShowCloseConfirmationDialogEvent)
        } else {
            editorContext.eventHandler.send(EditorEvent.CloseEditor())
        }
    },
    val onError: suspend EditorScope.(Throwable) -> Unit = { error ->
        editorContext.eventHandler.send(ShowErrorDialogEvent(error))
    },
    private val `_`: Nothing = nothing,
) {
    override fun toString(): String {
        return "$`_`EngineConfiguration(" +
            "license = $license, " +
            ", userId = $userId" +
            ", baseUri = $baseUri" +
            ", renderTarget = $renderTarget" +
            ", onCreate = $onCreate" +
            ", onUpload = $onExport" +
            ", onClose = $onClose" +
            ", onError = $onError" +
            ")"
    }

    companion object {
        /**
         * The default baseUri value used in [EngineConfiguration].
         */
        val defaultBaseUri: Uri by lazy {
            Uri.parse("https://cdn.img.ly/packages/imgly/cesdk-engine/${EditorBuildConfig.VERSION}/assets")
        }

        /**
         * The default sceneUri value used in [EngineConfiguration.rememberForDesign].
         */
        @UnstableEditorApi
        val defaultDesignSceneUri: Uri by lazy {
            Uri.parse("file:///android_asset/scenes/empty.scene")
        }

        /**
         * The default sceneUri value used in [EngineConfiguration.rememberForApparel].
         */
        @UnstableEditorApi
        val defaultApparelSceneUri: Uri by lazy {
            Uri.parse("file:///android_asset/scenes/apparel.scene")
        }

        /**
         * The default sceneUri value used in [EngineConfiguration.rememberForPostcard].
         */
        @UnstableEditorApi
        val defaultPostcardSceneUri: Uri by lazy {
            Uri.parse("file:///android_asset/scenes/postcard.scene")
        }

        /**
         * The default sceneUri value used in [EngineConfiguration.rememberForVideo].
         */
        @UnstableEditorApi
        val defaultVideoSceneUri: Uri by lazy {
            Uri.parse("file:///android_asset/scenes/video.scene")
        }

        /**
         * A composable function that creates and remembers an [EngineConfiguration] instance.
         * The essential requirement is the [license], with available extra parameters for further customization.
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
         * @param renderTarget the target which should be used by the [ly.img.engine.Engine] to render.
         * Default value is [EngineRenderTarget.SURFACE_VIEW].
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
         * The callback receives a boolean parameter that indicates whether editor has unsaved changes. In case the flag is true,
         * [ShowCloseConfirmationDialogEvent] event is sent in the default implementation which displays a confirmation dialog. If
         * the flag is false, the editor is closed.
         * Note that the "close" coroutine job will survive configuration changes and will be cancelled only if the editor is closed or
         * the process is killed when in the background.
         * @param onError the callback that is invoked after the editor captures an error. A [ShowErrorDialogEvent] event is sent in
         * the default implementation which displays a popup dialog with action button that closes the editor.
         * Note that the "error" coroutine job will survive configuration changes and will be cancelled only if the editor is closed or
         * the process is killed when in the background.
         */
        @Composable
        fun remember(
            license: String,
            userId: String? = null,
            baseUri: Uri = defaultBaseUri,
            renderTarget: EngineRenderTarget = EngineRenderTarget.SURFACE_VIEW,
            onCreate: suspend EditorScope.() -> Unit,
            onExport: suspend EditorScope.() -> Unit = {
                EditorDefaults.onExport(editorContext.engine, editorContext.eventHandler)
            },
            onUpload: suspend EditorScope.(
                AssetDefinition,
                UploadAssetSourceType,
            ) -> AssetDefinition = { asset, _ -> asset },
            onClose: suspend EditorScope.(Boolean) -> Unit = { hasUnsavedChanges ->
                if (hasUnsavedChanges) {
                    editorContext.eventHandler.send(ShowCloseConfirmationDialogEvent)
                } else {
                    editorContext.eventHandler.send(EditorEvent.CloseEditor())
                }
            },
            onError: suspend EditorScope.(Throwable) -> Unit = { error ->
                editorContext.eventHandler.send(ShowErrorDialogEvent(error))
            },
            `_`: Nothing = nothing,
        ): EngineConfiguration =
            androidx.compose.runtime.remember {
                EngineConfiguration(
                    license = license,
                    userId = userId,
                    baseUri = baseUri,
                    renderTarget = renderTarget,
                    onCreate = onCreate,
                    onExport = onExport,
                    onUpload = onUpload,
                    onClose = onClose,
                    onError = onError,
                    `_` = `_`,
                )
            }

        /**
         * A composable helper function that creates and remembers an [EngineConfiguration] instance when launching [DesignEditor].
         * This function simplifies the initialization process, offering a default configuration ideal for a wide range of
         * applications. The essential requirement is the [license], with available extra parameters for further customization.
         *
         * @param license the license required to activate the [ly.img.engine.Engine].
         * @param userId an optional identifier for the application's user, enhancing the accuracy of monthly active users (MAU) calculations.
         * This is particularly beneficial for tracking users across multiple devices when they sign in, ensuring they are counted uniquely.
         * @param baseUri the foundational uri for constructing absolute paths from relative ones. For example, setting it to
         * the Android assets directory allows loading resources directly from there: file:///android_asset/.
         * This base uri enables the loading of specific scenes or assets using their relative paths.
         * @param sceneUri the specific scene uri to load content within the [DesignEditor]. This uri is passed to
         * [EditorDefaults.onCreate] to facilitate scene and asset loading at initialization.
         * @param renderTarget the target which should be used by the [ly.img.engine.Engine] to render.
         * Default value is [EngineRenderTarget.SURFACE_VIEW].
         */
        @UnstableEditorApi
        @Composable
        fun rememberForDesign(
            license: String,
            userId: String? = null,
            baseUri: Uri = defaultBaseUri,
            sceneUri: Uri = defaultDesignSceneUri,
            renderTarget: EngineRenderTarget = EngineRenderTarget.SURFACE_VIEW,
        ): EngineConfiguration =
            remember(
                license = license,
                userId = userId,
                baseUri = baseUri,
                renderTarget = renderTarget,
                onCreate = { ->
                    EditorDefaults.onCreate(editorContext.engine, sceneUri, editorContext.eventHandler)
                },
            )

        /**
         * A composable helper function that creates and remembers an [EngineConfiguration] instance when launching [PhotoEditor].
         * This function simplifies the initialization process, offering a default configuration ideal for a wide range of
         * applications. The essential requirement is the [license], with available extra parameters for further customization.
         *
         * @param license the license required to activate the [ly.img.engine.Engine].
         * @param imageUri the uri of the image that is used to create a scene using [ly.img.engine.SceneApi.createFromImage] API.
         * @param imageSize the size that should be used to load the image. If null, original size of the image will be used.
         * @param userId an optional identifier for the application's user, enhancing the accuracy of monthly active users (MAU) calculations.
         * This is particularly beneficial for tracking users across multiple devices when they sign in, ensuring they are counted uniquely.
         * @param baseUri the foundational uri for constructing absolute paths from relative ones. For example, setting it to
         * the Android assets directory allows loading resources directly from there: file:///android_asset/.
         * This base uri enables the loading of specific scenes or assets using their relative paths.
         * [EditorDefaults.onCreate] to facilitate scene and asset loading at initialization.
         * @param renderTarget the target which should be used by the [ly.img.engine.Engine] to render.
         * Default value is [EngineRenderTarget.SURFACE_VIEW].
         */
        @UnstableEditorApi
        @Composable
        fun rememberForPhoto(
            license: String,
            imageUri: Uri,
            imageSize: SizeF? = null,
            userId: String? = null,
            baseUri: Uri = defaultBaseUri,
            renderTarget: EngineRenderTarget = EngineRenderTarget.SURFACE_VIEW,
        ): EngineConfiguration =
            remember(
                license = license,
                userId = userId,
                baseUri = baseUri,
                renderTarget = renderTarget,
                onCreate = { ->
                    EditorDefaults.onCreateFromImage(editorContext.engine, imageUri, editorContext.eventHandler, imageSize)
                },
                onExport = {
                    EditorDefaults.run {
                        val engine = editorContext.engine
                        val eventHandler = editorContext.eventHandler
                        eventHandler.send(ShowLoading)
                        val blob =
                            engine.block.export(
                                block = requireNotNull(engine.scene.get()),
                                mimeType = MimeType.PNG,
                            )
                        val tempFile = writeToTempFile(blob, mimeType = MimeType.PNG)
                        eventHandler.send(HideLoading)
                        eventHandler.send(
                            ShareFileEvent(
                                file = tempFile,
                                mimeType = MimeType.PNG.key,
                            ),
                        )
                    }
                },
            )

        /**
         * A composable helper function that creates and remembers an [EngineConfiguration] instance when launching [ApparelEditor].
         * This function simplifies the initialization process, offering a default configuration ideal for a wide range of
         * applications. The essential requirement is the [license], with available extra parameters for further customization.
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
         * @param renderTarget the target which should be used by the [ly.img.engine.Engine] to render.
         * Default value is [EngineRenderTarget.SURFACE_VIEW].
         */
        @UnstableEditorApi
        @Composable
        fun rememberForApparel(
            license: String,
            userId: String? = null,
            baseUri: Uri = defaultBaseUri,
            sceneUri: Uri = defaultApparelSceneUri,
            renderTarget: EngineRenderTarget = EngineRenderTarget.SURFACE_VIEW,
        ): EngineConfiguration =
            EngineConfiguration(
                license = license,
                userId = userId,
                baseUri = baseUri,
                renderTarget = renderTarget,
                onCreate = { ->
                    EditorDefaults.onCreate(editorContext.engine, sceneUri, editorContext.eventHandler)
                },
            )

        /**
         * A composable helper function that creates and remembers an [EngineConfiguration] instance when launching [PostcardEditor].
         * This function simplifies the initialization process, offering a default configuration ideal for a wide range of
         * applications. The essential requirement is the [license], with available extra parameters for further customization.
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
         * @param renderTarget the target which should be used by the [ly.img.engine.Engine] to render.
         * Default value is [EngineRenderTarget.SURFACE_VIEW].
         */
        @UnstableEditorApi
        @Composable
        fun rememberForPostcard(
            license: String,
            userId: String? = null,
            baseUri: Uri = defaultBaseUri,
            sceneUri: Uri = defaultPostcardSceneUri,
            renderTarget: EngineRenderTarget = EngineRenderTarget.SURFACE_VIEW,
        ): EngineConfiguration =
            EngineConfiguration(
                license = license,
                userId = userId,
                baseUri = baseUri,
                renderTarget = renderTarget,
                onCreate = { ->
                    EditorDefaults.onCreate(editorContext.engine, sceneUri, editorContext.eventHandler)
                },
            )

        /**
         * A composable helper function that creates and remembers an [EngineConfiguration] instance when launching [VideoEditor].
         * This function simplifies the initialization process, offering a default configuration ideal for a wide range of
         * applications. The essential requirement is the [license], with available extra parameters for further customization.
         *
         * @param license the license to activate the [ly.img.engine.Engine] with.
         * @param userId an optional unique ID tied to your application's user. This helps us accurately calculate monthly active users (MAU).
         * This is particularly beneficial for tracking users across multiple devices when they sign in, ensuring they are counted uniquely.
         * @param baseUri the foundational uri for constructing absolute paths from relative ones. For example, setting it to
         * the Android assets directory allows loading resources directly from there: file:///android_asset/.
         * This base uri enables the loading of specific scenes or assets using their relative paths.
         * @param sceneUri the specific scene uri to load content within the [VideoEditor]. This uri is passed to
         * [EditorDefaults.onCreate] to facilitate scene and asset loading at initialization.
         * @param renderTarget the target which should be used by the [ly.img.engine.Engine] to render.
         * Default value is [EngineRenderTarget.SURFACE_VIEW].
         */
        @UnstableEditorApi
        @Composable
        fun rememberForVideo(
            license: String,
            userId: String? = null,
            baseUri: Uri = defaultBaseUri,
            sceneUri: Uri = defaultVideoSceneUri,
            renderTarget: EngineRenderTarget = EngineRenderTarget.SURFACE_VIEW,
        ): EngineConfiguration =
            EngineConfiguration(
                license = license,
                userId = userId,
                baseUri = baseUri,
                renderTarget = renderTarget,
                onCreate = { ->
                    EditorDefaults.onCreate(editorContext.engine, sceneUri, editorContext.eventHandler)
                },
            )
    }

    @Deprecated("Use remember functions constructor instead. This constructor will be removed soon.")
    @UnstableEditorApi
    constructor(
        license: String,
        userId: String? = null,
        baseUri: Uri = defaultBaseUri,
        renderTarget: EngineRenderTarget = EngineRenderTarget.SURFACE_VIEW,
        onCreate: suspend (Engine, EditorEventHandler) -> Unit,
        onExport: suspend (Engine, EditorEventHandler) -> Unit = { engine, eventHandler ->
            EditorDefaults.onExport(engine, eventHandler)
        },
        onUpload: suspend AssetDefinition.(
            Engine,
            EditorEventHandler,
            UploadAssetSourceType,
        ) -> AssetDefinition = { _, _, _ -> this },
        onClose: suspend (Engine, Boolean, EditorEventHandler) -> Unit = { _, hasUnsavedChanges, eventHandler ->
            if (hasUnsavedChanges) {
                eventHandler.send(ShowCloseConfirmationDialogEvent)
            } else {
                eventHandler.send(EditorEvent.CloseEditor())
            }
        },
        onError: suspend (Throwable, Engine, EditorEventHandler) -> Unit = { error, _, eventHandler ->
            eventHandler.send(ShowErrorDialogEvent(error))
        },
    ) : this(
        license = license,
        userId = userId,
        baseUri = baseUri,
        renderTarget = renderTarget,
        onCreate = { onCreate(editorContext.engine, editorContext.eventHandler) },
        onExport = { onExport(editorContext.engine, editorContext.eventHandler) },
        onUpload = {
                asset,
                uploadAssetSourceType,
            ->
            onUpload(asset, editorContext.engine, editorContext.eventHandler, uploadAssetSourceType)
        },
        onClose = { hasUnsavedChanges -> onClose(editorContext.engine, hasUnsavedChanges, editorContext.eventHandler) },
        onError = { error -> onError(error, editorContext.engine, editorContext.eventHandler) },
    )
}
