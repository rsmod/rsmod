package org.runerealm.content.interfaces.prayer.tab.scripts

import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.player.ui.ifSetEvents
import org.runerealm.api.player.vars.boolVarBit
import org.runerealm.api.script.onIfOpen
import org.runerealm.api.script.onIfOverlayButton
import org.runerealm.content.interfaces.prayer.tab.configs.prayer_components
import org.runerealm.content.interfaces.prayer.tab.configs.prayer_varbits
import org.runerealm.game.entity.Player
import org.runerealm.game.type.interf.IfEvent
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class PrayerTabFilterScript : PluginScript() {
    override fun ScriptContext.startup() {
        onIfOpen(interfaces.prayerbook) { player.onTabOpen() }
        onIfOverlayButton(prayer_components.filters) { player.toggleFilter(comsub) }
    }

    private fun Player.onTabOpen() {
        ifSetEvents(prayer_components.filters, 0..4, IfEvent.Op1)
    }

    private fun Player.toggleFilter(comsub: Int) {
        when (comsub) {
            0 -> showLowerTiers = !showLowerTiers
            1 -> showTiered = !showTiered
            2 -> showRapidHealing = !showRapidHealing
            3 -> showWithoutLevel = !showWithoutLevel
            4 -> showWithoutReq = !showWithoutReq
            else -> throw IllegalStateException("Unhandled comsub: $comsub")
        }
    }
}

private var Player.showLowerTiers by boolVarBit(prayer_varbits.filter_show_lower_tiers)
private var Player.showTiered by boolVarBit(prayer_varbits.filter_show_tiered_prayers)
private var Player.showRapidHealing by boolVarBit(prayer_varbits.filter_show_rapid_healing)
private var Player.showWithoutLevel by boolVarBit(prayer_varbits.filter_show_prayers_fail_lvl)
private var Player.showWithoutReq by boolVarBit(prayer_varbits.filter_show_prayers_fail_req)
