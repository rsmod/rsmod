package org.rsmod.api.combat.formulas.maxhit.ranged

import java.util.EnumSet
import org.junit.jupiter.api.Assertions.assertEquals
import org.rsmod.api.combat.formulas.attributes.CombatNpcAttributes
import org.rsmod.api.combat.formulas.attributes.CombatRangedAttributes
import org.rsmod.api.combat.maxhit.player.PlayerRangedMaxHit
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.api.testing.params.testArgsOfSingleParam

private typealias RangeAttr = CombatRangedAttributes

private typealias NpcAttr = CombatNpcAttributes

class RangedMaxHitOperationsTest {
    @TestWithArgs(LoadoutProvider::class)
    fun `calculate max hit with loadout`(loadout: Loadout) {
        val expectedBaseDamage = loadout.baseDamage
        val expectedModifiedMaxHit = loadout.modifiedMaxHit
        val expectedFinalMaxHit = loadout.finalMaxHit
        val rangedLevel = loadout.rangedLevel
        val styleBonus = loadout.styleBonus
        val prayerBonus = loadout.prayerBonus
        val voidBonus = loadout.voidBonus
        val rangedStrBonus = loadout.rangedStrBonus
        val rangedAttributes = loadout.rangedAttributes
        val npcAttributes = loadout.npcAttributes
        val boltDamageAdd = loadout.boltDamageAdditive
        val targetMagic = loadout.targetMagic

        val effectiveRanged =
            PlayerRangedMaxHit.calculateEffectiveRanged(
                visibleRangedLvl = rangedLevel,
                styleBonus = styleBonus,
                prayerBonus = prayerBonus,
                voidBonus = voidBonus,
            )
        val baseDamage = PlayerRangedMaxHit.calculateBaseDamage(effectiveRanged, rangedStrBonus)
        assertEquals(expectedBaseDamage, baseDamage)

        val modifiedMaxHit =
            RangedMaxHitOperations.modifyBaseDamage(
                baseDamage,
                targetMagic,
                rangedAttributes,
                npcAttributes,
            )
        assertEquals(expectedModifiedMaxHit, modifiedMaxHit)

        val specMaxHit = (modifiedMaxHit * loadout.specMultiplier).toInt()
        val finalMaxHit =
            RangedMaxHitOperations.modifyPostSpec(
                modifiedDamage = specMaxHit,
                boltSpecDamage = boltDamageAdd,
                attackRate = loadout.attackRate,
                rangeAttributes = rangedAttributes,
                npcAttributes = npcAttributes,
            )
        assertEquals(expectedFinalMaxHit, finalMaxHit)
    }

    data class Loadout(
        val baseDamage: Int,
        val modifiedMaxHit: Int,
        val finalMaxHit: Int = modifiedMaxHit,
        val rangedLevel: Int = 99,
        val styleBonus: Int = 8,
        val prayerBonus: Double = 1.0,
        val voidBonus: Double = 1.0,
        val rangedStrBonus: Int = 0,
        val rangedAttributes: EnumSet<RangeAttr> = EnumSet.noneOf(RangeAttr::class.java),
        val npcAttributes: EnumSet<NpcAttr> = EnumSet.noneOf(NpcAttr::class.java),
        val specMultiplier: Double = 1.0,
        val attackRate: Int = 4,
        val boltDamageAdditive: Int = 0,
        val targetMagic: Int = 0,
    ) {
        fun withRangingPotion(): Loadout {
            val add = 4 + (rangedLevel * 0.10).toInt()
            return copy(rangedLevel = rangedLevel + add)
        }

        fun withSmellingSalts(): Loadout {
            val add = 11 + (rangedLevel * 0.16).toInt()
            return copy(rangedLevel = rangedLevel + add)
        }

        fun withEliteVoid() = copy(voidBonus = 1.125)

        fun withAccurateStyle() = copy(styleBonus = 11)

        fun withOtherStyle() = copy(styleBonus = 8)

        fun withRangedStrBonus(bonus: Int) = copy(rangedStrBonus = bonus)

        fun withSharpEye() = copy(prayerBonus = 1.05)

        fun withHawkEye() = copy(prayerBonus = 1.1)

        fun withRigour() = copy(prayerBonus = 1.23)

        fun withRangedAttributes(vararg attributes: RangeAttr) =
            copy(rangedAttributes = EnumSet.copyOf(attributes.toSet()))

        fun withNpcAttributes(vararg attributes: NpcAttr) =
            copy(npcAttributes = EnumSet.copyOf(attributes.toSet()))

        fun withSpecMultiplier(specMultiplier: Double) = copy(specMultiplier = specMultiplier)

        fun withAttackRate(attackRate: Int) = copy(attackRate = attackRate)

        fun withDragonBreathBolt() = copy(boltDamageAdditive = ((rangedLevel * 0.2).toInt()))

        fun withTargetMagic(magic: Int) = copy(targetMagic = magic)
    }

