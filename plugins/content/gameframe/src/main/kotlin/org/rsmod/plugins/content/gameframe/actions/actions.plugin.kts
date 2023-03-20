package org.rsmod.plugins.content.gameframe.actions

import org.rsmod.game.events.GameEventBus
import org.rsmod.game.events.subscribe
import org.rsmod.plugins.api.component
import org.rsmod.plugins.api.model.event.UpstreamEvent
import org.rsmod.plugins.api.movement.MoveSpeed
import org.rsmod.plugins.api.setMoveSpeed
import org.rsmod.plugins.content.gameframe.gameframe_run_button

private val events: GameEventBus by inject()

events.subscribe<UpstreamEvent.IfButton>(component.gameframe_run_button.id) {
    val speed = when (player.movement.speed) {
        MoveSpeed.Run -> MoveSpeed.Walk
        else -> MoveSpeed.Run
    }
    player.setMoveSpeed(speed)
}
