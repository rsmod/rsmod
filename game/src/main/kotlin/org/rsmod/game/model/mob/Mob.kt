package org.rsmod.game.model.mob

import com.github.michaelbull.logging.InlineLogger
import com.google.common.base.MoreObjects
import java.time.LocalDateTime
import org.rsmod.game.action.ActionBus
import org.rsmod.game.event.Event
import org.rsmod.game.event.EventBus
import org.rsmod.game.event.impl.LoginEvent
import org.rsmod.game.event.impl.LogoutEvent
import org.rsmod.game.message.ServerPacket
import org.rsmod.game.message.ServerPacketListener
import org.rsmod.game.model.client.Entity
import org.rsmod.game.model.client.NpcEntity
import org.rsmod.game.model.client.PlayerEntity
import org.rsmod.game.model.domain.Appearance
import org.rsmod.game.model.domain.Direction
import org.rsmod.game.model.domain.PlayerId
import org.rsmod.game.model.item.container.ItemContainer
import org.rsmod.game.model.item.container.ItemContainerMap
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.map.Viewport
import org.rsmod.game.model.snapshot.Snapshot
import org.rsmod.game.model.move.MovementQueue
import org.rsmod.game.model.move.MovementSpeed
import org.rsmod.game.model.ui.InterfaceList
import org.rsmod.game.queue.GameQueueStack
import org.rsmod.game.queue.QueueType
import org.rsmod.game.timer.TimerMap

private val logger = InlineLogger()

private val DEFAULT_DIRECTION = Direction.South
private const val DEFAULT_RUN_ENERGY = 100.0

sealed class Mob(
    val movement: MovementQueue = MovementQueue(),
    var speed: MovementSpeed = MovementSpeed.Walk,
    var faceDirection: Direction = DEFAULT_DIRECTION,
    var displace: Boolean = false,
    val timers: TimerMap = TimerMap(),
    val queueStack: GameQueueStack = GameQueueStack()
) {

    abstract val entity: Entity

    var index: Int
        get() = entity.index
        set(value) { entity.index = value }

    var coords: Coordinates
        get() = entity.coords
        set(value) { entity.coords = value }

    fun weakQueue(block: suspend () -> Unit) = queueStack.queue(QueueType.Weak, block)

    fun normalQueue(block: suspend () -> Unit) = queueStack.queue(QueueType.Normal, block)

    fun strongQueue(block: suspend () -> Unit) = queueStack.queue(QueueType.Strong, block)

    fun clearQueues() = queueStack.clear()

    fun displace(coords: Coordinates) {
        this.coords = coords
        this.displace = true
    }

    fun stopMovement() {
        movement.clear()
        movement.nextSteps.clear()
    }
}

class Player(
    val id: PlayerId,
    val loginName: String,
    val eventBus: EventBus,
    val actionBus: ActionBus,
    override val entity: PlayerEntity,
    var snapshot: Snapshot = Snapshot.INITIAL,
    var viewport: Viewport = Viewport.ZERO,
    var appearance: Appearance = Appearance.ZERO,
    val inventory: ItemContainer = ItemContainer(),
    val equipment: ItemContainer = ItemContainer(),
    val bank: ItemContainer = ItemContainer(),
    val containers: ItemContainerMap = ItemContainerMap(),
    val ui: InterfaceList = InterfaceList(),
    var lastSpeed: MovementSpeed? = null,
    var runEnergy: Double = DEFAULT_RUN_ENERGY,
    private val messageListeners: List<ServerPacketListener> = mutableListOf()
) : Mob() {

    val username: String
        get() = entity.username

    fun login() {
        eventBus.publish(LoginEvent(this, LoginEvent.Priority.High))
        eventBus.publish(LoginEvent(this, LoginEvent.Priority.Normal))
        eventBus.publish(LoginEvent(this, LoginEvent.Priority.Low))
    }

    fun logout() {
        eventBus.publish(LogoutEvent(this))
        messageListeners.forEach { it.close() }
    }

    fun eligibleRank(rank: Int): Boolean {
        return entity.rank >= rank
    }

    fun write(packet: ServerPacket) {
        messageListeners.forEach { it.write(packet) }
    }

    fun flush() {
        messageListeners.forEach { it.flush() }
    }

    fun snapshot() = Snapshot(
        timestamp = LocalDateTime.now(),
        coords = coords
    )

    fun info(message: () -> String) {
        logger.info { "$username: ${message()}" }
    }

    fun warn(message: () -> String) {
        logger.warn { "$username: ${message()}" }
    }

    fun trace(message: () -> String) {
        logger.trace { "$username: ${message()}" }
    }

    fun debug(message: () -> String) {
        logger.debug { "$username: ${message()}" }
    }

    inline fun <reified T : Event> submitEvent(event: T) {
        queueStack.submitEvent(event)
        eventBus.publish(event)
    }

    override fun toString(): String = MoreObjects
        .toStringHelper(this)
        .add("loginName", loginName)
        .add("displayName", username)
        .add("id", id)
        .add("index", index)
        .add("coords", coords)
        .toString()
}

class Npc(
    override val entity: NpcEntity
) : Mob()
