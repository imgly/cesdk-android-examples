package ly.img.editor.configuration.memories.scene

import android.util.Log
import ly.img.editor.configuration.memories.util.IMAGE_DURATION
import ly.img.editor.configuration.memories.util.OVERLAP_DURATION
import ly.img.editor.configuration.memories.util.TITLE_DURATION
import ly.img.engine.DesignBlock
import ly.img.engine.Engine
import ly.img.engine.TransitionType

/**
 * The blend between one slide and the next.
 *
 * A transition belongs to the **outgoing** clip and blends it into the next clip on the same track,
 * so the whole montage lives on one media track (see [createMainImageSequence]). Assigning one
 * overlaps the pair and pulls every later clip earlier: the engine owns the timeline, which is why
 * nothing here computes a start time. It also clamps the overlap to half of the shorter neighbour,
 * so a clip shorter than [TransitionSpec.duration] needs no special case — a 4s video between 8s
 * photos gets 2s blends on both sides without being asked.
 */

private const val TAG = "MemoriesTransitions"

/**
 * A style's blend, applied to every boundary in the montage. [configure] sets the per-type
 * properties, which live under `transition/{type}/{property}` keypaths — e.g.
 * `transition/color-wipe/direction`. Each type exposes its own set; `findAllProperties` on a
 * created transition lists them.
 */
data class TransitionSpec(
    /** The blend the engine draws between two clips. */
    val type: TransitionType,
    /** How long the two clips overlap, in seconds, before the engine's clamp applies. */
    val duration: Double = OVERLAP_DURATION,
    /** Sets the type's own properties on a freshly created transition block. */
    val configure: (Engine, DesignBlock) -> Unit = { _, _ -> },
)

/**
 * Blend each clip into the next with [spec]. Videos are included: a clip in a video track supports a
 * transition whatever it shows, so photo↔video boundaries blend like any other.
 *
 * [clips] must be in slot order and already appended to the media track. Re-wiring is safe — the
 * previous transitions are detached and destroyed first.
 */
internal fun wireTransitions(
    engine: Engine,
    clips: List<DesignBlock>,
    spec: TransitionSpec,
) {
    clearTransitions(engine, clips)
    for (index in 0 until clips.size - 1) {
        val outgoing = clips[index]
        if (!engine.block.supportsTransition(outgoing)) {
            Log.w(TAG, "slot $index takes no transition; that boundary stays a hard cut")
            continue
        }
        val transition = engine.block.createTransition(spec.type)
        engine.block.setDuration(transition, spec.duration)
        spec.configure(engine, transition)
        engine.block.setTransition(outgoing, transition)
    }
}

/**
 * Detach and destroy the transition each clip owns. `removeTransition` only detaches, so the block
 * stays alive until it is destroyed — re-wiring without this leaks one transition per style change.
 */
internal fun clearTransitions(
    engine: Engine,
    clips: List<DesignBlock>,
) {
    clips.forEach { clip ->
        val existing = runCatching { engine.block.getTransition(clip) }.getOrDefault(-1)
        if (existing == -1 || !runCatching { engine.block.isValid(existing) }.getOrDefault(false)) return@forEach
        engine.block.removeTransition(clip)
        engine.block.destroy(existing)
    }
}

/**
 * Where the montage ends in **page** time, read back from the engine after it has applied every
 * overlap. A title keeps a 1s tail after the last clip; an empty montage keeps one slot of page so
 * the editor still has a timeline to show.
 *
 * A clip's offset is relative to [track], which itself sits at the title gap, so the two add up —
 * dropping the track offset cuts the montage short by exactly that gap.
 *
 * The engine is the source of truth: the overlap it grants a boundary depends on the clamp, so the
 * end time cannot be predicted from the requested duration alone.
 */
internal fun reflowedEndTime(
    engine: Engine,
    track: DesignBlock,
    clips: List<DesignBlock>,
    hasTitle: Boolean,
): Double {
    val last = clips.lastOrNull() ?: return if (hasTitle) TITLE_DURATION else IMAGE_DURATION
    val end = runCatching {
        // Read the track's offset first, otherwise the clip values below are stale: asking a track
        // for its time offset is what runs the reflow, so the clips have not moved until it returns.
        val trackOffset = engine.block.getTimeOffset(track)
        trackOffset + engine.block.getTimeOffset(last) + engine.block.getDuration(last)
    }.getOrDefault(0.0)
    return end + if (hasTitle) 1.0 else 0.0
}
