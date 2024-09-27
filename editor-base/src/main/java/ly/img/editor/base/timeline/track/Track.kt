package ly.img.editor.base.timeline.track

import androidx.compose.runtime.mutableStateListOf
import ly.img.editor.base.timeline.clip.Clip
import java.util.UUID

data class Track(val id: String = UUID.randomUUID().toString(), val clips: MutableList<Clip> = mutableStateListOf())
