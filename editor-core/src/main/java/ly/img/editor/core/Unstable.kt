package ly.img.editor.core

@RequiresOptIn(
    level = RequiresOptIn.Level.WARNING,
    message = "This Editor API is unstable. It may be removed or changed in the future.",
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
annotation class UnstableEditorApi
