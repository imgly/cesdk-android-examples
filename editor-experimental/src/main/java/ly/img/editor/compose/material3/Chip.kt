/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ly.img.editor.compose.material3

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ly.img.editor.compose.material3.tokens.InputChipTokens

/**
 * <a href="https://m3.material.io/components/chips/overview" class="external" target="_blank">Material Design input chip</a>.
 *
 * Chips help people enter information, make selections, filter content, or trigger actions. Chips
 * can show multiple interactive elements together in the same area, such as a list of selectable
 * movie times, or a series of email contacts.
 *
 * Input chips represent discrete pieces of information entered by a user.
 *
 * ![Input chip image](https://developer.android.com/images/reference/androidx/compose/material3/input-chip.png)
 *
 * An Input Chip can have a leading icon or an avatar at its start. In case both are provided, the
 * avatar will take precedence and will be displayed.
 *
 * Example of an InputChip with a trailing icon:
 * @sample androidx.compose.material3.samples.InputChipSample
 *
 * Example of an InputChip with an avatar and a trailing icon:
 * @sample androidx.compose.material3.samples.InputChipWithAvatarSample
 *
 * Input chips should appear in a set and can be horizontally scrollable:
 * @sample androidx.compose.material3.samples.ChipGroupSingleLineSample
 *
 * Alternatively, use [androidx.compose.foundation.layout.FlowRow] to wrap chips to a new line.
 * @sample androidx.compose.material3.samples.ChipGroupReflowSample
 *
 * @param selected whether this chip is selected or not
 * @param onClick called when this chip is clicked
 * @param label text label for this chip
 * @param modifier the [Modifier] to be applied to this chip
 * @param enabled controls the enabled state of this chip. When `false`, this component will not
 * respond to user input, and it will appear visually disabled and disabled to accessibility
 * services.
 * @param leadingIcon optional icon at the start of the chip, preceding the [label] text
 * @param avatar optional avatar at the start of the chip, preceding the [label] text
 * @param trailingIcon optional icon at the end of the chip
 * @param shape defines the shape of this chip's container, border (when [border] is not null), and
 * shadow (when using [elevation])
 * @param colors [ChipColors] that will be used to resolve the colors used for this chip in
 * different states. See [InputChipDefaults.inputChipColors].
 * @param elevation [ChipElevation] used to resolve the elevation for this chip in different states.
 * This controls the size of the shadow below the chip. Additionally, when the container color is
 * [ColorScheme.surface], this controls the amount of primary color applied as an overlay. See
 * [InputChipDefaults.inputChipElevation].
 * @param border the border to draw around the container of this chip. Pass `null` for no border.
 * See [InputChipDefaults.inputChipBorder].
 * @param interactionSource the [MutableInteractionSource] representing the stream of [Interaction]s
 * for this chip. You can create and pass in your own `remember`ed instance to observe
 * [Interaction]s and customize the appearance / behavior of this chip in different states.
 */
@Composable
fun InputChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    avatar: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = InputChipDefaults.shape,
    colors: SelectableChipColors = InputChipDefaults.inputChipColors(),
    elevation: SelectableChipElevation? = InputChipDefaults.inputChipElevation(),
    border: SelectableChipBorder? = InputChipDefaults.inputChipBorder(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    // If given, place the avatar in an InputChipTokens.AvatarShape shape before passing it into the
    // Chip function.
    var shapedAvatar: @Composable (() -> Unit)? = null
    if (avatar != null) {
        val avatarOpacity = if (enabled) 1f else InputChipTokens.DisabledAvatarOpacity
        val avatarShape = InputChipTokens.AvatarShape.toShape()
        shapedAvatar = @Composable {
            Box(
                modifier =
                    Modifier.graphicsLayer {
                        this.alpha = avatarOpacity
                        this.shape = avatarShape
                        this.clip = true
                    },
                contentAlignment = Alignment.Center,
            ) {
                avatar()
            }
        }
    }
    SelectableChip(
        selected = selected,
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        label = label,
        labelTextStyle = MaterialTheme.typography.fromToken(InputChipTokens.LabelTextFont),
        leadingIcon = leadingIcon,
        avatar = shapedAvatar,
        trailingIcon = trailingIcon,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border?.borderStroke(enabled, selected)?.value,
        minHeight = InputChipDefaults.Height,
        paddingValues =
            inputChipPadding(
                hasAvatar = shapedAvatar != null,
                hasLeadingIcon = leadingIcon != null,
                hasTrailingIcon = trailingIcon != null,
            ),
        interactionSource = interactionSource,
    )
}

