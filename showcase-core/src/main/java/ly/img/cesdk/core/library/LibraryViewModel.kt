package ly.img.cesdk.core.library

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
import ly.img.cesdk.core.Environment
import ly.img.cesdk.core.R
import ly.img.cesdk.core.data.AssetSource
import ly.img.cesdk.core.data.UploadAssetSource
import ly.img.cesdk.core.data.WrappedAsset
import ly.img.cesdk.core.data.WrappedFindAssetsResult
import ly.img.cesdk.core.data.font.FontFamilyData
import ly.img.cesdk.core.data.getMeta
import ly.img.cesdk.core.data.getUri
import ly.img.cesdk.core.engine.ROLE
import ly.img.cesdk.core.engine.ROLE_ADOPTER
import ly.img.cesdk.core.library.components.section.LibrarySectionItem
import ly.img.cesdk.core.library.engine.addText
import ly.img.cesdk.core.library.engine.replaceSticker
import ly.img.cesdk.core.library.state.AssetLibraryUiState
import ly.img.cesdk.core.library.state.AssetSourceGroup
import ly.img.cesdk.core.library.state.AssetSourceGroupType
import ly.img.cesdk.core.library.state.AssetsData
import ly.img.cesdk.core.library.state.AssetsLoadState
import ly.img.cesdk.core.library.state.CategoryLoadState
import ly.img.cesdk.core.library.state.LibraryCategory
import ly.img.cesdk.core.library.state.LibraryCategoryData
import ly.img.cesdk.core.library.state.LibraryStackData
import ly.img.cesdk.core.library.util.LibraryEvent
import ly.img.cesdk.core.library.util.LibraryUiEvent
import ly.img.cesdk.core.library.util.getTitleRes
import ly.img.engine.Asset
import ly.img.engine.AssetDefinition
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.FindAssetsQuery
import ly.img.engine.FindAssetsResult
import ly.img.engine.SceneMode
import java.util.UUID

internal class LibraryViewModel : ViewModel() {
    private val assetsRepo = Environment.getAssetsRepo()
    private val imageLoader = Environment.getImageLoader()
    private val engine = Environment.getEngine()

    val fontFamilies: StateFlow<Result<Map<String, FontFamilyData>>?> = assetsRepo.fontFamilies

