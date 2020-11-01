package org.rsmod.plugins.api.protocol.packet

import org.rsmod.game.action.Action
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.step.StepSpeed

data class MapMove(val player: Player, val destination: Coordinates, val speed: StepSpeed) : Action
