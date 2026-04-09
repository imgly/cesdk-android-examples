@file:OptIn(UnstableEditorApi::class)
@file:Suppress("UnusedReceiverParameter", "UnusedFlow")

package ly.img.editor.configuration.postcard.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import ly.img.editor.configuration.postcard.PostcardConfigurationBuilder
import ly.img.editor.configuration.postcard.ext.getCurrentPageIndex
import ly.img.editor.configuration.postcard.ext.getFill
import ly.img.editor.configuration.postcard.ext.getStrokeColor
import ly.img.editor.core.EditorContext
import ly.img.editor.core.EditorScope
import ly.img.editor.core.R
import ly.img.editor.core.ScopedProperty
import ly.img.editor.core.UnstableEditorApi
import ly.img.editor.core.component.AbstractButtonBuilder
import ly.img.editor.core.component.Button
import ly.img.editor.core.component.DefaultDecoration
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.EditorTrigger
import ly.img.editor.core.component.data.EditorIcon
import ly.img.editor.core.component.data.GradientFill
import ly.img.editor.core.component.data.SolidFill
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberAssetLibrary
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.SizeLCircled
import ly.img.editor.core.iconpack.SizeMCircled
import ly.img.editor.core.iconpack.SizeSCircled
import ly.img.editor.core.iconpack.Typeface
import ly.img.editor.core.sheet.SheetType
import ly.img.editor.core.state.EditorViewMode
import ly.img.editor.core.ui.EditorIcon
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import java.util.TreeMap

/**
 * The configuration of the component that is displayed as horizontal list of items at the bottom of the editor.
 */
@Composable
fun PostcardConfigurationBuilder.rememberDock() = Dock.remember {
    scope = {
        val activeSceneTrigger by EditorTrigger.remember {
            editorContext.engine.scene.onActiveChanged()
        }
        val historyTrigger by EditorTrigger.remember(activeSceneTrigger) {
            editorContext.engine.editor.onHistoryUpdated()
        }
        val pageIndex by remember(this, activeSceneTrigger) {
            if (editorContext.engine.scene.get() == null) return@remember emptyFlow()
            val stack = editorContext.engine.block.findByType(DesignBlockType.Stack).first()
            editorContext.engine.event.subscribe(listOf(stack))
                .map { editorContext.engine.getCurrentPageIndex() }
        }.collectAsState(initial = 0)
        // Update Dock whenever the active scene, editor history or current page index changes.
        remember(this, pageIndex, historyTrigger) {
            Dock.Scope(parentScope = this)
        }
    }
    visible = {
        val state by editorContext.state.collectAsState()
        // Hide Dock in preview mode
        state.viewMode !is EditorViewMode.Preview
    }
    decoration = {
        // Show transparent background
        Dock.DefaultDecoration(
            background = Color.Transparent,
        ) { it() }
    }
    horizontalArrangement = {
        remember(this) {
            val isFirstPage = editorContext.engine.scene.get() != null &&
                editorContext.engine.getCurrentPageIndex() == 0
            // Show Dock start aligned on the first page and centered on the second page
            if (isFirstPage) Arrangement.Start else Arrangement.Center
        }
    }
    listBuilder = {
        Dock.ListBuilder.remember {
            add {
                Dock.Button.rememberAssetLibrary {
                    visible = {
                        remember(this) {
                            editorContext.engine.getCurrentPageIndex() == 0
                        }
                    }
                }
            }
            // Show divider only on the first page.
            if (editorContext.engine.getCurrentPageIndex() == 0) {
                add { Dock.Divider.remember() }
            }
            add { Dock.Button.rememberGreetingFont() }
            add { Dock.Button.rememberGreetingSize() }
            add { Dock.Button.rememberColors() }
        }
    }
}

/**
 * Greeting font button at the dock.
 */
@Composable
fun Dock.Button.rememberGreetingFont() = Dock.Button.remember {
    id = { EditorComponentId("ly.img.component.dock.button.greetingFont") }
    visible = {
        remember(this) {
            editorContext.engine.scene.run { getPages().indexOf(getCurrentPage()) == 1 }
        }
    }
    vectorIcon = { IconPack.Typeface }
    textString = { stringResource(R.string.ly_img_editor_dock_button_font) }
    onClick = {
        val designBlock = editorContext.engine.block.findByName("Greeting").first()
        editorContext.eventHandler.send(
            EditorEvent.Sheet.Open(SheetType.Font(designBlock = designBlock)),
        )
    }
}

