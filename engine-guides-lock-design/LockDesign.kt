import kotlinx.coroutines.withContext
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.GlobalScope
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode

suspend fun lockDesign(
    engine: Engine,
    restoreGlobalScopes: Boolean = false,
) = withContext(engine.dispatcher) {
    val previousGlobalScopes = if (restoreGlobalScopes) {
        engine.editor.findAllScopes().associateWith { scope ->
            engine.editor.getGlobalScope(key = scope)
        }
    } else {
        emptyMap()
    }

    try {
        val scene = engine.scene.create()

        val page = engine.block.create(DesignBlockType.Page)
        engine.block.setWidth(page, value = 800F)
        engine.block.setHeight(page, value = 600F)
        engine.block.appendChild(parent = scene, child = page)

        val textBlock = engine.block.create(DesignBlockType.Text)
        engine.block.appendChild(parent = page, child = textBlock)
        engine.block.setWidthMode(textBlock, mode = SizeMode.AUTO)
        engine.block.setHeightMode(textBlock, mode = SizeMode.AUTO)
        engine.block.replaceText(textBlock, text = "Editable headline")

        val imageBlock = engine.block.create(DesignBlockType.Graphic)
        engine.block.setShape(imageBlock, shape = engine.block.createShape(ShapeType.Rect))
        engine.block.setPositionY(imageBlock, value = 160F)
        engine.block.setWidth(imageBlock, value = 300F)
        engine.block.setHeight(imageBlock, value = 200F)
        val imageFill = engine.block.createFill(FillType.Image)
        engine.block.setString(
            block = imageFill,
            property = "fill/image/imageFileURI",
            value = "https://img.ly/static/ubq_samples/sample_1.jpg",
        )
        engine.block.setFill(imageBlock, fill = imageFill)
        engine.block.appendChild(parent = page, child = imageBlock)
        engine.block.forceLoadResources(listOf(textBlock, imageBlock))

        // highlight-android-lock-entire-design
        val scopes = engine.editor.findAllScopes()
        scopes.forEach { scope ->
            engine.editor.setGlobalScope(key = scope, globalScope = GlobalScope.DENY)
        }
        // highlight-android-lock-entire-design

        // highlight-android-enable-selection
        engine.editor.setGlobalScope(key = "editor/select", globalScope = GlobalScope.DEFER)
        engine.block.setScopeEnabled(textBlock, key = "editor/select", enabled = true)
        engine.block.setScopeEnabled(imageBlock, key = "editor/select", enabled = true)
        // highlight-android-enable-selection

        // highlight-android-text-editing
        engine.editor.setGlobalScope(key = "text/edit", globalScope = GlobalScope.DEFER)
        engine.editor.setGlobalScope(key = "text/character", globalScope = GlobalScope.DEFER)
        engine.editor.setGlobalScope(key = "fill/change", globalScope = GlobalScope.DEFER)
        engine.block.setScopeEnabled(textBlock, key = "text/edit", enabled = true)
        engine.block.setScopeEnabled(textBlock, key = "text/character", enabled = true)
        engine.block.setScopeEnabled(textBlock, key = "fill/change", enabled = true)
        // highlight-android-text-editing

        // highlight-android-image-replacement
        engine.editor.setGlobalScope(key = "fill/change", globalScope = GlobalScope.DEFER)
        engine.block.setScopeEnabled(imageBlock, key = "fill/change", enabled = true)
        // highlight-android-image-replacement

        // highlight-android-position-adjustments
        engine.editor.setGlobalScope(key = "layer/move", globalScope = GlobalScope.DEFER)
        engine.editor.setGlobalScope(key = "layer/resize", globalScope = GlobalScope.DEFER)
        engine.editor.setGlobalScope(key = "layer/rotate", globalScope = GlobalScope.DEFER)
        engine.block.setScopeEnabled(imageBlock, key = "layer/move", enabled = true)
        engine.block.setScopeEnabled(imageBlock, key = "layer/resize", enabled = true)
        engine.block.setScopeEnabled(imageBlock, key = "layer/rotate", enabled = true)
        // highlight-android-position-adjustments

        // highlight-android-check-permissions
        val canEditText = engine.block.isAllowedByScope(textBlock, key = "text/edit")
        val canChangeTextColor = engine.block.isAllowedByScope(textBlock, key = "fill/change")
        val canMoveText = engine.block.isAllowedByScope(textBlock, key = "layer/move")
        val canMoveImage = engine.block.isAllowedByScope(imageBlock, key = "layer/move")
        val canResizeImage = engine.block.isAllowedByScope(imageBlock, key = "layer/resize")
        val canRotateImage = engine.block.isAllowedByScope(imageBlock, key = "layer/rotate")
        val textEditScopeEnabled = engine.block.isScopeEnabled(textBlock, key = "text/edit")
        val textEditGlobalScope = engine.editor.getGlobalScope(key = "text/edit")
        require(canEditText)
        require(canChangeTextColor)
        require(!canMoveText)
        require(canMoveImage)
        require(canResizeImage)
        require(canRotateImage)
        require(textEditScopeEnabled)
        require(textEditGlobalScope == GlobalScope.DEFER)
        // highlight-android-check-permissions

        // highlight-android-get-scopes
        val availableScopes = engine.editor.findAllScopes()
        val currentScopeSettings = availableScopes.associateWith { scope ->
            engine.editor.getGlobalScope(key = scope)
        }
        require("text/edit" in availableScopes)
        require(currentScopeSettings["text/edit"] == GlobalScope.DEFER)
        // highlight-android-get-scopes
    } finally {
        previousGlobalScopes.forEach { (scope, globalScope) ->
            engine.editor.setGlobalScope(key = scope, globalScope = globalScope)
        }
    }
}
