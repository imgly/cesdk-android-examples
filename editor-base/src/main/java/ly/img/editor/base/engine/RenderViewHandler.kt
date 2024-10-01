package ly.img.editor.base.engine

import android.view.SurfaceHolder
import android.view.SurfaceView

/**
 * Handles the addition and removal of surface size callbacks for a given [SurfaceView].
 */
class RenderViewHandler(private val view: SurfaceView) {
    private var surfaceCallback: SurfaceHolder.Callback? = null

    /** Adds a callback to be notified of surface size changes.
     *
     * @param onSizeChanged A function to be called when the surface size changes.
     */
    fun addCallback(onSizeChanged: () -> Unit) {
        val runnable = Runnable { onSizeChanged() }
        surfaceCallback =
            object : SurfaceHolder.Callback {
                override fun surfaceCreated(holder: SurfaceHolder) {
                }

                override fun surfaceChanged(
                    holder: SurfaceHolder,
                    format: Int,
                    width: Int,
                    height: Int,
                ) {
                    view.handler.removeCallbacks(runnable)
                    view.handler.post(runnable)
                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {
                }
            }
        view.holder.addCallback(surfaceCallback)
    }

    /**
     * Removes the previously added callback.
     */
    fun removeCallback() {
        surfaceCallback?.let { view.holder.removeCallback(it) }
        surfaceCallback = null
    }
}
