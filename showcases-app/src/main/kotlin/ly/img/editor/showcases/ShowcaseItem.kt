package ly.img.editor.showcases

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

sealed class ShowcaseItem(
    open val actionScene: String? = null,
    open val actionScreen: Screen? = null,
    open val span: Int,
    val key: String,
) {
    data class Header(
        @StringRes val title: Int,
        override val span: Int,
        val items: List<ShowcaseItem> = emptyList(),
    ) : ShowcaseItem(
            actionScene = null,
            actionScreen = null,
            span = span,
            key = "item.header.$title",
        )

    data class Content(
        @DrawableRes val thumbnailRes: Int,
        override val actionScene: String,
        override val actionScreen: Screen,
        val thumbnailAspectRatio: Float = 1F,
    ) : ShowcaseItem(
            actionScene = actionScene,
            actionScreen = actionScreen,
            span = 1,
            key = "item.content.$actionScene",
        )

    class CarouselContent(
        @DrawableRes val iconRes: Int,
        @StringRes val label: Int,
        @StringRes val sublabel: Int?,
        val hasDotLine: Boolean = false,
        val clickAction: ClickAction = ClickAction.OPEN_SCENE,
        override val actionScene: String?,
        override val actionScreen: Screen,
    ) : ShowcaseItem(
            actionScene = actionScene,
            actionScreen = actionScreen,
            span = 1,
            key = "item.content.${actionScene ?: actionScreen.routeScheme}",
        ) {
        enum class ClickAction {
            OPEN_SCENE,
            PICK_SCENE,
            PICK_IMAGE,
        }
    }

    class CustomFunctionality(
        val vectorIcon: ImageVector,
        @DrawableRes val thumbnailRes: Int,
        @StringRes val label: Int,
        @StringRes val sublabel: Int?,
        val clickAction: CarouselContent.ClickAction = CarouselContent.ClickAction.OPEN_SCENE,
        override val actionScene: String?,
        override val actionScreen: Screen,
    ) : ShowcaseItem(
            actionScene = actionScene,
            actionScreen = actionScreen,
            span = 1,
            key = "item.custom_functionality.${actionScene ?: actionScreen.routeScheme}",
        )
}
