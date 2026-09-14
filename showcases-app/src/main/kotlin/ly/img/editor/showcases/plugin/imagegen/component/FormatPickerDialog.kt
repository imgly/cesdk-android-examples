package ly.img.editor.showcases.plugin.imagegen.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ly.img.editor.showcases.R
import ly.img.editor.showcases.plugin.imagegen.Format
import ly.img.editor.showcases.ui.preview.PreviewTheme

@Composable
fun FormatPickerDialog(
    selectedFormat: Format,
    customWidth: String,
    customHeight: String,
    onFormatSelected: (Format) -> Unit,
    onCustomWidthChange: (String) -> Unit,
    onCustomHeightChange: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            decorFitsSystemWindows = true,
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 32.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(16.dp),
                    ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.ly_img_showcases_ai_image_format_selection_title),
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }

                FormatSelectionContent(
                    modifier = Modifier
                        .verticalScroll(
                            state = rememberScrollState(),
                        ),
                    selectedFormat = selectedFormat,
                    customWidth = customWidth,
                    customHeight = customHeight,
                    onFormatSelected = onFormatSelected,
                    onCustomWidthChange = onCustomWidthChange,
                    onCustomHeightChange = onCustomHeightChange,
                    onBack = onDismiss,
                )
            }
        }
    }
}

@Composable
fun FormatSelectionContent(
    modifier: Modifier = Modifier,
    selectedFormat: Format,
    customWidth: String,
    customHeight: String,
    onFormatSelected: (Format) -> Unit,
    onCustomWidthChange: (String) -> Unit,
    onCustomHeightChange: (String) -> Unit,
    onBack: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val widthFocusRequester = remember { FocusRequester() }
    val heightFocusRequester = remember { FocusRequester() }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        val formats = Format.values()
        val selectedIndex = formats.indexOf(selectedFormat).takeIf { it >= 0 } ?: 0
        val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex)

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            contentPadding = PaddingValues(bottom = 24.dp, start = 8.dp, end = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(formats) { format ->
                FormatListItem(
                    format = format,
                    isSelected = format == selectedFormat,
                    onClick = { onFormatSelected(format) },
                )
            }
            item {
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 8.dp, top = 8.dp)
                        .alpha(
                            when (selectedFormat) {
                                Format.CUSTOM -> 1f
                                else -> .7f
                            },
                        ),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        OutlinedTextField(
                            value = customWidth,
                            onValueChange = onCustomWidthChange,
                            enabled = selectedFormat == Format.CUSTOM,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next,
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    if (selectedFormat == Format.CUSTOM) {
                                        heightFocusRequester.requestFocus()
                                    }
                                },
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            ),
                            label = {
                                Text(text = stringResource(R.string.ly_img_showcases_ai_image_width_px))
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .focusRequester(widthFocusRequester),
                        )

                        OutlinedTextField(
                            value = customHeight,
                            onValueChange = onCustomHeightChange,
                            enabled = selectedFormat == Format.CUSTOM,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done,
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (selectedFormat == Format.CUSTOM) {
                                        focusManager.clearFocus()
                                        onBack()
                                    }
                                },
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            ),
                            label = {
                                Text(text = stringResource(R.string.ly_img_showcases_ai_image_height_px))
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .focusRequester(heightFocusRequester),
                        )

                        IconButton(
                            onClick = {
                                focusManager.clearFocus()
                                onBack()
                            },
                            enabled = selectedFormat == Format.CUSTOM,
                            modifier = Modifier.padding(bottom = 8.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = stringResource(R.string.ly_img_showcases_apply_custom_format_content_description),
                                tint = if (selectedFormat == Format.CUSTOM) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FormatListItem(
    format: Format,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(
                shape = RoundedCornerShape(12.dp),
            )
            .clickable { onClick() }
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                shape = RoundedCornerShape(12.dp),
            )
            .defaultMinSize(minHeight = 64.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = "${format.ratio} (${format.label})",
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
            )
        }

        FormatIcon(
            format = format,
        )
    }
}

@Composable
@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun FormatPickerDialogPreview() {
    PreviewTheme {
        Surface {
            FormatPickerDialog(
                selectedFormat = Format.LANDSCAPE_16_9,
                customWidth = "1920",
                customHeight = "1080",
                onFormatSelected = {},
                onCustomWidthChange = {},
                onCustomHeightChange = {},
                onDismiss = {},
            )
        }
    }
}

@Composable
@Preview(name = "Custom Format - Light")
@Preview(name = "Custom Format - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun FormatPickerDialogCustomPreview() {
    PreviewTheme {
        Surface {
            FormatPickerDialog(
                selectedFormat = Format.CUSTOM,
                customWidth = "800",
                customHeight = "600",
                onFormatSelected = {},
                onCustomWidthChange = {},
                onCustomHeightChange = {},
                onDismiss = {},
            )
        }
    }
}
