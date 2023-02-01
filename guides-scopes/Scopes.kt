import android.net.Uri
import kotlinx.coroutines.*
import ly.img.engine.*
import ly.img.engine.GlobalScope

fun scopes() = CoroutineScope(Dispatchers.Main).launch {
	val engine = Engine.also { it.start() }
	engine.bindOffscreen(width = 100, height = 100)

	// highlight-setup
	val scene = engine.scene.createFromImage(Uri.parse("https://img.ly/static/ubq_samples/imgly_logo.jpg"))
	val image = engine.block.findByType(DesignBlockType.IMAGE).first()
	// highlight-setup

	// highlight-setGlobalScope
	// Let the global scope defer to the block-level.
	engine.editor.setGlobalScope(key = "design/arrange", globalScope = GlobalScope.DEFER)

	// Manipulation of layout properties of any block will fail at this point.
	try {
		engine.block.setPositionX(image, value = 100F) // Not allowed
	} catch(exception: Exception) {
		exception.printStackTrace()
	}
	// highlight-setGlobalScope

	// highlight-getGlobalScope
	// This will return `GlobalScope.DEFER`.
	engine.editor.getGlobalScope(key = "design/arrange")
	// highlight-getGlobalScope

	// highlight-setScopeEnabled
	// Allow the user to control the layout properties of the image block.
	engine.block.setScopeEnabled(image, key = "design/arrange", enabled = true)

	// Manipulation of layout properties of any block is now allowed.
	try {
		engine.block.setPositionX(image, value = 100F) // Allowed
	} catch(exception: Exception) {
		exception.printStackTrace()
	}
	// highlight-setScopeEnabled

	// highlight-isScopeEnabled
	// Verify that the "design/arrange" scope is now enabled for the image block.
	engine.block.isScopeEnabled(image, key = "design/arrange")
	// highlight-isScopeEnabled

	// highlight-isAllowedByScope
	// This will return true as well since the global scope is set to `GlobalScope.DEFER`.
	engine.block.isAllowedByScope(image, key = "design/arrange")
	// highlight-isAllowedByScope

	engine.stop()
}
