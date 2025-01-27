package org.rsmod.content.interfaces.prayer.tab

import org.rsmod.api.player.stat.baseDefenceLvl
import org.rsmod.api.player.stat.basePrayerLvl
import org.rsmod.game.entity.Player
import org.rsmod.game.type.synth.SynthType
import org.rsmod.game.type.varbit.VarBitType

data class Prayer(
    val id: Int,
    val name: String,
    val level: Int,
    val sound: SynthType,
    val enabled: VarBitType,
    val overhead: Int?,
    val unlocked: VarBitType?,
    val unlockState: Int,
    val defenceReq: Int?,
    val lockedMessage: String?,
) {
    val plainLockedMessage: String? =
        lockedMessage?.replace("<col=000080>", "")?.replace("</col>", "")?.replace("<br>", " ")

    fun hasAllRequirements(player: Player): Boolean {
        if (unlocked != null && player.vars[unlocked] < unlockState) {
            return false
        } else if (defenceReq != null && player.baseDefenceLvl < defenceReq) {
            return false
        }
        return hasBaseRequirement(player)
    }

    fun hasBaseRequirement(player: Player): Boolean = player.basePrayerLvl >= level
}
