package ly.img.editor.core.ui.library

import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.request.SuccessResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ly.img.editor.core.EditorScope
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.LibraryContent
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.library.data.TypefaceProvider
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.editor.core.ui.Environment
import ly.img.editor.core.ui.EventsHandler
import ly.img.editor.core.ui.engine.ROLE_ADOPTER
import ly.img.editor.core.ui.engine.Scope
import ly.img.editor.core.ui.engine.awaitEngineAndSceneLoad
import ly.img.editor.core.ui.engine.dpToCanvasUnit
import ly.img.editor.core.ui.engine.getBackgroundTrack
import ly.img.editor.core.ui.engine.getCamera
import ly.img.editor.core.ui.engine.getCurrentPage
import ly.img.editor.core.ui.engine.isSceneModeVideo
import ly.img.editor.core.ui.engine.overrideAndRestore
import ly.img.editor.core.ui.library.components.section.LibrarySectionItem
import ly.img.editor.core.ui.library.data.font.FontDataMapper
import ly.img.editor.core.ui.library.engine.isVideoBlock
import ly.img.editor.core.ui.library.state.AssetLibraryUiState
import ly.img.editor.core.ui.library.state.AssetsData
import ly.img.editor.core.ui.library.state.AssetsLoadState
import ly.img.editor.core.ui.library.state.CategoryLoadState
import ly.img.editor.core.ui.library.state.LibraryCategoryStackData
import ly.img.editor.core.ui.library.state.WrappedAsset
import ly.img.editor.core.ui.library.util.LibraryEvent
import ly.img.editor.core.ui.library.util.LibraryEvent.OnAddAsset
import ly.img.editor.core.ui.library.util.LibraryEvent.OnAddCameraRecordings
import ly.img.editor.core.ui.library.util.LibraryEvent.OnAddUri
import ly.img.editor.core.ui.library.util.LibraryEvent.OnAssetLongClick
import ly.img.editor.core.ui.library.util.LibraryEvent.OnDispose
import ly.img.editor.core.ui.library.util.LibraryEvent.OnDrillDown
import ly.img.editor.core.ui.library.util.LibraryEvent.OnEnterSearchMode
import ly.img.editor.core.ui.library.util.LibraryEvent.OnFetch
import ly.img.editor.core.ui.library.util.LibraryEvent.OnPopStack
import ly.img.editor.core.ui.library.util.LibraryEvent.OnReplaceAsset
import ly.img.editor.core.ui.library.util.LibraryEvent.OnReplaceUri
import ly.img.editor.core.ui.library.util.LibraryEvent.OnSearchTextChange
import ly.img.editor.core.ui.library.util.LibraryUiEvent
import ly.img.editor.core.ui.register
import ly.img.engine.Asset
import ly.img.engine.AssetDefinition
import ly.img.engine.ContentFillMode
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.FillType
import ly.img.engine.FindAssetsQuery
import ly.img.engine.FindAssetsResult
import ly.img.engine.PositionMode
import ly.img.engine.SceneMode
import ly.img.engine.ShapeType
import java.util.Stack
import java.util.UUID
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.DurationUnit

