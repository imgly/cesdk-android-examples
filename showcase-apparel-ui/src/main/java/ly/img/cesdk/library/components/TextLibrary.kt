package ly.img.cesdk.library.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ly.img.cesdk.apparelui.R
import ly.img.cesdk.core.components.SheetHeader
import ly.img.cesdk.library.LibraryViewModel
import ly.img.cesdk.library.data.font.DEFAULT_FONT_FAMILY
import ly.img.cesdk.library.data.font.FontFamilyData

@Composable
fun TextLibrary(
    title: String,
    onClose: () -> Unit,
    onTextPicked: (path: String, size: Float) -> Unit,
    viewModel: LibraryViewModel = viewModel(),
) {
    val onTextClick: ((path: String, size: Float) -> Unit) = remember {
        { path, size ->
            onClose()
            onTextPicked(path, size)
        }
    }

    Column {
        SheetHeader(
            title = title,
            onClose = onClose
        )

        val fontFamilies by viewModel.fontFamilies.collectAsState()
        val families: Map<String, FontFamilyData> = checkNotNull(fontFamilies).getOrThrow()
        LazyColumn(Modifier.fillMaxWidth()) {
            val sectionPaddingModifier = Modifier.padding(start = 16.dp, top = 14.dp)

            item {
                Text(
                    text = stringResource(R.string.cesdk_styles),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = sectionPaddingModifier
                )
            }
            val defaultFontFamilyData = checkNotNull(families[DEFAULT_FONT_FAMILY])
            item {
                TextRow(
                    text = stringResource(R.string.cesdk_add_title),
                    fontFamily = defaultFontFamilyData.fontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    onClick = {
                        onTextClick(defaultFontFamilyData.getFontData(FontWeight.Bold).fontPath, 32f)
                    }
                )
            }
            item {
                TextRow(
                    text = stringResource(R.string.cesdk_add_headline),
                    fontFamily = defaultFontFamilyData.fontFamily,
                    fontWeight = FontWeight.W500,
                    fontSize = 18.sp,
                    onClick = {
                        onTextClick(defaultFontFamilyData.getFontData(FontWeight.W500).fontPath, 18f)
                    }
                )
            }
            item {
                TextRow(
                    text = stringResource(R.string.cesdk_add_body),
                    fontFamily = defaultFontFamilyData.fontFamily,
                    fontWeight = FontWeight.W400,
                    fontSize = 14.sp,
                    onClick = {
                        onTextClick(defaultFontFamilyData.getFontData(FontWeight.W400).fontPath, 14f)
                    }
                )
            }

            item {
                Text(
                    text = stringResource(R.string.cesdk_fonts),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = sectionPaddingModifier
                )
            }

            families.forEach {
                item {
                    TextRow(
                        text = it.key,
                        fontFamily = it.value.fontFamily,
                        fontWeight = it.value.displayFont.fontWeight,
                        fontStyle = it.value.displayFont.fontStyle,
                        onClick = {
                            onTextClick(it.value.displayFont.fontPath, 24f)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TextRow(
    text: String,
    fontFamily: FontFamily,
    fontWeight: FontWeight,
    fontSize: TextUnit = 24.sp,
    fontStyle: FontStyle = FontStyle.Normal,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .heightIn(56.dp)
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontFamily = fontFamily,
            fontWeight = fontWeight,
            fontStyle = fontStyle,
            fontSize = fontSize,
            modifier = Modifier.align(Alignment.CenterStart)
        )
    }
}