package gg.rsmod.game.model.mob

import com.github.michaelbull.logging.InlineLogger
import com.google.common.base.MoreObjects
import gg.rsmod.game.action.ActionBus
import gg.rsmod.game.attribute.AttributeMap
import gg.rsmod.game.event.Event
import gg.rsmod.game.event.EventBus
import gg.rsmod.game.event.impl.LoginEvent
import gg.rsmod.game.message.ServerPacket
import gg.rsmod.game.message.ServerPacketListener
import gg.rsmod.game.model.client.Entity
import gg.rsmod.game.model.client.NpcEntity
import gg.rsmod.game.model.client.PlayerEntity
import gg.rsmod.game.model.domain.Appearance
import gg.rsmod.game.model.domain.Direction
import gg.rsmod.game.model.domain.PlayerId
import gg.rsmod.game.model.item.container.ItemContainer
import gg.rsmod.game.model.item.container.ItemContainerMap
import gg.rsmod.game.model.map.Coordinates
import gg.rsmod.game.model.map.Viewport
import gg.rsmod.game.queue.GameQueueStack
import gg.rsmod.game.queue.QueueType
import gg.rsmod.game.model.snapshot.Snapshot
import gg.rsmod.game.model.step.StepQueue
import gg.rsmod.game.model.step.StepSpeed
import gg.rsmod.game.model.ui.InterfaceList
import gg.rsmod.game.timer.TimerMap
import java.time.LocalDateTime
import java.util.ArrayDeque
import java.util.Queue

private val logger = InlineLogger()
private val DEFAULT_DIRECTION = Direction.South

sealed class Mob(
    val steps: StepQueue = StepQueue(),
    var speed: StepSpeed = StepSpeed.Walk,
    val movement: Queue<Coordinates> = ArrayDeque(),
    var faceDirection: Direction = DEFAULT_DIRECTION,
    var appendTeleport: Boolean = false,
    val timers: TimerMap = TimerMap(),
    val attribs: AttributeMap = AttributeMap(),
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
    private val messageListeners: List<ServerPacketListener> = mutableListOf()
) : Mob() {

    val username: String
        get() = entity.username

    fun login() {
        eventBus.publish(LoginEvent(this, LoginEvent.Priority.High))
        eventBus.publish(LoginEvent(this, LoginEvent.Priority.Normal))
        eventBus.publish(LoginEvent(this, LoginEvent.Priority.Low))
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

    fun warn(message: () -> String) {
        logger.warn { "$username: ${message()}" }
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
