import android.net.Uri
import ly.img.engine.Color
import ly.img.engine.ContentFillMode
import java.nio.ByteBuffer

data class EditStickersResult(
    val currentImageUri: Uri,
    val positionX: Float,
    val positionY: Float,
    val width: Float,
    val height: Float,
    val rotation: Float,
    val isFlippedHorizontally: Boolean,
    val coverMode: ContentFillMode,
    val finalContentFillMode: ContentFillMode,
    val opacity: Float,
    val dropShadowEnabled: Boolean,
    val dropShadowColor: Color,
    val dropShadowOffsetX: Float,
    val dropShadowOffsetY: Float,
    val dropShadowBlurRadiusX: Float,
    val dropShadowBlurRadiusY: Float,
    val duplicateKind: String,
    val duplicatePositionX: Float,
    val duplicatePositionY: Float,
    val strokeEnabled: Boolean,
    val strokeColor: Color,
    val strokeWidth: Float,
    val blurEnabled: Boolean,
    val effectCount: Int,
    val effectEnabled: Boolean,
    val previewPngData: ByteBuffer,
)
