package org.rsmod.api.player.vars

import org.rsmod.api.player.output.VarpSync.writeVarp
import org.rsmod.game.entity.Player
import org.rsmod.game.type.varp.VarpType

public fun Player.syncVarp(varp: VarpType, value: Int) {
    vars[varp] = if (value == 0) null else value
    if (varp.transmit) {
        writeVarp(this, varp, value)
    }
}

public fun Player.syncVarpStr(varp: VarpType, value: String?) {
    varsString[varp] = value
}
