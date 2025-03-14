package org.rsmod.api.combat.formulas.attributes.collector

import java.util.EnumSet
import org.rsmod.api.combat.formulas.attributes.CombatNpcAttributes
import org.rsmod.api.config.refs.npcs
import org.rsmod.api.config.refs.params
import org.rsmod.game.type.npc.UnpackedNpcType

public class CombatNpcAttributeCollector {
    public fun collect(type: UnpackedNpcType, slayerTask: Boolean): EnumSet<CombatNpcAttributes> {
        val npcAttributes = EnumSet.noneOf(CombatNpcAttributes::class.java)

        if (slayerTask) {
            npcAttributes += CombatNpcAttributes.SlayerTask
        }

        // TODO(combat): "In wilderness" area check.

        val sizeAttribute =
            when (val size = type.size) {
                2 -> CombatNpcAttributes.Size2
                3 -> CombatNpcAttributes.Size3
                4 -> CombatNpcAttributes.Size4
                else -> {
                    if (size >= 5) {
                        CombatNpcAttributes.Size5OrMore
                    } else {
                        null
                    }
                }
            }

        if (sizeAttribute != null) {
            npcAttributes += sizeAttribute
        }

        if (type.param(params.revenant) != 0) {
            npcAttributes += CombatNpcAttributes.Revenant
        }

        if (type.param(params.undead) != 0) {
            npcAttributes += CombatNpcAttributes.Undead
        }

        if (type.param(params.demon) != 0) {
            npcAttributes += CombatNpcAttributes.Demon
        }

        if (type.param(params.demonbane_resistant) != 0) {
            npcAttributes += CombatNpcAttributes.DemonbaneResistance
        }

        if (type.param(params.draconic) != 0) {
            npcAttributes += CombatNpcAttributes.Draconic
        }

        if (type.param(params.kalphite) != 0) {
            npcAttributes += CombatNpcAttributes.Kalphite
        }

        if (type.param(params.golem) != 0) {
            npcAttributes += CombatNpcAttributes.Golem
        }

        if (type.param(params.leafy) != 0) {
            npcAttributes += CombatNpcAttributes.Leafy
        }

        if (type.param(params.rat) != 0) {
            npcAttributes += CombatNpcAttributes.Rat
        }

        if (type.param(params.shade) != 0) {
            npcAttributes += CombatNpcAttributes.Shade
        }

        if (type.param(params.tormented_demon) != 0 && !type.param(params.td_shield_active)) {
            npcAttributes += CombatNpcAttributes.TormentedDemonUnshielded
        }

        if (type.isType(npcs.corporeal_beast)) {
            npcAttributes += CombatNpcAttributes.CorporealBeast
        }

        return npcAttributes
    }
}
