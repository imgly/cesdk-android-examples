package ly.img.editor.version.details

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.appdistribution.FirebaseAppDistribution
import com.google.firebase.installations.FirebaseInstallations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.img.editor.version.details.entity.Progress
import ly.img.editor.version.details.entity.Release
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.coroutines.cancellation.CancellationException

internal class VersionUpdateViewModel : ViewModel() {
    private val _errorChannel = Channel<String>()
    val errorState =
        _errorChannel
            .receiveAsFlow()

    private val _showSignInButtonState = MutableStateFlow(FirebaseAppDistribution.getInstance().isTesterSignedIn.not())
    val showSignInButtonState: StateFlow<Boolean> = _showSignInButtonState

    private val _updateRequestState = MutableStateFlow<Release?>(null)
    val updateRequestState: StateFlow<Release?> = _updateRequestState

    private val _updateProgressState = MutableStateFlow<Progress?>(null)
    val updateProgressState: StateFlow<Progress?> = _updateProgressState

    private var downloadJob: Job? = null

    private val httpClientClass by lazy {
        Class.forName(CLASS_NAME_HTTP_CLIENT)
    }

    private val httpClientConstructor by lazy {
        httpClientClass.declaredConstructors[0].also {
            it.isAccessible = true
        }
    }

    private val makeGetRequestMethod by lazy {
        httpClientClass.getDeclaredMethod(
            METHOD_NAME_MAKE_GET_REQUEST,
            String::class.java,
            String::class.java,
            String::class.java,
        ).also {
            it.isAccessible = true
        }
    }

    private val connectionFactoryConstructor by lazy {
        val connectionFactoryClass = Class.forName(CLASS_NAME_CONNECTION_FACTORY)
        connectionFactoryClass.declaredConstructors[0].also {
            it.isAccessible = true
        }
    }

    fun signIn() {
        FirebaseAppDistribution.getInstance()
            .signInTester()
            .addOnSuccessListener { _showSignInButtonState.value = false }
            .addOnFailureListener { _errorChannel.trySend("Cannot sign in to Firebase App Distribution. Please try later.") }
    }

    fun checkForNewRelease(
        activity: Activity,
        branchName: String,
        versionCode: Int,
    ) {
        Tasks.whenAllSuccess<String>(
            FirebaseInstallations.getInstance().id,
            FirebaseInstallations.getInstance()
                .getToken(false)
                .onSuccessTask { Tasks.forResult(it.token) },
        ).addOnSuccessListener(Dispatchers.IO.asExecutor()) { (fid, token) ->
            checkForNewRelease(
                activity = activity,
                branchName = branchName,
                versionCode = versionCode,
                fid = fid,
                token = token,
            )
        }
    }

    private fun checkForNewRelease(
        activity: Activity,
        branchName: String,
        versionCode: Int,
        fid: String,
        token: String,
    ) = viewModelScope.launch(Dispatchers.IO) {
        runCatching {
            val firebaseOptions = FirebaseApp.getInstance().options
            val apiClient =
                httpClientConstructor.newInstance(
                    activity,
                    firebaseOptions,
                    connectionFactoryConstructor.newInstance(),
                )
            val responseJson =
                makeGetRequestMethod.invoke(
                    apiClient,
                    GET_RELEASES_TAG,
                    GET_RELEASES_PATH.format(firebaseOptions.applicationId, fid),
                    token,
                ) as JSONObject
            val releasesJson = responseJson.getJSONArray(JSON_KEY_RELEASES)
            repeat(releasesJson.length()) { json ->
                val release = releasesJson.getJSONObject(json)
                val releaseNotes =
                    release.optString(JSON_KEY_RELEASE_NOTES, "")
                        .takeIf { it.isNotEmpty() } ?: return@repeat
                val buildVersion =
                    release.optString(JSON_KEY_BUILD_VERSION, "")
                        .let { runCatching { it.toLong() } }
                        .getOrNull() ?: return@repeat
                val downloadUrl =
                    release.optString(JSON_KEY_DOWNLOAD_URL, "")
                        .takeIf { it.isNotEmpty() } ?: return@repeat
                if (releaseNotes.trim().equals(branchName.trim(), ignoreCase = true) && buildVersion > versionCode) {
                    _updateRequestState.value =
                        Release(
                            releaseNotes = releaseNotes,
                            buildVersion = buildVersion,
                            downloadUrl = downloadUrl,
                        )
                    return@launch
                }
            }
            _updateRequestState.value = Release.UpToDate
        }
    }

    fun update(
        context: Context,
        release: Release,
    ) = viewModelScope.launch {
        runCatching {
            _updateProgressState.value = Progress.Pending(0F)
            val apkFile =
                withContext(Dispatchers.IO) {
                    val connection = URL(release.downloadUrl).openConnection() as HttpsURLConnection
                    connection.requestMethod = "GET"
                    val responseCode = connection.responseCode
                    require(responseCode in 200 until 300)
                    val responseLength = connection.contentLength.toLong()
                    val fileName = "${release.buildVersion}.apk"
                    context.deleteFile(fileName)
                    connection.inputStream.use { inputStream ->
                        context.openFileOutput(fileName, Context.MODE_PRIVATE).use { outputStream ->
                            var totalBytesRead = 0L
                            val byteArray = ByteArray(DEFAULT_BUFFER_SIZE)
                            while (true) {
                                val readBytes = inputStream.read(byteArray)
                                if (readBytes == -1) break
                                totalBytesRead += readBytes
                                outputStream.write(byteArray, 0, readBytes)
                                val progress = totalBytesRead.toFloat() / responseLength
                                ensureActive()
                                withContext(Dispatchers.Main) {
                                    _updateProgressState.value = Progress.Pending(progress)
                                }
                            }
                        }
                    }
                    context.getFileStreamPath(fileName)
                }
            _updateProgressState.value = Progress.Ready(apkFile)
        }.onFailure {
            if (it !is CancellationException) {
                _updateProgressState.value = Progress.Error(it)
            }
        }
    }.also {
        downloadJob = it
    }

    fun cancelActive() {
        downloadJob?.cancel()
        downloadJob = null
        _updateProgressState.value = null
    }

    companion object {
        private const val CLASS_NAME_HTTP_CLIENT = "com.google.firebase.appdistribution.impl.TesterApiHttpClient"
        private const val CLASS_NAME_CONNECTION_FACTORY = "com.google.firebase.appdistribution.impl.HttpsUrlConnectionFactory"
        private const val METHOD_NAME_MAKE_GET_REQUEST = "makeGetRequest"

        private const val GET_RELEASES_TAG = "Fetching new release"
        private const val GET_RELEASES_PATH = "v1alpha/devices/-/testerApps/%s/installations/%s/releases"

        private const val JSON_KEY_RELEASES = "releases"
        private const val JSON_KEY_RELEASE_NOTES = "releaseNotes"
        private const val JSON_KEY_BUILD_VERSION = "buildVersion"
        private const val JSON_KEY_DOWNLOAD_URL = "downloadUrl"

        const val EXTRA_APK_INSTALL_PATH = "INSTALL_PATH"
    }
}
