package ly.img.cesdk.dock.options.format

sealed interface Alignment

enum class HorizontalAlignment : Alignment {
    Left,
    Center,
    Right
}

enum class VerticalAlignment : Alignment {
    Top,
    Center,
    Bottom
}