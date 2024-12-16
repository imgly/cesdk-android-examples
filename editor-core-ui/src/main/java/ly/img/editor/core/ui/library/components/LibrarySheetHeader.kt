package ly.img.editor.core.ui.library.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ly.img.editor.compose.animation.AnimatedContent
import ly.img.editor.compose.animation.with
import ly.img.editor.compose.animation_core.fadeIn
import ly.img.editor.compose.animation_core.fadeOut
import ly.img.editor.compose.animation_core.slideInHorizontally
import ly.img.editor.compose.animation_core.slideOutHorizontally
import ly.img.editor.compose.animation_core.tween
import ly.img.editor.compose.material3.InputChip
import ly.img.editor.compose.material3.InputChipDefaults
import ly.img.editor.compose.material3.TextFieldColors
import ly.img.editor.compose.material3.TextFieldDefaults
import ly.img.editor.compose.material3.TextFieldDefaults.indicatorLine
import ly.img.editor.compose.material3.TopAppBar
import ly.img.editor.core.R
import ly.img.editor.core.iconpack.ArrowBack
import ly.img.editor.core.iconpack.Close
import ly.img.editor.core.theme.surface3
import ly.img.editor.core.ui.iconpack.Expandmore
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.iconpack.Search
import ly.img.editor.core.ui.library.state.AssetLibraryUiState
import ly.img.editor.core.ui.library.util.LibraryEvent
import ly.img.editor.core.iconpack.IconPack as CoreIconPack

@Composable
internal fun LibrarySearchHeader(
    uiState: AssetLibraryUiState,
    onLibraryEvent: (LibraryEvent) -> Unit,
    onBack: () -> Unit,
    onSearchFocus: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        AnimatedContent(
            modifier = Modifier.weight(1f),
            targetState = uiState.isInSearchMode,
            transitionSpec = {
                if (targetState) {
                    fadeIn(animationSpec = tween(250)) +
                        slideInHorizontally(
                            initialOffsetX = { it / 2 },
                            animationSpec = tween(250),
                        ) with fadeOut(animationSpec = tween(250))
                } else {
                    fadeIn(animationSpec = tween(100, delayMillis = 100)) with
                        fadeOut(animationSpec = tween(100)) +
                        slideOutHorizontally(
                            targetOffsetX = { it / 2 },
                            animationSpec = tween(100),
                        )
                }
            },
            label = "SearchAnimation",
        ) { isInSearchMode ->
            if (isInSearchMode) {
                val focusRequester = remember { FocusRequester() }
                var textFieldValue by remember {
                    mutableStateOf(
                        TextFieldValue(uiState.searchText, TextRange(uiState.searchText.length)),
                    )
                }
                SearchTextField(
                    modifier =
                        Modifier
                            .onFocusChanged {
                                if (it.isFocused) {
                                    onSearchFocus()
                                }
                            }
                            .focusRequester(focusRequester)
                            .padding(8.dp)
                            .fillMaxWidth(),
                    textFieldValue = textFieldValue,
                    placeholder = {
                        Text(stringResource(R.string.ly_img_editor_search_placeholder, stringResource(id = uiState.titleRes)))
                    },
                    onSearch = {
                        onLibraryEvent(LibraryEvent.OnEnterSearchMode(enter = false, uiState.libraryCategory))
                    },
                    onValueChange = {
                        textFieldValue = it
                        onLibraryEvent(LibraryEvent.OnSearchTextChange(it.text, uiState.libraryCategory, debounce = true))
                    },
                    leadingIcon = {
                        IconButton(onClick = {
                            onLibraryEvent(LibraryEvent.OnEnterSearchMode(enter = false, uiState.libraryCategory))
                        }) {
                            Icon(CoreIconPack.ArrowBack, contentDescription = stringResource(R.string.ly_img_editor_back))
                        }
                    },
                    trailingIcon = {
                        if (uiState.searchText.isNotEmpty()) {
                            IconButton(onClick = {
                                textFieldValue = textFieldValue.copy(text = "", selection = TextRange(0))
                                onLibraryEvent(LibraryEvent.OnSearchTextChange("", uiState.libraryCategory))
                            }) {
                                Icon(CoreIconPack.Close, contentDescription = stringResource(R.string.ly_img_editor_search_clear))
                            }
                        }
                    },
                )
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            } else {
                TopAppBar(
                    title = {
                        Text(
                            stringResource(id = uiState.titleRes),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    navigationIcon = {
                        if (!uiState.isRoot) {
                            IconButton(onClick = {
                                onLibraryEvent(LibraryEvent.OnPopStack(uiState.libraryCategory))
                            }) {
                                Icon(
                                    CoreIconPack.ArrowBack,
                                    contentDescription = stringResource(R.string.ly_img_editor_back),
                                )
                            }
                        }
                    },
                    actions = {
                        val searchQuery = uiState.searchText
                        if (searchQuery.isNotEmpty()) {
                            InputChip(
                                selected = true,
                                onClick = {
                                    onLibraryEvent(LibraryEvent.OnEnterSearchMode(enter = true, uiState.libraryCategory))
                                },
                                label = {
                                    Text(
                                        searchQuery,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.widthIn(max = 120.dp),
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = {
                                        onLibraryEvent(LibraryEvent.OnSearchTextChange("", uiState.libraryCategory))
                                    }, Modifier.size(InputChipDefaults.IconSize)) {
                                        Icon(
                                            CoreIconPack.Close,
                                            contentDescription = stringResource(R.string.ly_img_editor_search_clear),
                                        )
                                    }
                                },
                                shape = ShapeDefaults.Large,
                                modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                            )
                        } else {
                            Box(Modifier.offset(x = 4.dp)) {
                                IconButton(
                                    onClick = {
                                        onLibraryEvent(LibraryEvent.OnEnterSearchMode(enter = true, uiState.libraryCategory))
                                    },
                                ) {
                                    Icon(IconPack.Search, contentDescription = stringResource(id = R.string.ly_img_editor_search))
                                }
                            }
                        }
                    },
                )
            }
        }
        IconButton(
            onClick = onBack,
            Modifier.padding(end = 4.dp),
        ) {
            Icon(IconPack.Expandmore, contentDescription = stringResource(id = R.string.ly_img_editor_back))
        }
    }
}

@Composable
private fun SearchTextField(
    textFieldValue: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.extraLarge,
    colors: TextFieldColors =
        TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface3,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface3,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
) {
    val textColor = MaterialTheme.colorScheme.onSurface
    val mergedTextStyle = MaterialTheme.typography.bodyLarge.merge(TextStyle(color = textColor))

    BasicTextField(
        value = textFieldValue,
        modifier =
            modifier
                .indicatorLine(
                    enabled = true,
                    isError = false,
                    interactionSource = interactionSource,
                    colors = colors,
                    focusedIndicatorLineThickness = 0.dp, // to hide the indicator line
                    unfocusedIndicatorLineThickness = 0.dp, // to hide the indicator line
                ),
        onValueChange = onValueChange,
        enabled = true,
        readOnly = false,
        textStyle = mergedTextStyle,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch(textFieldValue.text) }),
        interactionSource = interactionSource,
        singleLine = singleLine,
        maxLines = maxLines,
        decorationBox = @Composable { innerTextField ->
            // places leading icon, text field with label and placeholder, trailing icon
            TextFieldDefaults.DecorationBox(
                value = textFieldValue.text,
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
                contentPadding = PaddingValues(vertical = 8.dp),
                colors = colors,
            )
        },
    )
}
