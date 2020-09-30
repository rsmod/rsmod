package gg.rsmod.plugins.core.protocol.packet

import gg.rsmod.game.action.Action
import gg.rsmod.game.model.map.Coordinates
import gg.rsmod.game.model.mob.Player
import gg.rsmod.game.model.step.StepSpeed

data class MapMove(val player: Player, val destination: Coordinates, val speed: StepSpeed) : Action
