package ly.img.editor.core.ui.library

import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.request.SuccessResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ly.img.editor.core.R
import ly.img.editor.core.library.AssetSourceGroup
import ly.img.editor.core.library.AssetSourceGroupType
import ly.img.editor.core.library.LibraryCategory
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
import ly.img.editor.core.ui.library.engine.addText
import ly.img.editor.core.ui.library.engine.replaceSticker
import ly.img.editor.core.ui.library.state.AssetLibraryUiState
import ly.img.editor.core.ui.library.state.AssetsData
import ly.img.editor.core.ui.library.state.AssetsLoadState
import ly.img.editor.core.ui.library.state.CategoryLoadState
import ly.img.editor.core.ui.library.state.LibraryCategoryData
import ly.img.editor.core.ui.library.state.LibraryStackData
import ly.img.editor.core.ui.library.state.WrappedAsset
import ly.img.editor.core.ui.library.state.WrappedFindAssetsResult
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
import ly.img.editor.core.ui.library.util.getTitleRes
import ly.img.editor.core.ui.register
import ly.img.engine.Asset
import ly.img.engine.AssetDefinition
import ly.img.engine.DesignBlock
import ly.img.engine.FillType
import ly.img.engine.FindAssetsQuery
import ly.img.engine.FindAssetsResult
import ly.img.engine.PositionMode
import ly.img.engine.SceneMode
import java.util.UUID

internal class LibraryViewModel : ViewModel() {
    private val assetsRepo = Environment.getAssetsRepo()
    private val imageLoader = Environment.getImageLoader()
    private val engine = Environment.getEngine()

    private val fontFamilies: StateFlow<Result<Map<String, FontFamilyData>>?> = assetsRepo.fontFamilies

    val libraryCategories = buildList {
        val isSceneModeVideo = Environment.sceneMode == SceneMode.VIDEO

        val elementsAssetSourceGroupList = buildList {
            add(AssetSourceGroup(R.string.cesdk_gallery, buildList {
                add(AssetSourceType.ImageUploads)
                if (isSceneModeVideo) add(AssetSourceType.VideoUploads)
            }, AssetSourceGroupType.Gallery))

            if (isSceneModeVideo) {
                add(
                    AssetSourceGroup(
                        R.string.cesdk_videos,
                        listOf(
                            AssetSourceType.VideoUploads,
                            AssetSourceType.Videos
                        ),
                        AssetSourceGroupType.Video
                    )
                )

                add(
                    AssetSourceGroup(
                        R.string.cesdk_audio,
                        listOf(
                            AssetSourceType.AudioUploads,
                            AssetSourceType.Audio
                        ),
                        AssetSourceGroupType.Audio
                    )
                )
            }

            add(
                AssetSourceGroup(
                    R.string.cesdk_images,
                    listOf(
                        AssetSourceType.ImageUploads,
                        AssetSourceType.Images,
                        AssetSourceType.Unsplash
                    ),
                    AssetSourceGroupType.Image
                )
            )

            add(
                AssetSourceGroup(
                    R.string.cesdk_text,
                    listOf(
                        AssetSourceType.Text
                    ),
                    AssetSourceGroupType.Text
                )
            )

            add(
                AssetSourceGroup(
                    R.string.cesdk_shapes,
                    listOf(
                        AssetSourceType.Shapes
                    ),
                    AssetSourceGroupType.Shape
                )
            )

            add(
                AssetSourceGroup(
                    R.string.cesdk_stickers,
                    listOf(
                        AssetSourceType.Stickers,
                    ),
                    AssetSourceGroupType.Sticker
                )
            )
        }
        add(LibraryCategory.Elements(elementsAssetSourceGroupList))

        if (isSceneModeVideo) {
            add(LibraryCategory.Video)
            add(LibraryCategory.Audio)
        }

        add(LibraryCategory.Images)
        add(LibraryCategory.Text)
        add(LibraryCategory.Shapes)
        add(LibraryCategory.Stickers)
        add(LibraryCategory.Filters)
        add(LibraryCategory.FxEffects)
        add(LibraryCategory.Blur)
    }

