@file:Suppress("UnusedReceiverParameter")

package ly.img.editor.configuration.apparel.component

import androidx.compose.runtime.Composable
import ly.img.editor.configuration.apparel.ApparelConfigurationBuilder
import ly.img.editor.core.component.InspectorBar
import ly.img.editor.core.component.remember
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
import ly.img.editor.core.component.rememberReplace
import ly.img.editor.core.component.rememberSelectGroup
import ly.img.editor.core.component.rememberShape
import ly.img.editor.core.component.rememberTextBackground

/**
 * The configuration of the component that is displayed as horizontal list of items at the
 * bottom of the editor when a design block is selected.
 */
@Composable
fun ApparelConfigurationBuilder.rememberInspectorBar() = InspectorBar.remember {
    listBuilder = {
        InspectorBar.ListBuilder.remember {
            add { InspectorBar.Button.rememberReplace() } // Image, Sticker, Audio
            add { InspectorBar.Button.rememberEditText() } // Text
            add { InspectorBar.Button.rememberFormatText() } // Text
            add { InspectorBar.Button.rememberFillStroke() } // Page, Image, Shape, Text
            add { InspectorBar.Button.rememberTextBackground() } // Text
            add { InspectorBar.Button.rememberCrop() } // Image
            add { InspectorBar.Button.rememberAdjustments() } // Image
            add { InspectorBar.Button.rememberFilter() } // Image
            add { InspectorBar.Button.rememberEffect() } // Image
            add { InspectorBar.Button.rememberBlur() } // Image
            add { InspectorBar.Button.rememberShape() } // Image, Shape
            add { InspectorBar.Button.rememberSelectGroup() } // Image, Sticker, Shape, Text
            add { InspectorBar.Button.rememberEnterGroup() } // Group
            add { InspectorBar.Button.rememberLayer() } // Image, Sticker, Shape, Text
            add { InspectorBar.Button.rememberDuplicate() } // Image, Sticker, Shape, Text
            add { InspectorBar.Button.rememberDelete() } // Image, Sticker, Shape, Text
        }
    }
}
