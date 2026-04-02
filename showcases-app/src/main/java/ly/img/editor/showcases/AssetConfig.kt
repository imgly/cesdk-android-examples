package ly.img.editor.showcases

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import ly.img.editor.featureFlag.flags.RemoteAssetsFeature

object AssetConfig {
    /**
     * Resolves the base URI using the following cascade:
     * 1. If RemoteAssetsFeature is enabled → skip local bundle (return remote or null)
     * 2. If local assets exist at imgly-assets/ → file:///android_asset/imgly-assets
     * 3. If BASE_URI BuildConfig is non-empty → use it (branch-deployed remote)
     * 4. Else → null (EngineConfiguration falls back to CDN default)
     */
    fun baseUri(context: Context): Uri? {
        if (!RemoteAssetsFeature.enabled && hasLocalAssets(context)) {
            return "file:///android_asset/imgly-assets".toUri()
        }
        return ShowcasesBuildConfig.BASE_URI.takeIf { it.isNotEmpty() }?.toUri()
    }

    private fun hasLocalAssets(context: Context): Boolean =
        runCatching { context.assets.list("imgly-assets")?.isNotEmpty() == true }.getOrDefault(false)
}
