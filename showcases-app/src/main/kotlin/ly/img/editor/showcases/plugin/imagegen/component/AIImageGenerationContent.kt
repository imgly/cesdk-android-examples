package ly.img.editor.showcases.plugin.imagegen.component

import android.content.res.Configuration
import androidx.compose.animation.Animatable
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ly.img.editor.core.theme.surface1
import ly.img.editor.core.theme.surface3
import ly.img.editor.core.ui.SheetHeader
import ly.img.editor.showcases.R
import ly.img.editor.showcases.icon.DockCreateWithAi
import ly.img.editor.showcases.icon.GenOutputImage
import ly.img.editor.showcases.icon.GenOutputVector
import ly.img.editor.showcases.icon.IconPack
import ly.img.editor.showcases.plugin.imagegen.AIImageGenerationState
import ly.img.editor.showcases.plugin.imagegen.Format
import ly.img.editor.showcases.plugin.imagegen.OutputType
import ly.img.editor.showcases.ui.preview.PreviewTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun AIImageGenerationContent(
    state: AIImageGenerationState,
    onPromptChange: (String) -> Unit,
    onAddImageClick: () -> Unit,
    onRemoveImageClick: () -> Unit = {},
    onMakeItClick: () -> Boolean,
    onStyleClick: () -> Unit,
    onAspectRatioClick: () -> Unit,
    onCloseSheet: () -> Unit = {},
    onOutputSelected: ((OutputType) -> Unit),
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp, top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SheetHeader(
            title = stringResource(R.string.ly_img_showcases_ai_image_generation_title),
            onClose = onCloseSheet,
        )

        val scope = rememberCoroutineScope()
        val borderColor = MaterialTheme.colorScheme.outlineVariant
        val errorColor = MaterialTheme.colorScheme.error
        val animatedBorderColor = remember { Animatable(borderColor) }
        val inputElevation = remember { androidx.compose.animation.core.Animatable(0f) }
        val haptic = LocalHapticFeedback.current

        val isDark = isSystemInDarkTheme()
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .graphicsLayer {
                    shadowElevation = inputElevation.value.dp.toPx()
                    spotShadowColor = errorColor
                    shape = RoundedCornerShape(24.dp)
                    translationY = inputElevation.value * .4f
                }
                .border(
                    width = 1.dp,
                    shape = RoundedCornerShape(24.dp),
                    color = animatedBorderColor.value,
                )
                .background(
                    shape = RoundedCornerShape(24.dp),
                    color = if (!isDark) Color.White else MaterialTheme.colorScheme.surface3,
                ),
        ) {
            if (state.imageUri != null) {
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp, start = 8.dp),
                ) {
                    AsyncImage(
                        model = state.imageUri,
                        contentDescription = stringResource(R.string.ly_img_showcases_selected_image_content_description),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(72.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onAddImageClick() }
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                            ),
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 8.dp, y = -8.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { onRemoveImageClick() }
                            .padding(6.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = CircleShape,
                            )
                            .background(
                                color = Color.White,
                                shape = CircleShape,
                            )
                            .padding(2.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = stringResource(R.string.ly_img_showcases_remove_image_content_description),
                            tint = Color.Black,
                            modifier = Modifier.size(14.dp),
                        )
                    }
                }
            } else {
                FilledTonalButton(
                    modifier = Modifier
                        .offset(y = (-4).dp)
                        .padding(top = 8.dp, start = 8.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        containerColor = MaterialTheme.colorScheme.surface1,
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    onClick = onAddImageClick,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.ly_img_showcases_ai_image_add_image_optional),
                    )
                }
            }

            OutlinedTextField(
                value = state.prompt,
                onValueChange = onPromptChange,
                placeholder = {
                    Text(
                        text = stringResource(R.string.ly_img_showcases_ai_image_prompt_placeholder),
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    errorBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(8.dp),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            OutputDropdown(
                selectedOutput = state.selectedOutput,
                onOutputSelected = onOutputSelected,
            )

            Box(
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .height(40.dp)
                    .width(1.dp)
                    .padding(vertical = 8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape,
                    ),
            )

            Option(
                title = "${state.selectedOutput.label} Style",
                text = state.selectedStyle.displayName,
                icon = {
                    StyleIcon(
                        style = state.selectedStyle,
                        modifier = Modifier.size(40.dp),
                    )
                },
                onClick = onStyleClick,
            )

            if (state.imageUri == null) {
                Option(
                    title = "Format",
                    text = if (state.selectedFormat == Format.CUSTOM) {
                        "${state.customWidth}×${state.customHeight}"
                    } else {
                        "${state.selectedFormat.ratio} (${state.selectedFormat.label})"
                    },
                    icon = {
                        Box(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .padding(8.dp)
                                .size(20.dp)
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    shape = RoundedCornerShape(4.dp),
                                ),
                        )
                    },
                    onClick = onAspectRatioClick,
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val success = onMakeItClick()
                if (!success) {
                    scope.launch {
                        animatedBorderColor.animateTo(errorColor)
                        animatedBorderColor.animateTo(
                            targetValue = borderColor,
                            animationSpec = spring(stiffness = Spring.StiffnessLow),
                        )
                    }
                    scope.launch {
                        inputElevation.animateTo(10f)
                        inputElevation.animateTo(
                            targetValue = 0f,
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        )
                    }
                    scope.launch {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        delay(130)
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                }
            },
            modifier = Modifier
                .alpha(
                    if (state.prompt.isBlank()) .4f else 1f,
                )
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        ) {
            Icon(
                imageVector = IconPack.DockCreateWithAi,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.ly_img_showcases_ai_image_make_it),
                style = MaterialTheme.typography.titleMedium,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun AIImageGenerationContentPreview() {
    PreviewTheme {
        Surface {
            AIImageGenerationContent(
                state = AIImageGenerationState(
                    prompt = "",
                ),
                onPromptChange = {},
                onAddImageClick = {},
                onRemoveImageClick = {},
                onMakeItClick = { true },
                onStyleClick = {},
                onAspectRatioClick = {},
                onCloseSheet = {},
                onOutputSelected = {},
            )
        }
    }
}

@Composable
@Preview(name = "With Image - Light")
@Preview(name = "With Image - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun AIImageGenerationContentWithImagePreview() {
    PreviewTheme {
        Surface {
            AIImageGenerationContent(
                state = AIImageGenerationState(
                    prompt = "A beautiful sunset over mountains",
                    imageUri = "https://picsum.photos/800/600",
                    isImageSelected = true,
                ),
                onPromptChange = {},
                onAddImageClick = {},
                onRemoveImageClick = {},
                onMakeItClick = { true },
                onStyleClick = {},
                onAspectRatioClick = {},
                onCloseSheet = {},
                onOutputSelected = {},
            )
        }
    }
}

@Composable
fun OutputDropdown(
    selectedOutput: OutputType,
    onOutputSelected: (OutputType) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Option(
            title = "Output",
            text = selectedOutput.label,
            icon = {
                Icon(
                    imageVector = when (selectedOutput) {
                        OutputType.IMAGE -> IconPack.GenOutputImage
                        OutputType.VECTOR -> IconPack.GenOutputVector
                    },
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            },
            onClick = { expanded = true },
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            OutputType.values().forEach { outputType ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                imageVector = when (outputType) {
                                    OutputType.IMAGE -> IconPack.GenOutputImage
                                    OutputType.VECTOR -> IconPack.GenOutputVector
                                },
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                            )
                            Text(outputType.label)
                        }
                    },
                    onClick = {
                        onOutputSelected(outputType)
                        expanded = false
                    },
                )
            }
        }
    }
}