/**
 * Contains the baseline values used by an [InputChip].
 */
object InputChipDefaults {
    /**
     * The height applied for an input chip.
     * Note that you can override it by applying Modifier.height directly on a chip.
     */
    val Height = InputChipTokens.ContainerHeight

    /**
     * The size of an input chip icon.
     */
    val IconSize = InputChipTokens.LeadingIconSize

    /**
     * The size of an input chip avatar.
     */
    val AvatarSize = InputChipTokens.AvatarSize

    /**
     * Creates a [SelectableChipColors] that represents the default container, label, and icon
     * colors used in an [InputChip].
     *
     * @param containerColor the container color of this chip when enabled
     * @param labelColor the label color of this chip when enabled
     * @param leadingIconColor the color of this chip's start icon when enabled
     * @param trailingIconColor the color of this chip's start end icon when enabled
     * @param disabledContainerColor the container color of this chip when not enabled
     * @param disabledLabelColor the label color of this chip when not enabled
     * @param disabledLeadingIconColor the color of this chip's start icon when not enabled
     * @param disabledTrailingIconColor the color of this chip's end icon when not enabled
     * @param selectedContainerColor the container color of this chip when selected
     * @param disabledSelectedContainerColor the container color of this chip when not enabled and
     * selected
     * @param selectedLabelColor the label color of this chip when selected
     * @param selectedLeadingIconColor the color of this chip's start icon when selected
     * @param selectedTrailingIconColor the color of this chip's end icon when selected
     */
    @Composable
    fun inputChipColors(
        containerColor: Color = Color.Transparent,
        labelColor: Color = InputChipTokens.UnselectedLabelTextColor.toColor(),
        leadingIconColor: Color = InputChipTokens.UnselectedLeadingIconColor.toColor(),
        trailingIconColor: Color = InputChipTokens.UnselectedTrailingIconColor.toColor(),
        disabledContainerColor: Color = Color.Transparent,
        disabledLabelColor: Color =
            InputChipTokens.DisabledLabelTextColor.toColor()
                .copy(alpha = InputChipTokens.DisabledLabelTextOpacity),
        disabledLeadingIconColor: Color =
            InputChipTokens.DisabledLeadingIconColor.toColor()
                .copy(alpha = InputChipTokens.DisabledLeadingIconOpacity),
        disabledTrailingIconColor: Color =
            InputChipTokens.DisabledTrailingIconColor.toColor()
                .copy(alpha = InputChipTokens.DisabledTrailingIconOpacity),
        selectedContainerColor: Color = InputChipTokens.SelectedContainerColor.toColor(),
        disabledSelectedContainerColor: Color =
            InputChipTokens.DisabledSelectedContainerColor.toColor()
                .copy(alpha = InputChipTokens.DisabledSelectedContainerOpacity),
        selectedLabelColor: Color = InputChipTokens.SelectedLabelTextColor.toColor(),
        selectedLeadingIconColor: Color = InputChipTokens.SelectedLeadingIconColor.toColor(),
        selectedTrailingIconColor: Color = InputChipTokens.SelectedTrailingIconColor.toColor(),
    ): SelectableChipColors =
        SelectableChipColors(
            containerColor = containerColor,
            labelColor = labelColor,
            leadingIconColor = leadingIconColor,
            trailingIconColor = trailingIconColor,
            disabledContainerColor = disabledContainerColor,
            disabledLabelColor = disabledLabelColor,
            disabledLeadingIconColor = disabledLeadingIconColor,
            disabledTrailingIconColor = disabledTrailingIconColor,
            selectedContainerColor = selectedContainerColor,
            disabledSelectedContainerColor = disabledSelectedContainerColor,
            selectedLabelColor = selectedLabelColor,
            selectedLeadingIconColor = selectedLeadingIconColor,
            selectedTrailingIconColor = selectedTrailingIconColor,
        )