    private object LoadoutProvider : TestArgsProvider {
        override fun args(): List<TestArgs> =
            testArgsOfSingleParam(
                /* General Loadouts */
                Loadout(baseDamage = 12, modifiedMaxHit = 12).withRangedStrBonus(bonus = 7),
                Loadout(baseDamage = 19, modifiedMaxHit = 19).withRangedStrBonus(bonus = 49),
                Loadout(baseDamage = 20, modifiedMaxHit = 20)
                    .withSharpEye()
                    .withRangedStrBonus(bonus = 49),
                Loadout(baseDamage = 24, modifiedMaxHit = 24)
                    .withAccurateStyle()
                    .withHawkEye()
                    .withRangingPotion()
                    .withRangedStrBonus(bonus = 49),
                Loadout(baseDamage = 29, modifiedMaxHit = 29)
                    .withAccurateStyle()
                    .withEliteVoid()
                    .withRigour()
                    .withRangingPotion()
                    .withRangedStrBonus(bonus = 49),
                Loadout(baseDamage = 36, modifiedMaxHit = 36, finalMaxHit = 54)
                    .withOtherStyle()
                    .withEliteVoid()
                    .withRigour()
                    .withSmellingSalts()
                    .withSpecMultiplier(specMultiplier = 1.5)
                    .withRangedStrBonus(bonus = 65),
                /* Revenant Loadouts */
                Loadout(baseDamage = 26, modifiedMaxHit = 31)
                    .withOtherStyle()
                    .withRigour()
                    .withRangingPotion()
                    .withRangedStrBonus(bonus = 49)
                    .withRangedAttributes(RangeAttr.AmuletOfAvarice)
                    .withNpcAttributes(NpcAttr.Revenant),
                Loadout(baseDamage = 26, modifiedMaxHit = 31)
                    .withOtherStyle()
                    .withRigour()
                    .withRangingPotion()
                    .withRangedStrBonus(bonus = 49)
                    .withRangedAttributes(RangeAttr.AmuletOfAvarice, RangeAttr.BlackMaskI)
                    .withNpcAttributes(NpcAttr.Revenant),
                Loadout(baseDamage = 26, modifiedMaxHit = 35)
                    .withOtherStyle()
                    .withRigour()
                    .withRangingPotion()
                    .withRangedStrBonus(bonus = 49)
                    .withRangedAttributes(RangeAttr.AmuletOfAvarice, RangeAttr.ForinthrySurge)
                    .withNpcAttributes(NpcAttr.Revenant),
                /* Undead Loadouts */
                Loadout(baseDamage = 30, modifiedMaxHit = 35)
                    .withOtherStyle()
                    .withRigour()
                    .withRangingPotion()
                    .withRangedStrBonus(bonus = 69)
                    .withRangedAttributes(RangeAttr.SalveAmuletI)
                    .withNpcAttributes(NpcAttr.Undead),
                Loadout(baseDamage = 30, modifiedMaxHit = 36)
                    .withOtherStyle()
                    .withRigour()
                    .withRangingPotion()
                    .withRangedStrBonus(bonus = 69)
                    .withRangedAttributes(RangeAttr.SalveAmuletEi)
                    .withNpcAttributes(NpcAttr.Undead),
                /* Slayer task Loadouts */
                Loadout(baseDamage = 30, modifiedMaxHit = 34)
                    .withOtherStyle()
                    .withRigour()
                    .withRangingPotion()
                    .withRangedStrBonus(bonus = 67)
                    .withRangedAttributes(RangeAttr.BlackMaskI)
                    .withNpcAttributes(NpcAttr.SlayerTask),
                /* Crystal Loadouts */
                Loadout(baseDamage = 45, modifiedMaxHit = 45)
                    .withAccurateStyle()
                    .withRigour()
                    .withSmellingSalts()
                    .withRangedStrBonus(bonus = 112)
                    .withRangedAttributes(RangeAttr.CrystalBow),
                Loadout(baseDamage = 45, modifiedMaxHit = 46)
                    .withAccurateStyle()
                    .withRigour()
                    .withSmellingSalts()
                    .withRangedStrBonus(bonus = 112)
                    .withRangedAttributes(RangeAttr.CrystalBow, RangeAttr.CrystalHelm),
                Loadout(baseDamage = 45, modifiedMaxHit = 48)
                    .withAccurateStyle()
                    .withRigour()
                    .withSmellingSalts()
                    .withRangedStrBonus(bonus = 112)
                    .withRangedAttributes(RangeAttr.CrystalBow, RangeAttr.CrystalBody),
                Loadout(baseDamage = 45, modifiedMaxHit = 47)
                    .withAccurateStyle()
                    .withRigour()
                    .withSmellingSalts()
                    .withRangedStrBonus(bonus = 112)
                    .withRangedAttributes(RangeAttr.CrystalBow, RangeAttr.CrystalLegs),
                Loadout(baseDamage = 45, modifiedMaxHit = 49)
                    .withAccurateStyle()
                    .withRigour()
                    .withSmellingSalts()
                    .withRangedStrBonus(bonus = 112)
                    .withRangedAttributes(
                        RangeAttr.CrystalBow,
                        RangeAttr.CrystalHelm,
                        RangeAttr.CrystalBody,
                    ),
                Loadout(baseDamage = 45, modifiedMaxHit = 48)
                    .withAccurateStyle()
                    .withRigour()
                    .withSmellingSalts()
                    .withRangedStrBonus(bonus = 112)
                    .withRangedAttributes(
                        RangeAttr.CrystalBow,
                        RangeAttr.CrystalHelm,
                        RangeAttr.CrystalLegs,
                    ),
                Loadout(baseDamage = 45, modifiedMaxHit = 51)
                    .withAccurateStyle()
                    .withRigour()
                    .withSmellingSalts()
                    .withRangedStrBonus(bonus = 112)
                    .withRangedAttributes(
                        RangeAttr.CrystalBow,
                        RangeAttr.CrystalHelm,
                        RangeAttr.CrystalBody,
                        RangeAttr.CrystalLegs,
                    ),
                /* Dragon hunter Loadouts */
                Loadout(baseDamage = 50, modifiedMaxHit = 62)
                    .withOtherStyle()
                    .withRigour()
                    .withSmellingSalts()
                    .withRangedStrBonus(bonus = 136)
                    .withRangedAttributes(RangeAttr.DragonHunterCrossbow)
                    .withNpcAttributes(NpcAttr.Draconic),
                /* Scorching bow Loadouts */
                Loadout(baseDamage = 44, modifiedMaxHit = 57)
                    .withOtherStyle()
                    .withRigour()
                    .withSmellingSalts()
                    .withRangedStrBonus(bonus = 110)
                    .withRangedAttributes(RangeAttr.ScorchingBow)
                    .withNpcAttributes(NpcAttr.Demon),
                /* Twisted bow Loadouts */
                Loadout(baseDamage = 42, modifiedMaxHit = 22)
                    .withOtherStyle()
                    .withRigour()
                    .withSmellingSalts()
                    .withRangedStrBonus(bonus = 101)
                    .withTargetMagic(magic = 0)
                    .withRangedAttributes(RangeAttr.TwistedBow),
                Loadout(baseDamage = 42, modifiedMaxHit = 49)
                    .withOtherStyle()
                    .withRigour()
                    .withSmellingSalts()
                    .withRangedStrBonus(bonus = 101)
                    .withTargetMagic(magic = 80)
                    .withRangedAttributes(RangeAttr.TwistedBow),
                Loadout(baseDamage = 42, modifiedMaxHit = 80)
                    .withOtherStyle()
                    .withRigour()
                    .withSmellingSalts()
                    .withRangedStrBonus(bonus = 101)
                    .withTargetMagic(magic = 200)
                    .withRangedAttributes(RangeAttr.TwistedBow),
                Loadout(baseDamage = 42, modifiedMaxHit = 86)
                    .withOtherStyle()
                    .withRigour()
                    .withSmellingSalts()
                    .withRangedStrBonus(bonus = 101)
                    .withTargetMagic(magic = 230)
                    .withRangedAttributes(RangeAttr.TwistedBow),
                Loadout(baseDamage = 42, modifiedMaxHit = 88)
                    .withOtherStyle()
                    .withRigour()
                    .withSmellingSalts()
                    .withRangedStrBonus(bonus = 101)
                    .withTargetMagic(magic = 240)
                    .withRangedAttributes(RangeAttr.TwistedBow),
                Loadout(baseDamage = 42, modifiedMaxHit = 104)
                    .withOtherStyle()
                    .withRigour()
                    .withSmellingSalts()
                    .withRangedStrBonus(bonus = 101)
                    .withTargetMagic(magic = 375)
                    .withRangedAttributes(RangeAttr.TwistedBow)
                    .withNpcAttributes(NpcAttr.Xerician),
                /* Tormented demon Loadouts */
                Loadout(baseDamage = 49, modifiedMaxHit = 49, finalMaxHit = 58)
                    .withOtherStyle()
                    .withEliteVoid()
                    .withRigour()
                    .withRangingPotion()
                    .withRangedStrBonus(bonus = 128)
                    .withAttackRate(attackRate = 5)
                    .withRangedAttributes(RangeAttr.Heavy)
                    .withNpcAttributes(NpcAttr.TormentedDemonUnshielded),
                Loadout(baseDamage = 49, modifiedMaxHit = 49, finalMaxHit = 80)
                    .withOtherStyle()
                    .withEliteVoid()
                    .withRigour()
                    .withRangingPotion()
                    .withRangedStrBonus(bonus = 128)
                    .withAttackRate(attackRate = 5)
                    .withDragonBreathBolt()
                    .withRangedAttributes(RangeAttr.Heavy, RangeAttr.DragonHunterCrossbow)
                    .withNpcAttributes(NpcAttr.TormentedDemonUnshielded),
            )
    }
}
