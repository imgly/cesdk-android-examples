package ly.img.editor.core.ui

import ly.img.editor.core.event.EditorEvent
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * A simple event system that can be used to communicate between different parts of the SDK.
 * @param register A lambda that will be called to register all events.
 * @see EventsHandler.register
 */
class EventsHandler(
    register: EventsHandler.() -> Unit,
) {
    private val eventMap = mutableMapOf<KClass<out EditorEvent>, (event: EditorEvent) -> Unit>()

    /**
     * handles an event by calling the lambda that was registered for the event type.
     * @param event The event to handle.
     */
    fun handleEvent(event: EditorEvent) {
        eventMap[event::class]?.invoke(event)
    }

    /**
     * A delegate that can be used to register a lambda to be called when an event of the given type is fired.
     * @param lambda The lambda that will be called when the event is fired.
     * @return The lambda that was passed in.
     */
    @Suppress("UNCHECKED_CAST")
    operator fun <EventType : EditorEvent> set(
        event: KClass<out EventType>,
        lambda: (event: EventType) -> Unit,
    ) {
        eventMap[event] = lambda as (event: EditorEvent) -> Unit
    }

    /**
     * A delegate that can be used to register a lambda to be called when an event of the given type is fired.
     * @param lambda The lambda that will be called when the event is fired.
     * @return The lambda that was passed in.
     */
    @Suppress("UNCHECKED_CAST")
    infix fun <EventType : EditorEvent> KClass<out EventType>.to(lambda: (event: EventType) -> Unit) {
        eventMap[this] = lambda as (event: EditorEvent) -> Unit
    }

    init {
        register()
    }
}

/**
 * Registers a lambda to be called when an event of the given type is fired.
 * @param lambda The lambda that will be called when the event is fired.
 * @return The lambda that was passed in.
 */
inline fun <reified EventType : EditorEvent> EventsHandler.register(noinline lambda: (event: EventType) -> Unit): Any {
    this[EventType::class] = lambda
    return lambda
}

/**
* Can be used to inject a dynamic value and bind it to a property.
* The type of the property is a primitive Int.
* @param inject The lambda that will be called to inject the value
*/
fun inject(inject: () -> Int) = InjectInt(inject)

/**
* Can be used to inject a dynamic value and bind it to a property.
* * The type of the property is a primitive Float.
* @param inject The lambda that will be called to inject the value
*/
fun inject(inject: () -> Float) = InjectFloat(inject)

/**
* Can be used to inject a dynamic value and bind it to a property.
* * * The type of the property is a primitive Long.
* @param inject The lambda that will be called to inject the value
*/
fun inject(inject: () -> Long) = InjectLong(inject)

/**
* Can be used to inject a dynamic value and bind it to a property.
* * * The type of the property is a primitive Double.
* @param inject The lambda that will be called to inject the value
*/
fun inject(inject: () -> Double) = InjectDouble(inject)

/**
* Can be used to inject a dynamic value and bind it to a property.
* * * The type of the property is a primitive Boolean.
* @param inject The lambda that will be called to inject the value
*/
fun inject(inject: () -> Boolean) = InjectBoolean(inject)

/**
* Can be used to inject a dynamic value and bind it to a property.
* The type of the property will be inferred from the lambda.
* @param inject The lambda that will be called to inject the value
*/
fun <Type> inject(inject: () -> Type) = Inject(inject)

/**
 * Can be used to inject a dynamic value and bind it to a property.
 * The type of the property will be inferred from the lambda.
 * @param inject The lambda that will be called to inject the value
 */
class Inject<Type> internal constructor(private val inject: () -> Type) {
    operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ): Type = inject()
}

/**
 * Can be used to inject a dynamic value and bind it to a property.
 * The type of the property is a primitive int. Make sure to prefer this over Inject<Int> for performance reasons.
 * @param inject The lambda that will be called to inject the value
 */
class InjectInt internal constructor(private val inject: () -> Int) {
    operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ) = inject()
}

/**
 * Can be used to inject a dynamic value and bind it to a property.
 * The type of the property is a primitive float. Make sure to prefer this over Inject<Float> for performance reasons.
 * @param inject The lambda that will be called to inject the value
 */
class InjectFloat internal constructor(private val inject: () -> Float) {
    operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ) = inject()
}

/**
 * Can be used to inject a dynamic value and bind it to a property.
 * The type of the property is a primitive double. Make sure to prefer this over Inject<Double> for performance reasons.
 * @param inject The lambda that will be called to inject the value
 */
class InjectDouble internal constructor(private val inject: () -> Double) {
    operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ) = inject()
}

/**
 * Can be used to inject a dynamic value and bind it to a property.
 * The type of the property is a primitive long. Make sure to prefer this over Inject<Long> for performance reasons.
 * @param inject The lambda that will be called to inject the value
 */
class InjectLong internal constructor(private val inject: () -> Long) {
    operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ) = inject()
}

/**
 * Can be used to inject a dynamic value and bind it to a property.
 * The type of the property is a primitive boolean. Make sure to prefer this over Inject<Boolean> for performance reasons.
 * @param inject The lambda that will be called to inject the value
 */
class InjectBoolean internal constructor(private val inject: () -> Boolean) {
    operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ) = inject()
}
