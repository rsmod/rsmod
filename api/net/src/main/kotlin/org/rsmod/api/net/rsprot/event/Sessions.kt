package org.rsmod.api.net.rsprot.event

import net.rsprot.protocol.api.Session
import org.rsmod.events.UnboundEvent
import org.rsmod.game.entity.Player

data class SessionStart(val player: Player, val session: Session<Player>) : UnboundEvent

data class SessionEnd(val player: Player, val session: Session<Player>) : UnboundEvent
