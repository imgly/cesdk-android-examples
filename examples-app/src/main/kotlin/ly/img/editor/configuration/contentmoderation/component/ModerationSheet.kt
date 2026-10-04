package ly.img.editor.configuration.contentmoderation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ly.img.editor.configuration.contentmoderation.ContentModerationConfigurationBuilder
import ly.img.editor.configuration.contentmoderation.iconPack.IconPack
import ly.img.editor.configuration.contentmoderation.iconPack.ShieldCheck
import ly.img.editor.configuration.contentmoderation.moderateContent
import ly.img.editor.configuration.contentmoderation.moderation.ModerationCategory
import ly.img.editor.configuration.contentmoderation.moderation.ModerationReport
import ly.img.editor.configuration.contentmoderation.moderation.ModerationResult
import ly.img.editor.configuration.contentmoderation.moderation.ModerationState
import ly.img.editor.configuration.contentmoderation.moderation.selectBlock
import ly.img.editor.core.EditorScope
import ly.img.editor.core.component.data.Height
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.iconpack.Close
import ly.img.editor.core.sheet.SheetStyle
import ly.img.editor.core.sheet.SheetType
import ly.img.engine.DesignBlock
import ly.img.editor.core.iconpack.IconPack as CoreIconPack

// Legend colors: red for a certain violation (FAILED), orange for a likely one (WARNING).
private val CertainColor = Color(0xFFE5484D)
private val LikelyColor = Color(0xFFF5A623)

/**
 * Builds the event that opens the content moderation results as a floating bottom sheet.
 *
 * The sheet has three states: an intro prompting the user to run a check, a loading state while
 * the check runs, and a results state listing the flagged categories. When the scene has not changed
 * since the last check, it opens straight to the cached results.
 *
 * @param builder the configuration builder, used to reach the moderation provider and cache.
 * @param autoRun when `true`, the check starts as soon as the sheet opens instead of waiting
 * for the user to tap "Run Moderation". Used by the export warning's "Moderate now" action.
 */
fun createModerationSheetOpenEvent(
    builder: ContentModerationConfigurationBuilder,
    autoRun: Boolean = false,
): EditorEvent.Sheet.Open = EditorEvent.Sheet.Open(
    SheetType.Custom(
        style = SheetStyle(
            isFloating = true,
            maxHeight = Height.Fraction(0.7f),
            isHalfExpandingEnabled = true,
        ),
        content = {
            ModerationSheetContent(builder = builder, autoRun = autoRun)
        },
    ),
)

@Composable
private fun EditorScope.ModerationSheetContent(
    builder: ContentModerationConfigurationBuilder,
    autoRun: Boolean,
) {
    val coroutineScope = rememberCoroutineScope()
    var report by remember { mutableStateOf<ModerationReport?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var job by remember { mutableStateOf<Job?>(null) }

    fun runCheck(force: Boolean) {
        job?.cancel()
        job = coroutineScope.launch {
            isLoading = true
            errorMessage = null
            try {
                report = builder.moderateContent(force = force)
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (throwable: Throwable) {
                report = null
                errorMessage = throwable.message ?: "The moderation service could not be reached. Please try again later."
            } finally {
                isLoading = false
            }
        }
    }

    // Track the sheet's open state so the navigation bar icon fills while it is open and the icon
    // can toggle it closed.
    DisposableEffect(Unit) {
        builder.isModerationSheetOpen = true
        onDispose { builder.isModerationSheetOpen = false }
    }

    // When opened from the export warning's "Moderate now" action, start the check right away.
    // Otherwise show cached results immediately when the scene has not changed, or wait for the user
    // to tap "Run Moderation".
    LaunchedEffect(Unit) {
        when {
            autoRun -> runCheck(force = false)
            builder.resultsFresh && builder.cachedResults != null -> report = builder.cachedResults
        }
    }

    val currentError = errorMessage
    val currentReport = report

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "Content Moderation",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            IconButton(onClick = { editorContext.eventHandler.send(EditorEvent.Sheet.Close(animate = true)) }) {
                Icon(
                    imageVector = CoreIconPack.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        when {
            isLoading -> LoadingState(onCancel = { job?.cancel() })
            currentError != null -> ErrorState(message = currentError, onRetry = { runCheck(force = true) })
            currentReport == null -> IntroState(onRun = { runCheck(force = false) })
            else -> ResultsState(
                flagged = currentReport.results.filter { it.category.state != ModerationState.SUCCESS },
                uncheckedCount = currentReport.unchecked.size,
                // Rerunning only makes sense after the scene changes. While the shown results still
                // reflect the current scene (nothing edited since), the check would just repeat the
                // same outcome — whether it found violations or came back clean — so disable it.
                rerunEnabled = !builder.resultsFresh,
                onRerun = { runCheck(force = true) },
                onSelect = { block ->
                    selectBlock(editorContext.engine, block)
                    editorContext.eventHandler.send(EditorEvent.Sheet.Close(animate = true))
                },
            )
        }
    }
}

@Composable
private fun IntroState(onRun: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            imageVector = IconPack.ShieldCheck,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(40.dp),
        )
        Text(
            text = "Check your design for content that may violate the rules before exporting.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Button(onClick = onRun, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Run Moderation")
        }
    }
}