    /**
     * Creates a [SelectableChipElevation] that will animate between the provided values according
     * to the Material specification for an [InputChip].
     *
     * @param elevation the elevation used when the [FilterChip] is has no other
     * [Interaction]s
     * @param pressedElevation the elevation used when the chip is pressed
     * @param focusedElevation the elevation used when the chip is focused
     * @param hoveredElevation the elevation used when the chip is hovered
     * @param draggedElevation the elevation used when the chip is dragged
     * @param disabledElevation the elevation used when the chip is not enabled
     */
    @Composable
    fun inputChipElevation(
        elevation: Dp = InputChipTokens.ContainerElevation,
        pressedElevation: Dp = elevation,
        focusedElevation: Dp = elevation,
        hoveredElevation: Dp = elevation,
        draggedElevation: Dp = InputChipTokens.DraggedContainerElevation,
        disabledElevation: Dp = elevation,
    ): SelectableChipElevation =
        SelectableChipElevation(
            elevation = elevation,
            pressedElevation = pressedElevation,
            focusedElevation = focusedElevation,
            hoveredElevation = hoveredElevation,
            draggedElevation = draggedElevation,
            disabledElevation = disabledElevation,
        )

    /**
     * Creates a [SelectableChipBorder] that represents the default border used in an [InputChip].
     *
     * @param borderColor the border color of this chip when enabled and not selected
     * @param selectedBorderColor the border color of this chip when enabled and selected
     * @param disabledBorderColor the border color of this chip when not enabled and not
     * selected
     * @param disabledSelectedBorderColor the border color of this chip when not enabled
     * but selected
     * @param borderWidth the border stroke width of this chip when not selected
     * @param selectedBorderWidth the border stroke width of this chip when selected
     */
    @Composable
    fun inputChipBorder(
        borderColor: Color = InputChipTokens.UnselectedOutlineColor.toColor(),
        selectedBorderColor: Color = Color.Transparent,
        disabledBorderColor: Color =
            InputChipTokens.DisabledUnselectedOutlineColor.toColor()
                .copy(alpha = InputChipTokens.DisabledUnselectedOutlineOpacity),
        disabledSelectedBorderColor: Color = Color.Transparent,
        borderWidth: Dp = InputChipTokens.UnselectedOutlineWidth,
        selectedBorderWidth: Dp = InputChipTokens.SelectedOutlineWidth,
    ): SelectableChipBorder =
        SelectableChipBorder(
            borderColor = borderColor,
            selectedBorderColor = selectedBorderColor,
            disabledBorderColor = disabledBorderColor,
            disabledSelectedBorderColor = disabledSelectedBorderColor,
            borderWidth = borderWidth,
            selectedBorderWidth = selectedBorderWidth,
        )

    /** Default shape of an input chip. */
    val shape: Shape @Composable get() = InputChipTokens.ContainerShape.toShape()
}

@Composable
private fun SelectableChip(
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
    enabled: Boolean,
    label: @Composable () -> Unit,
    labelTextStyle: TextStyle,
    leadingIcon: @Composable (() -> Unit)?,
    avatar: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    shape: Shape,
    colors: SelectableChipColors,
    elevation: SelectableChipElevation?,
    border: BorderStroke?,
    minHeight: Dp,
    paddingValues: PaddingValues,
    interactionSource: MutableInteractionSource,
) {
    // TODO(b/229794614): Animate transition between unselected and selected.
    Surface(
        selected = selected,
        onClick = onClick,
        modifier = modifier.semantics { role = Role.Checkbox },
        enabled = enabled,
        shape = shape,
        color = colors.containerColor(enabled, selected).value,
        tonalElevation =
            elevation?.tonalElevation(enabled, interactionSource)?.value
                ?: 0.dp,
        shadowElevation =
            elevation?.shadowElevation(enabled, interactionSource)?.value
                ?: 0.dp,
        border = border,
        interactionSource = interactionSource,
    ) {
        ChipContent(
            label = label,
            labelTextStyle = labelTextStyle,
            leadingIcon = leadingIcon,
            avatar = avatar,
            labelColor = colors.labelColor(enabled, selected).value,
            trailingIcon = trailingIcon,
            leadingIconColor = colors.leadingIconContentColor(enabled, selected).value,
            trailingIconColor = colors.trailingIconContentColor(enabled, selected).value,
            minHeight = minHeight,
            paddingValues = paddingValues,
        )
    }
}

