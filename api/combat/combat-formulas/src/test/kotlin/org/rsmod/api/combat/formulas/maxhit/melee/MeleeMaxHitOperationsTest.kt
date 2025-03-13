package org.rsmod.api.combat.formulas.maxhit.melee

import java.util.EnumSet
import org.junit.jupiter.api.Assertions.assertEquals
import org.rsmod.api.combat.formulas.attributes.CombatNpcAttributes
import org.rsmod.api.combat.formulas.attributes.CombatWornAttributes
import org.rsmod.api.combat.maxhit.player.PlayerMeleeMaxHit
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.api.testing.params.testArgsOfSingleParam

private typealias WornAttr = CombatWornAttributes

private typealias NpcAttr = CombatNpcAttributes

class MeleeMaxHitOperationsTest {
    @TestWithArgs(LoadoutProvider::class)
    fun `calculate max hit with loadout`(loadout: Loadout) {
        val expectedBaseDamage = loadout.baseDamage
        val expectedModifiedMaxHit = loadout.modifiedMaxHit
        val expectedFinalMaxHit = loadout.finalMaxHit
        val strengthLevel = loadout.strengthLevel
        val styleBonus = loadout.styleBonus
        val prayerBonus = loadout.prayerBonus
        val voidBonus = loadout.voidBonus
        val weaponBonus = loadout.weaponBonus
        val strengthBonus = loadout.strengthBonus
        val wornAttributes = loadout.wornAttributes
        val npcAttributes = loadout.npcAttributes

        val effectiveStrength =
            PlayerMeleeMaxHit.calculateEffectiveStrength(
                visibleStrengthLvl = strengthLevel,
                styleBonus = styleBonus,
                prayerBonus = prayerBonus,
                voidBonus = voidBonus,
                weaponBonus = weaponBonus,
            )
        val baseDamage = PlayerMeleeMaxHit.calculateBaseDamage(effectiveStrength, strengthBonus)
        assertEquals(expectedBaseDamage, baseDamage)

        val modifiedMaxHit =
            MeleeMaxHitOperations.modifyBaseDamage(baseDamage, wornAttributes, npcAttributes)
        assertEquals(expectedModifiedMaxHit, modifiedMaxHit)

        val specMaxHit = (modifiedMaxHit * loadout.specMultiplier).toInt()
        val finalMaxHit =
            MeleeMaxHitOperations.modifyPostSpec(
                modifiedDamage = specMaxHit,
                attackRate = loadout.attackRate,
                currHp = loadout.currHp,
                maxHp = loadout.maxHp,
                wornAttributes = wornAttributes,
                npcAttributes = npcAttributes,
            )
        assertEquals(expectedFinalMaxHit, finalMaxHit)
    }

