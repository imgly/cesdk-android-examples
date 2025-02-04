package ly.img.cesdk.library.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.indicatorLine
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import ly.img.cesdk.apparelui.R
import ly.img.cesdk.core.UiDefaults
import ly.img.cesdk.core.iconpack.Cancel
import ly.img.cesdk.core.iconpack.Expandmore
import ly.img.cesdk.core.iconpack.IconPack
import ly.img.cesdk.core.iconpack.Search
import ly.img.cesdk.core.utils.minimumTouchTargetSize

@Composable
fun SearchSheetHeader(
    searchValue: String = "",
    @StringRes placeholderTextRes: Int,
    onSearchValueChange: (String) -> Unit,
    onSearchFocus: () -> Unit,
    onClose: () -> Unit,
    leadingAction: @Composable (() -> Unit)? = null
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(UiDefaults.sheetHeaderHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingAction == null) {
            Spacer(Modifier.width(16.dp))
        } else {
            leadingAction()
        }

        SearchTextField(
            modifier = Modifier
                .weight(1f)
                .onFocusChanged {
                    if (it.isFocused) {
                        onSearchFocus()
                    }
                }
                .padding(vertical = 8.dp),
            value = searchValue,
            placeholder = {
                Text(stringResource(placeholderTextRes), style = MaterialTheme.typography.labelLarge)
            },
            onValueChange = onSearchValueChange,
            leadingIcon = {
                Icon(imageVector = IconPack.Search, contentDescription = null, modifier = Modifier.size(18.dp))
            },
            trailingIcon = {
                if (searchValue.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .minimumTouchTargetSize()
                            .size(18.dp)
                            .clickable(
                                onClick = { onSearchValueChange("") },
                                role = Role.Button,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(
                                    bounded = false,
                                    radius = 16.dp
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(IconPack.Cancel, contentDescription = stringResource(R.string.cesdk_search_clear))
                    }
                }
            }
        )

        IconButton(
            modifier = Modifier.padding(horizontal = 4.dp),
            onClick = onClose
        ) {
            Icon(IconPack.Expandmore, contentDescription = stringResource(R.string.cesdk_close))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.extraLarge,
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(
        textColor = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedLeadingIconColor = MaterialTheme.colorScheme.secondary,
        unfocusedLeadingIconColor = MaterialTheme.colorScheme.secondary,
        focusedTrailingIconColor = MaterialTheme.colorScheme.secondary,
        unfocusedTrailingIconColor = MaterialTheme.colorScheme.secondary,
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp),
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent
    )
) {
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant
    val mergedTextStyle = MaterialTheme.typography.labelLarge.merge(TextStyle(color = textColor))

    BasicTextField(
        value = value,
        modifier = modifier
            .indicatorLine(
                enabled = true,
                isError = false,
                interactionSource = interactionSource,
                colors = colors,
                focusedIndicatorLineThickness = 0.dp,  //to hide the indicator line
                unfocusedIndicatorLineThickness = 0.dp //to hide the indicator line
            ),
        onValueChange = onValueChange,
        enabled = true,
        readOnly = false,
        textStyle = mergedTextStyle,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        singleLine = singleLine,
        maxLines = maxLines,
        decorationBox = @Composable { innerTextField ->
            // places leading icon, text field with label and placeholder, trailing icon
            TextFieldDefaults.TextFieldDecorationBox(
                value = value,
                visualTransformation = visualTransformation,
                innerTextField = innerTextField,
                placeholder = placeholder,
                label = null,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                supportingText = null,
                shape = shape,
                singleLine = singleLine,
                enabled = true,
                isError = false,
                interactionSource = interactionSource,
                contentPadding = PaddingValues(0.dp),
                colors = colors
            )
        }
    )
}
