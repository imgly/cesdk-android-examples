package ly.img.cesdk.engine

object Scope {
    const val FillChange = "fill/change"
    const val StrokeChange = "stroke/change"
    const val ShapeChange = "shape/change"

    const val LayerMove = "layer/move"
    const val LayerResize = "layer/resize"
    const val LayerRotate = "layer/rotate"
    const val LayerFlip = "layer/flip"
    const val LayerCrop = "layer/crop"
    const val LayerOpacity = "layer/opacity"
    const val LayerBlendMode = "layer/blendMode"
    const val LayerClipping = "layer/clipping"
    const val LayerVisibility = "layer/visibility"

    const val TextEdit = "text/edit"
    const val TextCharacter = "text/character"

    const val EditorAdd = "editor/add"
    const val EditorSelect = "editor/select"

    const val LifecycleDuplicate = "lifecycle/duplicate"
    const val LifecycleDestroy = "lifecycle/destroy"
}