    private val _uiEvent = Channel<LibraryUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val libraryCategoriesDataMap = libraryCategories.associateWith {
        LibraryCategoryData(
            uiStateFlow = MutableStateFlow(
                AssetLibraryUiState(
                    libraryCategory = it,
                    titleRes = getTitleRes(it),
                )
            ),
            dataStack = arrayListOf(LibraryStackData(it.assetSourceGroups))
        )
    }

    private val eventHandler = EventsHandler {
        register<OnDispose> {
            onDispose()
        }
        register<OnDrillDown> {
            onDrillDown(it.libraryCategory, it.libraryStackData)
        }
        register<OnEnterSearchMode> {
            onEnterSearchMode(it.enter, it.libraryCategory)
        }
        register<OnFetch> {
            onFetch(it.libraryCategory, it.flatten)
        }
        register<OnPopStack> {
            onPopStack(it.libraryCategory)
        }
        register<OnSearchTextChange> {
            onSearchTextChange(it.value, it.libraryCategory, it.debounce)
        }
        register<OnReplaceAsset> {
            onReplaceAsset(it.assetSourceType, it.asset, it.designBlock)
        }
        register<OnReplaceUri> {
            onReplaceUri(it.assetSource, it.uri, it.designBlock)
        }
        register<OnAddAsset> {
            onAddAsset(it.assetSourceType, it.asset)
        }
        register<OnAddUri> {
            onAddUri(it.assetSource, it.uri)
        }
        register<OnAssetLongClick> {
            onAssetLongClick(it.assetSourceType, it.asset)
        }
    }

    fun onEvent(event: LibraryEvent) = eventHandler.handleEvent(event)

    fun getAssetLibraryUiState(libraryCategory: LibraryCategory): StateFlow<AssetLibraryUiState> {
        return getLibraryCategoryData(libraryCategory).uiStateFlow
    }

    private fun getLibraryCategoryData(libraryCategory: LibraryCategory): LibraryCategoryData {
        return checkNotNull(libraryCategoriesDataMap[libraryCategory])
    }