@Composable
private fun ChipContent(
    label: @Composable () -> Unit,
    labelTextStyle: TextStyle,
    labelColor: Color,
    leadingIcon: @Composable (() -> Unit)?,
    avatar: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    leadingIconColor: Color,
    trailingIconColor: Color,
    minHeight: Dp,
    paddingValues: PaddingValues,
) {
    CompositionLocalProvider(
        LocalContentColor provides labelColor,
        LocalTextStyle provides labelTextStyle,
    ) {
        Row(
            Modifier
                .defaultMinSize(minHeight = minHeight)
                .padding(paddingValues),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (avatar != null) {
                avatar()
            } else if (leadingIcon != null) {
                CompositionLocalProvider(
                    LocalContentColor provides leadingIconColor,
                    content = leadingIcon,
                )
            }
            Spacer(Modifier.width(HorizontalElementsPadding))
            label()
            Spacer(Modifier.width(HorizontalElementsPadding))
            if (trailingIcon != null) {
                CompositionLocalProvider(
                    LocalContentColor provides trailingIconColor,
                    content = trailingIcon,
                )
            }
        }
    }
}

/**
 * Represents the elevation for a chip in different states.
 */
@Immutable
class ChipElevation internal constructor(
    private val elevation: Dp,
    private val pressedElevation: Dp,
    private val focusedElevation: Dp,
    private val hoveredElevation: Dp,
    private val draggedElevation: Dp,
    private val disabledElevation: Dp,
) {
    /**
     * Represents the tonal elevation used in a chip, depending on its [enabled] state and
     * [interactionSource]. This should typically be the same value as the [shadowElevation].
     *
     * Tonal elevation is used to apply a color shift to the surface to give the it higher emphasis.
     * When surface's color is [ColorScheme.surface], a higher elevation will result in a darker
     * color in light theme and lighter color in dark theme.
     *
     * See [shadowElevation] which controls the elevation of the shadow drawn around the chip.
     *
     * @param enabled whether the chip is enabled
     * @param interactionSource the [InteractionSource] for this chip
     */
    @Composable
    internal fun tonalElevation(
        enabled: Boolean,
        interactionSource: InteractionSource,
    ): State<Dp> {
        return animateElevation(enabled = enabled, interactionSource = interactionSource)
    }

    /**
     * Represents the shadow elevation used in a chip, depending on its [enabled] state and
     * [interactionSource]. This should typically be the same value as the [tonalElevation].
     *
     * Shadow elevation is used to apply a shadow around the chip to give it higher emphasis.
     *
     * See [tonalElevation] which controls the elevation with a color shift to the surface.
     *
     * @param enabled whether the chip is enabled
     * @param interactionSource the [InteractionSource] for this chip
     */
    @Composable
    internal fun shadowElevation(
        enabled: Boolean,
        interactionSource: InteractionSource,
    ): State<Dp> {
        return animateElevation(enabled = enabled, interactionSource = interactionSource)
    }

    @Composable
    private fun animateElevation(
        enabled: Boolean,
        interactionSource: InteractionSource,
    ): State<Dp> {
        val interactions = remember { mutableStateListOf<Interaction>() }
        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is HoverInteraction.Enter -> {
                        interactions.add(interaction)
                    }
                    is HoverInteraction.Exit -> {
                        interactions.remove(interaction.enter)
                    }
                    is FocusInteraction.Focus -> {
                        interactions.add(interaction)
                    }
                    is FocusInteraction.Unfocus -> {
                        interactions.remove(interaction.focus)
                    }
                    is PressInteraction.Press -> {
                        interactions.add(interaction)
                    }
                    is PressInteraction.Release -> {
                        interactions.remove(interaction.press)
                    }
                    is PressInteraction.Cancel -> {
                        interactions.remove(interaction.press)
                    }
                    is DragInteraction.Start -> {
                        interactions.add(interaction)
                    }
                    is DragInteraction.Stop -> {
                        interactions.remove(interaction.start)
                    }
                    is DragInteraction.Cancel -> {
                        interactions.remove(interaction.start)
                    }
                }
            }
        }

        val interaction = interactions.lastOrNull()

        val target =
            if (!enabled) {
                disabledElevation
            } else {
                when (interaction) {
                    is PressInteraction.Press -> pressedElevation
                    is HoverInteraction.Enter -> hoveredElevation
                    is FocusInteraction.Focus -> focusedElevation
                    is DragInteraction.Start -> draggedElevation
                    else -> elevation
                }
            }

        val animatable = remember { Animatable(target, Dp.VectorConverter) }

        if (!enabled) {
            // No transition when moving to a disabled state
            LaunchedEffect(target) { animatable.snapTo(target) }
        } else {
            LaunchedEffect(target) {
                val lastInteraction =
                    when (animatable.targetValue) {
                        pressedElevation -> PressInteraction.Press(Offset.Zero)
                        hoveredElevation -> HoverInteraction.Enter()
                        focusedElevation -> FocusInteraction.Focus()
                        draggedElevation -> DragInteraction.Start()
                        else -> null
                    }
                animatable.animateElevation(
                    from = lastInteraction,
                    to = interaction,
                    target = target,
                )
            }
        }

        return animatable.asState()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is ChipElevation) return false

        if (elevation != other.elevation) return false
        if (pressedElevation != other.pressedElevation) return false
        if (focusedElevation != other.focusedElevation) return false
        if (hoveredElevation != other.hoveredElevation) return false
        if (disabledElevation != other.disabledElevation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = elevation.hashCode()
        result = 31 * result + pressedElevation.hashCode()
        result = 31 * result + focusedElevation.hashCode()
        result = 31 * result + hoveredElevation.hashCode()
        result = 31 * result + disabledElevation.hashCode()
        return result
    }
}

