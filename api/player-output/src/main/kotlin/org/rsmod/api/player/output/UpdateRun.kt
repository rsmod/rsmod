package org.rsmod.api.player.output

import net.rsprot.protocol.game.outgoing.misc.player.UpdateRunEnergy
import net.rsprot.protocol.game.outgoing.misc.player.UpdateRunWeight
import org.rsmod.game.entity.Player

public object UpdateRun {
    public fun updateRunEnergy(player: Player, energy: Int) {
        player.client.write(UpdateRunEnergy(energy))
    }

    public fun updateRunWeight(player: Player, kg: Int) {
        player.client.write(UpdateRunWeight(kg))
    }
}
