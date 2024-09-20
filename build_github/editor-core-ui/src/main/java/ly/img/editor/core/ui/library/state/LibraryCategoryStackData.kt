package ly.img.editor.core.ui.library.state

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import ly.img.editor.core.library.LibraryContent
import java.util.Stack

internal data class LibraryCategoryStackData(
    val dataStack: Stack<LibraryContent>,
    val uiStateFlow: MutableStateFlow<AssetLibraryUiState>,
    var dirty: Boolean = false,
    var fetchJob: Job? = null,
    var searchJob: Job? = null,
)
