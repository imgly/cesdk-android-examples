import android.net.Uri
import ly.img.engine.Engine

fun uriResolver() {
	val engine = Engine.getInstance(id = "ly.img.engine.example")
	engine.start()
	engine.bindOffscreen(width = 100, height = 100)

	// highlight-get-absolute-base-path
	// This will return uri to "banana.jpg" asset file
	engine.editor.getAbsoluteUri(uri = Uri.parse("banana.jpg"))
	// This will return uri to remote resource "https://example.com/orange.png"
	engine.editor.getAbsoluteUri(uri = Uri.parse("https://example.com/orange.png"))
	// highlight-get-absolute-base-path

	// highlight-resolver
	// Replace all .jpg files with the IMG.LY logo!
	engine.editor.setUriResolver {
		if (it.toString().endsWith(".jpg")) {
			Uri.parse("https://img.ly/static/ubq_samples/imgly_logo.jpg")
		} else {
			engine.editor.defaultUriResolver(it)
		}
	}
	// highlight-resolver

	// highlight-get-absolute-custom
	// The custom resolver will return a path to the IMG.LY logo because the given path ends with ".jpg".
	// This applies regardless if the given path is relative or absolute.
	engine.editor.getAbsoluteUri(uri = Uri.parse("banana.jpg"))

	// The custom resolver will not modify this path because it ends with ".png".
	engine.editor.getAbsoluteUri(uri = Uri.parse("https://example.com/orange.png"))

	// Because a custom resolver is set, relative paths that the resolver does not transform remain unmodified!
	engine.editor.getAbsoluteUri(uri = Uri.parse("/orange.png"))
	// highlight-get-absolute-custom

	// highlight-remove-resolver
	// Removes the previously set resolver.
	engine.editor.setUriResolver(null)

	// Since we"ve removed the custom resolver, this will return
	// Uri.Asset("banana.jpg") like before.
	engine.editor.getAbsoluteUri(uri = Uri.parse("banana.jpg"))
	// highlight-remove-resolver

	engine.editor.setUriResolver { uri ->
		val path = uri.path
		if (uri.host == "localhost" && path != null && path.startsWith("/scenes") && !path.endsWith(".scene")) {
			// Apply custom logic here, e.g. redirect to a different server
		}
		engine.editor.defaultUriResolver(uri)
	}

	engine.stop()
}
