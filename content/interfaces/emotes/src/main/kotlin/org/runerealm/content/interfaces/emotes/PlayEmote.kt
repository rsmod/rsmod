package org.runerealm.content.interfaces.emotes

import org.runerealm.events.UnboundEvent
import org.runerealm.game.entity.Player
import org.runerealm.game.type.seq.SeqType

data class PlayEmote(val player: Player, val seq: SeqType) : UnboundEvent
