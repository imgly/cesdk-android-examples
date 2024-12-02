package ly.img.editor

import android.net.Uri
import android.util.SizeF
import androidx.compose.runtime.Composable
import ly.img.editor.core.UnstableEditorApi
import ly.img.editor.core.engine.EngineRenderTarget
import ly.img.engine.MimeType

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
fun EngineConfiguration.Companion.rememberForDesign(
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
        onCreate = {
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
fun EngineConfiguration.Companion.rememberForPhoto(
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
        onCreate = {
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
fun EngineConfiguration.Companion.rememberForApparel(
    license: String,
    userId: String? = null,
    baseUri: Uri = defaultBaseUri,
    sceneUri: Uri = defaultApparelSceneUri,
    renderTarget: EngineRenderTarget = EngineRenderTarget.SURFACE_VIEW,
): EngineConfiguration =
    remember(
        license = license,
        userId = userId,
        baseUri = baseUri,
        renderTarget = renderTarget,
        onCreate = {
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
fun EngineConfiguration.Companion.rememberForPostcard(
    license: String,
    userId: String? = null,
    baseUri: Uri = defaultBaseUri,
    sceneUri: Uri = defaultPostcardSceneUri,
    renderTarget: EngineRenderTarget = EngineRenderTarget.SURFACE_VIEW,
): EngineConfiguration =
    remember(
        license = license,
        userId = userId,
        baseUri = baseUri,
        renderTarget = renderTarget,
        onCreate = {
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
fun EngineConfiguration.Companion.rememberForVideo(
    license: String,
    userId: String? = null,
    baseUri: Uri = defaultBaseUri,
    sceneUri: Uri = defaultVideoSceneUri,
    renderTarget: EngineRenderTarget = EngineRenderTarget.SURFACE_VIEW,
): EngineConfiguration =
    remember(
        license = license,
        userId = userId,
        baseUri = baseUri,
        renderTarget = renderTarget,
        onCreate = {
            EditorDefaults.onCreate(editorContext.engine, sceneUri, editorContext.eventHandler)
        },
    )
