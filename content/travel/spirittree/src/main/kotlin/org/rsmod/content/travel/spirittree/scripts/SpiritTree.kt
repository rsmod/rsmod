package org.rsmod.content.travel.spirittree.scripts

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.config.refs.npcs
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpLoc1
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.type.loc.LocType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class SpiritTree : PluginScript() {
    override fun ScriptContext.startup() {
        onOpLoc1(content.spirit_tree) { attemptDialogue(it.loc) }
    }

    private suspend fun ProtectedAccess.attemptDialogue(tree: BoundLocInfo) {
        startDialogue { spiritTreeDialogue(tree) }
        openSpiritTreeInterface()

    }

    private suspend fun Dialogue.spiritTreeDialogue(tree: BoundLocInfo) {
        chatNpcSpecific("Spirit Tree", npcs.spirit_tree_chathead, happy, "Hello gnome friend. Where would you like to go?")
        return
    }

    private fun ProtectedAccess.openSpiritTreeInterface() {
        ifOpenMain(interfaces.spirit_tree)
    }

}