/**
 * Greeting size button of the dock.
 */
@Composable
fun Dock.Button.rememberGreetingSize() = Dock.Button.remember {
    id = { EditorComponentId("ly.img.component.dock.button.greetingSize") }
    visible = {
        remember(this) {
            editorContext.engine.scene.run { getPages().indexOf(getCurrentPage()) == 1 }
        }
    }
    vectorIcon = {
        remember(this) {
            val block = editorContext.engine.block.findByName("Greeting").firstOrNull()
            requireNotNull(block) { "Page must have design block with name \"Greeting\"." }
            val size = editorContext.engine.block.getFloat(block, "text/fontSize")
            when {
                size <= 14F -> IconPack.SizeSCircled
                size > 18F -> IconPack.SizeLCircled
                else -> IconPack.SizeMCircled
            }
        }
    }
    textString = { stringResource(R.string.ly_img_editor_dock_button_size) }
    onClick = {
        val designBlock = editorContext.engine.block.findByName("Greeting").first()
        editorContext.eventHandler.send(
            EditorEvent.Sheet.Open(SheetType.FontSize(designBlock = designBlock)),
        )
    }
}

/**
 * Colors button of the dock.
 */
@Stable
open class ColorsButtonScope(
    parentScope: EditorScope,
    private val colors: Map<DesignBlock, Color>,
) : Dock.ItemScope(parentScope) {
    val EditorContext.colors: Map<DesignBlock, Color>
        get() = this@ColorsButtonScope.colors
}

/**
 * Button builder of the [rememberColors] button.
 */
@Stable
class ColorsButtonBuilder : AbstractButtonBuilder<ColorsButtonScope>() {
    override var scope: ScopedProperty<EditorScope, ColorsButtonScope> = {
        remember(this) {
            val engine = editorContext.engine
            val colorMapping = TreeMap<DesignBlock, Color>()
            // Creates design block to design block name mapping for all the
            // design blocks on the current page.
            engine.scene.getCurrentPage()?.let { currentPage ->
                val target = engine.block.findByName("Greeting")
                    .firstOrNull()
                    ?.takeIf { engine.block.getParent(it) == currentPage }
                    ?: currentPage
                editorContext.collectColorsRecursively(target, colorMapping)
            }
            ColorsButtonScope(parentScope = this, colors = colorMapping)
        }
    }

    private fun EditorContext.collectColorsRecursively(
        designBlock: DesignBlock,
        colorMapping: TreeMap<DesignBlock, Color>,
    ) {
        // We collect only named design blocks
        if (engine.block.getName(designBlock).isNotEmpty()) {
            val color = if (engine.block.supportsFill(designBlock) && engine.block.isFillEnabled(designBlock)) {
                when (val fillInfo = engine.getFill(designBlock)) {
                    is SolidFill, is GradientFill -> fillInfo.mainColor
                    else -> null
                }
            } else if (engine.block.supportsStroke(designBlock) && engine.block.isStrokeEnabled(designBlock)) {
                engine.getStrokeColor(designBlock)
            } else {
                null
            }
            color?.let { colorMapping[designBlock] = it }
        }
        engine.block.getChildren(designBlock).forEach {
            collectColorsRecursively(it, colorMapping)
        }
    }
}

/**
 * Colors button of the dock.
 */
@Composable
fun Dock.Button.rememberColors() = Button.remember(::ColorsButtonBuilder) {
    id = { EditorComponentId("ly.img.component.dock.button.colors") }
    icon = {
        val icon = remember(this) {
            EditorIcon.Colors(
                colors = editorContext.colors.values.toList(),
            )
        }
        EditorIcon(icon = icon)
    }
    textString = { stringResource(R.string.ly_img_editor_dock_button_colors) }
    onClick = {
        editorContext.colors
            .let { SheetType.Colors(designBlocks = it.keys.toList()) }
            .let(EditorEvent.Sheet::Open)
            .let(editorContext.eventHandler::send)
    }
}
