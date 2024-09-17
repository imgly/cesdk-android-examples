package ly.img.editor.catalog

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

sealed class CatalogItem(
    open val actionScene: String? = null,
    open val actionScreen: Screen? = null,
    open val span: Int,
    val key: String,
) {
    data class Header(
        @StringRes val title: Int,
        override val actionScene: String? = null,
        override val actionScreen: Screen? = null,
        override val span: Int,
        val items: List<CatalogItem> = emptyList(),
    ) : CatalogItem(
            actionScene = actionScene,
            actionScreen = actionScreen,
            span = span,
            key = "item.header.$title",
        )

    data class Content(
        @DrawableRes val thumbnailRes: Int,
        override val actionScene: String,
        override val actionScreen: Screen,
        val thumbnailAspectRatio: Float = 1F,
    ) : CatalogItem(
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
    ) : CatalogItem(
            actionScene = actionScene,
            actionScreen = actionScreen,
            span = 1,
            key = "item.content.$actionScene",
        ) {
        enum class ClickAction {
            OPEN_SCENE,
            PICK_SCENE,
            PICK_IMAGE,
        }
    }
}
