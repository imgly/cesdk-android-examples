package ly.img.cesdk.core.ui.tab_item

import androidx.compose.runtime.Composable
import kotlin.reflect.KClass

open class TabIconMappings {

    val mappings = mutableMapOf<KClass<out TabIcon>, TabIconComposable<out TabIcon>>()

    inline fun <reified T : TabIcon> registerTabIconComposable(tabIconComposable: TabIconComposable<out TabIcon>) {
        mappings[T::class] = tabIconComposable
    }

    @Composable
    @Suppress("UNCHECKED_CAST")
    fun TabIconContent(tabIcon: TabIcon) =
        (checkNotNull(mappings[tabIcon::class]) as TabIconComposable<TabIcon>).Content(icon = tabIcon)
}