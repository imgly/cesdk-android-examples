package ly.img.editor.showcases

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Properties

data class BuildMetadata(
    val buildName: String,
    val branchName: String,
    val commitId: String,
) {
    companion object {
        val EMPTY = BuildMetadata(buildName = "", branchName = "", commitId = "")

        suspend fun load(context: Context): BuildMetadata = withContext(Dispatchers.IO) {
            runCatching {
                context.assets.open("build_metadata.properties").use { stream ->
                    val properties = Properties().apply { load(stream) }
                    BuildMetadata(
                        buildName = properties.getProperty("buildName").orEmpty(),
                        branchName = properties.getProperty("branchName").orEmpty(),
                        commitId = properties.getProperty("commitId").orEmpty(),
                    )
                }
            }.getOrDefault(EMPTY)
        }
    }
}
