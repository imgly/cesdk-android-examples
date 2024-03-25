package ly.img.editor.compose.material3

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import ly.img.editor.compose.material3.tokens.TypographyKeyTokens

/**
 * Helper function for component typography tokens.
 */
internal fun Typography.fromToken(value: TypographyKeyTokens): TextStyle {
    return when (value) {
        TypographyKeyTokens.DisplayLarge -> displayLarge
        TypographyKeyTokens.DisplayMedium -> displayMedium
        TypographyKeyTokens.DisplaySmall -> displaySmall
        TypographyKeyTokens.HeadlineLarge -> headlineLarge
        TypographyKeyTokens.HeadlineMedium -> headlineMedium
        TypographyKeyTokens.HeadlineSmall -> headlineSmall
        TypographyKeyTokens.TitleLarge -> titleLarge
        TypographyKeyTokens.TitleMedium -> titleMedium
        TypographyKeyTokens.TitleSmall -> titleSmall
        TypographyKeyTokens.BodyLarge -> bodyLarge
        TypographyKeyTokens.BodyMedium -> bodyMedium
        TypographyKeyTokens.BodySmall -> bodySmall
        TypographyKeyTokens.LabelLarge -> labelLarge
        TypographyKeyTokens.LabelMedium -> labelMedium
        TypographyKeyTokens.LabelSmall -> labelSmall
    }
}