/**
 * Represents the elevation used in a selectable chip in different states.
 *
 * Note that this default implementation does not take into consideration the `selectable` state
 * passed into its [tonalElevation] and [shadowElevation]. If you wish to apply that state, use a
 * different [SelectableChipElevation].
 */
@Immutable
class SelectableChipElevation internal constructor(
    private val elevation: Dp,
    private val pressedElevation: Dp,
    private val focusedElevation: Dp,
    private val hoveredElevation: Dp,
    private val draggedElevation: Dp,
    private val disabledElevation: Dp,
) {
    /**
     * Represents the tonal elevation used in a chip, depending on [enabled] and
     * [interactionSource]. This should typically be the same value as the [shadowElevation].
     *
     * Tonal elevation is used to apply a color shift to the surface to give the it higher emphasis.
     * When surface's color is [ColorScheme.surface], a higher elevation will result in a darker
     * color in light theme and lighter color in dark theme.
     *
     * See [shadowElevation] which controls the elevation of the shadow drawn around the Chip.
     *
     * @param enabled whether the chip is enabled
     * @param interactionSource the [InteractionSource] for this chip
     */
    @Composable
    internal fun tonalElevation(
        enabled: Boolean,
        interactionSource: InteractionSource,
    ): State<Dp> {
        return animateElevation(enabled = enabled, interactionSource = interactionSource)
    }

    /**
     * Represents the shadow elevation used in a chip, depending on [enabled] and
     * [interactionSource]. This should typically be the same value as the [tonalElevation].
     *
     * Shadow elevation is used to apply a shadow around the surface to give it higher emphasis.
     *
     * See [tonalElevation] which controls the elevation with a color shift to the surface.
     *
     * @param enabled whether the chip is enabled
     * @param interactionSource the [InteractionSource] for this chip
     */
    @Composable
    internal fun shadowElevation(
        enabled: Boolean,
        interactionSource: InteractionSource,
    ): State<Dp> {
        return animateElevation(enabled = enabled, interactionSource = interactionSource)
    }

    @Composable
    private fun animateElevation(
        enabled: Boolean,
        interactionSource: InteractionSource,
    ): State<Dp> {
        val interactions = remember { mutableStateListOf<Interaction>() }
        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is HoverInteraction.Enter -> {
                        interactions.add(interaction)
                    }
                    is HoverInteraction.Exit -> {
                        interactions.remove(interaction.enter)
                    }
                    is FocusInteraction.Focus -> {
                        interactions.add(interaction)
                    }
                    is FocusInteraction.Unfocus -> {
                        interactions.remove(interaction.focus)
                    }
                    is PressInteraction.Press -> {
                        interactions.add(interaction)
                    }
                    is PressInteraction.Release -> {
                        interactions.remove(interaction.press)
                    }
                    is PressInteraction.Cancel -> {
                        interactions.remove(interaction.press)
                    }
                    is DragInteraction.Start -> {
                        interactions.add(interaction)
                    }
                    is DragInteraction.Stop -> {
                        interactions.remove(interaction.start)
                    }
                    is DragInteraction.Cancel -> {
                        interactions.remove(interaction.start)
                    }
                }
            }
        }

        val interaction = interactions.lastOrNull()

        val target =
            if (!enabled) {
                disabledElevation
            } else {
                when (interaction) {
                    is PressInteraction.Press -> pressedElevation
                    is HoverInteraction.Enter -> hoveredElevation
                    is FocusInteraction.Focus -> focusedElevation
                    is DragInteraction.Start -> draggedElevation
                    else -> elevation
                }
            }

        val animatable = remember { Animatable(target, Dp.VectorConverter) }

        if (!enabled) {
            // No transition when moving to a disabled state
            LaunchedEffect(target) { animatable.snapTo(target) }
        } else {
            LaunchedEffect(target) {
                val lastInteraction =
                    when (animatable.targetValue) {
                        pressedElevation -> PressInteraction.Press(Offset.Zero)
                        hoveredElevation -> HoverInteraction.Enter()
                        focusedElevation -> FocusInteraction.Focus()
                        draggedElevation -> DragInteraction.Start()
                        else -> null
                    }
                animatable.animateElevation(
                    from = lastInteraction,
                    to = interaction,
                    target = target,
                )
            }
        }

        return animatable.asState()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is SelectableChipElevation) return false

        if (elevation != other.elevation) return false
        if (pressedElevation != other.pressedElevation) return false
        if (focusedElevation != other.focusedElevation) return false
        if (hoveredElevation != other.hoveredElevation) return false
        if (disabledElevation != other.disabledElevation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = elevation.hashCode()
        result = 31 * result + pressedElevation.hashCode()
        result = 31 * result + focusedElevation.hashCode()
        result = 31 * result + hoveredElevation.hashCode()
        result = 31 * result + disabledElevation.hashCode()
        return result
    }
}

