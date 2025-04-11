package org.rsmod.api.net.rsprot.player

import net.rsprot.protocol.api.Session
import org.rsmod.events.UnboundEvent
import org.rsmod.game.entity.Player

data class SessionStart(val player: Player, val session: Session<Player>) : UnboundEvent
