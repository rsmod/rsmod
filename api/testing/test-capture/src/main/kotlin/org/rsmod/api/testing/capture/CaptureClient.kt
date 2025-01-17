package org.rsmod.api.testing.capture

import org.rsmod.game.client.Client
import org.rsmod.game.entity.Player

public class CaptureClient : Client<Any, Any> {
    public var sessionOpened: Boolean = false
        private set

    public var sessionClosed: Boolean = false
        private set

    private val _messages: MutableList<Any> = mutableListOf()
    public val messages: List<Any>
        get() = _messages

    public val isEmpty: Boolean
        get() = messages.isEmpty()

    public val isNotEmpty: Boolean
        get() = !isEmpty

    public fun count(): Int = messages.size

    public fun count(predicate: (Any) -> Boolean): Int = messages.count(predicate)

    public inline fun <reified R> filterIsInstance(): List<R> = messages.filterIsInstance<R>()

    public inline fun <reified R> allOf(predicate: (R) -> Boolean): Boolean =
        filterIsInstance<R>().all(predicate)

    public inline fun <reified R> anyOf(): Boolean = filterIsInstance<R>().any()

    public inline fun <reified R> anyOf(predicate: (R) -> Boolean): Boolean =
        filterIsInstance<R>().any(predicate)

    public inline fun <reified R> hasAny(): Boolean = filterIsInstance<R>().isNotEmpty()

    public inline fun <reified R> hasNone(): Boolean = filterIsInstance<R>().isEmpty()

    public inline fun <reified R> noneOf(): Boolean = filterIsInstance<R>().none()

    public inline fun <reified R> noneOf(predicate: (R) -> Boolean): Boolean =
        filterIsInstance<R>().none(predicate)

    public inline fun <reified R> countOf(): Int = filterIsInstance<R>().count()

    public inline fun <reified R> countOf(predicate: (R) -> Boolean): Int =
        filterIsInstance<R>().count(predicate)

    public inline fun <reified R, S> mapOf(transform: (R) -> S): List<S> =
        filterIsInstance<R>().map(transform)

    public inline fun <reified R, S> singleMapOf(transform: (R) -> S): S =
        transform(filterIsInstance<R>().single())

    public inline fun <reified R> single(): R = filterIsInstance<R>().single()

    public inline fun <reified R> singlePredicate(predicate: (R) -> Boolean): Boolean =
        predicate(single())

    public operator fun contains(message: Any): Boolean = message in messages

    public fun clear() {
        _messages.clear()
    }

    override fun open(service: Any, player: Player) {
        sessionOpened = true
    }

    override fun close(service: Any, player: Player) {
        sessionClosed = true
    }

    override fun write(message: Any) {
        _messages += message
    }

    override fun read(player: Player) {}

    override fun flush() {}

    override fun prePlayerCycle(player: Player) {}

    override fun postPlayerCycle(player: Player) {}
}
