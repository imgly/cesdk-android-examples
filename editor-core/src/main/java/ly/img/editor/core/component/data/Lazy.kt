package ly.img.editor.core.component.data

/**
 * A convenience function for creating a [Lazy] object with no thread safety.
 */
fun <T> unsafeLazy(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)
