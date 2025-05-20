package org.rsmod.api.combat.scripts

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.player.combatRetaliate
import org.rsmod.api.combat.commons.styles.AttackStyle
import org.rsmod.api.combat.weapon.WeaponSpeeds
import org.rsmod.api.combat.weapon.styles.AttackStyles
import org.rsmod.api.config.refs.queues
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onPlayerQueueWithArgs
import org.rsmod.game.entity.npc.NpcUid
import org.rsmod.game.entity.player.PlayerUid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

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
