import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.Engine

private const val ASSET_BASE_PATH = "file:///android_asset"

suspend fun uriResolver(engine: Engine): List<Uri> = withContext(Dispatchers.Main) {
    val originalBasePath = engine.editor.getSettingString("basePath")
    engine.editor.setSettingString(keypath = "basePath", value = ASSET_BASE_PATH)

    try {
        // highlight-android-test-resolution
        val relativeAsset = Uri.parse("images/poster.jpg")
        val protectedAsset = Uri.parse("https://assets.example.com/protected/poster.jpg")

        val defaultRelativeUri = engine.editor.getAbsoluteUri(uri = relativeAsset)
        val defaultAbsoluteUri = engine.editor.getAbsoluteUri(uri = protectedAsset)
        // highlight-android-test-resolution

        // highlight-android-set-resolver
        val appAssetHost = "app.example.com"
        val storageHost = "assets.example.com"
        val appAsset = Uri.parse("https://$appAssetHost/images/poster.jpg")

        engine.editor.setUriResolver { uri ->
            if (uri.host == appAssetHost) {
                uri
                    .buildUpon()
                    .authority(storageHost)
                    .build()
            } else {
                engine.editor.defaultUriResolver(uri)
            }
        }

        val rewrittenAppAssetUri = engine.editor.getAbsoluteUri(uri = appAsset)
        // highlight-android-set-resolver

        // highlight-android-authentication
        val protectedHost = "assets.example.com"
        val protectedPrefix = "/protected/"
        val token = "precomputed-session-token"

        engine.editor.setUriResolver { uri ->
            val shouldAuthenticate =
                uri.host == protectedHost &&
                    uri.path?.startsWith(protectedPrefix) == true

            if (shouldAuthenticate) {
                uri
                    .buildUpon()
                    .appendQueryParameter("token", token)
                    .build()
            } else {
                engine.editor.defaultUriResolver(uri)
            }
        }

        val authenticatedUri = engine.editor.getAbsoluteUri(uri = protectedAsset)
        val delegatedRelativeUri = engine.editor.getAbsoluteUri(uri = relativeAsset)
        // highlight-android-authentication

        // highlight-android-remove-resolver
        engine.editor.setUriResolver(null)
        val restoredRelativeUri = engine.editor.getAbsoluteUri(uri = relativeAsset)
        // highlight-android-remove-resolver

        listOf(
            defaultRelativeUri,
            defaultAbsoluteUri,
            rewrittenAppAssetUri,
            authenticatedUri,
            delegatedRelativeUri,
            restoredRelativeUri,
        )
    } finally {
        engine.editor.setUriResolver(null)
        engine.editor.setUriResolverAsync(null)
        engine.editor.setSettingString(keypath = "basePath", value = originalBasePath)
    }
}
