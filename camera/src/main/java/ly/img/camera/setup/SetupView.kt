package ly.img.camera.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ly.img.editor.core.ui.permissions.PermissionsView

@Composable
internal fun SetupView(
    modifier: Modifier,
    hasRequiredPermissions: Boolean,
    isLoading: Boolean,
    error: Throwable?,
    onClose: () -> Unit,
    onAllPermissionsGranted: () -> Unit,
) {
    Column(
        modifier =
            modifier
                .requiredWidth(264.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when {
            !hasRequiredPermissions -> PermissionsView(onAllPermissionsGranted = onAllPermissionsGranted)
            isLoading ->
                CircularProgressIndicator(
                    modifier = Modifier.size(36.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            error != null -> ErrorView(error, onClose)
        }
    }
}