class LibraryViewModel(
    private val editorScope: EditorScope,
    private val onUpload: suspend EditorScope.(AssetDefinition, UploadAssetSourceType) -> AssetDefinition,
) : ViewModel() {
    private val imageLoader = Environment.getImageLoader()
    private val editor = editorScope.run { editorContext }
    private val engine = editor.engine
    private val typefaceProvider = TypefaceProvider()
    private val fontDataMapper = FontDataMapper()

    private val _uiEvent = Channel<LibraryUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    val sceneMode: SceneMode
        get() = engine.scene.getMode()

    val assetLibrary
        get() = editor.assetLibrary

    val navBarItems
        get() = assetLibrary.tabs(sceneMode)

    private val libraryStackDataMapping by lazy {
        hashMapOf<LibraryCategory, LibraryCategoryStackData>()
    }

    private val eventHandler =
        EventsHandler {
            register<OnDispose> {
                onDispose()
            }
            register<OnDrillDown> {
                onDrillDown(it.libraryCategory, it.expandContent)
            }
            register<OnEnterSearchMode> {
                onEnterSearchMode(it.enter, it.libraryCategory)
            }
            register<OnFetch> {
                onFetch(it.libraryCategory)
            }
            register<OnPopStack> {
                onPopStack(it.libraryCategory)
            }
            register<OnSearchTextChange> {
                onSearchTextChange(it.value, it.libraryCategory, it.debounce)
            }
            register<OnReplaceAsset> {
                onReplaceAsset(it.wrappedAsset.assetSourceType, it.wrappedAsset.asset, it.designBlock, it.wrappedAsset.assetType)
            }
            register<OnReplaceUri> {
                onReplaceUri(it.assetSource, it.uri, it.designBlock)
            }
            register<OnAddAsset> {
                onAddAsset(it.wrappedAsset.assetSourceType, it.wrappedAsset.asset, it.addToBackgroundTrack)
            }
            register<OnAddUri> {
                onAddUri(it.assetSource, it.uri, it.addToBackgroundTrack)
            }
            register<OnAddCameraRecordings> {
                onAddCameraRecordings(it.assetSource, it.recordings)
            }
            register<OnAssetLongClick> {
                onAssetLongClick(it.wrappedAsset)
            }
        }

    fun onEvent(event: LibraryEvent) = eventHandler.handleEvent(event)

    fun getAssetLibraryUiState(libraryCategory: LibraryCategory): StateFlow<AssetLibraryUiState> {
        return getLibraryCategoryData(libraryCategory).uiStateFlow
    }

    private fun getLibraryCategoryData(libraryCategory: LibraryCategory): LibraryCategoryStackData {
        return libraryStackDataMapping.getOrPut(libraryCategory) {
            LibraryCategoryStackData(
                uiStateFlow =
                    MutableStateFlow(
                        AssetLibraryUiState(
                            libraryCategory = libraryCategory,
                            titleRes = libraryCategory.tabTitleRes,
                        ),
                    ),
                dataStack = Stack<LibraryContent>().apply { push(libraryCategory.content) },
            )
        }
    }

    private fun onAssetLongClick(wrappedAsset: WrappedAsset) {
        val assetSourceType = wrappedAsset.assetSourceType
        val asset = wrappedAsset.asset
        val sourceId = assetSourceType.sourceId
        val assetLicense = asset.license
        val sourceLicense = engine.asset.getLicense(sourceId)
        val showAssetDetails = assetLicense != null || sourceLicense != null
        if (!showAssetDetails) return
        val assetCredits = asset.credits
        val sourceCredits = engine.asset.getCredits(sourceId)
        viewModelScope.launch {
            _uiEvent.send(
                LibraryUiEvent.ShowAssetCredits(
                    assetLabel = asset.label ?: asset.getMeta("filename") ?: asset.id,
                    assetCredits = assetCredits,
                    assetLicense = assetLicense,
                    assetSourceCredits = sourceCredits,
                    assetSourceLicense = sourceLicense,
                ),
            )
        }
    }

    private fun onAddAsset(
        assetSourceType: AssetSourceType,
        asset: Asset,
        addToBackgroundTrack: Boolean,
    ) {
        viewModelScope.launch {
            engine.awaitEngineAndSceneLoad()
            val designBlock = engine.asset.applyAssetSourceAsset(assetSourceType.sourceId, asset) ?: return@launch

            if (engine.isSceneModeVideo) {
                setupAssetForVideo(asset, designBlock, addToBackgroundTrack)
            } else {
                placeDesignBlock(designBlock)
            }
        }
    }

    private fun onAddUri(
        assetSourceType: UploadAssetSourceType,
        uri: Uri,
        addToBackgroundTrack: Boolean,
    ) {
        viewModelScope.launch {
            engine.awaitEngineAndSceneLoad()
            val asset = uploadToAssetSource(assetSourceType, uri)
            onAddAsset(assetSourceType, asset, addToBackgroundTrack)
        }
    }

    private fun onAddCameraRecordings(
        assetSource: UploadAssetSourceType,
        recordings: List<Pair<Uri, Duration>>,
    ) {
        viewModelScope.launch {
            engine.awaitEngineAndSceneLoad()
            val page = engine.getCurrentPage()
            val backgroundTrack = checkNotNull(engine.block.getBackgroundTrack())

            // set playhead position to end of background track
            engine.block.setPlaybackTime(page, engine.block.getDuration(backgroundTrack))

            recordings.forEach { (uri, duration) ->
                uploadToAssetSource(assetSource, uri, duration)
                // We cannot use engine.asset.applyAssetSourceAsset() here as it adds an undo step at the end.
                // We only want to add one undo step at the end after adding all the recordings.
                addCameraRecording(uri, duration)
            }

            engine.editor.addUndoStep()
        }
    }

    private suspend fun addCameraRecording(
        uri: Uri,
        duration: Duration,
    ) {
        val backgroundTrack = checkNotNull(engine.block.getBackgroundTrack())
        val id = engine.block.create(DesignBlockType.Graphic)
        val rectShape = engine.block.createShape(ShapeType.Rect)
        engine.block.setShape(id, rectShape)
        engine.block.appendChild(parent = backgroundTrack, child = id)
        engine.block.fillParent(id)

        val durationInDouble = duration.toDouble(DurationUnit.SECONDS)
        engine.block.setDuration(id, durationInDouble)
        val fill = engine.block.createFill(FillType.Video)
        engine.block.setString(fill, "fill/video/fileURI", uri.toString())
        engine.block.setFill(id, fill)

        // Refreshing the duration helps to resolve rounding errors (in case the duration set is slightly off from the actual duration)
        refreshDuration(
            designBlock = id,
            fill = fill,
            resolvedClipDuration = durationInDouble,
            inBackgroundTrack = true,
        )
    }

    private fun onReplaceAsset(
        assetSourceType: AssetSourceType,
        asset: Asset,
        designBlock: DesignBlock,
        assetType: AssetType = AssetType.Image,
    ) {
        viewModelScope.launch {
            engine.awaitEngineAndSceneLoad()
            engine.asset.applyAssetSourceAsset(assetSourceType.sourceId, asset, designBlock)
            if (assetType == AssetType.Sticker) {
                engine.overrideAndRestore(designBlock, Scope.LayerCrop) {
                    engine.block.setContentFillMode(designBlock, ContentFillMode.CONTAIN)
                }
            }

            setBlockName(asset, designBlock)

            if (engine.editor.getRole() == ROLE_ADOPTER) {
                engine.block.setPlaceholderEnabled(designBlock, false)
            }

            if (engine.block.isVideoBlock(designBlock) ||
                DesignBlockType.get(engine.block.getType(designBlock)) == DesignBlockType.Audio
            ) {
                val oldDuration = engine.block.getDuration(designBlock)
                asset.getDuration()?.let { duration ->
                    engine.block.setDuration(designBlock, min(duration, oldDuration))
                }
                val fillBlock =
                    if (engine.block.supportsFill(designBlock)) {
                        engine.block.getFill(designBlock)
                    } else {
                        designBlock
                    }
                refreshDuration(designBlock, fillBlock, oldDuration)
            }
        }
        engine.editor.addUndoStep()
    }

    private fun onReplaceUri(
        assetSource: UploadAssetSourceType,
        uri: Uri,
        designBlock: DesignBlock,
    ) {
        viewModelScope.launch {
            engine.awaitEngineAndSceneLoad()
            val asset = uploadToAssetSource(assetSource, uri)
            onReplaceAsset(assetSource, asset, designBlock)
        }
    }

    private suspend fun setupAssetForVideo(
        asset: Asset,
        designBlock: DesignBlock,
        inBackgroundTrack: Boolean,
    ) {
        setBlockName(asset, designBlock)

        val defaultClipDuration = 5.0
        var resolvedClipDuration = asset.getDuration() ?: defaultClipDuration
        val page = engine.getCurrentPage()

        if (inBackgroundTrack) {
            val backgroundTrack = checkNotNull(engine.block.getBackgroundTrack())
            engine.block.appendChild(parent = backgroundTrack, child = designBlock)
            engine.block.fillParent(designBlock)
            // page duration needs to be updated to ensure playback time does not get clamped to the previous known page duration
            engine.block.setDuration(page, engine.block.getDuration(backgroundTrack))
            engine.block.setPlaybackTime(page, engine.block.getTimeOffset(designBlock))
        } else {
            engine.block.appendChild(parent = page, child = designBlock)

            // Adjust the time offset
            val minClipDuration = 1.0
            val playbackTime = engine.block.getPlaybackTime(page)
            val totalDuration = engine.block.getDuration(page)

            // Prevent inserting at the very end of the timeline, always insert audio at the beginning
            val clampedOffset =
                if (DesignBlockType.get(engine.block.getType(designBlock)) == DesignBlockType.Audio) {
                    0.0
                } else {
                    max(0.0, min(playbackTime, totalDuration - minClipDuration))
                }

            val maxClipDuration = totalDuration - clampedOffset
            val assetDuration = asset.getDuration() ?: max(defaultClipDuration, maxClipDuration)

            // If there is nothing in the scene yet, we allow the full asset duration,
            // otherwise shorten to fit remaining time:
            resolvedClipDuration = if (totalDuration == 0.0) assetDuration else min(assetDuration, maxClipDuration)
            engine.block.setTimeOffset(designBlock, offset = clampedOffset)
            placeDesignBlock(designBlock)
        }

        engine.block.setDuration(designBlock, duration = resolvedClipDuration)

        val fillBlock =
            if (DesignBlockType.get(engine.block.getType(designBlock)) == DesignBlockType.Audio) {
                // Prevent audio blocks from being considered in the z-index reordering
                engine.block.setAlwaysOnTop(designBlock, true)
                designBlock
            } else if (engine.block.isVideoBlock(designBlock)) {
                engine.block.getFill(designBlock)
            } else {
                null
            }

        fillBlock?.let { fill ->
            engine.block.setLooping(fill, false)
            refreshDuration(designBlock, fill, resolvedClipDuration, inBackgroundTrack)
        }
    }

    private suspend fun refreshDuration(
        designBlock: DesignBlock,
        fill: DesignBlock,
        resolvedClipDuration: Double,
        inBackgroundTrack: Boolean = false,
    ) {
        runCatching {
            engine.block.forceLoadAVResource(fill)
            val totalDuration = engine.block.getAVResourceTotalDuration(fill)
            val resolvedDuration = if (inBackgroundTrack) totalDuration else min(resolvedClipDuration, totalDuration)
            engine.block.setDuration(designBlock, resolvedDuration)
        }
    }

    private fun setBlockName(
        asset: Asset,
        designBlock: DesignBlock,
    ) {
        asset.label?.let { label ->
            engine.block.setMetadata(designBlock, "name", label)
        }
    }

    private fun placeDesignBlock(designBlock: DesignBlock) {
        val page = engine.getCurrentPage()
        val camera = engine.getCamera()
        val width = engine.block.getFrameWidth(designBlock)
        val height = engine.block.getFrameHeight(designBlock)

        val pixelRatio = engine.block.getFloat(camera, "camera/pixelRatio")
        val cameraWidth = engine.block.getFloat(camera, "camera/resolution/width") / pixelRatio
        val cameraHeight = engine.block.getFloat(camera, "camera/resolution/height") / pixelRatio

        val screenRect = RectF(0f, 0f, cameraWidth, cameraHeight)
        val pageRect = engine.block.getScreenSpaceBoundingBoxRect(listOf(page))
        val pageWidth = pageRect.width()

        // find visible page rect
        val visiblePageRect = RectF()
        visiblePageRect.setIntersect(pageRect, screenRect)

        // set the position of the new block in the center of the visible page
        engine.block.setPositionXMode(designBlock, PositionMode.ABSOLUTE)
        engine.block.setPositionYMode(designBlock, PositionMode.ABSOLUTE)

        val newX = engine.dpToCanvasUnit((visiblePageRect.left - pageRect.left) + visiblePageRect.width() / 2) - width / 2
        val newY = engine.dpToCanvasUnit((visiblePageRect.top - pageRect.top) + visiblePageRect.height() / 2) - height / 2

        engine.block.setPositionX(designBlock, newX)
        engine.block.setPositionY(designBlock, newY)

        // scale down the new block
        if (pageWidth > cameraWidth) {
            engine.block.scale(designBlock, cameraWidth / pageWidth, 0.5f, 0.5f)
        }
    }

    private fun onDispose() {
        libraryStackDataMapping.keys.forEach {
            // clear search
            onSearchTextChange("", it, debounce = false, force = true)
            // get out of search mode
            onEnterSearchMode(enter = false, it)
            // go to root
            val categoryData = getLibraryCategoryData(it)
            val dataStack = categoryData.dataStack
            dataStack.subList(1, dataStack.size).clear()
        }
    }

    private fun onEnterSearchMode(
        enter: Boolean,
        libraryCategory: LibraryCategory,
    ) {
        val uiStateFlow = getLibraryCategoryData(libraryCategory).uiStateFlow
        if (enter == uiStateFlow.value.isInSearchMode) return
        uiStateFlow.update { it.copy(isInSearchMode = enter) }
    }

    private fun onSearchTextChange(
        value: String,
        libraryCategory: LibraryCategory,
        debounce: Boolean,
        force: Boolean = false,
    ) {
        val categoryData = getLibraryCategoryData(libraryCategory)
        val oldValue = categoryData.uiStateFlow.value.searchText
        if (!force && oldValue == value) return

        categoryData.uiStateFlow.update { it.copy(searchText = value) }

        // cancel previous search job
        categoryData.searchJob?.cancel()

        viewModelScope.launch {
            // debounce is only needed when user is typing
            if (debounce) {
                delay(500)
            }

            // cancel previous fetch job
            categoryData.fetchJob?.cancel()
            categoryData.dirty = true

            categoryData.uiStateFlow.update {
                it.copy(
                    isRoot = if (force) true else it.isRoot,
                    titleRes = if (force) libraryCategory.tabTitleRes else it.titleRes,
                    assetsData = AssetsData(),
                    sectionItems = if (force) listOf() else it.sectionItems,
                )
            }

            // we only need to fetch when user is searching
            if (!force) {
                onFetch(libraryCategory)
            }
        }.also {
            categoryData.searchJob
        }
    }

    private fun onFetch(libraryCategory: LibraryCategory) {
        val categoryData = getLibraryCategoryData(libraryCategory)
        val content = categoryData.dataStack.last()

        categoryData.fetchJob =
            when (content) {
                is LibraryContent.Sections -> loadContent(categoryData, content)
                is LibraryContent.Grid -> loadContent(categoryData, content)
            }
    }

    private fun loadContent(
        categoryData: LibraryCategoryStackData,
        content: LibraryContent.Grid,
    ) = viewModelScope.launch {
        val uiStateFlow = categoryData.uiStateFlow
        val assetsData = uiStateFlow.value.assetsData
        val canLoadMore = (assetsData.page == 0 && assetsData.assets.isEmpty()) || assetsData.canPaginate
        val isLoading =
            assetsData.assetsLoadState == AssetsLoadState.Loading || assetsData.assetsLoadState == AssetsLoadState.Paginating
        if (canLoadMore.not() || isLoading) return@launch
        categoryData.dirty = false
        uiStateFlow.update {
            it.copy(
                titleRes = content.titleRes,
                isRoot = categoryData.dataStack.size == 1,
                assetsData =
                    it.assetsData.copy(
                        assetType = content.assetType,
                        assetSourceType = content.sourceType,
                        assetsLoadState = if (assetsData.page == 0) AssetsLoadState.Loading else AssetsLoadState.Paginating,
                    ),
                loadState = CategoryLoadState.LoadingAssets,
            )
        }
        runCatching {
            val findAssetsResult =
                findAssets(
                    sourceId = content.sourceType.sourceId,
                    query = uiStateFlow.value.searchText,
                    groups = content.groups,
                    page = assetsData.page,
                    perPage = content.perPage,
                )
            val canPaginate = findAssetsResult.nextPage > 0
            val resultAssets =
                findAssetsResult.assets.map {
                    createWrappedAsset(
                        asset = it,
                        assetSourceType = content.sourceType,
                        assetType = content.assetType,
                    )
                }
            val assets = if (assetsData.page == 0) resultAssets else assetsData.assets + resultAssets
            uiStateFlow.update {
                it.copy(
                    assetsData =
                        it.assetsData.copy(
                            canPaginate = canPaginate,
                            page = if (canPaginate) assetsData.page + 1 else assetsData.page,
                            assets = assets,
                            assetsLoadState = if (assets.isEmpty()) AssetsLoadState.EmptyResult else AssetsLoadState.Idle,
                        ),
                )
            }
        }.onFailure {
            if (it is CancellationException) {
                return@launch
            }
            uiStateFlow.update {
                it.copy(
                    assetsData =
                        assetsData.copy(
                            assetsLoadState = if (assetsData.page == 0) AssetsLoadState.Error else AssetsLoadState.PaginationError,
                        ),
                )
            }
        }
    }

    private fun loadContent(
        categoryData: LibraryCategoryStackData,
        content: LibraryContent.Sections,
    ) = viewModelScope.launch {
        val uiStateFlow = categoryData.uiStateFlow
        if (uiStateFlow.value.loadState != CategoryLoadState.Idle && !categoryData.dirty) return@launch
        val stackIndex = categoryData.dataStack.lastIndex
        val loadingSectionItemsList = mutableListOf<LibrarySectionItem>()
        categoryData.dirty = false
        uiStateFlow.update {
            it.copy(
                titleRes = content.titleRes,
                isRoot = categoryData.dataStack.size == 1,
                loadState = CategoryLoadState.Loading,
                sectionItems = listOf(LibrarySectionItem.Loading(stackIndex = stackIndex)),
            )
        }
        content.sections.forEachIndexed { sectionIndex, section ->
            val sectionTitleRes = section.titleRes
            if (sectionTitleRes != null) {
                LibrarySectionItem.Header(
                    stackIndex = stackIndex,
                    sectionIndex = sectionIndex,
                    titleRes = sectionTitleRes,
                    uploadAssetSourceType =
                        if (section.showUpload) {
                            section.sourceTypes.singleOrNull() as? UploadAssetSourceType
                        } else {
                            null
                        },
                    expandContent = section.expandContent,
                ).let(loadingSectionItemsList::add)
            }
            if (section.addGroupedSubSections) {
                val groups = engine.asset.getGroups(section.sourceTypes[0].sourceId)
                groups?.mapIndexed { groupIndex, group ->
                    LibrarySectionItem.ContentLoading(
                        stackIndex = stackIndex,
                        sectionIndex = sectionIndex,
                        subSectionIndex = groupIndex,
                        section =
                            section.copy(
                                groups = listOf(group),
                                addGroupedSubSections = false,
                            ),
                    ).let(loadingSectionItemsList::add)
                }
            } else {
                LibrarySectionItem.ContentLoading(
                    stackIndex = stackIndex,
                    sectionIndex = sectionIndex,
                    subSectionIndex = 0,
                    section = section,
                ).let(loadingSectionItemsList::add)
            }
        }

        uiStateFlow.update {
            it.copy(loadState = CategoryLoadState.LoadingSections, sectionItems = loadingSectionItemsList)
        }

        coroutineScope {
            uiStateFlow.value
                .sectionItems
                .filterIsInstance<LibrarySectionItem.ContentLoading>()
                .map { content ->
                    val section = content.section
                    async {
                        var total = 0
                        val updatedContent =
                            section.sourceTypes.map { source ->
                                async {
                                    runCatching {
                                        source to
                                            findAssets(
                                                sourceId = source.sourceId,
                                                query = uiStateFlow.value.searchText,
                                                groups = section.groups,
                                                perPage = section.count,
                                            )
                                    }
                                }
                            }.awaitAll()
                                .mapNotNull { it.getOrNull() }
                                .takeIf { it.isNotEmpty() }
                                ?.flatMap { (source, findResult) ->
                                    total = runCatching {
                                        Math.addExact(total, findResult.total)
                                    }.getOrNull() ?: Int.MAX_VALUE
                                    findResult.assets.map { asset -> source to asset }
                                }
                                ?.take(section.count)
                                ?.map { (source, asset) ->
                                    createWrappedAsset(asset, source, section.assetType)
                                }?.let { wrappedAssets ->
                                    LibrarySectionItem.Content(
                                        stackIndex = stackIndex,
                                        sectionIndex = content.sectionIndex,
                                        wrappedAssets = wrappedAssets,
                                        assetType = section.assetType,
                                        expandContent = if (total > wrappedAssets.size) section.expandContent else null,
                                    )
                                } ?: LibrarySectionItem.Error(
                                stackIndex = stackIndex,
                                sectionIndex = content.sectionIndex,
                                assetType = section.assetType,
                            )

                        uiStateFlow.update {
                            it.copy(
                                sectionItems =
                                    it.sectionItems.map { item ->
                                        when {
                                            item is LibrarySectionItem.Header && item.sectionIndex == content.sectionIndex -> {
                                                item.copy(count = total)
                                            }

                                            item is LibrarySectionItem.ContentLoading &&
                                                item.sectionIndex == content.sectionIndex &&
                                                item.subSectionIndex == content.subSectionIndex -> {
                                                updatedContent
                                            }

                                            else -> item
                                        }
                                    },
                            )
                        }
                    }
                }
        }.awaitAll()

        uiStateFlow.update {
            it.copy(loadState = CategoryLoadState.Success)
        }
    }

    private suspend fun createWrappedAsset(
        asset: Asset,
        assetSourceType: AssetSourceType,
        assetType: AssetType,
    ): WrappedAsset {
        return if (assetType == AssetType.Text) {
            WrappedAsset.TextAsset(
                asset = asset,
                assetSourceType = assetSourceType,
                assetType = assetType,
                fontData =
                    asset.getMeta("fontFamily")
                        ?.let { typefaceProvider.provideTypeface(engine, it) }
                        ?.let { fontDataMapper.getFontData(it, asset.getMeta("fontWeight")?.toInt()) },
            )
        } else {
            WrappedAsset.GenericAsset(
                asset = asset,
                assetType = assetType,
                assetSourceType = assetSourceType,
            )
        }
    }

    private suspend fun findAssets(
        sourceId: String,
        perPage: Int,
        query: String,
        page: Int = 0,
        groups: List<String>? = null,
    ): FindAssetsResult {
        return engine.asset.findAssets(
            sourceId = sourceId,
            query =
                FindAssetsQuery(
                    perPage = perPage,
                    query = query,
                    page = page,
                    groups = groups,
                    locale = "en",
                ),
        )
    }

    private fun onDrillDown(
        currentLibrary: LibraryCategory,
        expandContent: LibraryContent?,
    ) {
        modifyDataStack(currentLibrary) { dataStack ->
            dataStack.push(expandContent)
        }
    }

    private fun onPopStack(currentLibrary: LibraryCategory) {
        modifyDataStack(currentLibrary) { dataStack ->
            dataStack.pop()
        }
    }

    private fun modifyDataStack(
        libraryCategory: LibraryCategory,
        action: (dataStack: Stack<LibraryContent>) -> Unit,
    ) {
        val categoryData = getLibraryCategoryData(libraryCategory)
        categoryData.searchJob?.cancel()
        categoryData.fetchJob?.cancel()

        action(categoryData.dataStack)
        categoryData.dirty = true
        categoryData.uiStateFlow.update {
            it.copy(assetsData = AssetsData())
        }
        onFetch(libraryCategory)
    }

    private suspend fun uploadToAssetSource(
        assetSourceType: UploadAssetSourceType,
        uri: Uri,
        duration: Duration? = null,
    ): Asset {
        val uuid = UUID.randomUUID().toString()
        val uriString = uri.toString()
        val meta =
            mutableMapOf(
                "uri" to uriString,
            )
        var label: String? = null

        when (assetSourceType) {
            AssetSourceType.AudioUploads -> {
                meta["kind"] = "audio"
                meta["blockType"] = "//ly.img.ubq/audio"
                Environment.getMediaMetadataExtractor().getAudioMetadata(uri)?.let {
                    label = it.title
                    meta["duration"] = it.duration ?: "0"
                    if (it.artworkUri != null) {
                        meta["thumbUri"] = it.artworkUri
                    }
                }
            }

            AssetSourceType.ImageUploads -> {
                meta["thumbUri"] = uriString
                meta["kind"] = "image"
                meta["fillType"] = FillType.Image.key
                getImageDimensions(uri)?.let { (width, height) ->
                    meta["width"] = width.toString()
                    meta["height"] = height.toString()
                }
            }

            AssetSourceType.VideoUploads -> {
                meta["kind"] = "video"
                meta["fillType"] = FillType.Video.key
                Environment.getMediaMetadataExtractor().getVideoMetadata(uri)?.let {
                    meta["thumbUri"] = it.thumbUri
                    meta["width"] = it.width
                    meta["height"] = it.height
                    (it.duration ?: duration?.inWholeSeconds?.toString())?.let { metaDuration ->
                        meta["duration"] = metaDuration
                    }
                }
            }
        }

        val assetDefinition =
            AssetDefinition(
                id = uuid,
                meta = meta,
                label = if (label != null) mapOf("en" to label!!) else null,
            ).let {
                onUpload(editorScope, it, assetSourceType)
            }

        engine.asset.addAsset(
            sourceId = assetSourceType.sourceId,
            asset = assetDefinition,
        )
        val result = findAssets(assetSourceType.sourceId, perPage = 10, query = label ?: uuid)
        return result.assets.first { it.id == uuid }
    }

    private suspend fun getImageDimensions(uri: Uri): Pair<Int, Int>? {
        val result = imageLoader.execute(Environment.newImageRequest(uri))
        val bitmap = ((result as? SuccessResult)?.drawable as? BitmapDrawable)?.bitmap ?: return null
        val width = bitmap.width
        val height = bitmap.height
        return width to height
    }
}
