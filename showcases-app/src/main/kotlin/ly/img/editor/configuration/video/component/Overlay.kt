@file:Suppress("UnusedReceiverParameter")

package ly.img.editor.configuration.video.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import ly.img.editor.configuration.video.VideoConfigurationBuilder
import ly.img.editor.configuration.video.iconPack.CheckCircleOutline
import ly.img.editor.configuration.video.iconPack.ErrorOutline
import ly.img.editor.configuration.video.iconPack.IconPack
import ly.img.editor.configuration.video.model.ExportStatus
import ly.img.editor.core.R
import ly.img.editor.core.component.EditorComponent
import ly.img.editor.core.component.remember
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.theme.LocalExtendedColorScheme

/**
 * The configuration of the component that is displayed over the editor. It is useful if you want to display a popup dialog or anything in the
 * overlay.
 */
@Composable
fun VideoConfigurationBuilder.rememberOverlay() = EditorComponent.remember {
    decoration = {
        // Other than the main overlay also display export related overlays
        Overlay()
        if (exportStatus != null) {
            ExportOverlay()
        }
    }
}

/**
 * The export related overlay composable.
 *
 * @param loading the export loading composable.
 * @param success the export success composable.
 * @param error the export error composable.
 */
@Composable
fun VideoConfigurationBuilder.ExportOverlay(
    loading: @Composable VideoConfigurationBuilder.(ExportStatus.Loading) -> Unit = {
        ExportLoading(it)
    },
    success: @Composable VideoConfigurationBuilder.(ExportStatus.Success) -> Unit = {
        ExportSuccess(it)
    },
    error: @Composable VideoConfigurationBuilder.(ExportStatus.Error) -> Unit = {
        ExportError()
    },
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.32f))
            .pointerInput(Unit) {
                detectTapGestures {
                    // do nothing
                }
            },
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart),
            shape = RoundedCornerShape(
                topStart = 28.0.dp,
                topEnd = 28.0.dp,
                bottomEnd = 0.0.dp,
                bottomStart = 0.0.dp,
            ),
            shadowElevation = 16.dp,
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(all = 16.dp),
            ) {
                val localExportStatus = exportStatus
                when (localExportStatus) {
                    is ExportStatus.Loading -> loading(localExportStatus)
                    is ExportStatus.Success -> success(localExportStatus)
                    is ExportStatus.Error -> error(localExportStatus)
                    null -> {} // not possible
                }
            }
        }
    }
}

/**
 * The export loading composable.
 *
 * @param status the current status of the export.
 */