@Composable
private fun LoadingState(onCancel: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        CircularProgressIndicator()
        Text(
            text = "Moderating content…",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        FilledTonalButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Cancel")
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = "Content check unavailable",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        ) {
            Text(text = "Rerun Moderation")
        }
    }
}

@Composable
private fun ResultsState(
    flagged: List<ModerationResult>,
    uncheckedCount: Int,
    rerunEnabled: Boolean,
    onRerun: () -> Unit,
    onSelect: (DesignBlock) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = IconPack.ShieldCheck,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = summarize(flagged, uncheckedCount),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant),
        )

        if (uncheckedCount > 0) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                text = "$uncheckedCount ${if (uncheckedCount == 1) "block" else "blocks"} could not be checked, " +
                    "so this scene is not confirmed clean. Run the check again.",
                style = MaterialTheme.typography.bodySmall,
                color = CertainColor,
            )
        }

        if (flagged.isEmpty() && uncheckedCount == 0) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                text = "Every image and text block was checked and nothing was flagged. You're good to go.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            // One entry per block: a block that violates several categories gets a single heading
            // and a single Select action, with its categories listed underneath.
            val groups = flagged.groupBy { it.block }.entries.toList()
            val labels = numberRepeats(groups.map { it.value.first().blockLabel })
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 280.dp),
            ) {
                itemsIndexed(items = groups, key = { _, group -> group.key }) { index, group ->
                    ModerationBlockGroup(
                        label = labels[index],
                        categories = group.value.map { it.category },
                        showDivider = index < groups.lastIndex,
                        onSelect = { onSelect(group.key) },
                    )
                }
            }
        }

        if (flagged.isNotEmpty()) {
            ProbabilityLegend()
        }

        Button(
            onClick = onRerun,
            enabled = rerunEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        ) {
            Text(text = "Rerun Moderation")
        }
    }
}

@Composable
private fun ProbabilityLegend() {
    Row(
        modifier = Modifier.padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Probability:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.width(12.dp))
        LegendEntry(color = CertainColor, label = "Certain")
        Spacer(modifier = Modifier.width(16.dp))
        LegendEntry(color = LikelyColor, label = "Likely")
    }
}

@Composable
private fun LegendEntry(
    color: Color,
    label: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Dot(color = color)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ModerationBlockGroup(
    label: String,
    categories: List<ModerationCategory>,
    showDivider: Boolean,
    onSelect: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(onClick = onSelect) {
                Text(text = "Select")
            }
        }
        // Categories sit closer to each other than groups do to each other, so a block and its
        // violations read as one unit.
        Column(
            modifier = Modifier.padding(top = 4.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            categories.forEach { category ->
                ModerationCategoryRow(category = category)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant),
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ModerationCategoryRow(category: ModerationCategory) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Nudge the dot down onto the first text line rather than centring it on the whole row.
        Box(modifier = Modifier.padding(top = 6.dp)) {
            Dot(color = category.state.indicatorColor())
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = category.displayName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = category.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/**
 * Numbers labels that occur more than once, so two unnamed images read "Image 1" and "Image 2"
 * instead of two identical headings. Labels that are already unique are left alone.
 */
private fun numberRepeats(labels: List<String>): List<String> {
    val totals = labels.groupingBy { it }.eachCount()
    val seen = mutableMapOf<String, Int>()
    return labels.map { label ->
        if (totals.getValue(label) == 1) {
            label
        } else {
            val ordinal = seen.getOrDefault(label, 0) + 1
            seen[label] = ordinal
            "$label $ordinal"
        }
    }
}

/**
 * Summarizes the flagged results, counting both the violations and the blocks they sit on, because
 * one block can violate several categories at once.
 */
private fun summarize(
    flagged: List<ModerationResult>,
    uncheckedCount: Int,
): String {
    if (flagged.isEmpty()) return if (uncheckedCount > 0) "Incomplete check" else "No issues found"
    val blocks = flagged.distinctBy { it.block }.size
    val issueLabel = if (flagged.size == 1) "issue" else "issues"
    val blockLabel = if (blocks == 1) "block" else "blocks"
    return "${flagged.size} $issueLabel · $blocks $blockLabel"
}

@Composable
private fun Dot(color: Color) {
    Box(
        modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(color),
    )
}

private fun ModerationState.indicatorColor(): Color = when (this) {
    ModerationState.FAILED -> CertainColor
    // Only flagged categories reach the list, so WARNING is the mildest state that can render.
    else -> LikelyColor
}
