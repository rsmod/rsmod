package org.rsmod.api.combat.formulas.accuracy.ranged

import java.util.EnumSet
import org.junit.jupiter.api.Assertions.assertEquals
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.api.testing.params.testArgsOfSingleParam

class RangedAccuracyOperationsAttackRollTest {
    @TestWithArgs(LoadoutProvider::class)
    fun `calculate hit chance based on loadout`(loadout: Loadout) {
        val baseAttackRoll = loadout.baseAttackRoll
        val finalAttackRoll = loadout.finalAttackRoll
        val targetMagic = loadout.targetMagic
        val targetDistance = loadout.targetDistance
        val rangeAttributes = loadout.rangedAttributes
        val npcAttributes = loadout.npcAttributes

        val modifiedAttackRoll =
            RangedAccuracyOperations.modifyAttackRoll(
                attackRoll = baseAttackRoll,
                targetMagic = targetMagic,
                targetDistance = targetDistance,
                rangeAttributes = rangeAttributes,
                npcAttributes = npcAttributes,
            )
        assertEquals(finalAttackRoll, modifiedAttackRoll)
    }

    data class Loadout(
        val baseAttackRoll: Int,
        val finalAttackRoll: Int,
        val targetMagic: Int = 0,
        val targetDistance: Int = 1,
        val rangedAttributes: EnumSet<RangeAttr> = EnumSet.noneOf(RangeAttr::class.java),
        val npcAttributes: EnumSet<NpcAttr> = EnumSet.noneOf(NpcAttr::class.java),
    ) {
        fun withTargetMagic(targetMagic: Int) = copy(targetMagic = targetMagic)

        fun withTargetDistance(targetDistance: Int) = copy(targetDistance = targetDistance)

        fun withRangedAttributes(vararg attributes: RangeAttr) =
            copy(rangedAttributes = EnumSet.copyOf(attributes.toSet()))

        fun withNpcAttributes(vararg attributes: NpcAttr) =
            copy(npcAttributes = EnumSet.copyOf(attributes.toSet()))
    }

