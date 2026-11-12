import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.img.editor.Editor
import ly.img.editor.configuration.video.VideoConfigurationBuilder
import ly.img.editor.configuration.video.callback.onCreate
import ly.img.editor.core.component.InspectorBar
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.configuration.then
import ly.img.editor.plugin.voiceIsolation.IMGLYVoiceIsolationConfig
import ly.img.editor.plugin.voiceIsolation.IMGLYVoiceIsolationPlugin
import ly.img.editor.plugin.voiceIsolation.IMGLYVoiceIsolator
import ly.img.editor.plugin.voiceIsolation.VoiceIsolationConfig
import ly.img.editor.plugin.voiceIsolation.VoiceIsolationException
import ly.img.editor.plugin.voiceIsolation.VoiceIsolationPlugin
import ly.img.editor.plugin.voiceIsolation.VoiceIsolator
import ly.img.editor.plugin.voiceIsolation.canIsolateVoice
import ly.img.editor.plugin.voiceIsolation.isolateVoice
import ly.img.editor.plugin.voiceIsolation.rememberVoiceIsolation
import ly.img.engine.BlockState
import ly.img.engine.DesignBlockType
import okhttp3.OkHttpClient
import java.io.File
import java.util.UUID
import java.util.concurrent.TimeUnit

// Add this composable to your NavHost.
// The demo opens a sample video, so that the timeline has a clip with audio for the "Isolate Voice" button. The guide
// shows the minimal setup in VoiceIsolationEditorSolutionMinimal.
@Composable
fun VoiceIsolationEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license,
        configuration = {
            EditorConfiguration
                .remember(::VideoConfigurationBuilder) {
                    onCreate = {
                        onCreate(
                            createScene = {
                                editorContext.engine.scene.createFromVideo(
                                    videoUri = editorContext.baseUri
                                        .buildUpon()
                                        .appendPath("ly.img.video")
                                        .appendPath("videos")
                                        .appendPath("pexels-kampus-production-8154913.mp4")
                                        .build(),
                                )
                            },
                        )
                    }
                }
                .then(::IMGLYVoiceIsolationPlugin)
        },
        onClose = onClose,
    )
}

@Composable
private fun VoiceIsolationEditorSolutionMinimal(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    // highlight-android-minimal
    Editor(
        license = license,
        configuration = {
            EditorConfiguration
                .remember(::VideoConfigurationBuilder)
                .then(::IMGLYVoiceIsolationPlugin)
        },
        onClose = onClose,
    )
    // highlight-android-minimal
}

@Composable
private fun VoiceIsolationEditorSolutionWithConfiguration(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license,
        configuration = {
            EditorConfiguration
                .remember(::VideoConfigurationBuilder)
                // highlight-android-configuration
                .then(::IMGLYVoiceIsolationPlugin) {
                    config = IMGLYVoiceIsolationConfig(
                        // The default model location and timeouts. Replace them with your server and your timeouts.
                        modelBaseUri = "https://staticimgly.com/imgly/plugin-mobile-voice-isolation/1.0.0".toUri(),
                        loadMode = IMGLYVoiceIsolationConfig.LoadMode.LAZY,
                        httpClient = OkHttpClient.Builder()
                            .connectTimeout(15, TimeUnit.SECONDS)
                            .readTimeout(120, TimeUnit.SECONDS)
                            .writeTimeout(120, TimeUnit.SECONDS)
                            .build(),
                    )
                    inspectorBarModifier = {
                        addLast { InspectorBar.Button.rememberVoiceIsolation(config = it) }
                    }
                }
            // highlight-android-configuration
        },
        onClose = onClose,
    )
}

