package ly.img.editor.base.dock.options.format

sealed interface Alignment

enum class HorizontalAlignment : Alignment {
    Left,
    Center,
    Right,
}

enum class VerticalAlignment : Alignment {
    Top,
    Center,
    Bottom,
}
