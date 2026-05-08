import android.app.Activity
import android.view.SurfaceView
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.compose.ui.viewinterop.AndroidView
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.SceneLayout
import ly.img.engine.ShapeType

@Composable
fun ModifyingScenesEngineSolution(
    license: String,
    onClose: () -> Unit,
) {
    val activity = LocalContext.current as Activity
    val engine = remember(activity.application) {
        Engine.init(activity.application)
        Engine.getInstance(id = "ly.img.engine.examples.scenes")
    }
    val surfaceView = remember { SurfaceView(activity) }
    val savedStateRegistryOwner = LocalSavedStateRegistryOwner.current

    BackHandler(onBack = onClose)
    AndroidView(factory = { surfaceView })

    LaunchedEffect(Unit) {
        engine.start(
            license = license,
            userId = "examples-scenes",
            savedStateRegistryOwner = savedStateRegistryOwner,
        )
        engine.bindSurfaceView(surfaceView)

        val scene = engine.scene.create(sceneLayout = SceneLayout.VERTICAL_STACK)
        val page = engine.block.create(DesignBlockType.Page)
        engine.block.setWidth(page, value = 800F)
        engine.block.setHeight(page, value = 600F)
        engine.block.appendChild(parent = scene, child = page)

        val block = engine.block.create(DesignBlockType.Graphic)
        val shape = engine.block.createShape(ShapeType.Rect)
        engine.block.setShape(block, shape = shape)
        val fill = engine.block.createFill(FillType.Color)
        engine.block.setFill(block, fill = fill)
        engine.block.setColor(
            fill,
            property = "fill/color/value",
            value = Color.fromRGBA(r = 0.11F, g = 0.61F, b = 0.35F, a = 1F),
        )
        engine.block.setPositionX(block, value = 300F)
        engine.block.setPositionY(block, value = 200F)
        engine.block.setWidth(block, value = 200F)
        engine.block.setHeight(block, value = 200F)
        engine.block.appendChild(parent = page, child = block)

        engine.scene.zoomToBlock(
            block = page,
            paddingLeft = 20F,
            paddingTop = 20F,
            paddingRight = 20F,
            paddingBottom = 20F,
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            if (engine.isEngineRunning()) {
                if (activity.isChangingConfigurations) {
                    engine.unbind()
                } else {
                    engine.stop()
                }
            }
        }
    }
}
