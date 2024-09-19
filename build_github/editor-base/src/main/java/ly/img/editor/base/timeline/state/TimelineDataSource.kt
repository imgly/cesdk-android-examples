package ly.img.editor.base.timeline.state

import androidx.compose.runtime.mutableStateListOf
import ly.img.editor.base.timeline.clip.Clip
import ly.img.editor.base.timeline.track.Track
import ly.img.engine.DesignBlock

class TimelineDataSource {
    private val _tracks = mutableStateListOf<Track>()
    val tracks: List<Track>
        get() = _tracks.toList()

    val backgroundTrack = Track()

    fun addTrack(track: Track) {
        _tracks.add(0, track)
    }

    fun addAudioTrack(track: Track) {
        _tracks.add(track)
    }

    fun findClip(block: DesignBlock): Clip? {
        val find: ((Clip) -> Boolean) = {
            it.id == block || it.trimmableId == block || it.fillId == block || it.shapeId == block || it.blurId == block ||
                it.effectIds?.contains(block) == true
        }
        // Search in tracks
        for (track in _tracks) {
            val result = track.clips.find(find)
            if (result != null) return result
        }
        // Finally search in background track
        return backgroundTrack.clips.find(find)
    }

    fun allClips(): List<Clip> {
        return backgroundTrack.clips + tracks.flatMap { it.clips }
    }

    fun findTrack(clip: Clip): Track {
        return checkNotNull(
            tracks.find {
                it.clips.contains(clip)
            },
        )
    }

    fun indexOf(clip: Clip): Int {
        return tracks.indexOfFirst {
            it.clips.contains(clip)
        }
    }

    fun reset() {
        _tracks.clear()
        backgroundTrack.clips.clear()
    }

    override fun toString(): String {
        return "TimelineDataSource: \n tracks=${
            tracks.flatMap { it.clips }.joinToString("\n")
        } \n backgroundTrack=${
            backgroundTrack.clips.joinToString("\n")
        }"
    }
}
