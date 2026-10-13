package org.runerealm.api.player.output

import net.rsprot.protocol.game.outgoing.misc.player.SetMapFlag
import org.runerealm.game.entity.Player
import org.runerealm.map.CoordGrid

public fun Player.clearMapFlag() {
    client.write(SetMapFlag(255, 255))
}

public object MapFlag {
    public fun setMapFlag(player: Player, coords: CoordGrid) {
        setMapFlag(player, coords.x, coords.z)
    }

    public fun setMapFlag(player: Player, x: Int, z: Int) {
        val dx = x - player.buildArea.x
        val dz = z - player.buildArea.z
        player.client.write(SetMapFlag(dx, dz))
    }
}