/**
 * Represents the container and content colors used in a clickable chip in different states.
 *
 * See [AssistChipDefaults], [InputChipDefaults], and [SuggestionChipDefaults] for the default
 * colors used in the various Chip configurations.
 */
@Immutable
class ChipColors internal constructor(
    private val containerColor: Color,
    private val labelColor: Color,
    private val leadingIconContentColor: Color,
    private val trailingIconContentColor: Color,
    private val disabledContainerColor: Color,
    private val disabledLabelColor: Color,
    private val disabledLeadingIconContentColor: Color,
    private val disabledTrailingIconContentColor: Color,
    // TODO(b/113855296): Support other states: hover, focus, drag
) {
    /**
     * Represents the container color for this chip, depending on [enabled].
     *
     * @param enabled whether the chip is enabled
     */
    @Composable
    internal fun containerColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(if (enabled) containerColor else disabledContainerColor)
    }

    /**
     * Represents the label color for this chip, depending on [enabled].
     *
     * @param enabled whether the chip is enabled
     */
    @Composable
    internal fun labelColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(if (enabled) labelColor else disabledLabelColor)
    }

    /**
     * Represents the leading icon's content color for this chip, depending on [enabled].
     *
     * @param enabled whether the chip is enabled
     */
    @Composable
    internal fun leadingIconContentColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(
            if (enabled) leadingIconContentColor else disabledLeadingIconContentColor,
        )
    }

    /**
     * Represents the trailing icon's content color for this chip, depending on [enabled].
     *
     * @param enabled whether the chip is enabled
     */
    @Composable
    internal fun trailingIconContentColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(
            if (enabled) trailingIconContentColor else disabledTrailingIconContentColor,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is ChipColors) return false

        if (containerColor != other.containerColor) return false
        if (labelColor != other.labelColor) return false
        if (leadingIconContentColor != other.leadingIconContentColor) return false
        if (trailingIconContentColor != other.trailingIconContentColor) return false
        if (disabledContainerColor != other.disabledContainerColor) return false
        if (disabledLabelColor != other.disabledLabelColor) return false
        if (disabledLeadingIconContentColor != other.disabledLeadingIconContentColor) return false
        if (disabledTrailingIconContentColor != other.disabledTrailingIconContentColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = containerColor.hashCode()
        result = 31 * result + labelColor.hashCode()
        result = 31 * result + leadingIconContentColor.hashCode()
        result = 31 * result + trailingIconContentColor.hashCode()
        result = 31 * result + disabledContainerColor.hashCode()
        result = 31 * result + disabledLabelColor.hashCode()
        result = 31 * result + disabledLeadingIconContentColor.hashCode()
        result = 31 * result + disabledTrailingIconContentColor.hashCode()

        return result
    }
}

