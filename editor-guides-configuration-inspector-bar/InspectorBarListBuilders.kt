import androidx.compose.runtime.Composable
import ly.img.editor.core.component.InspectorBar
import ly.img.editor.core.component.rememberAdjustments
import ly.img.editor.core.component.rememberBlur
import ly.img.editor.core.component.rememberCrop
import ly.img.editor.core.component.rememberDelete
import ly.img.editor.core.component.rememberDuplicate
import ly.img.editor.core.component.rememberEditText
import ly.img.editor.core.component.rememberEffect
import ly.img.editor.core.component.rememberEnterGroup
import ly.img.editor.core.component.rememberFillStroke
import ly.img.editor.core.component.rememberFilter
import ly.img.editor.core.component.rememberFormatText
import ly.img.editor.core.component.rememberLayer
import ly.img.editor.core.component.rememberMoveAsClip
import ly.img.editor.core.component.rememberMoveAsOverlay
import ly.img.editor.core.component.rememberReorder
import ly.img.editor.core.component.rememberReplace
import ly.img.editor.core.component.rememberSelectGroup
import ly.img.editor.core.component.rememberShape
import ly.img.editor.core.component.rememberSplit
import ly.img.editor.core.component.rememberVolume

// highlight-listBuilders
@Composable
fun InspectorBar.ListBuilder.remember() = InspectorBar.ListBuilder.remember {
    add { InspectorBar.Button.rememberReplace() } // Video, Image, Sticker, Audio
    add { InspectorBar.Button.rememberEditText() } // Text
    add { InspectorBar.Button.rememberFormatText() } // Text
    add { InspectorBar.Button.rememberFillStroke() } // Page, Video, Image, Shape, Text
    add { InspectorBar.Button.rememberVolume() } // Video, Audio
    add { InspectorBar.Button.rememberCrop() } // Video, Image
    add { InspectorBar.Button.rememberAdjustments() } // Video, Image
    add { InspectorBar.Button.rememberFilter() } // Video, Image
    add { InspectorBar.Button.rememberEffect() } // Video, Image
    add { InspectorBar.Button.rememberBlur() } // Video, Image
    add { InspectorBar.Button.rememberShape() } // Video, Image, Shape
    add { InspectorBar.Button.rememberSelectGroup() } // Video, Image, Sticker, Shape, Text
    add { InspectorBar.Button.rememberEnterGroup() } // Group
    add { InspectorBar.Button.rememberLayer() } // Video, Image, Sticker, Shape, Text
    add { InspectorBar.Button.rememberSplit() } // Video, Image, Sticker, Shape, Text, Audio
    add { InspectorBar.Button.rememberMoveAsClip() } // Video, Image, Sticker, Shape, Text
    add { InspectorBar.Button.rememberMoveAsOverlay() } // Video, Image, Sticker, Shape, Text
    add { InspectorBar.Button.rememberReorder() } // Video, Image, Sticker, Shape, Text
    add { InspectorBar.Button.rememberDuplicate() } // Video, Image, Sticker, Shape, Text, Audio
    add { InspectorBar.Button.rememberDelete() } // Video, Image, Sticker, Shape, Text, Audio
}
// highlight-listBuilders
