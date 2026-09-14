package ly.img.editor.showcases.ui.section

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.camera.core.CameraMode
import ly.img.camera.core.CameraResult
import ly.img.camera.core.CaptureVideo
import ly.img.camera.core.EngineConfiguration
import ly.img.editor.showcases.R
import ly.img.editor.showcases.Screen
import ly.img.editor.showcases.Secrets
import ly.img.editor.showcases.ui.component.QuickActionButton

fun LazyListScope.quickActionsSection(
    onResult: (String, Any?) -> Unit,
    navigateTo: (String) -> Unit,
) {
    item {
        QuickActions(
            onResult = onResult,
            navigateTo = navigateTo,
        )
    }
}

@Composable
fun QuickActions(
    modifier: Modifier = Modifier,
    onResult: (String, Any?) -> Unit,
    navigateTo: (String) -> Unit,
) {
    val cameraLauncher = rememberLauncherForActivityResult(contract = CaptureVideo()) { result ->
        result ?: run { return@rememberLauncherForActivityResult }
        when (result) {
            is CameraResult.Record -> {
                onResult("recording", result)
                navigateTo(Screen.EditCameraRecordings.getRoute())
            }

            is CameraResult.Reaction -> {
                onResult("reaction", result)
                navigateTo(Screen.EditRecordedReaction.getRoute())
            }
        }
    }

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(11.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        item {
            QuickActionButton(
                title = stringResource(id = R.string.ly_img_showcases_button_quick_action_record_video),
                iconId = R.drawable.quick_action_record_video,
                onClick = {
                    val cameraInput = CaptureVideo.Input(
                        engineConfiguration = EngineConfiguration(license = Secrets.license),
                    )
                    cameraLauncher.launch(cameraInput)
                },
            )
        }

        item {
            val videoPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { videoUri ->
                videoUri?.let { uri ->
                    val cameraInput = CaptureVideo.Input(
                        engineConfiguration = EngineConfiguration(license = Secrets.license),
                        cameraMode = CameraMode.Reaction(video = uri),
                    )
                    cameraLauncher.launch(cameraInput)
                }
            }

            QuickActionButton(
                title = stringResource(id = R.string.ly_img_showcases_button_quick_action_react_to_video),
                iconId = R.drawable.quick_action_react_to_video,
                onClick = {
                    videoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
                },
            )
        }

        item {
            val context = LocalContext.current
            val mediaPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { mediaUri ->
                mediaUri?.let { uri ->
                    val type = context.contentResolver.getType(uri) ?: ""
                    when {
                        type.startsWith("image") -> {
                            navigateTo(
                                Screen.PhotoUi.getRoute(
                                    "image" to uri,
                                ),
                            )
                        }

                        type.startsWith("video") -> {
                            onResult("videoUri", uri)
                            navigateTo(Screen.EditVideoFromUri.getRoute())
                        }

                        else -> {}
                    }
                }
            }

            QuickActionButton(
                title = stringResource(id = R.string.ly_img_showcases_button_quick_action_pick_from_gallery),
                iconId = R.drawable.quick_action_edit_media,
                onClick = {
                    mediaPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
                },
            )
        }

        // Will be added in an update
        //
        // item {
        //     QuickActionButton(
        //         title = stringResource(id = R.string.quick_action_dual_camera),
        //         iconId = R.drawable.quick_action_dual_camera,
        //         onClick = {
        //
        //         }
        //     )
        // }
    }
}
