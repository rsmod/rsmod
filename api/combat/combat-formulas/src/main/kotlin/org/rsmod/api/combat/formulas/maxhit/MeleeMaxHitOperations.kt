package org.rsmod.api.combat.formulas.maxhit

import java.util.EnumSet
import kotlin.math.max
import org.rsmod.api.combat.commons.styles.MeleeAttackStyle
import org.rsmod.api.combat.formulas.EquipmentChecks
import org.rsmod.api.combat.formulas.attributes.CombatNpcAttributes
import org.rsmod.api.combat.formulas.attributes.CombatWornAttributes
import org.rsmod.api.combat.formulas.scale
import org.rsmod.api.combat.maxhit.player.PlayerMeleeMaxHit
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.config.refs.varps
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.type.obj.Wearpos
import org.rsmod.game.vars.VarPlayerIntMap

private typealias WornAttr = CombatWornAttributes

private typealias NpcAttr = CombatNpcAttributes

public object MeleeMaxHitOperations {
    public fun modifyBaseDamage(
        baseDamage: Int,
        wornAttributes: EnumSet<CombatWornAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        var modified = baseDamage

        if (WornAttr.AmuletOfAvarice in wornAttributes && NpcAttr.Revenant in npcAttributes) {
            val multiplier = if (WornAttr.ForinthrySurge in wornAttributes) 27 else 24
            modified = scale(modified, multiplier, divisor = 20)
        } else if (WornAttr.SalveAmuletE in wornAttributes && NpcAttr.Undead in npcAttributes) {
            modified = scale(modified, multiplier = 6, divisor = 5)
        } else if (WornAttr.SalveAmulet in wornAttributes && NpcAttr.Undead in npcAttributes) {
            modified = scale(modified, multiplier = 7, divisor = 6)
        } else if (WornAttr.BlackMask in wornAttributes && NpcAttr.SlayerTask in npcAttributes) {
            modified = scale(modified, multiplier = 7, divisor = 6)
        }

        if (WornAttr.Arclight in wornAttributes && NpcAttr.Demon in npcAttributes) {
            modified =
                if (NpcAttr.DemonbaneResistance in npcAttributes) {
                    scale(modified, multiplier = 149, divisor = 100)
                } else {
                    scale(modified, multiplier = 170, divisor = 100)
                }
        }

        if (WornAttr.BurningClaws in wornAttributes && NpcAttr.Demon in npcAttributes) {
            modified =
                if (NpcAttr.DemonbaneResistance in npcAttributes) {
                    scale(modified, multiplier = 207, divisor = 200)
                } else {
                    scale(modified, multiplier = 210, divisor = 200)
                }
        }

        if (WornAttr.Obsidian in wornAttributes && WornAttr.TzHaarWeapon in wornAttributes) {
            modified += baseDamage / 10
        }

        if (WornAttr.DragonHunterWeapon in wornAttributes && NpcAttr.Draconic in npcAttributes) {
            modified = scale(modified, multiplier = 6, divisor = 5)
        }

        if (WornAttr.KerisWeapon in wornAttributes && NpcAttr.Kalphite in npcAttributes) {
            modified = scale(modified, multiplier = 133, divisor = 100)
        }

        if (WornAttr.BarroniteMaceWeapon in wornAttributes && NpcAttr.Golem in npcAttributes) {
            modified = scale(modified, multiplier = 23, divisor = 20)
        }

        if (WornAttr.RevenantMeleeWeapon in wornAttributes && NpcAttr.Wilderness in npcAttributes) {
            modified = scale(modified, multiplier = 3, divisor = 2)
        }

        if (WornAttr.Silverlight in wornAttributes && NpcAttr.Demon in npcAttributes) {
            modified =
                if (NpcAttr.DemonbaneResistance in npcAttributes) {
                    scale(modified, multiplier = 71, divisor = 50)
                } else {
                    scale(modified, multiplier = 80, divisor = 50)
                }
        }

        if (WornAttr.LeafBladed in wornAttributes && NpcAttr.Leafy in npcAttributes) {
            modified = scale(modified, multiplier = 47, divisor = 40)
        }

        if (WornAttr.ColossalBlade in wornAttributes) {
            val additive =
                when {
                    NpcAttr.Size2 in npcAttributes -> 4
                    NpcAttr.Size3 in npcAttributes -> 6
                    NpcAttr.Size4 in npcAttributes -> 8
                    NpcAttr.Size5OrMore in npcAttributes -> 10
                    else -> 2
                }
            modified += additive
        }

        if (WornAttr.RatBoneWeapon in wornAttributes && NpcAttr.Rat in npcAttributes) {
            modified += 10
        }

        if (WornAttr.Crush in wornAttributes) {
            var inquisitorPieces = 0
            if (WornAttr.InquisitorHelm in wornAttributes) {
                inquisitorPieces++
            }
            if (WornAttr.InquisitorTop in wornAttributes) {
                inquisitorPieces++
            }
            if (WornAttr.InquisitorBottom in wornAttributes) {
                inquisitorPieces++
            }

            val multiplierAdditive =
                if (inquisitorPieces == 0) {
                    0
                } else if (WornAttr.InquisitorWeapon in wornAttributes) {
                    inquisitorPieces * 5
                } else if (inquisitorPieces == 3) {
                    5
                } else {
                    inquisitorPieces
                }

            if (multiplierAdditive > 0) {
                modified = scale(modified, multiplier = 200 + multiplierAdditive, divisor = 200)
            }
        }

        return modified
    }

