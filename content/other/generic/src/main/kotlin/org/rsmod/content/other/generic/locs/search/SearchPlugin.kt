package org.rsmod.content.other.generic.locs.search

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.player.output.mes
import org.rsmod.api.script.onOpLoc1
import org.rsmod.game.entity.Player
import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class SearchPlugin : PluginScript() {
    override fun ScriptContext.startUp() {
        onOpLoc1(content.empty_crate) { player.search(it.type) }
        onOpLoc1(content.empty_sacks) { player.search(it.type) }
    }

    private fun Player.search(type: UnpackedLocType) {
        val message = type.paramOrNull(params.game_message) ?: SearchConstants.DEFAULT
        mes(message)
    }
}
