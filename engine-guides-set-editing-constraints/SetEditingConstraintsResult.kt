data class SetEditingConstraintsResult(
    val availableScopes: List<String>,
    val moveScopeEnabled: Boolean,
    val moveAllowed: Boolean,
    val resizeAllowed: Boolean,
    val positionLockedDestroyAllowed: Boolean,
    val positionLockedDuplicateAllowed: Boolean,
    val destroyAllowed: Boolean,
    val duplicateAllowed: Boolean,
    val deletionLockedMoveAllowed: Boolean,
)
