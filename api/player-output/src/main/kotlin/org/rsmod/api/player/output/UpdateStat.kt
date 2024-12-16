package org.rsmod.api.player.output

import net.rsprot.protocol.game.outgoing.misc.player.UpdateStatV2
import org.rsmod.game.entity.Player
import org.rsmod.game.type.stat.StatType

public object UpdateStat {
    public fun update(player: Player, stat: StatType, currXp: Int, currLvl: Int, hiddenLvl: Int) {
        val message = UpdateStatV2(stat.id, currLvl, hiddenLvl, currXp)
        player.client.write(message)
    }
}
