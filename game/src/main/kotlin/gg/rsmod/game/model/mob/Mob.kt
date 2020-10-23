package gg.rsmod.game.model.mob

import com.github.michaelbull.logging.InlineLogger
import com.google.common.base.MoreObjects
import gg.rsmod.game.action.ActionBus
import gg.rsmod.game.attribute.AttributeMap
import gg.rsmod.game.event.EventBus
import gg.rsmod.game.event.impl.LoginEvent
import gg.rsmod.game.message.ServerPacket
import gg.rsmod.game.message.ServerPacketListener
import gg.rsmod.game.model.client.NpcEntity
import gg.rsmod.game.model.client.PlayerEntity
import gg.rsmod.game.model.domain.Appearance
import gg.rsmod.game.model.domain.Direction
import gg.rsmod.game.model.domain.PlayerId
import gg.rsmod.game.model.item.ItemContainer
import gg.rsmod.game.model.map.Coordinates
import gg.rsmod.game.model.map.Viewport
import gg.rsmod.game.queue.GameQueue
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
private const val EQUIPMENT_CAPACITY = 12

sealed class Mob(
    val steps: StepQueue = StepQueue(),
    var speed: StepSpeed = StepSpeed.Walk,
    val movement: Queue<Direction> = ArrayDeque(),
    var faceDirection: Direction = DEFAULT_DIRECTION,
    var appendTeleport: Boolean = false,
    val timers: TimerMap = TimerMap(),
    val attribs: AttributeMap = AttributeMap(),
    internal val queueStack: GameQueueStack = GameQueueStack()
) {

    fun weakQueue(block: suspend GameQueue.() -> Unit) = queueStack.queue(QueueType.Weak, block)

    fun normalQueue(block: suspend GameQueue.() -> Unit) = queueStack.queue(QueueType.Normal, block)

    fun strongQueue(block: suspend GameQueue.() -> Unit) = queueStack.queue(QueueType.Strong, block)

    fun clearQueues() = queueStack.clear()
}

class Player(
    val id: PlayerId,
    val loginName: String,
    val entity: PlayerEntity,
    val eventBus: EventBus,
    val actionBus: ActionBus,
    var snapshot: Snapshot = Snapshot.INITIAL,
    var viewport: Viewport = Viewport.ZERO,
    var appearance: Appearance = Appearance.ZERO,
    val equipment: ItemContainer = ItemContainer(EQUIPMENT_CAPACITY),
    val ui: InterfaceList = InterfaceList(),
    private val messageListeners: List<ServerPacketListener> = mutableListOf()
) : Mob() {

    val username: String
        get() = entity.username

    var index: Int
        get() = entity.index
        set(value) { entity.index = value }

    var coords: Coordinates
        get() = entity.coords
        set(value) { entity.coords = value }

    fun login() {
        eventBus.publish(LoginEvent(this, LoginEvent.Priority.High))
        eventBus.publish(LoginEvent(this, LoginEvent.Priority.Normal))
        eventBus.publish(LoginEvent(this, LoginEvent.Priority.Low))
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
    val entity: NpcEntity
) : Mob() {

    var index: Int
        get() = entity.index
        set(value) { entity.index = value }

    var coords: Coordinates
        get() = entity.coords
        set(value) { entity.coords = value }
}
