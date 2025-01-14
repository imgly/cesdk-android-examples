package ly.img.editor.showcase

import android.app.UiModeManager
import android.app.UiModeManager.MODE_NIGHT_YES
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.SurfaceView
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.IdlingPolicies
import androidx.test.platform.app.InstrumentationRegistry
import com.dropbox.dropshots.Dropshots
import ly.img.editor.base.R
import ly.img.engine.Engine
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

// showcases app is deleted after test run. Issue appeared in AGP 8.1.0 https://issuetracker.google.com/issues/295039976
@OptIn(ExperimentalTestApi::class)
class ShowcasesTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule(ShowcaseActivity::class.java)

    @get:Rule
    val dropshots = Dropshots()

    init {
        Engine.`$disableInternalDeployMode` = true
    }

    @Before
    fun before() {
        composeTestRule.runOnUiThread {
            // We should force the light/dark mode to get correct screenshot tests.
            InstrumentationRegistry
                .getInstrumentation()
                .context
                .getSystemService(Context.UI_MODE_SERVICE)
                .let { it as UiModeManager }
                .setApplicationNightMode(MODE_NIGHT_YES)
        }
    }

    @Test
    fun snapApparelUi() {
        composeTestRule
            .onNodeWithText(text = "Apparel UI")
            .performClick()

        composeTestRule.waitUntilDoesNotExist(
            hasTestTag(testTag = "MainLoading"),
            timeoutMillis = 10_000,
        )

        composeTestRule
            .onNodeWithTag(testTag = "LibraryButton")
            .performClick()

        composeTestRule
            .onNodeWithTag(testTag = "LibraryNavigationBar")
            .onChildAt(0)
            .onChildAt(1) // Images tab
            .performClick()

        composeTestRule
            .waitUntilExactlyOneExists(hasTestTag(testTag = "LibrarySectionColumn"))

        composeTestRule
            .onNodeWithTag(
                testTag = "LibraryImageCard/assets/demo/v2/ly.img.image/thumbnails/sample_1.jpg",
            ).performClick()

        composeTestRule.waitForIdle()

        dropshots.assertSnapshot(bitmap = capture(), name = "apparel_added_image")
    }

    @Test
    @Ignore("This test is unreliable when run on an emulator that uses OpenGL emulation on a CPU.")
    fun snapVideoUi() {
        IdlingPolicies.setIdlingResourceTimeout(5, TimeUnit.MINUTES)
        IdlingPolicies.setMasterPolicyTimeout(5, TimeUnit.MINUTES)
        loadVideoUi()

        dropshots.assertSnapshot(bitmap = capture(), name = "video_loaded")
    }

    @Test
    @Ignore("This test taskes to long to run.")
    fun snapVideoUiExportSuccess() {
        IdlingPolicies.setIdlingResourceTimeout(5, TimeUnit.MINUTES)
        IdlingPolicies.setMasterPolicyTimeout(5, TimeUnit.MINUTES)
        loadVideoUi()

        composeTestRule
            .onNodeWithContentDescription("Export")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.waitUntilExactlyOneExists(hasText("Export Complete"), 60_000)

        composeTestRule.waitForIdle()

        dropshots.assertSnapshot(bitmap = capture(), name = "video_export_success")
    }

    private fun loadVideoUi() {
        IdlingPolicies.setIdlingResourceTimeout(5, TimeUnit.MINUTES)
        IdlingPolicies.setMasterPolicyTimeout(5, TimeUnit.MINUTES)
        composeTestRule
            .onNodeWithText(text = "Video UI")
            .performClick()

        composeTestRule.waitUntilDoesNotExist(
            hasTestTag(testTag = "MainLoading"),
            timeoutMillis = 10_000,
        )

        composeTestRule.waitUntilExactlyOneExists(
            hasContentDescription("Play"),
            timeoutMillis = 40_000,
        )

        composeTestRule.waitForIdle()
    }

    private fun capture(): Bitmap {
        val activity = composeTestRule.activity
        val lock = CountDownLatch(2)
        val (windowBitmap, windowLocation) =
            activity.window.decorView.rootView.run {
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val location = IntArray(2)
                getLocationInWindow(location)
                val rect = Rect(location[0], location[1], location[0] + width, location[1] + height)
                PixelCopy.request(activity.window, rect, bitmap, {
                    lock.countDown()
                }, Handler(Looper.getMainLooper()))
                bitmap to location
            }
        val (canvasBitmap, canvasLocation) =
            activity
                .findViewById<SurfaceView>(
                    R.id.editor_render_view,
                ).run {
                    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    val location = IntArray(2)
                    getLocationInWindow(location)
                    PixelCopy.request(this, bitmap, {
                        lock.countDown()
                    }, Handler(Looper.getMainLooper()))
                    bitmap to location
                }
        lock.await()
        val jointBitmap =
            Bitmap.createBitmap(
                windowBitmap.width,
                windowBitmap.height,
                Bitmap.Config.ARGB_8888,
            )
        val canvas = Canvas(jointBitmap)
        canvas.drawBitmap(canvasBitmap, 0F, (canvasLocation[1] - windowLocation[1]).toFloat(), null)
        canvas.drawBitmap(windowBitmap, 0F, 0F, null)
        return jointBitmap
    }
}
