package ly.img.editor.core.engine

/**
 * Configures which android view should be used for [ly.img.engine.Engine] rendering.
 */
enum class EngineRenderTarget {
    /**
     * For rendering on [android.view.SurfaceView].
     */
    SURFACE_VIEW,

    /**
     * For rendering on [android.view.TextureView].
     */
    TEXTURE_VIEW,
}
