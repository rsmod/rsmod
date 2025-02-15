package org.rsmod.api.testing.capture

import net.rsprot.protocol.message.IncomingGameMessage
import net.rsprot.protocol.message.codec.incoming.MessageConsumer
import org.rsmod.game.client.Client
import org.rsmod.game.entity.Player

public class CaptureClient : Client<Any, Any> {
    public var sessionOpened: Boolean = false
        private set

    public var sessionClosed: Boolean = false
        private set

    private val _outgoingMessages: MutableList<Any> = mutableListOf()
    public val outgoingMessages: List<Any>
        get() = _outgoingMessages

    private val _incomingHandlers = mutableListOf<IncomingHandlerMessage<in IncomingGameMessage>>()

    public val isEmpty: Boolean
        get() = outgoingMessages.isEmpty()

    public val isNotEmpty: Boolean
        get() = !isEmpty

    public fun count(): Int = outgoingMessages.size

    public fun count(predicate: (Any) -> Boolean): Int = outgoingMessages.count(predicate)

    public inline fun <reified R> filterIsInstance(): List<R> =
        outgoingMessages.filterIsInstance<R>()

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

    public operator fun contains(message: Any): Boolean = message in outgoingMessages

    public fun clearOutgoing() {
        _outgoingMessages.clear()
    }

    public fun clearIncoming() {
        _incomingHandlers.clear()
    }

    @Suppress("UNCHECKED_CAST")
    public fun <T : IncomingGameMessage> queue(handler: MessageConsumer<Player, T>, message: T) {
        val handlerMessage =
            IncomingHandlerMessage(handler, message) as IncomingHandlerMessage<IncomingGameMessage>
        _incomingHandlers += handlerMessage
    }

    override fun open(service: Any, player: Player) {
        sessionOpened = true
    }

    override fun close(service: Any, player: Player) {
        sessionClosed = true
    }

    override fun write(message: Any) {
        _outgoingMessages += message
    }

    override fun read(player: Player) {
        for ((handler, message) in _incomingHandlers) {
            handler.consume(player, message)
        }
    }

    override fun flush() {}

    private data class IncomingHandlerMessage<T : IncomingGameMessage>(
        val handler: MessageConsumer<Player, T>,
        val message: T,
    )
}
