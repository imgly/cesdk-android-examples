package ly.img.editor.core.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.remember

/**
 * The scope class of calculation block in [rememberLastValue] functions.
 */
@Suppress("UNCHECKED_CAST")
class RememberLastScope<T : Any?> {
    private var isValueSet: Boolean = false

    @PublishedApi
    internal var internalLastValue: T? = null
        set(value) {
            field = value
            isValueSet = true
        }

    /**
     * Last value, that was returned by calculation block in [rememberLastValue]. Note that accessing this property will throw
     * an exception if calculation block has not returned any value before.
     */
    val lastValue: T
        get() =
            if (isValueSet.not()) {
                error(
                    "lastValue is accessed before returning a value in rememberLastValue block. " +
                        "Make sure to provide at least one value before accessing lastValue.",
                )
            } else {
                internalLastValue as T
            }
}

/**
 * An extension to the regular [remember] function that allows accessing last calculated value in the [calculation] block.
 */
@Composable
inline fun <T> rememberLastValue(crossinline calculation: @DisallowComposableCalls RememberLastScope<T>.() -> T): T {
    val scope = remember { RememberLastScope<T>() }
    return remember {
        calculation(scope)
    }.also { scope.internalLastValue = it }
}

/**
 * An extension to the regular [remember] function that allows accessing last calculated value in the [calculation] block.
 */
@Composable
inline fun <T> rememberLastValue(
    key: Any?,
    crossinline calculation: @DisallowComposableCalls RememberLastScope<T>.() -> T,
): T {
    val scope = remember { RememberLastScope<T>() }
    return remember(key) {
        calculation(scope)
    }.also { scope.internalLastValue = it }
}

/**
 * An extension to the regular [remember] function that allows accessing last calculated value in the [calculation] block.
 */
@Composable
inline fun <T> rememberLastValue(
    key1: Any?,
    key2: Any?,
    crossinline calculation: @DisallowComposableCalls RememberLastScope<T>.() -> T,
): T {
    val scope = remember { RememberLastScope<T>() }
    return remember(key1, key2) {
        calculation(scope)
    }.also { scope.internalLastValue = it }
}

/**
 * An extension to the regular [remember] function that allows accessing last calculated value in the [calculation] block.
 */
@Composable
inline fun <T> rememberLastValue(
    key1: Any?,
    key2: Any?,
    key3: Any?,
    crossinline calculation: @DisallowComposableCalls RememberLastScope<T>.() -> T,
): T {
    val scope = remember { RememberLastScope<T>() }
    return remember(key1, key2, key3) {
        calculation(scope)
    }.also { scope.internalLastValue = it }
}

/**
 * An extension to the regular [remember] function that allows accessing last calculated value in the [calculation] block.
 */
@Composable
inline fun <T> rememberLastValue(
    vararg keys: Any?,
    crossinline calculation: @DisallowComposableCalls RememberLastScope<T>.() -> T,
): T {
    val scope = remember { RememberLastScope<T>() }
    return remember(keys) {
        calculation(scope)
    }.also { scope.internalLastValue = it }
}
