import kotlinx.coroutines.Job

data class TemplateLibraryResult(
    val templateSourceRegistered: Boolean,
    val businessTemplateIds: List<String>,
    val allTemplateCount: Int,
    val groups: List<String>,
    val hostedSourceId: String,
    val hostedSourceRemoved: Boolean,
    val observedAddedSourceIds: List<String>,
    val observedRemovedSourceIds: List<String>,
    val sourceMonitorJobs: List<Job>,
    val pageCountAfterApply: Int,
)
