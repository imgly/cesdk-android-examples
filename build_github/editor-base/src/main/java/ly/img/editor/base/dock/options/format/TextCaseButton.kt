package ly.img.editor.base.dock.options.format

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.Preview
import ly.img.editor.base.R
import ly.img.editor.base.components.ToggleIconButton
import ly.img.editor.core.ui.iconpack.Capitalizecasing
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.iconpack.Lowercasing
import ly.img.editor.core.ui.iconpack.Nonecasing
import ly.img.editor.core.ui.iconpack.Uppercasing
import ly.img.editor.core.ui.library.data.font.FontData
import ly.img.engine.TextCase

@Composable
fun TextCaseButton(
    casing: TextCase,
    currentCasing: TextCase,
    fontData: FontData? = null,
    changeCasing: (TextCase) -> Unit,
) {
    ToggleIconButton(
        checked = currentCasing == casing,
        onCheckedChange = {
            changeCasing(casing)
        },
    ) {
        val contentDescription =
            when (casing) {
                TextCase.NORMAL -> stringResource(R.string.ly_img_editor_letter_casing_none)
                TextCase.UPPER_CASE -> stringResource(R.string.ly_img_editor_letter_casing_uppercase)
                TextCase.LOWER_CASE -> stringResource(R.string.ly_img_editor_letter_casing_lowercase)
                TextCase.TITLE_CASE -> stringResource(R.string.ly_img_editor_letter_casing_capitalize)
            }

        Icon(
            imageVector =
                when (casing) {
                    TextCase.NORMAL -> IconPack.Nonecasing
                    TextCase.UPPER_CASE -> IconPack.Uppercasing
                    TextCase.LOWER_CASE -> IconPack.Lowercasing
                    TextCase.TITLE_CASE -> IconPack.Capitalizecasing
                },
            contentDescription = contentDescription,
        )
    }
}

@Composable
private fun Preview(
    textCase: TextCase,
    checked: Boolean,
) {
    TextCaseButton(
        casing = textCase,
        currentCasing =
            if (checked) {
                textCase
            } else if (textCase == TextCase.NORMAL) {
                TextCase.UPPER_CASE
            } else {
                TextCase.NORMAL
            },
        changeCasing = {},
    )
}

@Preview
@Composable
private fun PreviewAll() {
    Column {
        Row {
            Preview(TextCase.NORMAL, true)
            Preview(TextCase.LOWER_CASE, true)
            Preview(TextCase.UPPER_CASE, true)
            Preview(TextCase.TITLE_CASE, true)
        }
        Row {
            Preview(TextCase.NORMAL, false)
            Preview(TextCase.LOWER_CASE, false)
            Preview(TextCase.UPPER_CASE, false)
            Preview(TextCase.TITLE_CASE, false)
        }
    }
}
