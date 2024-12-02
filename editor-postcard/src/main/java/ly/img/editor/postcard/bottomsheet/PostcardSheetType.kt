package ly.img.editor.postcard.bottomsheet

import ly.img.editor.core.sheet.SheetStyle
import ly.img.editor.core.sheet.SheetType

sealed interface PostcardSheetType : SheetType {
    /**
     * A sheet that is used to control template colors in [ly.img.editor.PostcardEditor].
     *
     * @param style the style that should be used to display the sheet.
     * Default value is the default [SheetStyle].
     */
    class TemplateColors(
        override val style: SheetStyle = SheetStyle(),
    ) : PostcardSheetType

    /**
     * A sheet that is used to control fonts in [ly.img.editor.PostcardEditor].
     *
     * @param style the style that should be used to display the sheet.
     * Default value is the default [SheetStyle].
     */
    class Font(
        override val style: SheetStyle = SheetStyle(),
    ) : PostcardSheetType

    /**
     * A sheet that is used to control sizes in [ly.img.editor.PostcardEditor].
     *
     * @param style the style that should be used to display the sheet.
     * Default value is the default [SheetStyle].
     */
    class Size(
        override val style: SheetStyle = SheetStyle(),
    ) : PostcardSheetType

    /**
     * A sheet that is used to control colors in [ly.img.editor.PostcardEditor].
     *
     * @param style the style that should be used to display the sheet.
     * Default value is the default [SheetStyle].
     */
    class Color(
        override val style: SheetStyle = SheetStyle(),
    ) : PostcardSheetType
}
