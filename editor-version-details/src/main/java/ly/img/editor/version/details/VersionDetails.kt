package ly.img.editor.version.details

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.appdistribution.impl.InstallActivity
import ly.img.editor.version.details.entity.Progress
import ly.img.editor.version.details.entity.Release

@Composable
fun VersionDetails(
    modifier: Modifier = Modifier,
    versionInfo: String,
    branchName: String,
    versionCode: Int,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier =
                Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (versionInfo.isNotEmpty()) {
                Text(
                    text = versionInfo,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall,
                )
            }
            if (branchName.isNotEmpty() && versionCode > 0) {
                VersionUpdate(
                    branchName = branchName,
                    versionCode = versionCode,
                )
            }
        }
    }
}

@Composable
private fun VersionUpdate(
    branchName: String,
    versionCode: Int,
) {
    val activity = LocalContext.current as Activity
    val viewModel = viewModel { VersionUpdateViewModel() }
    val error by viewModel.errorState.collectAsState(null)
    val showSignInButton by viewModel.showSignInButtonState.collectAsState()
    val updateRequest by viewModel.updateRequestState.collectAsState()
    val updateProgress by viewModel.updateProgressState.collectAsState()

    error?.let {
        Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
    }
    LaunchedEffect(showSignInButton) {
        if (!showSignInButton) {
            viewModel.checkForNewRelease(
                activity = activity,
                branchName = branchName,
                versionCode = versionCode,
            )
        }
    }
    if (showSignInButton) {
        Button(onClick = {
            viewModel.signIn()
        }) {
            Text(text = stringResource(id = R.string.ly_img_editor_version_details_sign_in))
        }
    } else {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            contentAlignment = Alignment.Center,
        ) {
            when (val updateRequestInternal = updateRequest) {
                null -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Release.UpToDate -> {
                    Text(
                        text = stringResource(id = R.string.ly_img_editor_version_details_up_to_date),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
                else -> {
                    Button(onClick = {
                        viewModel.update(context = activity, release = updateRequestInternal)
                    }) {
                        Text(text = stringResource(id = R.string.ly_img_editor_version_details_update))
                    }
                }
            }
        }
    }
    updateProgress?.let {
        UpdateProgressDialog(progress = it, activity = activity) {
            viewModel.cancelActive()
        }
    }
}

@Composable
private fun UpdateProgressDialog(
    progress: Progress,
    activity: Activity,
    onClearProgress: () -> Unit,
) {
    when (progress) {
        is Progress.Ready -> {
            onClearProgress()
            val intent = Intent(activity, InstallActivity::class.java)
            intent.putExtra(VersionUpdateViewModel.EXTRA_APK_INSTALL_PATH, progress.file.path)
            activity.startActivity(intent)
        }
        is Progress.Pending -> {
            Dialog(
                onDismissRequest = { },
                properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
            ) {
                Column(
                    modifier =
                        Modifier
                            .background(color = AlertDialogDefaults.containerColor, shape = AlertDialogDefaults.shape)
                            .padding(all = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(stringResource(id = R.string.ly_img_editor_version_details_pending_dialog_title))
                    Box(
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "${(progress.progress * 100).toInt()}%")
                        CircularProgressIndicator(
                            modifier = Modifier.size(60.dp),
                            progress = progress.progress,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Button(
                        onClick = { onClearProgress() },
                    ) {
                        Text(stringResource(R.string.ly_img_editor_version_details_cancel))
                    }
                }
            }
        }
        is Progress.Error -> {
            AlertDialog(
                title = {
                    Text(text = stringResource(R.string.ly_img_editor_version_details_error_dialog_title))
                },
                text = {
                    Text(text = stringResource(R.string.ly_img_editor_version_details_error_dialog_description))
                },
                confirmButton = {
                    TextButton(
                        onClick = { onClearProgress() },
                    ) {
                        Text(stringResource(R.string.ly_img_editor_version_details_dismiss))
                    }
                },
                onDismissRequest = { },
                properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
            )
        }
    }
}
