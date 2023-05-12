package ly.img.cesdk.library

import android.graphics.drawable.BitmapDrawable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.request.SuccessResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.img.cesdk.Environment
import ly.img.cesdk.engine.ROLE
import ly.img.cesdk.engine.ROLE_ADOPTER
import ly.img.cesdk.engine.addText
import ly.img.cesdk.engine.replaceSticker
import ly.img.cesdk.library.components.assets.AssetSource
import ly.img.cesdk.library.components.assets.FindAssetsResultUiState
import ly.img.cesdk.library.components.assets.ImageSource
import ly.img.cesdk.library.components.assets.ListState
import ly.img.cesdk.library.components.assets.getUri
import ly.img.cesdk.library.data.font.FontFamilyData
import ly.img.engine.Asset
import ly.img.engine.AssetDefinition
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.FindAssetsQuery
import java.util.UUID

class LibraryViewModel : ViewModel() {
    private val assetsRepo = Environment.getAssetsRepo()
    private val imageLoader = Environment.getImageLoader()
    private val engine = Environment.getEngine()

    val fontFamilies: StateFlow<Result<Map<String, FontFamilyData>>?> = assetsRepo.fontFamilies

    private val _imageSearchText = MutableStateFlow("")
    private val imagesSearchText = _imageSearchText.asStateFlow()

    private val _stickerSearchText = MutableStateFlow("")
    private val stickersSearchText = _stickerSearchText.asStateFlow()

    private val _shapeSearchText = MutableStateFlow("")
    private val shapesSearchText = _shapeSearchText.asStateFlow()

    private val _uploadsUiState = MutableStateFlow(FindAssetsResultUiState())
    private val uploadsUiState = _uploadsUiState.asStateFlow()

    private val _imagesUiState = MutableStateFlow(FindAssetsResultUiState())
    private val imagesUiState = _imagesUiState.asStateFlow()

    private val _unsplashUiState = MutableStateFlow(FindAssetsResultUiState())
    private val unsplashUiState = _unsplashUiState.asStateFlow()

    private val _stickersUiState = MutableStateFlow(FindAssetsResultUiState())
    private val stickersUiState = _stickersUiState.asStateFlow()

    private val _shapesUiState = MutableStateFlow(FindAssetsResultUiState())
    private val shapesUiState = _shapesUiState.asStateFlow()

    private var uploadsJob: Job? = null
    private var imagesJob: Job? = null
    private var unsplashJob: Job? = null
    private var stickersJob: Job? = null
    private var shapesJob: Job? = null

    fun getUiStateFlow(assetSource: AssetSource): StateFlow<FindAssetsResultUiState> {
        return when (assetSource) {
            ImageSource.Local -> imagesUiState
            ImageSource.Unsplash -> unsplashUiState
            ImageSource.Uploads -> uploadsUiState
            AssetSource.Shapes -> shapesUiState
            AssetSource.Stickers -> stickersUiState
        }
    }

    fun getSearchTextFlow(assetSource: AssetSource): StateFlow<String> {
        return when (assetSource) {
            is ImageSource -> imagesSearchText
            AssetSource.Shapes -> shapesSearchText
            AssetSource.Stickers -> stickersSearchText
        }
    }

    fun fetchAssets(source: AssetSource) {
        val stateFlow = when (source) {
            ImageSource.Local -> _imagesUiState
            ImageSource.Unsplash -> _unsplashUiState
            ImageSource.Uploads -> _uploadsUiState
            AssetSource.Shapes -> _shapesUiState
            AssetSource.Stickers -> _stickersUiState
        }
        val state = stateFlow.value
        if (((state.page == 0 && state.assets.isEmpty()) || state.canPaginate) && !(state.listState == ListState.LOADING || state.listState == ListState.PAGINATING)) {
            stateFlow.update {
                it.copy(listState = if (state.page == 0) ListState.LOADING else ListState.PAGINATING)
            }
            val job = viewModelScope.launch {
                try {
                    val query = getSearchTextFlow(source).value
                    val findAssetsResult = engine.asset.findAssets(
                        sourceId = source.sourceId,
                        query = FindAssetsQuery(query = query, perPage = 20, page = state.page, locale = "en")
                    )
                    ensureActive()
                    withContext(Dispatchers.Default) {
                        stateFlow.update {
                            val canPaginate = findAssetsResult.nextPage > 0
                            val assetIdsSet = HashSet<String>(it.assetIds)
                            // filter any duplicate assets (usually happens with Unsplash)
                            val resultAssets = findAssetsResult.assets.filter { assetIdsSet.add(it.id) }
                            val assets = if (it.page == 0) resultAssets else it.assets + resultAssets
                            it.copy(
                                canPaginate = canPaginate,
                                searchQuery = query,
                                listState = if (assets.isEmpty()) {
                                    if (source is ImageSource.Uploads && query.isEmpty()) ListState.UPLOADS_EMPTY_MAIN_RESULT
                                    else ListState.EMPTY_RESULT
                                } else ListState.IDLE,
                                page = if (canPaginate) it.page + 1 else it.page,
                                assets = assets,
                                assetIds = assetIdsSet
                            )
                        }
                    }
                } catch (ex: Exception) {
                    if (ex is CancellationException) {
                        throw ex
                    } else {
                        stateFlow.update {
                            it.copy(listState = if (state.page == 0) ListState.ERROR else ListState.PAGINATION_ERROR)
                        }
                    }
                }
            }
            when (source) {
                ImageSource.Local -> imagesJob = job
                ImageSource.Unsplash -> unsplashJob = job
                ImageSource.Uploads -> uploadsJob = job
                AssetSource.Shapes -> shapesJob = job
                AssetSource.Stickers -> stickersJob = job
            }
        }
    }

