package ly.img.editor.configuration.memories.component

import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ly.img.editor.configuration.memories.MemoriesViewModel
import ly.img.editor.configuration.memories.iconPack.Audiotrack
import ly.img.editor.configuration.memories.iconPack.Image
import ly.img.editor.configuration.memories.iconPack.Style
import ly.img.editor.configuration.memories.sheet.createFilterStylesSheetType
import ly.img.editor.core.EditorScope
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.EditorComponent
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.remember
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.sheet.SheetType
import ly.img.editor.core.state.EditorViewMode
import ly.img.editor.core.theme.surface1
import ly.img.editor.core.theme.surface3

fun dockConfiguration(viewModel: MemoriesViewModel): @Composable (EditorScope.() -> EditorComponent<*>) = dock@{
    val showImageGrid by viewModel.showImageGrid.collectAsState()
    val shouldHideUIForPreview by viewModel.shouldHideUIForPreview.collectAsState()

    Dock.remember {
        enterTransition = {
            expandVertically(animationSpec = tween(200)) + fadeIn()
        }
        exitTransition = {
            shrinkVertically(animationSpec = tween(200)) + fadeOut()
        }
        decoration = { content ->
            val state by editorContext.state.collectAsState()
            Box(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surface1.copy(alpha = 0.95f),
                    )
                    .padding(8.dp)
                    .padding(horizontal = 32.dp)
                    .shadow(
                        elevation = 3.dp,
                        shape = CircleShape,
                        spotColor = Color.Black.copy(alpha = .6f),
                    )
                    .background(
                        MaterialTheme.colorScheme.surface3.copy(alpha = 0.95f),
                        shape = CircleShape,
                    ),
            ) {
                content()
            }
        }
        horizontalArrangement = { Arrangement.SpaceEvenly }
        visible = {
            val state by editorContext.state.collectAsState()
            (state.viewMode !is EditorViewMode.Preview) && !shouldHideUIForPreview
        }
        listBuilder = {
            Dock.ListBuilder.remember {
                // Media (image + video) library button
                add {
                    Dock.Button.remember {
                        id = { EditorComponentId("ly.img.images") }
                        vectorIcon = { IconPack.Image }
                        textString = { "Media" }
                        onClick = {
                            if (showImageGrid) {
                                // Already open → treat as cancel
                                viewModel.cancelOverlay()
                            } else {
                                // Seed the editable grid from the tracks (source of truth)
                                viewModel.openImageGridFromTracks(editorContext)
                            }
                        }
                    }
                }

                // Styles button
                add {
                    Dock.Button.remember {
                        id = { EditorComponentId("ly.img.styles") }
                        vectorIcon = { IconPack.Style }
                        textString = { "Styles" }
                        onClick = {
                            editorContext.eventHandler.send(
                                EditorEvent.Sheet.Open(
                                    createFilterStylesSheetType(
                                        thumbnails = viewModel.styleThumbnails.value,
                                        // Preselect the currently-applied style on reopen.
                                        initialSelectedId = viewModel.activeStyleId,
                                    ) { style ->
                                        viewModel.applyStyle(editorContext, style.id)
                                    },
                                ),
                            )
                        }
                    }
                }

                // Audio library button
                add {
                    Dock.Button.remember {
                        id = { EditorComponentId("ly.img.audio") }
                        vectorIcon = { IconPack.Audiotrack }
                        textString = { "Audio" }
                        onClick = {
                            // Open the SDK's built-in audio library
                            editorContext.eventHandler.send(
                                EditorEvent.Sheet.Open(
                                    SheetType.LibraryAdd(
                                        libraryCategory = LibraryCategory.Audio,
                                    ),
                                ),
                            )
                            editorContext.engine.scene.getCurrentPage()?.let { page ->
                                editorContext.engine.block.setPlaying(page, false)
                                // Keep the ViewModel in sync, else the next play/pause needs a double tap.
                                viewModel.setPlaying(false)
                            }
                        }
                    }
                }
            }
        }
    }
}
