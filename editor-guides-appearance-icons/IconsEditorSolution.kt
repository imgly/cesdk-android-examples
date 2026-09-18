import android.widget.Toast
import androidx.compose.runtime.Composable
import ly.img.editor.Editor
import ly.img.editor.core.component.CanvasMenu
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberImagesLibrary
import ly.img.editor.core.component.rememberTextLibrary
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.Replace
import ly.img.editor.guides.icons.BrandIcons
import ly.img.editor.guides.icons.brandicons.BrandSpark
import ly.img.engine.DesignBlockType
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import ly.img.engine.Color as EngineColor

// Add this composable to your NavHost
@Composable
fun IconsEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license,
        configuration = {
            EditorConfiguration.remember {
                onCreate = {
                    if (editorContext.engine.scene.get() == null) {
                        val scene = editorContext.engine.scene.create()
                        val page = editorContext.engine.block.create(DesignBlockType.Page)
                        editorContext.engine.block.setWidth(page, value = 1080F)
                        editorContext.engine.block.setHeight(page, value = 1080F)
                        editorContext.engine.block.appendChild(parent = scene, child = page)

                        val block = editorContext.engine.block.create(DesignBlockType.Graphic)
                        editorContext.engine.block.setShape(
                            block,
                            shape = editorContext.engine.block.createShape(ShapeType.Rect),
                        )
                        val fill = editorContext.engine.block.createFill(FillType.Color)
                        editorContext.engine.block.setFill(block, fill = fill)
                        editorContext.engine.block.setFillSolidColor(
                            block = block,
                            color = EngineColor.fromRGBA(r = 0.86F, g = 0.9F, b = 1F, a = 1F),
                        )
                        editorContext.engine.block.setWidth(block, value = 420F)
                        editorContext.engine.block.setHeight(block, value = 420F)
                        editorContext.engine.block.setPositionX(block, value = 330F)
                        editorContext.engine.block.setPositionY(block, value = 330F)
                        editorContext.engine.block.appendChild(parent = page, child = block)
                        editorContext.engine.scene.zoomToBlock(page)
                        editorContext.engine.block.setSelected(block, selected = true)
                    }
                }
                dock = {
                    Dock.remember {
                        listBuilder = {
                            Dock.ListBuilder.remember {
                                add {
                                    // highlight-android-replace-dock-icon
                                    Dock.Button.rememberImagesLibrary {
                                        vectorIcon = { BrandIcons.BrandSpark }
                                    }
                                    // highlight-android-replace-dock-icon
                                }
                                add { Dock.Button.rememberTextLibrary() }
                            }
                        }
                    }
                }
                canvasMenu = {
                    CanvasMenu.remember {
                        listBuilder = {
                            CanvasMenu.ListBuilder.remember {
                                add {
                                    // highlight-android-custom-canvas-menu-icon
                                    CanvasMenu.Button.remember {
                                        id = { EditorComponentId("com.example.guides.icons.canvasMenu.brandAction") }
                                        vectorIcon = { BrandIcons.BrandSpark }
                                        contentDescription = { "Show brand action" }
                                        onClick = {
                                            Toast.makeText(
                                                editorContext.activity,
                                                "Brand action",
                                                Toast.LENGTH_SHORT,
                                            ).show()
                                        }
                                    }
                                    // highlight-android-custom-canvas-menu-icon
                                }
                                add {
                                    // highlight-android-iconpack-component
                                    CanvasMenu.Button.remember {
                                        id = { EditorComponentId("com.example.guides.icons.canvasMenu.replace") }
                                        vectorIcon = { IconPack.Replace }
                                        contentDescription = { "Replace selection" }
                                        onClick = {
                                            Toast.makeText(
                                                editorContext.activity,
                                                "Replace selection",
                                                Toast.LENGTH_SHORT,
                                            ).show()
                                        }
                                    }
                                    // highlight-android-iconpack-component
                                }
                            }
                        }
                    }
                }
            }
        },
        onClose = onClose,
    )
}
