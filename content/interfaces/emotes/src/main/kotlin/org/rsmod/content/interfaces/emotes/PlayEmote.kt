package org.rsmod.content.interfaces.emotes

import org.rsmod.events.UnboundEvent
import org.rsmod.game.entity.Player
import org.rsmod.game.type.seq.SeqType

data class PlayEmote(val player: Player, val seq: SeqType) : UnboundEvent