@Composable
private fun VoiceIsolationEditorSolutionWithBlockCall(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license,
        configuration = {
            // highlight-android-block-call
            val voiceIsolationConfig = remember { IMGLYVoiceIsolationConfig() }
            EditorConfiguration
                .remember(::VideoConfigurationBuilder) {
                    onCreate = {
                        onCreate(
                            createScene = {
                                val engine = editorContext.engine
                                engine.scene.createFromVideo(
                                    videoUri = editorContext.baseUri
                                        .buildUpon()
                                        .appendPath("ly.img.video")
                                        .appendPath("videos")
                                        .appendPath("pexels-kampus-production-8154913.mp4")
                                        .build(),
                                )
                                val clip = engine.block.findByType(DesignBlockType.Graphic)
                                    .firstOrNull { canIsolateVoice(block = it, engine = engine) }
                                if (clip != null) {
                                    // A separate coroutine, so that the editor opens while the model cleans the clip.
                                    editorContext.coroutineScope.launch {
                                        // The call shows no loading state, so the app shows it. The user can delete the
                                        // clip during the call.
                                        engine.block.setState(clip, BlockState.Pending(progress = 0F))
                                        try {
                                            val cleanedClip = isolateVoice(
                                                block = clip,
                                                engine = engine,
                                                config = voiceIsolationConfig,
                                                context = editorContext.activity,
                                            )
                                            if (engine.block.isValid(clip)) {
                                                engine.block.setState(clip, BlockState.Ready)
                                            }
                                            // Lets the user undo the cleaning as a step of its own.
                                            engine.editor.addUndoStep()
                                            Log.i("VoiceIsolation", "Cleaned the audio of clip $cleanedClip.")
                                        } catch (exception: VoiceIsolationException) {
                                            if (engine.block.isValid(clip)) {
                                                engine.block.setState(clip, BlockState.Ready)
                                            }
                                            Log.e("VoiceIsolation", "Could not isolate the voice.", exception)
                                        }
                                    }
                                }
                            },
                        )
                    }
                }
                .then(::IMGLYVoiceIsolationPlugin) {
                    config = voiceIsolationConfig
                }
            // highlight-android-block-call
        },
        onClose = onClose,
    )
}

// highlight-android-custom-isolator
private class CustomVoiceIsolator : VoiceIsolator {
    override suspend fun prewarm(context: Context) {
        // Download or load your model here. The plugin calls this when the editor opens.
    }

    override suspend fun isolateVoice(
        context: Context,
        input: File,
        output: File,
    ) {
        // The plugin can call this on the main thread, so the file work runs on the IO dispatcher.
        withContext(Dispatchers.IO) {
            // Replace this copy with your model or service. Write a WAV file with the format and length of input.
            input.copyTo(output, overwrite = true)
        }
    }
}

private data class CustomVoiceIsolationConfig(
    override val isolator: VoiceIsolator = CustomVoiceIsolator(),
    override val httpClient: OkHttpClient = OkHttpClient(),
) : VoiceIsolationConfig
// highlight-android-custom-isolator

@Composable
private fun VoiceIsolationEditorSolutionWithCustomIsolator(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license,
        configuration = {
            EditorConfiguration
                .remember(::VideoConfigurationBuilder)
                // highlight-android-custom-plugin
                .then(::VoiceIsolationPlugin) {
                    config = CustomVoiceIsolationConfig()
                }
            // highlight-android-custom-plugin
        },
        onClose = onClose,
    )
}

// highlight-android-chained-isolator
private class ChainedVoiceIsolator(
    private val imglyIsolator: IMGLYVoiceIsolator = IMGLYVoiceIsolator(IMGLYVoiceIsolationConfig()),
) : VoiceIsolator {
    override suspend fun prewarm(context: Context) {
        imglyIsolator.prewarm(context)
    }

    override suspend fun isolateVoice(
        context: Context,
        input: File,
        output: File,
    ) {
        val firstPass = File(context.cacheDir, "${UUID.randomUUID()}.wav")
        try {
            withContext(Dispatchers.IO) {
                // Replace this copy with your model. Write a WAV file with the format and length of input.
                input.copyTo(firstPass)
            }
            imglyIsolator.isolateVoice(context = context, input = firstPass, output = output)
        } finally {
            firstPass.delete()
        }
    }
}
// highlight-android-chained-isolator

@Composable
private fun VoiceIsolationEditorSolutionWithChainedIsolator(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license,
        configuration = {
            EditorConfiguration
                .remember(::VideoConfigurationBuilder)
                // highlight-android-chained-plugin
                .then(::VoiceIsolationPlugin) {
                    config = CustomVoiceIsolationConfig(isolator = ChainedVoiceIsolator())
                }
            // highlight-android-chained-plugin
        },
        onClose = onClose,
    )
}