/**
 * Represents the container and content colors used in a selectable chip in different states.
 *
 * See [FilterChipDefaults.filterChipColors] and [FilterChipDefaults.elevatedFilterChipColors] for
 * the default colors used in [FilterChip].
 */
@Immutable
class SelectableChipColors internal constructor(
    private val containerColor: Color,
    private val labelColor: Color,
    private val leadingIconColor: Color,
    private val trailingIconColor: Color,
    private val disabledContainerColor: Color,
    private val disabledLabelColor: Color,
    private val disabledLeadingIconColor: Color,
    private val disabledTrailingIconColor: Color,
    private val selectedContainerColor: Color,
    private val disabledSelectedContainerColor: Color,
    private val selectedLabelColor: Color,
    private val selectedLeadingIconColor: Color,
    private val selectedTrailingIconColor: Color,
    // TODO(b/113855296): Support other states: hover, focus, drag
) {
    /**
     * Represents the container color for this chip, depending on [enabled] and [selected].
     *
     * @param enabled whether the chip is enabled
     * @param selected whether the chip is selected
     */
    @Composable
    internal fun containerColor(
        enabled: Boolean,
        selected: Boolean,
    ): State<Color> {
        val target =
            when {
                !enabled -> if (selected) disabledSelectedContainerColor else disabledContainerColor
                !selected -> containerColor
                else -> selectedContainerColor
            }
        return rememberUpdatedState(target)
    }

    /**
     * Represents the label color for this chip, depending on [enabled] and [selected].
     *
     * @param enabled whether the chip is enabled
     * @param selected whether the chip is selected
     */
    @Composable
    internal fun labelColor(
        enabled: Boolean,
        selected: Boolean,
    ): State<Color> {
        val target =
            when {
                !enabled -> disabledLabelColor
                !selected -> labelColor
                else -> selectedLabelColor
            }
        return rememberUpdatedState(target)
    }

    /**
     * Represents the leading icon color for this chip, depending on [enabled] and [selected].
     *
     * @param enabled whether the chip is enabled
     * @param selected whether the chip is selected
     */
    @Composable
    internal fun leadingIconContentColor(
        enabled: Boolean,
        selected: Boolean,
    ): State<Color> {
        val target =
            when {
                !enabled -> disabledLeadingIconColor
                !selected -> leadingIconColor
                else -> selectedLeadingIconColor
            }
        return rememberUpdatedState(target)
    }

    /**
     * Represents the trailing icon color for this chip, depending on [enabled] and [selected].
     *
     * @param enabled whether the chip is enabled
     * @param selected whether the chip is selected
     */
    @Composable
    internal fun trailingIconContentColor(
        enabled: Boolean,
        selected: Boolean,
    ): State<Color> {
        val target =
            when {
                !enabled -> disabledTrailingIconColor
                !selected -> trailingIconColor
                else -> selectedTrailingIconColor
            }
        return rememberUpdatedState(target)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is SelectableChipColors) return false

        if (containerColor != other.containerColor) return false
        if (labelColor != other.labelColor) return false
        if (leadingIconColor != other.leadingIconColor) return false
        if (trailingIconColor != other.trailingIconColor) return false
        if (disabledContainerColor != other.disabledContainerColor) return false
        if (disabledLabelColor != other.disabledLabelColor) return false
        if (disabledLeadingIconColor != other.disabledLeadingIconColor) return false
        if (disabledTrailingIconColor != other.disabledTrailingIconColor) return false
        if (selectedContainerColor != other.selectedContainerColor) return false
        if (disabledSelectedContainerColor != other.disabledSelectedContainerColor) return false
        if (selectedLabelColor != other.selectedLabelColor) return false
        if (selectedLeadingIconColor != other.selectedLeadingIconColor) return false
        if (selectedTrailingIconColor != other.selectedTrailingIconColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = containerColor.hashCode()
        result = 31 * result + labelColor.hashCode()
        result = 31 * result + leadingIconColor.hashCode()
        result = 31 * result + trailingIconColor.hashCode()
        result = 31 * result + disabledContainerColor.hashCode()
        result = 31 * result + disabledLabelColor.hashCode()
        result = 31 * result + disabledLeadingIconColor.hashCode()
        result = 31 * result + disabledTrailingIconColor.hashCode()
        result = 31 * result + selectedContainerColor.hashCode()
        result = 31 * result + disabledSelectedContainerColor.hashCode()
        result = 31 * result + selectedLabelColor.hashCode()
        result = 31 * result + selectedLeadingIconColor.hashCode()
        result = 31 * result + selectedTrailingIconColor.hashCode()

        return result
    }
}

