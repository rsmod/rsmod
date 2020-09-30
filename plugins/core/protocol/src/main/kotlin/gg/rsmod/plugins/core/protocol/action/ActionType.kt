package gg.rsmod.plugins.core.protocol.action

import gg.rsmod.game.action.Action
import gg.rsmod.game.model.map.Coordinates
import gg.rsmod.game.model.mob.Player

data class MapMove(val player: Player, val destination: Coordinates) : Action
