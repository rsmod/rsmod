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
        val wornAttributes = loadout.wornAttributes
        val npcAttributes = loadout.npcAttributes

        val modifiedAttackRoll =
            MeleeAccuracyOperations.modifyAttackRoll(
                attackRoll = baseAttackRoll,
                wornAttributes = wornAttributes,
                npcAttributes = npcAttributes,
            )
        assertEquals(finalAttackRoll, modifiedAttackRoll)
    }

    data class Loadout(
        val baseAttackRoll: Int,
        val finalAttackRoll: Int,
        val wornAttributes: EnumSet<WornAttr> = EnumSet.noneOf(WornAttr::class.java),
        val npcAttributes: EnumSet<NpcAttr> = EnumSet.noneOf(NpcAttr::class.java),
    ) {
        fun withWornAttributes(vararg attributes: WornAttr) =
            copy(wornAttributes = EnumSet.copyOf(attributes.toSet()))

        fun withNpcAttributes(vararg attributes: NpcAttr) =
            copy(npcAttributes = EnumSet.copyOf(attributes.toSet()))
    }

    private object LoadoutProvider : TestArgsProvider {
        override fun args(): List<TestArgs> {
            return testArgsOfSingleParam(
                Loadout(baseAttackRoll = 2280, finalAttackRoll = 2736)
                    .withWornAttributes(WornAttr.AmuletOfAvarice)
                    .withNpcAttributes(NpcAttr.Revenant),
                Loadout(baseAttackRoll = 1608, finalAttackRoll = 2733)
                    .withWornAttributes(WornAttr.Arclight)
                    .withNpcAttributes(NpcAttr.Demon),
                Loadout(baseAttackRoll = 1608, finalAttackRoll = 2395)
                    .withWornAttributes(WornAttr.Arclight)
                    .withNpcAttributes(NpcAttr.Demon, NpcAttr.DemonbaneResistance),
                Loadout(baseAttackRoll = 1800, finalAttackRoll = 1890)
                    .withWornAttributes(WornAttr.BurningClaws)
                    .withNpcAttributes(NpcAttr.Demon),
                Loadout(baseAttackRoll = 1800, finalAttackRoll = 1863)
                    .withWornAttributes(WornAttr.BurningClaws)
                    .withNpcAttributes(NpcAttr.Demon, NpcAttr.DemonbaneResistance),
                Loadout(baseAttackRoll = 2136, finalAttackRoll = 3738)
                    .withWornAttributes(WornAttr.BlackMask, WornAttr.RevenantMeleeWeapon)
                    .withNpcAttributes(NpcAttr.Wilderness, NpcAttr.SlayerTask),
                Loadout(baseAttackRoll = 1848, finalAttackRoll = 2310)
                    .withWornAttributes(WornAttr.KerisSunPartisan)
                    .withNpcAttributes(NpcAttr.Amascut, NpcAttr.QuarterHealth),
                Loadout(baseAttackRoll = 1710, finalAttackRoll = 2462)
                    .withWornAttributes(WornAttr.SalveAmuletE, WornAttr.DragonHunterLance)
                    .withNpcAttributes(NpcAttr.Undead, NpcAttr.Draconic),
                Loadout(baseAttackRoll = 1476, finalAttackRoll = 1918)
                    .withWornAttributes(
                        WornAttr.SalveAmuletE,
                        WornAttr.Obsidian,
                        WornAttr.TzHaarWeapon,
                    )
                    .withNpcAttributes(NpcAttr.Undead),
            )
        }
    }
}
