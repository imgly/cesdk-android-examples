package ly.img.editor.showcases

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

sealed class ShowcaseItem(
    open val span: Int,
    val key: String,
) {
    data class Header(
        @StringRes val title: Int,
        override val span: Int,
        val items: List<ShowcaseItem> = emptyList(),
    ) : ShowcaseItem(
            span = span,
            key = "item.header.$title",
        )

    data class ClickAction(
        val destination: Screen,
        val requestApiKey: Boolean = false,
        val requestImage: Boolean = false,
        val requestScene: Boolean = false,
        val sceneId: String? = null,
    ) {
        init {
            if (requestImage && requestScene) {
                error("Cannot request both image and scene.")
            }
            if (sceneId != null && requestScene) {
                error("Cannot request scene and also provide one.")
            }
        }
    }

    interface Clickable {
        val clickAction: ClickAction
    }

    data class Content(
        @DrawableRes val thumbnailRes: Int,
        override val clickAction: ClickAction,
        val thumbnailAspectRatio: Float = 1F,
    ) : ShowcaseItem(
            span = 1,
            key = "item.content.$clickAction",
        ),
        Clickable

    class CarouselContent(
        @DrawableRes val iconRes: Int,
        @StringRes val label: Int,
        @StringRes val sublabel: Int?,
        val hasDotLine: Boolean = false,
        override val clickAction: ClickAction,
    ) : ShowcaseItem(
            span = 1,
            key = "item.content.$clickAction",
        ),
        Clickable

    class CustomFunctionality(
        val vectorIcon: ImageVector,
        @DrawableRes val thumbnailRes: Int,
        @StringRes val label: Int,
        @StringRes val sublabel: Int?,
        override val clickAction: ClickAction,
    ) : ShowcaseItem(
            span = 1,
            key = "item.content.$clickAction",
        ),
        Clickable
}
