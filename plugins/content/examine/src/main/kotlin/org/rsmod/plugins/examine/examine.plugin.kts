package org.rsmod.plugins.examine

import org.rsmod.plugins.api.model.mob.player.sendMessage
import org.rsmod.plugins.api.protocol.packet.ItemAction
import org.rsmod.plugins.api.protocol.packet.NpcClick
import org.rsmod.plugins.api.protocol.packet.ObjectClick

// TODO: repository to fetch examine messages

onAction<ItemAction.ExamineAction> {
    player.sendMessage("Nothing interesting happens.")
}

onAction<ObjectClick.ExamineAction> {
    player.sendMessage("Nothing interesting happens.")
}

onAction<NpcClick.ExamineAction> {
    player.sendMessage("Nothing interesting happens.")
}
