package org.rsmod.api.combat.formulas.maxhit.melee

import java.util.EnumSet
import kotlin.math.max
import org.rsmod.api.combat.commons.styles.MeleeAttackStyle
import org.rsmod.api.combat.formulas.EquipmentChecks
import org.rsmod.api.combat.formulas.attributes.CombatMeleeAttributes
import org.rsmod.api.combat.formulas.attributes.CombatNpcAttributes
import org.rsmod.api.combat.formulas.attributes.DamageReductionAttributes
import org.rsmod.api.combat.formulas.scale
import org.rsmod.api.combat.maxhit.player.PlayerMeleeMaxHit
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.stat.stat
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.type.obj.Wearpos
import org.rsmod.game.vars.VarPlayerIntMap

private typealias MeleeAttr = CombatMeleeAttributes

private typealias NpcAttr = CombatNpcAttributes

public object MeleeMaxHitOperations {
    public fun modifyBaseDamage(
        baseDamage: Int,
        meleeAttributes: EnumSet<CombatMeleeAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        var modified = baseDamage

        if (MeleeAttr.AmuletOfAvarice in meleeAttributes && NpcAttr.Revenant in npcAttributes) {
            val multiplier = if (MeleeAttr.ForinthrySurge in meleeAttributes) 27 else 24
            modified = scale(modified, multiplier, divisor = 20)
        } else if (MeleeAttr.SalveAmuletE in meleeAttributes && NpcAttr.Undead in npcAttributes) {
            modified = scale(modified, multiplier = 6, divisor = 5)
        } else if (MeleeAttr.SalveAmulet in meleeAttributes && NpcAttr.Undead in npcAttributes) {
            modified = scale(modified, multiplier = 7, divisor = 6)
        } else if (MeleeAttr.BlackMask in meleeAttributes && NpcAttr.SlayerTask in npcAttributes) {
            modified = scale(modified, multiplier = 7, divisor = 6)
        }

        if (MeleeAttr.Arclight in meleeAttributes && NpcAttr.Demon in npcAttributes) {
            modified =
                if (NpcAttr.DemonbaneResistance in npcAttributes) {
                    scale(modified, multiplier = 149, divisor = 100)
                } else {
                    scale(modified, multiplier = 170, divisor = 100)
                }
        }

        if (MeleeAttr.BurningClaws in meleeAttributes && NpcAttr.Demon in npcAttributes) {
            modified =
                if (NpcAttr.DemonbaneResistance in npcAttributes) {
                    scale(modified, multiplier = 207, divisor = 200)
                } else {
                    scale(modified, multiplier = 210, divisor = 200)
                }
        }

        if (MeleeAttr.Obsidian in meleeAttributes && MeleeAttr.TzHaarWeapon in meleeAttributes) {
            modified += baseDamage / 10
        }

        if (MeleeAttr.DragonHunterLance in meleeAttributes && NpcAttr.Draconic in npcAttributes) {
            modified = scale(modified, multiplier = 6, divisor = 5)
        }

        if (MeleeAttr.DragonHunterWand in meleeAttributes && NpcAttr.Draconic in npcAttributes) {
            modified = scale(modified, multiplier = 6, divisor = 5)
        }

        if (MeleeAttr.KerisWeapon in meleeAttributes && NpcAttr.Kalphite in npcAttributes) {
            modified = scale(modified, multiplier = 133, divisor = 100)
        }

        if (MeleeAttr.KerisBreachPartisan in meleeAttributes && NpcAttr.Kalphite in npcAttributes) {
            modified = scale(modified, multiplier = 133, divisor = 100)
        }

        if (MeleeAttr.KerisSunPartisan in meleeAttributes && NpcAttr.Kalphite in npcAttributes) {
            modified = scale(modified, multiplier = 133, divisor = 100)
        }

        if (MeleeAttr.BarroniteMaceWeapon in meleeAttributes && NpcAttr.Golem in npcAttributes) {
            modified = scale(modified, multiplier = 23, divisor = 20)
        }

        if (MeleeAttr.RevenantWeapon in meleeAttributes && NpcAttr.Wilderness in npcAttributes) {
            modified = scale(modified, multiplier = 3, divisor = 2)
        }

        if (MeleeAttr.Silverlight in meleeAttributes && NpcAttr.Demon in npcAttributes) {
            modified =
                if (NpcAttr.DemonbaneResistance in npcAttributes) {
                    scale(modified, multiplier = 71, divisor = 50)
                } else {
                    scale(modified, multiplier = 80, divisor = 50)
                }
        }

        if (MeleeAttr.LeafBladed in meleeAttributes && NpcAttr.Leafy in npcAttributes) {
            modified = scale(modified, multiplier = 47, divisor = 40)
        }

        if (MeleeAttr.ColossalBlade in meleeAttributes) {
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

        if (MeleeAttr.RatBoneWeapon in meleeAttributes && NpcAttr.Rat in npcAttributes) {
            modified += 10
        }

        if (MeleeAttr.Crush in meleeAttributes) {
            var inquisitorPieces = 0
            if (MeleeAttr.InquisitorHelm in meleeAttributes) {
                inquisitorPieces++
            }
            if (MeleeAttr.InquisitorTop in meleeAttributes) {
                inquisitorPieces++
            }
            if (MeleeAttr.InquisitorBottom in meleeAttributes) {
                inquisitorPieces++
            }

            val multiplierAdditive =
                if (inquisitorPieces == 0) {
                    0
                } else if (MeleeAttr.InquisitorWeapon in meleeAttributes) {
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
        meleeAttributes: EnumSet<CombatMeleeAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        var modified = modifiedDamage

        if (MeleeAttr.GadderhammerProc in meleeAttributes && NpcAttr.Shade in npcAttributes) {
            modified *= 2
        } else if (MeleeAttr.Gadderhammer in meleeAttributes && NpcAttr.Shade in npcAttributes) {
            modified = scale(modified, multiplier = 5, divisor = 4)
        }

        if (MeleeAttr.KerisProc in meleeAttributes && NpcAttr.Kalphite in npcAttributes) {
            modified *= 3
        }

        val unshieldedTormentedDemon =
            MeleeAttr.Crush in meleeAttributes && NpcAttr.TormentedDemonUnshielded in npcAttributes
        if (unshieldedTormentedDemon) {
            val bonusDamage = max(0, (attackRate * attackRate) - 16)
            modified += bonusDamage
        }

        if (MeleeAttr.Dharoks in meleeAttributes) {
            val multiplier = (maxHp - currHp) * maxHp
            modified += scale(modified, multiplier, divisor = 10_000)
        }

        val berserkerNecklacePassive =
            MeleeAttr.BerserkerNeck in meleeAttributes && MeleeAttr.TzHaarWeapon in meleeAttributes
        if (berserkerNecklacePassive) {
            modified = scale(modified, multiplier = 6, divisor = 5)
        }

        // TODO(combat): Vampyre mods

        // Corporeal beast damage reduction is apparently applied _before_ ruby bolt proc, but
        // _after_ other bolt procs. This is why this is handled here instead of in a specialized
        // `onModifyNpcHit(corporeal_beast)` script. Though this is for Melee max hit, this should
        // be consistent throughout all combat styles to avoid any conflict or hardcoded checks
        // if it were to be handled via a script.
        val corpBeastReduction =
            NpcAttr.CorporealBeast in npcAttributes && MeleeAttr.CorpBaneWeapon !in meleeAttributes
        if (corpBeastReduction) {
            modified /= 2
        }

        return modified
    }

    public fun applyDamageReductions(
        startDamage: Int,
        activeDefenceBonus: Int,
        reductionAttributes: EnumSet<DamageReductionAttributes>,
    ): Int {
        var modified = startDamage

        if (DamageReductionAttributes.ElysianProc in reductionAttributes) {
            modified = scale(modified, multiplier = 3, divisor = 4)
        }

        if (DamageReductionAttributes.DinhsBlock in reductionAttributes) {
            modified = scale(modified, multiplier = 4, divisor = 5)
        }

        if (DamageReductionAttributes.Justiciar in reductionAttributes) {
            val factor = activeDefenceBonus / 3000.0
            // Damage reduction effect will always reduce at least `1`.
            val reduction = max(1, (modified * factor).toInt())
            modified = max(0, modified - reduction)
        }

        return modified
    }

    public fun calculateEffectiveStrength(player: Player, attackStyle: MeleeAttackStyle?): Int {
        val strengthLevel = player.stat(stats.strength)
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
