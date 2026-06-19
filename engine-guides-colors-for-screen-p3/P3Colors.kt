import ly.img.engine.Engine

// Demonstrates P3 support checks, enabling, and graceful fallback.
fun p3Colors(engine: Engine) {
    // highlight-android-check-support
    val p3IsSupported = engine.editor.supportsP3()
    // highlight-android-check-support

    // highlight-android-check-support-throwing
    try {
        engine.editor.checkP3Support()
    } catch (exception: Exception) {
        println("P3 unavailable: ${exception.message}")
    }
    // highlight-android-check-support-throwing

    // highlight-android-enable
    if (p3IsSupported) {
        engine.editor.setSettingBoolean(
            keypath = "features/p3WorkingColorSpace",
            value = true,
        )
    }
    // highlight-android-enable

    // highlight-android-graceful-fallback
    try {
        engine.editor.checkP3Support()
        engine.editor.setSettingBoolean(
            keypath = "features/p3WorkingColorSpace",
            value = true,
        )
    } catch (exception: Exception) {
        println("Staying on sRGB: ${exception.message}")
    }
    // highlight-android-graceful-fallback
}
