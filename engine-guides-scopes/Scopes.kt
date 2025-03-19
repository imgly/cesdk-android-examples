import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.GlobalScope

fun scopes(
    license: String,
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 100, height = 100)

    // highlight-setup
    engine.scene.createFromImage(Uri.parse("https://img.ly/static/ubq_samples/imgly_logo.jpg"))
    val block = engine.block.findByType(DesignBlockType.Graphic).first()
    // highlight-setup

    // highlight-findAllScopes
    val scopes = engine.editor.findAllScopes()
    // highlight-findAllScopes

    // highlight-setGlobalScope
    // Let the global scope defer to the block-level.
    engine.editor.setGlobalScope(key = "layer/move", globalScope = GlobalScope.DEFER)

    // Manipulation of layout properties of any block will fail at this point.
    try {
        engine.block.setPositionX(block, value = 100F) // Not allowed
    } catch (exception: Exception) {
        exception.printStackTrace()
    }
    // highlight-setGlobalScope

    // highlight-getGlobalScope
    // This will return `GlobalScope.DEFER`.
    engine.editor.getGlobalScope(key = "layer/move")
    // highlight-getGlobalScope

    // highlight-setScopeEnabled
    // Allow the user to control the layout properties of the image block.
    engine.block.setScopeEnabled(block, key = "layer/move", enabled = true)

    // Manipulation of layout properties of any block is now allowed.
    try {
        engine.block.setPositionX(block, value = 100F) // Allowed
    } catch (exception: Exception) {
        exception.printStackTrace()
    }
    // highlight-setScopeEnabled

    // highlight-isScopeEnabled
    // Verify that the "layer/move" scope is now enabled for the image block.
    engine.block.isScopeEnabled(block, key = "layer/move")
    // highlight-isScopeEnabled

    // highlight-isAllowedByScope
    // This will return true as well since the global scope is set to `GlobalScope.DEFER`.
    engine.block.isAllowedByScope(block, key = "layer/move")
    // highlight-isAllowedByScope

    engine.stop()
}
