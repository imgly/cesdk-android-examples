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
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.LibraryContent
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.editor.core.ui.Environment
import ly.img.editor.core.ui.EventsHandler
import ly.img.editor.core.ui.engine.BlockKind
import ly.img.editor.core.ui.engine.ROLE
import ly.img.editor.core.ui.engine.ROLE_ADOPTER
import ly.img.editor.core.ui.engine.dpToCanvasUnit
import ly.img.editor.core.ui.engine.getCamera
import ly.img.editor.core.ui.engine.getKindEnum
import ly.img.editor.core.ui.engine.getPage
import ly.img.editor.core.ui.library.components.section.LibrarySectionItem
import ly.img.editor.core.ui.library.data.font.FontFamilyData
import ly.img.editor.core.ui.library.engine.replaceSticker
import ly.img.editor.core.ui.library.state.AssetLibraryUiState
import ly.img.editor.core.ui.library.state.AssetsData
import ly.img.editor.core.ui.library.state.AssetsLoadState
import ly.img.editor.core.ui.library.state.CategoryLoadState
import ly.img.editor.core.ui.library.state.LibraryCategoryStackData
import ly.img.editor.core.ui.library.state.WrappedAsset
import ly.img.editor.core.ui.library.util.LibraryEvent
import ly.img.editor.core.ui.library.util.LibraryEvent.OnAddAsset
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
import ly.img.engine.DesignBlock
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.FindAssetsQuery
import ly.img.engine.FindAssetsResult
import ly.img.engine.PositionMode
import ly.img.engine.SceneMode
import java.util.Stack
import java.util.UUID

internal class LibraryViewModel : ViewModel() {
    private val assetsRepo = Environment.getAssetsRepo()
    private val imageLoader = Environment.getImageLoader()
    private val engine = Environment.getEngine()

    private val fontFamilies: StateFlow<Map<String, FontFamilyData>?> = assetsRepo.fontFamilies

    private val _uiEvent = Channel<LibraryUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val sceneMode: SceneMode
        get() = engine.scene.getMode()

