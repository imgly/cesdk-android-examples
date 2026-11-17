import android.net.Uri
import android.util.Log
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.Engine
import ly.img.engine.MimeType
import ly.img.engine.SceneMarker
import java.nio.ByteBuffer

private const val TAG = "ImportLottie"

data class ImportLottie(
    val groups: List<DesignBlock>,
    val layerNames: List<String>,
    val ringAnimatedProperties: List<String>,
    val brandColor: Color,
    val markers: List<SceneMarker>,
    val previewPng: ByteBuffer,
)

// The archive URI comes from the caller, for example the badge.imgly that the Node.js example of
// this guide saves, copied to a file.
suspend fun importLottie(
    engine: Engine,
    archiveUri: Uri,
): ImportLottie {
    // highlight-android-load-archive
    // overrideEditorConfig = true also loads the variables that the importer created for the slots.
    engine.scene.load(
        sceneUri = archiveUri,
        overrideEditorConfig = true,
        waitForResources = true,
    )
    // highlight-android-load-archive

    // highlight-android-imported-blocks
    // Each import is one group on the page. Its blocks carry the names of the Lottie layers.
    val page = engine.scene.getPages().first()
    val groups = engine.block.getChildren(page)
    val layerNames = engine.block.getChildren(groups.first()).map { engine.block.getName(it) }
    Log.i(TAG, "Imported layers: $layerNames")

    // The animation of a layer is a set of keyframe tracks on its block.
    val ring = engine.block.findByName("Ring").first()
    val ringAnimatedProperties = engine.block.getAnimatedProperties(ring)
    Log.i(TAG, "Animated properties of the ring: $ringAnimatedProperties")
    // highlight-android-imported-blocks

    // highlight-android-slots
    // The "brand" slot became a color variable. Setting it recolors every fill that uses the slot.
    engine.variable.setColor(
        key = "brand",
        value = Color.fromRGBA(r = 0.13F, g = 0.55F, b = 0.45F, a = 1F),
    )
    val brandColor = engine.variable.getColor(key = "brand")
    // highlight-android-slots

    // highlight-android-markers
    // Lottie markers became scene markers, in seconds.
    val markers = engine.scene.getMarkers()
    markers.forEach { marker ->
        Log.i(TAG, "Marker \"${marker.name}\" at ${marker.time}s for ${marker.duration}s")
    }
    // highlight-android-markers

    val previewPng = engine.block.export(block = page, mimeType = MimeType.PNG)
    return ImportLottie(
        groups = groups,
        layerNames = layerNames,
        ringAnimatedProperties = ringAnimatedProperties,
        brandColor = brandColor,
        markers = markers,
        previewPng = previewPng,
    )
}
