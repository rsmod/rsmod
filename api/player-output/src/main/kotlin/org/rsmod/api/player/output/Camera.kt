package org.rsmod.api.player.output

import net.rsprot.protocol.game.outgoing.camera.CamLookAt
import net.rsprot.protocol.game.outgoing.camera.CamMoveTo
import net.rsprot.protocol.game.outgoing.camera.CamReset
import org.rsmod.game.entity.Player
import org.rsmod.map.CoordGrid

public object Camera {
    public fun camReset(player: Player) {
        player.client.write(CamReset)
    }

    public fun camLookAt(player: Player, dest: CoordGrid, height: Int, rate: Int, rate2: Int) {
        // TODO: Add require assertion to make sure coords is valid within build area.
        val dx = dest.x - player.buildArea.x
        val dz = dest.z - player.buildArea.z
        player.client.write(CamLookAt(dx, dz, height, rate, rate2))
    }

    public fun camMoveTo(player: Player, dest: CoordGrid, height: Int, rate: Int, rate2: Int) {
        // TODO: Add require assertion to make sure coords is valid within build area.
        val dx = dest.x - player.buildArea.x
        val dz = dest.z - player.buildArea.z
        player.client.write(CamMoveTo(dx, dz, height, rate, rate2))
    }
}