    private val onUpload: suspend AssetDefinition.(Engine, UploadAssetSourceType) -> AssetDefinition =
        requireNotNull(Environment.onUpload)
    private val assetLibrary = requireNotNull(Environment.assetLibrary)
    val navBarItems by lazy {
        assetLibrary.tabs(sceneMode)
    }
    val replaceImageCategory by lazy {
        assetLibrary.images(sceneMode)
    }
    val replaceStickerCategory by lazy {
        assetLibrary.stickers(sceneMode)
    }
    private val libraryCategories by lazy {
        navBarItems +
            listOf(
                replaceImageCategory,
                replaceStickerCategory,
                AppearanceLibraryCategory.Filters,
                AppearanceLibraryCategory.FxEffects,
                AppearanceLibraryCategory.Blur,
            ).toSet()
    }
    private val libraryStackDataMapping by lazy {
        libraryCategories.associateWith {
            LibraryCategoryStackData(
                uiStateFlow =
                    MutableStateFlow(
                        AssetLibraryUiState(
                            libraryCategory = it,
                            titleRes = it.tabTitleRes,
                        ),
                    ),
                dataStack = Stack<LibraryContent>().apply { push(it.content) },
            )
        }
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
                onReplaceAsset(it.wrappedAsset.assetSourceType, it.wrappedAsset.asset, it.wrappedAsset.assetType, it.designBlock)
            }
            register<OnReplaceUri> {
                onReplaceUri(it.assetSource, it.uri, it.designBlock)
            }
            register<OnAddAsset> {
                onAddAsset(it.wrappedAsset.assetSourceType, it.wrappedAsset.asset)
            }
            register<OnAddUri> {
                onAddUri(it.assetSource, it.uri)
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
        return checkNotNull(libraryStackDataMapping[libraryCategory])
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
    ) {
        viewModelScope.launch {
            val designBlock = engine.asset.applyAssetSourceAsset(assetSourceType.sourceId, asset) ?: return@launch

            val camera = engine.getCamera()
            val width = engine.block.getFrameWidth(designBlock)
            val height = engine.block.getFrameHeight(designBlock)

            val pixelRatio = engine.block.getFloat(camera, "camera/pixelRatio")
            val cameraWidth = engine.block.getFloat(camera, "camera/resolution/width") / pixelRatio
            val cameraHeight = engine.block.getFloat(camera, "camera/resolution/height") / pixelRatio

            val screenRect = RectF(0f, 0f, cameraWidth, cameraHeight)
            val pageRect = engine.block.getScreenSpaceBoundingBoxRect(listOf(engine.getPage(0)))
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
    }

    private fun onAddUri(
        assetSourceType: UploadAssetSourceType,
        uri: Uri,
    ) {
        viewModelScope.launch {
            when (assetSourceType) {
                AssetSourceType.ImageUploads -> {
                    val asset = addImageToAssetSource(assetSourceType, uri)
                    onAddAsset(assetSourceType, asset)
                }

                AssetSourceType.VideoUploads -> {
                }

                AssetSourceType.AudioUploads -> {
                }
            }
        }
    }

    private fun onReplaceAsset(
        assetSourceType: AssetSourceType,
        asset: Asset,
        assetType: AssetType,
        designBlock: DesignBlock,
    ) {
        viewModelScope.launch {
            // Replace is currently not supported for stickers by the AssetSource API
            if (assetType == AssetType.Sticker) {
                engine.replaceSticker(designBlock, asset.getUri())
            } else {
                engine.asset.applyAssetSourceAsset(assetSourceType.sourceId, asset, designBlock)
                if (engine.block.getKindEnum(
                        designBlock,
                    ) == BlockKind.Image && engine.editor.getSettingEnum(ROLE) == ROLE_ADOPTER
                ) {
                    engine.block.setPlaceholderEnabled(designBlock, false)
                }
            }
            engine.editor.addUndoStep()
        }
    }

    private fun onReplaceUri(
        assetSourceType: UploadAssetSourceType,
        uri: Uri,
        designBlock: DesignBlock,
    ) {
        viewModelScope.launch {
            when (assetSourceType) {
                AssetSourceType.ImageUploads -> {
                    val asset = addImageToAssetSource(assetSourceType, uri)
                    onReplaceAsset(assetSourceType, asset, AssetType.Image, designBlock)
                }

                else -> throw IllegalArgumentException("Replace using Uri is currently only supported for images.")
            }
        }
    }

    private fun onDispose() {
        libraryCategories.forEach {
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
                    uploadAssetSource =
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

    private fun createWrappedAsset(
        asset: Asset,
        assetSourceType: AssetSourceType,
        assetType: AssetType,
    ): WrappedAsset {
        return if (assetType == AssetType.Text) {
            WrappedAsset.TextAsset(
                asset = asset,
                assetSourceType = assetSourceType,
                assetType = assetType,
                fontFamily = checkNotNull(checkNotNull(fontFamilies.value)[asset.getMeta("fontFamily", "")]),
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

    private suspend fun addImageToAssetSource(
        assetSourceType: UploadAssetSourceType,
        uri: Uri,
    ): Asset {
        val (width, height) = getImageDimensions(uri)
        val uriString = uri.toString()
        val assetDefinition =
            AssetDefinition(
                id = UUID.randomUUID().toString(),
                meta =
                    mapOf(
                        "uri" to uriString,
                        "thumbUri" to uriString,
                        "kind" to "image",
                        "fillType" to FillType.Image.key,
                        "width" to width.toString(),
                        "height" to height.toString(),
                    ),
            ).let { onUpload(it, engine, assetSourceType) }
        engine.asset.addAsset(
            sourceId = assetSourceType.sourceId,
            asset = assetDefinition,
        )
        val result = findAssets(assetSourceType.sourceId, perPage = 10, query = assetDefinition.id)
        return result.assets.single()
    }

    private suspend fun getImageDimensions(uri: Uri): Pair<Int, Int> {
        val result = imageLoader.execute(Environment.newImageRequest(uri))
        val bitmap = ((result as? SuccessResult)?.drawable as BitmapDrawable).bitmap
        val width = bitmap.width
        val height = bitmap.height
        return width to height
    }
}
