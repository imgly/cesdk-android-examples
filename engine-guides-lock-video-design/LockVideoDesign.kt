import android.app.Application
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.GlobalScope
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode

suspend fun lockVideoDesign(
    application: Application,
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
) = withContext(Dispatchers.Main) {
    Engine.init(application)
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1280, height = 720)

    try {
        val scene = engine.scene.createForVideo()
        val page = engine.block.create(DesignBlockType.Page)
        engine.block.appendChild(parent = scene, child = page)
        engine.block.setWidth(page, value = 1280F)
        engine.block.setHeight(page, value = 720F)
        engine.block.setDuration(page, duration = 12.0)

        val track = engine.block.create(DesignBlockType.Track)
        engine.block.appendChild(parent = page, child = track)

        val videoClip = engine.block.create(DesignBlockType.Graphic)
        engine.block.setShape(videoClip, shape = engine.block.createShape(ShapeType.Rect))
        engine.block.setDuration(videoClip, duration = 12.0)
        val videoFill = engine.block.createFill(FillType.Video)
        // Video source URIs are currently set through the fill property key.
        engine.block.setUri(
            block = videoFill,
            property = "fill/video/fileURI",
            value = Uri.parse(
                "https://cdn.img.ly/assets/demo/v3/ly.img.video/videos/pexels-kampus-production-8154913.mp4",
            ),
        )
        engine.block.setFill(videoClip, fill = videoFill)
        engine.block.appendChild(parent = track, child = videoClip)
        engine.block.fillParent(track)

        val titleOverlay = engine.block.create(DesignBlockType.Text)
        engine.block.appendChild(parent = page, child = titleOverlay)
        engine.block.setWidthMode(titleOverlay, mode = SizeMode.AUTO)
        engine.block.setHeightMode(titleOverlay, mode = SizeMode.AUTO)
        engine.block.setPositionX(titleOverlay, value = 80F)
        engine.block.setPositionY(titleOverlay, value = 80F)
        engine.block.setDuration(titleOverlay, duration = 12.0)
        engine.block.replaceText(titleOverlay, text = "Editable title")

        val watermarkOverlay = engine.block.create(DesignBlockType.Text)
        engine.block.appendChild(parent = page, child = watermarkOverlay)
        engine.block.setWidthMode(watermarkOverlay, mode = SizeMode.AUTO)
        engine.block.setHeightMode(watermarkOverlay, mode = SizeMode.AUTO)
        engine.block.setPositionX(watermarkOverlay, value = 980F)
        engine.block.setPositionY(watermarkOverlay, value = 640F)
        engine.block.setDuration(watermarkOverlay, duration = 12.0)
        engine.block.replaceText(watermarkOverlay, text = "LOCKED")

        // highlight-android-lock-video-design
        val scopes = engine.editor.findAllScopes()
        scopes.forEach { scope ->
            engine.editor.setGlobalScope(key = scope, globalScope = GlobalScope.DENY)
        }
        // highlight-android-lock-video-design

        // highlight-android-enable-selection
        engine.editor.setGlobalScope(key = "editor/select", globalScope = GlobalScope.DEFER)
        engine.block.setScopeEnabled(videoClip, key = "editor/select", enabled = true)
        engine.block.setScopeEnabled(titleOverlay, key = "editor/select", enabled = true)
        engine.block.setScopeEnabled(watermarkOverlay, key = "editor/select", enabled = false)
        // highlight-android-enable-selection

        // highlight-android-text-overlay-editing
        engine.editor.setGlobalScope(key = "text/edit", globalScope = GlobalScope.DEFER)
        engine.editor.setGlobalScope(key = "text/character", globalScope = GlobalScope.DEFER)
        engine.block.setScopeEnabled(titleOverlay, key = "text/edit", enabled = true)
        engine.block.setScopeEnabled(titleOverlay, key = "text/character", enabled = true)
        // highlight-android-text-overlay-editing

        // highlight-android-video-replacement
        engine.editor.setGlobalScope(key = "fill/change", globalScope = GlobalScope.DEFER)
        engine.block.setScopeEnabled(videoClip, key = "fill/change", enabled = true)
        // highlight-android-video-replacement

        // highlight-android-layout-adjustments
        engine.editor.setGlobalScope(key = "layer/move", globalScope = GlobalScope.DEFER)
        engine.editor.setGlobalScope(key = "layer/resize", globalScope = GlobalScope.DEFER)
        engine.editor.setGlobalScope(key = "layer/rotate", globalScope = GlobalScope.DEFER)
        engine.block.setScopeEnabled(titleOverlay, key = "layer/move", enabled = true)
        engine.block.setScopeEnabled(titleOverlay, key = "layer/resize", enabled = true)
        engine.block.setScopeEnabled(titleOverlay, key = "layer/rotate", enabled = true)
        // highlight-android-layout-adjustments

        // highlight-android-protect-overlay
        val lockedOverlayScopes = listOf(
            "editor/select",
            "text/edit",
            "text/character",
            "fill/change",
            "layer/move",
            "layer/resize",
            "layer/rotate",
            "lifecycle/destroy",
        )
        lockedOverlayScopes.forEach { scope ->
            engine.block.setScopeEnabled(watermarkOverlay, key = scope, enabled = false)
        }
        // highlight-android-protect-overlay

        // highlight-android-check-permissions
        val canSelectVideoClip = engine.block.isAllowedByScope(videoClip, key = "editor/select")
        val canReplaceVideoClip = engine.block.isAllowedByScope(videoClip, key = "fill/change")
        val canMoveVideoClip = engine.block.isAllowedByScope(videoClip, key = "layer/move")
        val canEditTitle = engine.block.isAllowedByScope(titleOverlay, key = "text/edit")
        val canMoveTitle = engine.block.isAllowedByScope(titleOverlay, key = "layer/move")
        val canSelectWatermark = engine.block.isAllowedByScope(watermarkOverlay, key = "editor/select")
        val titleTextScopeEnabled = engine.block.isScopeEnabled(titleOverlay, key = "text/edit")
        val textEditGlobalScope = engine.editor.getGlobalScope(key = "text/edit")

        require(canSelectVideoClip)
        require(canReplaceVideoClip)
        require(!canMoveVideoClip)
        require(canEditTitle)
        require(canMoveTitle)
        require(!canSelectWatermark)
        require(titleTextScopeEnabled)
        require(textEditGlobalScope == GlobalScope.DEFER)
        // highlight-android-check-permissions

        // highlight-android-discover-scopes
        val availableScopes = engine.editor.findAllScopes()
        val currentScopeSettings = availableScopes.associateWith { scope ->
            engine.editor.getGlobalScope(key = scope)
        }
        require("editor/select" in availableScopes)
        require("fill/change" in availableScopes)
        require(currentScopeSettings["editor/select"] == GlobalScope.DEFER)
        // highlight-android-discover-scopes
    } finally {
        engine.stop()
    }
}
