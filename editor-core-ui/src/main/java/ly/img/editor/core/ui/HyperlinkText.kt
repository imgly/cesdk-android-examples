package ly.img.editor.core.ui

import android.content.ActivityNotFoundException
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun HyperlinkText(
    modifier: Modifier = Modifier,
    fullText: String,
    hyperLinks: Map<String?, String?>,
    textStyle: TextStyle = MaterialTheme.typography.bodySmall,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    linkTextDecoration: TextDecoration = TextDecoration.Underline,
) {
    val annotatedString =
        buildAnnotatedString {
            append(fullText)

            for ((key, value) in hyperLinks) {
                if (key == null || value == null) continue
                val startIndex = fullText.indexOf(key)
                val endIndex = startIndex + key.length
                addStyle(
                    style =
                        SpanStyle(
                            textDecoration = linkTextDecoration,
                        ),
                    start = startIndex,
                    end = endIndex,
                )
                addStringAnnotation(
                    tag = "URL",
                    annotation = value,
                    start = startIndex,
                    end = endIndex,
                )
            }
        }

    val uriHandler = LocalUriHandler.current

    ClickableText(
        modifier = modifier,
        text = annotatedString,
        style = textStyle.copy(color = textColor),
        onClick = {
            annotatedString
                .getStringAnnotations("URL", it, it)
                .firstOrNull()?.let { stringAnnotation ->
                    try {
                        uriHandler.openUri(stringAnnotation.item)
                    } catch (ex: ActivityNotFoundException) {
                        ex.printStackTrace()
                    }
                }
        },
    )
}
