package org.rsmod.plugins.api

import com.github.michaelbull.logging.InlineLogger
import org.rsmod.game.events.GameEvent
import org.rsmod.game.events.GameEventBus
import org.rsmod.game.events.GameKeyedEvent
import org.rsmod.game.model.mob.Player
import org.rsmod.game.types.NamedComponent
import org.rsmod.game.types.NamedInterface
import org.rsmod.plugins.api.model.ui.StandardGameframe
import org.rsmod.plugins.api.net.downstream.IfOpenSub
import org.rsmod.plugins.api.net.downstream.IfOpenTop

private val logger = InlineLogger()

public fun Player.openGameframe(gameframe: StandardGameframe) {
    val topLevel = gameframe.topLevel
    val overlays = gameframe.overlays
    openTopLevel(topLevel)
    overlays.forEach {
        val overlay = NamedInterface(it.interfaceId)
        val target = NamedComponent(topLevel.id, it.child)
        openOverlay(overlay, target)
    }
}

public fun Player.openTopLevel(topLevel: NamedInterface) {
    downstream += IfOpenTop(topLevel.id)
}

public fun Player.openOverlay(overlay: NamedInterface, target: NamedComponent) {
    downstream += IfOpenSub(overlay.id, target.id, 1)
}

public fun <T : GameEvent> Player.publish(event: T, bus: GameEventBus) {
    logger.trace { "Player $this publishing event $event." }
    val actions = bus.getOrNull(event::class.java)
    if (actions == null) {
        logger.debug { "No actions defined for event ${event.javaClass}. (player=$this)" }
        return
    }
    actions.forEach { it.invoke(event) }
}

public fun <T : GameKeyedEvent> Player.publish(id: Long, event: T, bus: GameEventBus) {
    logger.trace { "Player $this publishing keyed event $event." }
    val map = bus.getOrNull(event::class.java)
    if (map == null) {
        logger.debug { "No actions defined for event ${event.javaClass}. (player=$this)" }
        return
    }
    val action = map[id]
    if (action == null) {
        logger.debug { "No action mapped to id $id for event $event. (player=$this)" }
        return
    }
    action.invoke(event)
}

public fun <T : GameKeyedEvent> Player.publish(id: Int, event: T, bus: GameEventBus): Unit =
    publish(id.toLong(), event, bus)
