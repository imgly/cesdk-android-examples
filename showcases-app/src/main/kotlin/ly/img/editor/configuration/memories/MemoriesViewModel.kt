package ly.img.editor.configuration.memories

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ly.img.editor.configuration.memories.model.ImageItem
import ly.img.editor.configuration.memories.scene.applyDraftToTracks
import ly.img.editor.configuration.memories.scene.findTracks
import ly.img.editor.configuration.memories.scene.getPageVolume
import ly.img.editor.configuration.memories.scene.readImagesFromTracks
import ly.img.editor.configuration.memories.scene.setPageLooping
import ly.img.editor.configuration.memories.scene.setPageVolume
import ly.img.editor.configuration.memories.scene.togglePageLoop
import ly.img.editor.configuration.memories.style.VideoStyles
import ly.img.editor.configuration.memories.style.applyStyleToSlideshow
import ly.img.editor.configuration.memories.util.detectSortOrder
import ly.img.editor.configuration.memories.util.sortByDate
import ly.img.editor.core.EditorContext
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.state.EditorViewMode

private const val TAG = "MemoriesVM"

class MemoriesViewModel : ViewModel() {
    private var appContext: Context? = null

    fun setContext(context: Context) {
        appContext = context.applicationContext
    }

    fun getContext(): Context = appContext
        ?: throw IllegalStateException("Context not set in ViewModel")

    // Playback state
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    // Selected images for the video
    private val _selectedImages = MutableStateFlow<List<ImageItem>>(emptyList())
    val selectedImages: StateFlow<List<ImageItem>> = _selectedImages.asStateFlow()

    // Ids of images the user has marked for deletion (selection mode on the picker screen)
    private val _imagesToDelete = MutableStateFlow<Set<Int>>(emptySet())
    val imagesToDelete: StateFlow<Set<Int>> = _imagesToDelete.asStateFlow()

    // Multi-select mode: when on, tiles show checkboxes and delete removes the selection;
    // when off, delete clears all. Shared by the picker screen and the in-editor overlay.
    private val _multiSelectMode = MutableStateFlow(false)
    val multiSelectMode: StateFlow<Boolean> = _multiSelectMode.asStateFlow()

    // Image grid state (used in overlay during editing)
    private val _gridImages = MutableStateFlow<List<ImageItem>>(emptyList())
    val gridImages: StateFlow<List<ImageItem>> = _gridImages.asStateFlow()

    private val _showImageGrid = MutableStateFlow(false)
    val showImageGrid: StateFlow<Boolean> = _showImageGrid.asStateFlow()

    // Overlay (in-editor grid) staging state — edits are non-destructive until applied
    private val _overlaySortEnabled = MutableStateFlow(false)
    val overlaySortEnabled: StateFlow<Boolean> = _overlaySortEnabled.asStateFlow()

    private val _overlaySortAscending = MutableStateFlow(true)
    val overlaySortAscending: StateFlow<Boolean> = _overlaySortAscending.asStateFlow()

    // Last filter style applied to image blocks, re-applied after an in-place track rebuild
    private var activeFilter: String? = null

    /** The currently applied style id (Default until one is chosen), so the Styles sheet preselects it. */
    val activeStyleId: String get() = activeFilter ?: VideoStyles.DEFAULT.id

    // Picker thumbnails (style id -> thumbUri), read once from the custom style asset source.
    private val _styleThumbnails = MutableStateFlow<Map<String, String>>(emptyMap())
    val styleThumbnails: StateFlow<Map<String, String>> = _styleThumbnails.asStateFlow()

    /** Cache the style picker thumbnails resolved from the custom style asset source. */
    fun setStyleThumbnails(thumbnails: Map<String, String>) {
        _styleThumbnails.value = thumbnails
    }

    // Animation pairs
    private val _animationPairs =
        MutableStateFlow<List<ly.img.editor.configuration.memories.util.AnimationPair>>(emptyList())
    val animationPairs: StateFlow<List<ly.img.editor.configuration.memories.util.AnimationPair>> =
        _animationPairs.asStateFlow()

    // Video title
    private val _videoTitle = MutableStateFlow("")
    val videoTitle: StateFlow<String> = _videoTitle.asStateFlow()

    // Preview mode state management
    private val _isPreviewMode = MutableStateFlow(false)
    val isPreviewMode: StateFlow<Boolean> = _isPreviewMode.asStateFlow()

