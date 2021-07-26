package org.rsmod.plugins.dev.cmd

import org.rsmod.plugins.api.model.mob.player.sendMessage
import org.rsmod.plugins.api.onCommand

onCommand("empty") {
    description = "Empty inventory"
    execute {
        player.inventory.clear()
        player.sendMessage("Your inventory items have been cleared")
    }
}
