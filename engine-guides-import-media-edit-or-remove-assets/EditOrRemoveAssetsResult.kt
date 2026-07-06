data class EditOrRemoveAssetsResult(
    val queriedAssetId: String,
    val initialAssetIds: List<String>,
    val updatedLabel: String?,
    val remainingAssetIds: List<String>,
    val removedAssetWasPresent: Boolean,
    val temporarySourceExistsAfterRemoval: Boolean,
    val managedSourcesCleanedUp: Boolean,
)
