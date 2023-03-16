package ly.img.cesdk.dock

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.cesdk.core.components.ColorButton

@Composable
fun DockMenuOption(
    modifier: Modifier = Modifier,
    data: OptionItemData,
    onClick: (OptionType) -> Unit
) {
    TextButton(
        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
        onClick = { onClick(data.optionType) },
        modifier = modifier.widthIn(min = 80.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (val icon = data.icon) {
                is OptionIcon.FillStroke -> {
                    if (icon.hasFill && icon.hasStroke) {
                        Box {
                            ColorButton(color = icon.fillColor, buttonSize = 24.dp, selectionStrokeWidth = 0.dp)
                            ColorButton(
                                color = icon.strokeColor,
                                buttonSize = 24.dp,
                                selectionStrokeWidth = 0.dp,
                                punchHole = true,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        }
                    } else if (icon.hasFill) {
                        ColorButton(color = icon.fillColor, buttonSize = 24.dp, selectionStrokeWidth = 0.dp)
                    } else if (icon.hasStroke) {
                        ColorButton(color = icon.strokeColor, buttonSize = 24.dp, selectionStrokeWidth = 0.dp, punchHole = true)
                    } else {
                        throw IllegalStateException("OptionItemData with OptionIcon.FillStroke has neither stroke nor fill.")
                    }
                }
                is OptionIcon.Vector -> Icon(imageVector = icon.imageVector, contentDescription = null)
            }
            Text(
                text = stringResource(id = data.labelStringRes),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}