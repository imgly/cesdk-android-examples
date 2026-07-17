data class RefreshAssetsResult(
    val sourceId: String,
    val initialTotal: Int,
    val afterUploadTotal: Int,
    val renamedAssetLabel: String?,
    val remainingAssetIds: List<String>,
    val refreshEventSourceIds: List<String>,
)