@Composable
fun VideoConfigurationBuilder.ExportLoading(status: ExportStatus.Loading) {
    ExportOverlayContent(
        title = stringResource(R.string.ly_img_editor_dialog_export_progress_title),
        text = stringResource(R.string.ly_img_editor_dialog_export_progress_text),
        button = stringResource(R.string.ly_img_editor_dialog_export_progress_button_dismiss),
        buttonColor = MaterialTheme.colorScheme.error,
        onClick = { showCancelExportConfirmationDialog = true },
        centerContent = {
            Box {
                ExportProgressIndicator(progress = status.progress)
                Text(
                    text = "${(status.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        },
    )
    if (showCancelExportConfirmationDialog) {
        CancelExportConfirmationDialog()
    }
}

/**
 * The export success composable.
 *
 * @param status the current status of the export.
 */
@Composable
fun VideoConfigurationBuilder.ExportSuccess(status: ExportStatus.Success) {
    ExportOverlayContent(
        title = stringResource(R.string.ly_img_editor_dialog_export_success_title),
        text = stringResource(R.string.ly_img_editor_dialog_export_success_text),
        button = stringResource(R.string.ly_img_editor_dialog_export_success_button_dismiss),
        onClick = {
            exportStatus = null
            shareFile(file = status.file, mimeType = status.mimeType)
        },
        centerContent = {
            Icon(
                imageVector = IconPack.CheckCircleOutline,
                contentDescription = null,
                modifier = Modifier.size(144.dp),
                tint = LocalExtendedColorScheme.current.green.color,
            )
        },
    )
}

/**
 * The error loading composable.
 */
@Composable
fun VideoConfigurationBuilder.ExportError() {
    ExportOverlayContent(
        title = stringResource(R.string.ly_img_editor_dialog_export_error_title),
        text = stringResource(R.string.ly_img_editor_dialog_export_error_text),
        button = stringResource(R.string.ly_img_editor_dialog_export_error_button_dismiss),
        onClick = { exportStatus = null },
        centerContent = {
            Icon(
                imageVector = IconPack.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(144.dp),
                tint = MaterialTheme.colorScheme.error,
            )
        },
    )
}

/**
 * The confirmation dialog to cancel the export process.
 */
@Composable
fun VideoConfigurationBuilder.CancelExportConfirmationDialog() {
    AlertDialog(
        icon = {
            Icon(
                imageVector = IconPack.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
            )
        },
        title = {
            Text(text = stringResource(R.string.ly_img_editor_dialog_export_cancel_title))
        },
        text = {
            Text(text = stringResource(R.string.ly_img_editor_dialog_export_cancel_text))
        },
        confirmButton = {
            TextButton(
                onClick = {
                    showCancelExportConfirmationDialog = false
                    editorContext.eventHandler.send(EditorEvent.Export.Cancel())
                },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                ),
            ) {
                Text(stringResource(R.string.ly_img_editor_dialog_export_cancel_button_confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    showCancelExportConfirmationDialog = false
                },
            ) {
                Text(stringResource(R.string.ly_img_editor_dialog_export_cancel_button_dismiss))
            }
        },
        onDismissRequest = {
            showCancelExportConfirmationDialog = false
        },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
    )
}

/**
 * Generic export overlay content that should be used by all the states of the [ExportStatus].
 *
 * @param modifier the modifier.
 * @param title the title of the export state.
 * @param text the title of the export state.
 * @param button the action button of the export state.
 */
@Composable
fun VideoConfigurationBuilder.ExportOverlayContent(
    modifier: Modifier = Modifier.fillMaxWidth(),
    title: String,
    text: String,
    button: String,
    buttonColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit,
    centerContent: @Composable () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        centerContent()
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(24.dp))
        TextButton(
            onClick = onClick,
            colors = ButtonDefaults.textButtonColors(
                contentColor = buttonColor,
            ),
        ) {
            Text(button)
        }
    }
}

/**
 * Circular export progress indicator.
 *
 * @param progress the progress of the export, between 0F and 1F.
 * @param fillColor the fill color of the circle.
 * @param gradientColor the gradient color of the circle.
 * @param strokeWidth the stroke width of the circle.
 * @param strokeWidth the size (width/height) of the circle.
 */
@Composable
fun VideoConfigurationBuilder.ExportProgressIndicator(
    progress: Float,
    fillColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    gradientColor: Color = MaterialTheme.colorScheme.onSurface,
    strokeWidth: Dp = 8.dp,
    size: Dp = 144.dp,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    )

    val stroke = with(LocalDensity.current) {
        Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
    }

    val transition = rememberInfiniteTransition()
    val rotationAngle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
        ),
        label = "Gradient Rotation",
    )

    val sweepGradient = remember(gradientColor) {
        Brush.sweepGradient(
            colorStops = listOf(
                0.02f to Color.Transparent,
                0.5f to gradientColor.copy(alpha = 0.24f),
                1f to Color.Transparent,
            ).toTypedArray(),
        )
    }

    Canvas(
        modifier = Modifier
            .progressSemantics(animatedProgress)
            .size(size - strokeWidth * 2),
    ) {
        drawSweepGradientCircularIndicator(
            rotationAngle = rotationAngle,
            stroke = stroke,
            gradientBrush = sweepGradient,
        )

        drawCircularIndicator(
            startAngle = 270f,
            sweep = animatedProgress * 360f,
            color = fillColor,
            stroke = stroke,
        )
    }
}

private fun DrawScope.drawSweepGradientCircularIndicator(
    rotationAngle: Float,
    stroke: Stroke,
    gradientBrush: Brush,
) {
    rotate(-180f + rotationAngle) {
        val diameterOffset = stroke.width / 2
        val arcDimen = size.width - 2 * diameterOffset
        drawArc(
            brush = gradientBrush,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(diameterOffset, diameterOffset),
            style = stroke,
            size = Size(arcDimen, arcDimen),
        )
    }
}

private fun DrawScope.drawCircularIndicator(
    startAngle: Float,
    sweep: Float,
    color: Color,
    stroke: Stroke,
) {
    // To draw this circle we need a rect with edges that line up with the midpoint of the stroke.
    // To do this we need to remove half the stroke width from the total diameter for both sides.
    val diameterOffset = stroke.width / 2
    val arcDimen = size.width - 2 * diameterOffset
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweep,
        useCenter = false,
        topLeft = Offset(diameterOffset, diameterOffset),
        size = Size(arcDimen, arcDimen),
        style = stroke,
    )
}
