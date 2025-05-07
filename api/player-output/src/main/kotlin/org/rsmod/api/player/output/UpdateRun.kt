package org.rsmod.api.player.output

import net.rsprot.protocol.game.outgoing.misc.player.UpdateRunEnergy
import net.rsprot.protocol.game.outgoing.misc.player.UpdateRunWeight
import org.rsmod.game.entity.Player

public object UpdateRun {
    /** @see [UpdateRunEnergy] */
    public fun energy(player: Player, energy: Int) {
        player.client.write(UpdateRunEnergy(energy))
    }

    /** @see [UpdateRunWeight] */
    public fun weight(player: Player, kg: Int) {
        player.client.write(UpdateRunWeight(kg))
    }
}
