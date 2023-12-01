package ly.img.cesdk.showcase

import colors
import createSceneFromImageBlob
import createSceneFromImageURL
import createSceneFromScratch
import createSceneFromVideoURL
import customAssetSource
import cutouts
import editVideo
import exportingBlocks
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import loadSceneFromBlob
import loadSceneFromRemote
import loadSceneFromString
import ly.img.cesdk.core.Secrets
import modifyingScenes
import org.junit.Test
import saveSceneToArchive
import saveSceneToBlob
import saveSceneToString
import spotColors
import storeMetadata
import textProperties
import textWithEmojis
import uriResolver
import usingEffects
import usingFills
import usingShapes

class GuidesTest {

    @Test
    fun testColors() = runGuide(::colors)

    @Test
    fun testCreateSceneFromImageBlob() = runGuide(::createSceneFromImageBlob)

    @Test
    fun testCreateSceneFromImageURL() = runGuide(::createSceneFromImageURL)
    @Test
    fun testCreateSceneFromScratch() = runGuide(::createSceneFromScratch)

    @Test
    fun testCreateSceneFromVideoURL() = runGuide(::createSceneFromVideoURL)

    @Test
    fun testCustomAssetSource() = runGuide { license, userId ->
        customAssetSource(license = license, userId = userId, unsplashBaseUrl = "")
    }

    @Test
    fun testCutouts() = runGuide(::cutouts)

    @Test
    fun testExportingBlocks() = runGuide(::exportingBlocks)

    @Test
    fun testLoadSceneFromBlob() = runGuide(::loadSceneFromBlob)

    @Test
    fun testLoadSceneFromRemote() = runGuide(::loadSceneFromRemote)

    @Test
    fun testLoadSceneFromString() = runGuide(::loadSceneFromString)

    @Test
    fun testModifyingScenes() = runGuide(::modifyingScenes)

    @Test
    fun testSaveSceneToArchive() = runGuide(::saveSceneToArchive)

    @Test
    fun testSaveSceneToBlob() = runGuide { license, userId ->
        saveSceneToBlob(license = license, userId = userId, uploadUrl = "")
    }

    @Test
    fun testSaveSceneToString() = runGuide(::saveSceneToString)

    @Test
    fun testSpotColors() = runGuide(::spotColors)

    @Test
    fun testStoreMetadata() = runGuide(::storeMetadata)

    @Test
    fun testTextProperties() = runGuide(::textProperties)

    @Test
    fun testTextWithEmojis() = runGuide(::textWithEmojis)

    @Test
    fun testUriResolver() = runGuide(::uriResolver)

    @Test
    fun testUsingEffects() = runGuide(::usingEffects)

    @Test
    fun testUsingFills() = runGuide(::usingFills)

    @Test
    fun testUsingShapes() = runGuide(::usingShapes)

    @Test
    fun testEditVideo() = runGuide(::editVideo)

    private fun runGuide(block: (String, String) -> Job) = runBlocking {
        block(Secrets.license, "userId").join()
    }
}
