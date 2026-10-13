package org.runerealm.api.net.rsprot.player

import net.rsprot.protocol.api.Session
import org.runerealm.events.UnboundEvent
import org.runerealm.game.entity.Player

data class SessionStart(val player: Player, val session: Session<Player>) : UnboundEvent
