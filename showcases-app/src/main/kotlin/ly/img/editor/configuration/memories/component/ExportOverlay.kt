package ly.img.editor.configuration.memories.component

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ly.img.editor.configuration.memories.MemoriesConfiguration
import ly.img.editor.configuration.memories.iconPack.CheckCircle
import ly.img.editor.configuration.memories.iconPack.Info
import ly.img.editor.configuration.memories.model.ExportStatus
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.iconpack.IconPack

/** A bottom sheet over the editor showing export progress, then a share button (or an error). */
@Composable
fun MemoriesConfiguration.ExportOverlay() {
    val status = exportStatus ?: return
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.32f))
            .pointerInput(Unit) { detectTapGestures { } },
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomStart),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            shadowElevation = 16.dp,
        ) {
            Box(Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp)) {
                when (status) {
                    is ExportStatus.Loading -> ExportLoading(status)
                    is ExportStatus.Success -> ExportContent(
                        center = { ExportIcon(IconPack.CheckCircle, MaterialTheme.colorScheme.primary) },
                        title = "Ready to share",
                        text = "Your memory has been exported.",
                        button = "Share",
                        onClick = {
                            exportStatus = null
                            shareFile(file = status.file, mimeType = status.mimeType)
                        },
                    )
                    is ExportStatus.Error -> ExportContent(
                        center = { ExportIcon(IconPack.Info, MaterialTheme.colorScheme.error) },
                        title = "Export failed",
                        text = "Something went wrong while exporting.",
                        button = "Dismiss",
                        onClick = { exportStatus = null },
                    )
                }
            }
        }
    }
}

@Composable
private fun MemoriesConfiguration.ExportLoading(status: ExportStatus.Loading) {
    ExportContent(
        center = {
            Box(contentAlignment = Alignment.Center) {
                ExportProgressIndicator(progress = status.progress)
                Text(
                    text = "${(status.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        title = "Exporting",
        text = "Creating your video memory.",
        button = "Cancel",
        buttonColor = MaterialTheme.colorScheme.error,
        onClick = { editorContext.eventHandler.send(EditorEvent.Export.Cancel()) },
    )
}

@Composable
private fun ExportIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
) {
    Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(144.dp), tint = tint)
}

@Composable
private fun ExportContent(
    center: @Composable () -> Unit,
    title: String,
    text: String,
    button: String,
    buttonColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(Modifier.height(24.dp))
        center()
        Spacer(Modifier.height(24.dp))
        Text(
            title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
        TextButton(onClick = onClick, colors = ButtonDefaults.textButtonColors(contentColor = buttonColor)) {
            Text(button)
        }
    }
}

/** A ring that fills with [progress] (0..1), with a softly rotating gradient backdrop. */
@Composable
private fun ExportProgressIndicator(
    progress: Float,
    fillColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    gradientColor: Color = MaterialTheme.colorScheme.onSurface,
    strokeWidth: Dp = 8.dp,
    size: Dp = 144.dp,
) {
    val animatedProgress by animateFloatAsState(progress, ProgressIndicatorDefaults.ProgressAnimationSpec, label = "progress")
    val rotation by rememberInfiniteTransition(label = "ring").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing)),
        label = "rotation",
    )
    val gradient = Brush.sweepGradient(
        0.02f to Color.Transparent,
        0.5f to gradientColor.copy(alpha = 0.24f),
        1f to Color.Transparent,
    )

    Canvas(Modifier.size(size - strokeWidth * 2)) {
        val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        rotate(-180f + rotation) { arc(gradient = gradient, startAngle = 0f, sweep = 180f, stroke = stroke) }
        arc(color = fillColor, startAngle = 270f, sweep = animatedProgress * 360f, stroke = stroke)
    }
}

private fun DrawScope.arc(
    color: Color,
    startAngle: Float,
    sweep: Float,
    stroke: Stroke,
) {
    val inset = stroke.width / 2
    drawArc(
        color,
        startAngle,
        sweep,
        useCenter = false,
        topLeft = Offset(inset, inset),
        size = Size(
            size.width - 2 * inset,
            size.height - 2 * inset,
        ),
        style = stroke,
    )
}

private fun DrawScope.arc(
    gradient: Brush,
    startAngle: Float,
    sweep: Float,
    stroke: Stroke,
) {
    val inset = stroke.width / 2
    drawArc(
        gradient,
        startAngle,
        sweep,
        useCenter = false,
        topLeft = Offset(inset, inset),
        size = Size(
            size.width - 2 * inset,
            size.height - 2 * inset,
        ),
        style = stroke,
    )
}