    private object LoadoutProvider : TestArgsProvider {
        override fun args(): List<TestArgs> {
            return testArgsOfSingleParam(
                /* General Loadouts */
                Loadout(baseAttackRoll = 24824, finalAttackRoll = 29788)
                    .withRangedAttributes(RangeAttr.AmuletOfAvarice)
                    .withNpcAttributes(NpcAttr.Revenant),
                Loadout(baseAttackRoll = 39643, finalAttackRoll = 47571)
                    .withRangedAttributes(RangeAttr.SalveAmuletEi)
                    .withNpcAttributes(NpcAttr.Undead),
                Loadout(baseAttackRoll = 39643, finalAttackRoll = 46250)
                    .withRangedAttributes(RangeAttr.SalveAmuletI)
                    .withNpcAttributes(NpcAttr.Undead),
                Loadout(baseAttackRoll = 36915, finalAttackRoll = 42452)
                    .withRangedAttributes(RangeAttr.BlackMaskI)
                    .withNpcAttributes(NpcAttr.SlayerTask),
                /* Twisted bow Loadouts */
                Loadout(baseAttackRoll = 14338, finalAttackRoll = 5735)
                    .withTargetMagic(targetMagic = 1)
                    .withRangedAttributes(RangeAttr.TwistedBow),
                Loadout(baseAttackRoll = 14338, finalAttackRoll = 19643)
                    .withTargetMagic(targetMagic = 230)
                    .withRangedAttributes(RangeAttr.TwistedBow),
                Loadout(baseAttackRoll = 14338, finalAttackRoll = 5735)
                    .withTargetMagic(targetMagic = 1)
                    .withRangedAttributes(RangeAttr.TwistedBow)
                    .withNpcAttributes(NpcAttr.Xerician),
                Loadout(baseAttackRoll = 14338, finalAttackRoll = 20216)
                    .withTargetMagic(targetMagic = 250)
                    .withRangedAttributes(RangeAttr.TwistedBow)
                    .withNpcAttributes(NpcAttr.Xerician),
                /* Revenant Loadouts */
                Loadout(baseAttackRoll = 15943, finalAttackRoll = 23914)
                    .withRangedAttributes(RangeAttr.RevenantWeapon)
                    .withNpcAttributes(NpcAttr.Wilderness),
                /* Draconic Loadouts */
                Loadout(baseAttackRoll = 17013, finalAttackRoll = 26539)
                    .withRangedAttributes(RangeAttr.DragonHunterCrossbow, RangeAttr.SalveAmuletEi)
                    .withNpcAttributes(NpcAttr.Draconic, NpcAttr.Undead),
                /* Chinchompa Loadouts */
                Loadout(baseAttackRoll = 15840, finalAttackRoll = 15840)
                    .withTargetDistance(targetDistance = 1)
                    .withRangedAttributes(RangeAttr.ShortFuse),
                Loadout(baseAttackRoll = 15840, finalAttackRoll = 15840)
                    .withTargetDistance(targetDistance = 2)
                    .withRangedAttributes(RangeAttr.ShortFuse),
                Loadout(baseAttackRoll = 15840, finalAttackRoll = 15840)
                    .withTargetDistance(targetDistance = 3)
                    .withRangedAttributes(RangeAttr.ShortFuse),
                Loadout(baseAttackRoll = 15840, finalAttackRoll = 11880)
                    .withTargetDistance(targetDistance = 4)
                    .withRangedAttributes(RangeAttr.ShortFuse),
                Loadout(baseAttackRoll = 15840, finalAttackRoll = 11880)
                    .withTargetDistance(targetDistance = 5)
                    .withRangedAttributes(RangeAttr.ShortFuse),
                Loadout(baseAttackRoll = 15840, finalAttackRoll = 11880)
                    .withTargetDistance(targetDistance = 6)
                    .withRangedAttributes(RangeAttr.ShortFuse),
                Loadout(baseAttackRoll = 15840, finalAttackRoll = 7920)
                    .withTargetDistance(targetDistance = 7)
                    .withRangedAttributes(RangeAttr.ShortFuse),
                Loadout(baseAttackRoll = 15408, finalAttackRoll = 11556)
                    .withTargetDistance(targetDistance = 1)
                    .withRangedAttributes(RangeAttr.MediumFuse),
                Loadout(baseAttackRoll = 15408, finalAttackRoll = 11556)
                    .withTargetDistance(targetDistance = 2)
                    .withRangedAttributes(RangeAttr.MediumFuse),
                Loadout(baseAttackRoll = 15408, finalAttackRoll = 11556)
                    .withTargetDistance(targetDistance = 3)
                    .withRangedAttributes(RangeAttr.MediumFuse),
                Loadout(baseAttackRoll = 15408, finalAttackRoll = 15408)
                    .withTargetDistance(targetDistance = 4)
                    .withRangedAttributes(RangeAttr.MediumFuse),
                Loadout(baseAttackRoll = 15408, finalAttackRoll = 15408)
                    .withTargetDistance(targetDistance = 5)
                    .withRangedAttributes(RangeAttr.MediumFuse),
                Loadout(baseAttackRoll = 15408, finalAttackRoll = 15408)
                    .withTargetDistance(targetDistance = 6)
                    .withRangedAttributes(RangeAttr.MediumFuse),
                Loadout(baseAttackRoll = 15408, finalAttackRoll = 11556)
                    .withTargetDistance(targetDistance = 7)
                    .withRangedAttributes(RangeAttr.MediumFuse),
                Loadout(baseAttackRoll = 15408, finalAttackRoll = 7704)
                    .withTargetDistance(targetDistance = 1)
                    .withRangedAttributes(RangeAttr.LongFuse),
                Loadout(baseAttackRoll = 15408, finalAttackRoll = 7704)
                    .withTargetDistance(targetDistance = 2)
                    .withRangedAttributes(RangeAttr.LongFuse),
                Loadout(baseAttackRoll = 15408, finalAttackRoll = 7704)
                    .withTargetDistance(targetDistance = 3)
                    .withRangedAttributes(RangeAttr.LongFuse),
                Loadout(baseAttackRoll = 15408, finalAttackRoll = 11556)
                    .withTargetDistance(targetDistance = 4)
                    .withRangedAttributes(RangeAttr.LongFuse),
                Loadout(baseAttackRoll = 15408, finalAttackRoll = 11556)
                    .withTargetDistance(targetDistance = 5)
                    .withRangedAttributes(RangeAttr.LongFuse),
                Loadout(baseAttackRoll = 15408, finalAttackRoll = 11556)
                    .withTargetDistance(targetDistance = 6)
                    .withRangedAttributes(RangeAttr.LongFuse),
                Loadout(baseAttackRoll = 15408, finalAttackRoll = 15408)
                    .withTargetDistance(targetDistance = 7)
                    .withRangedAttributes(RangeAttr.LongFuse),
                /* Scorching bow Loadouts */
                Loadout(baseAttackRoll = 20116, finalAttackRoll = 26150)
                    .withRangedAttributes(RangeAttr.ScorchingBow)
                    .withNpcAttributes(NpcAttr.Demon),
                Loadout(baseAttackRoll = 20116, finalAttackRoll = 24340)
                    .withRangedAttributes(RangeAttr.ScorchingBow)
                    .withNpcAttributes(NpcAttr.Demon, NpcAttr.DemonbaneResistance),
            )
        }
    }
}
