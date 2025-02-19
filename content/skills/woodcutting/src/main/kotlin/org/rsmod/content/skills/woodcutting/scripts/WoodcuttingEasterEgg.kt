package org.rsmod.content.skills.woodcutting.scripts

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.objs
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpLocU
import org.rsmod.content.skills.woodcutting.configs.woodcutting_locs
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class WoodcuttingEasterEgg : PluginScript() {
    override fun ScriptContext.startUp() {
        onOpLocU(content.tree, objs.herring) { treeHerring() }
        onOpLocU(content.tree, objs.raw_herring) { treeHerring() }
        onOpLocU(woodcutting_locs.redwood_tree_29668, objs.herring) { redwoodTreeHerring() }
        onOpLocU(woodcutting_locs.redwood_tree_29670, objs.raw_herring) { redwoodTreeHerring() }
    }

    private fun ProtectedAccess.treeHerring() {
        mes("This is not the mightiest tree in the forest.")
    }

    private fun ProtectedAccess.redwoodTreeHerring() {
        mes("This is not the mightiest tree in the forest; it is fairly mighty though.")
    }
}
