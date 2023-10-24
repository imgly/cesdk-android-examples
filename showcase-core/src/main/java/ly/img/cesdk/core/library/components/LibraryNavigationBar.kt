package ly.img.cesdk.core.library.components

import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

@Composable
internal fun LibraryNavigationBar(
    items: List<LibraryNavBarItem>,
    modifier: Modifier = Modifier,
    selectedItemIndex: Int,
    onSelectionChange: (Int) -> Unit
) {
    if (items.size > 5) {
        ScrollableNavigationBar(modifier) {
            itemsIndexed(items) { index, item ->
                WeightlessNavigationBarItem(
                    icon = {
                        Icon(
                            if (selectedItemIndex == index) item.selectedIcon else item.unselectedIcon,
                            contentDescription = null
                        )
                    },
                    label = { Text(stringResource(item.titleRes)) },
                    selected = selectedItemIndex == index,
                    onClick = { onSelectionChange(index) }
                )
            }
        }
    } else {
        NavigationBar(modifier) {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            if (selectedItemIndex == index) item.selectedIcon else item.unselectedIcon,
                            contentDescription = null
                        )
                    },
                    label = { Text(stringResource(item.titleRes)) },
                    selected = selectedItemIndex == index,
                    onClick = { onSelectionChange(index) }
                )
            }
        }
    }
}