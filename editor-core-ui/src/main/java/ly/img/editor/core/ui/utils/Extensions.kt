package ly.img.editor.core.ui.utils

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity

val Context.activity: ComponentActivity?
    get() =
        when (this) {
            is ComponentActivity -> this
            is ContextWrapper -> baseContext.activity
            else -> null
        }
