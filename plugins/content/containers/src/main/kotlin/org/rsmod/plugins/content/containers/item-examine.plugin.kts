package org.rsmod.plugins.content.containers

import org.rsmod.plugins.api.model.mob.player.sendMessage
import org.rsmod.plugins.api.protocol.packet.ItemAction

onAction<ItemAction.Inventory6> {
    // TODO: send item examine message
    player.sendMessage("Nothing interesting happens.")
}
