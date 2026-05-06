import android.app.Application
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private sealed interface DataMergeGuideUiState {
    data object Loading : DataMergeGuideUiState

    data class Success(
        val mergedCards: List<MergedCard>,
    ) : DataMergeGuideUiState

    data class Failure(
        val message: String,
    ) : DataMergeGuideUiState
}

@Composable
fun DataMergeGuideScreen(
    navController: NavHostController,
    license: String?,
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val scope = rememberCoroutineScope()
    var uiState by remember {
        mutableStateOf<DataMergeGuideUiState>(DataMergeGuideUiState.Loading)
    }
    var activeRun by remember { mutableStateOf<Job?>(null) }

    fun generateCards() {
        if (activeRun?.isActive == true) return

        activeRun = scope.launch {
            uiState = DataMergeGuideUiState.Loading
            try {
                uiState = runCatching {
                    mergeBusinessCards(
                        application = application,
                        license = license?.ifBlank { null },
                        userId = "data-merge-guide",
                    )
                }.fold(
                    onSuccess = { cards -> DataMergeGuideUiState.Success(cards) },
                    onFailure = { error ->
                        DataMergeGuideUiState.Failure(
                            message = error.message ?: "Unable to run the data merge example.",
                        )
                    },
                )
            } finally {
                activeRun = null
            }
        }
    }

    LaunchedEffect(Unit) {
        generateCards()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Button(onClick = { navController.popBackStack() }) {
            Text("Back")
        }

        Text(
            text = "Data Merge",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "This guide screen generates two personalized business cards with the headless engine and shows the first exported PNG.",
            style = MaterialTheme.typography.bodyLarge,
        )
        Button(
            onClick = ::generateCards,
            enabled = activeRun?.isActive != true && uiState !is DataMergeGuideUiState.Loading,
        ) {
            Text("Generate business cards")
        }

        when (val state = uiState) {
            DataMergeGuideUiState.Loading -> {
                CircularProgressIndicator()
                Text("Generating preview output...")
            }

            is DataMergeGuideUiState.Success -> {
                val preview = remember(state.mergedCards) {
                    state.mergedCards.firstOrNull()?.pngBytes?.let { bytes ->
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    }
                }

                Text(
                    text = "Generated ${state.mergedCards.size} personalized exports.",
                    style = MaterialTheme.typography.bodyLarge,
                )
                preview?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Merged business card preview",
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                state.mergedCards.forEach { card ->
                    Text(
                        text = "- ${card.fileName}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            is DataMergeGuideUiState.Failure -> {
                Text(
                    text = state.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}