    data class Loadout(
        val baseDamage: Int,
        val modifiedMaxHit: Int,
        val finalMaxHit: Int = modifiedMaxHit,
        val strengthLevel: Int = 99,
        val styleBonus: Int = 8,
        val prayerBonus: Double = 1.0,
        val voidBonus: Double = 1.0,
        val weaponBonus: Double = 1.0,
        val strengthBonus: Int = 0,
        val wornAttributes: EnumSet<WornAttr> = EnumSet.noneOf(WornAttr::class.java),
        val npcAttributes: EnumSet<NpcAttr> = EnumSet.noneOf(NpcAttr::class.java),
        val specMultiplier: Double = 1.0,
        val attackRate: Int = 4,
        val currHp: Int = 99,
        val maxHp: Int = 99,
    ) {
        fun withSuperStrPotion(): Loadout {
            val add = 5 + (strengthLevel * 0.15).toInt()
            return copy(strengthLevel = strengthLevel + add)
        }

        fun withSmellingSalts(): Loadout {
            val add = 11 + (strengthLevel * 0.16).toInt()
            return copy(strengthLevel = strengthLevel + add)
        }

        fun withDragonBattleaxeSpec(): Loadout = copy(strengthLevel = strengthLevel + 21)

        fun withVoid() = copy(voidBonus = 1.1)

        fun withAggressiveStyle() = copy(styleBonus = 11)

        fun withControlledStyle() = copy(styleBonus = 9)

        fun withDefensiveStyle() = copy(styleBonus = 8)

        fun withStrengthBonus(bonus: Int) = copy(strengthBonus = bonus)

        fun withBurstOfStrength() = copy(prayerBonus = 1.05)

        fun withSuperhumanStrength() = copy(prayerBonus = 1.1)

        fun withPiety() = copy(prayerBonus = 1.23)

        fun withWornAttributes(vararg attributes: WornAttr) =
            copy(wornAttributes = EnumSet.copyOf(attributes.toSet()))

        fun withNpcAttributes(vararg attributes: NpcAttr) =
            copy(npcAttributes = EnumSet.copyOf(attributes.toSet()))

        fun withSpecMultiplier(specMultiplier: Double) = copy(specMultiplier = specMultiplier)

        fun withAttackRate(attackRate: Int) = copy(attackRate = attackRate)

        fun withHp(currHp: Int, maxHp: Int) = copy(currHp = currHp, maxHp = maxHp)
    }

