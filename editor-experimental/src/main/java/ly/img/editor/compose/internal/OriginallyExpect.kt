package ly.img.editor.compose.internal

/**
 * Whenever the copied function, getter, class, property is expect, we copy the actual implementation and mark it
 * as OriginallyExpect to remember that originally it had expect keyword.
 */
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.TYPE,
    AnnotationTarget.TYPE_PARAMETER,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY,
)
annotation class OriginallyExpect