    val libraryCategories = buildList {
        val isSceneModeVideo = Environment.sceneMode == SceneMode.VIDEO

        val elementsAssetSourceGroupList = buildList {
            add(AssetSourceGroup(R.string.cesdk_gallery, buildList {
                add(AssetSource.ImageUploads)
                if (isSceneModeVideo) add(AssetSource.VideoUploads)
            }, AssetSourceGroupType.Gallery))

            if (isSceneModeVideo) {
                add(
                    AssetSourceGroup(
                        R.string.cesdk_videos,
                        listOf(
                            AssetSource.VideoUploads,
                            AssetSource.Videos
                        ),
                        AssetSourceGroupType.Video
                    )
                )

                add(
                    AssetSourceGroup(
                        R.string.cesdk_audio,
                        listOf(
                            AssetSource.AudioUploads,
                            AssetSource.Audio
                        ),
                        AssetSourceGroupType.Audio
                    )
                )
            }

            add(
                AssetSourceGroup(
                    R.string.cesdk_images,
                    listOf(
                        AssetSource.ImageUploads,
                        AssetSource.Images,
                        AssetSource.Unsplash
                    ),
                    AssetSourceGroupType.Image
                )
            )

            add(
                AssetSourceGroup(
                    R.string.cesdk_text,
                    listOf(
                        AssetSource.Text
                    ),
                    AssetSourceGroupType.Text
                )
            )

            add(
                AssetSourceGroup(
                    R.string.cesdk_shapes,
                    listOf(
                        AssetSource.Shapes
                    ),
                    AssetSourceGroupType.Shape
                )
            )

            add(
                AssetSourceGroup(
                    R.string.cesdk_stickers,
                    listOf(
                        AssetSource.Stickers,
                    ),
                    AssetSourceGroupType.Sticker
                )
            )
        }
        add(LibraryCategory.Elements(elementsAssetSourceGroupList))

        val galleryAssetSourceGroupList = listOf(
            AssetSourceGroup(R.string.cesdk_gallery, buildList {
                add(AssetSource.ImageUploads)
                if (isSceneModeVideo) add(AssetSource.VideoUploads)
            }, AssetSourceGroupType.Gallery)
        )
        add(LibraryCategory.Gallery(galleryAssetSourceGroupList))

        if (isSceneModeVideo) {
            add(LibraryCategory.Video)
            add(LibraryCategory.Audio)
        }

        add(LibraryCategory.Images)
        add(LibraryCategory.Text)
        add(LibraryCategory.Shapes)
        add(LibraryCategory.Stickers)
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

    fun onEvent(event: LibraryEvent) {
        when (event) {
            is LibraryEvent.OnDispose -> onDispose(event.libraryCategory)
            is LibraryEvent.OnDrillDown -> onDrillDown(event.libraryCategory, event.libraryStackData)
            is LibraryEvent.OnEnterSearchMode -> onEnterSearchMode(event.enter, event.libraryCategory)
            is LibraryEvent.OnFetch -> onFetch(event.libraryCategory)
            is LibraryEvent.OnPopStack -> onPopStack(event.libraryCategory)
            is LibraryEvent.OnSearchTextChange -> onSearchTextChange(event.value, event.libraryCategory, event.debounce)
            is LibraryEvent.OnReplaceAsset -> onReplaceAsset(event.assetSource, event.asset, event.designBlock)
            is LibraryEvent.OnReplaceUri -> onReplaceUri(event.assetSource, event.uri, event.designBlock)
            is LibraryEvent.OnAddAsset -> onAddAsset(event.assetSource, event.asset)
            is LibraryEvent.OnAddUri -> onAddUri(event.assetSource, event.uri)
            is LibraryEvent.OnAssetLongClick -> onAssetLongClick(event.assetSource, event.asset)
        }
    }

    fun getAssetLibraryUiState(libraryCategory: LibraryCategory): StateFlow<AssetLibraryUiState> {
        return getLibraryCategoryData(libraryCategory).uiStateFlow
    }

    private fun getLibraryCategoryData(libraryCategory: LibraryCategory): LibraryCategoryData {
        return checkNotNull(libraryCategoriesDataMap[libraryCategory])
    }

    private fun onAssetLongClick(assetSource: AssetSource, asset: Asset) {
        val sourceId = assetSource.sourceId
        val assetLicense = asset.license
        val sourceLicense = engine.asset.getLicense(sourceId)
        val showAssetDetails = assetLicense != null || sourceLicense != null
        if (!showAssetDetails) return
        val assetCredits = asset.credits
        val sourceCredits = engine.asset.getCredits(sourceId)
        viewModelScope.launch {
            _uiEvent.send(
                LibraryUiEvent.ShowAssetCredits(
                    assetLabel = asset.label ?: asset.getMeta("filename", null) ?: asset.id,
                    assetCredits = assetCredits,
                    assetLicense = assetLicense,
                    assetSourceCredits = sourceCredits,
                    assetSourceLicense = sourceLicense
                )
            )
        }
    }

    private fun onAddAsset(assetSource: AssetSource, asset: Asset) {
        if (assetSource == AssetSource.Text) {
            val fontFamilyString = asset.getMeta("fontFamily", "")
            val fontFamily = checkNotNull(checkNotNull(fontFamilies.value).getOrThrow()[fontFamilyString])
            val fontSize = requireNotNull(asset.getMeta("fontSize", "")).toFloat()
            val fontWeight = FontWeight(requireNotNull(asset.getMeta("fontWeight", "")).toInt())
            engine.addText(fontFamily.getFontData(fontWeight).fontPath, fontSize)
        } else {
            viewModelScope.launch {
                engine.asset.applyAssetSourceAsset(assetSource.sourceId, asset)
            }
        }
    }

    private fun onAddUri(assetSource: UploadAssetSource, uri: Uri) {
        viewModelScope.launch {
            when (assetSource) {
                AssetSource.ImageUploads -> {
                    val asset = addImageToAssetSource(assetSource, uri)
                    onAddAsset(assetSource, asset)
                }

                AssetSource.VideoUploads -> {
                }

                AssetSource.AudioUploads -> {
                }
            }
        }
    }

    private fun onReplaceAsset(assetSource: AssetSource, asset: Asset, designBlock: DesignBlock) {
        viewModelScope.launch {
            // Replace is currently not supported for stickers by the AssetSource API
            if (assetSource is AssetSource.Stickers) {
                engine.replaceSticker(designBlock, asset.getUri())
            } else {
                engine.asset.applyAssetSourceAsset(assetSource.sourceId, asset, designBlock)
                if (engine.block.getType(designBlock) == DesignBlockType.IMAGE.key && engine.editor.getSettingEnum(ROLE) == ROLE_ADOPTER) {
                    engine.block.setPlaceholderEnabled(designBlock, false)
                }
            }
            engine.editor.addUndoStep()
        }
    }

    private fun onReplaceUri(assetSource: UploadAssetSource, uri: Uri, designBlock: DesignBlock) {
        viewModelScope.launch {
            when (assetSource) {
                AssetSource.ImageUploads -> {
                    val asset = addImageToAssetSource(assetSource, uri)
                    onReplaceAsset(assetSource, asset, designBlock)
                }

                else -> throw IllegalArgumentException("Replace using Uri is currently only supported for images.")
            }
        }
    }

    private fun onDispose(libraryCategory: LibraryCategory) {
        // clear search
        onSearchTextChange("", libraryCategory, debounce = false, force = true)
        // get out of search mode
        onEnterSearchMode(enter = false, libraryCategory)
        // go to root
        val categoryData = getLibraryCategoryData(libraryCategory)
        val dataStack = categoryData.dataStack
        dataStack.subList(1, dataStack.size).clear()
    }

    private fun onEnterSearchMode(enter: Boolean, libraryCategory: LibraryCategory) {
        val uiStateFlow = getLibraryCategoryData(libraryCategory).uiStateFlow
        if (enter == uiStateFlow.value.isInSearchMode) return
        uiStateFlow.update { it.copy(isInSearchMode = enter) }
    }

    private fun onSearchTextChange(value: String, libraryCategory: LibraryCategory, debounce: Boolean, force: Boolean = false) {
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
                onFetch(libraryCategory)
            }
        }.also {
            categoryData.searchJob
        }
    }

    private fun onFetch(libraryCategory: LibraryCategory) {
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
                                assetSource = lastInStackData.getSingleAssetSource(),
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
                fetchAssetsForAssetSourceGroups(libraryCategory)
            }
        }.also {
            categoryData.fetchJob = it
        }
    }

    private suspend fun fetchAssetsForAssetSourceGroups(libraryCategory: LibraryCategory) {
        val categoryData = getLibraryCategoryData(libraryCategory)
        val assetLibraryUiStateFlow = categoryData.uiStateFlow
        val lastInStackData = categoryData.dataStack.last()

        val loadingSectionItemsList = mutableListOf<LibrarySectionItem>()
        val shouldExpandGroups = lastInStackData.assetSourceGroups.size == 1

        fun addAssetSourceGroupToList(
            titleRes: Int,
            assetSourceGroup: AssetSourceGroup,
            assetSource: AssetSource?,
            group: String?,
            fullQuery: Boolean
        ) {
            loadingSectionItemsList.add(
                LibrarySectionItem.Header(
                    titleRes = titleRes,
                    stackData = LibraryStackData(listOf(assetSourceGroup), group, fullQuery),
                    uploadAssetSource = assetSource as? UploadAssetSource,
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
                            assetSource = assetSource,
                            group = group,
                            fullQuery = true
                        )
                    }
                }
            } else {
                addAssetSourceGroupToList(
                    titleRes = assetSourceGroup.titleRes,
                    assetSourceGroup = assetSourceGroup,
                    assetSource = null,
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
                            perPage = assetSourceGroup.previewCount()
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
                            uploadAssetSource = assetSource as? UploadAssetSource,
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
                    val index = checkNotNull(totalSectionItemsList.indexOfFirst { item ->
                        (item as? LibrarySectionItem.Header)?.stackData?.getSingleAssetSource() == assetSource
                    })
                    sectionItemsList.forEachIndexed { idx, item ->
                        totalSectionItemsList[index + idx] = item
                    }
                    it.copy(sectionItems = totalSectionItemsList)
                }
            }
        } else {
            lastInStackData.assetSourceGroups.forEach { assetSourceGroup ->
                val findAssetResults = linkedSetOf<WrappedFindAssetsResult>()
                var error = false
                var totalCount = 0
                for (assetSource in assetSourceGroup.sources) {
                    val groups = engine.asset.getGroups(assetSource.sourceId)
                    val totalGroups = if (groups.isNullOrEmpty()) listOf(null) else groups
                    for (group in totalGroups) {
                        try {
                            val findAssetsResult = findAssets(
                                assetSource.sourceId,
                                query = assetLibraryUiStateFlow.value.searchText,
                                group = group,
                                perPage = assetSourceGroup.previewCount()
                            )
                            totalCount = try {
                                Math.addExact(totalCount, findAssetsResult.total)
                            } catch (ex: ArithmeticException) {
                                Int.MAX_VALUE
                            }
                            findAssetResults.add(
                                WrappedFindAssetsResult(
                                    assetSource = assetSource,
                                    findAssetsResult = findAssetsResult
                                )
                            )
                        } catch (ex: Exception) {
                            if (ex is CancellationException) {
                                throw ex
                            }
                            error = true
                            break
                        }
                    }
                    if (error) break
                }

                val wrappedAssets = mutableListOf<WrappedAsset>()
                if (!error) {
                    var index = 0
                    val requiredCount = assetSourceGroup.previewCount()
                    var iterator = findAssetResults.iterator()
                    while (wrappedAssets.size != requiredCount) {
                        while (iterator.hasNext()) {
                            val wrappedFindAssetsResult = iterator.next()
                            val asset = wrappedFindAssetsResult.findAssetsResult.assets.getOrNull(index)
                            if (asset == null) iterator.remove()
                            else wrappedAssets.add(
                                createWrappedAsset(
                                    asset = asset,
                                    assetSource = wrappedFindAssetsResult.assetSource,
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
                        count = if (error) null else totalCount
                    )
                )

                sectionItemsList.add(
                    if (error) {
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

    private fun createWrappedAsset(
        asset: Asset,
        assetSource: AssetSource,
        assetSourceGroupType: AssetSourceGroupType
    ): WrappedAsset {
        return if (assetSourceGroupType == AssetSourceGroupType.Text) {
            WrappedAsset.TextAsset(
                asset = asset,
                assetSource = assetSource,
                fontFamily = checkNotNull(checkNotNull(fontFamilies.value).getOrThrow()[asset.getMeta("fontFamily", "")])
            )
        } else {
            WrappedAsset.GenericAsset(
                asset = asset,
                assetSource = assetSource
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
                    assetSource = singleAssetSource,
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

    private fun onDrillDown(libraryCategory: LibraryCategory, libraryStackData: LibraryStackData) {
        modifyDataStack(libraryCategory) { dataStack ->
            dataStack.add(libraryStackData)
        }
    }

    private fun onPopStack(libraryCategory: LibraryCategory) {
        modifyDataStack(libraryCategory) { dataStack ->
            dataStack.removeLast()
        }
    }

    private fun modifyDataStack(libraryCategory: LibraryCategory, action: (dataStack: ArrayList<LibraryStackData>) -> Unit) {
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

    private suspend fun addImageToAssetSource(assetSource: UploadAssetSource, uri: Uri): Asset {
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
                    "blockType" to DesignBlockType.IMAGE.key,
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