/**
 * Represents the border stroke used used in a selectable chip in different states.
 */
@Immutable
class SelectableChipBorder internal constructor(
    private val borderColor: Color,
    private val selectedBorderColor: Color,
    private val disabledBorderColor: Color,
    private val disabledSelectedBorderColor: Color,
    private val borderWidth: Dp,
    private val selectedBorderWidth: Dp,
) {
    /**
     * Represents the [BorderStroke] stroke used for this chip, depending on [enabled] and
     * [selected].
     *
     * @param enabled whether the chip is enabled
     * @param selected whether the chip is selected
     */
    @Composable
    internal fun borderStroke(
        enabled: Boolean,
        selected: Boolean,
    ): State<BorderStroke?> {
        val color =
            if (enabled) {
                if (selected) selectedBorderColor else borderColor
            } else {
                if (selected) disabledSelectedBorderColor else disabledBorderColor
            }
        return rememberUpdatedState(
            BorderStroke(if (selected) selectedBorderWidth else borderWidth, color),
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is SelectableChipBorder) return false

        if (borderColor != other.borderColor) return false
        if (selectedBorderColor != other.selectedBorderColor) return false
        if (disabledBorderColor != other.disabledBorderColor) return false
        if (disabledSelectedBorderColor != other.disabledSelectedBorderColor) return false
        if (borderWidth != other.borderWidth) return false
        if (selectedBorderWidth != other.selectedBorderWidth) return false

        return true
    }

    override fun hashCode(): Int {
        var result = borderColor.hashCode()
        result = 31 * result + selectedBorderColor.hashCode()
        result = 31 * result + disabledBorderColor.hashCode()
        result = 31 * result + disabledSelectedBorderColor.hashCode()
        result = 31 * result + borderWidth.hashCode()
        result = 31 * result + selectedBorderWidth.hashCode()

        return result
    }
}

/**
 * Represents the border stroke used in a chip in different states.
 */
@Immutable
class ChipBorder internal constructor(
    private val borderColor: Color,
    private val disabledBorderColor: Color,
    private val borderWidth: Dp,
) {
    /**
     * Represents the [BorderStroke] for this chip, depending on [enabled].
     *
     * @param enabled whether the chip is enabled
     */
    @Composable
    internal fun borderStroke(enabled: Boolean): State<BorderStroke?> {
        return rememberUpdatedState(
            BorderStroke(borderWidth, if (enabled) borderColor else disabledBorderColor),
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is ChipBorder) return false

        if (borderColor != other.borderColor) return false
        if (disabledBorderColor != other.disabledBorderColor) return false
        if (borderWidth != other.borderWidth) return false

        return true
    }

    override fun hashCode(): Int {
        var result = borderColor.hashCode()
        result = 31 * result + disabledBorderColor.hashCode()
        result = 31 * result + borderWidth.hashCode()

        return result
    }
}

/**
 * Returns the [PaddingValues] for the input chip.
 */
private fun inputChipPadding(
    hasAvatar: Boolean = false,
    hasLeadingIcon: Boolean = false,
    hasTrailingIcon: Boolean = false,
): PaddingValues {
    val start = if (hasAvatar || !hasLeadingIcon) 4.dp else 8.dp
    val end = if (hasTrailingIcon) 8.dp else 4.dp
    return PaddingValues(start = start, end = end)
}

/**
 * The padding between the elements in the chip.
 */
private val HorizontalElementsPadding = 8.dp

/**
 * Returns the [PaddingValues] for the assist chip.
 */
private val AssistChipPadding = PaddingValues(horizontal = HorizontalElementsPadding)

/**
 * [PaddingValues] for the filter chip.
 */
private val FilterChipPadding = PaddingValues(horizontal = HorizontalElementsPadding)

/**
 * Returns the [PaddingValues] for the suggestion chip.
 */
private val SuggestionChipPadding = PaddingValues(horizontal = HorizontalElementsPadding)
