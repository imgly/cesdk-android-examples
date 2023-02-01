package ly.img.cesdk.showcase

import android.app.Activity
import android.os.Bundle
import android.widget.FrameLayout

class ShowcaseActivity : Activity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(FrameLayout(this))
		// Use this as a place to call engine guides functions, such as createSceneFromImageURL()
	}
}
