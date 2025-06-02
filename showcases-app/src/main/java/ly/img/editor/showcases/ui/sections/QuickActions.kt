package ly.img.editor.showcases.ui.sections

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
import androidx.navigation.NavHostController
import ly.img.camera.core.CameraMode
import ly.img.camera.core.CameraResult
import ly.img.camera.core.CaptureVideo
import ly.img.camera.core.EngineConfiguration
import ly.img.editor.showcases.R
import ly.img.editor.showcases.Screen
import ly.img.editor.showcases.Secrets
import ly.img.editor.showcases.ui.components.QuickActionButton

fun LazyListScope.quickActionsSection(navController: NavHostController) {
    item {
        QuickActions(navController = navController)
    }
}

@Composable
fun QuickActions(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    val cameraLauncher = rememberLauncherForActivityResult(contract = CaptureVideo()) { result ->
        result ?: run { return@rememberLauncherForActivityResult }
        when (result) {
            is CameraResult.Record -> {
                navController.currentBackStackEntry?.savedStateHandle?.set("recording", result)
                navController.navigate(Screen.EditCameraRecordings.getRoute())
            }

            is CameraResult.Reaction -> {
                navController.currentBackStackEntry?.savedStateHandle?.set("reaction", result)
                navController.navigate(Screen.EditRecordedReaction.getRoute())
            }

            else -> {}
        }
    }

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(11.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        item {
            QuickActionButton(
                title = stringResource(id = R.string.quick_action_record_video),
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
                title = stringResource(id = R.string.quick_action_react_to_video),
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
                            navController.navigate(
                                Screen.PhotoUi.getRoute(
                                    "image" to uri,
                                ),
                            )
                        }

                        type.startsWith("video") -> {
                            navController.currentBackStackEntry?.savedStateHandle?.set("videoUri", uri)
                            navController.navigate(Screen.EditVideoFromUri.getRoute())
                        }

                        else -> {}
                    }
                }
            }

            QuickActionButton(
                title = stringResource(id = R.string.quick_action_pick_from_gallery),
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