    private object LoadoutProvider : TestArgsProvider {
        override fun args(): List<TestArgs> =
            testArgsOfSingleParam(
                /* General Loadouts */
                Loadout(baseDamage = 12, modifiedMaxHit = 12).withStrengthBonus(bonus = 8),
                Loadout(baseDamage = 22, modifiedMaxHit = 22).withStrengthBonus(bonus = 69),
                Loadout(baseDamage = 24, modifiedMaxHit = 24)
                    .withSuperhumanStrength()
                    .withStrengthBonus(bonus = 69),
                /* Revenant Loadouts */
                Loadout(baseDamage = 19, modifiedMaxHit = 22)
                    .withVoid()
                    .withStrengthBonus(bonus = 39)
                    .withWornAttributes(WornAttr.AmuletOfAvarice)
                    .withNpcAttributes(NpcAttr.Revenant),
                Loadout(baseDamage = 19, modifiedMaxHit = 22)
                    .withVoid()
                    .withStrengthBonus(bonus = 39)
                    .withWornAttributes(WornAttr.AmuletOfAvarice, WornAttr.BlackMask)
                    .withNpcAttributes(NpcAttr.Revenant),
                Loadout(baseDamage = 19, modifiedMaxHit = 25)
                    .withVoid()
                    .withStrengthBonus(bonus = 39)
                    .withWornAttributes(WornAttr.AmuletOfAvarice, WornAttr.ForinthrySurge)
                    .withNpcAttributes(NpcAttr.Revenant),
                /* Undead Loadouts */
                Loadout(baseDamage = 60, modifiedMaxHit = 70)
                    .withDefensiveStyle()
                    .withSuperStrPotion()
                    .withPiety()
                    .withStrengthBonus(bonus = 189)
                    .withWornAttributes(WornAttr.SalveAmulet)
                    .withNpcAttributes(NpcAttr.Undead),
                Loadout(baseDamage = 60, modifiedMaxHit = 72)
                    .withDefensiveStyle()
                    .withSuperStrPotion()
                    .withPiety()
                    .withStrengthBonus(bonus = 189)
                    .withWornAttributes(WornAttr.SalveAmuletE)
                    .withNpcAttributes(NpcAttr.Undead),
                /* Slayer task Loadouts */
                Loadout(baseDamage = 51, modifiedMaxHit = 59)
                    .withControlledStyle()
                    .withSuperStrPotion()
                    .withPiety()
                    .withStrengthBonus(bonus = 147)
                    .withWornAttributes(WornAttr.BlackMask)
                    .withNpcAttributes(NpcAttr.SlayerTask),
                /* Arclight Loadouts */
                Loadout(baseDamage = 24, modifiedMaxHit = 24)
                    .withStrengthBonus(bonus = 77)
                    .withWornAttributes(WornAttr.Arclight),
                Loadout(baseDamage = 24, modifiedMaxHit = 40)
                    .withStrengthBonus(bonus = 77)
                    .withWornAttributes(WornAttr.Arclight)
                    .withNpcAttributes(NpcAttr.Demon),
                Loadout(baseDamage = 24, modifiedMaxHit = 35)
                    .withStrengthBonus(bonus = 77)
                    .withWornAttributes(WornAttr.Arclight)
                    .withNpcAttributes(NpcAttr.Demon, NpcAttr.DemonbaneResistance),
                /* Burning claws Loadouts */
                Loadout(baseDamage = 35, modifiedMaxHit = 35)
                    .withAggressiveStyle()
                    .withSuperStrPotion()
                    .withBurstOfStrength()
                    .withStrengthBonus(bonus = 101)
                    .withWornAttributes(WornAttr.BurningClaws),
                Loadout(baseDamage = 40, modifiedMaxHit = 42)
                    .withAggressiveStyle()
                    .withSuperStrPotion()
                    .withPiety()
                    .withStrengthBonus(bonus = 101)
                    .withWornAttributes(WornAttr.BurningClaws)
                    .withNpcAttributes(NpcAttr.Demon),
                Loadout(baseDamage = 40, modifiedMaxHit = 41)
                    .withAggressiveStyle()
                    .withSuperStrPotion()
                    .withPiety()
                    .withStrengthBonus(bonus = 101)
                    .withWornAttributes(WornAttr.BurningClaws)
                    .withNpcAttributes(NpcAttr.Demon, NpcAttr.DemonbaneResistance),
                /* Dharok Loadouts */
                Loadout(baseDamage = 38, modifiedMaxHit = 38, finalMaxHit = 47)
                    .withAggressiveStyle()
                    .withStrengthBonus(bonus = 156)
                    .withWornAttributes(WornAttr.Dharoks)
                    .withHp(currHp = 75, maxHp = 99),
                Loadout(baseDamage = 38, modifiedMaxHit = 38, finalMaxHit = 56)
                    .withAggressiveStyle()
                    .withStrengthBonus(bonus = 156)
                    .withWornAttributes(WornAttr.Dharoks)
                    .withHp(currHp = 50, maxHp = 99),
                Loadout(baseDamage = 38, modifiedMaxHit = 38, finalMaxHit = 65)
                    .withAggressiveStyle()
                    .withStrengthBonus(bonus = 156)
                    .withWornAttributes(WornAttr.Dharoks)
                    .withHp(currHp = 25, maxHp = 99),
                Loadout(baseDamage = 38, modifiedMaxHit = 38, finalMaxHit = 71)
                    .withAggressiveStyle()
                    .withStrengthBonus(bonus = 156)
                    .withWornAttributes(WornAttr.Dharoks)
                    .withHp(currHp = 10, maxHp = 99),
                Loadout(baseDamage = 38, modifiedMaxHit = 38, finalMaxHit = 74)
                    .withAggressiveStyle()
                    .withStrengthBonus(bonus = 156)
                    .withWornAttributes(WornAttr.Dharoks)
                    .withHp(currHp = 1, maxHp = 99),
                Loadout(baseDamage = 56, modifiedMaxHit = 56, finalMaxHit = 110)
                    .withAggressiveStyle()
                    .withSmellingSalts()
                    .withPiety()
                    .withStrengthBonus(bonus = 156)
                    .withWornAttributes(WornAttr.Dharoks)
                    .withHp(currHp = 1, maxHp = 99),
                Loadout(baseDamage = 38, modifiedMaxHit = 38, finalMaxHit = 38)
                    .withAggressiveStyle()
                    .withStrengthBonus(bonus = 156)
                    .withWornAttributes(WornAttr.Dharoks)
                    .withHp(currHp = 1, maxHp = 16),
                Loadout(baseDamage = 38, modifiedMaxHit = 38, finalMaxHit = 39)
                    .withAggressiveStyle()
                    .withStrengthBonus(bonus = 156)
                    .withWornAttributes(WornAttr.Dharoks)
                    .withHp(currHp = 1, maxHp = 17),
                /* Corporeal Beast Loadout */
                Loadout(baseDamage = 38, modifiedMaxHit = 38, finalMaxHit = 19)
                    .withAggressiveStyle()
                    .withStrengthBonus(bonus = 157)
                    .withNpcAttributes(NpcAttr.CorporealBeast),
                Loadout(baseDamage = 54, modifiedMaxHit = 54, finalMaxHit = 27)
                    .withAggressiveStyle()
                    .withSuperStrPotion()
                    .withPiety()
                    .withStrengthBonus(bonus = 157)
                    .withNpcAttributes(NpcAttr.CorporealBeast),
                Loadout(baseDamage = 50, modifiedMaxHit = 50, finalMaxHit = 50)
                    .withControlledStyle()
                    .withSuperStrPotion()
                    .withPiety()
                    .withStrengthBonus(bonus = 144)
                    .withWornAttributes(WornAttr.CorpBaneWeapon)
                    .withNpcAttributes(NpcAttr.CorporealBeast),
                /* Highest max hit Loadouts */
                Loadout(baseDamage = 42, modifiedMaxHit = 65, finalMaxHit = 195)
                    .withAggressiveStyle()
                    .withDragonBattleaxeSpec()
                    .withPiety()
                    .withStrengthBonus(bonus = 106)
                    .withWornAttributes(
                        WornAttr.BlackMask,
                        WornAttr.KerisWeapon,
                        WornAttr.KerisProc,
                    )
                    .withNpcAttributes(NpcAttr.Kalphite, NpcAttr.SlayerTask),
                Loadout(baseDamage = 58, modifiedMaxHit = 69, finalMaxHit = 75)
                    .withAggressiveStyle()
                    .withSuperStrPotion()
                    .withPiety()
                    .withStrengthBonus(bonus = 175)
                    .withWornAttributes(WornAttr.SalveAmuletE)
                    .withNpcAttributes(NpcAttr.Undead)
                    .withSpecMultiplier(specMultiplier = 1.1),
                // Wiki page listing the highest known max hits shows the following scenario as 139,
                // however the wiki dps calculator shows 171. It is more likely the wiki page is
                // outdated (or incorrect).
                Loadout(baseDamage = 54, modifiedMaxHit = 54, finalMaxHit = 171)
                    .withAggressiveStyle()
                    .withDragonBattleaxeSpec()
                    .withPiety()
                    .withStrengthBonus(bonus = 156)
                    .withWornAttributes(WornAttr.Dharoks, WornAttr.Crush)
                    .withNpcAttributes(NpcAttr.TormentedDemonUnshielded)
                    .withAttackRate(attackRate = 7)
                    .withHp(currHp = 1, maxHp = 99),
                Loadout(baseDamage = 51, modifiedMaxHit = 61, finalMaxHit = 120)
                    .withAggressiveStyle()
                    .withDragonBattleaxeSpec()
                    .withPiety()
                    .withStrengthBonus(bonus = 144)
                    .withWornAttributes(WornAttr.Dharoks, WornAttr.SalveAmuletE)
                    .withNpcAttributes(NpcAttr.Undead)
                    .withHp(currHp = 1, maxHp = 99),
                Loadout(baseDamage = 42, modifiedMaxHit = 49, finalMaxHit = 56)
                    .withAggressiveStyle()
                    .withSuperStrPotion()
                    .withPiety()
                    .withStrengthBonus(bonus = 109)
                    .withWornAttributes(WornAttr.BlackMask)
                    .withNpcAttributes(NpcAttr.SlayerTask)
                    .withSpecMultiplier(specMultiplier = 1.15),
            )
    }
}
