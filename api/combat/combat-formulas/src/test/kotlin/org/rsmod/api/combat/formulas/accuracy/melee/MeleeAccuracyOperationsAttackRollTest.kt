package org.rsmod.api.combat.formulas.accuracy.melee

import java.util.EnumSet
import org.junit.jupiter.api.Assertions.assertEquals
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.api.testing.params.testArgsOfSingleParam

class MeleeAccuracyOperationsAttackRollTest {
    @TestWithArgs(LoadoutProvider::class)
    fun `calculate hit chance based on loadout`(loadout: Loadout) {
        val baseAttackRoll = loadout.baseAttackRoll
        val finalAttackRoll = loadout.finalAttackRoll
        val meleeAttributes = loadout.meleeAttributes
        val npcAttributes = loadout.npcAttributes

        val modifiedAttackRoll =
            MeleeAccuracyOperations.modifyAttackRoll(
                attackRoll = baseAttackRoll,
                meleeAttributes = meleeAttributes,
                npcAttributes = npcAttributes,
            )
        assertEquals(finalAttackRoll, modifiedAttackRoll)
    }

    data class Loadout(
        val baseAttackRoll: Int,
        val finalAttackRoll: Int,
        val meleeAttributes: EnumSet<MeleeAttr> = EnumSet.noneOf(MeleeAttr::class.java),
        val npcAttributes: EnumSet<NpcAttr> = EnumSet.noneOf(NpcAttr::class.java),
    ) {
        fun withMeleeAttributes(vararg attributes: MeleeAttr) =
            copy(meleeAttributes = EnumSet.copyOf(attributes.toSet()))

        fun withNpcAttributes(vararg attributes: NpcAttr) =
            copy(npcAttributes = EnumSet.copyOf(attributes.toSet()))
    }

    private object LoadoutProvider : TestArgsProvider {
        override fun args(): List<TestArgs> {
            return testArgsOfSingleParam(
                Loadout(baseAttackRoll = 2280, finalAttackRoll = 2736)
                    .withMeleeAttributes(MeleeAttr.AmuletOfAvarice)
                    .withNpcAttributes(NpcAttr.Revenant),
                Loadout(baseAttackRoll = 1608, finalAttackRoll = 2733)
                    .withMeleeAttributes(MeleeAttr.Arclight)
                    .withNpcAttributes(NpcAttr.Demon),
                Loadout(baseAttackRoll = 1608, finalAttackRoll = 2395)
                    .withMeleeAttributes(MeleeAttr.Arclight)
                    .withNpcAttributes(NpcAttr.Demon, NpcAttr.DemonbaneResistance),
                Loadout(baseAttackRoll = 1800, finalAttackRoll = 1890)
                    .withMeleeAttributes(MeleeAttr.BurningClaws)
                    .withNpcAttributes(NpcAttr.Demon),
                Loadout(baseAttackRoll = 1800, finalAttackRoll = 1863)
                    .withMeleeAttributes(MeleeAttr.BurningClaws)
                    .withNpcAttributes(NpcAttr.Demon, NpcAttr.DemonbaneResistance),
                Loadout(baseAttackRoll = 2136, finalAttackRoll = 3738)
                    .withMeleeAttributes(MeleeAttr.BlackMask, MeleeAttr.RevenantWeapon)
                    .withNpcAttributes(NpcAttr.Wilderness, NpcAttr.SlayerTask),
                Loadout(baseAttackRoll = 1848, finalAttackRoll = 2310)
                    .withMeleeAttributes(MeleeAttr.KerisSunPartisan)
                    .withNpcAttributes(NpcAttr.Amascut, NpcAttr.QuarterHealth),
                Loadout(baseAttackRoll = 1710, finalAttackRoll = 2462)
                    .withMeleeAttributes(MeleeAttr.SalveAmuletE, MeleeAttr.DragonHunterLance)
                    .withNpcAttributes(NpcAttr.Undead, NpcAttr.Draconic),
                Loadout(baseAttackRoll = 1476, finalAttackRoll = 1918)
                    .withMeleeAttributes(
                        MeleeAttr.SalveAmuletE,
                        MeleeAttr.Obsidian,
                        MeleeAttr.TzHaarWeapon,
                    )
                    .withNpcAttributes(NpcAttr.Undead),
            )
        }
    }
}
