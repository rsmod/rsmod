package org.runerealm.content.generic.locs.search

import org.runerealm.api.config.refs.content
import org.runerealm.api.config.refs.params
import org.runerealm.api.player.output.mes
import org.runerealm.api.script.onOpLoc1
import org.runerealm.game.entity.Player
import org.runerealm.game.type.loc.UnpackedLocType
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class SearchPlugin : PluginScript() {
    override fun ScriptContext.startup() {
        onOpLoc1(content.empty_crate) { player.search(it.type) }
        onOpLoc1(content.empty_sacks) { player.search(it.type) }
        onOpLoc1(content.empty_boxes) { player.search(it.type) }
    }

    private fun Player.search(type: UnpackedLocType) {
        val message = type.paramOrNull(params.game_message) ?: SearchConstants.DEFAULT
        mes(message)
    }
}