    private fun onAssetLongClick(assetSourceType: AssetSourceType, asset: Asset) {
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
                    assetSourceLicense = sourceLicense
                )
            )
        }
    }

    private fun onAddAsset(assetSourceType: AssetSourceType, asset: Asset) {
        viewModelScope.launch {
            val designBlock = if (assetSourceType == AssetSourceType.Text) {
                val fontFamilyString = asset.getMeta("fontFamily", "")
                val fontFamily = checkNotNull(checkNotNull(fontFamilies.value).getOrThrow()[fontFamilyString])
                val fontSize = requireNotNull(asset.getMeta("fontSize", "")).toFloat()
                val fontWeight = FontWeight(requireNotNull(asset.getMeta("fontWeight", "")).toInt())
                engine.addText(fontFamily.getFontData(fontWeight).fontPath, fontSize)
            } else {
                engine.asset.applyAssetSourceAsset(assetSourceType.sourceId, asset) ?: return@launch
            }

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

    private fun onAddUri(assetSource: UploadAssetSourceType, uri: Uri) {
        viewModelScope.launch {
            when (assetSource) {
                AssetSourceType.ImageUploads -> {
                    val asset = addImageToAssetSource(assetSource, uri)
                    onAddAsset(assetSource, asset)
                }

                AssetSourceType.VideoUploads -> {
                }

                AssetSourceType.AudioUploads -> {
                }
            }
        }
    }

    private fun onReplaceAsset(assetSourceType: AssetSourceType, asset: Asset, designBlock: DesignBlock) {
        viewModelScope.launch {
            // Replace is currently not supported for stickers by the AssetSource API
            if (assetSourceType is AssetSourceType.Stickers) {
                engine.replaceSticker(designBlock, asset.getUri())
            } else {
                engine.asset.applyAssetSourceAsset(assetSourceType.sourceId, asset, designBlock)
                if (engine.block.getKindEnum(designBlock) == BlockKind.Image && engine.editor.getSettingEnum(ROLE) == ROLE_ADOPTER) {
                    engine.block.setPlaceholderEnabled(designBlock, false)
                }
            }
            engine.editor.addUndoStep()
        }
    }

    private fun onReplaceUri(assetSource: UploadAssetSourceType, uri: Uri, designBlock: DesignBlock) {
        viewModelScope.launch {
            when (assetSource) {
                AssetSourceType.ImageUploads -> {
                    val asset = addImageToAssetSource(assetSource, uri)
                    onReplaceAsset(assetSource, asset, designBlock)
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

    private fun onEnterSearchMode(enter: Boolean, libraryCategory: LibraryCategory) {
        val uiStateFlow = getLibraryCategoryData(libraryCategory).uiStateFlow
        if (enter == uiStateFlow.value.isInSearchMode) return
        uiStateFlow.update { it.copy(isInSearchMode = enter) }
    }

    private fun onSearchTextChange(value: String, libraryCategory: LibraryCategory, debounce: Boolean, force: Boolean = false, flatten: Boolean = false) {
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
                    titleRes = if (force) getTitleRes(libraryCategory) else it.titleRes,
                    assetsData = AssetsData(),
                    sectionItems = if (force) listOf() else it.sectionItems
                )
            }

            // we only need to fetch when user is searching
            if (!force) {
                onFetch(libraryCategory, flatten)
            }
        }.also {
            categoryData.searchJob
        }
    }

    private fun onFetch(libraryCategory: LibraryCategory, flatten: Boolean) {
        val categoryData = getLibraryCategoryData(libraryCategory)
        val assetLibraryUiStateFlow = categoryData.uiStateFlow
        val assetsData = assetLibraryUiStateFlow.value.assetsData
        val lastInStackData = categoryData.dataStack.last()

        @StringRes
        fun getPageTitleRes(): Int {
            return if (lastInStackData.assetSourceGroups.size > 1) getTitleRes(libraryCategory)
            else lastInStackData.assetSourceGroups.single().titleRes
        }

        viewModelScope.launch {
            if (lastInStackData.isSingleAssetSource() &&
                (lastInStackData.getSingleAssetSourceGroupType() == AssetSourceGroupType.Text || lastInStackData.fullQuery)
            ) {
                if (((assetsData.page == 0 && assetsData.assets.isEmpty()) || assetsData.canPaginate) &&
                    !(assetsData.assetsLoadState == AssetsLoadState.Loading || assetsData.assetsLoadState == AssetsLoadState.Paginating)
                ) {
                    categoryData.dirty = false
                    assetLibraryUiStateFlow.update {
                        it.copy(
                            titleRes = getPageTitleRes(),
                            isRoot = categoryData.dataStack.size == 1,
                            assetsData = it.assetsData.copy(
                                assetSourceGroupType = lastInStackData.assetSourceGroups.single().type,
                                assetSourceType = lastInStackData.getSingleAssetSource(),
                                assetsLoadState = if (assetsData.page == 0) AssetsLoadState.Loading else AssetsLoadState.Paginating
                            ),
                            loadState = CategoryLoadState.LoadingAssets
                        )
                    }
                    fetchAssetsForSingleAssetSource(libraryCategory)
                }
            } else {
                if (assetLibraryUiStateFlow.value.loadState != CategoryLoadState.Idle && !categoryData.dirty) return@launch
                categoryData.dirty = false
                assetLibraryUiStateFlow.update {
                    it.copy(
                        titleRes = getPageTitleRes(),
                        loadState = CategoryLoadState.Loading,
                        isRoot = categoryData.dataStack.size == 1,
                        sectionItems = listOf(LibrarySectionItem.Loading)
                    )
                }
                fetchAssetsForAssetSourceGroups(libraryCategory, flatten)
            }
        }.also {
            categoryData.fetchJob = it
        }
    }

    private suspend fun fetchAssetsForAssetSourceGroups(libraryCategory: LibraryCategory, flatten: Boolean = true) {
        val categoryData = getLibraryCategoryData(libraryCategory)
        val assetLibraryUiStateFlow = categoryData.uiStateFlow
        val lastInStackData = categoryData.dataStack.last()

        val loadingSectionItemsList = mutableListOf<LibrarySectionItem>()
        val shouldExpandGroups = lastInStackData.assetSourceGroups.size == 1

        fun addAssetSourceGroupToList(
            titleRes: Int,
            assetSourceGroup: AssetSourceGroup,
            assetSourceType: AssetSourceType?,
            group: String?,
            fullQuery: Boolean
        ) {
            loadingSectionItemsList.add(
                LibrarySectionItem.Header(
                    titleRes = titleRes,
                    stackData = LibraryStackData(listOf(assetSourceGroup), group, fullQuery),
                    uploadAssetSource = assetSourceType as? UploadAssetSourceType,
                )
            )
            loadingSectionItemsList.add(LibrarySectionItem.ContentLoading(assetSourceGroup.type))
        }

        lastInStackData.assetSourceGroups.forEach { assetSourceGroup ->
            if (shouldExpandGroups) {
                assetSourceGroup.sources.forEach { assetSource ->
                    val groups = engine.asset.getGroups(assetSource.sourceId)
                    val totalGroups = if (groups.isNullOrEmpty()) listOf(null) else groups
                    totalGroups.forEach { group ->
                        addAssetSourceGroupToList(
                            titleRes = getTitleRes(group ?: assetSource.sourceId),
                            assetSourceGroup = AssetSourceGroup(
                                getTitleRes(group ?: assetSource.sourceId),
                                listOf(assetSource),
                                assetSourceGroup.type
                            ),
                            assetSourceType = assetSource,
                            group = group,
                            fullQuery = true
                        )
                    }
                }
            } else {
                addAssetSourceGroupToList(
                    titleRes = assetSourceGroup.titleRes,
                    assetSourceGroup = assetSourceGroup,
                    assetSourceType = null,
                    group = null,
                    fullQuery = false
                )
            }
        }
        assetLibraryUiStateFlow.update {
            it.copy(loadState = CategoryLoadState.LoadingSections, sectionItems = loadingSectionItemsList)
        }

        if (shouldExpandGroups) {
            val assetSourceGroup = lastInStackData.assetSourceGroups.single()
            assetSourceGroup.sources.forEach { assetSource ->
                val sectionItemsList = mutableListOf<LibrarySectionItem>()
                val groups = engine.asset.getGroups(assetSource.sourceId)
                val totalGroups = if (groups.isNullOrEmpty()) listOf(null) else groups
                totalGroups.forEach { group ->
                    var findAssetsResult: FindAssetsResult? = null
                    try {
                        findAssetsResult = findAssets(
                            assetSource.sourceId,
                            query = assetLibraryUiStateFlow.value.searchText,
                            group = group,
                            perPage = if (flatten) Int.MAX_VALUE else assetSourceGroup.previewCount()
                        )
                    } catch (ex: Exception) {
                        if (ex is CancellationException) {
                            throw ex
                        }
                    }

                    val libraryStackData = LibraryStackData(
                        assetSourceGroups = listOf(
                            AssetSourceGroup(
                                getTitleRes(group ?: assetSource.sourceId),
                                listOf(assetSource),
                                lastInStackData.assetSourceGroups.single().type
                            )
                        ),
                        group = group,
                        fullQuery = true
                    )

                    sectionItemsList.add(
                        LibrarySectionItem.Header(
                            titleRes = getTitleRes(group ?: assetSource.sourceId),
                            stackData = libraryStackData,
                            uploadAssetSource = assetSource as? UploadAssetSourceType,
                            count = findAssetsResult?.total
                        )
                    )
                    sectionItemsList.add(
                        if (findAssetsResult != null) {
                            val wrappedAssets = findAssetsResult.assets.map {
                                createWrappedAsset(it, assetSource, assetSourceGroup.type)
                            }
                            LibrarySectionItem.Content(
                                wrappedAssets = wrappedAssets,
                                assetSourceGroupType = assetSourceGroup.type,
                                stackData = libraryStackData,
                                moreAssetsAvailable = findAssetsResult.total > wrappedAssets.size
                            )
                        } else {
                            LibrarySectionItem.Error(assetSourceGroupType = assetSourceGroup.type)
                        }
                    )
                }
                assetLibraryUiStateFlow.update {
                    val totalSectionItemsList = it.sectionItems.toMutableList()
                    val index = totalSectionItemsList.indexOfFirst { item ->
                        (item as? LibrarySectionItem.Header)?.stackData?.getSingleAssetSource() == assetSource
                    }
                    if (index >= 0) {
                        sectionItemsList.forEachIndexed { idx, item ->
                            totalSectionItemsList[index + idx] = item
                        }
                    }
                    it.copy(sectionItems = totalSectionItemsList)
                }
            }
        } else {
            lastInStackData.assetSourceGroups.forEach { assetSourceGroup ->
                val findAssetResults = linkedSetOf<WrappedFindAssetsResult>()
                var errorCount = 0
                var totalCount = 0
                for (assetSource in assetSourceGroup.sources) {
                    val groups = engine.asset.getGroups(assetSource.sourceId)
                    val totalGroups = if (groups.isNullOrEmpty()) listOf(null) else groups
                    var error = false
                    for (group in totalGroups) {
                        try {
                            val findAssetsResult = findAssets(
                                assetSource.sourceId,
                                query = assetLibraryUiStateFlow.value.searchText,
                                group = group,
                                perPage = if (flatten) Int.MAX_VALUE else assetSourceGroup.previewCount()
                            )
                            totalCount = try {
                                Math.addExact(totalCount, findAssetsResult.total)
                            } catch (ex: ArithmeticException) {
                                Int.MAX_VALUE
                            }
                            findAssetResults.add(
                                WrappedFindAssetsResult(
                                    assetSourceType = assetSource,
                                    findAssetsResult = findAssetsResult
                                )
                            )
                        } catch (ex: Exception) {
                            if (ex is CancellationException) {
                                throw ex
                            }
                            error = true
                        }
                    }
                    if (error) {
                        errorCount += 1
                    }
                }

                val allFailed = errorCount == assetSourceGroup.sources.size

                val wrappedAssets = mutableListOf<WrappedAsset>()
                if (!allFailed) {
                    var index = 0
                    val requiredCount = if (flatten) Int.MAX_VALUE else assetSourceGroup.previewCount()
                    var iterator = findAssetResults.iterator()
                    while (wrappedAssets.size != requiredCount) {
                        while (iterator.hasNext()) {
                            val wrappedFindAssetsResult = iterator.next()
                            val asset = wrappedFindAssetsResult.findAssetsResult.assets.getOrNull(index)
                            if (asset == null) iterator.remove()
                            else wrappedAssets.add(
                                createWrappedAsset(
                                    asset = asset,
                                    assetSourceType = wrappedFindAssetsResult.assetSourceType,
                                    assetSourceGroupType = assetSourceGroup.type
                                )
                            )
                            if (wrappedAssets.size == requiredCount) break
                        }
                        iterator = findAssetResults.iterator()
                        if (!iterator.hasNext()) break
                        index++
                    }
                }
                val sectionItemsList = mutableListOf<LibrarySectionItem>()
                val libraryStackData = LibraryStackData(listOf(assetSourceGroup), null, false)
                sectionItemsList.add(
                    LibrarySectionItem.Header(
                        titleRes = assetSourceGroup.titleRes,
                        stackData = libraryStackData,
                        uploadAssetSource = null,
                        count = if (allFailed) null else totalCount
                    )
                )

                sectionItemsList.add(
                    if (allFailed) {
                        LibrarySectionItem.Error(assetSourceGroupType = assetSourceGroup.type)
                    } else {
                        LibrarySectionItem.Content(
                            wrappedAssets = wrappedAssets,
                            assetSourceGroupType = assetSourceGroup.type,
                            stackData = libraryStackData,
                            moreAssetsAvailable = totalCount > wrappedAssets.size
                        )
                    }
                )

                assetLibraryUiStateFlow.update {
                    val totalSectionItemsList = it.sectionItems.toMutableList()
                    val index = checkNotNull(totalSectionItemsList.indexOfFirst { item ->
                        (item as? LibrarySectionItem.Header)?.stackData?.assetSourceGroups?.single() == assetSourceGroup
                    })
                    sectionItemsList.forEachIndexed { idx, item ->
                        totalSectionItemsList[index + idx] = item
                    }
                    it.copy(sectionItems = totalSectionItemsList)
                }
            }
        }
        assetLibraryUiStateFlow.update {
            it.copy(loadState = CategoryLoadState.Success)
        }
    }

    private fun AssetSourceGroup.previewCount(): Int {
        return if (type == AssetSourceGroupType.Audio || type == AssetSourceGroupType.Text) 3 else 10
    }

    private fun createWrappedAsset(
        asset: Asset,
        assetSourceType: AssetSourceType,
        assetSourceGroupType: AssetSourceGroupType
    ): WrappedAsset {
        return if (assetSourceGroupType == AssetSourceGroupType.Text) {
            WrappedAsset.TextAsset(
                asset = asset,
                assetSourceType = assetSourceType,
                fontFamily = checkNotNull(checkNotNull(fontFamilies.value).getOrThrow()[asset.getMeta("fontFamily", "")])
            )
        } else {
            WrappedAsset.GenericAsset(
                asset = asset,
                assetSourceType = assetSourceType
            )
        }
    }

    private suspend fun fetchAssetsForSingleAssetSource(libraryCategory: LibraryCategory) {
        val categoryData = getLibraryCategoryData(libraryCategory)
        val assetLibraryUiStateFlow = categoryData.uiStateFlow
        val assetsData = assetLibraryUiStateFlow.value.assetsData
        val lastInStackData = categoryData.dataStack.last()
        val singleAssetSource = lastInStackData.getSingleAssetSource()

        try {
            val findAssetsResult = findAssets(
                sourceId = singleAssetSource.sourceId,
                query = assetLibraryUiStateFlow.value.searchText,
                page = assetsData.page,
                perPage = 20,
                group = lastInStackData.group
            )
            val canPaginate = findAssetsResult.nextPage > 0
            val resultAssets = findAssetsResult.assets.map {
                createWrappedAsset(
                    it,
                    assetSourceType = singleAssetSource,
                    assetSourceGroupType = lastInStackData.getSingleAssetSourceGroupType()
                )
            }
            val assets = if (assetsData.page == 0) resultAssets else assetsData.assets + resultAssets
            assetLibraryUiStateFlow.update {
                it.copy(
                    assetsData = it.assetsData.copy(
                        canPaginate = canPaginate,
                        page = if (canPaginate) assetsData.page + 1 else assetsData.page,
                        assets = assets,
                        assetsLoadState = if (assets.isEmpty()) AssetsLoadState.EmptyResult else AssetsLoadState.Idle,
                    )
                )
            }
        } catch (ex: Exception) {
            if (ex is CancellationException) {
                throw ex
            } else {
                assetLibraryUiStateFlow.update {
                    it.copy(
                        assetsData = assetsData.copy(
                            assetsLoadState = if (assetsData.page == 0) AssetsLoadState.Error else AssetsLoadState.PaginationError,
                        )
                    )
                }
            }
        }
    }

    private suspend fun findAssets(
        sourceId: String,
        perPage: Int,
        query: String,
        page: Int = 0,
        group: String? = null,
    ): FindAssetsResult {
        return engine.asset.findAssets(
            sourceId = sourceId,
            query = FindAssetsQuery(
                perPage = perPage,
                query = query,
                page = page,
                groups = if (group == null) null else listOf(group),
                locale = "en"
            )
        )
    }

    private fun onDrillDown(libraryCategory: LibraryCategory, libraryStackData: LibraryStackData, flatten: Boolean = false) {
        modifyDataStack(libraryCategory, flatten) { dataStack ->
            dataStack.add(libraryStackData)
        }
    }

    private fun onPopStack(libraryCategory: LibraryCategory, flatten: Boolean = false) {
        modifyDataStack(libraryCategory, flatten) { dataStack ->
            dataStack.removeLast()
        }
    }

    private fun modifyDataStack(libraryCategory: LibraryCategory, flatten: Boolean = false, action: (dataStack: ArrayList<LibraryStackData>) -> Unit) {
        val categoryData = getLibraryCategoryData(libraryCategory)
        categoryData.searchJob?.cancel()
        categoryData.fetchJob?.cancel()

        action(categoryData.dataStack)
        categoryData.dirty = true
        categoryData.uiStateFlow.update {
            it.copy(assetsData = AssetsData())
        }
        onFetch(libraryCategory, flatten)
    }

    private suspend fun addImageToAssetSource(assetSource: UploadAssetSourceType, uri: Uri): Asset {
        val (width, height) = getImageDimensions(uri)
        val uuid = UUID.randomUUID().toString()
        val uriString = uri.toString()
        engine.asset.addAsset(
            sourceId = assetSource.sourceId,
            asset = AssetDefinition(
                id = uuid,
                meta = mapOf(
                    "uri" to uriString,
                    "thumbUri" to uriString,
                    "kind" to "image",
                    "fillType" to FillType.Image.key,
                    "width" to width.toString(),
                    "height" to height.toString()
                )
            )
        )
        val result = findAssets(assetSource.sourceId, perPage = 10, query = uuid)
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