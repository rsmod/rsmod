package org.runerealm.api.combat.scripts

import jakarta.inject.Inject
import org.runerealm.api.combat.commons.player.combatRetaliate
import org.runerealm.api.combat.commons.styles.AttackStyle
import org.runerealm.api.combat.weapon.WeaponSpeeds
import org.runerealm.api.combat.weapon.styles.AttackStyles
import org.runerealm.api.config.refs.queues
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onPlayerQueueWithArgs
import org.runerealm.game.entity.npc.NpcUid
import org.runerealm.game.entity.player.PlayerUid
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

internal class PlayerRetaliateScript
@Inject
constructor(private val attackStyles: AttackStyles, private val weaponSpeeds: WeaponSpeeds) :
    PluginScript() {
    override fun ScriptContext.startup() {
        onPlayerQueueWithArgs(queues.com_retaliate_npc) { autoRetaliateNpc(it.args) }
        onPlayerQueueWithArgs(queues.com_retaliate_player) { autoRetaliatePlayer(it.args) }
    }

    private fun ProtectedAccess.autoRetaliateNpc(uid: NpcUid) {
        val flinchDelay = flinchDelay()
        combatRetaliate(uid, flinchDelay)
    }

    private fun ProtectedAccess.autoRetaliatePlayer(uid: PlayerUid) {
        val flinchDelay = flinchDelay()
        combatRetaliate(uid, flinchDelay)
    }

    private fun ProtectedAccess.flinchDelay(): Int {
        val attackStyle = attackStyles.get(player)
        val attackRate = weaponSpeeds.base(player)
        return (attackRate / 2) - (if (attackStyle == AttackStyle.RapidRanged) 1 else 0)
    }
}
