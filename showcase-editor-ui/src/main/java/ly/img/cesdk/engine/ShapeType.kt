package ly.img.cesdk.engine

import ly.img.engine.BlockApi
import ly.img.engine.DesignBlock

enum class ShapeType(val key: String) {
    Star("//ly.img.ubq/shape/star"),
    Polygon("//ly.img.ubq/shape/polygon"),
    Line("//ly.img.ubq/shape/line"),
    Other("other")
}

fun BlockApi.getShapeType(designBlock: DesignBlock): ShapeType {
    val shape = this.getShape(designBlock)
    val shapeType = this.getType(shape)
    for (type in ShapeType.values()) {
        if (type.key == shapeType) return type
    }
    return ShapeType.Other
}