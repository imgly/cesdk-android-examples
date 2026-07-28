data class TransitionSummary(
    val clipsSupportTransitions: Boolean,
    val incomingClipOffset: Double,
    val pushProperties: List<String>,
    val assignedTransitionType: String?,
    val replacedTransitionType: String,
    val detachedTransitionIsValid: Boolean,
)