    public fun modifyPostSpec(
        modifiedDamage: Int,
        attackRate: Int,
        currHp: Int,
        maxHp: Int,
        wornAttributes: EnumSet<CombatWornAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        var modified = modifiedDamage

        if (WornAttr.GadderhammerProc in wornAttributes && NpcAttr.Shade in npcAttributes) {
            modified *= 2
        } else if (WornAttr.Gadderhammer in wornAttributes && NpcAttr.Shade in npcAttributes) {
            modified = scale(modified, multiplier = 5, divisor = 4)
        }

        if (WornAttr.KerisProc in wornAttributes && NpcAttr.Kalphite in npcAttributes) {
            modified *= 3
        }

        if (WornAttr.Crush in wornAttributes && NpcAttr.TormentedDemonUnshielded in npcAttributes) {
            val bonusDamage = max(0, (attackRate * attackRate) - 16)
            modified += bonusDamage
        }

        if (WornAttr.Dharoks in wornAttributes) {
            val multiplier = (maxHp - currHp) * maxHp
            modified += scale(modified, multiplier, divisor = 10_000)
        }

        if (WornAttr.BerserkerNeck in wornAttributes && WornAttr.TzHaarWeapon in wornAttributes) {
            modified = scale(modified, multiplier = 6, divisor = 5)
        }

        // TODO(combat): Vampyre mods

        // Corporeal beast damage reduction is apparently applied _before_ ruby bolt proc, but
        // _after_ other bolt procs. This is why this is handled here instead of in a specialized
        // `onModifyNpcHit(corporeal_beast)` script. Though this is for Melee max hit, this should
        // be consistent throughout all combat styles to avoid any conflict or hardcoded checks
        // if it were to be handled via a script.
        if (NpcAttr.CorporealBeast in npcAttributes && WornAttr.CorpBaneWeapon !in wornAttributes) {
            modified /= 2
        }

        return modified
    }

    public fun calculateEffectiveStrength(player: Player, attackStyle: MeleeAttackStyle?): Int {
        val strengthLevel = player.statMap.getCurrentLevel(stats.strength).toInt()
        val soulreaperAxe = EquipmentChecks.isSoulreaperAxe(player.worn[Wearpos.RightHand.slot])
        val soulStackBonus = if (soulreaperAxe) player.vars.soulStackBonus() else 1.0
        return calculateEffectiveStrength(
            visLevel = strengthLevel,
            weaponBonus = soulStackBonus,
            vars = player.vars,
            worn = player.worn,
            attackStyle = attackStyle,
        )
    }

    private fun calculateEffectiveStrength(
        visLevel: Int,
        weaponBonus: Double,
        vars: VarPlayerIntMap,
        worn: Inventory,
        attackStyle: MeleeAttackStyle?,
    ): Int {
        val styleBonus = attackStyle.styleBonus()
        val prayerBonus = vars.prayerBonus()
        val voidBonus = worn.voidBonus()
        return PlayerMeleeMaxHit.calculateEffectiveStrength(
            visibleStrengthLvl = visLevel,
            styleBonus = styleBonus,
            prayerBonus = prayerBonus,
            voidBonus = voidBonus,
            weaponBonus = weaponBonus,
        )
    }

    private fun MeleeAttackStyle?.styleBonus(): Int =
        when (this) {
            MeleeAttackStyle.Controlled -> 9
            MeleeAttackStyle.Aggressive -> 11
            else -> 8
        }

    private fun VarPlayerIntMap.prayerBonus(): Double =
        when {
            this[varbits.burst_of_strength] == 1 -> 1.05
            this[varbits.superhuman_strength] == 1 -> 1.1
            this[varbits.ultimate_strength] == 1 -> 1.15
            this[varbits.chivalry] == 1 -> 1.18
            this[varbits.piety] == 1 -> 1.23
            else -> 1.0
        }

    private fun Inventory.voidBonus(): Double {
        val helm = this[Wearpos.Hat.slot]
        if (!EquipmentChecks.isVoidMeleeHelm(helm)) {
            return 1.0
        }

        val top = this[Wearpos.Torso.slot]
        if (!EquipmentChecks.isVoidTop(top)) {
            return 1.0
        }

        val legs = this[Wearpos.Legs.slot]
        if (!EquipmentChecks.isVoidRobe(legs)) {
            return 1.0
        }

        val gloves = this[Wearpos.Hands.slot]
        if (!EquipmentChecks.isVoidGloves(gloves)) {
            return 1.0
        }

        return 1.1
    }

    private fun VarPlayerIntMap.soulStackBonus(): Double {
        val souls = this[varps.soulreaper_souls]
        return 1.0 + (souls * 0.06)
    }
}
