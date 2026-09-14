import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine

fun storeMetadata(
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1080, height = 1920)

    // highlight-setup
    var scene = engine.scene.createFromImage(
        Uri.parse("https://img.ly/static/ubq_samples/imgly_logo.jpg"),
    )
    val block = engine.block.findByType(DesignBlockType.Page).first()
    // highlight-setup

    // highlight-setMetadata
    engine.block.setMetadata(scene, key = "author", value = "img.ly")
    engine.block.setMetadata(block, key = "customer_id", value = "1234567890")
    engine.block.setMetadata(block, key = "customer_name", value = "Name")
    // highlight-setMetadata

    // highlight-getMetadata
    // This will return "img.ly"
    engine.block.getMetadata(scene, key = "author")

    // This will return "1000000"
    engine.block.getMetadata(block, key = "customer_id")
    // highlight-getMetadata

    // highlight-findAllMetadata
    // This will return ["customer_id", "customer_name"]
    engine.block.findAllMetadata(block)
    // highlight-findAllMetadata

    // highlight-removeMetadata
    engine.block.removeMetadata(block, key = "customer_id")

    // This will return false
    engine.block.hasMetadata(block, key = "customer_id")
    // highlight-removeMetadata

    // highlight-persistence
    // We save our scene and reload it from scratch
    val sceneString = engine.scene.saveToString(scene)
    scene = engine.scene.load(scene = sceneString)

    // This still returns "img.ly"
    engine.block.getMetadata(scene, key = "author")

    // And this still returns "Name"
    engine.block.getMetadata(block, key = "customer_name")
    // highlight-persistence

    engine.stop()
}