    // State to control when dock/bottomPanel should be hidden during preview transition
    private val _shouldHideUIForPreview = MutableStateFlow(false)
    val shouldHideUIForPreview: StateFlow<Boolean> = _shouldHideUIForPreview.asStateFlow()

    // Fullscreen mode state (for hiding system UI)
    private val _isFullscreen = MutableStateFlow(false)
    val isFullscreen: StateFlow<Boolean> = _isFullscreen.asStateFlow()

    // Editor loading state
    private val _isEditorLoading = MutableStateFlow(true)
    val isEditorLoading: StateFlow<Boolean> = _isEditorLoading.asStateFlow()

    // Loading start time for minimum duration calculation
    private val _loadingStartTime = MutableStateFlow(0L)
    val loadingStartTime: StateFlow<Long> = _loadingStartTime.asStateFlow()

    // Loop state management
    private val _isLooping = MutableStateFlow(true)
    val isLooping: StateFlow<Boolean> = _isLooping.asStateFlow()

    // Volume state management
    private val _volume = MutableStateFlow(1.0f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    // Sort functionality
    private val _isSortEnabled = MutableStateFlow(false)
    val isSortEnabled: StateFlow<Boolean> = _isSortEnabled.asStateFlow()

    private val _isSortAscending = MutableStateFlow(true)
    val isSortAscending: StateFlow<Boolean> = _isSortAscending.asStateFlow()

    // Editor context for direct editor control
    private var editorContext: EditorContext? = null

    // The in-flight "enter preview" transition, cancelled on the next toggle so a fast double-tap
    // can't apply a stale view-mode change after the UI state has flipped back.
    private var previewJob: Job? = null

    // Functions to update state
    fun setPlaying(playing: Boolean) {
        _isPlaying.value = playing
    }

    fun updateGridImages(images: List<ImageItem>) {
        _gridImages.value = images
        // Reflect the new order in the overlay sort button (a manual reorder turns it off).
        refreshOverlaySortState()
    }

    fun updateSelectedImages(images: List<ImageItem>) {
        _selectedImages.value = images
        // Also update grid images to match selected images initially
        _gridImages.value = images
        // Reflect the new order in the sort button: enabled (with direction) when the list is in
        // chronological order, off otherwise — so a manual reorder out of order turns it off.
        refreshSortState()
    }

    /**
     * Append newly-picked images to the selection. When sort is currently enabled the combined list
     * is re-sorted so the additions land in chronological order; otherwise they are appended as-is.
     * Either way [updateSelectedImages] then reconciles the sort button with the resulting order.
     */
    fun addSelectedImages(newImages: List<ImageItem>) {
        if (newImages.isEmpty()) return
        val combined = _selectedImages.value + newImages
        val next = if (_isSortEnabled.value) sortByDate(combined, _isSortAscending.value) else combined
        updateSelectedImages(next)
    }

    fun setAnimationPairs(pairs: List<ly.img.editor.configuration.memories.util.AnimationPair>) {
        _animationPairs.value = pairs
    }

    fun setVideoTitle(title: String) {
        _videoTitle.value = title
    }

    fun togglePreviewMode() {
        val currentPreviewMode = _isPreviewMode.value
        // Cancel any in-flight enter-preview transition before starting/reverting one.
        previewJob?.cancel()
        if (!currentPreviewMode) {
            // Entering preview mode: hide UI first, then enable preview after animation delay
            _shouldHideUIForPreview.value = true
            _isPreviewMode.value = true
            _isFullscreen.value = true // Hide system UI

            // Handle delayed preview mode transition in ViewModel scope
            previewJob = viewModelScope.launch {
                // Guarded like every other engine access here: the editor may be disposed inside the
                // 200ms window, so never let it throw uncaught in viewModelScope.
                runCatching {
                    editorContext?.let { context ->
                        context.engine.block.findAllSelected().forEach { selectedBlock ->
                            context.engine.block.setSelected(selectedBlock, false)
                        }
                        context.eventHandler.send(EditorEvent.Sheet.Close(false))
                        delay(200) // Animation duration for dock/bottomPanel
                        // Only enter preview if we are still meant to be in it (the cancel above
                        // usually covers a toggle-back, but re-check to be safe).
                        if (_isPreviewMode.value) {
                            context.eventHandler.send(EditorEvent.SetViewMode(EditorViewMode.Preview()))
                        }
                    }
                }.onFailure { Log.w(TAG, "Could not enter preview mode", it) }
            }
        } else {
            // Exiting preview mode: disable preview immediately, show UI after
            _isPreviewMode.value = false
            _shouldHideUIForPreview.value = false
            _isFullscreen.value = false // Show system UI

            // Set editor to edit mode immediately
            runCatching {
                editorContext?.eventHandler?.send(EditorEvent.SetViewMode(EditorViewMode.Edit()))
            }.onFailure { Log.w(TAG, "Could not exit preview mode", it) }
        }
    }

    fun setEditorLoading(loading: Boolean) {
        if (loading) {
            _loadingStartTime.value = System.currentTimeMillis()
        }
        _isEditorLoading.value = loading
    }

    fun toggleSort() {
        if (_isSortEnabled.value) {
            // Already in chronological order — flip the direction.
            _isSortAscending.value = !_isSortAscending.value
        } else {
            // List is out of order (e.g. after a manual drag): restore the last direction first
            // rather than jumping to the opposite one. A second tap then flips it.
            _isSortEnabled.value = true
        }
        applySortToImages()
    }

    fun clearSelectedImages() {
        _selectedImages.value = emptyList()
        _gridImages.value = emptyList()
        _imagesToDelete.value = emptySet()
        _isSortEnabled.value = false
    }

    /** Toggle multi-select mode; either transition resets the current selection to empty. */
    fun toggleMultiSelect() {
        _multiSelectMode.value = !_multiSelectMode.value
        _imagesToDelete.value = emptySet()
    }

    /** Toggle whether an image (by id) is marked for deletion. */
    fun toggleImageForDeletion(id: Int) {
        _imagesToDelete.value = _imagesToDelete.value.toMutableSet().apply {
            if (!add(id)) remove(id)
        }
    }

    /** Remove only the images currently marked for deletion. */
    fun deleteSelectedImages() {
        val toDelete = _imagesToDelete.value
        if (toDelete.isEmpty()) return
        val remaining = _selectedImages.value.filterNot { it.id in toDelete }
        _selectedImages.value = remaining
        _gridImages.value = remaining
        _imagesToDelete.value = emptySet()
        // Removing images can leave (or break) chronological order — keep the button in sync.
        refreshSortState()
    }

    // ---- In-editor overlay grid: non-destructive staging ---------------------------------------

    // ──────────────── In-editor overlay (non-destructive staging) ────────────────

    /** Open the overlay seeded from the tracks (the source of truth) and pause playback. */
    fun openImageGridFromTracks(editorContext: EditorContext) {
        val engine = editorContext.engine
        val page = engine.scene.getCurrentPage() ?: return
        val tracks = findTracks(engine, page) ?: return
        _gridImages.value = try {
            readImagesFromTracks(engine, tracks)
        } catch (e: Exception) {
            Log.e(TAG, "Error reading images from tracks", e)
            _gridImages.value
        }
        _imagesToDelete.value = emptySet()
        _multiSelectMode.value = false
        // Reflect the existing track order: if the images are already chronological, show it.
        _overlaySortAscending.value = true
        refreshOverlaySortState()
        _showImageGrid.value = true
        try {
            engine.scene.getCurrentPage()?.let {
                engine.block.setPlaying(it, false)
                _isPlaying.value = false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error pausing playback when opening overlay", e)
        }
    }

    /** Close the overlay discarding the staged edits (tracks are untouched). */
    fun cancelOverlay() {
        _showImageGrid.value = false
        _imagesToDelete.value = emptySet()
        _multiSelectMode.value = false
    }

    /** Sort the staged overlay draft by creation date (does not touch the tracks until applied). */
    fun toggleOverlaySort() {
        if (_overlaySortEnabled.value) {
            // Already in chronological order — flip the direction.
            _overlaySortAscending.value = !_overlaySortAscending.value
        } else {
            // Out of order (e.g. after a manual drag): restore the last direction first.
            _overlaySortEnabled.value = true
        }
        _gridImages.value = sortByDate(_gridImages.value, _overlaySortAscending.value)
    }

    /** Append newly-picked images to the draft. */
    fun addImagesToOverlay(images: List<ImageItem>) {
        if (images.isEmpty()) return
        val combined = _gridImages.value + images
        // Keep chronological order when sort is on; otherwise just append.
        _gridImages.value =
            if (_overlaySortEnabled.value) sortByDate(combined, _overlaySortAscending.value) else combined
        refreshOverlaySortState()
    }

    /** Remove the overlay-selected images from the draft only. */
    fun deleteSelectedFromOverlay() {
        val toDelete = _imagesToDelete.value
        if (toDelete.isEmpty()) return
        _gridImages.value = _gridImages.value.filterNot { it.id in toDelete }
        _imagesToDelete.value = emptySet()
        // Removing images can leave (or break) chronological order — keep the button in sync.
        refreshOverlaySortState()
    }

    /** Clear all images from the draft only. */
    fun clearOverlayImages() {
        _gridImages.value = emptyList()
        _imagesToDelete.value = emptySet()
    }

    /** Apply the staged draft to the tracks in-place, then close the overlay. */
    fun applyOverlayChanges(editorContext: EditorContext) {
        val engine = editorContext.engine
        viewModelScope.launch {
            try {
                val page = engine.scene.getCurrentPage() ?: return@launch
                val tracks = findTracks(engine, page) ?: return@launch
                val draft = _gridImages.value
                applyDraftToTracks(
                    engine = engine,
                    page = page,
                    tracks = tracks,
                    staged = draft,
                    animationPairs = _animationPairs.value,
                    hasTitle = _videoTitle.value.isNotEmpty(),
                )
                // New blocks default to full-bleed and no effects; re-apply the active style so they
                // match (including Default, which resets scale to full-bleed and clears the backdrop).
                applyStyle(editorContext, activeStyleId)
                _selectedImages.value = draft
                _imagesToDelete.value = emptySet()
                _multiSelectMode.value = false
                _showImageGrid.value = false
            } catch (e: Exception) {
                Log.e(TAG, "Error applying overlay changes", e)
            }
        }
    }

    private fun applySortToImages() {
        if (_isSortEnabled.value) {
            val sortedImages = sortByDate(_selectedImages.value, _isSortAscending.value)
            _selectedImages.value = sortedImages
            _gridImages.value = sortedImages
        }
    }

    /**
     * Reconcile the picker's sort state with the current order of [_selectedImages]. When the list
     * happens to be in chronological order the sort button reflects that direction; when it is out
     * of order sort turns off but the last direction is remembered (so the next tap restores it).
     */
    private fun refreshSortState() {
        val order = detectSortOrder(_selectedImages.value)
        if (order == null) {
            _isSortEnabled.value = false // keep _isSortAscending so the next tap restores this direction
        } else {
            _isSortEnabled.value = true
            _isSortAscending.value = order
        }
    }

    /** Same reconciliation as [refreshSortState] but for the in-editor overlay draft. */
    private fun refreshOverlaySortState() {
        val order = detectSortOrder(_gridImages.value)
        if (order == null) {
            _overlaySortEnabled.value = false
        } else {
            _overlaySortEnabled.value = true
            _overlaySortAscending.value = order
        }
    }

    fun setEditorContext(context: EditorContext) {
        editorContext = context
    }

    /** Drop the editor context when the editor closes, so post-close calls no-op instead of acting
     *  on a stale/disposed editor. */
    fun clearEditorContext() {
        previewJob?.cancel()
        editorContext = null
    }

    /** Loop on, volume synced — called once the scene is built. */
    fun initializeEditorState() {
        val engine = editorContext?.engine ?: return
        viewModelScope.launch {
            runCatching {
                setPageLooping(engine, true)
                _isLooping.value = true
                getPageVolume(engine)?.let { _volume.value = it }
            }.onFailure { Log.w(TAG, "Could not initialize editor state", it) }
        }
    }

    // ──────────────── Playback: loop & volume ────────────────

    fun toggleLoop() {
        val engine = editorContext?.engine ?: return
        viewModelScope.launch {
            runCatching { togglePageLoop(engine)?.let { _isLooping.value = it } }
                .onFailure { Log.w(TAG, "Could not toggle loop", it) }
        }
    }

    fun setVolume(volume: Float) {
        val engine = editorContext?.engine ?: return
        viewModelScope.launch {
            runCatching {
                setPageVolume(engine, volume)
                _volume.value = volume
            }.onFailure { Log.w(TAG, "Could not set volume", it) }
        }
    }

    // ──────────────── Styles ────────────────

    /** Apply a style to the slideshow (see [applyStyleToSlideshow]); remembered for re-applies. */
    fun applyStyle(
        editorContext: EditorContext,
        styleId: String,
    ) {
        activeFilter = styleId
        val engine = editorContext.engine
        viewModelScope.launch {
            val page = engine.scene.getCurrentPage() ?: return@launch
            runCatching { applyStyleToSlideshow(engine, page, VideoStyles.byId(styleId)) }
                .onFailure { Log.w(TAG, "Could not apply style '$styleId'", it) }
        }
    }
}
