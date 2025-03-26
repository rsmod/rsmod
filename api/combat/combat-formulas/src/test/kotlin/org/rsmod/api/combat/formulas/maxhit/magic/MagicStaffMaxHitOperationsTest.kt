package org.rsmod.api.combat.formulas.maxhit.magic

import java.util.EnumSet
import kotlin.math.round
import org.junit.jupiter.api.Assertions.assertEquals
import org.rsmod.api.combat.formulas.attributes.CombatStaffAttributes
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.api.testing.params.testArgsOfSingleParam

private typealias StaffAttr = CombatStaffAttributes

class MagicStaffMaxHitOperationsTest {
    @TestWithArgs(LoadoutProvider::class)
    fun `calculate max hit with loadout`(loadout: Loadout) {
        val expectedModifiedMaxHit = loadout.modifiedMaxHit
        val expectedFinalMaxHit = loadout.finalMaxHit
        val bonusDmg = round(loadout.bonusDmgPercent * 10).toInt()
        val prayerDmg = round(loadout.prayerDmgPercent * 10).toInt()
        val staffAttributes = loadout.staffAttributes
        val npcAttributes = loadout.npcAttributes

        val modifiedMaxHit =
            MagicMaxHitOperations.modifyStaffBaseDamage(
                baseDamage = loadout.spellMaxHit,
                sourceBaseMagicDmgBonus = bonusDmg,
                sourceMagicPrayerBonus = prayerDmg,
                staffAttributes = staffAttributes,
                npcAttributes = npcAttributes,
            )
        assertEquals(expectedModifiedMaxHit, modifiedMaxHit)

        val finalMaxHit = (modifiedMaxHit * loadout.specMultiplier).toInt()
        assertEquals(expectedFinalMaxHit, finalMaxHit)
    }

    data class Loadout(
        val spellMaxHit: Int = 0,
        val modifiedMaxHit: Int = spellMaxHit,
        val finalMaxHit: Int = modifiedMaxHit,
        val bonusDmgPercent: Double = 0.0,
        val prayerDmgPercent: Double = 0.0,
        val staffAttributes: EnumSet<StaffAttr> = EnumSet.noneOf(StaffAttr::class.java),
        val npcAttributes: EnumSet<NpcAttr> = EnumSet.noneOf(NpcAttr::class.java),
        val specMultiplier: Double = 1.0,
    ) {
        fun withMagicDmgBonus(bonusPercent: Double) = copy(bonusDmgPercent = bonusPercent)

        fun withMysticLore() = copy(prayerDmgPercent = 1.0)

        fun withAugury() = copy(prayerDmgPercent = 4.0)

        fun withStaffAttributes(vararg attributes: StaffAttr) =
            copy(staffAttributes = EnumSet.copyOf(attributes.toSet()))

        fun withNpcAttributes(vararg attributes: NpcAttr) =
            copy(npcAttributes = EnumSet.copyOf(attributes.toSet()))

        fun withSpecMultiplier(specMultiplier: Double) = copy(specMultiplier = specMultiplier)
    }

    private object LoadoutProvider : TestArgsProvider {
        override fun args(): List<TestArgs> =
            testArgsOfSingleParam(
                Loadout(spellMaxHit = (112 / 3 - 6), modifiedMaxHit = 58)
                    .withAugury()
                    .withMagicDmgBonus(bonusPercent = 24.0)
                    .withStaffAttributes(StaffAttr.ForinthrySurge, StaffAttr.RevenantWeapon)
                    .withNpcAttributes(NpcAttr.Revenant, NpcAttr.Wilderness),
                Loadout(spellMaxHit = (112 / 3 - 6), modifiedMaxHit = 72, finalMaxHit = 108)
                    .withAugury()
                    .withMagicDmgBonus(bonusPercent = 19.0)
                    .withSpecMultiplier(specMultiplier = 1.5)
                    .withStaffAttributes(
                        StaffAttr.ForinthrySurge,
                        StaffAttr.RevenantWeapon,
                        StaffAttr.AmuletOfAvarice,
                    )
                    .withNpcAttributes(NpcAttr.Revenant, NpcAttr.Wilderness),
                Loadout(spellMaxHit = (112 / 3 + 1), modifiedMaxHit = 74)
                    .withAugury()
                    // Tumeken's shadow implicitly "sets" the bonus to 57.
                    .withMagicDmgBonus(bonusPercent = 19.0)
                    .withStaffAttributes(
                        StaffAttr.ForinthrySurge,
                        StaffAttr.TumekensShadow,
                        StaffAttr.AmuletOfAvarice,
                    )
                    .withNpcAttributes(NpcAttr.Revenant, NpcAttr.Wilderness),
                Loadout(spellMaxHit = (125 / 3 + 1), modifiedMaxHit = 75)
                    .withAugury()
                    // Tumeken's shadow implicitly "sets" the bonus to 76.
                    .withMagicDmgBonus(bonusPercent = 19.0)
                    .withStaffAttributes(StaffAttr.TumekensShadow)
                    .withNpcAttributes(NpcAttr.Amascut),
                Loadout(spellMaxHit = (125 / 3 + 1), modifiedMaxHit = 74)
                    .withMysticLore()
                    // Tumeken's shadow implicitly "sets" the bonus to 76.
                    .withMagicDmgBonus(bonusPercent = 19.0)
                    .withStaffAttributes(StaffAttr.TumekensShadow)
                    .withNpcAttributes(NpcAttr.Amascut),
            )
    }
}