    private var imageSearchJob: Job? = null
    private var stickerSearchJob: Job? = null
    private var shapeSearchJob: Job? = null
    fun onSearchTextChange(value: String, currentAssetSource: AssetSource, force: Boolean = false) {
        when (currentAssetSource) {
            is ImageSource -> _imageSearchText
            AssetSource.Shapes -> _shapeSearchText
            AssetSource.Stickers -> _stickerSearchText
        }.value = value

        when (currentAssetSource) {
            is ImageSource -> imageSearchJob
            AssetSource.Shapes -> shapeSearchJob
            AssetSource.Stickers -> stickerSearchJob
        }?.cancel()

        fun updateFlow(flow: MutableStateFlow<FindAssetsResultUiState>) {
            flow.update {
                FindAssetsResultUiState(
                    listState = ListState.IDLE
                )
            }
        }

        viewModelScope.launch {
            // debounce is only needed when user is searching
            if (!force) {
                delay(500)
            }

            // cancel all previous jobs
            when (currentAssetSource) {
                is ImageSource -> {
                    uploadsJob?.cancel()
                    imagesJob?.cancel()
                    unsplashJob?.cancel()

                    arrayOf(_imagesUiState, _unsplashUiState, _uploadsUiState).forEach {
                        updateFlow(it)
                    }
                }

                AssetSource.Shapes -> {
                    shapesJob?.cancel()
                    updateFlow(_shapesUiState)
                }

                AssetSource.Stickers -> {
                    stickersJob?.cancel()
                    updateFlow(_stickersUiState)
                }
            }

            // we only need to fetch when user is searching
            if (!force) {
                fetchAssets(currentAssetSource)
            }
        }.also {
            if (!force) {
                when (currentAssetSource) {
                    is ImageSource -> imageSearchJob = it
                    AssetSource.Shapes -> shapeSearchJob = it
                    AssetSource.Stickers -> stickerSearchJob = it
                }
            }
        }
    }

    fun clearSearch(assetSource: AssetSource) {
        onSearchTextChange(value = "", currentAssetSource = assetSource, force = true)
    }

    fun addUploadedImage(uri: String) {
        viewModelScope.launch {
            val asset = addAssetToUploads(uri)
            addAsset(ImageSource.Uploads, asset)
        }
    }

    fun addAsset(assetSource: AssetSource, asset: Asset) {
        viewModelScope.launch {
            engine.asset.applyAssetSourceAsset(assetSource.sourceId, asset)
        }
    }

    fun addText(path: String, size: Float) {
        engine.addText(path, size)
    }

    fun replaceUploadedImage(uri: String, imageBlock: DesignBlock) {
        viewModelScope.launch {
            val asset = addAssetToUploads(uri)
            replaceAsset(ImageSource.Uploads, asset, imageBlock)
        }
    }

    fun replaceAsset(assetSource: AssetSource, asset: Asset, designBlock: DesignBlock) {
        viewModelScope.launch {
            // Replace is currently not supported for stickers by the AssetSource API
            if (assetSource is AssetSource.Stickers) {
                engine.replaceSticker(designBlock, asset.getUri())
            } else {
                engine.asset.applyAssetSourceAsset(assetSource.sourceId, asset, designBlock)
                if (assetSource is ImageSource && engine.editor.getSettingEnum(ROLE) == ROLE_ADOPTER) {
                    engine.block.setPlaceholderEnabled(designBlock, false)
                }
            }
            engine.editor.addUndoStep()
        }
    }

    private suspend fun addAssetToUploads(uri: String): Asset {
        val (width, height) = getImageDimensions(uri)
        val uuid = UUID.randomUUID().toString()
        engine.asset.addAsset(
            sourceId = ImageSource.Uploads.sourceId,
            asset = AssetDefinition(
                id = uuid,
                meta = mapOf(
                    "uri" to uri,
                    "thumbUri" to uri,
                    "blockType" to DesignBlockType.IMAGE.key,
                    "width" to width.toString(),
                    "height" to height.toString()
                )
            )
        )
        val result = engine.asset.findAssets(
            sourceId = ImageSource.Uploads.sourceId,
            query = FindAssetsQuery(
                perPage = 10,
                page = 0,
                query = uuid
            )
        )
        return result.assets.single()
    }

    private suspend fun getImageDimensions(uri: String): Pair<Int, Int> {
        val result = imageLoader.execute(Environment.newImageRequest(uri))
        val bitmap = ((result as? SuccessResult)?.drawable as BitmapDrawable).bitmap
        val width = bitmap.width
        val height = bitmap.height
        return width to height
    }
}